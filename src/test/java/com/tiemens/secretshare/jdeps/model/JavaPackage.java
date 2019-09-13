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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class JavaPackage
{

    private String name;
    private String moduleName;
    private int volatility;
    //private HashSet classes;
    private List<JavaPackage> usedBy;       // afferents
    private List<JavaPackage> dependsUpon;  // efferents


    public JavaPackage(String name)
    {
        this(name, null, 1);
    }

    public JavaPackage(final String name, String moduleName, int volatility)
    {
        if (name == null)
        {
            throw new RuntimeException("Name cannot be null");
        }
        this.name = name;
        this.moduleName = moduleName;
        setVolatility(volatility);
        //classes = new HashSet();
        usedBy = new ArrayList<>();
        dependsUpon = new ArrayList<>();
    }

    public String getName()
    {
        return name;
    }

    public String getModuleName()
    {
        return moduleName;
    }

    /**
     * @return The package's volatility (0-1).
     */
    public int getVolatility()
    {
        return volatility;
    }

    /**
     * @param v Volatility (0-1).
     */
    public void setVolatility(int v)
    {
        volatility = v;
    }

    public boolean containsCycle()
    {
        return collectCycle(new ArrayList<>());
    }

    /**
     * Collects the packages participating in the first package dependency cycle
     * detected which originates from this package.
     *
     * @param list Collecting object to be populated with the list of
     *            JavaPackage instances in a cycle.
     * @return <code>true</code> if a cycle exist; <code>false</code>
     *         otherwise.
     */
    public boolean collectCycle(List<JavaPackage> list)
    {
        if (list.contains(this))
        {
            list.add(this);
            return true;
        }

        list.add(this);

        for (JavaPackage efferent : getDependsUpon())
        {
            if (efferent.collectCycle(list))
            {
                return true;
            }
        }

        list.remove(this);

        return false;
    }

    /**
     * Collects all the packages participating in a package dependency cycle
     * which originates from this package.
     * <p>
     * This is a more exhaustive search than that employed by
     * <code>collectCycle</code>.
     *
     * @param list Collecting object to be populated with the list of
     *            JavaPackage instances in a cycle.
     * @return <code>true</code> if a cycle exist; <code>false</code>
     *         otherwise.
     */
    public boolean collectAllCycles(List<JavaPackage> list)
    {

        if (list.contains(this))
        {
            list.add(this);
            return true;
        }

        list.add(this);

        boolean containsCycle = false;
        for (JavaPackage efferent : getDependsUpon())
        {
            if (efferent.collectAllCycles(list))
            {
                containsCycle = true;
            }
        }

        if (containsCycle)
        {
            return true;
        }

        list.remove(this);
        return false;
    }

    //public void addClass(JavaClass clazz) {
    //    classes.add(clazz);
    //}

    //public Collection getClasses() {
    //    return classes;
    //}

    //public int getClassCount() {
    //    return classes.size();
    //}

    //public int getAbstractClassCount() {
    //    int count = 0;

        //for (Iterator i = classes.iterator(); i.hasNext();) {
          //  JavaClass clazz = (JavaClass)i.next();
            //if (clazz.isAbstract()) {
              //  count++;
           // }
       // }

       // return count;
    //}

    //public int getConcreteClassCount() {
        //int count = 0;

        //for (Iterator i = classes.iterator(); i.hasNext();) {
          //  JavaClass clazz = (JavaClass)i.next();
            //if (!clazz.isAbstract()) {
              //  count++;
            //}
       // }

        //return count;
    //}

    /**
     * Adds the specified Java package to the dependsUpon list of this package
     *    and adds this package to the usedBy list of the specified Java package.
     *
     * @param jPackage Java package.
     */
    public void dependsUpon(JavaPackage jPackage)
    {
        addToDependsOn(jPackage);
        jPackage.addToUsedBy(this);
    }

    /**
     * Adds the specified Java package to the usedBy list.
     *
     * @param jPackage Java package.
     */
    private void addToUsedBy(JavaPackage jPackage)
    {
        if (!jPackage.getName().equals(getName()))
        {
            if (!usedBy.contains(jPackage))
            {
                usedBy.add(jPackage);
            }
        }
    }

    public Collection<JavaPackage> getUsedBy()
    {
        return usedBy;
    }

    // efferent
    private void addToDependsOn(JavaPackage jPackage)
    {
        if (!jPackage.getName().equals(getName()))
        {
            if (!dependsUpon.contains(jPackage))
            {
                dependsUpon.add(jPackage);
            }
        }
    }

    public Collection<JavaPackage> getDependsUpon()
    {
        return dependsUpon;
    }

    public Collection<JavaPackage> getAfferents()
    {
        return usedBy;       // afferents
    }

    public Collection<JavaPackage> getEfferents()
    {
        return dependsUpon;  // efferents
    }

    /**
     * @return The afferent coupling (Ca) of this package.
     */
    public int afferentCoupling()
    {
        return usedBy.size();
    }

    /**
     * @return The efferent coupling (Ce) of this package.
     */
    public int efferentCoupling()
    {
        return dependsUpon.size();
    }

    /**
     * @return Instability (0-1).
     */
    public float instability()
    {
        float totalCoupling = (float) efferentCoupling() +
                (float) afferentCoupling();

        if (totalCoupling > 0)
        {
            return efferentCoupling() / totalCoupling;
        }

        return 0;
    }

    /**
     * @return The package's abstractness (0-1).
     */
    //public float abstractness() {

      //  if (getClassCount() > 0) {
        //    return (float) getAbstractClassCount() / (float) getClassCount();
       // }

        //return 0;
    //}

    /**
     * @return The package's distance from the main sequence (D).
     */
    //public float distance() {
      //  float d = Math.abs(abstractness() + instability() - 1);
        //return d * volatility;
    //}

    @Override
    public boolean equals(Object other)
    {
        if (other instanceof JavaPackage)
        {
            JavaPackage otherPackage = (JavaPackage) other;
            return otherPackage.getName().equals(getName());
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return getName().hashCode();
    }

    @Override
    public String toString()
    {
        return name;
    }
}
