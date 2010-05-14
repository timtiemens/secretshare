package com.tiemens.secretshare.main.gui.bigintegerchecksum;

import java.math.BigInteger;

import javax.swing.event.ChangeListener;

import org.pushingpixels.flamingo.slider.FlexiRangeModel.Range;
import org.pushingpixels.flamingo.slider.FlexiRangeModel.Value;

public interface BigIntegerChecksumModel
{

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
    
    public Value getValue();

    public void setValue(Value value);
    
    
    /**
     * Adds a ChangeListener to the model's listener list.
     * 
     * @param x  the ChangeListener to add
     * @see #removeChangeListener
     */
    public void addChangeListener(ChangeListener x);

    /**
     * Removes a ChangeListener from the model's listener list.
     * 
     * @param x  the ChangeListener to remove
     * @see #addChangeListener
     */
    public void removeChangeListener(ChangeListener x);
    
    
    // ==================================================
    // non public methods
    // ==================================================
    
    public static class Value 
    {
        private BigInteger biginteger;

        public Value(BigInteger bigint)
        {
            this.biginteger = bigint;
        }

        public Value(Value value) 
        {
            this(value.biginteger);
        }

        @Override
        public boolean equals(Object obj) 
        {
            if (obj instanceof Value) 
            {
                Value value2 = (Value) obj;
                if (this.biginteger != null)
                {
                    return this.biginteger.equals(value2.biginteger);
                }
                else
                {
                    return value2.biginteger == null;
                }
            }
            return false;
        }

        @Override
        public String toString() 
        {
            return "" + this.biginteger;
        }
    }    
}