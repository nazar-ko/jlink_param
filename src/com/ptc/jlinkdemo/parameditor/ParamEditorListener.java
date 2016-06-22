/* HISTORY

13-May-99 I-01-36 jcn      $$1  Created from SaveListener.java

*/

package com.ptc.jlinkdemo.parameditor;

import javax.swing.JOptionPane;

import java.util.Vector;

import com.ptc.cipjava.*;
import com.ptc.pfc.pfcCommand.*;
import com.ptc.pfc.pfcSession.*;
import com.ptc.pfc.pfcModel.*;
import com.ptc.pfc.pfcModelItem.*;
import com.ptc.pfc.pfcPart.*;

import com.ptc.jlinkdemo.common.ParamWindow;
import com.ptc.jlinkdemo.common.ParameterHelper;
import com.ptc.jlinkdemo.common.UIHelper;

/**
 * Creates the UI command implmentation for the Applications/J-Link Parameter Editor
 * menu button.
 */
class ParamEditorListener extends DefaultUICommandActionListener
{
    Session session;
    /**
     * Default title = "Parameter Editor".  Reset this variable to change
     * window titles.
     */
    static String title = "Parameter Editor";

    /**
     * Pro/E constructor.
     */
    public ParamEditorListener (Session session)
    {
        this.session = session;

        //printMsg ("created");
    }

    /**
     * Overrides com.ptc.pfc.pfcCommand.UICommandActionListener.  Creates a
     * com.ptc.jlinkdemo.common.ParamWindow and populates it.
     */
    public void OnCommand ()
    {
        //printMsg ("Registered button press...");

        try
        {
            Model curModel = session.GetCurrentModel ();
            if (curModel == null)
                msgBox ("No current model");
            else
                processParameterOwner ((ParameterOwner)curModel);
        }
        catch (Throwable x)
        {
            UIHelper.showException (x, "performing release checks");
        }
    }

    /**
     * Sets up ParameterHelpers for all Pro/E parameters owned by p_owner.
     * Because this uses the ParameterOwner interface, any ParameterOwner,
     * including models, features, surfaces, and edges may be used.
     */
    public void processParameterOwner (ParameterOwner p_owner) throws jxthrowable
    {
        // Parameters
        Parameters params = p_owner.ListParams ();
        int nParams = params.getarraysize ();
        Vector pHelperVector = new Vector ();
        for (int iParam = 0; iParam < nParams; ++iParam)
        {
            Parameter param = params.get (iParam);
            ParameterHelper pHelper = ParameterHelper.create (p_owner, param);
            if (pHelper != null)
            {
                pHelperVector.addElement (pHelper);
            }
        }

        // printMsg ("Parameters:");
        ParameterHelper [] pHelpers =
            new ParameterHelper [pHelperVector.size ()];
        pHelperVector.copyInto (pHelpers);

        ParamWindow paramWnd = new ParamWindow (session, pHelpers, false, title);
        paramWnd.show ();
        paramWnd.dispose ();
    }

    //=========================================================================
    private static void printMsg (String msg)
    {
        System.out.println ("ParamEditorListener: " + msg);
    }

    private static void msgBox (String msg)
    {
        JOptionPane.showMessageDialog (new java.awt.Frame (),
                                       msg,
                                       title + " Error",
                                       JOptionPane.ERROR_MESSAGE);
    }
}
