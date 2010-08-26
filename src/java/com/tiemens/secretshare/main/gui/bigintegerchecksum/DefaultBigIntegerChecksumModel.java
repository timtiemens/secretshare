package com.tiemens.secretshare.main.gui.bigintegerchecksum;

import java.math.BigInteger;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.EventListenerList;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.GapContent;
import javax.swing.text.PlainDocument;
import javax.swing.text.AbstractDocument.Content;

import com.tiemens.secretshare.math.BigIntStringChecksum;
import com.tiemens.secretshare.math.BigIntUtilities;

public class DefaultBigIntegerChecksumModel
        implements
            BigIntegerChecksumModel
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
    /** The listeners waiting for model changes. */
    protected EventListenerList listenerList = new EventListenerList();

    private Document textFieldDocument;
    
    private BigIntegerChecksumModel.Value value = null;

    // Does this model/GUI allow for "free for all" strings?
    private final boolean allowChoiceHumanString;
    
    // ==================================================
    // factories
    // ==================================================

    // ==================================================
    // constructors
    // ==================================================
    
    public DefaultBigIntegerChecksumModel()
    {
        this(true);
    }
    public DefaultBigIntegerChecksumModel(boolean inAllowHumanString)
    {
        Content c = new GapContent(120);
        textFieldDocument = new PlainDocument(c);
        textFieldDocument.addDocumentListener(new MyTextFieldDocumentListener());
        allowChoiceHumanString = inAllowHumanString;
    }
    
    // ==================================================
    // public methods
    // ==================================================

    public boolean isAllowChoiceHumanString()
    {
        return allowChoiceHumanString;
    }
    

    protected boolean isValueLegal(BigIntegerChecksumModel.Value value) 
    {
        return true;
    }


    /**
     * {@inheritDoc}
     * 
     */
    public BigIntegerChecksumModel.Value getValue() 
    {
        return this.value;
    }

    
    public void setValueToInvalid()
    {
        setValue( (BigIntegerChecksumModel.Value) null );
    }
    
    /**
     * {@inheritDoc}
     * 
     */
    public void setValue(BigIntegerChecksumModel.Value inValue) 
        throws IllegalArgumentException 
    {
        if (inValue == null) 
        {
            if (this.value == null)
            {
                // no op
            }
            else
            {
                this.value = null;
                this.fireStateChanged();
            }
        }
        else
        {
            if (!inValue.equals(this.value)) 
            {
                if (!this.isValueLegal(inValue))
                {
                    throw new IllegalArgumentException("Value is not legal for the model");
                }
            
                System.out.println("Setvalue changed, firestate on value=" + value);
                
                // reminder: we don't copy because value is immutable  
                this.value = inValue;
                this.fireStateChanged();
            }
            else
            {
                System.out.println("Setvalue no op because equals");
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void setValue(BigInteger inBigInteger)
    {
        setValue(new Value.ValueAsBigInteger(inBigInteger));
    }
    

    @Override
    public void setValueAsBigIntCsString(String s)
    {
        // "check" it
        /* BigIntStringChecksum bigintcs = */ BigIntUtilities.Checksum.createBiscs(s);
        // set it
        setTextValue(s);
    }

    @Override
    public void setValueAsBigIntegerString(String s)
    {
        // check
        /* BigInteger v = */ new BigInteger(s);
        // set
        setTextValue(s);
    }

    @Override
    public void setValueAsHexString(String s)
    {
        // check
        /* BigInteger v = */ BigIntUtilities.Hex.createBigInteger(s);
        // set
        setTextValue(s);
    }
    
    @Override
    public void setValueAsHumanString(String s)
    {
        // check
        /* BigInteger v = */ BigIntUtilities.Human.createBigInteger(s);
        // set
        setTextValue(s);
    }

    
    private void setTextValue(String s)
    {
        Document doc = getTextFieldDocument();
        try
        {
            if (doc instanceof AbstractDocument) 
            {
                ((AbstractDocument)doc).replace(0, doc.getLength(), s, null);
            }
            else 
            {
                doc.remove(0, doc.getLength());
                doc.insertString(0, s, null);
            }
        }
        catch (BadLocationException e)
        {
            System.out.println("ERROR: " + e);
        }
        
    }
    /**
     * {@inheritDoc}
     * 
     */
    public void addChangeListener(ChangeListener l) 
    {
        listenerList.add(ChangeListener.class, l);
    }

    /**
     * {@inheritDoc}
     * 
     */
    public void removeChangeListener(ChangeListener l) 
    {
        listenerList.remove(ChangeListener.class, l);
    }

    /**
     * Runs each <code>ChangeListener</code>'s <code>stateChanged</code>
     * method.
     */
    protected void fireStateChanged() 
    {
        ChangeEvent event = new ChangeEvent(this);
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) 
        {
            if (listeners[i] == ChangeListener.class) 
            {
                ((ChangeListener) listeners[i + 1]).stateChanged(event);
            }
        }
    }

    /**
     * Returns an array of all the change listeners registered on this
     * <code>DefaultBoundedRangeModel</code>.
     * 
     * @return all of this model's <code>ChangeListener</code>s or an empty
     *         array if no change listeners are currently registered
     * 
     * @see #addChangeListener
     * @see #removeChangeListener
     */
    public ChangeListener[] getChangeListeners() 
    {
        return (ChangeListener[]) listenerList
                .getListeners(ChangeListener.class);
    }

    @Override
    public Document getTextFieldDocument()
    {
        return textFieldDocument;
    }


    @Override
    public boolean isAllowHumanString()
    {
        return allowChoiceHumanString;
    }


    // ==================================================
    // non public methods
    // ==================================================
    
    // inner class:
    public class MyTextFieldDocumentListener
        implements DocumentListener
    {
        @Override
        public void changedUpdate(DocumentEvent e)
        {
            basicChange(e);
        }

        @Override
        public void insertUpdate(DocumentEvent e)
        {
            basicChange(e);
        }

        @Override
        public void removeUpdate(DocumentEvent e)
        {
            basicChange(e);
        }
        private void basicChange(DocumentEvent e)
        {
            try
            {
                String s = e.getDocument().getText(0, e.getDocument().getLength());
                BigIntegerChecksumModel.Value v = 
                    BigIntegerChecksumModel.Value.create(s, isAllowHumanString());
                setValue(v);
            }
            catch (BadLocationException e1)
            {
                setValueToInvalid();
            }
            
            fireStateChanged();
        }
        
    }

}