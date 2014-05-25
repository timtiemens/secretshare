/*******************************************************************************
 * $Id: $
 * Copyright (c) 2009-2010 Tim Tiemens.
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
package com.tiemens.secretshare.main.cli;

/**
 * "Dispatch" main program.
 * Based on the 1st argument, calls one of:
 *   split
 *   combine
 *
 * @author tiemens
 *
 */
public class Main
{
    /**
     * @return version as a string
     */
    public static String getVersionString()
    {
        return "1.2.2-SNAPSHOT";
    }



    /**
     * @param args
     */
    public static void main(String[] args)
    {
        if (args.length < 1)
        {
            System.out.println("Error: must supply at least 1 argument");
            usage();
            System.exit(1);
        }
        else
        {
            String cmd = args[0];
            args[0] = null;
            if ("split".equalsIgnoreCase(cmd))
            {
                MainSplit.main(args);
            }
            else if ("combine".equalsIgnoreCase(cmd))
            {
                MainCombine.main(args);
            }
            else
            {
                System.out.println("Error: could not understand argument '" + cmd + "' - it must be " +
                                   "either 'split' or 'combine'");
                usage();
                System.exit(1);
            }
        }

    }


    private static void usage()
    {
        System.out.println("Usage:  java -jar secretshare.jar <split>|<combine>");
    }

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

    // ==================================================
    // public methods
    // ==================================================

    // ==================================================
    // non public methods
    // ==================================================
}
