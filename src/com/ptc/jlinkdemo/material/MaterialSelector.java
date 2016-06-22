package com.ptc.jlinkdemo.material;

/*****************************************************************************\

MaterialSelector.java
MaterialSelector demo - J.Link startup class.

25-Jan-99    mshmays   $$1   Created.
08-Feb-99    JCN       $$2   Renamed and removed debug messages.  Added comments
                             Added Listener inner class.
22-Feb-99    mshmays   $$3   Fixed the Material Selection.
13-Apr-99    klm       $$4   Cleanup

\*****************************************************************************/


import com.ptc.cipjava.*;
import com.ptc.pfc.pfcGlobal.*;
import com.ptc.pfc.pfcCommand.*;
import com.ptc.pfc.pfcSession.*;

public class MaterialSelector  {

    private static Session curSession;

    //=========================================================================
    public static void start ()
    {
        try
        {
            curSession = pfcGlobal.GetProESession ();
        }
        catch (jxthrowable x)
        {
            printMsg ("exception: " + x);
            x.printStackTrace ();
            System.out.println ("\n------------------------------------");
        }

        addButton ();
    }

    //=========================================================================
    public static void stop ()
    {
        printMsg ("stopped");
    }

    //=========================================================================
    public static void addButton()
    {
        MaterialCommandListener matCommandListener =
            new MaterialCommandListener (curSession);

        UICommand inputCommand = null;

        try
        {
            inputCommand =
                curSession.UICreateCommand ("MatDemo", matCommandListener);
        }
        catch (jxthrowable x)
        {
            printMsg("exception: " + x);
            x.printStackTrace ();
            System.out.println ("\n------------------------------------");
        }
        try
        {
            // Create a submenu called "User Applications"
            //   under the Applications menu
            curSession.UIAddMenu ("User Applications",
                                  "Applications.psh_util_pproc",
                                  "ootkmsg.txt", "Applications");

            // Create a button called "Material Demo"
            //   in the new submenu using the command "MatDemo"
            curSession.UIAddButton (inputCommand, "User Applications",
                                    "", "Material Demo",
                                    "click to start", "ootkmsg.txt");
        }
        catch (jxthrowable x)
        {
            printMsg ("exception: " + x);
            x.printStackTrace ();
            System.out.println ("\n------------------------------------");
        }
    }

    //=========================================================================
    public static void printMsg (String msg)
    {
        System.out.println ("MaterialSelector: " + msg);
    }
}
