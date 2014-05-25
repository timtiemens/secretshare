package com.tiemens.secretshare.gui.bigintegerchecksum;

import java.math.BigInteger;

import javax.swing.event.ChangeListener;
import javax.swing.text.Document;

import com.tiemens.secretshare.exceptions.SecretShareException;
import com.tiemens.secretshare.math.BigIntStringChecksum;
import com.tiemens.secretshare.math.BigIntUtilities;

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
    
    public void setValue(BigInteger value);

    public void setValueAsBigIntCsString(String s);    

    public void setValueAsBigIntegerString(String s);

    public void setValueAsHexString(String s);
    
    public void setValueAsHumanString(String s);

    public boolean isAllowHumanString();


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
    
    /**
     * Design: Immutable
     *
     */
    public static class Value 
    {
        public static Value create(String s,
                                   boolean allowHumanString)
        {
            Value ret = null;
            if (BigIntUtilities.Checksum.couldCreateFromStringMd5CheckSum(s))
            {
                System.out.println("Attempting bigintcs on '" + s + "'");
                try
                {
                    BigIntStringChecksum v = BigIntUtilities.Checksum.createBiscs(s);
                    ret = new Value.ValueAsBigIntStringChecksum(v);
                }
                catch (SecretShareException e)
                {
                    ret = null;
                }
            }
            else if (BigIntUtilities.Hex.couldCreateFromStringHex(s))
            {
                System.out.println("Attempting hex on '" + s + "'");
                try
                {
                    BigInteger v = BigIntUtilities.Hex.createBigInteger(s);
                    ret = new Value.ValueAsHexString(v);
                }
                catch (SecretShareException e)
                {
                    ret = null;
                }
            }
            else
            {
                try
                {
                    System.out.println("Attempting biginteger on '" + s + "'");
                    BigInteger v = new BigInteger(s);
                    ret = new Value.ValueAsBigInteger(v);
                }
                catch (NumberFormatException e)
                {
                    if (allowHumanString && s.length() > 0)
                    {
                        System.out.println("Attempting humanstring on '" + s + "'");
                        BigInteger v = BigIntUtilities.Human.createBigInteger(s);
                        ret = new Value.ValueAsHumanString(v);
                    }
                    else
                    {
                        ret = null;
                    }
                }
            }
            System.out.println("  Return is " + ret);
            return ret;
        }

        
        private static enum ValueSource
        {
            BIG_INTEGER,
            BIGINTCS,
            HEX,
            HUMAN_STRING;
        };
        
        private final BigInteger biginteger;
        private final ValueSource source;

        protected Value(BigInteger inBiginteger,
                        ValueSource inValueSource)
        {
            this.biginteger = inBiginteger;
            this.source = inValueSource;
        }
        
        
        @Override
        public boolean equals(Object obj) 
        {
            boolean ret = false;
            if (obj instanceof Value) 
            {
                ret = true;
                Value value2 = (Value) obj;
                if (this.biginteger != null)
                {
                    ret = ret && this.biginteger.equals(value2.biginteger);
                }
                else
                {
                    ret = ret && value2.biginteger == null;
                }
                if (this.source != null)
                {
                    ret = ret && this.source.equals(value2.source);
                }
                else
                {
                    ret = ret && value2.source == null;
                }
            }
            return ret;
        }

        @Override
        public String toString() 
        {
            return "" + this.biginteger + " [" + source + "]";
        }

        public BigInteger getBigInteger()
        {
            return biginteger;
        }
        
        public boolean isBigIntegerChecksum()
        {
            return ValueSource.BIGINTCS.equals(source);
        }

        public boolean isBigInteger()
        {
            return ValueSource.BIG_INTEGER.equals(source);
        }

        public boolean isHex()
        {
            return ValueSource.HEX.equals(source);
        }

        public boolean isHumanString()
        {
            return ValueSource.HUMAN_STRING.equals(source);
        }

        public static class ValueAsBigInteger
            extends Value
        {
            public ValueAsBigInteger(BigInteger in)
            {
                super(in, ValueSource.BIG_INTEGER);
            }
        }
        public static class ValueAsBigIntStringChecksum
            extends Value
        {
            public ValueAsBigIntStringChecksum(BigIntStringChecksum in)
            {
                super(in.asBigInteger(), ValueSource.BIGINTCS);
            }
        }
        public static class ValueAsHexString
            extends Value
        {
            public ValueAsHexString(BigInteger in)
            {
                super(in, ValueSource.HEX);
            }
        }
        public static class ValueAsHumanString
            extends Value
        {
            public ValueAsHumanString(BigInteger in)
            {
                super(in, ValueSource.HUMAN_STRING);
            }
        }
    }


    /**
     * @return Document used by the textfield
     */
    public Document getTextFieldDocument();

}