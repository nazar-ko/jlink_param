/* HISTORY

13-Apr-99 TK-2-01 klm      $$1  Created

*/

package com.ptc.jlinkdemo.parameditor;

import com.ptc.cipjava.*;
import com.ptc.pfc.pfcGlobal.*;
import com.ptc.pfc.pfcCommand.*;
import com.ptc.pfc.pfcSession.*;

import com.ptc.jlinkdemo.common.UIHelper;

public class ParamEditor
{
    private static Session session;

    //=========================================================================
    public static void start ()
    {
        try
        {
            session = pfcGlobal.GetProESession ();
        }
        catch (jxthrowable x)
        {
            UIHelper.showException (x, "getting Pro/E session");
        }

        addButton ();
        UIHelper.setLookAndFeel ();
    }

    //=========================================================================
    public static void stop ()
    {
        printMsg ("stopped");
    }

    //=========================================================================
    private static void addButton()
    {
        try
        {
            UICommand cmdParamEditor =
                session.UICreateCommand ("ParamEditor",
                                         new ParamEditorListener (session));

			session.UIAddButton (cmdParamEditor,
								 "Applications",
								 "Applications.psh_util_pproc",
								 "JLink Parameter Editor",
								 "Edit model parameters",
								 "msg_peditor.txt");
        }
        catch (jxthrowable x)
        {
            UIHelper.showException (x, "adding release check button");
        }
    }

    //=========================================================================
    private static void printMsg (String msg)
    {
        System.out.println ("ParamEditor: " + msg);
    }
}
