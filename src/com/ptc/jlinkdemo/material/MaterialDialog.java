package com.ptc.jlinkdemo.material;

/**********************************************************************\

	MaterialDialog.java
	UI for material demo.

25-Jan-99  mshmays  $$1 Created.
08-Feb-99  JCN      $$2 Renamed.  Adjusted to read all ".mat" files in
                        current directory.
22-Feb-99  mshmays  $$3 Fixed the Material selection.
13-Apr-99  klm      $$4 AWT->Swing 1.1, MAJOR cleanup, but it's still a mess

\**********************************************************************/

import java.awt.*;
import java.util.*;
import java.io.*;

import javax.swing.*;
import javax.swing.table.*;

import com.ptc.cipjava.*;
import com.ptc.pfc.pfcGlobal.*;
import com.ptc.pfc.pfcPart.*;
import com.ptc.pfc.pfcCommand.*;
import com.ptc.pfc.pfcSession.*;
import com.ptc.pfc.pfcModelItem.*;
import com.ptc.pfc.pfcModel.*;
import com.ptc.pfc.pfcFeature.*;

import com.ptc.jlinkdemo.common.UIHelper;

public class MaterialDialog extends JDialog
{
    private static Vector fieldName = new Vector ();
    private static Vector data = new Vector ();
    private static Vector data1 = new Vector ();

    protected boolean           isShowingChildDlg = false;

    Session session;
    Part    part;

	//{{DECLARE_CONTROLS
	JButton     Assign;
	JButton     Display;
	JButton     button4;
	JButton     Cancel;
	JTable      jTable1;
	//}}

	public MaterialDialog (Frame parent, Session session, Part part)
	{
		super (parent, "Material Selector", true);
		this.session = session;
        this.part = part;

        fillContent ();
	}

    // GUI components fill-in
	protected void fillContent ()
	{
        data = readMats ();
        data1 = (Vector) data.clone ();

        final String [] columnNames = {
            "Material", "Young", "Poisson", "Conduct.", "Hardness"
        };

		fieldName = new Vector ();
        for (int iField = 0; iField < columnNames.length; ++iField)
            fieldName.addElement (columnNames [iField]);
        
        AbstractTableModel amodel = new AbstractTableModel ()
        {
            public String getColumnName (int col)
            {
                return columnNames[col].toString (); 
            }
            public int getRowCount () { return data.size (); }
            public int getColumnCount () { return columnNames.length; }
            public Object getValueAt (int row, int col)
            { 
                return ((Vector)data.elementAt (row)).elementAt (col); 
            }
            public boolean isCellEditable (int row, int col)
            {
                return (false);
            }
        };

		//{{INIT_CONTROLS
        JRootPane root = getRootPane ();
        Container content = root.getContentPane ();

        root.setMinimumSize (new Dimension (100, 100));
		root.setPreferredSize (new Dimension (454,306));

		content.setLayout (new BorderLayout ());
        Box box = Box.createVerticalBox ();
        content.add (box, BorderLayout.CENTER);

		jTable1 = new JTable ();
        jTable1.setModel (amodel);
        jTable1.setBackground (Color.white);
        jTable1.getSelectionModel ().setSelectionMode (
            ListSelectionModel.SINGLE_SELECTION);

        box.add (Box.createVerticalStrut (5));
		box.add (new JScrollPane (jTable1));
        box.add (Box.createVerticalStrut (5));
        box.add (new JSeparator (SwingConstants.HORIZONTAL));
        box.add (Box.createVerticalStrut (5));

        Box btnBoxTop = Box.createHorizontalBox ();
		// setBackground (new Color (-16177505));
		Assign = new JButton ();
		Assign.setText ("Assign");
        btnBoxTop.add (Box.createHorizontalGlue ());
		btnBoxTop.add (Assign);
        btnBoxTop.add (Box.createHorizontalGlue ());
		Display = new JButton ();
		Display.setText ("View File");
        btnBoxTop.add (Box.createHorizontalGlue ());
		btnBoxTop.add (Display);
        btnBoxTop.add (Box.createHorizontalGlue ());
		button4 = new JButton ();
		button4.setText ("Search");
        btnBoxTop.add (Box.createHorizontalGlue ());
		btnBoxTop.add (button4);
        btnBoxTop.add (Box.createHorizontalGlue ());

        box.add (btnBoxTop);
        box.add (Box.createVerticalStrut (5));

        Box btnBoxBtm = Box.createHorizontalBox ();
		Cancel = new JButton ();
		Cancel.setText ("Close");
        btnBoxBtm.add (Box.createHorizontalGlue ());
		btnBoxBtm.add (Cancel);
        btnBoxBtm.add (Box.createHorizontalGlue ());

        box.add (btnBoxBtm);
        box.add (Box.createVerticalStrut (5));

		setTitle ("Material Selector");
		//}}
        
		//{{REGISTER_LISTENERS
		addWindowListener (new SymWindow ());

		SymAction aSymAction = new SymAction ();
		Assign.addActionListener (aSymAction);
		Display.addActionListener (aSymAction);
        Cancel.addActionListener (aSymAction);
        button4.addActionListener (aSymAction);
		//}}
		
        pack ();
        UIHelper.centerWindow (this);
	}

