/* HISTORY

11-May-99 I-01-36 jcn      $$1  Created

*/

package com.ptc.jlinkdemo.custom;

/**
 * Signals that the "Values" parameter does not contain enough values for the specified type.
 */
public class NotEnoughValsSuppliedException extends CustomParameterTypeException
{
    public NotEnoughValsSuppliedException (String typeName, int minimum, int supplied)
    {
        super ("Not enough values ("+supplied+", minimum is "+minimum+") supplied to custom type "+typeName);
    }
}
