/* HISTORY

11-May-99 I-01-36 jcn      $$1  Created

*/
package com.ptc.jlinkdemo.custom;

/**
 * Signals that the range definition contains an error; typically, the minimum
 * value is greater than the maximum.
 */
public class IncompatibleRangeException extends CustomParameterTypeException
{
    public IncompatibleRangeException (String typeName, double min, double max)
    {
        super ("The minimum value "+min+" and the maximum "+max+" are not compatible in custom type "+typeName);
    }
}
