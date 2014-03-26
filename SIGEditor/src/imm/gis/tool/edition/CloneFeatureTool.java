package imm.gis.tool.edition;

import imm.gis.AppContext;
import imm.gis.core.gui.GuiUtils;
import imm.gis.core.interfaces.ICoreAccess;
import imm.gis.core.layer.metadata.LayerMetadata;
import imm.gis.edition.EditionContext;
import imm.gis.edition.util.GeomUtil;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;

import org.geotools.feature.AttributeType;
import org.geotools.feature.Feature;

import com.vividsolutions.jts.geom.Coordinate;

public class CloneFeatureTool extends AbstractEditionTool {

	private ICoreAccess coreAccess;
	private Cursor cursor;
	private EditionContext editionController;
	
	public CloneFeatureTool(ICoreAccess coreAccess, EditionContext editionController) {
		this.coreAccess = coreAccess;
		this.editionController = editionController;
		
		Toolkit tkt = Toolkit.getDefaultToolkit();
		
		cursor = tkt.createCustomCursor(GuiUtils.loadIcon("Drag32.gif").getImage(), new Point(15, 15),"");
	}

	
	public Cursor getCursor() {
		return cursor;
	}
	
	public void mouseClicked(Coordinate c) {
		
		try {
			Feature originalFeature = (Feature) coreAccess.getISelection().getSelected(editionController.getEditableType()).iterator().next();
			
			if (originalFeature != null) {
				
				if (GeomUtil.isPointGeometry(originalFeature.getFeatureType())) {
					// No podemos usar FeatureType.duplicate(), dado que le setea el mismo ID
					
			        int numAtts = originalFeature.getNumberOfAttributes();
			        Object attributes[] = new Object[numAtts];
			        
			        LayerMetadata metadata = AppContext.getInstance().getCapa(editionController.getEditableType()).getMetadata();
			        
			        for (int i = 0; i < numAtts; i++) {
			        	AttributeType curAttType = originalFeature.getFeatureType().getAttributeType(i);
			        	
			        	if (metadata.getAttributeMetadata(curAttType.getLocalName()).isReadOnly())
			        		attributes[i] = curAttType.createDefaultValue();
			        	else
			        		attributes[i] = curAttType.duplicate(originalFeature.getAttribute(i));
			        }
	
			        Feature clonedFeature = coreAccess.getIModel().createFeature(editionController.getEditableType(), attributes);
					clonedFeature.setDefaultGeometry(coreAccess.getIModel().getGeometryFactory().createPoint(c));
					
					coreAccess.getIModel().addFeature(clonedFeature);
				}
				else
					coreAccess.getIUserInterface().showError("La geometria del feature a clonar debe ser de tipo punto");
			}
			else
				coreAccess.getIUserInterface().showError("Debe seleccionar un feature");
		}
		catch (Exception e) {
			coreAccess.getIUserInterface().showError("Error al consultar el feature",e);
			e.printStackTrace();
		}
	}
	
	public boolean requiresAddPermission() {
		return false;
	}
}
