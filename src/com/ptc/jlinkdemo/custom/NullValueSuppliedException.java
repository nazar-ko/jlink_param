/* HISTORY

11-May-99 I-01-36 jcn      $$1  Created

*/

package com.ptc.jlinkdemo.custom;

/**
 *  Signals that null has been passed to the CustomParameterType for evaluation.
 */
public class NullValueSuppliedException extends CustomParameterTypeException
{
    public NullValueSuppliedException (String typeName)
    {
        super ("A null value was given to the custom type "+typeName);
    }
}