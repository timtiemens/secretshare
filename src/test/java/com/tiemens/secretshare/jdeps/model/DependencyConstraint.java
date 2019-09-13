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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


/**
 *
 * Wrapper for a collection of JavaPackage(s).
 * Indexed by package name.
 *
 */
public class DependencyConstraint
{

    private Map<String, JavaPackage> packages;

    public DependencyConstraint()
    {
        packages = new HashMap<>();
    }

    public JavaPackage addPackage(String packageName)
    {
        JavaPackage jPackage = packages.get(packageName);
        if (jPackage == null)
        {
            jPackage = new JavaPackage(packageName);
            addPackage(jPackage);
        }
        return jPackage;
    }

    public void addPackage(JavaPackage jPackage)
    {
        if (!packages.containsValue(jPackage))
        {
            packages.put(jPackage.getName(), jPackage);
        }
    }

    public Collection<JavaPackage> getPackages()
    {
        return packages.values();
    }

    /**
     * Indicates whether the specified packages match the
     * packages in this constraint.
     *
     * @return <code>true</code> if the packages match this constraint
     */
    public boolean match(Collection<JavaPackage> expectedPackages)
    {
        if (packages.size() == expectedPackages.size())
        {
            for (JavaPackage next : expectedPackages)
            {
                if (next instanceof JavaPackage)
                {
                    JavaPackage nextPackage = next;
                    if (!matchPackage(nextPackage))
                    {
                        return false;
                    }
                }
                else
                {
                    break;
                }

                return true;
            }
        }
        else
        {
            //System.out.println("SIZE mismatch " + packages.size() + " vs " + expectedPackages.size());
        }

        return false;
    }

    private boolean matchPackage(JavaPackage expectedPackage)
    {

        JavaPackage actualPackage = packages.get(expectedPackage.getName());

        if (actualPackage != null)
        {
            if (equalsDependencies(actualPackage, expectedPackage))
            {
                return true;
            }
        }

        return false;
    }

    private boolean equalsDependencies(JavaPackage a, JavaPackage b)
    {
        return equalsAfferents(a, b) && equalsEfferents(a, b);
    }

    private boolean equalsAfferents(JavaPackage a, JavaPackage b)
    {
        if (a.equals(b))
        {
            Collection<JavaPackage> otherAfferents = b.getAfferents();

            if (a.getAfferents().size() == otherAfferents.size())
            {
                for (JavaPackage afferent : a.getAfferents())
                {
                    if (!otherAfferents.contains(afferent))
                    {
                        return false;
                    }
                }

                return true;
            }
        }

        return false;
    }

    private boolean equalsEfferents(JavaPackage a, JavaPackage b)
    {
        if (a.equals(b))
        {
            Collection<JavaPackage> otherEfferents = b.getEfferents();

            if (a.getEfferents().size() == otherEfferents.size())
            {
                for (JavaPackage efferent : a.getEfferents())
                {
                    if (!otherEfferents.contains(efferent))
                    {
                        return false;
                    }
                }

                return true;
            }
        }

        return false;
    }
}
