/*
Copyright 2003-2012 Dmitry Barashev, GanttProject Team

This file is part of GanttProject, an opensource project management tool.

GanttProject is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

GanttProject is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with GanttProject.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sourceforge.ganttproject;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.eclipse.core.runtime.Platform.getUpdater;

public abstract class GPVersion {
  public static String BUILD = "2906"; // BUILD NUMBER
  public static String V2_0_1 = "2.0.1";
  public static String V2_0_2 = "2.0.2";
  public static String V2_0_3 = "2.0.3";
  public static String V2_0_4 = "2.0.4";
  public static String V2_0_5 = "2.0.5";
  public static String V2_0_6 = "2.0.6";
  public static String V2_0_7 = "2.0.7";
  public static String V2_0_8 = "2.0.8";
  public static String V2_0_9 = "2.0.9";
  public static String V2_0_10 = "2.0.10";
  public static String V2_0_X = V2_0_10;
  public static String PRAHA = "2.5.5 Praha (build 1256)";
  public static String BRNO = "2.6 Brno (build 1473)";
  public static String BRNO_2_6_1 = "2.6.1 Brno (build 1499)";
  public static String BRNO_2_6_2 = "2.6.2 Brno (build 1544)";
  public static String BRNO_2_6_3 = "2.6.3 Brno (build 1610)";
  public static String BRNO_2_6_4 = "2.6.4 Brno (build 1622)";
  public static String BRNO_2_6_5 = "2.6.5 Brno (build 1638)";
  public static String BRNO_2_6_6 = "2.6.6 Brno (build 1715)";
  public static String OSTRAVA = "2.7 Ostrava (build 1891)";
  public static String OSTRAVA_2_7_1 = "2.7.1 Ostrava (build 1924)";
  public static String OSTRAVA_2_7_2 = "2.7.2 Ostrava (build 1954)";
  public static String PILSEN = "2.8 Pilsen (build 2016)";
  public static String PILSEN_2_8_1 = "2.8.1 Pilsen (build 2024)";
  public static String PILSEN_2_8_2 = "2.8.2 Pilsen (build 2069)";
  public static String PILSEN_2_8_3 = String.format("2.8.3 Pilsen (build 2088)");
  public static String PILSEN_2_8_4 = String.format("2.8.4 Pilsen (build 2134)");
  public static String PILSEN_2_8_5 = String.format("2.8.5 Pilsen (build 2179)");
  public static String PILSEN_2_8_6 = String.format("2.8.6 Pilsen (build 2233)");
  public static String PILSEN_2_8_7 = String.format("2.8.7 Pilsen (build 2262)");
  public static String PILSEN_2_8_8 = String.format("2.8.8 Pilsen (build 2308)");
  public static String PILSEN_2_8_9 = String.format("2.8.9 Pilsen (build 2335)");
  public static String PILSEN_2_8_10 = String.format("2.8.10 Pilsen (build 2364)");
  public static String PILSEN_2_8_11 = String.format("2.8.11 Pilsen (build %s)", BUILD);
//  public static String DEV = getUpdater().getInstalledUpdateVersions().stream().max(String::compareTo)
//      .orElse(String.format("2.99.%s", BUILD));
//  public static String CURRENT = DEV;

  public static String getCurrentVersionNumber() {
    var updater = getUpdater();
    var version = updater == null
        ? null : updater.getInstalledUpdateVersions().stream().max(String::compareTo)
        .orElse(null);
    return version == null ? String.format("3.0.%s", BUILD) : version;
  }

  public static String getCurrentBuildNumber() {
    return getCurrentVersionNumber().split("\\.")[2];
  }

  public static String getCurrentShortVersionNumber() {
    return Arrays.stream(getCurrentVersionNumber().split("\\.")).limit(2).collect(Collectors.joining("."));
  }
}
