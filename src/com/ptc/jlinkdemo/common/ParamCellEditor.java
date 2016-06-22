/* HISTORY

13-Apr-99 TK-2-01 klm      $$1  Created
12-May-99 I-01-36 jcn      $$2  Added Custom capabilities: create combo box 
                                on the fly

*/

package com.ptc.jlinkdemo.common;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import java.awt.Component;
import java.awt.AWTEvent;
import java.util.EventObject;

import com.ptc.cipjava.*;
import com.ptc.pfc.pfcModelItem.*;

import com.ptc.jlinkdemo.common.BaseParameterHelper;

public class ParamCellEditor extends DefaultCellEditor
{
    public static final int NOT_EDITABLE = -1;
    public static final int TEXTFIELD = 0;
    public static final int COMBOBOX = 1;
    
    
    
    public ParamCellEditor (ParamTableModel model)
    {
        super (new JTextField ());
        // Save super's values
        textField = (JTextField) editorComponent;
        textDelegate = delegate;

        this.model = model;
        createCombos ();
    }

    public Component getTableCellEditorComponent
        (JTable table, Object value, boolean isSelected, int row, int column)
    {
        // printMsg ("getTableCellEditorComponent(" + row + ", " + column +
        //          " [" + value + "]");

        BaseParameterHelper pHelper = model.getParameterHelper (row);
        int pType = pHelper.getType ();
        if (pType == BaseParameterHelper.BOOLEAN)
        {
            editorComponent = activeCombo = booleanCombo;
            delegate = comboDelegate;
        }
        else if (pType == BaseParameterHelper.CUSTOM)
        {
            int editortype = pHelper.getCustomType().getTableCellEditorComponentType ();
            switch (editortype)
            {
                
                case COMBOBOX:
                    {
                        JComboBox combo = createComboBox ((String [] )pHelper.getCustomType().getUIValues());
                        editorComponent = activeCombo = combo;
                        delegate = comboDelegate;
                        break;
                    }
                case TEXTFIELD:
                    {
                        activeCombo = null;
                        editorComponent = textField;
                        delegate = textDelegate;
                        break; 
                    }                 
                default:
                case NOT_EDITABLE:
                    {
                        activeCombo = null;
                        editorComponent = textField;
                        textField.setEditable (false);
                        delegate = textDelegate;
                    }
                
            }
        }
        else
        {
            activeCombo = null;
            editorComponent = textField;
            delegate = textDelegate;
        }
        return (super.getTableCellEditorComponent (table, value, isSelected,
                                                   row, column));
    }

    protected JComboBox createComboBox (String [] comboValues)
    {
        JComboBox combo = new JComboBox (comboValues);
        combo.setEditable (false);
        
        combo.addItemListener (comboDelegate);
        
        return (combo);
    }


    protected void createCombos ()
    {
        comboDelegate = new EditorDelegate () {
            public void setValue (Object x)
                {
                    super.setValue (x);
                    ((JComboBox) activeCombo).setSelectedItem (x);
                }

            public Object getCellEditorValue ()
                {
                    return (((JComboBox) activeCombo).getSelectedItem ());
                }

            public boolean startCellEditing (EventObject anEvent)
                {
                    return (anEvent instanceof AWTEvent);
                }

            public boolean stopCellEditing ()
                {
                    return (true);
                }
        };
        
        final String [] boolComboValues = { "no", "yes" };
       // final String [] lrComboValues = { "left", "right" };
        booleanCombo = createComboBox (boolComboValues);
       
        //leftRightCombo = new JComboBox (lrComboValues);
    }

    //=========================================================================
    private static void printMsg (String msg)
    {
        System.out.println ("ParamCellEditor: " + msg);
    }

    //=========================================================================
    protected ParamTableModel   model;

    protected JTextField        textField;      // Saving super's editorComp.
    protected EditorDelegate    textDelegate;   // Saving super's delegate
    protected EditorDelegate    comboDelegate;  // Delegate for combo boxes
    protected JComboBox         booleanCombo;   // Yes/No combo
    protected JComboBox         leftRightCombo; // Left/Right combo
    protected JComboBox         activeCombo;    // Currently active combo
                                                // (for use in comboDelegate)
}
