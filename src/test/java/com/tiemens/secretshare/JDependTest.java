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

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import jdepend.framework.DependencyConstraint;
import jdepend.framework.JDepend;
import jdepend.framework.JavaPackage;
import jdepend.framework.PackageFilter;
import junit.framework.TestCase;

/**
 * The <code>JDependTest</code> is an example <code>TestCase</code>
 * that demonstrates how to call the JDepend library.
 * Some of the tests are:
 * <li>package dependency constraints,
 * <li>existence of cyclic package dependencies,
 * <li>measuring the distance from the main sequence (D)
 *
 * The parts to configure are at the top of the .java file.
 *
 * Prints a DOT digraph of the dependency graph.
 * See http://www.webgraphviz.com/ to create the image of the digraph.
 */

public class JDependTest extends TestCase
{

    // Note: this example does not analyze test classes:
    private static String classesDir = "./build/classes/java/main";

    private static List<String> excludes = Arrays.asList(
            // Standard libraries
            "java.*", "javax.*"
            //
            // Test libraries - if you analyze test classes, then these are useful:
            //"org.junit", "org.junit.*",
            //"junit.framework", "junit.textui",
            //"jdepend.framework"
            //
            // any extras go here
            );

    /*
     * Builds the di-graph of the package dependencies of interest.
     */
    private static DependencyConstraint createDependencyConstraint()
    {
        DependencyConstraint constraint = new DependencyConstraint();

        JavaPackage ssTop        = constraint.addPackage("com.tiemens.secretshare");
        JavaPackage ssEngine     = constraint.addPackage("com.tiemens.secretshare.engine");
        JavaPackage ssException  = constraint.addPackage("com.tiemens.secretshare.exceptions");
        //JavaPackage ssMain      = constraint.addPackage("com.tiemens.secretshare.main");
        JavaPackage ssMath       = constraint.addPackage("com.tiemens.secretshare.math");
        JavaPackage ssMd5        = constraint.addPackage("com.tiemens.secretshare.md5sum");
        JavaPackage ssMathMatrix = constraint.addPackage("com.tiemens.secretshare.math.matrix");
        JavaPackage ssMainTest   = constraint.addPackage("com.tiemens.secretshare.main.test");
        JavaPackage ssMainCli    = constraint.addPackage("com.tiemens.secretshare.main.cli");

        ssEngine.dependsUpon(ssException);
        ssEngine.dependsUpon(ssMath);
        ssEngine.dependsUpon(ssMathMatrix);

        ssMainCli.dependsUpon(ssTop);
        ssMainCli.dependsUpon(ssEngine);
        ssMainCli.dependsUpon(ssException);
        ssMainCli.dependsUpon(ssMath);

        ssMainTest.dependsUpon(ssEngine);

        ssMath.dependsUpon(ssException);
        ssMath.dependsUpon(ssMd5);

        ssMathMatrix.dependsUpon(ssMath);

        ssMd5.dependsUpon(ssException);

        return constraint;
    }

    // if testDependencyConstraint fails, then set this to true to
    //    see the mis-match.  Or run the JDepend gui with:
    // java -cp $(find $HOME/.gradle|grep -i jdep|grep jar) jdepend.swingui.JDepend ./build/classes/
    private static boolean debugDump = false;


    //
    // ===================================
    //
    private JDepend jdepend;


    public JDependTest(String name)
    {
        super(name);
    }

    @Override
    protected void setUp() throws IOException
    {
        PackageFilter filter = new PackageFilter();
        for (String pkg : excludes)
        {
            filter.addPackage(pkg);
        }

        jdepend = new JDepend(filter);

        jdepend.addDirectory(classesDir);
    }

    /**
     * Tests the distance of a single package to a distance
     * from the main sequence (D) within a tolerance.
     */
    public void testOnePackageDistance()
    {

        double ideal = 0.0;
        double tolerance = 0.8;

        jdepend.analyze();

        JavaPackage p = jdepend.getPackage("com.tiemens.secretshare.engine");

        assertEquals("Distance exceeded: " + p.getName(),
                     ideal, p.distance(), tolerance);
    }

