/*******************************************************************************
 * $Id: $
 * Copyright (c) 2009-2017 Tim Tiemens.
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
 ******************************************************************************/
package com.tiemens.secretshare;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Two classes for managing build version and information:
 *    BuildVersion - outer, static API.
 *    BuildVersion.BuildInfo - simple data structure class for storing the version, build number, build date.
 *
 * Note: This class is shared by the Application (src/main/java/...) and
 *       by the build system (buildSrc/src/main/java...)
 *       See the symbolic link in buildSrc.
 *
 * Note: This class is duplicated between release artifacts (e.g. com.tiemens.secretshare and com.tiemens.areacalc)
 *       because trying to _share_ a common class (e.g. com.tiemens.BuildVersion) could result in incompatibilities.
 *
 */
public final class BuildVersion
{
    // ==================================================
    // class static data
    // ==================================================

    //
    public static final String PROPERTIES_FILE_NAME = "build-info.properties";


    public static final String PROPERTY_NAME_FAILURE_ACTION = "com.tiemens.secretshare.BuildVersionFailure";
    // Values:    "ignore"   "warn"   "throw" [default is throw a runtime exception]
    //     OR: unit tests can set this to true:
    /*default*/ static boolean ignoreFailureUnitTest = false;

    // ==================================================
    // class static methods
    // ==================================================
    public static void disableFailureInLoad()
    {
        BuildVersion.ignoreFailureUnitTest = true;
    }

    // ==================================================
    // instance data
    // ==================================================

    // ==================================================
    // factories
    // ==================================================

    // ==================================================
    // constructors
    // ==================================================

    // ==================================================
    // public methods
    // ==================================================


    /**
     * @return The version of the project from the build.gradle file.
     *    e.g. "1.3.2-SNAPSHOT"
     */
    public static String getVersion()
    {
        return instance().getVersion();
    }

    /**
     * @return The group of the project from the build.gradle file.
     *    e.g. "com.tiemens"
     */
    public static String getGroup()
    {
        return instance().getArtifactGroup();
    }

    /**
     *
     * @return The date this file was generated, usually the last date that the project was modified.
     *    e.g. "Tue Jun 10 20:42:34 CDT 2014"
     */
    public static String getDate()
    {
        return instance().getDate();
    }

    /**
     * @return "" or build number if available - build systems like Jenkins will set the BUILD_NUMBER
     *    e.g. "41"
     */
    public static String getBuildNumber()
    {
        return instance().getBuildNumber();
    }

    /**
     * @return The details for display to humans.
     *     This is a combination of VERSION and BUILD_NUMBER.
     *     e.g. "1.3.2-SNAPSHOT" or "1.3.2-SNAPSHOT-b41"
     */
    public static String getUiVersion()
    {
        return instance().getUiVersion();
    }

    /**
     * @return The full details of the version, including the build date.
     */
    public static String getDetailedVersion()
    {
        return instance().getDetailedVersion();
    }

    private static BuildInfo singleton;
    public static BuildInfo instance()
    {
        if (singleton == null)
        {
             singleton = createInstance();
        }
        return singleton;
    }

    private static BuildInfo createInstance()
    {
        BuildInfo ret = null;
        // By default, look for build-info.properties file next to BuildVersion.class
        try
        {
            Properties props = new Properties();
            props.load(BuildVersion.class.getResourceAsStream("build-info.properties"));
            ret = BuildInfo.createFromProperties(props);
        }
        catch (Exception e)  // RuntimeException || IOException
        {
            String value = System.getProperty(PROPERTY_NAME_FAILURE_ACTION);
            if (("ignore".equalsIgnoreCase(value)) || ignoreFailureUnitTest)
            {
                ret = createFakeBuildInfo();
            }
            else if ("warn".equalsIgnoreCase(value))
            {
                System.err.println("BuildVersion read error was ignored.");
                e.printStackTrace(System.err);
                ret = createFakeBuildInfo();
            }
            else
            {
                if (e instanceof RuntimeException)
                {
                    throw (RuntimeException) e;
                }
                else
                {
                    throw new RuntimeException(e);
                }
            }
        }
        return ret;
    }


