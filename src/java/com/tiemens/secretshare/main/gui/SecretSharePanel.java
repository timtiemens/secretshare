/*
 * Copyright (c) 1995 - 2009 Sun Microsystems, Inc.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Sun Microsystems nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 

package com.tiemens.secretshare.main.gui;

/*
 * This code is based on an example provided by Richard Stanford, 
 * a tutorial reader.
 */

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import com.tiemens.secretshare.main.gui.bigintegerchecksum.BigIntegerChecksumModel;
import com.tiemens.secretshare.main.gui.bigintegerchecksum.DefaultBigIntegerChecksumModel;
import com.tiemens.secretshare.main.gui.bigintegerchecksum.JBigIntegerChecksum;

public class SecretSharePanel 
    extends JPanel 
    implements ActionListener 
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
    private GuiFactory guiFactory;
    
    // ==================================================
    // factories
    // ==================================================

    private static final String SECRET_STRING_COMMAND = "asstring";
    private static final String SECRET_NUMBER_COMMAND = "asnumber";

    // ==================================================
    // constructors
    // ==================================================
    public SecretSharePanel(GuiFactory inGuiFactory) 
    {
        super(new BorderLayout());
        guiFactory = inGuiFactory;
        
    }
    // ==================================================
    // public methods
    // ==================================================

    /**
     * Create the GUI. 
     * Only invoked from the event-dispatching thread.
     */
    public void createGUI() 
    {
        //Create the components.
//        treePanel = new DynamicTree();
//        treePanel.createGUI();
        

        JTextField secretStringTextField = new JTextField("-sample string-");
        secretStringTextField.setActionCommand(SECRET_STRING_COMMAND);
        secretStringTextField.addActionListener(this);

        JTextField secretNumberTextField = new JTextField("-sample number-");
        secretNumberTextField.setActionCommand(SECRET_NUMBER_COMMAND);
        secretNumberTextField.addActionListener(this);

        JBigIntegerChecksum bigintField = 
            new JBigIntegerChecksum(); //new String[] { "abba", "abba2", "abc" }, false);
        //bigintField.setActionCommand(SECRET_NUMBER_COMMAND);
        //bigintField.addActionListener(this);
        
        BigIntegerChecksumModel nohuman = new DefaultBigIntegerChecksumModel(false);
        JBigIntegerChecksum bignohuman = 
            new JBigIntegerChecksum(nohuman); 
        
        //Lay everything out.
//        treePanel.setPreferredSize(new Dimension(300, 150));
//        add(treePanel, BorderLayout.CENTER);

        MigLayout layout = new MigLayout(
                                         //  layout constraints
                                         "",  // "debug 3",
                                         //new LC(),
                                         //  column constraints
                                         "[grow, left]",
                                         //new AC().size("80:100:100").gap("").size("200"),
                                         // row constraints
                                         "" 
                                         // new AC().size("min!")
        );
        this.setLayout(layout);
        
        this.add(secretStringTextField, "growx, wrap");
        this.add(secretNumberTextField, "wrap"); 
        this.add(bigintField, "grow, wrap");
        this.add(bignohuman, "grow, wrap");
        
        
        DynamicTreePanel dtp = new DynamicTreePanel(guiFactory);
        dtp.createGUI();
        this.add(dtp, "wrap");
    }

    private static int actionperformedcount = 0; 
    public void actionPerformed(ActionEvent e) 
    {
        actionperformedcount++;
        String command = e.getActionCommand();
        
        if (SECRET_STRING_COMMAND.equals(command)) 
        {
            System.out.println("in STRING");
            //Add button clicked
//            treePanel.addObject("New Node " + newNodeSuffix++);
        } 
        else if (SECRET_NUMBER_COMMAND.equals(command)) 
        {
            System.out.println("in NUMBER (" + actionperformedcount + ")");
            //Remove button clicked
//            treePanel.removeCurrentNode();
        }
        else
        {
            System.out.println("Unknown action: " + e);
        }
    }
    
    
    // ==================================================
    // non public methods
    // ==================================================


}
