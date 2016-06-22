/* HISTORY

13-Apr-99 TK-2-01 klm      $$1  Created
12-May-99 I-01-36 jcn      $$2  Removed 'LEFTRIGHT' reference.

*/

package com.ptc.jlinkdemo.common;

import com.ptc.cipjava.jxthrowable;

public class DBParameterHelper extends BaseParameterHelper
{
    protected void determineProeName ()
    {
    }

    public DBParameterHelper (String name, int type, Object value)
    {
        proeName = name;
        this.value = value;
        this.type = type;
        this.proeType = type;
    }
}
