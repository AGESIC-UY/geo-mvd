package imm.gis.core.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JButton;

import org.geotools.filter.ExpressionBuilder;
import org.geotools.filter.parser.ParseException;
import org.opengis.filter.Filter;

import imm.gis.AppContext;
import imm.gis.consulta.LOVLoader;
import imm.gis.core.feature.ExternalAttribute;
import imm.gis.core.gui.FilterLayerDialog;
import imm.gis.core.gui.worker.BlockingSwingWorker;
import imm.gis.core.layer.Layer;
import imm.gis.core.layer.metadata.LayerAttributeMetadata;
import org.apache.log4j.Logger;

public class ContFilterLayerDialog {
	private FilterLayerDialog dialog;
	private AppContext context = AppContext.getInstance();
	private Layer capa = null;
	private ExpressionBuilder expBuilder = new ExpressionBuilder();
	private final ContPpal contPpal;
	private LOVLoader lovLoader = new LOVLoader();
	private ExternalAttribute values[];
	private boolean lovAttribute = false;
	private String lastFiltertxt; 
	static private Logger logger = Logger.getLogger(ContFilterLayerDialog.class.getName());
	public ContFilterLayerDialog(ContPpal p){
		logger.debug("ContFilterLayerDialog constructor");
		contPpal = p;
		dialog = new FilterLayerDialog(contPpal.getVistaPrincipal());
		logger.debug("Seteando Listener");
		addListeners();
		
	}

	private void addListeners(){
		dialog.butOk.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				new BlockingSwingWorker(dialog){
					boolean canFilter;
					
					protected void doNonUILogic() throws RuntimeException {
						canFilter = setFilter();
						if (canFilter){ 
							//contPpal.getModel().refreshViews(true);
							contPpal.getModel().setBboxFilterLayer(capa.getNombre());
						}											
					}
				    protected void doUIUpdateLogic() throws RuntimeException {
						if (canFilter){
							dialog.setVisible(false);
						}
						else{
							contPpal.showError("La sintaxis del filtro ingresado no es correcta");
						}
				    }	
				}.start();
			}
		});
		
		dialog.butCancel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				dialog.setVisible(false);
			}
		});
		
		ActionListener myListener = new ActionListener(){
			public void actionPerformed(ActionEvent e){
				dialog.txtFilter.append(' ' + ((JButton)e.getSource()).getText() + ' ');
			}
		};
		
		dialog.butEquals.addActionListener(myListener);
		dialog.butMajor.addActionListener(myListener);
		dialog.butMinor.addActionListener(myListener);
		dialog.butMajorEq.addActionListener(myListener);
		dialog.butMinorEq.addActionListener(myListener);
		dialog.butAnd.addActionListener(myListener);
		dialog.butOr.addActionListener(myListener);
		
		dialog.lstAttr.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				String val = (String)dialog.lstAttr.getSelectedValue();
				Object [] emptyData = new Object[]{""};
				if (val == null) return;
				
				if (e.getClickCount() == 2){
					dialog.txtFilter.append(' ' + val + ' ');
				} else if (e.getClickCount() == 1){
					LayerAttributeMetadata da = (LayerAttributeMetadata)capa.getMetadata().getAttributesMetadata().get(val);
					
					if (da.getUsage() == LayerAttributeMetadata.ATTR_USAGE_LOV){
						lovLoader.setAttributeType(capa.getFt().getAttributeType(da.getName()));
						lovLoader.setOrderByDescription(da.isOrderByDescription()); //NUEVO
						try {
							values = lovLoader.getValores();
							dialog.lstValues.setListData(values);
							lovAttribute = true;
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					} else {
						dialog.lstValues.setListData(emptyData);
					}
				}
			}
		});
		
		dialog.lstValues.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				int index = dialog.lstValues.getSelectedIndex();
				if (index < 0) return;
				
				if (e.getClickCount() == 2){
					if (lovAttribute == true){
						ExternalAttribute val = values[index];
						dialog.txtFilter.append("\""+val.getValue().toString()+"\"");					
					}					
				}
			}
		});
	}
	
	public void showDialog(String layer){
		capa = context.getCapa(layer);
		
		Iterator i = capa.getMetadata().getAttributesMetadata().values().iterator();
		
		// Aca habia un getQueryableAttributes, habria que rehacerlo si se sigue usando
		ArrayList<String> attsList = new ArrayList<String>();
		while (i.hasNext()){
			LayerAttributeMetadata latM = (LayerAttributeMetadata) i.next();
			if(latM.isQueryCapable()){
				attsList.add(latM.getName());
			}
			
		}
		
		dialog.lblFilter.setText("Filtro actual: " + getCurrentFilterStr());
		
		dialog.lstAttr.setListData(attsList.toArray(new String[attsList.size()]));
		dialog.txtFilter.setText(capa.getFilter() == null ? null : lastFiltertxt);
		dialog.setTitle("Filtrado capa " + layer);
		dialog.setVisible(true);
	}
	
	private String getCurrentFilterStr() {
		// TODO Auto-generated method stub
		String toReturn = capa.getFilter()== null ? "":capa.getFilter().toString(); 
		return toReturn;
	}

	private boolean setFilter(){
		boolean res = true;
		
		String text = dialog.txtFilter.getText();
		if (text == null || text.length() == 0){
			capa.setFilter(null);
		} else {
			try {
				Filter filter = (Filter)expBuilder.parser(capa.getFt(), text);
				capa.setFilter(filter);
				lastFiltertxt = text;
			} catch (ParseException e) {
				e.printStackTrace();
				res = false;
			}
		}
		
		return res;
	}
}
