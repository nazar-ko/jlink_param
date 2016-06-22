/* HISTORY

13-Apr-99 TK-2-01 klm      $$1  Created
14-May-99 I-01-36 jcn      $$2  Moved to com.ptc.jlinkdemo.common.

*/

package com.ptc.jlinkdemo.common;

public class UnassignedParamTable extends ParamTable
{
    public UnassignedParamTable (BaseParameterHelper [] paramHelpers)
    {
        super (paramHelpers);
    }

    protected int checkValid (int row, int column)
    {
        Object newValue = paramModel.getNewValue (row);
        BaseParameterHelper helper = paramModel.getParameterHelper (row);
        if (newValue == null)
        {

            helper.setInvalidMessage ("The new value is null.");
            return (INVALID);
        }

        Class valueClass = newValue.getClass ();
        // printMsg ("value:" + valueClass.getName () + " = " + value);

        if (helper.isUnassigned (newValue))
        {
            helper.setInvalidMessage ("The parameter value is set to the default for that type.");
            return (INVALID);
        }

        return super.checkValid (row, column);
    }

    //=========================================================================
    private static void printMsg (String msg)
    {
        System.out.println ("UnassignedParamTable: " + msg);
    }
}
