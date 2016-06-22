/* HISTORY

11-May-99 I-01-36 jcn      $$1  Created

*/
package com.ptc.jlinkdemo.custom;

import com.ptc.jlinkdemo.common.BaseParameterHelper;

/**
 * Signals that a value provided to a CustomParameterType is not acceptable 
 * for the Pro/E Parameter which is associated with it.
 */
public final class IncompatibleValueException
    extends CustomParameterTypeException {

    private int type;
    private Object value;

    public IncompatibleValueException (int type, Object value)
    {
        super ("Provided value "+value+" is not compatible with parameter type "+BaseParameterHelper.getTypeNameForType (type));
        this.type = type;
        this.value = value;
    }
}