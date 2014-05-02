package imm.gis.core.gui;

import imm.gis.core.interfaces.IModel;
import imm.gis.edition.util.GeomUtil;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;

import org.geotools.feature.FeatureType;

public class ListRenderer extends DefaultListCellRenderer {

	private static final long serialVersionUID = 1L;

	IModel modelData;
	ImageIcon pointIcon;
	ImageIcon lineIcon;
	ImageIcon polyIcon;
	
	public ListRenderer(IModel model){
		modelData = model;
		pointIcon = GuiUtils.loadIcon("PointLayer16.gif");
		lineIcon = GuiUtils.loadIcon("LineLayer16.gif");
		polyIcon = GuiUtils.loadIcon("PolyLayer16.gif");
	}
	
	public java.awt.Component getListCellRendererComponent(JList list, Object value, 
			 int index, boolean isSelected, boolean hasFocus){
		JLabel label = (JLabel)super.getListCellRendererComponent(list, value, index, isSelected, hasFocus);

		String type = (String)value;
		
		try{
			FeatureType featureType = modelData.getSchema(type);
			
			if (GeomUtil.isPointGeometry(featureType)){
				label.setIcon(pointIcon);			
			} else if (GeomUtil.isLineGeometry(featureType)){
				label.setIcon(lineIcon);							
			} else {
				label.setIcon(polyIcon);							
			}
		} catch (Exception e){}
		
		return label;
	}

}
