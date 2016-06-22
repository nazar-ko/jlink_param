/* HISTORY

11-May-99 I-01-36 jcn      $$1  Created
23-Jul-99 I-02-07 msh	   $$2  Fixed NotEnoughValsSuppliedException for length
				purposes.	

*/
package com.ptc.jlinkdemo.custom;

import com.ptc.jlinkdemo.common.BaseParameterHelper;
import com.ptc.jlinkdemo.common.ParamCellEditor;

/**
 * Forces the value to be between (or equal to) a minimum and maximum value.
 * Valid for Integer and Double parameters.
 */
public class RangeParameterType extends CustomParameterType {

/**
 * Implements CustomParameterType.
 */
  public boolean checkValue (Object newValue)
  {
    try {
        isValidType (newValue);
        isInRange ((Number)newValue);
        lastException = null;
        return (true);
    }
    catch (CustomParameterTypeException ce)
    {
        lastException = ce;
        return (false);
    }
      
  }
 
 /**
  * Determine if the specified number is in the range.
  */
  protected void isInRange (Number newValue) throws CustomParameterTypeException
  {
    if (proeType == BaseParameterHelper.INTEGER)
    {
        int min = (int)minimum;
        int max = (int)maximum;
        
        if (newValue.intValue() < min)
            throw new OutOfRangeException (typeName, newValue, min, OutOfRangeException.TOO_LOW);
        
        if (newValue.intValue() > max)
            throw new OutOfRangeException (typeName, newValue, max, OutOfRangeException.TOO_HIGH);
            
        return;
    }
    else if (proeType == BaseParameterHelper.DOUBLE)
    {
        if (newValue.doubleValue() < minimum)
            throw new OutOfRangeException (typeName, newValue, minimum, OutOfRangeException.TOO_LOW);
    
        if (newValue.doubleValue() > maximum)
            throw new OutOfRangeException (typeName, newValue, maximum, OutOfRangeException.TOO_HIGH);
            
        return;
    }
    else 
        throw new UnsupportedValueTypeException (typeName, proeType);
  }
    
 /**
  * Implements CustomParameterType.
  */
  public Object commitNewValue (Object newValue)
  {
        //nothing to do here
        return newValue;
  }

 /**
  * Implements CustomParameterType.
  */
  public boolean checkType (int proeType)
  {
    if (proeType == BaseParameterHelper.BOOLEAN)
        return false;
    if (proeType == BaseParameterHelper.NOTE)
        return false;
    if (proeType == BaseParameterHelper.STRING)
        return false;
    return (true);
  }

 /**
  * Implements custom parameter type.  Currently returns TEXTFIELD; conceivably
  * could a "Slider" type component instead.
  */
  public int getTableCellEditorComponentType ()
  {
    return ParamCellEditor.TEXTFIELD;
  }

 /**
  * Implements CustomParameterType (returns null).
  */
  public Object [] getUIValues ()
  {
    return null;  // This is not a "ComboBox" parameter type.
  }

 /**
  * Create a new RangeParameterType given a type name, min and max values,
  * and a Pro/E parameter type.
  */
  public RangeParameterType (String typeName, String values, int proeType)
    throws CustomParameterTypeException 
    {
    this.typeName = typeName;
    this.proeType = proeType;

    String [] stringValues;
    Object [] rangeValues;

    stringValues = getStringArray (values);
    rangeValues = parseStringValues (stringValues, proeType);

    this.setRangeValues(rangeValues); //may be overridden in children
    
    
 }

/**
 * Set the minimum and maximum values.
 */
  protected void setRangeValues (Object [] values) 
    throws CustomParameterTypeException
  {
     //Need error check here
    if (values.length < 2)
        throw new NotEnoughValsSuppliedException (typeName, 2, values.length);
    
    Number min = (Number)values [0];
    Number max = (Number)values [1];

    minimum = min.doubleValue();
    maximum = max.doubleValue();
    
    if (minimum > maximum)
        throw new IncompatibleRangeException (typeName, minimum, maximum);

  }

 /**
  * The minimum in the range.  Stored as double, but converted to int where appropriate.
  */
  public double getMinimum ()
  {
    return minimum;
  }

/**
 * The maximum in the range. Stored as double, but converted to int where appropriate.
 */
  public double getMaximum ()
  {
    return maximum;
  }

  protected double minimum;
  protected double maximum;
}





