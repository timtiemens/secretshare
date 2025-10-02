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
package com.tiemens.secretshare.md5sum;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.tiemens.secretshare.exceptions.SecretShareException;

public final class Md5ChecksummerFactory
{

    // ==================================================
    // class static data
    // ==================================================

    private static final String KEY = "ssmd5class";

    // ==================================================
    // class static methods
    // ==================================================

    // ==================================================
    // instance data
    // ==================================================

    // ==================================================
    // factories
    // ==================================================
    public static Md5Checksummer create()
    {
        String cname = System.getProperty(KEY);
        if (cname != null)
        {
            return createFromClassName(cname);
        }
        else
        {
            try
            {
                return new Md5ChecksummerImpl();
            }
            catch (SecretShareException e)
            {
                throw new SecretShareException("Failed to create built-in MD5 digest.  " +
                                               "Use -D" + KEY + "=a.b.c.YourMd5Checksummer" +
                                               " where your class must implement the interface " +
                                               Md5Checksummer.class.getName(), e);
            }

        }
    }

    /**
     * @param cname the name of the class that implements Md5Checksummer interface
     * @return instance
     * @throws SecretShareException on error
     */
    public static Md5Checksummer createFromClassName(String cname)
    {
        final String msg = "create md5, name='" + cname + "' ";
        try
        {
            Class<?> c = Class.forName(cname);
            if (Md5Checksummer.class.isAssignableFrom(c))
            {
                // Get a specific constructor
                Constructor<?> constructor = c.getDeclaredConstructor();
                return (Md5Checksummer) constructor.newInstance();
            }
            else
            {
                throw new SecretShareException(msg + " does not implement interface " +
                                               Md5Checksummer.class.getName());
            }
        }
        catch (InstantiationException e)
        {
            throw new SecretShareException(msg + "instantiation", e);
        }
        catch (IllegalAccessException e)
        {
            throw new SecretShareException(msg + "access", e);
        }
        catch (ClassNotFoundException e)
        {
            throw new SecretShareException(msg + "class not found", e);
        }
        catch (NoSuchMethodException e)
        {
            throw new SecretShareException(msg + " no such method", e);
        }
        catch (SecurityException e)
        {
            throw new SecretShareException(msg + " security", e);
        }
        catch (IllegalArgumentException e)
        {
            throw new SecretShareException(msg + " illegal argument", e);
        }
        catch (InvocationTargetException e)
        {
            throw new SecretShareException(msg + " invocation target", e);
        }
    }

    // ==================================================
    // constructors
    // ==================================================

    private Md5ChecksummerFactory()
    {
        // no instances
    }

    // ==================================================
    // public methods
    // ==================================================

    // ==================================================
    // non public methods
    // ==================================================
}
