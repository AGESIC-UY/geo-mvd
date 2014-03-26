package imm.gis.gui.actions.application;

import imm.gis.core.gui.ListRenderer;
import imm.gis.core.gui.SelEditableLayer;
import imm.gis.core.gui.worker.BlockingSwingWorker;
import imm.gis.core.interfaces.AbstractNonUILogic;
import imm.gis.core.interfaces.ICoreAccess;
import imm.gis.edition.EditionContext;
import imm.gis.gui.toolbar.toolbutton.ExtendedAction;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JOptionPane;

public class EditAction extends ExtendedAction{

	private static final long serialVersionUID = 1L;

	private SelEditableLayer selEditableLayer = null;
	
	private ICoreAccess coreAccess;

	private EditionContext editionContext;
	
	public EditAction(ICoreAccess coreAccess, EditionContext editionContext){
		super("Editar");
		
		this.coreAccess = coreAccess;
		this.editionContext = editionContext;
		
		setToggle(true);
	}
	
	public void actionPerformed(ActionEvent e) {
		final javax.swing.AbstractButton ab = (javax.swing.AbstractButton)e.getSource();
		
		try{
			if (ab.isSelected()){
				
				if (selEditableLayer == null) { 
					selEditableLayer = new SelEditableLayer(coreAccess.getIApplication().getMainGUI(),
															editionContext.getEditableTypes());
					
					selEditableLayer.getList().setCellRenderer(new ListRenderer(coreAccess.getIModel()));
					selEditableLayer.setCloseListener(new ActionListener(){
						public void actionPerformed(final ActionEvent e){
							new BlockingSwingWorker(selEditableLayer){
							
							    protected void doNonUILogic() throws RuntimeException{
									try {
										editionContext.setEditableType(selEditableLayer.getValue());
										if(e.getActionCommand().equals("CANCEL")){
											ab.setSelected(false);
										}
										
									} catch (IOException e1) {
										e1.printStackTrace();
									}																	
								}

							    protected void doUIUpdateLogic() throws RuntimeException {
									selEditableLayer.setVisible(false);
							    }
							}.start();
						}
					});
				}
				
				selEditableLayer.choose();
			} else {
				String editableType = editionContext.getEditableType();
				if (editableType != null && coreAccess.getIModel().isModified(editableType)) {
					final int res = JOptionPane.showConfirmDialog(
							(java.awt.Component)coreAccess.getIApplication().getMainGUI(),
							"Hay cambios sin salvar.\nSalva los cambios?",
							"Confirmar cambios",
							JOptionPane.YES_NO_CANCEL_OPTION
					);
					
					AbstractNonUILogic l = new AbstractNonUILogic() {
						public void logic() {
							
							try {
								if (res == JOptionPane.YES_OPTION){
									//getSaveAction().actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, ""));
									coreAccess.getIModel().confirmModified(editionContext.getEditableType());
									editionContext.setEditableType(null);
								}
								else if (res == JOptionPane.NO_OPTION) {
									coreAccess.getIModel().undoAllModified(editionContext.getEditableType());
									editionContext.setEditableType(null);
								}
								else {
									ab.setSelected(true);
								}
							}
							catch (Exception e) {
								coreAccess.getIUserInterface().showError("Error al guardar las modificaciones",e);
								ab.setSelected(true);
								e.printStackTrace();
							}
						}
					};
					
					coreAccess.getIUserInterface().doNonUILogic(l);
				}
				else
					editionContext.setEditableType(null);						
			}
		} catch (Exception ex){
			ex.printStackTrace();
			coreAccess.getIUserInterface().showError(ex);
		}
	}
}