    private static BuildInfo createFakeBuildInfo()
    {
        return new BuildInfo.Builder().version("failsafe-version-string").build();
    }


    /**
     * Data structure for build information.
     * Knows how to read/write to .properties file.
     *
     */
    public static class BuildInfo
    {
        // ==================================================
        // class static data
        // ==================================================

        /**
         * Corresponds to the keys in the .properties file and in Map<Object,Object> read/write.
         */
        private static List<String> propNames =
                Arrays.asList("version",
                              "date", "buildNumber",
                              "artifactGroup", "artifactName",
                              "uiVersion");

        // ==================================================
        // class static methods
        // ==================================================

        // ==================================================
        // instance data
        // ==================================================

        /** The version of the project - full string, but it does not include build number. No default. */
        private String version; //  = "1.3.2-SNAPSHOT";

        /** The group of the release artifact. */
        private String artifactGroup = ""; //  = "com.tiemens";

        /** The name of the release artifact. */
        private String artifactName = ""; //  = "secretshare";

        /** The date this build was generated, usually the last date that the project was modified. */
        private String date = ""; // = "Tue Jun 10 20:42:34 CDT 2014";

        /** The build number.  Can be empty "". */
        private String buildNumber = ""; // = "";  or = "42"

        /** The details for display to humans.  Can be empty null.
         *  When null (the default), getUiVersion() returns a combination of VERSION and BUILD_NUMBER.
         */
        private String uiVersion; //= "1.3.2-SNAPSHOT";  or "1.3.2-SNAPSHOT-b42"


        // ==================================================
        // factories
        // ==================================================
        public static BuildInfo createFromPropertiesFile(File propertiesFile)
        {
            Properties props = new Properties();
            try
            {
                props.load(new FileReader(propertiesFile));
                return createFromProperties(props);
            }
            catch (FileNotFoundException e)
            {
                throw new RuntimeException("BuildInfo properties file not found: " + propertiesFile, e);
            }
            catch (IOException e)
            {
                throw new RuntimeException("BuildInfo properties io error: " + propertiesFile, e);
            }
        }

        public static BuildInfo createFromProperties(Properties properties)
        {
            return createFromMap(properties);
        }

        public static BuildInfo createFromMap(Map<Object, Object> properties)
        {
            BuildInfo ret = new BuildInfo();
            for (String property : propNames)
            {
                ret.genericSet(property, properties);
            }
            return ret;
        }

        public static BuildInfo create(String version)
        {
            return create(version, "");
        }

        public static BuildInfo create(String version, String date)
        {
            BuildInfo ret = new BuildInfo();
            ret.setVersion(version);
            ret.setDate(date);
            return ret;
        }

        // ==================================================
        // constructors
        // ==================================================

        // ==================================================
        // public methods
        // ==================================================

        /**
         * @return The full version of the project, but not including the build number,
         *    e.g. "1.3.2-SNAPSHOT"
         */
        public String getVersion()
        {
            return version;
        }

        /**
         * @return The group of the release artifact,
         *    e.g. "com.tiemens"
         */
        public String getArtifactGroup()
        {
            return artifactGroup;
        }

        /**
         * @return The name of the release artifact,
         *    e.g. "secretshare"
         */
        public String getArtifactName()
        {
            return artifactName;
        }

        /**
         *
         * @return The date this artifact was built,
         *    e.g. "Tue Jun 10 20:42:34 CDT 2014"
         */
        public String getDate()
        {
            return date;
        }

        /**
         * @return "" or build number if available - build systems like Jenkins will set the BUILD_NUMBER
         *    e.g. "41"
         */
        public String getBuildNumber()
        {
            return buildNumber;
        }

