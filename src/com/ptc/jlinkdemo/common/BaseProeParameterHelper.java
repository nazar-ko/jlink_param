/* HISTORY

13-Apr-99 TK-2-01 klm      $$1  Created

*/

package com.ptc.jlinkdemo.common;

import com.ptc.jlinkdemo.custom.CustomParameterType;

import com.ptc.cipjava.jxthrowable;
import com.ptc.pfc.pfcModelItem.*;

public abstract class BaseProeParameterHelper extends BaseParameterHelper
{
    public BaseProeParameterHelper (BaseParameter p)
    {
        try
        {
            this.p = p;
            // proeName = p.GetName ();
            paramValue = p.GetValue ();

            determineProeType ();
            type = proeType;
            typeName = getTypeNameForType (type);

            refreshValue ();
        }
        catch (jxthrowable x)
        {
            UIHelper.showException (x, "getting parameter info");
        }
    }

    public BaseProeParameterHelper (BaseParameter p, CustomParameterType c)
    {
        try
        {
            this.p = p;
            // proeName = p.GetName ();
            paramValue = p.GetValue ();

            // Verify if customType is valid for this attribute
            determineProeType ();
            setCustomType (c);
            typeName = getTypeNameForType (type);

            refreshValue ();
        }
        catch (jxthrowable x)
        {
            UIHelper.showException (x, "getting parameter info");
        }
    }

    // Must be overridden and called from child constructor
    protected abstract void determineProeName ();

    protected boolean commitNewValue (Object newValue, int proeParamType)
    {
        try {
         switch (proeType) 
         {      
            case STRING:
                paramValue.SetStringValue ((String) newValue);
                break;

            case INTEGER:
            case NOTE:
                paramValue.SetIntValue (((Integer) newValue).intValue ());
                break;

            case BOOLEAN:
                paramValue.SetBoolValue (((Boolean) newValue).booleanValue ());
                break;

            case DOUBLE:
                paramValue.SetDoubleValue (((Double) newValue).doubleValue ());
                break;
            default:
                return (false);
         }
        }
        catch (jxthrowable x)
            {
                UIHelper.showException (x, "setting parameter value");
                refreshValue ();
                return (false);
            }
         return (true);
        }

    protected boolean commitNewValue (Object newValue)
    {
        try
        {
            switch (type)
            {
            case STRING:
            case INTEGER:
            case DOUBLE:
            case BOOLEAN:
            case NOTE:
                {
                   boolean checkValue = commitNewValue (newValue, type);
                   if (!checkValue) return (false);
                   break;
                }
            case CUSTOM:
                {
                    boolean checkValue = commitNewValue (newValue, getProeType ());
                    if (!checkValue) return (false);
                    break;
                }
            default:
                printMsg ("unknown parameter type: " + type);
                return (false);
            }

            p.SetValue (paramValue);
            return (super.commitNewValue (newValue));
        }
        catch (jxthrowable x)
             {
                UIHelper.showException (x, "setting parameter value");
                refreshValue ();
                return (false);
            }
        
    }

    public void refreshValue (int proEParamType)
    {
        try {
        switch (proEParamType)
            {
            case UNDEFINED:
            case CUSTOM:
                value = null;
                break;

            case STRING:
                value = paramValue.GetStringValue ();
                break;

            case INTEGER:
                value = new Integer (paramValue.GetIntValue ());
                break;

            case BOOLEAN:
                value = new Boolean (paramValue.GetBoolValue ());
                break;

            case DOUBLE:
                value = new Double (paramValue.GetDoubleValue ());
                break;
                
            case NOTE:
                value = new Integer (paramValue.GetNoteId ());
                break;
                
            default:
                printMsg ("unknown parameter type: " + type);
                return;   
            }
            relationDriven = false; // UNIMPLEMENTED p.GetIsRelationDriven ();
        }
        catch (jxthrowable x)
        {
            UIHelper.showException (x, "getting parameter value");
        }
    }
                

    public void refreshValue ()
    {
        switch (type)
        {
            case STRING:
            case INTEGER:
            case DOUBLE:
            case BOOLEAN:
            case NOTE:
                refreshValue (type);
                break;
            case CUSTOM:
                refreshValue (proeType);
                break;
            case UNDEFINED:
                value = null;
                break;
        }         
    }

    public static int determineProeType (ParamValueType type)
    {
        int proeType = UNDEFINED;
        switch (type.getValue())
            {
            case ParamValueType._PARAM_STRING:
                proeType = STRING;
                break;

            case ParamValueType._PARAM_INTEGER:
                proeType = INTEGER;
                break;

            case ParamValueType._PARAM_BOOLEAN:
                proeType = BOOLEAN;
                break;

            case ParamValueType._PARAM_DOUBLE:
                proeType = DOUBLE;
                break;

            case ParamValueType._PARAM_NOTE:
                proeType = NOTE;
                break;

            default:
                printMsg ("unknown Pro/E parameter type: " +
                          type.getValue ());
                proeType = UNDEFINED;
                break;
            } 
        return (proeType);
    }

    protected void determineProeType ()
    {
        try
        {
            proeType = determineProeType (paramValue.Getdiscr());
        }   
        catch (jxthrowable x)
        {
            UIHelper.showException (x, "getting parameter type");

            proeType = UNDEFINED;
        }
    }

    //=========================================================================
    private static void printMsg (String msg)
    {
        System.out.println ("BaseProeParameterHelper: " + msg);
    }

    //=========================================================================
    protected BaseParameter     p = null;
    protected ParamValue        paramValue = null;
}
