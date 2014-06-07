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

import java.io.InputStream;
import java.io.PrintStream;

import com.tiemens.secretshare.BuildVersion;

/**
 * "Dispatch" main program.
 * Based on the first argument in args[], this calls one of:
 *   split
 *   combine
 *
 * @author tiemens
 *
 */
public final class Main
{
    /**
     * @return version as a string
     */
    public static String getVersionString()
    {
        return BuildVersion.UI_VERSION;
    }



    /**
     * @param args from command line
     */
    public static void main(String[] args)
    {
        main(args, System.in, System.out, true);
    }

    public static void main(String[] args,
                            InputStream in,
                            PrintStream out,
                            boolean callExit)
    {
        if (args.length < 1)
        {
            out.println("Error: must supply at least 1 argument");
            usage(out);
            if (callExit)
            {
                System.exit(1);
            }
            else
            {
                return;
            }
        }
        else
        {
            String cmd = args[0];
            args[0] = null;
            if ("split".equalsIgnoreCase(cmd))
            {
                MainSplit.main(args, in, out);
            }
            else if ("combine".equalsIgnoreCase(cmd))
            {
                MainCombine.main(args, in, out);
            }
            else if ("info".equalsIgnoreCase(cmd))
            {
                MainInfo.main(args, in, out);
            }
            else if ("bigintcs".equalsIgnoreCase(cmd))
            {
                MainBigIntCs.main(args, in, out);
            }
            else
            {
                out.println("Error: could not understand argument '" + cmd + "' - it must be " +
                                   "either 'split', 'combine', 'info' or 'bigintcs'");
                usage(out);
                if (callExit)
                {
                    System.exit(1);
                }
                else
                {
                    return;
                }
            }
        }

    }


    private static void usage(PrintStream out)
    {
        out.println("Usage:  java -jar secretshare.jar <split>|<combine>");
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

    private Main()
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
