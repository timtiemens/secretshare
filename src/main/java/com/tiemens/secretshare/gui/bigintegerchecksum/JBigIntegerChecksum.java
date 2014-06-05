/*******************************************************************************
 * Copyright (c) 2009, 2014 Tim Tiemens.
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
 *******************************************************************************/
package com.tiemens.secretshare.gui.bigintegerchecksum;

import javax.swing.JComponent;
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
    public static final String UI_CLASS_ID = "BigIntegerChecksumUI";

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
     * Returns the UI object which implements the LnF for this component.
     *
     * @return UI object which implements the LnF for this component.
     * @see #setUI
     */
    public BigIntegerChecksumUI getUI()
    {
        return (BigIntegerChecksumUI) ui;
    }

    /**
     * Returns the name of the UI class that implements the LnF for this
     * component.
     *
     * @return The name of the UI class that implements the LnF for this
     *         component.
     * @see JComponent#getUIClassID
     * @see javax.swing.UIDefaults#getUI
     */
    @Override
    public String getUIClassID()
    {
        return UI_CLASS_ID;
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
