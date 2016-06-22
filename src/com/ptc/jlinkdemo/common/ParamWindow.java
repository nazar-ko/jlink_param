/* HISTORY

13-Apr-99 TK-2-01 klm      $$1  Created
14-May-99 I-01-36 jcn      $$2  Created version without material selection.
                                Moved to com.ptc.jlinkdemo.common.
21-Apr-00 I-03-29 msh      $$3  Created Create and Delete Parameter capabilities
				Split GUI into two modes based on application.

*/

package com.ptc.jlinkdemo.common;

import javax.swing.*;
import javax.swing.border.*;

import java.awt.Frame;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Image;
import java.awt.Toolkit;

import com.ptc.cipjava.jxthrowable;
import com.ptc.pfc.pfcSession.Session;
import com.ptc.pfc.pfcSelect.*;
import com.ptc.pfc.pfcPart.Part;
import com.ptc.pfc.pfcPart.Material;
import com.ptc.pfc.pfcModelItem.*;

import com.ptc.Platform;

import com.ptc.jlinkdemo.material.MaterialDialog;
import com.ptc.jlinkdemo.parameditor.*;

public class ParamWindow extends JDialog
{

    /* The Configuration Parameters */
    private static final int CONF_RELEASE_CHECK = 1;
    private static final int CONF_PARAM_EDITOR  = 2;
    private int configuration;
    /********************************/

    /* Paramter Editor Specific Components and Containers */
    protected Box selectorBox;
    protected JPanel selectorPanel;

    protected JButton selectButton;
    protected JTextField paramOwnerName;
    protected JButton someThing;

    protected JButton newParameter;
    protected JButton deleteParameter;

    protected ParamHelperCreator ph;


    /******************************************************/
    protected Container         content;

    protected JPanel            buttonPane;
    public    JPanel            tablePane;

    protected JTextField        materialNameField;
    protected Color             defaultTextColor;
    protected JButton           materialButton;
    protected JButton           setButton, resetButton, infoButton, closeButton, aboutButton;

    protected boolean           isShowingChildDlg = false;

    protected ParamTable        table;

    protected Session            session;
    protected Part               part;
    protected Material           material;
    protected ParameterHelper [] paramHelpers;

    protected String             title;
    protected boolean            includeMaterial = false;

    public ParamWindow (Session session, Part part,
                        ParameterHelper [] paramHelpers)
    {
        // super /* JFrame */ ("Table Window");
        super /* JDialog */ (new JFrame (), "Release Checking", true);
        setDefaultCloseOperation (JDialog.DO_NOTHING_ON_CLOSE);

        this.session = session;
        this.part = part;
        this.paramHelpers = paramHelpers;
        configuration = CONF_RELEASE_CHECK;
        title = "Release Checking";
        fillContent (true);
        setSize (450, 450);

        UIHelper.centerWindow (this);


    }

    public ParamWindow (Session session, ParameterHelper [] paramHelpers,
                        boolean checkUnassigned, String title)
    {
        super /* JDialog */ (new JFrame (), title, true);
        setDefaultCloseOperation (JDialog.DO_NOTHING_ON_CLOSE);

        this.session = session;
        this.paramHelpers = paramHelpers;
        configuration = CONF_PARAM_EDITOR;
        this.title = title;
        fillContent (checkUnassigned);
        setSize (450, 450);

        UIHelper.centerWindow (this);
        
    }


