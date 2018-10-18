package com.tiemens.secretshare;

import java.io.File;
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
 * The <code>ExampleTest</code> is an example <code>TestCase</code> 
 * that demonstrates tests for measuring the distance from the 
 * main sequence (D), package dependency constraints, and the 
 * existence of cyclic package dependencies.
 * <p>
 * This test analyzes the JDepend class files.
 * 
 * @author <b>Mike Clark</b>
 * @author Clarkware Consulting, Inc.
 */

public class JDependTest extends TestCase {

	private static List<String> excludes = Arrays.asList(
			// 
			"java.*", "javax.*",
			//
	        "org.junit",
	        "org.junit.*",
	        "junit.framework",
	        "junit.textui",
	        "jdepend.framework",	        
	        //
		    "org.jacoco.*",
	        "com.vladium.emma.rt"
			);
	
    private JDepend jdepend;

    
    public JDependTest(String name) {
        super(name);
    }

    protected void setUp() throws IOException {
    	String jdependHomeDirectory;

        jdependHomeDirectory = System.getProperty("jdepend.home");
        if (jdependHomeDirectory == null) {
            //fail("Property 'jdepend.home' not defined");
            jdependHomeDirectory = ".";
        }

        PackageFilter filter = new PackageFilter();
        for (String pkg : excludes) {
        	filter.addPackage(pkg);
        }

        jdepend = new JDepend(filter);

        String classesDir = jdependHomeDirectory + File.separator + "build";

        jdepend.addDirectory(classesDir);
    }

    /**
     * Tests the distance of a single package to a distance 
     * from the main sequence (D) within a tolerance.
     */
    public void testOnePackageDistance() {

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
    public void testOnePackageHasNoCycles() {

        jdepend.analyze();

        JavaPackage p = jdepend.getPackage("com.tiemens.secretshare.engine");

        assertEquals("Cycles exist: " + p.getName(), 
                     false, p.containsCycle());
    }

    /**
     * Tests the conformance of all analyzed packages to a 
     * distance from the main sequence (D) within a tolerance.
     */
    public void testAllPackagesDistance() {

        double ideal = 0.0;
        double tolerance = 1.0;

        Collection packages = jdepend.analyze();

        for (Iterator iter = packages.iterator(); iter.hasNext();) {
            JavaPackage p = (JavaPackage)iter.next();
            assertEquals("Distance exceeded: " + p.getName(), 
                         ideal, p.distance(), tolerance);
        }
    }

    /**
     * Tests that a package dependency cycle does not exist 
     * for any of the analyzed packages.
     */
    public void testAllPackagesHaveNoCycles() {

        Collection packages = jdepend.analyze();

        if (jdepend.containsCycles()) {
        	   for (Iterator i = jdepend.getPackages().iterator(); i.hasNext();) {
                   JavaPackage jPackage = (JavaPackage)i.next();
                   if (jPackage.containsCycle()) {
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
    public void testDependencyConstraint() {
    	DependencyConstraint constraint = new DependencyConstraint();
    	
    	JavaPackage ssTop       = constraint.addPackage("com.tiemens.secretshare");
    	JavaPackage ssEngine    = constraint.addPackage("com.tiemens.secretshare.engine");
    	JavaPackage ssException = constraint.addPackage("com.tiemens.secretshare.exceptions");
    	//JavaPackage ssMain      = constraint.addPackage("com.tiemens.secretshare.main");
    	JavaPackage ssMath      = constraint.addPackage("com.tiemens.secretshare.math");
    	JavaPackage ssMd5       = constraint.addPackage("com.tiemens.secretshare.md5sum");
    	JavaPackage ssMathMatrix= constraint.addPackage("com.tiemens.secretshare.math.matrix");    	
    	JavaPackage ssMainTest  = constraint.addPackage("com.tiemens.secretshare.main.test");    	
    	JavaPackage ssMainCli   = constraint.addPackage("com.tiemens.secretshare.main.cli");    	    	

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
    	
    	//ssTop.dependsUpon(ssEngine);
    	
    	ssMd5.dependsUpon(ssException);
    	
    	
    	jdepend.analyze();
    	
    	System.out.println(dumpAsDotString(jdepend, constraint));
    	
    	boolean debugDump = false;
    	if (debugDump) {
    		System.out.println("Packages = " + jdepend.getPackages().size());
    		for (Iterator i = jdepend.getPackages().iterator(); i.hasNext();) {
    			JavaPackage jPackage = (JavaPackage)i.next();
    			System.out.println("J.pkg=" + jPackage.getName());
    		}
    		for (Iterator i = constraint.getPackages().iterator(); i.hasNext();) {
    			JavaPackage jPackage = (JavaPackage)i.next();
    			System.out.println("C.pkg=" + jPackage.getName());
    		}
    	}
    	

        assertEquals("Constraint mismatch", 
                     true, jdepend.dependencyMatch(constraint));    	
    }
    
    private String dumpAsDotString(JDepend jdepend, DependencyConstraint constraint) {
    	StringBuilder sb = new StringBuilder();
    	final String quote = "\"";
    	final String nl = "\n";
    	sb.append("digraph jdepend {" + nl);
    	for (JavaPackage pkg : new java.util.ArrayList<JavaPackage>(jdepend.getPackages())) {
    		for (JavaPackage to : new java.util.ArrayList<JavaPackage>(pkg.getEfferents())) {
    			sb.append(quote + pkg.getName() + quote + " -> " + quote + to.getName() + quote + nl);
    		}
    	}

    	sb.append("}" + nl);
    	return sb.toString();
		
	}

	/*
    public void testDependencyConstrain2t() {

        DependencyConstraint constraint = new DependencyConstraint();

        JavaPackage junitframework = constraint.addPackage("junit.framework");
        JavaPackage junitui = constraint.addPackage("junit.textui");
        JavaPackage framework = constraint.addPackage("jdepend.framework");
        JavaPackage text = constraint.addPackage("jdepend.textui");
        JavaPackage xml = constraint.addPackage("jdepend.xmlui");
        JavaPackage swing = constraint.addPackage("jdepend.swingui");
        JavaPackage orgjunitrunners = constraint.addPackage("orgjunitrunners");
        JavaPackage jdependframeworkp2 = constraint.addPackage("jdependframeworkp2");
        JavaPackage jdependframeworkp3 = constraint.addPackage("jdependframeworkp3");
        JavaPackage jdependframeworkp1 = constraint.addPackage("jdependframeworkp1");
        JavaPackage orgjunit = constraint.addPackage("orgjunit");

        framework.dependsUpon(junitframework);
        framework.dependsUpon(junitui);
        text.dependsUpon(framework);
        xml.dependsUpon(framework);
        xml.dependsUpon(text);
        swing.dependsUpon(framework);
        framework.dependsUpon(jdependframeworkp2);
        framework.dependsUpon(jdependframeworkp3);
        framework.dependsUpon(jdependframeworkp1);
        framework.dependsUpon(orgjunitrunners);
        framework.dependsUpon(orgjunit);

        jdepend.analyze();

        assertEquals("Constraint mismatch", 
                     true, jdepend.dependencyMatch(constraint));
    }
  */
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(JDependTest.class);
    }
}