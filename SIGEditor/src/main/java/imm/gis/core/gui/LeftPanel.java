package imm.gis.core.gui;

import imm.gis.core.gui.editedfeatures.EditedFeaturesPanel;
import imm.gis.core.gui.layermanager.LayerManagerPanel;
import imm.gis.core.interfaces.IMap;
import imm.gis.core.interfaces.IModel;
import imm.gis.edition.EditionContext;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

public class LeftPanel extends JSplitPane {

	private static final long serialVersionUID = 1L;
	
	EditedFeaturesPanel editionHistoryPanel;
	ScalePanel scalePanel;
	LayerManagerPanel layerManagerPanel;
	
	public LeftPanel(EditionContext editionContext, IModel model, IMap mapa, JFrame frame) {
		super(JSplitPane.VERTICAL_SPLIT);
		setOneTouchExpandable(true);
		
		editionHistoryPanel = new EditedFeaturesPanel(model, editionContext);
		scalePanel = new ScalePanel(mapa, frame);
		//MapContext mapContext = controladorPrincipal.getModel().getContext(); 
		layerManagerPanel = new LayerManagerPanel(mapa);		

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(layerManagerPanel);
		panel.add(scalePanel);
		
		setLeftComponent(editionHistoryPanel);
		setRightComponent(panel);
	}
	
	public EditedFeaturesPanel getTreePanel() {
		return editionHistoryPanel;
	}
	
	public LayerManagerPanel getLegendPanel(){
		return layerManagerPanel;
	}
	
	public ScalePanel getScalePanel() {
		return scalePanel;
	}
}
