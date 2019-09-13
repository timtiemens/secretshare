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
package com.tiemens.secretshare.jdeps.model;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public final class JDeps
{
    private List<JdepsLine> parsedLines;
    private Set<String> excludeStartsWith = new HashSet<>();
    private Map<String, JavaPackage> packageMap;

    public static JDeps createFromCommandLine(String classesDir)
    {
        List<String> lines = runProcess("jdeps", classesDir);
        JDeps jdeps = null;

        if (lines != null)
        {
            jdeps = JDeps.parseJDeps(lines);
        }

        return jdeps;
    }

    public static JDeps parseJDeps(List<String> lines)
    {
        JDeps ret = new JDeps();
        ret.parsedLines = parseLines(lines);
        return ret;
    }


    private static List<JdepsLine> parseLines(List<String> lines)
    {
        List<JdepsLine> ret = new ArrayList<>();
        List<ParseJdepsLine> parsers = new ArrayList<>();
        parsers.add(new ParseJdepsLineCrap());
        parsers.add(new ParseJdepsLineModule2Module());
        parsers.add(new ParseJdepsLinePackage2PackageModule());

        for (String line :  lines)
        {
            JdepsLine answer = null;
            for (ParseJdepsLine parser: parsers)
            {
                JdepsLine parsed = parser.parseLine(line);
                if (parsed != null)
                {
                    answer = parsed;
                    break;
                }
            }
            if (answer != null)
            {
                ret.add(answer);
            }
            else
            {
                throw new RuntimeException("Failed to parse line '" + line + "'");
            }
        }
        return ret;
    }

    private JDeps()
    {

    }


    //
    //   ProcessBuilder processBuilder = new ProcessBuilder("jdeps", "build/classes/java/main");
    private static List<String> runProcess(String command, String arg)
    {
        List<String> lines = new ArrayList<>();
        try
        {
            ProcessBuilder processBuilder = new ProcessBuilder(command, arg);
            final Process process = processBuilder.start();

            int exitCode = process.waitFor();
            //System.out.println("Exit code = " + exitCode);
            if (exitCode == 0)
            {
                BufferedReader br;
                br = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = br.readLine()) != null)
                {
                    lines.add(line);
                    //System.out.println(line);
                }
            }

            return lines;

        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }

    //
    //
    public void addExcludes(List<String> addTheseStartsWith)
    {
        for (String exclude : addTheseStartsWith)
        {
            addExcludeStartsWith(exclude);
        }
    }


    private void addExcludeStartsWith(String exclude)
    {
        if (exclude.endsWith("*"))
        {
            exclude = exclude.substring(0, exclude.length() - 1);
            // System.out.println("NOW exclude=" + exclude);
        }
        excludeStartsWith.add(exclude);
    }

    public void analyze()
    {
        boolean error = false;

        error = error || lines2JavaPackages();

        // more steps?

        if (error)
        {
            throw new RuntimeException("Error analyzing input");
        }
    }

    /**
     * Indicates whether the analyzed packages match the specified
     * dependency constraint.
     *
     * @return <code>true</code> if the packages match the dependency
     *         constraint
     */
    public boolean dependencyMatch(DependencyConstraint constraint)
    {
        return constraint.match(getPackages());
    }

    private boolean lines2JavaPackages()
    {
        packageMap = new HashMap<>();
        boolean error = false;

        for (JdepsLine depsline : parsedLines)
        {
            if (depsline instanceof JDeps.JdepsLinePackage2PackageModule)
            {
                JDeps.JdepsLinePackage2PackageModule p2pm = (JDeps.JdepsLinePackage2PackageModule) depsline;
                String jpackage = p2pm.getUserPackage();
                String dependsOn = p2pm.getUsesPackage();

                if (keepFilter(jpackage))
                {
                    if (keepFilter(dependsOn))
                    {
                        JavaPackage findJavaPackage = getPackage(jpackage);
                        JavaPackage dependsOnPackage = getPackage(dependsOn);
                        //System.out.println("ADDING  jpackage=" + jpackage + " dependsOn=" + dependsOn);
                        findJavaPackage.dependsUpon(dependsOnPackage);
                    }
                    else
                    {
                        //System.out.println("EXCLUDE dependsOn=" + dependsOn);
                    }
                }
                else
                {
                    //System.out.println("EXCLUDE jpackage=" + jpackage);
                }
            }
        }
        return error;
    }

    public JavaPackage getPackage(final String jpackage)
    {
        if (! packageMap.containsKey(jpackage))
        {
            JavaPackage add = new JavaPackage(jpackage);
            packageMap.put(jpackage, add);
        }

        return packageMap.get(jpackage);
    }

    public Collection<JavaPackage> getPackages()
    {
        return packageMap.values();
    }


    private boolean keepFilter(final String name)
    {
        for (String exclude : excludeStartsWith)
        {
            if (name.startsWith(exclude))
            {
                return false;
            }
        }
        return true;
    }


    //
    //
    public interface ParseJdepsLine
    {
        public JdepsLine parseLine(String line);
    }
    public static class ParseJdepsLineModule2Module implements ParseJdepsLine
    {

        private static Pattern pattern = Pattern.compile("^(\\S+)\\s+->\\s+(.+)$");

        @Override
        public JdepsLine parseLine(String line)
        {
            Matcher matcher = pattern.matcher(line);
            if (matcher.matches())
            {
                return new JdepsLineModule2Module(matcher.group(1), matcher.group(2));
            }
            else
            {
                return null;
            }
        }
    }

    public static class ParseJdepsLinePackage2PackageModule implements ParseJdepsLine
    {

        private static Pattern pattern = Pattern.compile("^\\s+(\\S+)\\s+->\\s+(\\S+)\\s+(.+)$");

        @Override
        public JdepsLine parseLine(String line)
        {
            Matcher matcher = pattern.matcher(line);
            if (matcher.matches())
            {
                return new JdepsLinePackage2PackageModule(matcher.group(1), matcher.group(2), matcher.group(3));
            }
            else
            {
                return null;
            }
        }

    }

    public static class ParseJdepsLineCrap implements ParseJdepsLine
    {

        private static Pattern pattern1 = Pattern.compile("^(\\S+)$");
        private static Pattern pattern2 = Pattern.compile("^ \\[file:(.*)$");
        private static Pattern pattern3 = Pattern.compile("^   (requires .*)$");
        private static List<Pattern> patterns = List.of(pattern1,  pattern2, pattern3);

        @Override
        public JdepsLine parseLine(String line)
        {
            for (Pattern pattern : patterns)
            {
                Matcher matcher = pattern.matcher(line);
                if (matcher.matches())
                {
                    return new JdepsLineCrap(matcher.group(1));
                }
            }
            return null;

        }

    }

    /**
     * Sample output file with module-info.java in secretshare:
           wTF?  no -> ?
           WTF? 1 space then [] ?
           WTF? 3 spaces then requires ?
com.tiemens.secretshare
 [file:///home/tim/workspace/secretshare-docs/build/classes/java/main/]
   requires java.base (@11.0.4)
   requires transitive java.logging (@11.0.4)
com.tiemens.secretshare -> java.base
com.tiemens.secretshare -> java.logging
   com.tiemens.secretshare              -> java.io                                  java.base
   com.tiemens.secretshare.engine       -> com.tiemens.secretshare.exceptions       com.tiemens.secretshare
        etc.
     *
     *
     */
    public abstract static class JdepsLine
    {

    }
    public static class JdepsLineModule2Module extends JdepsLine
    {
        private final String userModule;
        private final String usesModule;
        public JdepsLineModule2Module(String userModule, String usesModule)
        {
            super();
            this.userModule = userModule;
            this.usesModule = usesModule;
        }
        public String getUserModule()
        {
            return userModule;
        }
        public String getUsesModule()
        {
            return usesModule;
        }
    }
    public static class JdepsLinePackage2PackageModule extends JdepsLine
    {
        private final String userPackage;
        private final String usesPackage;
        private final String usesModule;
        public JdepsLinePackage2PackageModule(String userPackage,
                String usesPackage, String usesModule)
        {
            super();
            this.userPackage = userPackage;
            this.usesPackage = usesPackage;
            this.usesModule = usesModule;
        }
        public String getUserPackage()
        {
            return userPackage;
        }
        public String getUsesPackage()
        {
            return usesPackage;
        }
        public String getUsesModule()
        {
            return usesModule;
        }
    }
    public static class JdepsLineCrap extends JdepsLine
    {
        private final String crapLine;

        public JdepsLineCrap(String crap)
        {
            super();
            this.crapLine = crap;
        }

        public String getCrapLine()
        {
            return crapLine;
        }

    }


    public boolean containsCycles()
    {
        // TODO Auto-generated method stub
        return false;
    }


}
