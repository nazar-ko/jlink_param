/* HISTORY

13-Apr-99 TK-2-01 klm      $$1  Created
11-May-99 I-01-36 jcn      $$2  Added Custom parameter type implementation.
19-Nov-03 K-01-18 JCN      $$3  Replaced deprecated method usage.

*/

package com.ptc.jlinkdemo.common;

import com.ptc.jlinkdemo.custom.*;

import java.util.Hashtable;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;

import com.ptc.cipjava.jxthrowable;

public abstract class BaseParameterHelper
{
    public static final int UNDEFINED = 0;
    // Pro/E predefined types
    public static final int STRING = 1;
    public static final int INTEGER = 2;
    public static final int BOOLEAN = 3;
    public static final int DOUBLE = 4;
    public static final int NOTE = 5;
    // Any custom type
    public static final int CUSTOM = 6;
    

    protected BaseParameterHelper ()
    {
    }

    /**
     * Get Pro/E parameter name.
     */
    public String getProeName ()
    {
        return (proeName);
    }

    /**
     * Get custom UI name.
     */
    public String getCustomName ()
    {
        return (customName);
    }
   
    /**
     * Set custom UI name.
     */
    public void setCustomName (String customName)
    {
        this.customName = customName;
    }

    /**
     * Helper method to get the actual name used in the UI (could be custom or Pro/E).
     */
    public String getName ()
    {
        return ((customName != null) ? customName : proeName);
    }

    /**
     * Get the custom parameter type associated with this object.
     */
    public CustomParameterType getCustomType ()
    {
        return customType;
    }
    
    /**
     * Assign a custom parameter type to this object.  Will also set 'type' = CUSTOM.
     */
    protected void setCustomType (CustomParameterType c)
    {
        customType = c;
        type = CUSTOM;
    }
    
    /**
     * Get the name associated with the custom type.
     */
    public String getCustomTypeName ()
    {
        if (customType != null)
            return customType.getCustomTypeName();
        else return null;
    }
    
    /**
     * Get the last message from the custom type.
     */
    public String getCustomTypeMessage ()
    {
        if (customType == null) return null;
        return (customType.getMessage());
    }

    /**
     * Get the explanation for the invalid parameter value (formatted).
     */
    public String getInvalidMessage ()
    {
        return ("\u2219"+getName()+": "+invalidMessage);
    }
    
    /**
     * Set the explanation for the invalid parameter value.
     */
    public void setInvalidMessage (String msg)
    {
        invalidMessage = msg;
    }

    /**
     * Get the value of the parameter.
     */
    public Object getValue ()
    {
        return (value);
    }

    /**
     * Get the Pro/E parameter type for the parameter.  May not equal the result from "getType()".
     */
    public int getProeType ()
    {
        return (proeType);
    }

    /**
     * Get the type of the parameter (Pro/E or otherwise).
     */
    public int getType ()
    {
        return (type);
    }

    /**
     * Get the type name (String representation) of type.
     */
    public String getTypeName ()
    {
        return (typeName);
    }

    /**
     * Determine is the parameter is relation driven (and therefore, unmodifiable).
     */
    public boolean isRelationDriven ()
    {
        return (relationDriven);
    }
    
    /**
     * Determine if the parameter is set to the default value for its Pro/E type.
     *  String = "";
     *  Integer = 0;
     *  Double = 0.0;
     */
    public boolean isUnassigned (Object value)
    {
        Class valueClass = value.getClass();
        
        if (type == CUSTOM) return (false);  //Assume that the custom type does the checking.
        if (valueClass == Integer.class)
        {
            if (((Integer) value).intValue () == 0)
                return (true);
        }
        else if (valueClass == Double.class)
        {
            if (((Double) value).doubleValue () == 0)
                return (true);
        }
        else if (valueClass == String.class)
        {
            if (((String) value).equals (""))
                return (true);
        }
        return (false);
    }
    
    /**
     *  Set the value of the parameter to a new value. Return value: true, if parameter value was changed.
     *  Includes type checking, relation driven check, and custom parameter type check.
     */
    public boolean setValue (Object newValue)
    {
        if (newValue == null)
            return (false);
        if (type == UNDEFINED)
            return (false);
        if (relationDriven)
            return (false);

        Class newClass = newValue.getClass ();
        int newType;
        
        if (newClass == String.class)
            newType = STRING;
        else if (newClass == Integer.class)
            newType = (type == NOTE) ? NOTE : INTEGER;
        else if (newClass == Boolean.class)
            newType = BOOLEAN;
        else if (newClass == Double.class)
            newType = DOUBLE;
        else
        {
            newValue = newValue.toString ();
            newClass = String.class;
            newType = STRING;
        }
        if (type == CUSTOM)
        {
           boolean ok = customType.checkValue (newValue);
           if (!ok) return (false);
        }
        else if (type != newType)
            return (false);

        return (commitNewValue (newValue));
    }

