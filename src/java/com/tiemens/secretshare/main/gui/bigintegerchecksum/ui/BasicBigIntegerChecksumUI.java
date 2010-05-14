package com.tiemens.secretshare.main.gui.bigintegerchecksum.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;

import javax.swing.CellRendererPane;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ComponentUI;

import org.pushingpixels.flamingo.slider.FlexiRangeModel;
import org.pushingpixels.flamingo.slider.JFlexiSlider;
import org.pushingpixels.flamingo.slider.ui.BasicFlexiSliderUI;

import com.tiemens.secretshare.main.gui.bigintegerchecksum.BigIntegerChecksumModel;
import com.tiemens.secretshare.main.gui.bigintegerchecksum.JBigIntegerChecksum;

public class BasicBigIntegerChecksumUI
    extends BigIntegerChecksumUI
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
    /**
     * The associated JComponent.
     */
    protected JBigIntegerChecksum outer;

    
    protected JTextField jtextField;
    protected JLabel jlabel;
    
    protected MouseListener mouseListener;

    protected MouseMotionListener mouseMotionListener;

    protected ChangeListener changeListener;

    // ==================================================
    // factories
    // ==================================================

    /**
     * {@inheritDoc}
     * TODO: why arg 'c'? 
     */
    public static ComponentUI createUI(JComponent c) 
    {
        return new BasicBigIntegerChecksumUI();
    }

    // ==================================================
    // constructors
    // ==================================================

    // ==================================================
    // public methods
    // ==================================================

    // ==================================================
    // non public methods
    // ==================================================

    /**
     * {@inheritDoc}
     * 
     */
    @Override
    public void installUI(JComponent c) 
    {
        System.out.println("installUI");
        this.outer = (JBigIntegerChecksum) c;
        c.setLayout(createLayoutManager());
        c.setBorder(new EmptyBorder(1, 1, 1, 1));
        
//        installDefaults();
        installComponents();
        installListeners();

        
        
    }

    /**
     * {@inheritDoc}
     * 
     */
    @Override
    public void uninstallUI(JComponent c) 
    {
        c.setLayout(null);
        uninstallListeners();
        uninstallComponents();
        uninstallDefaults();

        this.outer = null;
    }

    /**
     * {@inheritDoc} 
     */
//    @Override
//    public void installDefaults() 
//    {
//
//    }

    public void installComponents() 
    {
        this.jlabel = new JLabel("BigInt");
        this.jtextField = new JTextField(25);

        this.jlabel.setFocusable(false);

        this.outer.add(this.jlabel, BorderLayout.WEST);
        this.outer.add(this.jtextField, BorderLayout.EAST);

    }

    public void installListeners() 
    {
        this.changeListener = new ChangeListener() 
        {
            public void stateChanged(ChangeEvent e) 
            {
                outer.repaint();
            }
        };
        this.outer.getModel().addChangeListener(this.changeListener);
    }

    public void uninstallDefaults() 
    {
        this.outer.remove(this.jlabel);
        this.jlabel = null;
        this.outer.remove(jtextField);
        this.jtextField = null;
    }

    public void uninstallComponents() 
    {

    }

    public void uninstallListeners() 
    {
        this.outer.getModel().removeChangeListener(this.changeListener);
        this.changeListener = null;
    }

    @Override
    public void paint(Graphics g, JComponent c) 
    {
        super.paint(g, c);
        this.paintCustom(g);
    }

    protected void paintCustom(Graphics g) 
    {
//        Rectangle sliderBounds = sliderRendererPane.getBounds();
//        this.sliderRendererPane.paintComponent(g, this.slider,
//                this.flexiSlider, sliderBounds.x, sliderBounds.y,
//                sliderBounds.width, sliderBounds.height, true);
    }

    protected int modelValueToOuterValue(FlexiRangeModel.Value modelValue) 
    {
        if (modelValue == null)
            return 0;

        // TODO: 
        return 0;
    }

    protected BigIntegerChecksumModel.Value OuterValueToModelValue(int sliderValue)
    {
        // get the model
        BigIntegerChecksumModel model = this.outer.getModel();

        // TODO:
        return null;
    }

    /**
     * Invoked by <code>installUI</code> to create a layout manager object to
     * manage the {@link JFlexiSlider}.
     * 
     * @return a layout manager object
     */
    protected LayoutManager createLayoutManager() 
    {
        return new BorderLayout();
        //return new FlexiSliderLayout();
    }

}