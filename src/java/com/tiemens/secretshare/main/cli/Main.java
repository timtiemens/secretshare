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
        return "1.0";
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
                System.out.println("Error: could not understand argument '" + cmd + "'");
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