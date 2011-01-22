/***************************************************************************
 * GanttGraphicArea.java  -  description
 * -------------------
 * begin                : dec 2002
 * copyright            : (C) 2002 by Thomas Alexandre
 * email                : alexthomas(at)ganttproject.org
 ***************************************************************************/

/***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

package net.sourceforge.ganttproject;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.util.Date;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.table.JTableHeader;

import net.sourceforge.ganttproject.chart.ChartModelBase;
import net.sourceforge.ganttproject.chart.ChartModelResource;
import net.sourceforge.ganttproject.chart.ChartSelection;
import net.sourceforge.ganttproject.chart.ChartViewState;
import net.sourceforge.ganttproject.chart.RenderedChartImage;
import net.sourceforge.ganttproject.chart.RenderedResourceChartImage;
import net.sourceforge.ganttproject.chart.ResourceChart;
import net.sourceforge.ganttproject.font.Fonts;
import net.sourceforge.ganttproject.gui.zoom.ZoomManager;
import net.sourceforge.ganttproject.language.GanttLanguage;
import net.sourceforge.ganttproject.resource.HumanResource;
import net.sourceforge.ganttproject.resource.HumanResourceManager;
import net.sourceforge.ganttproject.task.TaskLength;
import net.sourceforge.ganttproject.task.TaskManager;
import net.sourceforge.ganttproject.time.TimeUnit;
import net.sourceforge.ganttproject.time.gregorian.GregorianCalendar;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * Class for the graphic part of the soft
 */
