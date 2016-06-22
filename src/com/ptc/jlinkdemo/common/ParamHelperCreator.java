/* HISTORY

13-May-99 J-01-03 MSH      $$1  Created,

*/

package com.ptc.jlinkdemo.common;

import javax.swing.JOptionPane;

import java.util.Vector;

import com.ptc.cipjava.*;
import com.ptc.pfc.pfcCommand.*;
import com.ptc.pfc.pfcSession.*;
import com.ptc.pfc.pfcModel.*;
import com.ptc.pfc.pfcModelItem.*;
import com.ptc.pfc.pfcPart.*;


public class ParamHelperCreator {

 ParameterHelper [] pHelpers;

 public ParamHelperCreator (ParameterOwner p_owner)
 {
        try{
        Parameters params = p_owner.ListParams ();
        int nParams = params.getarraysize ();
        Vector pHelperVector = new Vector ();
        for (int iParam = 0; iParam < nParams; ++iParam)
        {
            Parameter param = params.get (iParam);
            ParameterHelper pHelper = ParameterHelper.create (p_owner, param);
            if (pHelper != null)
            {
                pHelperVector.addElement (pHelper);
            }
        }

        pHelpers =
            new ParameterHelper [pHelperVector.size ()];
        pHelperVector.copyInto (pHelpers);
        }
        catch (jxthrowable x)
        {
         x.printStackTrace();
        }

 }

 public ParameterHelper [] getHelpers ()
 {
        return pHelpers;

 }






}
