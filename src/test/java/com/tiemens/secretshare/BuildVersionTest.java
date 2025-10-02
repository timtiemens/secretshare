/*******************************************************************************
 * Copyright (c) 2009, 2014 Tim Tiemens.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 *
 * Contributors:
 *     Tim Tiemens - initial API and implementation
 *******************************************************************************/
package com.tiemens.secretshare;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.Properties;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.tiemens.secretshare.BuildVersion.BuildInfo;

public class BuildVersionTest
{


    // ==================================================
    // class static data
    // ==================================================

    // ==================================================
    // class static methods
    // ==================================================

    // ==================================================
    // instance data
    // ==================================================

    // ==================================================
    // factories
    // ==================================================

    // ==================================================
    // constructors
    // ==================================================

    @BeforeEach
    public void disableFailureInLoad()
    {
        BuildVersion.disableFailureInLoad();
    }


    // ==================================================
    // public methods
    // ==================================================

    @Test
    public void testCreateFromMap()
    {
        Properties props = new Properties();
        final String version = "1.2.3.4";
        props.setProperty("version", version);

        BuildVersion.BuildInfo buildInfo = BuildVersion.BuildInfo.createFromProperties(props);

        assertEquals(version, buildInfo.getVersion());
    }

    @Test
    public void testCreateMethods()
    {
        final String version = "4.2.1";
        final String date = "2014/07/04";
        final String buildNumber = "42";
        //final String artifactGroup = "com.ti";
        //final String artifactName = "ss";
        //final String uiVersion = "";

        BuildVersion.BuildInfo buildInfo1 = BuildVersion.BuildInfo.create(version);
        BuildVersion.BuildInfo buildInfo2 = BuildVersion.BuildInfo.create(version, date);
        BuildVersion.BuildInfo buildInfo3 = BuildVersion.BuildInfo.create(version, date);
        buildInfo3.setBuildNumber(buildNumber);

        subcompare("buildInfo1", buildInfo1, version, "", "", "", "", null);
        subcompare("buildInfo2", buildInfo2, version, date, "", "", "", null);
        subcompare("buildInfo3", buildInfo3, version, date, buildNumber, "", "", null);
    }

    @Test
    public void testBuilder()
    {
        final String version = "8.3.1";
        final String date = "2011/07/04";
        final String buildNumber = "142";
        final String artifactGroup = "a.com.ti";
        final String artifactName = "b.ss";

        BuildVersion.BuildInfo buildinfo = new BuildVersion.BuildInfo.Builder()
                .version(version)
                .date(date)
                .buildNumber(buildNumber)
                .artifactGroup(artifactGroup)
                .artifactName(artifactName)
                .build();

        subcompare("builder", buildinfo, version, date, buildNumber, artifactGroup, artifactName, null);
    }

    @Test
    public void testConvertToMapAndProperties()
    {
        BuildVersion.BuildInfo buildinfo = BuildVersion.BuildInfo.create("a.b.c");
        Map<Object, Object> map = buildinfo.convertToMap();
        assertNotNull(map);

        Properties props = buildinfo.convertToProperties();
        assertNotNull(props);
    }

    @Test
    public void testConvertToPropertiesNullBuildNumber()
    {
        BuildVersion.BuildInfo buildinfo = BuildVersion.BuildInfo.create("w.e.r");
        // there was a bug: when explicitly set buildNumber to null, convertToProperties() would fail.
        buildinfo.setBuildNumber(null);
        Properties props = buildinfo.convertToProperties();
        assertNotNull(props);
    }
    // ==================================================
    // non public methods
    // ==================================================

    private void subcompare(String description, BuildInfo buildInfo,
                                            String eVersion, String eDate, String eBuildNumber, String eArtifactGroup,
                                            String eArtifactName, String eUiVersion)
        {
                subcompareField(description, "version",       buildInfo.getVersion(),       eVersion);
                subcompareField(description, "date",          buildInfo.getDate(),          eDate);
                subcompareField(description, "buildNumber",   buildInfo.getBuildNumber(),   eBuildNumber);
                subcompareField(description, "artifactGroup", buildInfo.getArtifactGroup(), eArtifactGroup);
                subcompareField(description, "artifactName",  buildInfo.getArtifactName(),  eArtifactName);
                subcompareField(description, "uiVersion",     buildInfo.getUiVersion(),     eUiVersion);
        }

        private void subcompareField(String description, String fieldName,
                                                 String actual, String expected)
        {
                final String msg = description + " field '" + fieldName + "'";
                if (expected != null)
                {
                    assertEquals(expected, actual, msg);
                }
                else
                {
                        if ("uiVersion".equals(fieldName))
                        {
                                // uiVersion is never null
                                assertTrue(actual.length() > 0, msg + "uiversion.length");
                        }
                        else
                        {
                            assertNull(actual, msg + "(null)");
                        }

                }
        }


}
