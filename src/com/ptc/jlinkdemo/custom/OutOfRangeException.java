/* HISTORY

11-May-99 I-01-36 jcn      $$1  Created

*/
package com.ptc.jlinkdemo.custom;


/**
 * Signals that a value is out of range.
 */
public class OutOfRangeException extends CustomParameterTypeException
{
    
    public static final int TOO_LOW = 0;
    public static final int TOO_HIGH = 1;


    public OutOfRangeException (String typeName, Object value, int boundary, int outOfRangeType)
    {
        super ("The value "+value+" is "+((outOfRangeType == TOO_LOW)?"below":"above")+" the boundary value "+boundary+" .");
    }

    public OutOfRangeException (String typeName, Object value, double boundary, int outOfRangeType)
    {
        super ("The value "+value+" is "+((outOfRangeType == TOO_LOW)?"below":"above")+" the boundary value "+boundary+" .");
    }
}