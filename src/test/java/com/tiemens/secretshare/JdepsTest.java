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

import com.tiemens.secretshare.jdeps.model.DependencyConstraint;
import com.tiemens.secretshare.jdeps.model.JDeps;
import com.tiemens.secretshare.jdeps.model.JavaPackage;

import junit.framework.TestCase;

/**
 * The <code>JdepsTest</code> is an example <code>TestCase</code>
 * that demonstrates how to call the jdeps command line tool.
 * Some of the tests are:
 * <li>package dependency constraints,
 * <li>existence of cyclic package dependencies,
 *
 * The parts to configure are at the top of the .java file.
 *
 * Prints a DOT digraph of the dependency graph.
 * See http://www.webgraphviz.com/ to create the SVG of the digraph.
 * See https://svgtopng.com/ to convert the SVG to PNG.
 */

public class JdepsTest extends TestCase
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
        JavaPackage ssMd5        = constraint.addPackage("com.tiemens.secretshare.md5sum");
        //JavaPackage ssMath         = constraint.addPackage("com.tiemens.secretshare.math");
        JavaPackage ssMathMatrix   = constraint.addPackage("com.tiemens.secretshare.math.matrix");
        JavaPackage ssMathCombo    = constraint.addPackage("com.tiemens.secretshare.math.combination");
        JavaPackage ssMathEquation = constraint.addPackage("com.tiemens.secretshare.math.equation");
        JavaPackage ssMathType     = constraint.addPackage("com.tiemens.secretshare.math.type");
        JavaPackage ssMainTest   = constraint.addPackage("com.tiemens.secretshare.main.test");
        JavaPackage ssMainCli    = constraint.addPackage("com.tiemens.secretshare.main.cli");

        ssEngine.dependsUpon(ssException);
        ssEngine.dependsUpon(ssMathCombo);
        ssEngine.dependsUpon(ssMathEquation);
        ssEngine.dependsUpon(ssMathMatrix);
        ssEngine.dependsUpon(ssMathType);

        ssMainCli.dependsUpon(ssTop);
        ssMainCli.dependsUpon(ssEngine);
        ssMainCli.dependsUpon(ssException);
        ssMainCli.dependsUpon(ssMathType);

        //ssMainTest.dependsUpon(ssEngine);

        ssMathCombo.dependsUpon(ssException);

        ssMathEquation.dependsUpon(ssException);

        ssMathMatrix.dependsUpon(ssException);
        ssMathMatrix.dependsUpon(ssMathType);

        ssMathType.dependsUpon(ssException);
        ssMathType.dependsUpon(ssMd5);

        ssMd5.dependsUpon(ssException);

        return constraint;
    }

    // if testDependencyConstraint fails, then set this to true tosee the mis-match.
    //  Or, run the utility with:
    //  $ jdeps build/classes/java/main | grep -v ' java.'
    private static boolean debugDump = false;


    //
    // ===================================
    //
    private JDeps jdeps;


    public JdepsTest(String name)
    {
        super(name);
    }

    @Override
    protected void setUp() throws IOException
    {
        jdeps = JDeps.createFromCommandLine(classesDir);

        jdeps.addExcludes(excludes);

        jdeps.analyze();
    }



    /**
     * Tests that a single package does not contain any
     * package dependency cycles.
     */
    public void testOnePackageHasNoCycles()
    {

        JavaPackage p = jdeps.getPackage("com.tiemens.secretshare.engine");

        assertEquals("Cycles exist: " + p.getName(),
                     false, p.containsCycle());
    }

    /**
     * Tests that a package dependency cycle does not exist
     * for any of the analyzed packages.
     */
    public void testAllPackagesHaveNoCycles()
    {
        Collection<JavaPackage> packages = jdeps.getPackages();
        assertNotNull(packages);

        if (jdeps.containsCycles())
        {
            for (JavaPackage jPackage : jdeps.getPackages())
            {
                if (jPackage.containsCycle())
                {
                    System.out.println("Cycle at " + jPackage.getName());
                }
            }
        }

        assertEquals("Cycles exist", false, jdeps.containsCycles());
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

        System.out.println(dumpAsDotString(jdeps, constraint));

        if (debugDump)
        {
            System.out.println("J Packages = " + jdeps.getPackages().size());
            for (JavaPackage jPackage : jdeps.getPackages())
            {
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
                true, jdeps.dependencyMatch(constraint));
    }

    private String dumpAsDotString(JDeps jdeps, DependencyConstraint constraint)
    {
        StringBuilder sb = new StringBuilder();
        final String quote = "\"";
        final String nl = "\n";

        sb.append("## DOT/Graphviz format.  See http://www.webgraphviz.com/" + nl);
        sb.append("digraph jdepend {" + nl);
        for (JavaPackage pkg : new java.util.ArrayList<JavaPackage>(jdeps.getPackages()))
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
        junit.textui.TestRunner.run(JdepsTest.class);
    }
}