    protected void fillContent (boolean checkUnassigned)
    {
        JRootPane root = getRootPane ();
        content = root.getContentPane ();

        root.setMinimumSize (new Dimension (100, 100));
        root.setPreferredSize (new Dimension (500, 400));

        // **** Listeners ****
        addWindowListener (new WindowEventHandler());

        // **** Set layout ****
        GridBagLayout layout = new GridBagLayout ();
        content.setLayout (layout);
        GridBagConstraints cstr = new GridBagConstraints ();

        // **** Add components ****

        // *** Top-level panes ***

        // ** Material selection pane **
        Box materialBox = null;

            materialBox = Box.createHorizontalBox ();

            cstr.insets = new Insets (5, 5, 0, 5);
            cstr.gridwidth = GridBagConstraints.REMAINDER;
            cstr.weightx = 1.0;
            cstr.weighty = 0.0;
            cstr.fill = GridBagConstraints.HORIZONTAL;
            layout.setConstraints (materialBox, cstr);
            content.add (materialBox);

        if (configuration == CONF_PARAM_EDITOR)
        {
         //Can possible add more stuff on top.
         selectorBox = Box.createHorizontalBox();

         cstr.insets = new Insets (5, 5, 5, 5);
         cstr.weightx = 1.0;
         cstr.fill = GridBagConstraints.HORIZONTAL;
         layout.setConstraints(selectorBox, cstr);
         content.add(selectorBox);

         /* Unable to select stuff due to modality
         Image img = Toolkit.getDefaultToolkit().getImage("images/pointer.gif");

         ImageIcon pointer =  new ImageIcon(img);
         selectButton = new JButton(pointer);
         paramOwnerName = new JTextField(session.GetCurrentModel().GetFullName());

         selectButton.addActionListener(new ButtonListener());

         selectorPanel = new JPanel();
         selectorPanel.setLayout(new BorderLayout());
         selectorPanel.add(selectButton, BorderLayout.WEST);
         selectorPanel.add(paramOwnerName, BorderLayout.CENTER);
         selectorBox.add(selectorPanel); */

         newParameter = new JButton("Create");
         deleteParameter = new JButton("Delete");

         selectorBox.add(Box.createHorizontalGlue());
         selectorBox.add(newParameter);
         selectorBox.add(Box.createHorizontalGlue());
         selectorBox.add(deleteParameter);
         selectorBox.add(Box.createHorizontalGlue());

         ActionListener al = new ButtonListener();
         newParameter.addActionListener(al);
         deleteParameter.addActionListener(al);



        }
        // ** Table pane **
        tablePane = new JPanel ();
        tablePane.setLayout (new BorderLayout ());

        cstr.weighty = 1.0;
        cstr.fill = GridBagConstraints.BOTH;
        layout.setConstraints (tablePane, cstr);
        content.add (tablePane);

        JSeparator separator = new JSeparator (SwingConstants.HORIZONTAL);
        cstr.weighty = 0.0;
        layout.setConstraints (separator, cstr);
        content.add (separator);

        // ** Set/Reset button pane **
        Box buttonBox = Box.createHorizontalBox ();
        cstr.insets = new Insets (5, 5, 5, 5);
        cstr.fill = GridBagConstraints.HORIZONTAL;
        layout.setConstraints (buttonBox, cstr);
        content.add (buttonBox);

        // * Material selection *
        if (configuration == CONF_RELEASE_CHECK)
        {
            materialBox.add (new JLabel ("Material"));
            materialBox.add (Box.createHorizontalStrut (10));
            materialNameField = new JTextField ();
            defaultTextColor = materialNameField.getForeground ();
            materialNameField.setEditable (false);
            materialBox.add (materialNameField);
            materialBox.add (Box.createHorizontalStrut (10));
            materialButton = new JButton ("...");
            materialButton.addActionListener(new MaterialButtonListener ());
            materialBox.add (materialButton);

            updateMaterial ();
        }
        // * Table *
        if (checkUnassigned)
            table = new UnassignedParamTable (paramHelpers);
        else
            table = new ParamTable (paramHelpers);
        tablePane.add (new JScrollPane (table));

        // * Set/Reset/Close buttons *
        ButtonListener btnListener = new ButtonListener ();

        setButton = new JButton ("Set");
        setButton.addActionListener(btnListener);
        resetButton = new JButton ("Undo");
        resetButton.addActionListener(btnListener);
        infoButton = new JButton ("Info");
        infoButton.addActionListener (btnListener);
        closeButton = new JButton ("Close");
        closeButton.addActionListener(btnListener);
        aboutButton = new JButton ("About");
        aboutButton.addActionListener(btnListener);

        buttonBox.add (Box.createHorizontalGlue ());
        buttonBox.add (setButton);
        buttonBox.add (Box.createHorizontalGlue ());
        buttonBox.add (resetButton);
        buttonBox.add (Box.createHorizontalGlue ());
        buttonBox.add (infoButton);
        buttonBox.add (Box.createHorizontalGlue ());
        buttonBox.add (closeButton);
        buttonBox.add (Box.createHorizontalGlue ());
        buttonBox.add (aboutButton);
        buttonBox.add (Box.createHorizontalGlue ());

        root.setDefaultButton (setButton);
    }

