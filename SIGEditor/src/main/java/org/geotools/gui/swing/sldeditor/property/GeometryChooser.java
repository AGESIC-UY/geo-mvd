/*
 * Created on 15-feb-2004
 */
package org.geotools.gui.swing.sldeditor.property;

import javax.swing.JComponent;

import org.geotools.gui.swing.sldeditor.SLDEditor;

/**
 * @author wolf
 */
public abstract class GeometryChooser extends JComponent implements SLDEditor {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public abstract String getSelectedName();
    public abstract void setSelectedName(String name);
	public abstract int getGeomPropertiesCount();
}
