package com.tiemens.secretshare.main.cli;

import java.math.BigInteger;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class MainSplitTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testNullIfLessThanZero() {
        Assert.assertNull(MainSplit.SplitInput.nullIfLessThanZero(BigInteger.valueOf(-2)));
        Assert.assertNull(MainSplit.SplitInput.nullIfLessThanZero(BigInteger.valueOf(-1)));
        Assert.assertNotNull(MainSplit.SplitInput.nullIfLessThanZero(BigInteger.valueOf(0)));
        Assert.assertNotNull(MainSplit.SplitInput.nullIfLessThanZero(BigInteger.valueOf(1)));
    }

}
