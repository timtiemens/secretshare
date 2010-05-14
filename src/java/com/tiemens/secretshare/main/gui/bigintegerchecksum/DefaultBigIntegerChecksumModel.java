package com.tiemens.secretshare.main.gui.bigintegerchecksum;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

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

    private BigIntegerChecksumModel.Value value = null;

    // ==================================================
    // factories
    // ==================================================

    // ==================================================
    // constructors
    // ==================================================

    // ==================================================
    // public methods
    // ==================================================
   
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

    /**
     * {@inheritDoc}
     * 
     */
    public void setValue(BigIntegerChecksumModel.Value value) 
        throws IllegalArgumentException 
    {
        if (value == null) 
        {
            throw new IllegalArgumentException("Can't set null value");
        }
        
        if (!value.equals(this.value)) 
        {
            if (!this.isValueLegal(value))
            {
                throw new IllegalArgumentException("Value is not legal for the model");
            }
            
            // reminder: we don't copy IFF value is immutable  
            this.value = value;
            this.fireStateChanged();
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

    // ==================================================
    // non public methods
    // ==================================================
}