/* HISTORY

11-May-99 I-01-36 jcn      $$1  Created.

*/

package com.ptc.jlinkdemo.custom;

/**
 * Signals that the "Values" parameter has not been created for a specified type.
 */
public class MissingCusParamValException 
extends CustomParameterTypeException {

    public MissingCusParamValException (String param_name, String custom_type)
    {
        super ("No values for parameter "+param_name+" of type "+custom_type+" found.");
    }

}
