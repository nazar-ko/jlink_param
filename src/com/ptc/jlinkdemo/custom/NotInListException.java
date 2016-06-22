/* HISTORY

11-May-99 I-01-36 jcn      $$1  Created

*/

package com.ptc.jlinkdemo.custom;

/**
 * Signals that a value is not in the list of acceptable values.
 */
class NotInListException extends CustomParameterTypeException {

    public NotInListException (String typeName, Object value, String [] list)
    {
        super ("The value "+value+" was not found in the custom list "+typeName);
    }
}