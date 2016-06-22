/* HISTORY

13-Apr-99 I-01-36 jcn      $$1  Created.
28-Jul-99 I-02-07 MSH	   $$2  Fixed the Range Increment bug.

*/


package com.ptc.jlinkdemo.custom;

import com.ptc.jlinkdemo.common.BaseParameterHelper;

/**
 * This class creates instances of CustomParameterType objects.  New Custom types must be
 * added to its static methods so that the Parameter Editor knows which objects to create.
 */
public class CustomParameterTypeCreator {


    //Replicate the Pro/E types here:
    public static final int STRING = BaseParameterHelper.STRING;
    public static final int INTEGER  = BaseParameterHelper.INTEGER;
    public static final int DOUBLE = BaseParameterHelper.DOUBLE;
    public static final int BOOLEAN = BaseParameterHelper.BOOLEAN;
    public static final int NOTE = BaseParameterHelper.NOTE;

/**
 * Add your own default CustomParameterType values here.  Currently defines: "LIST_LR", with values of "LEFT or RIGHT",
 * "LIST_RELEASE_LEVEL", with the release levels used in the Release Checking Demo, "RANGE_POSITIVE_INTS", with a minimum
 * of 0 and a mximum of Java's maximum integer value, and "RANGE_INC_FOURTHS", which must be multipes of 1/4, between 0.0 and 1.0.
 */
    public static CustomParameterType defaultCustomTypeFromString (String typeName)
    {
        try {
            if (typeName.equalsIgnoreCase ("LIST_LR") || typeName.equalsIgnoreCase ("LR"))
                {
                      return new ListParameterType ("LIST_LR", "LEFT RIGHT", STRING);
                }
            if (typeName.equalsIgnoreCase ("LIST_RELEASE_LEVEL"))
                {
                      return new ListParameterType ("LIST_RELEASE_LEVEL", "INITIAL PROTOTYPE PREPRODUCTION PRODUCTION RELEASED LEGACY OBSOLETE", STRING);
                }
            if (typeName.equalsIgnoreCase ("RANGE_POSITIVE_INTS"))
                {
                      return new RangeParameterType ("RANGE_POSITIVE_INTS", "0 "+String.valueOf(Integer.MAX_VALUE), INTEGER);
                }
            if (typeName.equalsIgnoreCase ("RANGE_INC_FOURTHS"))
                {
                      return new RangeIncrementalParameterType ("RANGE_INC_FOURTHS", "0.0 1.0 0.25", DOUBLE);
                }
        }
        catch (CustomParameterTypeException ce)
        {
            return new InvalidParameterType (typeName, ce);
        }
        return (null);
    }
    /**
     * Add your own CustomParameterTypes here.  This method expects that the values which define the type are supplied by the
     * values parameter value.  The third argument should match the Pro/E parameter type being constrained.  Currently defines
     * "LIST", "RANGE", and "RANGE_INC".
     */
    public static CustomParameterType createCustomType (String typeName, String values, int type)
    {
        try {
            if (typeName.startsWith("LIST") || typeName.startsWith ("list"))
            {
                return new ListParameterType (typeName, values, type);
            }
            if (typeName.startsWith("RANGE_INC") || typeName.startsWith ("range_inc"))
            {
                return new RangeIncrementalParameterType (typeName, values, type);
            }
            if (typeName.startsWith("RANGE") || typeName.startsWith ("range"))
            {
                return new RangeParameterType (typeName, values, type);
            }
            return (null);
        }
        catch (CustomParameterTypeException ce)
        {
            return new InvalidParameterType (typeName, ce);
        }
    }
}
