package com.tiemens.secretshare.gui.bigintegerchecksum;

import javax.swing.JComponent;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

import com.tiemens.secretshare.gui.bigintegerchecksum.ui.BasicBigIntegerChecksumUI;
import com.tiemens.secretshare.gui.bigintegerchecksum.ui.BigIntegerChecksumUI;

/**
 *
 * @author Tim Tiemens
 */
public class JBigIntegerChecksum
extends JComponent // JPanel // JComponent
{
    // ==================================================
    // class static data
    // ==================================================
    /**
     * The UI class ID string.
     */
    public static final String uiClassID = "BigIntegerChecksumUI";

    // ==================================================
    // class static methods
    // ==================================================

    // ==================================================
    // instance data
    // ==================================================
    protected BigIntegerChecksumModel model;

    //    private Icon[] controlPointIcons;

    //private String[] controlPointTexts;

    // ==================================================
    // factories
    // ==================================================

    // ==================================================
    // constructors
    // ==================================================
    public JBigIntegerChecksum()
            throws NullPointerException,
            IllegalArgumentException
            {

        this(new DefaultBigIntegerChecksumModel());
            }

    public JBigIntegerChecksum(BigIntegerChecksumModel model)
            throws NullPointerException,
            IllegalArgumentException
            {

        this.model = model;
        //    this.model.setRanges(ranges);

        //    this.controlPointIcons = new Icon[controlPointIcons.length];

        this.updateUI();
            }

    // ==================================================
    // public methods
    // ==================================================


    /**
     * Sets the new UI delegate.
     *
     * @param ui  New UI delegate.
     */
    public void setUI(BigIntegerChecksumUI ui)
    {
        super.setUI(ui);
    }

    /**
     * Resets the UI property to a value from the current look and feel.
     *
     * @see JComponent#updateUI
     */
    @Override
    public void updateUI()
    {
        BigIntegerChecksumUI newui;
        if (UIManager.get(getUIClassID()) != null)
        {
            newui = (BigIntegerChecksumUI) UIManager.getUI(this);
        }
        else
        {
            newui = new BasicBigIntegerChecksumUI();
        }
        System.out.println("JBigIntegerChecksum.newui.classname=" +
                newui.getClass().getName());
        setUI(newui);
    }

    /**
     * Returns the UI object which implements the L&F for this component.
     *
     * @return UI object which implements the L&F for this component.
     * @see #setUI
     */
    public BigIntegerChecksumUI getUI()
    {
        return (BigIntegerChecksumUI) ui;
    }

    /**
     * Returns the name of the UI class that implements the L&F for this
     * component.
     *
     * @return The name of the UI class that implements the L&F for this
     *         component.
     * @see JComponent#getUIClassID
     * @see UIDefaults#getUI
     */
    @Override
    public String getUIClassID()
    {
        return uiClassID;
    }



    public BigIntegerChecksumModel getModel()
    {
        return this.model;
    }

    public BigIntegerChecksumModel.Value getValue()
    {
        return this.model.getValue();
    }

    public void setValue(BigIntegerChecksumModel.Value value)
    {
        BigIntegerChecksumModel m = getModel();
        BigIntegerChecksumModel.Value oldValue = m.getValue();
        if (value.equals(oldValue))
        {
            return;
        }
        else
        {
            m.setValue(value);
        }
    }
}
