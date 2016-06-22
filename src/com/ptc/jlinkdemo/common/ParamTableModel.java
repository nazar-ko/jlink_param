/* HISTORY

13-Apr-99 TK-2-01 klm      $$1  Created
12-May-99 I-01-36 jcn      $$2  commitNewValues () does not refresh values.

*/

package com.ptc.jlinkdemo.common;

import javax.swing.table.AbstractTableModel;

import com.ptc.cipjava.*;
import com.ptc.pfc.pfcModelItem.*;

public class ParamTableModel extends AbstractTableModel
{
    protected final int PARAM_NAME_COL_IDX  = 0;
    protected final int PARAM_VALUE_COL_IDX = 1;
    protected final int TOTAL_COLS          = 2;

    public ParamTableModel (BaseParameterHelper [] paramHelpers)
    {
        pHelpers = paramHelpers;
        resetNewValues ();
    }

    public int getRowCount ()
    {
        return (pHelpers.length);
    }

    public int getColumnCount ()
    {
        return (TOTAL_COLS);
    }

    public Object getValueAt (int rowIdx, int colIdx)
    {
        // printMsg ("getValueAt(" + rowIdx + ", " + colIdx + ")");
        if (colIdx == PARAM_NAME_COL_IDX)
            return (pHelpers [rowIdx].getName ());
        else if (colIdx == PARAM_VALUE_COL_IDX)
        {
            // printMsg ("  returning param value");
            int valueType = pHelpers [rowIdx].getProeType ();
            return (BaseParameterHelper.valueToString (newValues [rowIdx],
                                                       valueType));
        }
        else
        {
            return (null);
        }
    }

    public void setValueAt (Object strValue, int rowIdx, int colIdx)
    {
        int valueType = pHelpers [rowIdx].getProeType ();
        Object value = BaseParameterHelper.valueFromString ((String)strValue,
                                                            valueType);
        if (value != null)
        {
            // printMsg ("setValueAt(" + value.getClass ().getName () +
            //           "=" + value + ", " + rowIdx + ", " + colIdx + ")");
            newValues [rowIdx] = value;
        }

        // We need to fire this event even in case of invalid input,
        // to update the cell back to original value
        fireTableCellUpdated (rowIdx, colIdx);
    }

    public boolean isCellEditable(int rowIdx, int colIdx)
    {
        if (colIdx != PARAM_VALUE_COL_IDX)
            return (false);
        return (! pHelpers [rowIdx].isRelationDriven ());
    }

    public String getColumnName (int colIdx)
    {
        return ((colIdx == PARAM_NAME_COL_IDX) ? "Parameter" :
                (colIdx == PARAM_VALUE_COL_IDX) ? "Value" : null);
    }

    public Class getColumnClass (int colIdx)
    {
        return ((colIdx == PARAM_NAME_COL_IDX) ? String.class : Object.class);
    }

    public int getParamValueColIdx ()
    {
        return (PARAM_VALUE_COL_IDX);
    }

    public int getParamNameColIdx ()
    {
        return (PARAM_NAME_COL_IDX);
    }

    public BaseParameterHelper getParameterHelper (int rowIdx)
    {
        if (pHelpers != null && rowIdx >= 0 && rowIdx < pHelpers.length)
            return (pHelpers [rowIdx]);
        else
            return (null);
    }

    public Object getNewValue (int rowIdx)
    {
        if (pHelpers != null && rowIdx >= 0 && rowIdx < pHelpers.length)
            return (newValues [rowIdx]);
        else
            return (null);
    }

    public void resetNewValues ()
    {
        newValues = new Object [pHelpers.length];
        for (int iParam = 0; iParam < pHelpers.length; ++iParam)
        {
            newValues [iParam] = pHelpers [iParam].getValue ();
        }
        fireTableDataChanged ();
    }

    public void commitNewValues ()
    {
        for (int iParam = 0; iParam < newValues.length; ++iParam)
        {
            BaseParameterHelper pHelper = pHelpers [iParam];
            /* 
            if (! pHelper.setValue (newValues [iParam]))
            {
                pHelper.refreshValue ();
                newValues [iParam] = pHelper.getValue ();
            }
            */
            if (!pHelper.setValue (newValues [iParam]))
            {
               // UIHelper.showErrorMessage ("Error: invalid value "+newValues [iParam]);
            }
            
        }
        fireTableDataChanged ();
    }

    //=========================================================================
    private static void printMsg (String msg)
    {
        System.out.println ("ParamTableModel: " + msg);
    }

    //=========================================================================
    protected BaseParameterHelper []    pHelpers;
    protected Object []                 newValues;
}
