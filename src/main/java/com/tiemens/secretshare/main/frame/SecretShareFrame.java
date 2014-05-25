package com.tiemens.secretshare.main.frame;

import javax.swing.JFrame;

import com.tiemens.secretshare.gui.GuiFactory;
import com.tiemens.secretshare.gui.SecretSharePanel;

public class SecretShareFrame
{

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        final GuiFactory guiFactory = new GuiFactory();

        final SecretShareFrame ssf = new SecretShareFrame(guiFactory);
        
        javax.swing.SwingUtilities.invokeLater(new Runnable() 
        {
            public void run() 
            {
                ssf.createAndShowGUI();
            }
        });
    }


    // ==================================================
    // class static data
    // ==================================================

    // ==================================================
    // class static methods
    // ==================================================

    // ==================================================
    // instance data
    // ==================================================
    private JFrame frame;
    private GuiFactory guiFactory;
    
    
    // ==================================================
    // factories
    // ==================================================

    // ==================================================
    // constructors
    // ==================================================

    public SecretShareFrame(GuiFactory inGuiFactory)
    {
        guiFactory = inGuiFactory;
    }
    
    // ==================================================
    // public methods
    // ==================================================

    // ==================================================
    // non public methods
    // ==================================================
    /**
     * Create AND show/display the GUI. 
     * Only invoked from the event-dispatching thread.
     */
    private void createAndShowGUI()
    {
        createGUI(guiFactory);
        
        //Display the window.
        this.frame.pack();
        this.frame.setVisible(true);
    }
    
    /**
     * Create the GUI. 
     * Only invoked from the event-dispatching thread.
     */
    private void createGUI(GuiFactory guiFactory) 
    {
        //Create and set up the window.
        this.frame = new JFrame("Secret Share Application");
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
//        DynamicTreePanel newContentPane = new DynamicTreePanel();
        SecretSharePanel newContentPanel = new SecretSharePanel(guiFactory);
        
        newContentPanel.createGUI();
        newContentPanel.setOpaque(true); //content panes must be opaque
        this.frame.setContentPane(newContentPanel);
    }
    

}