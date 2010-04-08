package com.tiemens.secretshare;

import com.tiemens.secretshare.engine.SecretShareUT;
import com.tiemens.secretshare.math.BigIntStringChecksumUT;
import com.tiemens.secretshare.math.BigIntUtilitiesUT;
import com.tiemens.secretshare.math.EasyLinearEquationUT;
import com.tiemens.secretshare.math.PolyEquationImplUT;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class SuiteUT
{
    // ==================================================
    // class static data
    // ==================================================

    // ==================================================
    // class static methods
    // ==================================================
    public static Test createSuite()
    {
        TestSuite ret =
            new TestSuite(new SuiteUT().getClass().getName());
        
        ret.addTestSuite(BigIntStringChecksumUT.class);
        ret.addTestSuite(BigIntUtilitiesUT.class);
        ret.addTestSuite(PolyEquationImplUT.class);
        ret.addTestSuite(EasyLinearEquationUT.class);
        ret.addTestSuite(SecretShareUT.class);
        
        return ret;
    }
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

    public static void main(final String[] args)
    {
        junit.textui.TestRunner.run(createSuite());
    }
    // ==================================================
    // non public methods
    // ==================================================
}