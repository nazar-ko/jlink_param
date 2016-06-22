package com.ptc.jlinkdemo.material;

/*****************************************************************************\

MaterialSelector.java
MaterialSelector demo - J.Link startup class.

07-Apr-99 klm      $$1  Extracted from MaterialSelector.java

\*****************************************************************************/

import com.ptc.cipjava.*;
import com.ptc.pfc.pfcSession.Session;
import com.ptc.pfc.pfcCommand.DefaultUICommandActionListener;
import com.ptc.pfc.pfcPart.Part;

import java.awt.Frame;

class MaterialCommandListener extends DefaultUICommandActionListener
{
    Session session;

    public MaterialCommandListener (Session session)
    {
        this.session = session;
    }

    // Creates and shows a new material dialog
    public void OnCommand () throws jxthrowable
    {
        MaterialSelector.printMsg ("Registered button press...");

	    com.ptc.pfc.pfcWindow.Window curWindow = session.GetCurrentWindow ();
	    if (curWindow == null)
        {
            printMsg ("no current window");
            return;
        }

        com.ptc.pfc.pfcModel.Model model = curWindow.GetModel ();
        if (model == null)
        {
            printMsg ("no model in current window");
            return;
        }

        if (! (model instanceof Part))
        {
            printMsg ("current model is not a part");
            return;
        }

        MaterialDialog myDlg = new MaterialDialog (new Frame(),
                                                   session, (Part) model);
        myDlg.setVisible (true);
    }

    //=========================================================================
    public static void printMsg (String msg)
    {
        System.out.println ("MaterialCommandListener: " + msg);
    }
}
