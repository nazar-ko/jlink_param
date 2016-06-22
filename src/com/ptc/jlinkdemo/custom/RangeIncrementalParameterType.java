/* HISTORY

11-May-99 I-01-36 jcn      $$1  Created
23-Jul-99 I-02-07 msh	   $$2  Fixed NotEnoughValsSuppliedException for length
				purposes.  

*/

package com.ptc.jlinkdemo.custom;

import com.ptc.jlinkdemo.common.BaseParameterHelper;
import com.ptc.jlinkdemo.common.ParamCellEditor;

/**
 * Incorporates the restrictions of RangeParameterType with an added check:
 * the value must lie on an increment of a specified value above the minimum.
 */
public class RangeIncrementalParameterType extends RangeParameterType {

     /** 
     * Overrides RangeParameterType.
     */
    public boolean checkValue (Object newValue)
    {
          try {
            isValidType (newValue);
            isInRange ((Number)newValue);
            isOnIncrement ((Number)newValue);
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
     * Checks that the value is on the increment.
     */
    protected void isOnIncrement (Number newValue) 
        throws CustomParameterTypeException
    {
        if (proeType == BaseParameterHelper.INTEGER)
        {
            int min = (int)minimum;
            int inc = (int)increment;
        
            if ((newValue.intValue()-min)%inc != 0)
                throw new NotOnIncrementException (typeName, newValue, min, inc);
            
            return;
    }
    else if (proeType == BaseParameterHelper.DOUBLE)
    {
        
        if ((newValue.doubleValue()-minimum) % increment > epsilon*increment)
            throw new NotOnIncrementException (typeName, newValue, minimum, increment, epsilon);
            
        return;
    }
    else 
        throw new UnsupportedValueTypeException (typeName, proeType);
        
    }
    
    /**
     * Constructs a new RangeIncrementalParameterType given a type name, a String of values, 
     * and a Pro/E parameter type (from com.ptc.jlinkdemo.common.BaseParameterHelper.
     */
    public RangeIncrementalParameterType (String typeName, String values, int proeType)
        throws CustomParameterTypeException
    {
        super( typeName, values, proeType);
    }

    /**
     * Overrides RangeParameterType.
     */
    public void setRangeValues (Object [] values) 
        throws CustomParameterTypeException
    {
        if (values.length < 3)
            throw new NotEnoughValsSuppliedException (typeName, 3, values.length);
        
        Number min = (Number)values [0];
        Number max = (Number)values [1];
        Number inc = (Number)values [2];

        minimum = min.doubleValue();
        maximum = max.doubleValue();
        increment = inc.doubleValue();

        if (values.length >= 4 && proeType == BaseParameterHelper.DOUBLE)
        {
            Double eps = (Double)values [3];
            epsilon = eps.doubleValue();
        }
        if (minimum > maximum)
            throw new IncompatibleRangeException (typeName, minimum, maximum);
        
    }

    /**
     * Returns the increment for this type. Although the return value is double, 
     * it is converted to an int before use if necessary.
     */
    public double getIncrement()
    {
        return increment;
    }

    /**
     * Returns the epsilon value for this type.  If the type is double, this value is used 
     * in the evaluation (value-min) % increment > epsilon * increment (since the result of %)
     * is not exactly zero.  If not explicitly set, this value defualts to 0.001.
     */
    public double getEpsilon()
    {
        return epsilon;
    }

    protected double increment;
    protected double epsilon = 0.001;  //default epsilon
}


    
