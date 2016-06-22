/* HISTORY

11-May-99 I-01-36 jcn      $$1  Created

*/

package com.ptc.jlinkdemo.custom;

/**
 * Signals that a value does not fall on the specified increment.
 */
public class NotOnIncrementException extends CustomParameterTypeException
{
    public NotOnIncrementException (String typeName, Object value, int min, int inc)
    {
        super ("The value "+value+" does not fall on the increment "+inc+" above the minimum "+min);
    }

    public NotOnIncrementException (String typeName, Object value, double min, double inc, double eps)
    {
        super ("The value "+value+" does not fall on the increment "+inc+" above the minimum "+min);
    }
}