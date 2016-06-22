/* HISTORY

11-May-99 I-01-36 jcn      $$1  Created

*/
package com.ptc.jlinkdemo.custom;

import com.ptc.jlinkdemo.common.BaseParameterHelper;

/**
 * Signals that a Pro/E parameter of an certain type has been associated 
 * with a CustomParameterType which is not compatible with that type.
 */
public final class UnsupportedValueTypeException 
    extends CustomParameterTypeException {

    private int type;

    public UnsupportedValueTypeException (String typeName, int type)
    {
         super ("The parameter type "+BaseParameterHelper.getTypeNameForType (type)+" is not supported by the custom parameter type "+typeName+".");
         this.type = type;
    }
}
