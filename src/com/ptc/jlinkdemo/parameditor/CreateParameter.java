/* HISTORY

04-Mar-2000      MSH    $$1 Created.

*/

package com.ptc.jlinkdemo.parameditor;

import javax.swing.*;
import javax.swing.border.*;

import java.awt.*;
import java.awt.event.*;

import com.ptc.jlinkdemo.common.*;

import com.ptc.cipjava.*;
import com.ptc.pfc.pfcSession.*;
import com.ptc.pfc.pfcSelect.*;
import com.ptc.pfc.pfcPart.*;
import com.ptc.pfc.pfcModelItem.*;
import com.ptc.pfc.pfcModel.*;

public class CreateParameter extends JDialog implements ActionListener
{
   protected Container content;

   protected JLabel instructions;
   protected JLabel pLabel;
   protected JLabel pUILabel;
   protected JLabel pTypeLabel;
   protected JLabel custParamTypeLabel;
   protected JLabel valueLabel;
   protected JLabel customLabel;

   protected JTextField pName;
   protected JTextField pUIName;
   protected JComboBox pTypeName;
   protected JTextField custParamTypeName;
   protected JPanel valuePanel;
   protected JPanel customPanel;
   protected JTextField value;
   protected JComboBox value2;
   protected JComboBox customBox;


   protected JButton create;
   protected JButton cancel;
   protected JButton addButton;
   protected JButton removeButton;

   public ParamTable table;
   public ParamWindow jdial;
   public Session session;
   public ParameterOwner pOwner;
   private boolean created = false;

   private JTextField maxText;
   private JTextField minText;
   private JTextField incText;
   private JTextField listText;
   private JList strList;
   private DefaultListModel listModel;
   private JScrollPane listScrollPane;

   boolean isChildShowing = false;

   public static void main (String [] args)
   {
      CreateParameter wnd = new CreateParameter();

      wnd.show();
      wnd.dispose();


   }

   public CreateParameter()
   {
      super /*JDialog*/ (new Frame(), "Create A Parameter", true);

      fillContent();

      setSize(500, 300);
      UIHelper.centerWindow(this);
   }

   public CreateParameter(Session session, ParamWindow jdial, ParamTable table)
   {
      super /*JDialog*/ ((Frame)jdial.getParent(), "Create A Parameter", true);

      this.session = session;
      this.jdial = jdial;
      this.table = table;
      fillContent();
      setSize(500, 300);
      UIHelper.centerWindow(this);
   }

   public CreateParameter(ParameterOwner pOwner, ParamWindow jdial, ParamTable table)
   {
      super /*JDialog*/ ((Frame)jdial.getParent(), "Create A Parameter", true);

      this.pOwner = pOwner;
      this.jdial = jdial;
      this.table = table;
      fillContent();
      setSize(500, 300);
      UIHelper.centerWindow(this);
   }



