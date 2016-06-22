/* HISTORY

13-Apr-99 TK-2-01 klm      $$1  Created
12-May-99 I-01-36 jcn      $$2  Added Custom type implementation.
20-Apr-00 I-03-26 msh      $$3  Changed Model to static to stay the same between objects

*/

package com.ptc.jlinkdemo.common;

import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;

import java.awt.Color;
import java.awt.Dimension;

import java.util.Vector;

public class ParamTable extends JTable
{
    public ParamTable (BaseParameterHelper [] paramHelpers)
    {
        paramModel = new ParamTableModel (paramHelpers);
        setModel (paramModel);

        // Specialized cell editor for parameter values
        TableColumn paramColumn = getColumn (
            paramModel.getColumnName (paramModel.getParamValueColIdx ()));
        paramColumn.setCellEditor (new ParamCellEditor (paramModel));
        setPreferredScrollableViewportSize(new Dimension(500, 70));

        // Set background to white for compatibility with specialized renderers
        setBackground (Color.white);

        // Specialized cell renderers for highlighting parameters
        validRenderer = new DefaultTableCellRenderer ();
        validRenderer.setForeground (Color.green.darker ());

        invalidRenderer = new DefaultTableCellRenderer ();
        invalidRenderer.setForeground (Color.red);

        modifiedRenderer = new DefaultTableCellRenderer ();
        modifiedRenderer.setForeground (Color.yellow.darker ());
    }

    public TableCellRenderer getDefaultRenderer (Class columnClass)
    {
        return (super.getDefaultRenderer (columnClass));
    }

    public TableCellRenderer getCellRenderer (int row, int column)
    {
        switch (checkValid (row, column))
        {
        case INVALID:
            return (invalidRenderer);
        case VALID:
            return (validRenderer);
        case MODIFIED:
            return (modifiedRenderer);
        }
        // Must never happen
        return (null);
    }

    public boolean isAllValid ()
    {
        int nParams = paramModel.getRowCount ();
        int valCol = paramModel.getParamValueColIdx ();
        for (int iParam = 0; iParam < nParams; ++iParam)
        {
            if (checkValid (iParam, valCol) == INVALID)
                return (false);
        }
        return (true);
    }
    
    public String[] getInvalidMessages ()
    {
        if (this.isAllValid()) return null;
        
        Vector messages = new Vector(1);
        
        messages.addElement("The table contains the following invalid parameters: ");
        int nParams = paramModel.getRowCount ();
        int valCol = paramModel.getParamValueColIdx ();
        
        for (int iParam = 0; iParam < nParams; ++iParam)
        {
            if (this.checkValid (iParam, valCol) == INVALID)
            messages.addElement (this.getInvalidMessage (iParam, valCol));
        }
        
        if (messages.size() == 1) return null;
        
        String [] msgs = new String [messages.size()];
        messages.copyInto (msgs);
        
        return (msgs);
    }

    protected int checkValid (int row, int column)
    {
        BaseParameterHelper helper = paramModel.getParameterHelper (row);
        Object oldValue = helper.getValue ();
        Object newValue = paramModel.getNewValue (row);       
        if (newValue == null)
            return ((oldValue == null) ? VALID : MODIFIED);
        if (helper.getType () == BaseParameterHelper.CUSTOM)
        {
            if (!helper.getCustomType().checkValue (newValue)) 
                {               
                helper.setInvalidMessage (helper.getCustomTypeMessage());
                return (INVALID);
                }
        }
        if (oldValue == null || ! oldValue.equals (newValue))
            return (MODIFIED);
        else
            return (VALID);
    }
    
    protected String getInvalidMessage (int row, int column)
    {
        return paramModel.getParameterHelper (row).getInvalidMessage();
    }

    public void commitNewValues ()
    {
        paramModel.commitNewValues ();
    }

    public void resetNewValues ()
    {
        paramModel.resetNewValues ();
    }

    //=========================================================================
    private static void printMsg (String msg)
    {
        System.out.println ("ParamTable: " + msg);
    }

    //=========================================================================
    protected final static int          INVALID = 0;
    protected final static int          VALID = 1;
    protected final static int          MODIFIED = 2;

    protected static ParamTableModel           paramModel;
    protected DefaultTableCellRenderer  invalidRenderer;
    protected DefaultTableCellRenderer  validRenderer;
    protected DefaultTableCellRenderer  modifiedRenderer;
}