    /**
     * Tests that a single package does not contain any
     * package dependency cycles.
     */
    public void testOnePackageHasNoCycles()
    {

        jdepend.analyze();

        JavaPackage p = jdepend.getPackage("com.tiemens.secretshare.main.cli");

        assertEquals("Cycles exist: " + p.getName(),
                     false, p.containsCycle());
    }

    @SuppressWarnings("unchecked")
    private Collection<JavaPackage> doAnalyze(JDepend jdepend) {
        return (Collection<JavaPackage>) jdepend.analyze();
    }

    @SuppressWarnings("unchecked")
    private Iterator<JavaPackage> doGetPackageIterator(JDepend jdepend) {
        return jdepend.getPackages().iterator();
    }
    /**
     * Tests the conformance of all analyzed packages to a
     * distance from the main sequence (D) within a tolerance.
     */
    public void testAllPackagesDistance()
    {

        double ideal = 0.0;
        double tolerance = 1.0;

        Collection<JavaPackage> packages = doAnalyze(jdepend);
        assertNotNull(packages);

        for (Iterator<JavaPackage> iter = packages.iterator(); iter.hasNext();)
        {
            JavaPackage p = (JavaPackage) iter.next();
            assertEquals("Distance exceeded: " + p.getName(),
                         ideal, p.distance(), tolerance);
        }
    }

    /**
     * Tests that a package dependency cycle does not exist
     * for any of the analyzed packages.
     */
    public void testAllPackagesHaveNoCycles()
    {

        Collection<JavaPackage> packages = doAnalyze(jdepend);
        assertNotNull(packages);

        if (jdepend.containsCycles())
        {
            for (Iterator<JavaPackage> i = doGetPackageIterator(jdepend); i.hasNext();)
            {
                JavaPackage jPackage = (JavaPackage) i.next();
                if (jPackage.containsCycle())
                {
                    System.out.println("Cycle at " + jPackage.getName());
                }
            }
        }

        assertEquals("Cycles exist", false, jdepend.containsCycles());
    }

    /**
     * Tests that a package dependency constraint is matched
     * for the analyzed packages.
     * <p>
     * Fails if any package dependency other than those declared
     * in the dependency constraints are detected.
     */
    public void testDependencyConstraint()
    {
        DependencyConstraint constraint = createDependencyConstraint();

        jdepend.analyze();

        System.out.println(dumpAsDotString(jdepend, constraint));

        if (debugDump)
        {
            System.out.println("J Packages = " + jdepend.getPackages().size());
            for (Iterator<JavaPackage> i = doGetPackageIterator(jdepend); i.hasNext();)
            {
                JavaPackage jPackage = i.next();
                System.out.println("J.pkg=" + jPackage.getName());
            }
            System.out.println("C Packages = " + constraint.getPackages().size());
            for (Iterator<JavaPackage> i = constraint.getPackages().iterator(); i.hasNext();)
            {
                JavaPackage jPackage = i.next();
                System.out.println("C.pkg=" + jPackage.getName());
            }
        }

        assertEquals("Constraint mismatch",
                true, jdepend.dependencyMatch(constraint));
    }

    private String dumpAsDotString(JDepend jdepend, DependencyConstraint constraint)
    {
        StringBuilder sb = new StringBuilder();
        final String quote = "\"";
        final String nl = "\n";

        sb.append("## DOT/Graphviz format.  See http://www.webgraphviz.com/" + nl);
        sb.append("digraph jdepend {" + nl);
        for (JavaPackage pkg : new java.util.ArrayList<JavaPackage>(jdepend.getPackages()))
        {
            for (JavaPackage to : new java.util.ArrayList<JavaPackage>(pkg.getEfferents()))
            {
                sb.append(quote + pkg.getName() + quote + " -> " + quote + to.getName() + quote + nl);
            }
        }

        sb.append("}" + nl);
        return sb.toString();
    }

    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(JDependTest.class);
    }
}