   protected void fillContent()
   {
      content = this.getRootPane().getContentPane();

      // WindowListener
      addWindowListener( new WindowAdapter () {

          public void windowClosing( WindowEvent e) {
            setVisible(false);
          }
      });


      // **** Set layout ****
      GridBagLayout layout = new GridBagLayout ();
      content.setLayout (layout);
      GridBagConstraints cstr = new GridBagConstraints ();

      cstr.insets = new Insets (5, 5, 5, 5);
      cstr.gridwidth = GridBagConstraints.REMAINDER;
      cstr.anchor = GridBagConstraints.NORTH;
      cstr.fill = GridBagConstraints.HORIZONTAL;
      instructions = new JLabel("Please fill out the fields that apply", SwingConstants.CENTER);
      layout.setConstraints(instructions, cstr);
      content.add(instructions);

      JSeparator separator = new JSeparator (SwingConstants.HORIZONTAL);
      cstr.weighty = 0.0;
      layout.setConstraints (separator, cstr);
      content.add (separator);

      /* add parameter name */
      cstr.weightx = 0.5;
      cstr.anchor = GridBagConstraints.CENTER;
      cstr.fill = GridBagConstraints.BOTH;
      cstr.insets = new Insets(5, 5, 5, 0);
      cstr.gridwidth = 1;
      pLabel = new JLabel("Parameter Name: ");
      layout.setConstraints (pLabel, cstr);
      content.add(pLabel);

      cstr.insets = new Insets(5, 0 , 5, 5);
      cstr.gridx = 1;
      cstr.weightx = 1.0;
      pName = new JTextField();
      layout.setConstraints (pName, cstr);
      content.add(pName);

      /* Add ParameterUI Box */
      cstr.insets = new Insets(5, 5, 5, 0);
      cstr.weightx = 0.5;
      cstr.gridwidth = 1;
      cstr.gridx = 0;
      pUILabel = new JLabel("ParameterUI Name: ");
      layout.setConstraints (pUILabel, cstr);
      content.add(pUILabel);

      cstr.insets = new Insets(5, 0 , 5, 5);
      cstr.gridx = 1;
      cstr.weightx = 1.0;
      pUIName = new JTextField();
      layout.setConstraints (pUIName, cstr);
      content.add (pUIName);


      /* Add ParameterType Box */
      cstr.insets = new Insets(5, 5, 5, 0);
      cstr.gridwidth = 1;
      cstr.weightx = 0.5;
      cstr.gridx = 0;
      pTypeLabel = new JLabel("Parameter Type: ");
      layout.setConstraints (pTypeLabel, cstr);
      content.add (pTypeLabel);

      cstr.insets = new Insets(5, 0 , 5, 5);
      cstr.weightx = 1.0;
      cstr.gridx = 1;
      pTypeName = new JComboBox();
      pTypeName.addItem("Integer");
      pTypeName.addItem("Real");
      pTypeName.addItem("String");
      pTypeName.addItem("Boolean");
      layout.setConstraints (pTypeName, cstr);
      content.add (pTypeName);

      /* Add CustParameterType Box */
      cstr.insets = new Insets(5, 5, 5, 0);
      cstr.weightx = 0.5;
      cstr.gridwidth = 1;
      cstr.gridx = 0;
      customLabel = new JLabel("Custom Type: ");
      layout.setConstraints (customLabel, cstr);
      content.add (customLabel);

      cstr.insets = new Insets(5, 0 , 5, 5);
      cstr.gridx = 1;
      cstr.weightx = 1.0;
      customBox = new JComboBox();
      customBox.addItem("None");
      customBox.addItem("List");
      customBox.addItem("Range");
      customBox.addItem("Range with increment");
      layout.setConstraints (customBox, cstr);
      content.add (customBox);


      cstr.gridx=0;
      cstr.gridwidth = 2;
      cstr.insets = new Insets(5, 5, 5, 5);
      customPanel = new JPanel();
      layout.setConstraints(customPanel, cstr);
      content.add (customPanel);

      /* Add Value Box */
      GridBagLayout layout2 = new GridBagLayout();
      customPanel.setLayout(layout2);
      cstr.insets = new Insets(0, 0, 0, 0);
      cstr.gridwidth = 1;
      cstr.gridx = 0;
      cstr.weightx = 0.5;
      valueLabel = new JLabel("Parameter Value: ");
      layout2.setConstraints (valueLabel, cstr);
      customPanel.add (valueLabel);

      cstr.insets = new Insets(0, 0 , 0, 0);
      cstr.gridx = 1;
      valuePanel = new JPanel();
      valuePanel.setLayout(new BorderLayout());
      cstr.weightx = 1.0;
      layout2.setConstraints (valuePanel, cstr);
      customPanel.add (valuePanel);
      value = new JTextField();
      valuePanel.add(value, BorderLayout.CENTER);


      cstr.gridwidth = GridBagConstraints.REMAINDER;
      cstr.gridx = 0;
      cstr.insets = new Insets (5, 5, 5, 5);
      JSeparator separator2 = new JSeparator (SwingConstants.HORIZONTAL);
      cstr.weighty = 0.0;
      layout.setConstraints (separator2, cstr);
      content.add (separator2);

      /* Control Buttons */
      Box buttonBox = Box.createHorizontalBox();
      cstr.gridheight = GridBagConstraints.REMAINDER;
      cstr.fill = GridBagConstraints.BOTH;
      layout.setConstraints(buttonBox, cstr);
      content.add (buttonBox);

      create = new JButton("Create");
      cancel = new JButton("Cancel");
      buttonBox.add(Box.createHorizontalGlue());
      buttonBox.add(create);
      buttonBox.add(Box.createHorizontalGlue());
      buttonBox.add(cancel);
      buttonBox.add(Box.createHorizontalGlue());


      /* add the listeners to all the components */
      create.addActionListener(this);
      cancel.addActionListener(this);
      customBox.addActionListener(this);
      pTypeName.addActionListener(this);

      /* add items to the backup value2 combobox */
      value2 = new JComboBox();
      value2.addItem("True");
      value2.addItem("False");




   }