    /**
     * Overridden in children to force the dimension to be set in Pro/E or elsewhere.
     */
    protected boolean commitNewValue (Object newValue)
    {
        value = newValue;
        return (true);
    }

    /**
     * Overrides java.lang.Object.
     */
    public String toString ()
    {
        return (valueToString (value, proeType));
    }

    /**
     * Overriders java.lang.Object.
     */
    public boolean equals (Object obj)
    {
        if (super.equals (obj))
            return (true);
        if (obj == null || ! (obj instanceof BaseParameterHelper))
            return (false);

        BaseParameterHelper other = (BaseParameterHelper) obj;

        if (proeType != other.getProeType ())
            return (false);

        if (type == CUSTOM)
        {
            if (customType != null)
            {
                if (!customType.equals(other.getCustomType()))
                    return (false);
            }
            else 
            {
                if (other.getCustomType() != null)
                    return (false);
            }
        }

        Object otherValue = other.getValue ();

        if (otherValue == null || value == null)
            return (otherValue == value);
        
            
        if (proeType == STRING)
            return (((String) value).equalsIgnoreCase (((String) otherValue)));
        else
            return (value.equals (otherValue));
    }

    public void refreshValue ()
    {
    }

    public boolean loadFromProps (Properties props)
    {
        String strValue = props.getProperty (getProeName ());
        if (strValue == null)
            return (false);
        Object newValue = valueFromString (strValue, getProeType ());
        if (newValue == null)
            return (false);
        
        return (setValue (newValue));
    }

    public boolean saveToProps (Properties props)
    {
        if (value == null || type == UNDEFINED)
            return (false);

        props.put (proeName, toString ());
        return (true);
    }

    /**
      * Return the string corresponding to a particular type.
      */
    public static String getTypeNameForType (int valueType)
    {
        final String [] typeNames = {
            "undefined", "string", "integer", "yes/no", "real", "note", "custom"
            };

      

        if (valueType >= 0 && valueType < typeNames.length)
            return (typeNames [valueType]);
        else
        {
            printMsg ("getTypeName: unknown type: " + valueType);
            return (null);
        }
    }

/**
      * Helper method to convert from string.
      */
    public static Object valueFromString (String strValue, int valueType)
    {
        final String [] trueValues  = { "true",  "yes", "y" };
        final String [] falseValues = { "false", "no",  "n" };

        if (strValue == null)
            return (null);
        switch (valueType)
        {
        case CUSTOM:
            return null;

        case STRING:
            return (strValue);

        case INTEGER:
            try
            {
                return (new Integer (strValue.trim ()));
            }
            catch (NumberFormatException x)
            {
                return (null);
            }

        case BOOLEAN:
        {
            String trimStrValue = strValue.trim ();
            for (int iBool = 0; iBool < trueValues.length; ++iBool)
            {
                if (trimStrValue.equalsIgnoreCase (trueValues [iBool]))
                    return (new Boolean (true));
                else if (trimStrValue.equalsIgnoreCase (falseValues [iBool]))
                    return (new Boolean (false));
            }
            printMsg ("valueFromString: invalid boolean string: " +
                      trimStrValue);
            return (null);
        }
        case DOUBLE:
            try
            {
                return (new Double (strValue.trim ()));
            }
            catch (NumberFormatException x)
            {
                return (null);
            }

        case NOTE:
            try
            {
                return (new Integer (strValue.trim ()));
            }
            catch (NumberFormatException x)
            {
                return (null);
            }

        default:
            printMsg ("valueFromString: unknown parameter type: " + valueType +
                      " value: \"" + strValue + "\"");
            return (null);
        }
    }
  /**
      * Helper method to convert to string
      */
    public static String valueToString (Object value, int valueType)
    {
        if (value == null)
            return (null);

        switch (valueType)
        {
        case UNDEFINED:
            return (null);

        case CUSTOM:
            return value.toString ();

        case STRING:
            return ((String) value);

        case INTEGER:
        case DOUBLE:
        case NOTE:
            return (value.toString ());

        case BOOLEAN:
            return (((Boolean) value).booleanValue () ? "yes" : "no");

        default:
            printMsg ("valueToString: unknown parameter type: " + valueType);
            return (null);
        }
    }

