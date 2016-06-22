/* HISTORY

11-May-99 I-01-36 jcn      $$1  Created

*/

package com.ptc.jlinkdemo.custom;

/**
 * This exception class is used to signal a problem with a CustomParameterType.
 * Subclass this exception to create the message displayed in the "Info" dialog
 * when a parameter is set to an invalid value.
 */
public class CustomParameterTypeException extends Exception
{
    /** Creates an exception with a String message.  Call getMessage() (inherited
     *  from java.lang.Throwable) to get the message for a particular exception.
     */
    public CustomParameterTypeException (String s)
    {
        super (s);
    }
}