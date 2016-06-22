/* HISTORY

11-May-99 I-01-36 jcn      $$1  Created

*/

package com.ptc.jlinkdemo.custom;


import com.ptc.jlinkdemo.common.BaseParameterHelper;
import com.ptc.jlinkdemo.common.ParamCellEditor;

/**
 * This custom parameter type is created when the program tries, and fails, to
 * create another custom type based on the parameters available in the model.
 * This type stores the error condition received as a reason for invalidating the
 * parameter.
 */
public class InvalidParameterType extends CustomParameterType  {

    /**
     * Implements CustomParameterType. Returns false.
     */   
    public boolean checkValue (Object newValue)
    {
        return (false);
    }
    
    /**
     * Implements CustomParameterType.
     */
    public Object commitNewValue (Object newValue)
    {
        return (newValue);
    }

    /**
     * Implements CustomParameterType. This type is valid for all Pro/E types.
     */
    public boolean checkType (int type)
    {
        return (true);
    }

    /**
     * Implements CustomParameterType.  Returns "Not Editable".
     */
    public int getTableCellEditorComponentType ()
    {
        return ParamCellEditor.NOT_EDITABLE;
    }

    /**
     * Implements CustomParameterType.
     */
    public Object [] getUIValues ()
    {
        return null;
    }

    /**
     * Create a new InvalidParameterType, with a type name and the exceptional
     * condition which occurred.
     */
    public InvalidParameterType (String typeName, CustomParameterTypeException exception)
    {
        this.typeName = typeName;
        this.lastException = exception;
    }
}