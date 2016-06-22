/* HISTORY

13-Apr-99 TK-2-01 klm      $$1  Created
11-May-99 I-01-36 jcn      $$2  Added CustomParameterType implementation.
23-Jul-99 I-02-07 msh      $$3  Change to MissingCusParamValException for length
				purposes.
*/

package com.ptc.jlinkdemo.common;

import com.ptc.jlinkdemo.custom.*;

import com.ptc.cipjava.jxthrowable;
import com.ptc.pfc.pfcModelItem.*;

/**
 * Helper for Pro/E parameters.
 */
public class ParameterHelper extends BaseProeParameterHelper
{
        /**
         * Create a Pro/E parameter helper.
         */
    public ParameterHelper (Parameter p)
    {
        super (p);
        determineProeName ();
    }
    /**
     * Create a custom parameter helper.
     */
    public ParameterHelper (Parameter p, CustomParameterType customType)
    {
        super (p, customType);
        determineProeName ();
    }

    /**
     * Get the Pro/E name from a parameter.
     */
    protected void determineProeName ()
    {
        try
        {
            proeName = ((Parameter) p).GetName ();
        }
        catch (jxthrowable x)
        {
            UIHelper.showException (x, "getting parameter name");
        }
    }

    /**
     * Create a ParameterHelper from a String name.
     */
    public static ParameterHelper create(ParameterOwner owner, String paramName)
    {
        try
        {
            return (create (owner, owner.GetParam (paramName)));
        }
        catch (jxthrowable x)
        {
            UIHelper.showException (x, "obtaining parameter info");
            return (null);
        }
    }

    /**
     * Create a parameter helper from a Pro/E parameter.
     */
    public static ParameterHelper create (ParameterOwner owner, Parameter param)
    {
        if (param == null)
        {
            printMsg ("create: param is null");
            return (null);
        }
        try
        {
            String paramName = param.GetName ();

            // Check if a special parameter
            if (paramName.endsWith ("_TYPE") || paramName.endsWith ("_UINAME") || paramName.endsWith ("_VALUES"))
            {
                // printMsg ("create: special parameter " + paramName);
                return (null);
            }

            // Get special parameters corresponding to param
            Parameter nameParam = owner.GetParam (paramName + "_UINAME");
            Parameter typeParam = owner.GetParam (paramName + "_TYPE");
            Parameter valuesParam = owner.GetParam (paramName + "_VALUES");

            // See if there is custom name
            String customName = (nameParam == null) ? null :
                                nameParam.GetValue ().GetStringValue ().trim ();
            if (customName != null && customName.length () == 0)
            {
                printMsg ("create: invalid custom name");
                // ParameterHelper pHelper = new ParameterHelper (nameParam);
                // printMsg ("  name: " + pHelper.getName ());
                // printMsg ("  type: " + pHelper.getTypeName ());
                // printMsg ("  value: " + pHelper.getValue ());
                return (null);
            }

            // See if there is custom type
            CustomParameterType customParameterType = null;
            if (typeParam != null)
            {
                customParameterType = CustomParameterTypeCreator.defaultCustomTypeFromString (
                                 typeParam.GetValue ().GetStringValue ());
                if (customParameterType == null)
                {
                    try {
                        // Problem: No values to assign to the custom parameter type
                        if (valuesParam == null)
                            throw new MissingCusParamValException (param.GetName(), typeParam.GetValue().GetStringValue());
                    
                        // Not a default parameter type
                        // Create a new one based on values
                       
                        customParameterType = CustomParameterTypeCreator.createCustomType (typeParam.GetValue ().GetStringValue ()+"_"+param.GetName(),
                                                    valuesParam.GetValue().GetStringValue(), 
                                                    determineProeType (param.GetValue().Getdiscr()));
                        
                    }
                    catch (MissingCusParamValException me)
                    {                       
                       customParameterType = new InvalidParameterType (typeParam.GetValue().GetStringValue()+"_"+param.GetName(), me);
                    }
                }
            }
           
           
            ParameterHelper result;
            if (typeParam == null)
                result = new ParameterHelper (param);
            else if (customParameterType == null)
                return null;  //Should have been a custom type but something is wrong. Don't show anything.
            else
                result = new ParameterHelper (param, customParameterType);
            if (customName != null)
                result.setCustomName (customName);

            return (result);
        }
        catch (jxthrowable x)
        {
            printMsg ("create: exception");
            UIHelper.showException (x, "obtaining parameter info");
            return (null);
        }
    }

    //=========================================================================
    private static void printMsg (String msg)
    {
        System.out.println ("ParameterHelper: " + msg);
    }
}