   public void actionPerformed (ActionEvent e)
   {
      Object source = e.getSource();

      if (source.equals(create))
      {
        createParameter();
        if (created)
        {
        this.setVisible(false);
        this.dispose();
        }
      }
      else if (source.equals(cancel))
      {
        this.setVisible(false);
        this.dispose();
      }
      else if (source.equals(pTypeName))
      {
        String sel = (String)pTypeName.getSelectedItem();
        if (customBox == null)
          {
           System.out.println("Something went wrong here");

          }
        if (sel.equals("Boolean"))
          {
          valuePanel.removeAll();
          valuePanel.add(value2, BorderLayout.CENTER);

          customBox.removeAllItems();
          customBox.addItem("None");
          customPanel.removeAll();
          createRegularValue(customPanel);
          }
        else
          {
          valuePanel.removeAll();
          valuePanel.add(value, BorderLayout.CENTER);

          if (sel.equals("String"))
          {
            customBox.removeAllItems();
            customBox.addItem("None");
            customBox.addItem("List");
            customPanel.removeAll();
            createRegularValue(customPanel);
          }
          else  if (sel.equals("Real"))
          {
            customPanel.removeAll();
            createRegularValue(customPanel);

            customBox.removeAllItems();
            customBox.addItem("None");
            customBox.addItem("List");
            customBox.addItem("Range");
            customBox.addItem("Range with increment");
          }
          else  if (sel.equals("Integer"))
          {
            customPanel.removeAll();
            createRegularValue(customPanel);

            customBox.removeAllItems();
            customBox.addItem("None");
            customBox.addItem("List");
            customBox.addItem("Range");
            customBox.addItem("Range with increment");
          }
          }
        this.validate();

      }
      else if (source.equals(customBox))
      {
        if (customBox.getItemCount() != 0)
        {
        customPanel.removeAll();
        String sel = (String)customBox.getSelectedItem();


        if (sel.equals("List"))
              createListEditor(customPanel);
        else if (sel.equals("Range"))
              createRangeEditor(customPanel, false);
        else if (sel.equals("Range with increment"))
              createRangeEditor(customPanel, true);
        else if (sel.equals("None"))
              createRegularValue(customPanel);

        this.validate();
        }
      }
      else if (source.equals(addButton))
      {
        String val = listText.getText();
        if (val != null && !val.equals(""))
        {
           listModel.addElement(val);
           strList.setSelectedIndex(listModel.getSize() - 1);
        }
      }
      else if (source.equals(removeButton))
      {
        if (listModel.getSize() != 0)
        {
          listModel.removeElementAt(strList.getSelectedIndex());
          if (listModel.getSize() != 0)
          {
            strList.setSelectedIndex(0);
          }
        }
      }

   }