        /**
         * @return The details for display to humans.
         *     This is a combination of VERSION and BUILD_NUMBER.
         *     e.g. "1.3.2-SNAPSHOT" or "1.3.2-SNAPSHOT-b41"
         */
        public final String getUiVersion()
        {
            if (uiVersion != null)
            {
                return uiVersion;
            }
            else
            {
                if ((getBuildNumber() == null) || getBuildNumber().trim().isEmpty())
                {
                    return getVersion();
                }
                else
                {
                    return getVersion() + "-build-" + getBuildNumber();
                }
            }
        }

        /**
         * @return The full details of the version, including the build date.
         */
        public final String getDetailedVersion()
        {
            return getArtifactGroup() + ":" + getArtifactName() + ":" +
                   getVersion() + ":" +
                   getBuildNumber() + ":" +
                   getDate();
        }

        public void convertToPropertiesFile(File propertiesFile)
        {
            try
            {
                String comments = "Generated by build system, BuildVersion.BuildInfo";
                this.convertToProperties().store(new FileWriter(propertiesFile), comments);
            }
            catch (IOException e)
            {
                throw new RuntimeException("BuildInfo write to file io error: " + propertiesFile, e);
            }
        }
        public Properties convertToProperties()
        {
            Map<Object, Object> map = convertToMap();
            Properties properties = new Properties();
            for (Object key : map.keySet())
            {
                String value = (String) map.get(key);
                if (value != null)
                {
                    properties.setProperty((String) key, value);
                }
            }
            return properties;
        }
        public Map<Object, Object> convertToMap()
        {
            Map<Object, Object> ret = new HashMap<Object, Object>();
            for (String property : propNames)
            {
                ret.put(property, this.genericGet(property));
            }
            return ret;
        }





        public void setVersion(String version)
        {
            this.version = version;
        }

        public void setArtifactGroup(String artifactGroup)
        {
            this.artifactGroup = artifactGroup;
        }

        public void setArtifactName(String artifactName)
        {
            this.artifactName = artifactName;
        }

        public void setDate(String date)
        {
            this.date = date;
        }

        public void setBuildNumber(String buildNumber)
        {
            this.buildNumber = buildNumber;
        }

        public void setUiVersion(String uiVersion)
        {
            this.uiVersion = uiVersion;
        }

        // ==================================================
        // private methods
        // ==================================================

        private static String upfirst(String in)
        {
            return in.substring(0, 1).toUpperCase() + in.substring(1);
        }

        private String genericGet(String property)
        {
            try
            {
                Method method = this.getClass().getMethod("get" + upfirst(property));

                Object ret = method.invoke(this);

                return (String) ret;
            }
            catch (Exception e)
            {
                throw new RuntimeException("Failed to reflection get property '" + property + "'", e);
            }
        }

        private void genericSet(String property, Map<Object, Object> properties)
        {
            final Object value = properties.get(property);
            if (value == null)
            {
                if ("version".equals(property))
                {
                    throw new RuntimeException("Missing property 'version'");
                }
            }
            else
            {
                try
                {
                    Method method = this.getClass().getMethod("set" + upfirst(property), String.class);

                    method.invoke(this, value);
                }
                catch (Exception e)
                {
                    throw new RuntimeException("Failed to reflection set property '" + property + "' to value '" + value + "'", e);
                }
            }
        }

        public static class Builder
        {
            public BuildInfo build;

            public Builder()
            {
                build = new BuildInfo();
            }

            public BuildInfo build()
            {
                return build;
            }

            public Builder version(String version)
            {
                build.setVersion(version);
                return this;
            }
            public Builder date(String date)
            {
                build.setDate(date);
                return this;
            }
            public Builder buildNumber(String buildNumber)
            {
                build.setBuildNumber(buildNumber);
                return this;
            }
            public Builder artifactGroup(String artifactGroup)
            {
                build.setArtifactGroup(artifactGroup);
                return this;
            }
            public Builder artifactName(String artifactName)
            {
                build.setArtifactName(artifactName);
                return this;
            }
            public Builder uiVersion(String uiVersion)
            {
                build.setUiVersion(uiVersion);
                return this;
            }
        }
    } // class BuildInfo



}