    /**
     * Compare two parameter sets.
     */
    public static boolean equalsParameterSets (BaseParameterHelper [] params1,
                                               BaseParameterHelper [] params2)
    {
        if (params1.length != params2.length)
            return (false);

        Hashtable table2 = paramSetToHashtable (params2);

        // Some may have had the same Pro/E name
        if (params1.length != table2.size ())
            return (false);

        for (int iParam = 0; iParam < params1.length; ++iParam)
        {
            BaseParameterHelper param1 = params1 [iParam];
            BaseParameterHelper param2 =
                (BaseParameterHelper) table2.get (param1.getProeName ());

            if (! param1.equals (param2))
                return (false);
        }

        return (true);
    }

// NOTE: The methods below are not needed to run the J-Link parameter editor.

    /**
     * Print a parameter set.
     */
    public static void printParameterSet (String heading,
                                          BaseParameterHelper [] params)
    {
        if (heading != null)
            System.out.println (heading);
        for (int iParam = 0; iParam < params.length; ++iParam)
        {
            BaseParameterHelper ph = params [iParam];
            if (ph == null)
                System.out.println ("  null");
            else
            {
                System.out.print ("  " + ph.getName ());
                if (ph.getCustomName () != null)
                    System.out.print ("[" + ph.getProeName () + "]");
                System.out.println (":" + ph.getTypeName () + " = " + ph);
            }
        }
    }

    protected static Hashtable paramSetToHashtable (BaseParameterHelper [] phs)
    {
        Hashtable result = new Hashtable ();
        for (int iParam = 0; iParam < phs.length; ++iParam)
        {
            result.put (phs [iParam].getProeName (), phs [iParam]);
        }
        return (result);
    }

    public static boolean loadParamsFromFile (BaseParameterHelper [] pHelpers,
                                              String fileName)
    {
        Properties props = new Properties ();
        try
        {
            FileInputStream fileStream = new FileInputStream (fileName);
            props.load (fileStream);
            fileStream.close ();
        }
        catch (FileNotFoundException x)
        {
            UIHelper.showErrorMessage ("File not found: " + fileName,
                                       "reading parameters from " + fileName);
        }
        catch (IOException x)
        {
            UIHelper.showException(x, "reading parameters from " + fileName);
            return (false);
        }

        StringBuffer badParams = new StringBuffer ();
        int nBadParams = 0;
        for (int iParam = 0; iParam < pHelpers.length; ++iParam)
        {
            if (! pHelpers [iParam].loadFromProps (props))
            {
                if (nBadParams > 0)
                    badParams.append ('\n');
                badParams.append (pHelpers [iParam].getName ());
                ++nBadParams;
            }
        }
        if (nBadParams > 0)
        {
            UIHelper.showErrorMessage (
                "The following parameters were not read:\n" + badParams,
                "reading parameters from " + fileName);
        }

        return (true);
    }

    public static boolean saveParamsToFile (BaseParameterHelper [] pHelpers,
                                            String fileName)
    {
        Properties props = new Properties ();
        StringBuffer badParams = new StringBuffer ();
        int nBadParams = 0;
        for (int iParam = 0; iParam < pHelpers.length; ++iParam)
        {
            if (! pHelpers [iParam].saveToProps (props))
            {
                if (nBadParams > 0)
                    badParams.append ('\n');
                badParams.append (pHelpers [iParam].getName ());
                ++nBadParams;
            }
        }

        try
        {
            FileOutputStream fileStream = new FileOutputStream (fileName);
            props.store (fileStream, "Configuration properties");
            fileStream.close ();
        }
        catch (IOException x)
        {
            UIHelper.showException(x, "writing parameters to " + fileName);
            return (false);
        }

        if (nBadParams > 0)
        {
            UIHelper.showErrorMessage (
                "The following parameters were not written:\n" + badParams,
                "writing parameters to " + fileName);
        }

        return (true);
    }

    //=========================================================================
    private static void printMsg (String msg)
    {
        System.out.println ("BaseParameterHelper: " + msg);
    }

    //=========================================================================
    protected String            proeName = null;
    protected String            customName = null;
    protected int               proeType = UNDEFINED;
    protected int               type = UNDEFINED;
    protected String            typeName = null;
    protected Object            value = null;
    protected boolean           relationDriven = false;
    protected CustomParameterType  customType = null;
    private String              invalidMessage = null;
}
