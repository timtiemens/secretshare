package com.tiemens.secretshare;

/**
 * Simple class for storing the version derived from the build system.
 *
 */
public final class BuildVersion
{
    /** The version of the project from the gradle build.gradle file. */
    private String VERSION; //  = "1.3.2-SNAPSHOT";

    /** The group of the project from the gradle build.gradle file. */
    private String GROUP; //  = "com.tiemens";

    /** The date this file was generated, usually the last date that the project was modified. */
    private String DATE; // = "Tue Jun 10 20:42:34 CDT 2014";

    /** The build number. */
    private String BUILD_NUMBER; // = "";

    /** The details for display to humans.
     *  This is a combination of VERSION and BUILD_NUMBER.
     */
    private String UI_VERSION; //= "1.3.2-SNAPSHOT";


    /**
     * @return The version of the project from the build.gradle file.
     *    e.g. "1.3.2-SNAPSHOT"
     */
    public static String getVersion() {
        return instance().fieldVersion();
    }

    /**
     * @return The group of the project from the build.gradle file.
     *    e.g. "com.tiemens"
     */
    public static String getGroup() {
        return instance().fieldGroup();
    }

    /**
     *
     * @return The date this file was generated, usually the last date that the project was modified.
     *    e.g. "Tue Jun 10 20:42:34 CDT 2014"
     */
    public static String getDate() {
        return instance().fieldDate();
    }

    /**
     * @return "" or build number if available - build systems like Jenkins will set the BUILD_NUMBER
     *    e.g. "41"
     */
    public static String getBuildNumber() {
        return instance().fieldBuildNumber();
    }

    /**
     * @return The details for display to humans.
     *     This is a combination of VERSION and BUILD_NUMBER.
     *     e.g. "1.3.2-SNAPSHOT" or "1.3.2-SNAPSHOT-b41"
     */
    public static String getUiVersion() {
        return instance().fieldUiVersion();
    }



    /**
     * @return The full details of the version, including the build date.
     */
    public static String getDetailedVersion() {
        return instance().fieldDetailedVersion();
    }

    private static BuildVersion singleton = new BuildVersion();
    public static BuildVersion instance() {
        return singleton;
    }




    /**
     * @return The version of the project from the build.gradle file.
     *    e.g. "1.3.2-SNAPSHOT"
     */
    public String fieldVersion() {
        return VERSION;
    }

    /**
     * @return The group of the project from the build.gradle file.
     *    e.g. "com.tiemens"
     */
    public String fieldGroup() {
        return GROUP;
    }

    /**
     *
     * @return The date this file was generated, usually the last date that the project was modified.
     *    e.g. "Tue Jun 10 20:42:34 CDT 2014"
     */
    public String fieldDate() {
        return DATE;
    }

    /**
     * @return "" or build number if available - build systems like Jenkins will set the BUILD_NUMBER
     *    e.g. "41"
     */
    public String fieldBuildNumber() {
        return BUILD_NUMBER;
    }

    /**
     * @return The details for display to humans.
     *     This is a combination of VERSION and BUILD_NUMBER.
     *     e.g. "1.3.2-SNAPSHOT" or "1.3.2-SNAPSHOT-b41"
     */
    public String fieldUiVersion() {
        return UI_VERSION;
    }

    /**
     * @return The full details of the version, including the build date.
     */
    public String fieldDetailedVersion() {
        return fieldGroup() + ":" + fieldVersion() + ":" +
                fieldBuildNumber() + ":" + fieldDate();
    }

}
