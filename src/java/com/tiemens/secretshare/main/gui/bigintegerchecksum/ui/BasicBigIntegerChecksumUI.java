package com.tiemens.secretshare.main.gui.bigintegerchecksum.ui;

import java.awt.Graphics;
import java.awt.LayoutManager;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ComponentUI;

import net.miginfocom.layout.AC;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

import com.tiemens.secretshare.main.gui.bigintegerchecksum.BigIntegerChecksumModel;
import com.tiemens.secretshare.main.gui.bigintegerchecksum.JBigIntegerChecksum;
import com.tiemens.secretshare.math.BigIntUtilities;

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
    // redundant data - copied from outer.getModel().isAllowHumanChoice:
    private boolean allowChoiceHumanString;
    
    protected JTextField jtextField;
    protected JComboBox  jcomboBox;
    
    protected ComboCoordinator comboCoordinator;
    protected MyComboChoice choiceBigIntegerChecksum;
    protected MyComboChoice choiceBigInteger;
    protected MyComboChoice choiceHexNumber;
    protected MyComboChoice choiceHumanString;
    
    protected MouseListener mouseListener;

    protected MouseMotionListener mouseMotionListener;

    protected ChangeListener changeListener;
    
    protected ItemListener itemListener;

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

    public BasicBigIntegerChecksumUI()
    {

    }

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
        this.outer = (JBigIntegerChecksum) c;
        
        this.outer.setLayout(createLayoutManager());
        
        this.allowChoiceHumanString = outer.getModel().isAllowHumanString();
        
        //this.outer.setBorder(new EmptyBorder(1, 1, 1, 1));
        
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
        this.jcomboBox = new JComboBox();
        this.jtextField = new JTextField(80);
        this.jtextField.setDocument(this.outer.getModel().getTextFieldDocument());
        

        this.comboCoordinator = new ComboCoordinator();
        this.choiceBigIntegerChecksum = new MyComboChoice(this.comboCoordinator,
                                                          ComboType.AS_BIGINT_CS);
        this.choiceBigInteger = new MyComboChoice(this.comboCoordinator,
                                                  ComboType.AS_BIG_INTEGER);
        this.choiceHexNumber = new MyComboChoice(this.comboCoordinator,
                                                 ComboType.AS_HEX);
        this.choiceHumanString = new MyComboChoice(this.comboCoordinator,
                                                 ComboType.AS_HUMAN_STRING);
        
        this.jcomboBox.addItem(choiceBigIntegerChecksum);
        this.jcomboBox.addItem(choiceBigInteger);
        this.jcomboBox.addItem(choiceHexNumber);
        if (allowChoiceHumanString)
        {
            this.jcomboBox.addItem(choiceHumanString);
        }
        this.jcomboBox.setSelectedIndex(-1); // no selection
        
        this.outer.add(this.jcomboBox, "grow");  // new CC().growX());   // BorderLayout.CENTER);
        this.outer.add(this.jtextField, "grow"); // new CC().growX());   //BorderLayout.EAST);

    }

    public void installListeners() 
    {
        this.changeListener = new ChangeListener() 
        {
            @Override
            public void stateChanged(ChangeEvent e) 
            {
                outer.repaint();
            }

        };
        this.outer.getModel().addChangeListener(this.changeListener);
        
        this.itemListener = new ItemListener()
        {
            @Override
            public void itemStateChanged(ItemEvent e)
            {
                ((MyComboChoice) e.getItem()).itemSelected();
            }
            
        };
        this.jcomboBox.addItemListener(this.itemListener);
    }
    
    public void uninstallDefaults() 
    {
        this.outer.remove(this.jcomboBox);
        this.jcomboBox = null;
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
        this.jcomboBox.removeItemListener(this.itemListener);
        this.itemListener = null;
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

    protected int modelValueToOuterValue(BigIntegerChecksumModel.Value modelValue) 
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
     * Invoked by <code>installUI</code> to create the layout manager object.
     * 
     * @return a layout manager object
     */
    protected LayoutManager createLayoutManager() 
    {
        //return new BorderLayout();
        //return new FlexiSliderLayout();
        return new MigLayout(
                             //  layout constraints
                             //    "hidemode 2"
                             new LC(),
                             //  column constraints
                             // "[5][20][40:100:140][20]"
                             new AC().size("140::150").align("right").grow().gap("")
                               .size("100:250:").grow().align("left"),
                             // row constraints
                             //    "" 
                             new AC().size("min!")
                             );
    }

    private static enum ComboType
    {
        AS_BIGINT_CS,
        AS_BIG_INTEGER, 
        AS_HEX,
        AS_HUMAN_STRING
    }
    private class ComboCoordinator
    {

        public void itemSelected(ComboType target)
        {
            BigIntegerChecksumModel.Value value = outer.getModel().getValue();
            if (value != null) // .isValid() ?
            {
                String s;
                switch (target)
                {
                    case AS_BIGINT_CS:
                        s = BigIntUtilities.createStringMd5CheckSumFromBigInteger(value.getBigInteger());
                        outer.getModel().setValueAsBigIntCsString(s);
                        break;
                    case AS_BIG_INTEGER:
                        s = value.getBigInteger().toString();
                        outer.getModel().setValueAsBigIntegerString(s);
                        break;
                    case AS_HEX:
                        s = BigIntUtilities.createHexStringFromBigInteger(value.getBigInteger());
                        outer.getModel().setValueAsHexString(s);
                        break;
                    case AS_HUMAN_STRING:
                        s = BigIntUtilities.createStringFromBigInteger(value.getBigInteger());
                        outer.getModel().setValueAsHumanString(s);
                        break;
                }
            }
            else
            {
                System.out.println("selection seen, but ignored");
            }
        }

        public String getStringForTypeAndState(ComboType type)
        {
            String ret = null;
            Map<ComboType, String> lookup = new HashMap<ComboType, String>();
            BigIntegerChecksumModel.Value value = outer.getModel().getValue();
            if (value != null) // .isValid() ?
            {
                lookup.put(ComboType.AS_BIGINT_CS, "convert to BigIntCs"); 
                lookup.put(ComboType.AS_BIG_INTEGER, "convert to Big Integer"); 
                lookup.put(ComboType.AS_HEX, "convert to Hex");
                lookup.put(ComboType.AS_HUMAN_STRING, "convert to Human String");
                
                if (value.isBigIntegerChecksum())
                {
                    lookup.put(ComboType.AS_BIGINT_CS, "(bigintcs:)");
                }
                else if (value.isBigInteger())
                {
                    lookup.put(ComboType.AS_BIG_INTEGER, "(big integer)");
                }
                else if (value.isHex())
                {
                    lookup.put(ComboType.AS_HEX, "(hex)");
                }
                else if (value.isHumanString())
                {
                    lookup.put(ComboType.AS_HUMAN_STRING, "(string)");
                }
                else
                {
                    // error
                }
                ret = lookup.get(type);
            }
            else
            {
                ret = "(invalid)";
            }
            
            if (ret == null)
            {
                ret = ":lookup error: on type " + type;
            }
            return ret;

        }
    }
    private static class MyComboChoice
    {
        private final ComboType type;
        private final ComboCoordinator coordinator;
        
        public MyComboChoice(ComboCoordinator inCoordinator,
                             ComboType inType)
        {
            coordinator = inCoordinator;
            type = inType;
        }
        public void itemSelected()
        {
            coordinator.itemSelected(this.type);
        }
        public String toString()
        {
            return coordinator.getStringForTypeAndState(type);
        }
    }
}