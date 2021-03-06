/* HISTORY

13-Apr-99 TK-2-01 klm      $$1  Cleanup of M.Shmays's creation

*/

package com.ptc.jlinkdemo.material;

import java.awt.Frame;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import java.io.*;

import com.ptc.jlinkdemo.common.UIHelper;

public class SelectBox extends JDialog
{
	//{{DECLARE_CONTROLS
	JTextArea textArea1;
	JButton OKAY;
	//}}

	private SelectBox (Frame parent, String title, String text)
	{
		super (parent, title, true);

        JRootPane root = getRootPane ();
        Container content = root.getContentPane ();

        root.setMinimumSize (new Dimension (100, 100));
		root.setPreferredSize (new Dimension (510,561));

		// This code is automatically generated by Visual Cafe when you add
		// components to the visual environment. It instantiates and initializes
		// the components. To modify the code, only use code syntax that matches
		// what Visual Cafe can generate, or Visual Cafe may be unable to back
		// parse your Java file into its visual environment.
		//{{INIT_CONTROLS
		content.setLayout (new BorderLayout ());
        Box box = Box.createVerticalBox ();
        content.add (box, BorderLayout.CENTER);

		textArea1 = new JTextArea (text);
		textArea1.setEditable(false);
		textArea1.setFont(new Font("Courier", Font.PLAIN, 12));
		box.add (textArea1);

        box.add (new JSeparator (SwingConstants.HORIZONTAL));

        Box btnBox = Box.createHorizontalBox ();
        box.add (btnBox);
		OKAY = new JButton ();
		OKAY.setText ("Close");
        btnBox.add (Box.createHorizontalGlue ());
		btnBox.add (OKAY);
        btnBox.add (Box.createHorizontalGlue ());
		//}}

		//{{REGISTER_LISTENERS
		addWindowListener(new SymWindow());
		OKAY.addMouseListener(new SymMouse());
		//}}

        pack ();
        UIHelper.centerWindow (this);
	}

    public static void showMaterialFile (Frame parent, String matName)
    {
        String fileName = matName + ".mat";
		try
        {
            BufferedReader in =
                new BufferedReader (new FileReader (fileName));
            StringBuffer text = new StringBuffer ();
            String s;
            while ((s = in.readLine ()) != null)
            {
                text.append (s);
                text.append ('\n');
            }

            new SelectBox (parent, "Material File: " + fileName,
                           text.toString ())
                .show ();
	    }
	    catch (FileNotFoundException e)
        {
            UIHelper.showErrorMessage (parent,
                                       "File: " + fileName + " is not found",
                                       "reading material file " + fileName);
        }
	    catch (IOException e)
        {
            UIHelper.showException (parent, e,
                                    "reading material file " + fileName);
        }
    }

	class SymWindow extends WindowAdapter
	{
		public void windowClosing (WindowEvent event)
		{
            setVisible (false);
		}
	}

	class SymMouse extends MouseAdapter
	{
		public void mousePressed(MouseEvent event)
		{
			Object object = event.getSource ();
			if (object == OKAY)
                setVisible (false);
		}
	}
}