    //============================== readMats () ===============================
    // Reads all ".mat" files in the current directory
    //   and adds them to the list of materials
    Vector readMats ()
    {
        Vector mats = new Vector ();
        Vector mat_info;
        String [] mat_array;
        File current = new File (".");

        mat_array = current.list (new FilenameFilter ()
        {
            public boolean accept (File dir, String name)
            {
                if (name.endsWith (".mat"))
                {
                    return true;
                }
                else return false;
            }
        });

        int numMats= mat_array.length;

        for (int ii = 0; ii < numMats; ii++)
        {
            mat_info = readMat (mat_array[ii]);
            if (mat_info != null)
            {
                mats.addElement (mat_info);
            }
        }

        return mats;
    }

    //================================ readMat () ==============================
    //Reads a ".mat" file and parses values for use by the display dialog
	Vector readMat (String filename)
	{
	    Vector temp = new Vector ();

        temp.addElement (filename.substring (0, filename.length () - 4));
        try
        {
            BufferedReader in = new BufferedReader (new FileReader (filename));

            String s;
            while ((s = in.readLine ()) != null)
            {
                if (s.indexOf ("YOUNG_MODULUS") != -1 ||
                    s.indexOf ("POISSON_RATIO") != -1 ||
                    s.indexOf ("THERMAL_CONDUCTIVITY") != -1 ||
                    s.indexOf ("HARDNESS") != -1)
                {
                    temp.addElement (Double.valueOf (
                                     s.substring (s.indexOf ("=")+1,
                                                  s.length ())));
                }
            }
        }
        catch (IOException e)
        {
            UIHelper.showException (this, e, "reading material file");
            return (null);
        }
        catch (NumberFormatException e)
        {
            UIHelper.showErrorMessage (this,
                "The VALUES are invalid in the MATERIAL FILE: " + filename,
                "reading material file");
            return (null);
        }

        return temp;
    }

	class SymWindow extends java.awt.event.WindowAdapter
	{
		public void windowClosing (java.awt.event.WindowEvent event)
		{
			if (! isShowingChildDlg)
                setVisible (false);
		}
	}

	class SymAction implements java.awt.event.ActionListener
	{
	    public void actionPerformed (java.awt.event.ActionEvent event)
		{
		    Object source = event.getSource ();
		    
		    if (source == Display)
				doDisplay ();
		    else if (source == Assign)
			    doAssign ();
			else if (source == Cancel)
			    doCancel ();
			else if (source == button4)
			    doSearch ();
		}
	}

	void doDisplay ()
	{
	    int index = jTable1.getSelectedRow ();  
	    if (index != -1)
	    {
            String filename = (String) jTable1.getValueAt (index, 0);
            isShowingChildDlg = true;
            SelectBox.showMaterialFile ((Frame) getParent (), filename);
            isShowingChildDlg = false;
		}
		else
		    printMsg ("doDisplay: nothing selected");
	}

    // Allows the user to select a material type
    //   and assign it to a model in Pro/E.
	void doAssign ()
	{
        int index = jTable1.getSelectedRow ();
        if (index != -1)
        {
            String filename = (String) jTable1.getValueAt (index, 0);
            // printMsg ("Material: " + filename);
            selectMaterial (filename);
        }
        else
            printMsg ("doAssign: nothing selected");
	}

    // Assigns a material to a model in Pro/E
	void selectMaterial (String fileName)
	{
	    try
        {
            // printMsg ("selectMaterial: part.RetrieveMaterial: " + fileName);
            part.RetrieveMaterial (fileName);
            setVisible (false);
        }
        catch (jxthrowable e)
        {
            UIHelper.showException (this, e, "setting material");
        }
	}

    void doCancel ()
    {
        setVisible (false);
    }

    void doSearch ()
    {
        (new MaterialSearcher ((Frame) getParent (), data1, session))
            .show ();
    }

    //=========================================================================
    public static void printMsg (String msg)
    {
        System.out.println ("MaterialDialog: " + msg);
    }
}
