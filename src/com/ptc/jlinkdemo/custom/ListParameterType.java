/* HISTORY

11-May-99 I-01-36 jcn      $$1  Created

*/

package com.ptc.jlinkdemo.custom;

import com.ptc.jlinkdemo.common.BaseParameterHelper;
import com.ptc.jlinkdemo.common.ParamCellEditor;

/**
 * Forces the parameter value to be a member of a specified list.  Valid
 * for Strings, Integers and Doubles.
 */
public class ListParameterType extends CustomParameterType {

    private Object [] listValues;
    private String [] stringValues;
    
    /** 
     * Implements CustomParameterType.
     */
    public boolean checkValue (Object value)
    {
        try {
            isInList (value);
            lastException = null;
            return (true);
        }
        catch (CustomParameterTypeException ce)
        {
            lastException = ce;
            return (false);
        }
    }
    
    /**
     * Check if an object is a member of the stored list.
     */
    protected void isInList (Object value) throws CustomParameterTypeException
    {
        for (int ii =0; ii < listValues.length;  ii++)
        {
              if (value.equals (listValues[ii]))
              {
                    return;
              }
        }
    throw new NotInListException (typeName, value, stringValues);
    }       

    /**
     * Implements CustomParameterType.
     */
    public Object commitNewValue (Object newValue)
    {
        return newValue;
        //Nothing to do here
    }
    
    /**
     * Implements CustomParameterType.
     */
    public boolean checkType (int proeType)
    {
         if (proeType == BaseParameterHelper.BOOLEAN)
            return false;
         if (proeType == BaseParameterHelper.NOTE)
            return false;

         return true;
    }
    
    /**
     * Implements CustomParameterType.
     */
    public int getTableCellEditorComponentType ()
    {
         return ParamCellEditor.COMBOBOX;
    }
    
    /**
     * Implements CustomParameterType.
     */
    public Object [] getUIValues ()
    {
        return stringValues;
    }

    /**
     * Creates a new ListParameterType object, given a type name, a space-delimited 
     * String, and a Pro/E parameter type (from com.ptc.jlinkdemo.common.BaseParameterHelper.
     */
    public ListParameterType (String typeName, String list, int type)
        throws CustomParameterTypeException
    {
       this.typeName = typeName;
       this.proeType = type;
       stringValues = getStringArray (list);
       listValues = parseStringValues (stringValues, type);
    }
    
}

