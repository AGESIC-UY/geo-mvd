package imm.gis.gui.actions.edition;

import imm.gis.core.gui.GuiUtils;
import imm.gis.core.interfaces.ICoreAccess;
import imm.gis.edition.EditionContext;
import imm.gis.edition.IFeatureEditor;
import imm.gis.gui.toolbar.toolbutton.ExtendedAction;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Collection;
import java.util.NoSuchElementException;

import org.geotools.feature.IllegalAttributeException;


public class EditSelectedDataAction extends ExtendedAction implements
		IFeatureEditor {

	private static final long serialVersionUID = 1L;

	private ICoreAccess coreAccess;

	private EditionContext editionController;

	public EditSelectedDataAction(ICoreAccess coreAccess,
			EditionContext editionController) {
		super("Editar datos de elementos seleccionados",
				GuiUtils.loadIcon("EditSelected16.gif"),
				GuiUtils.loadIcon("EditSelected16Dis.gif"));

		this.coreAccess = coreAccess;
		this.editionController = editionController;

		setToggle(false);
		setEnabled(false);
	}

	public void actionPerformed(ActionEvent e) {
		/*
		 * ITool editTool = new EditSelectedDataTool(coreAccess,
		 * editionController); coreAccess.setActiveTool(editTool);
		 */

			Collection selected;
			try {
				selected = coreAccess.getISelection().getSelected(
						editionController.getEditableType());
				coreAccess.getIForm().openEditFeaturesForm(selected);
			} catch (NoSuchElementException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IllegalAttributeException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
	
	}

	public boolean requiresAddPermission() {
		return false;
	}

	public boolean requiresDeletePermission() {
		return false;
	}

	public boolean requiresModifyPermission() {
		return true;
	}

	public boolean requiresSelectVarious() {
		// TODO Auto-generated method stub
		return true;
	}

}
