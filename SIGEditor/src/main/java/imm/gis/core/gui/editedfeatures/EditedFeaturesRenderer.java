package imm.gis.core.gui.editedfeatures;

import imm.gis.core.controller.IGeometryType;
import imm.gis.core.gui.GuiUtils;
import imm.gis.core.interfaces.IModel;
import imm.gis.core.model.IStatus;
import imm.gis.edition.EditionContext;
import imm.gis.edition.util.GeomUtil;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

import org.apache.log4j.Logger;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureType;

public class EditedFeaturesRenderer extends JPanel implements TreeCellRenderer {
	static private final Logger log = Logger.getLogger(EditedFeaturesRenderer.class);
	private static final long serialVersionUID = 1L;
	private Font normalFont;
	private Font createdFont;
	private Font updatedFont;
	
	private ImageIcon iconEditPoint = GuiUtils.loadIcon("EditPoint16.gif");
	private ImageIcon iconEditLine = GuiUtils.loadIcon("EditLine16.gif");
	private ImageIcon iconEditPoly = GuiUtils.loadIcon("EditPolygon16.gif");
	//
	protected JLabel label = new JLabel();

	private IModel model;
	//private EditionContext editionContext;
	
	public EditedFeaturesRenderer(IModel model, EditionContext editionContext){
	    super(new BorderLayout());
	    
	    this.model = model;
	    //this.editionContext = editionContext;
	    
	    setOpaque(false);
	    //check.setOpaque(false);
	    label.setOpaque(false);
	    //check.setSelected(false);
	    //add(check, BorderLayout.WEST);
	    add(label, BorderLayout.CENTER);
		normalFont = UIManager.getFont("Tree.font");
		updatedFont = normalFont.deriveFont(Font.ITALIC);
		createdFont = normalFont.deriveFont(Font.BOLD);
	}
	
	public Component getTreeCellRendererComponent(JTree tree, Object v,
			boolean s, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {

		DefaultMutableTreeNode treeNode;
		Feature f;
		FeatureType ft;
		String idAtt;
		Object value;
		int featureState;
		
		treeNode = (DefaultMutableTreeNode)v;
		value = treeNode.getUserObject();

		if (Feature.class.isAssignableFrom(value.getClass())) {
			f = (Feature)value;
			ft = f.getFeatureType();
			idAtt = null; //contPpal.getIModel().getUserIdName(ft.getTypeName()); 
			
			featureState = model.getStatus(f);
			
			if (idAtt == null)
				label.setText(f.getID());
			else if (f.getAttribute(idAtt)==null)
				label.setText("ID no asignado");
			else
				label.setText(f.getAttribute(idAtt).toString());
			
			switch (featureState)
			{
				case IStatus.CREATED_ATTRIBUTE:
					label.setFont(createdFont);
					label.setForeground(Color.BLACK);
					break;
				case IStatus.UPDATED_ATTRIBUTE:
					label.setFont(updatedFont);
					label.setForeground(Color.BLACK);
					break;
				case IStatus.DELETED_ATTRIBUTE:
					label.setFont(normalFont);
					label.setForeground(Color.RED);
					break;
				default:
					label.setFont(normalFont);
					label.setForeground(Color.BLACK);
			}
			label.setIcon(null);
			
			//check.setVisible(false);
		}
		else if (FeatureType.class.isAssignableFrom(value.getClass())) {
			ft = (FeatureType)value;
			String typeName = ft.getTypeName();

			
			label.setText(typeName);
			
			switch (GeomUtil.getGeometryType(ft)){
				case IGeometryType.POINT_GEOMETRY:
					label.setIcon(iconEditPoint);				
					break;
				case IGeometryType.LINE_GEOMETRY:
					label.setIcon(iconEditLine);
					break;
				case IGeometryType.POLYGON_GEOMETRY:
					label.setIcon(iconEditPoly);
					break;
				default:
					log.info("No se que geometria tiene " + typeName);						
					break;
			}
			
			label.setFont(normalFont);
			label.setForeground(Color.BLACK);//selected ? Color.RED : Color.BLACK);
			//check.setVisible(true);		
			//boolean sel = coreAccess.getIModel().isEnabledLayer(typeName);
			//check.setSelected(sel);
		}
		else {
			label.setText(String.valueOf(v));
			label.setIcon(null);
			setFont(normalFont);
			setForeground(Color.BLACK);						
			//check.setVisible(false);			
		}
		
		return this;
	}

	/*
	*/
}
