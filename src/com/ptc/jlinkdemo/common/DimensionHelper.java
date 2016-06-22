/* HISTORY

13-Apr-99 TK-2-01 klm      $$1  Created

*/

package com.ptc.jlinkdemo.common;

import com.ptc.cipjava.jxthrowable;
import com.ptc.pfc.pfcDimension.Dimension;

public class DimensionHelper extends BaseProeParameterHelper
{
    protected void determineProeName ()
    {
        try
        {
            proeName = ((Dimension) p).GetName ();
            if (proeName == null)
            {
                // printMsg ("GetName() returned null");
                proeName = "dim_" + ((Dimension) p).GetId ();
            }
        }
        catch (jxthrowable x)
        {
            UIHelper.showException (x, "getting dimension name");
        }
    }

    public DimensionHelper (Dimension d)
    {
        super (d);
        determineProeName ();
    }

    //=========================================================================
    private static void printMsg (String msg)
    {
        System.out.println ("DimensionHelper: " + msg);
    }
}
