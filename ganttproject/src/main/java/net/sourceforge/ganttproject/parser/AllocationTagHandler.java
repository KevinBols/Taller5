/*
GanttProject is an opensource project management tool.
Copyright (C) 2002-2010 Alexandre Thomas, Dmitry Barashev

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 3
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package net.sourceforge.ganttproject.parser;

import biz.ganttproject.core.io.XmlAllocation;
import biz.ganttproject.core.io.XmlProject;
import net.sourceforge.ganttproject.resource.HumanResource;
import net.sourceforge.ganttproject.resource.HumanResourceManager;
import net.sourceforge.ganttproject.roles.Role;
import net.sourceforge.ganttproject.roles.RoleManager;
import net.sourceforge.ganttproject.roles.RolePersistentID;
import net.sourceforge.ganttproject.roles.RoleSet;
import net.sourceforge.ganttproject.task.ResourceAssignment;
import net.sourceforge.ganttproject.task.Task;
import net.sourceforge.ganttproject.task.TaskManager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author bard
 */
public class AllocationTagHandler extends AbstractTagHandler {
  private final HumanResourceManager myResourceManager;

  private final TaskManager myTaskManager;

  private final RoleManager myRoleManager;

  private final HashMap<ResourceAssignment, String> myLateAssigmnent2roleBinding = new HashMap<>();

  public AllocationTagHandler(HumanResourceManager resourceMgr, TaskManager taskMgr, RoleManager roleMgr) {
    super("allocation");
    myResourceManager = resourceMgr;
    myTaskManager = taskMgr;
    myRoleManager = roleMgr;
  }

  @Override
  public void process(XmlProject xmlProject) {
    xmlProject.getAllocations().forEach(this::loadAllocation);
    processRoleBindings();
  }

  private void loadAllocation(XmlAllocation xmlAllocation) throws FileFormatException {
    var taskId = xmlAllocation.getTaskId();
    var resourceId = xmlAllocation.getResourceId();
    var load = xmlAllocation.getLoad();
    var coordinator = xmlAllocation.isCoordinator();

    HumanResource human = getResourceManager().getById(resourceId);
    if (human == null) {
      throw new FileFormatException("Human resource with id=" + resourceId + " not found");
    }

    Task task = getTaskManager().getTask(taskId);
    if (task == null) {
      throw new FileFormatException("Task with id=" + taskId + " not found");
    }
    ResourceAssignment assignment = task.getAssignmentCollection().addAssignment(human);

    try {
      if (xmlAllocation.getRole() != null) {
        myLateAssigmnent2roleBinding.put(assignment, xmlAllocation.getRole());
      }
    } catch (NumberFormatException e) {
      System.out.println("ERROR in parsing XML File function id is not numeric: " + e);
    }

    assignment.setLoad(load);
    assignment.setCoordinator(coordinator);
  }

  private HumanResourceManager getResourceManager() {
    return myResourceManager;
  }

  private TaskManager getTaskManager() {
    return myTaskManager;
  }

  private Role findRole(String persistentIDasString) {
    RolePersistentID persistentID = new RolePersistentID(persistentIDasString);
    String rolesetName = persistentID.getRoleSetID();
    int roleID = persistentID.getRoleID();
    RoleSet roleSet;
    if (rolesetName == null) {
      roleSet = myRoleManager.getProjectRoleSet();
      if (roleSet.findRole(roleID) == null) {
        if (roleID <= 10 && roleID > 2) {
          roleSet = myRoleManager.getRoleSet(RoleSet.SOFTWARE_DEVELOPMENT);
          roleSet.setEnabled(true);
        } else if (roleID <= 2) {
          roleSet = myRoleManager.getRoleSet(RoleSet.DEFAULT);
        }
      }
    } else {
      roleSet = myRoleManager.getRoleSet(rolesetName);
    }
    return roleSet.findRole(roleID);
  }

  private void processRoleBindings() {
    for (Iterator<Entry<ResourceAssignment, String>> lateBindingEntries = myLateAssigmnent2roleBinding.entrySet().iterator(); lateBindingEntries.hasNext();) {
      Map.Entry<ResourceAssignment, String> nextEntry = lateBindingEntries.next();
      String persistentID = nextEntry.getValue();
      Role nextRole = findRole(persistentID);
      if (nextRole != null) {
        lateBindingEntries.remove();
        nextEntry.getKey().setRoleForAssignment(nextRole);
      }
    }
    if (!myLateAssigmnent2roleBinding.isEmpty()) {
      System.err.println("[ResourceTagHandler] parsingFinished(): not found roles:\n" + myLateAssigmnent2roleBinding);
    }
  }
}
