/*
 * Created on 16-feb-2004
 *
 */
package org.geotools.gui.swing.sldeditor.property;

import javax.swing.JComponent;

import org.geotools.gui.swing.sldeditor.SLDEditor;

/**
 * @author wolf
 */
public abstract class ScaleEditor extends JComponent implements SLDEditor {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public abstract void setScaleDenominator(double scale);
    public abstract double getScaleDenominator();
}
