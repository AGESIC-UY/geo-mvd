/*
 * Created on 15-feb-2004
 */
package org.geotools.gui.swing.sldeditor.property;

import javax.swing.JComponent;

import org.geotools.gui.swing.sldeditor.SLDEditor;
import org.geotools.styling.LabelPlacement;

/**
 * @author wolf
 */
public abstract class LabelPlacementEditor extends JComponent implements SLDEditor {
	private static final long serialVersionUID = 1L;

	public abstract void setLabelPlacement(LabelPlacement labelPlacement);
    public abstract LabelPlacement getLabelPlacement();
}
