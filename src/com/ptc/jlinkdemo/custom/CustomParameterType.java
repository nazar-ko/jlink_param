/* HISTORY

11-May-99 I-01-36 jcn      $$1  Created

*/

package com.ptc.jlinkdemo.custom;

import com.ptc.jlinkdemo.common.BaseParameterHelper;

import java.util.Vector;

/**
 * Base class for all custom parameter type rules objects.  Subclasses of this
 * type should implement all of the abstract methods needed to determine whether
 * or not to accept a certain value.
 */
public abstract class CustomParameterType {

    protected int proeType;
    protected String typeName;
    /**
     * The last exception thrown by this type.  Used to determine the reasons
     * for an invalid parameter value.
     */
    protected CustomParameterTypeException lastException = null;

    /**
     * This method checks if the provided value follows the Custom rules.
     */
    public abstract boolean checkValue (Object value);

    /**
     * This method can be used for conversion after the rules have been checked.
     * Possible applications: pass the value for conversion to date or time format, 
     * or use for localizing a parameter value.
     */
    public abstract Object commitNewValue (Object newValue);

    /**
     * Check if this CustomParameterType could be one of the five Pro/E types
     * defined in com.ptc.jlinkdemo.common.BaseParameterHelper.
     */ 
    public abstract boolean checkType (int proeType);
    
    /**
     * Tell which type of editor this parameter type prefers.  Values in 
     * com.ptc.jlinkdemo.common.ParamCellEditor.
     */
    public abstract int getTableCellEditorComponentType ();
    
    /**
     * Return the values needed to populate a special UI component.  Return null if not 
     * using a specialized editor.
     */
    public abstract Object [] getUIValues ();

    /**
     * Returns the last exception message generated.
     */
    public String getMessage ()
    {
        if (lastException == null) return null;
        else return lastException.getMessage();
    }

    /**
     * Compares two CustomParameterType objects.  Overrides java.lang.Object.
     */
    public boolean equals (Object obj)
    {
        if (super.equals (obj))
            return (true);
        if (obj == null || ! (obj instanceof CustomParameterType))
            return (false);

        CustomParameterType other = (CustomParameterType ) obj;

        if (!typeName.equalsIgnoreCase (other.getCustomTypeName()))
            return (false);

        if ( proeType != other.getProEType ())
            return (false);

        return true;
    }

    /**
     * Return the com.ptc.jlinkdemo.BaseParameterHelper Pro/E parameter type.
     */
    public int getProEType ()
    {
        return proeType;
    }

    /**
     * Return the CustomParameterType name.
     */
    public String getCustomTypeName ()
    {
        return typeName;
    }

    /**
     * Checks the type of the incoming object to see if it is acceptable.
     */
    protected void isValidType (Object newValue) 
        throws CustomParameterTypeException
    {
        if (!checkType (proeType))
            throw new UnsupportedValueTypeException (typeName, proeType);
        if (newValue == null)
            throw new NullValueSuppliedException (typeName);
       
        Class newClass = newValue.getClass();
        
        switch (proeType)
        {
            case BaseParameterHelper.STRING:
            {
                return;  //Any class may be converted to a string.
            }
            case BaseParameterHelper.NOTE:
            case BaseParameterHelper.INTEGER:
            {
                if (newValue instanceof Integer ||
                    newValue instanceof Short ||
                    newValue instanceof Byte )
                    return;
                throw new IncompatibleValueException (proeType, newValue);
            }
            case BaseParameterHelper.DOUBLE:
            {
                if (newValue instanceof Number)
                    return;
                throw new IncompatibleValueException (proeType, newValue);
            }
            case BaseParameterHelper.BOOLEAN:
            {
                if (newValue instanceof Boolean)
                    return;
                throw new IncompatibleValueException (proeType, newValue);
            }
            default:
                throw new UnsupportedValueTypeException (typeName, proeType);
        }
        
    }
        
        
    /**
     * Takes a String array and converts to an Object array, with type-checking.
     */
    protected Object [] parseStringValues (String [] array, int type)
        throws CustomParameterTypeException
    {
        switch (type)
        {
             case BaseParameterHelper.STRING:
                return array;
             case BaseParameterHelper.INTEGER:
                Integer [] int_array = new Integer [array.length];
                int ii = 0;
                try {
                    for (ii =0; ii< array.length; ii++)
                    {
                         int_array [ii]= new Integer (array [ii]);
                    }
                }
                catch (NumberFormatException e)
                {
                     throw new IncompatibleValueException (type, array[ii]);
                }
                return int_array;
             case BaseParameterHelper.DOUBLE:
                Double [] dbl_array = new Double [array.length];
                ii =0;
                try {
                    
                    for (ii =0; ii< array.length; ii++)
                    {
                         dbl_array [ii]= new Double (array [ii]);
                    }
                }
                catch (NumberFormatException e)
                {
                     throw new IncompatibleValueException (type, array[ii]);
                }
                return dbl_array;
            default:
                //Unsupported type
                throw new UnsupportedValueTypeException (typeName, type);
            }
        }

        /**
         * Takes a space-delimited String and converts to an array of String values.
         */
        protected String [] getStringArray (String inString)
        {
            inString.trim();
            int length = inString.length();
            int firstIndex = inString.indexOf (' ');
            int lastIndex = inString.lastIndexOf (' ');
            if (firstIndex == -1)
                {
                     return new String [] { inString };
                }

            Vector v = new Vector ();
            int previousIndex = 0;
            for (int i = firstIndex; i <= lastIndex; i++)
            {
                    if (inString.charAt (i) == ' ')
                    {
                        String theString = new String(inString.substring(previousIndex, i));
                        previousIndex = i+1;
                        v.addElement (theString);
                    }
            }
            String theString = new String (inString.substring (lastIndex+1, length));
            v.addElement (theString);
            
            String [] strings = new String [v.size()];
    
            v.copyInto (strings);
            
            return strings;
        }

        protected static void printMsg(String str)
        {
            System.out.println (str);
        }
}

