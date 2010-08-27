package com.tiemens.secretshare.gui;

import java.awt.Component;
import java.util.*;
import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxEditor;
import javax.swing.text.*;


public class BigIntegerChecksumComboBox
    extends JComboBox
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

    // ==================================================
    // non public methods
    // ==================================================



    private static final Locale[] INSTALLED_LOCALES = Locale.getAvailableLocales();
    
    private ComboBoxModel model = null;
    
    public static void main(String[] args)
    {
        JFrame f = new JFrame("AutoCompleteComboBox");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        BigIntegerChecksumComboBox box = 
            new BigIntegerChecksumComboBox(INSTALLED_LOCALES, false);
        f.getContentPane().add(box);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }
    
    /** 
     * Constructor for AutoCompleteComboBox -
     * The Default Model is a TreeSet which is alphabetically sorted and doesnt allow
     * duplicates.  
     * @param items  
     */
    public BigIntegerChecksumComboBox(Object[] items, boolean caseSensitive)
    {
        super(items);
        model = new ComboBoxModel(items);
        setModel(model);
        setEditable(true);
        setEditor(new AutoCompleteEditor(this, caseSensitive));
    }
    
    public BigIntegerChecksumComboBox(List<Object> items, boolean caseSensitive)
    {
        super();
        model = new ComboBoxModel(items);
        setModel(model);
        setEditable(true);
        setEditor(new AutoCompleteEditor(this, caseSensitive));
    }

    public BigIntegerChecksumComboBox(boolean caseSensitive)
    {
        super();
        setEditable(true);
        setEditor(new AutoCompleteEditor(this, caseSensitive));
    }

    /*   * ComboBoxModel.java    */
    public class ComboBoxModel 
        extends DefaultComboBoxModel
    {
        /**
         *  The TreeSet which holds the combobox's data (ordered no duplicates)
         */
        private TreeSet<String> values = null;
        
        public ComboBoxModel(List<Object> items)
        {
            super();
            this.values = new TreeSet<String>();
            int i, c;
            for (i = 0, c = items.size(); i < c; i++)
            {
                values.add(items.get(i).toString());
            }
            Iterator<String> it = values.iterator();
            while (it.hasNext())
            {
                super.addElement(it.next().toString());
            }
        }
        public ComboBoxModel(final Object items[])
        {
            this(Arrays.asList(items));
        }
    }

    /*   * AutoCompleteEditor.java   */
    public class AutoCompleteEditor
        extends BasicComboBoxEditor
    {
        private JTextField editor = null;
        public AutoCompleteEditor(JComboBox combo, boolean caseSensitive)
        {
            super();
            editor = new AutoCompleteEditorComponent(combo, caseSensitive);
        }
        /**
         * overrides BasicComboBox's getEditorComponent to return custom TextField
         * (AutoCompleteEditorComponent)        
         */
        public Component getEditorComponent()
        {
            return editor;
        }
    }

    /*   * AutoCompleteEditorComponent.java  */
    public class AutoCompleteEditorComponent 
        extends JTextField
    {
        JComboBox combo = null;
        boolean caseSensitive = false;
        public AutoCompleteEditorComponent(JComboBox combo, boolean caseSensitive)
        {
            super();
            this.combo = combo;
            this.caseSensitive = caseSensitive;
        }
        /**
        * overwritten to return custom PlainDocument which does the work*/
        protected Document createDefaultModel()
        {
            return new PlainDocument()
            {
                public void insertString(int offs, String str, AttributeSet a) 
                    throws BadLocationException
                {
                    if (str == null || str.length() == 0)
                        return;
                    int size = combo.getItemCount();
                    String text = getText(0, getLength());
                    for (int i = 0; i < size; i++)
                    {
                        String item = combo.getItemAt(i).toString();
                        if (getLength() + str.length() > item.length())
                            continue;
                        if (!caseSensitive)
                        {
                            if ((text + str).equalsIgnoreCase(item))
                            {
                                combo.setSelectedIndex(i);
                                if (!combo.isPopupVisible())
                                  combo.setPopupVisible(true);
                                super.remove(0, getLength());
                                super.insertString(0, item, a);
                                return;
                            }
                            else if (item.substring(0, getLength() + str.length()).equalsIgnoreCase(text + str))
                            {
                                combo.setSelectedIndex(i);
                                if (!combo.isPopupVisible())
                                        combo.setPopupVisible(true);
                                    super.remove(0, getLength());
                                    super.insertString(0, item, a);
                                    return;
                            }
                        }
                        else if (caseSensitive)
                        {
                            if ((text + str).equals(item))
                            {
                                combo.setSelectedIndex(i);
                                if (!combo.isPopupVisible())
                                    combo.setPopupVisible(true);
                                super.remove(0, getLength());
                                super.insertString(0, item, a);
                                return;
                            }
                            else if (item.substring(0, getLength() + str.length()).equals(text + str))
                            {
                                combo.setSelectedIndex(i);
                                if (!combo.isPopupVisible())
                                    combo.setPopupVisible(true);
                                super.remove(0, getLength());
                                super.insertString(0, item, a);
                                return;
                            }
                        }
                    }
                }
            };
        }
    }
}