public class ResourceLoadGraphicArea extends ChartComponentBase implements
        ResourceChart {


    /** Render the ganttproject version */
    private boolean drawVersion = false;

    /** The main application */
    private GanttProject appli;

    private ChartModelResource myChartModel;

    private ChartViewState myViewState;

    public ResourceLoadGraphicArea(GanttProject app, ZoomManager zoomManager) {
        super(app.getProject(), app.getUIFacade(), zoomManager);
        this.setBackground(Color.WHITE);
        myChartModel = new ChartModelResource(getTaskManager(),
                (HumanResourceManager) app.getHumanResourceManager(),
                getTimeUnitStack(), getUIConfiguration(), (ResourceChart) this);
        myChartImplementation = new ResourcechartImplementation(app.getProject(), myChartModel, this);
        myViewState = new ChartViewState(this, app.getUIFacade());
        super.setStartDate(GregorianCalendar.getInstance().getTime());
        appli = app;
        //myTableHeader = app.getResourcePanel().table.getTableHeader();
    }

    /** The size of the panel. */
    public Dimension getPreferredSize() {
        return new Dimension(465, 600);
    }

    protected int getRowHeight() {
        return appli.getResourcePanel().table.getRowHeight();
    }

    public void drawGPVersion(Graphics g) {
        g.setColor(Color.black);
        g.setFont(Fonts.GP_VERSION_FONT);
        g.drawString("GanttProject (" + GanttProject.version + ")", 3,
                getHeight() - 8);
    }

    public BufferedImage getChart(GanttExportSettings settings) {
        RenderedChartImage renderedImage = (RenderedChartImage) getRenderedImage(settings);
        BufferedImage result = renderedImage.getWholeImage();
        repaint();
        return result;
    }

    /** @return an image with the gantt chart */
    public RenderedImage getRenderedImage(GanttExportSettings settings) {
        ResourceTreeTable treetable = Mediator.getGanttProjectSingleton().getResourcePanel().getResourceTreeTable();
        org.jdesktop.swing.JXTreeTable xtreetable = treetable.getTreeTable();

//      I don't know why we need to add 62 to the height to make it fit the real height
        int tree_height = xtreetable.getHeight()+62;

        //GanttImagePanel logo_panel= new GanttImagePanel("big.png", 1024, 40);
        BufferedImage tree  = new BufferedImage(xtreetable.getWidth(), tree_height, BufferedImage.TYPE_INT_RGB);
        BufferedImage treeview = new BufferedImage(treetable.getWidth(), treetable.getHeight(), BufferedImage.TYPE_INT_RGB);
        BufferedImage logo  = new BufferedImage(xtreetable.getWidth(), 40, BufferedImage.TYPE_INT_RGB);

        Graphics2D glogo = logo.createGraphics();
        glogo.drawImage(AbstractChartImplementation.LOGO.getImage(), 0, 0, null);

        Graphics2D gtreeview = treeview.createGraphics();
        treetable.paintComponents(gtreeview);

        BufferedImage header = treeview.getSubimage(0, 0, treeview.getWidth(), treetable.getRowHeight()+3);
        treeview.flush();

        Graphics2D gtree = tree.createGraphics();
        xtreetable.printAll(gtree);

        //create a new image that will contain the logo, the table/tree and the chart
        BufferedImage resource_image = new BufferedImage(
            xtreetable.getWidth(), 
            tree_height+AbstractChartImplementation.LOGO.getIconHeight(), 
            BufferedImage.TYPE_INT_RGB);

        Graphics2D gimage = resource_image.createGraphics();

        //draw the logo on the image
        gimage.drawImage(logo, 0, 0, tree.getWidth(), logo.getHeight(), Color.WHITE, null);
        //draw the header on the image
        gimage.drawImage(header, 0, logo.getHeight(), header.getWidth(), header.getHeight(), null);
        //draw the tree on the image
        gimage.drawImage(tree, 0, logo.getHeight()+header.getHeight(), tree.getWidth(), tree.getHeight(), null);

        Date dateStart = null;
        Date dateEnd = null;

        TimeUnit unit = getViewState().getBottomTimeUnit();

        dateStart = settings.getStartDate() == null ? getStartDate() : settings.getStartDate();
        dateEnd = settings.getEndDate() == null ? getEndDate() : settings.getEndDate();

        if (dateStart.after(dateEnd)) {
            Date tmp = (Date) dateStart.clone();
            dateStart = (Date) dateEnd.clone();
            dateEnd = tmp;
        }

        TaskLength printedLength = getTaskManager().createLength(unit, dateStart, dateEnd);

        int chartWidth = (int) ((printedLength.getLength(getViewState().getBottomTimeUnit()) + 1) * getViewState().getBottomUnitWidth());
        if (chartWidth<this.getWidth()) {
            chartWidth = this.getWidth();
        }
        int chartHeight = resource_image.getHeight();

        return new RenderedResourceChartImage(myChartModel, myChartImplementation,  resource_image, chartWidth, chartHeight);
    }

    public String getName() {
        return GanttLanguage.getInstance().getText("resourcesChart");
    }

    public Date getStartDate() {
        // return this.beg.getTime();
        return getTaskManager().getProjectStart();
    }

    public Date getEndDate() {
        return getTaskManager().getProjectEnd();
    }

    protected ChartModelBase getChartModel() {
        return myChartModel;
    }

    protected MouseListener getMouseListener() {
        if (myMouseListener == null) {
            myMouseListener = new MouseListenerBase() {
                protected Action[] getPopupMenuActions() {
                    return new Action[] { getOptionsDialogAction()};
                }

            };
        }
        return myMouseListener;
    }

    protected MouseMotionListener getMouseMotionListener() {
        if (myMouseMotionListener == null) {
            myMouseMotionListener = new MouseMotionListenerBase();
        }
        return myMouseMotionListener;
    }

    protected AbstractChartImplementation getImplementation() {
        return myChartImplementation;
    }

    public boolean isExpanded(HumanResource resource) {
        return true;
    }

    private MouseMotionListener myMouseMotionListener;

    private MouseListener myMouseListener;

    private final AbstractChartImplementation myChartImplementation;

    public void setTaskManager(TaskManager taskManager) {
        // TODO Auto-generated method stub
    }

    public void reset() {
    	repaint();
    }

    public Icon getIcon() {
        // TODO Auto-generated method stub
        return null;
    }

    private class ResourcechartImplementation extends AbstractChartImplementation {

        public ResourcechartImplementation(
                IGanttProject project, ChartModelBase chartModel, ChartComponentBase chartComponent) {
            super(project, chartModel, chartComponent);
            // TODO Auto-generated constructor stub
        }
        public void paintChart(Graphics g) {
            synchronized (ChartModelBase.STATIC_MUTEX) {
                // LaboPM
                // ResourceLoadGraphicArea.super.paintComponent(g);
                if (isShowing()) {
                    myChartModel.setHeaderHeight(getImplementation().getHeaderHeight(
                        appli.getResourcePanel(), appli.getResourcePanel().table.getTable()));
                }
                myChartModel.setBottomUnitWidth(getViewState().getBottomUnitWidth());
                myChartModel.setRowHeight(getRowHeight());// myChartModel.setRowHeight(tree.getJTree().getRowHeight());
                myChartModel.setTopTimeUnit(getViewState().getTopTimeUnit());
                myChartModel.setBottomTimeUnit(getViewState().getBottomTimeUnit());
                //myChartModel.paint(g);
                super.paintChart(g);

                if (drawVersion) {
                    drawGPVersion(g);
                }
            }
        }
        public ChartSelection getSelection() {
            ChartSelectionImpl result = new ChartSelectionImpl() {
                public boolean isEmpty() {
                    return false;
                }
                public void startCopyClipboardTransaction() {
                    super.startCopyClipboardTransaction();
                    appli.getResourcePanel().copySelection();
                }

                public void startMoveClipboardTransaction() {
                    super.startMoveClipboardTransaction();
                    appli.getResourcePanel().cutSelection();
                }
            };
            return result;
        }
        public IStatus canPaste(ChartSelection selection) {
            return Status.OK_STATUS;
        }
        public void paste(ChartSelection selection) {
            appli.getResourcePanel().pasteSelection();
        }
    }

    @Override
    public ChartViewState getViewState() {
        return myViewState;
    }
}