   public void createParameter()
   {
      String message = "";
      System.out.println("In Create Parameter");
      try {
        ParamValue val = null;
        ParamValue UIval = null;
        String P_TYPE = "";
        String P_VALUE = "";
        ParamValue pvalue = null;
        ParamValue ptype = null;
        boolean custom = false;

        if (!customBox.getSelectedItem().equals("None"))
        {
            custom = true;
        }

        if (pTypeName.getSelectedItem().equals("Integer"))
        {
          int num = Integer.parseInt(value.getText());
          val = pfcModelItem.CreateIntParamValue(num);
        }
        else if (pTypeName.getSelectedItem().equals("Real"))
        {
          double dnum = new Double(value.getText()).doubleValue();
          val = pfcModelItem.CreateDoubleParamValue(dnum);
        }
        else if (pTypeName.getSelectedItem().equals("Boolean"))
        {
          if (value2.getSelectedItem().equals("True"))
              val = pfcModelItem.CreateBoolParamValue(true);
          else
              val = pfcModelItem.CreateBoolParamValue(false);
        }
        else if (pTypeName.getSelectedItem().equals("String"))
        {
          val = pfcModelItem.CreateStringParamValue(value.getText());
        }
        System.out.println("Created The first value");

        //Create the UI Name
        if (pUIName.getText() == null || pUIName.getText().equals(""))
            UIval = null;
        else
            UIval = pfcModelItem.CreateStringParamValue(pUIName.getText());


        /* Now for custom type  */
        if (custom)
        {
          //First check for the parameter type and then make sure
          //That each piece of entered data is of correct type.
          if (customBox.getSelectedItem().equals("List"))
          {
            P_TYPE = "LIST";
            int checkint = 0;
            double checkreal = 0;

            for (int i = 0; i < listModel.size(); i++)
            {
                 //Make sure we have a list with correct types
              if (pTypeName.getSelectedItem().equals("Integer"))
                checkint = Integer.parseInt((String)listModel.getElementAt(i));
              else if (pTypeName.getSelectedItem().equals("Real"))
                checkreal = new Double((String)listModel.getElementAt(i)).doubleValue();
              // If all is well add the number to the P_VALUE;
              if (P_VALUE.equals(""))
                P_VALUE = "" +  listModel.getElementAt(i);
              else
                P_VALUE = P_VALUE + " " + listModel.getElementAt(i);
            }
          }
          else if (customBox.getSelectedItem().equals("Range") ||
                   customBox.getSelectedItem().equals("Range with increment") )
          {
            if (customBox.getSelectedItem().equals("Range"))
              P_TYPE = "RANGE";
            else
              P_TYPE = "RANGE_INC";

            if (pTypeName.getSelectedItem().equals("Integer"))
            {
              int min =  Integer.parseInt(minText.getText());
              int max =  Integer.parseInt(maxText.getText());
              if (min > max)
                throw new Exception("Minimum Value Is Greater Then Maximum");
              if (customBox.getSelectedItem().equals("Range with increment"))
              {
                int inc = Integer.parseInt(incText.getText());
                // If increment is negative it is invalid
                if (inc < 0)
                  throw new Exception("Increment Value Cannot Be Negative");
                P_VALUE = "" + min + " " + max + " " + inc;
              }
              else
                P_VALUE = "" + min + " " + max;
            }
            else if (pTypeName.getSelectedItem().equals("Real"))
            {
              double min1 = new Double(minText.getText()).doubleValue();
              double max1 = new Double(maxText.getText()).doubleValue();
              if (min1 > max1)
                throw new Exception("Minimum Value Is Greater Than Maximum");
              if (customBox.getSelectedItem().equals("Range with increment"))
              {
                double inc1 = new Double(incText.getText()).doubleValue();
                // If increment is negative it is invalid
                if (inc1 < 0)
                  throw new Exception("Increment Value Cannot Be Negative");

                P_VALUE = "" + min1 + " " + max1 + " " + inc1;
              }
              else
                P_VALUE = "" + min1 + " " + max1;
            }
          }
          if (P_TYPE.equals("") || P_VALUE.equals(""))
            {
              throw new Exception("No Custom Value Specified");
            }
          else
          {
          ptype = pfcModelItem.CreateStringParamValue(P_TYPE);
          pvalue = pfcModelItem.CreateStringParamValue(P_VALUE);
          }
          //Currently, Boolean has no custom parameters
        }

        if (session == null)
        {
          System.out.println("Using ParamOwner");
          pOwner.CreateParam(pName.getText(), val);
          if (UIval != null)
            pOwner.CreateParam(pName.getText() + "_UINAME", UIval);
          if (custom)
          {
            pOwner.CreateParam(pName.getText() + "_TYPE", ptype);
            pOwner.CreateParam(pName.getText() + "_VALUES", pvalue);
          }

          ParamHelperCreator ph = new ParamHelperCreator(pOwner);
          //Set New Data.
          table = new ParamTable (ph.getHelpers());

          jdial.tablePane.removeAll();
          jdial.tablePane.add (new JScrollPane (table));
          created = true;

        }
        else if (pOwner == null && session == null)
        {
          System.out.println("No Valid Model Available");
          created = false;
        }
        else
        {
          System.out.println("Using Session Model to Create Parameter");
          Model mdl = session.GetCurrentModel();
          mdl.CreateParam(pName.getText(), val);
          if (UIval != null)
            mdl.CreateParam(pName.getText() + "_UINAME", UIval);
          if (custom)
          {
            mdl.CreateParam(pName.getText() + "_TYPE", ptype);
            mdl.CreateParam(pName.getText() + "_VALUES", pvalue);
          }


          ParamHelperCreator ph = new ParamHelperCreator((ParameterOwner)mdl);
          //Set New Data.
          table = new ParamTable (ph.getHelpers());

          jdial.tablePane.removeAll();
          jdial.tablePane.add (new JScrollPane (table));
          created = true;
        }

      }
      catch (NumberFormatException e)
      {
        message = "Invalid Value For A Numeric Parameter";
        created = false;
      }
      catch (jxthrowable x)
      {
        message = new String("The Name And/Or Values Are Invalid");
        System.out.println("JXThrowable");
        created = false;
      }
      catch (Exception e)
      {
        message = "" + e.getMessage();
      }

      if (!created)
      {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
      }

   }

   public ParamTable getTable()
   {
      return this.table;
   }