    protected void updateMaterial ()
    {
        try
        {
            material = part.GetCurrentMaterial ();
            String materialName;
            if (material == null)
            {
                materialNameField.setForeground (Color.red);
                materialNameField.setText ("<unassigned>");
            }
            else
            {
                materialNameField.setForeground (defaultTextColor);
                materialNameField.setText (material.GetName ());
            }
        }
        catch (jxthrowable x)
        {
            UIHelper.showException (ParamWindow.this, x, "obtaining material");
        }
    }

    public float getAlignmentX ()
    {
        return (CENTER_ALIGNMENT);
    }

    public float getAlignmentY ()
    {
        return (CENTER_ALIGNMENT);
    }

    private class WindowEventHandler extends WindowAdapter
    {
        public void windowOpened (WindowEvent e)
        {
            if (Platform.getOSAPI () == Platform.OSAPI_WIN32)
                toFront ();
        }

        public void windowClosing (WindowEvent e)
        {
            if (! isShowingChildDlg)
                setVisible (false);
        }
    }

    private class ButtonListener implements ActionListener
    {
        public void actionPerformed (ActionEvent e)
        {
            Object source = e.getSource ();
            if (source == setButton)
            {
                table.commitNewValues ();
                if (table.isAllValid())
                {
                    if (material != null)
                        {
                            setVisible (false);
                            return;
                        }
                    if (!includeMaterial)
                        {
                            setVisible (false);
                            return;
                        }
                }
                if (material == null && includeMaterial)
                    JOptionPane.showMessageDialog (ParamWindow.this,
                                                   "Material is not assigned",
                                                   title+" Error",
                                                   JOptionPane.ERROR_MESSAGE);
                else
                    {
                        String [] warnings = table.getInvalidMessages();

                    JOptionPane.showMessageDialog (ParamWindow.this,
                                              warnings,
                                              title+" Error",
                                              JOptionPane.ERROR_MESSAGE);
                    }
            }
            else if (source == resetButton)
            {
                table.resetNewValues ();
            }
            else if (source == infoButton)
            {
                    String [] warnings = table.getInvalidMessages();
                    if (table.isAllValid() || warnings == null)
                        JOptionPane.showMessageDialog (ParamWindow.this,
                                              "No invalid values found.",
                                              title+ " Info",
                                              JOptionPane.PLAIN_MESSAGE);
                    else
                    {
                    JOptionPane.showMessageDialog (ParamWindow.this,
                                              warnings,
                                              title+ " Info",
                                              JOptionPane.WARNING_MESSAGE);
                    }
            }
            else if (source == closeButton)
            {
                if (! isShowingChildDlg)
                    setVisible (false);
            }
            else if (source == aboutButton)
            {
                String [] aboutMessages =
                    new String []
     {"This application was developed by PTC to demonstrate the use of the new J-Link API.",
      "It is intended as an example of how J-Link can be used to improve productivity at",
      "your company.  Questions regarding J-Link can be directed to customer support or local",
      "AE's, or technical marketing.  PTC also offers J-Link implementation services through ",
      "PTC global services."};
                JOptionPane.showMessageDialog (ParamWindow.this,
                                                 aboutMessages,
                                                "About "+ title,
                                                JOptionPane.PLAIN_MESSAGE);
            }
            else if (source == selectButton)
            {
                 try {
                 String KeyWords = "feature,surface,edge";
                 SelectionOptions selop = pfcSelect.SelectionOptions_Create(KeyWords);
                 selop.SetMaxNumSels(new Integer(1));

                 Selections sels = session.Select(selop, null);
                 Selection sel = sels.get(0);

                 ModelItem mdlitem = sel.GetSelItem();
                 ph = new ParamHelperCreator((ParameterOwner)mdlitem);
                 //Set New Data.
                 table = new ParamTable (ph.getHelpers());

                 paramOwnerName.setText(mdlitem.GetName());
                 tablePane.removeAll();
                 tablePane.add (new JScrollPane (table));
                 }
                 catch (jxthrowable x)
                 {
                   System.out.println("jxthrowable in ButtonListener");
                   x.printStackTrace();
                 }


            }
            else if (source == newParameter)
            {
              CreateParameter createUI = new CreateParameter(session, ParamWindow.this, table);
              createUI.show();
              ParamWindow.this.table = createUI.getTable();
              ParamWindow.this.validate();
            }
            else if (source == deleteParameter)
            {
              try{

                //Get the model and create a set of helpers for it.
                ParameterOwner pOwner = (ParameterOwner)session.GetCurrentModel();
                ParamHelperCreator phCreate = new ParamHelperCreator(pOwner);
                ParameterHelper [] tempPHelpers = phCreate.getHelpers();

                //Make Sure the selection is made
                if (table.getSelectedRow() == -1)
                  JOptionPane.showMessageDialog(ParamWindow.this, "No Parameter Selected");
                else
                {
                int deleteRow =  table.getSelectedRow();
                String deleteName = tempPHelpers[deleteRow].getProeName();
                //Ask the User for confirmation on the parameter to be deleted
                int shouldDelete = JOptionPane.showConfirmDialog(ParamWindow.this,
                                        "Delete Parameter " + tempPHelpers[deleteRow].getName() + " ?");

                //Find and Delete the Needed Parameters.
                if (shouldDelete == 0)
                {
                  Parameters tempParams = pOwner.ListParams();
                  for (int i = 0; i < tempParams.getarraysize(); i++)
                  {
                   Parameter tempParam = tempParams.get(i);

                    if (tempParam.GetName().equals(deleteName))
                      tempParam.Delete();
                    else if (tempParam.GetName().equals(deleteName + "_UINAME"))
                      tempParam.Delete();
                    else if (tempParam.GetName().equals(deleteName + "_TYPE"))
                      tempParam.Delete();
                    else if (tempParam.GetName().equals(deleteName + "_VALUES"))
                      tempParam.Delete();

                  }

                  //Now recreate the new param table GUI
                  phCreate = new ParamHelperCreator(pOwner);
                  tempPHelpers = phCreate.getHelpers();
                  ParamWindow.this.table = new ParamTable(tempPHelpers);
                  ParamWindow.this.tablePane.removeAll();
                  ParamWindow.this.tablePane.add (new JScrollPane (table));
                  ParamWindow.this.validate();
                  }
                  }
                }
                catch (jxthrowable x)
                {
                  x.printStackTrace();
                  System.out.println("Out of Delete");
                }


            }
        }
    }


    private class MaterialButtonListener implements ActionListener
    {
        public void actionPerformed (ActionEvent e)
        {
            printMsg ("materialButton pressed");
            MaterialDialog matDlg = new MaterialDialog ((Frame) getParent (),
                                                        session, part);
            isShowingChildDlg = true;
            matDlg.show ();
            isShowingChildDlg = false;
            matDlg.dispose ();
            updateMaterial ();
        }
    }

    //=========================================================================
    private static void printMsg (String msg)
    {
        System.out.println ("ParamWindow: " + msg);
    }
}
