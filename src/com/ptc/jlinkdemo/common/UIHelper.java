/* HISTORY

13-Apr-99 TK-2-01 klm      $$1  Created
13-May-99 I-01-36 jcn      $$2  Added CustomParameterType explanation window.

*/

package com.ptc.jlinkdemo.common;

import java.io.StringWriter;
import java.io.PrintWriter;
import java.io.File;

import java.awt.Component;
import java.awt.Window;
import java.awt.Dimension;
import java.awt.Point;

import javax.swing.UIManager;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import com.ptc.cipjava.jxthrowable;
import com.ptc.pfc.pfcExceptions.*;

public abstract class UIHelper
{
    //=========================================================================
    public static void setLookAndFeel ()
    {
        // Prefer platform-native look&feel
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception xnative)
        {
            printMsg ("Failed to set platform native look&feel. " +
                      "Trying cross-platform look&feel.");
            try
            {
                UIManager.setLookAndFeel (
                    UIManager.getCrossPlatformLookAndFeelClassName());
            }
            catch (Exception xmetal)
            {
                printMsg ("Failed to set Java native look&feel. " +
                          "Using default look&feel.");
            }
        }
    }

    //=========================================================================
    public static void centerWindow (Window window)
    {
        Dimension scrSize = window.getToolkit ().getScreenSize ();
        Dimension wndSize = window.getSize ();
        Point centerPos = new Point ((scrSize.width - wndSize.width) / 2,
                                     (scrSize.height - wndSize.height) / 2);
        window.setLocation (centerPos);
    }

    //=========================================================================
    public static void showErrorMessage (Component parent, String msg,
                                         String where)
    {
        String title = "Error";
        if (where != null)
            title = title + " " + where;
        JOptionPane.showMessageDialog (parent, msg, title, 
                                       JOptionPane.ERROR_MESSAGE);
    }

    public static void showErrorMessage (String msg, String where)
    {
        showErrorMessage (null, msg, where);
    }

    public static void showErrorMessage (String msg)
    {
        showErrorMessage (null, msg, null);
    }

    public static void showWarningMessages (Component parent, String [] msgs,
                                            String where)
    {
        String title = "Warning";
        if (where != null)
            title = title+" " +where;
        JOptionPane.showMessageDialog (parent, msgs, title, JOptionPane.WARNING_MESSAGE);
    }
     
    public static void showWarningMessages (String [] msgs, String where)
    {
        showWarningMessages (null, msgs, where);
    }
    
    public static void showWarningMessages (String [] msgs)
    {
        showWarningMessages (null, msgs, null);
    }
       
  

    //=========================================================================
    public static void showException (Component parent,
                                      Throwable x, String where)
    {
        String msg = x.toString ();
        StringWriter stackWriter = new StringWriter ();
        x.printStackTrace (new PrintWriter (stackWriter));

        String title = "Exception";
        if (where != null)
            title = title + " " + where;

        if (x instanceof XPFC)
        {
            try
            {
                String newMsg = null;
                if (x instanceof XInAMethod)
                {
                    newMsg = "Exception in PFC method " +
                          ((XInAMethod) x).GetMethodName ();
                    if (x instanceof XToolkitError)
                    {
                        newMsg = newMsg + "\nPro/TK function: " +
                            ((XToolkitError) x).GetToolkitFunctionName () +
                            "\nPro/TK error code: " +
                            ((XToolkitError) x).GetErrorCode ();
                    }
                    else if (x instanceof XBadArgument)
                    {
                        newMsg = newMsg + "\nBad argument: " +
                            ((XBadArgument) x).GetArgumentName ();
                    }
                    else
                    {
                        newMsg = newMsg + "\nType: " + x.getClass ().getName ();
                    }
                }

                if (newMsg != null)
                    msg = newMsg;
            }
            catch (jxthrowable xBummer)
            {
                ; // Just ignore it and use default message
                printMsg ("exception in showException");
                xBummer.printStackTrace ();
            }
        }

        
        JOptionPane msgPane =
            new JOptionPane (msg + "\n***\nStack Trace:\n" + stackWriter,
                             JOptionPane.ERROR_MESSAGE);
        JDialog msgDlg = msgPane.createDialog (parent, title);
        msgDlg.pack ();
        msgDlg.show ();
        
        //JOptionPane.showMessageDialog (parent, msg +
        //                               "\n***\nStack Trace:\n" + stackWriter,
        //                               title, JOptionPane.ERROR_MESSAGE);
    }

    public static void showException (Throwable x, String where)
    {
        showException (null, x, where);
    }

    public static void showException (Throwable x)
    {
        showException (null, x, null);
    }

    //=========================================================================
    public static boolean loadParamsFromFile (Component parent,
                                              BaseParameterHelper [] pHelpers)
    {
        JFileChooser chooser = createParamFileChooser (parent);

        int result = chooser.showOpenDialog (parent);
        if (result == JFileChooser.APPROVE_OPTION)
        {
            File file = chooser.getSelectedFile ();
            // printMsg ("accepted file: " + file.getPath ());
            return (BaseParameterHelper.loadParamsFromFile (pHelpers,
                                                            file.getPath ()));
        }
        else
        {
            // printMsg ("canceled file");
            return (false);
        }
    }

    public static boolean saveParamsToFile (Component parent,
                                            BaseParameterHelper [] pHelpers)
    {
        JFileChooser chooser = createParamFileChooser (parent);

        int result = chooser.showSaveDialog (parent);
        if (result == JFileChooser.APPROVE_OPTION)
        {
            File file = chooser.getSelectedFile ();
            // printMsg ("accepted file: " + file.getPath ());
            return (BaseParameterHelper.saveParamsToFile (pHelpers,
                                                          file.getPath ()));
        }
        else
        {
            // printMsg ("canceled file");
            return (false);
        }
    }

    private static JFileChooser createParamFileChooser (Component parent)
    {
        final FileFilter filter = new FileFilter () {
            public boolean accept (File f)
                { return (f.getName ().endsWith (".par")); }
            public String getDescription ()
                { return ("Parameter files (*.par)"); }
        };

        JFileChooser chooser =
            new JFileChooser (new File (new File ("").getAbsolutePath ()));
        chooser.addChoosableFileFilter (filter);
        chooser.setFileFilter (filter);

        return (chooser);
    }

    //=========================================================================
    private static void printMsg (String msg)
    {
        System.out.println ("UIHelper: " + msg);
    }
}