   private void createRegularValue(JPanel customPanel)
   {
     GridBagLayout layout = new GridBagLayout();
     GridBagConstraints cstr = new GridBagConstraints();

     cstr.fill = GridBagConstraints.HORIZONTAL;
     cstr.weightx = 0.5;

     customPanel.setLayout(layout);
     cstr.insets = new Insets(0, 0, 0, 0);
     cstr.gridwidth = 1;
     cstr.gridx = 0;
     layout.setConstraints (valueLabel, cstr);
     customPanel.add (valueLabel);

     cstr.insets = new Insets(0, 0 , 0, 0);
     cstr.gridx = 1;
     cstr.weightx = 1.0;
     layout.setConstraints (valuePanel, cstr);
     customPanel.add (valuePanel);

     this.setSize(500,300);

   }

   private void createRangeEditor(JPanel customPanel, boolean inc)
   {
     System.out.println("Create Range Editor " + inc);
     JLabel minLabel = new JLabel("Min:");
     JLabel maxLabel = new JLabel("Max:");
     JLabel incLabel = new JLabel("Increment:");
     maxText = new JTextField();
     minText = new JTextField();
     incText = new JTextField();

     GridBagLayout layout = new GridBagLayout();
     GridBagConstraints cstr = new GridBagConstraints();

     cstr.fill = GridBagConstraints.HORIZONTAL;
     cstr.weightx = 0.5;

     customPanel.setLayout(layout);
     cstr.insets = new Insets(0, 0, 5, 0);
     cstr.gridwidth = 1;
     cstr.gridx = 0;
     layout.setConstraints (valueLabel, cstr);
     customPanel.add (valueLabel);

     cstr.insets = new Insets(0, 0 , 5, 0);
     cstr.gridx = 1;
     cstr.weightx = 1.0;
     layout.setConstraints (valuePanel, cstr);
     customPanel.add (valuePanel);


     cstr.insets = new Insets(0, 0, 5, 0);
     cstr.gridwidth = 1;
     cstr.gridx = 0;
     cstr.weightx = 0.5;
     layout.setConstraints(minLabel, cstr);
     layout.setConstraints(maxLabel, cstr);
     layout.setConstraints(incLabel, cstr);

     cstr.gridx = 1;
     cstr.weightx = 1.0;
     layout.setConstraints(maxText, cstr);
     layout.setConstraints(minText, cstr);
     layout.setConstraints(incText, cstr);

     customPanel.add(minLabel);
     customPanel.add(minText);
     customPanel.add(maxLabel);
     customPanel.add(maxText);
     if (inc)
     {
     customPanel.add(incLabel);
     customPanel.add(incText);
     }
     this.setSize(500, 370);


   }

   private void createListEditor(JPanel customPanel)
   {
     addButton = new JButton("Add");
     removeButton = new JButton("Remove");
     listText  = new JTextField();
     listModel = new DefaultListModel();
     strList = new JList(listModel);
     strList.setVisibleRowCount(4);
     strList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
     listScrollPane = new JScrollPane(strList);


     GridBagLayout layout = new GridBagLayout();
     GridBagConstraints cstr = new GridBagConstraints();

     customPanel.setLayout(layout);
     cstr.fill = GridBagConstraints.BOTH;
     cstr.weightx = 1.0;
     cstr.insets = new Insets(0, 0, 5, 0);
     cstr.gridwidth = 1;
     cstr.gridx = 0;
     layout.setConstraints (valueLabel, cstr);
     customPanel.add (valueLabel);

     cstr.insets = new Insets(0, 0 , 5, 0);
     cstr.gridx = 1;
     layout.setConstraints (valuePanel, cstr);
     customPanel.add (valuePanel);

     cstr.insets = new Insets(0, 0, 5, 0);
     cstr.gridwidth = 2;
     cstr.gridheight = 2;
     cstr.gridx = 0;
     cstr.weighty = 1.0;
     layout.setConstraints(listScrollPane, cstr);
     customPanel.add(listScrollPane);

     cstr.fill = GridBagConstraints.HORIZONTAL;
     cstr.weighty = 0;
     layout.setConstraints(listText, cstr);
     customPanel.add(listText);

     cstr.gridwidth = 1;
     cstr.gridx = 0;
     cstr.insets = new Insets(0, 0, 5, 5);
     layout.setConstraints(addButton, cstr);
     customPanel.add(addButton);

     cstr.gridx = 1;
     cstr.insets = new Insets(0, 0, 5, 0);
     layout.setConstraints(removeButton, cstr);
     customPanel.add(removeButton);

     /* add the Listener to both buttons */
     addButton.addActionListener(this);
     removeButton.addActionListener(this);

     this.setSize(500, 420);



   }

}
