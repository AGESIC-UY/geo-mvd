/*
 * Created on 15-feb-2004
 */
package org.geotools.gui.swing.sldeditor.property;

import javax.swing.JComponent;

import org.geotools.gui.swing.sldeditor.SLDEditor;

/**
 * @author wolf
 */
public abstract class DashArrayEditor extends JComponent implements SLDEditor {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public abstract void setDashArray(float[] dash);
    public abstract float[] getDashArray();
}
