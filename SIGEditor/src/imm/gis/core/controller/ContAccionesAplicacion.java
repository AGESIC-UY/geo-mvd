package imm.gis.core.controller;

import imm.gis.comm.FactoryClientFacade;
import imm.gis.core.feature.ExternalAttribute;
import imm.gis.core.feature.ExternalAttributeType;
import imm.gis.core.feature.FeatureReferenceAttribute;
import imm.gis.core.gui.GuiUtils;
import imm.gis.core.gui.SelKMLData;
import imm.gis.core.gui.UserChooser;
import imm.gis.core.gui.worker.BlockingSwingWorker;
import imm.gis.core.model.ModelData;
import imm.gis.core.model.event.FeatureChangeEvent;
import imm.gis.core.model.event.FeatureEventManager;
import imm.gis.gui.toolbar.toolbutton.ExtendedAction;

import java.awt.Color;
import java.awt.Desktop;
import java.io.IOException;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.geotools.data.DataStore;
import org.geotools.data.DefaultQuery;
import org.geotools.data.FeatureReader;
import org.geotools.data.FeatureStore;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.feature.AttributeType;
import org.geotools.feature.AttributeTypeFactory;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.FeatureType;
import org.geotools.feature.SimpleFeatureType;
import org.opengis.filter.Filter;

import com.vividsolutions.jts.geom.Geometry;

//Agregado
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
//Fin agregado


public class ContAccionesAplicacion {
	private ContPpal contPpal = null;
	private ExtendedAction exitAction = null;
	private ExtendedAction exportExcelAction = null;
	private ExtendedAction exportImageAction = null;
	private ExtendedAction exportKMLAction = null;
	private ExtendedAction exportShapeAction = null;
//	private ExtendedAction configLayerStyleAction = null;
	private ExtendedAction fullScreenAction = null;
//	private ContStyleDialog contStyleDialog = null;
	private JFileChooser chooserExportImage;
	private JFileChooser chooserExportExcel;
	private FileFilter jpgFileFilter;
	private FileFilter pngFileFilter;
	private FileFilter xlsFileFilter;
	private ExtendedAction manualUsuarioAction = null;
	
	
	private static Logger logger = Logger.getLogger(ContAccionesAplicacion.class);
	
	public ContAccionesAplicacion(ContPpal p){
		logger.debug("ContAccionesAplicacion constructor");
		contPpal = p;
		
		getFullScreenAction();
		getExitAction();
		getExportImageAction();
		getExportExcelAction();
		getExportShapeAction();
		getExportKMLAction();
	
	}

	public void setCalculatedAttribute(Map<String,Map<String,Object>> calculatedAttributes){
		((ExportExcelAction)exportExcelAction).setCalculatedAttributes(calculatedAttributes);
	}
	
	
	public ExtendedAction getFullScreenAction(){
		if (fullScreenAction == null){
			fullScreenAction = new FullScreenAction();
		}
		
		return fullScreenAction;
	}

	public ExtendedAction getExitAction(){
		if (exitAction == null){
			exitAction = new ExitAction();
		}
		
		return exitAction;
	}

	public ExtendedAction getExportImageAction(){
		if (exportImageAction == null){
			exportImageAction = new ExportImageAction();
			jpgFileFilter = new FileFilter(){
				public boolean accept(File f){
					return f.isDirectory() || f.getName().toLowerCase().endsWith(".jpg");
				}
				
				public String getDescription(){
					return "Imagen JPG (*.jpg)";
				}
			};
			pngFileFilter = new FileFilter(){
				public boolean accept(File f){
					return f.isDirectory() || f.getName().toLowerCase().endsWith(".png");
				}
				
				public String getDescription(){
					return "Imagen PNG (*.png)";
				}
			};
			chooserExportImage = new JFileChooser(){
				private static final long serialVersionUID = 1L;

				@Override
				public void updateUI() {
					putClientProperty("FileChooser.useShellFolder", Boolean.FALSE);
					super.updateUI();
				}				
			};
			chooserExportImage.setAcceptAllFileFilterUsed(false);
			chooserExportImage.addChoosableFileFilter(jpgFileFilter);
			chooserExportImage.addChoosableFileFilter(pngFileFilter);
			//chooserExportImage.set
		}
		
		return exportImageAction;
	}
	
	public ExtendedAction getManualUsuarioAction(String ubicacionManual){
		try {
			 if (manualUsuarioAction == null)
				manualUsuarioAction = new ManualUsuarioAction(ubicacionManual);
				
		     
		}catch (Exception ex) {
		     ex.printStackTrace();
		}		
		return manualUsuarioAction;
	}
	

	
	public ExtendedAction getExportExcelAction(){
		if (exportExcelAction == null){
			exportExcelAction = new ExportExcelAction();
			xlsFileFilter = new FileFilter(){
				public boolean accept(File f){
					return f.isDirectory() || f.getName().toLowerCase().endsWith(".xls");
				}
				
				public String getDescription(){
					return "Archivo Excel (*.xls)";
				}
			};
			chooserExportExcel = new JFileChooser(){
				private static final long serialVersionUID = 1L;

				@Override
				public void updateUI() {
					putClientProperty("FileChooser.useShellFolder", Boolean.FALSE);
					super.updateUI();
				}								
			};
			chooserExportExcel.setAcceptAllFileFilterUsed(false);
			chooserExportExcel.addChoosableFileFilter(xlsFileFilter);
		}
		
		return exportExcelAction;
	}
	
	public ExtendedAction getExportKMLAction(){
		if (exportKMLAction == null){
			exportKMLAction = new ExportKMLAction();
		}
		
		return exportKMLAction;

	}

	public ExtendedAction getExportShapeAction(){
		if (exportShapeAction == null){
			exportShapeAction = new ExportShapeAction();
		}
		
		return exportShapeAction;

	}

	
	
	/*	public ExtendedAction getConfigLayerStyleAction(){
		if (configLayerStyleAction == null){
			contStyleDialog = new ContStyleDialog();
			configLayerStyleAction = new ConfigLayerStyleAction();
		}
		
		return configLayerStyleAction;
	}
*/	
	private class FullScreenAction extends ExtendedAction{

		private static final long serialVersionUID = 1L;

		public FullScreenAction(){
			super("Pantalla completa");
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_P));
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("Ctrl F"));
		}
		
		public void actionPerformed(ActionEvent e){
			
			if (logger.isDebugEnabled())
				logger.debug("Full screen...");
			
			GuiUtils.fullScreen(contPpal.getVistaPrincipal());
		}
	}

	private class ExitAction extends ExtendedAction{

		private static final long serialVersionUID = 1L;

		public ExitAction(){
			super("Salir");
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_S));
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("Ctrl X"));
		}
		
		public void actionPerformed(ActionEvent e){
			contPpal.cerrar();
		}
	}

	private class ExportImageAction extends ExtendedAction{

		private static final long serialVersionUID = 1L;

		public ExportImageAction(){
			super("Exportar imagen");
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_E));
		}
		
		public void actionPerformed(ActionEvent e){
			int res = chooserExportImage.showSaveDialog(contPpal.getVistaPrincipal());
			if (res == JFileChooser.APPROVE_OPTION){
					
				new BlockingSwingWorker(contPpal.getVistaPrincipal(), "Grabando imagen", "Espere, por favor...", true){
					String name = null;
					
		            protected void doNonUILogic() throws RuntimeException {
						try{
							//Envelope bbox = contPpal.getModel().getBbox();
							BufferedImage tmp = contPpal.getImage();				
							BufferedImage image = new BufferedImage(tmp.getWidth(), tmp.getHeight(), BufferedImage.TYPE_INT_RGB);
							image.setData(tmp.getData());
							tmp = null;
							
							File file = chooserExportImage.getSelectedFile();
							File saveFile;
							String format;
							
							if (chooserExportImage.getFileFilter() == jpgFileFilter)
								format = "jpg";
							else
								format = "png";

							if (!file.getName().toLowerCase().endsWith("." + format))
								saveFile = new File(file.getAbsolutePath() + "." + format);
							else
								saveFile = file;
								
							logger.info("Creando imagen " + saveFile.getName());
							Graphics2D g = image.createGraphics();
							
							long scale = contPpal.getContMapa().getScale();
							//int scale = ContUtils.scale(bbox);
							String text = " Escala 1 : " + scale + " ";
							g.setFont(g.getFont().deriveFont(Font.BOLD));
							Rectangle rectangle = g.getFontMetrics().getStringBounds(text, g).getBounds();
							g.setColor(Color.WHITE);
							g.fill3DRect(5, 5, rectangle.width, rectangle.height, true);
							g.draw3DRect(5, 5, rectangle.width, rectangle.height, true);
							g.setColor(Color.BLACK);
							g.drawString(text, 5, 1 + rectangle.height);
							g.dispose();
							logger.info("Grabando imagen...");
							
							if (!ImageIO.write(image, format, saveFile))
								logger.error("No se puede grabar imagen en formato " + format);
							else {
								name = saveFile.getName();
								
								if (logger.isDebugEnabled())
									logger.debug("Imagen generada con exito");						
							}
						}
						catch (Exception e) {
							e.printStackTrace();
							logger.error(e);
						}
					}
		            
		            protected void doUIUpdateLogic() throws RuntimeException {
		            	if (name != null){
		            		javax.swing.JOptionPane.showMessageDialog(contPpal.getVistaPrincipal(), "Imagen generada con exito");
		            	}
		            }
				}.start();
			}
		}
	}

	//------------------------------------------------------------------------	
	// Metodo modificado por Gabriela G. el 23/03/2009
	
	private class ExportExcelAction extends ExtendedAction{

		private static final long serialVersionUID = 1L;
		
		private Map<String,Map<String,Object>> calculatedAttributes;
		
		public ExportExcelAction(){
			super("Exportar datos a Excel");
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_X));			
		}
		
		public void setCalculatedAttributes(Map<String,Map<String,Object>> calculatedAttributes){
			this.calculatedAttributes = calculatedAttributes;
		}
				
		public void actionPerformed(ActionEvent e){
			int res = chooserExportExcel.showSaveDialog(contPpal.getVistaPrincipal());
			if (res == JFileChooser.APPROVE_OPTION){
				new BlockingSwingWorker(contPpal.getVistaPrincipal(), "Creando planilla", "Espere, por favor...", true){
					File file = null;
					
		            protected void doNonUILogic() throws RuntimeException {
						file = chooserExportExcel.getSelectedFile();
						if (!file.getName().toLowerCase().endsWith(".xls")){
							file = new File(file.getAbsolutePath() + ".xls");
						}
						
						logger.info("Creando planilla " + file.getName());
						try{
							HSSFWorkbook wb = new HSSFWorkbook();
							HSSFSheet sheet;
							HSSFRow row;
							HSSFCell cell;
							ModelData model = contPpal.getModel();
							String layers[] = model.getNotEmptyTypes();
							FeatureCollection fc;
							FeatureIterator fi;
							FeatureType ft;
							Feature f;
							HSSFFont fontHeader = wb.createFont();
							int y,z;
							int cellIndex;
							
							Object value;
							fontHeader.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);


//Agregado (crea un estilo de celda de tipo fecha)   
				            HSSFCellStyle cellStyle = wb.createCellStyle();   
				            cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("d-mmm-yy"));
//Fin agregado
							
							
							for (int x = 0; x < layers.length; x++){
								
//Agregado (Solo se exportan las capas editables)									
							//  if (model.isEditable(layers[x])) {
//Fin agregado									
								
								logger.info("Creando hoja " + layers[x]);
								if (layers[x].length() > 31)
									sheet = wb.createSheet(layers[x].substring(0, 31));
								else
									sheet = wb.createSheet(layers[x]);
								ft = model.getSchema(layers[x]);
								
								//cantAtts = ft.getAttributeCount() - 2;
								y = 0;
								row = sheet.createRow((short)y++);
								cellIndex = 0;

								HSSFRichTextString columnHeader = null;
								
//Agregado (Agrega la columna ID)									
								cell = row.createCell((short)cellIndex);
								logger.info("Creando columna ID");
								columnHeader = new HSSFRichTextString("ID");
								columnHeader.applyFont(fontHeader);
								cell.setCellValue(columnHeader);
								cellIndex++;								
//Fin agregado
								
								
								for (z = 0; z < ft.getAttributeCount(); z++) {
									if (!Geometry.class.isAssignableFrom(ft.getAttributeType(z).getBinding())) {
										cell = row.createCell((short)cellIndex);
										logger.info("Creando columna " + ft.getAttributeType(z).getLocalName());
										cell.setCellType(HSSFCell.CELL_TYPE_STRING);
										columnHeader = new HSSFRichTextString(ft.getAttributeType(z).getLocalName());
										columnHeader.applyFont(fontHeader);
										cell.setCellValue(columnHeader);
										cellIndex++;
									}
								}
								
								fc = model.getFeatureSource(layers[x]).getFeatures();

								fi = fc.features();
								
								/*NUEVO */
								model.notifyChangeListeners(new FeatureChangeEvent(fc,
										layers[x], null, null,
										FeatureChangeEvent.EXPORT_ACTION),
										FeatureEventManager.BEFORE_MODIFY);
								
								Iterator it;
								if (calculatedAttributes != null){
									it = calculatedAttributes.keySet().iterator();
									while(it.hasNext()){
										String nameAtt = (String) it.next();
										cell = row.createCell((short)cellIndex);
										//logger.info("Creando columna " + nameAtt);
										cell.setCellType(HSSFCell.CELL_TYPE_STRING);
										columnHeader = new HSSFRichTextString(nameAtt);
										columnHeader.applyFont(fontHeader);
										cell.setCellValue(columnHeader);
										cellIndex++;
									}
								}
								/*FIN NUEVO */
								
								while (fi.hasNext()){
									f = fi.next();
									row = sheet.createRow(y++);
									cellIndex = 0;


//Agregado (Exporta el valor de la columna ID, si es un String que puede convertirse en numero,
//			lo exporta como numero de forma que puedan ordenar por dicha columna en Excel)
									cell = row.createCell((short)cellIndex);
									value = f.getID();
									
									if (value instanceof Number){
										cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
										cell.setCellValue(((Number)value).doubleValue());
									} else if (value instanceof Date){
							            cell.setCellValue((Date)value);
							            cell.setCellStyle(cellStyle);
									} else if (value instanceof Calendar){
							            cell.setCellValue((Date)value);
							            cell.setCellStyle(cellStyle);
									} else {

										try {
											Double valueNumber = Double.valueOf(value.toString()).doubleValue();
											cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
											cell.setCellValue(valueNumber);
										} catch (Exception ex) {
											cell.setCellType(HSSFCell.CELL_TYPE_STRING);
											cell.setCellValue(new HSSFRichTextString(value.toString()));
										}
									}

									cellIndex++;								
//Fin agregado
									
									
									for (z = 0; z < ft.getAttributeCount(); z++){
										
										//if (contPpal.getModel().isNativeAttribute(ft.getAttributeType(z).getName()) &&
										//		!Geometry.class.isAssignableFrom(ft.getAttributeType(z).getType())) {
										if (!Geometry.class.isAssignableFrom(ft.getAttributeType(z).getBinding())) {
										
											cell = row.createCell((short)cellIndex);
											value = f.getAttribute(z);
											
											if (value == null){
												cell.setCellType(HSSFCell.CELL_TYPE_BLANK);
												cell.setCellValue((HSSFRichTextString)null);
											} else if (value instanceof Number){
												cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
												cell.setCellValue(((Number)value).doubleValue());
											} else if (value instanceof Date){

//Agregado (las fechas se exportan con un estilo para que el usuario pueda
//          modificarlo luego en la planilla)
									            cell.setCellValue((Date)value);
									            cell.setCellStyle(cellStyle);
											
//												cell.setCellType(HSSFCell.CELL_TYPE_STRING);
//												cell.setCellValue(new HSSFRichTextString(sdf.format((Date)value)));
// Fin agregado									            

											} else if (value instanceof Calendar){

//Agregado (las fechas se exportan con un estilo para que el usuario pueda
//          modificarlo luego en la planilla)
												
									            cell.setCellValue((Date)value);
									            cell.setCellStyle(cellStyle);
												
//												cell.setCellType(HSSFCell.CELL_TYPE_STRING);
//												cell.setCellValue(new HSSFRichTextString(sdf.format((Calendar)value)));
// Fin agregado									            
												
											} else {
												
												cell.setCellType(HSSFCell.CELL_TYPE_STRING);
												cell.setCellValue(new HSSFRichTextString(value.toString()));
											}
											
											cellIndex++;
										}
									}
									
									/* NUEVO*/
									if (calculatedAttributes != null){
										it = calculatedAttributes.keySet().iterator();
										while(it.hasNext()){
											String nameAtt = (String) it.next();
											
											Map<String,Object> mapa = calculatedAttributes.get(nameAtt);
											String data = (String)mapa.get(f.getID());
											
											cell = row.createCell((short)cellIndex);
											//logger.info("Creando columna " + nameAtt);
											cell.setCellType(HSSFCell.CELL_TYPE_STRING);
											cell.setCellValue(new HSSFRichTextString(data));
											cellIndex++;
										}
									}
									/* FIN NUEVO*/
								}
								if (calculatedAttributes != null)
									calculatedAttributes.clear();
								
//Agregado (si la capa es editable)								
						      //}
//Fin agregado								
								
								
							}
							FileOutputStream fos = new FileOutputStream(file.getAbsolutePath());
							wb.write(fos);
							fos.close();
							
							if (logger.isDebugEnabled())
								logger.debug("Se grabo excel " + file.getName());
						} catch (Exception ex) {
							file = null;
							ex.printStackTrace();
							logger.error(ex);
						}
		            }
		            
		            protected void doUIUpdateLogic() throws RuntimeException {
		            	if (file != null){
		            		javax.swing.JOptionPane.showMessageDialog(contPpal.getVistaPrincipal(), "Planilla generada con exito");
		            	}
//Agregado (mensaje de error si no pudo generar la planilla)
		            	else {
		            		javax.swing.JOptionPane.showMessageDialog(contPpal.getVistaPrincipal(), "ERROR ! No se pudo generar la planilla");
		            	}
//Fin agregado		            	
		            }
				}.start();	
			}
		}
	}

	//---------------------------------------------------------------------------

	
	private class ExportShapeAction extends ExtendedAction{
		private static final long serialVersionUID = 1L;
		UserChooser chooser = new UserChooser("Elija capas a exportar...", JOptionPane.getRootFrame());

		public ExportShapeAction(){
			super("Exportar datos a Shape");
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_S));
		}
		
		public void actionPerformed(ActionEvent e){
			chooser.choose(contPpal.getModel().getNotEmptyTypes(), 300, 200);
			final Object[] layers = chooser.getSelectedOptions();
			final String base = System.getProperty("user.home");
			
			if (layers == null){
				return; // No eligio nada...
			}
				
			new BlockingSwingWorker(contPpal.getVistaPrincipal(), "Creando shapes en " + base, "Espere, por favor...", true){
				Exception exception = null;
				ModelData model = contPpal.getModel();
				//String layers[] = model.getEditableTypes();

				protected void doNonUILogic() throws RuntimeException {
					logger.info("Creando shape files...");
					
					if (layers == null || layers.length == 0){
						exception = new Exception("No existen capas con datos!!!");
					} else {
						String layer;
						File file = null;
						DataStore ds = null;
						FeatureStore fs = null;
						FeatureReader fr = null;
						FeatureCollection fc = null;
						Transaction t = null;
						ShapefileDataStoreFactory sdsf = new ShapefileDataStoreFactory(); 
						Map<String, Object> params = new HashMap<String, Object>();
						
						try{
							for (int i = 0; i < layers.length; i++){
								layer = (String)layers[i];
								file = new File(base + "/" + layer + ".shp");
								logger.info("Creando shape para " + layer + " en " + file.toURI());
								
								params.put("url", file.toURI().toURL());
								ds = sdsf.createNewDataStore(params);
								
								logger.info("Creando esquema...");
								FeatureType ft = model.getSchema(layer);
								Collection<AttributeType> types = new ArrayList<AttributeType>();
								AttributeType at = null;

								for (int j = 0; j < ft.getAttributeCount(); j++){
									at = ft.getAttributeType(j);
									// No admite los external attribute ni los reference attribute
									if (ExternalAttribute.class.isAssignableFrom(at.getBinding()) ||     
									(FeatureReferenceAttribute.class.isAssignableFrom(at.getBinding()) )){ 
										types.add(AttributeTypeFactory.newAttributeType(at.getLocalName(), String.class));
									} else {
										types.add(at);										
									}
								}
								
								SimpleFeatureType sft = new SimpleFeatureType(ft.getTypeName(), ft.getNamespace(), types, Collections.EMPTY_LIST, ft.getDefaultGeometry());
								types = null;
								ds.createSchema(sft);
								
								fr = model.getDataStore().getFeatureReader(new DefaultQuery(layer, Filter.INCLUDE), Transaction.AUTO_COMMIT);
								fc = FeatureCollections.newCollection();
								while (fr.hasNext()){
									fc.add(fr.next());									
								}
								fr.close();
								fs = (FeatureStore)ds.getFeatureSource(layer);
								t = fs.getTransaction();
								
								logger.info("cargando features...");
								fs.addFeatures(fc);
								
								logger.info("commit...");
								t.commit();
								t.close();
								t = null;
								
								ds.dispose();
							}													
						} catch (Exception e){
							exception = e;
							if (t != null){
								try{t.rollback();t.close();}catch (Exception e1){}
							}
						}
					}					
	            }
	            
	            protected void doUIUpdateLogic() throws RuntimeException {
	            	if (exception == null){
	            		javax.swing.JOptionPane.showMessageDialog(contPpal.getVistaPrincipal(), "Shapes creados con exito");
	            	}
	            	else {
	            		javax.swing.JOptionPane.showMessageDialog(contPpal.getVistaPrincipal(), "ERROR! No se pudieron crear los shapes\n" + exception.getMessage());
	            	}
	            }
			}.start();	
		}
	}
	

	private class ManualUsuarioAction extends ExtendedAction{
		private static final long serialVersionUID = 1L;
		private String ubicacionManual = "";
		
		public ManualUsuarioAction(String ubicacionManual){
			super("Manual de usuario");			
			this.ubicacionManual = ubicacionManual;
		}
		
		public void actionPerformed(ActionEvent e){
			
			//imm.gis.core.gui.VistaAyuda va = new imm.gis.core.gui.VistaAyuda(ubicacionManual);
			try {
			 //    File path = new File (ubicacionManual);
			 //    Desktop.getDesktop().open(path);
			     Desktop.getDesktop().browse(new URI(ubicacionManual));
			}catch (Exception ex) {
			     ex.printStackTrace();
			}
	            			
		}
	}
	
	
	private class ExportKMLAction extends ExtendedAction{

		private static final long serialVersionUID = 1L;

		public ExportKMLAction(){
			super("Exportar datos a Google Earth");
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_G));
		}
		
		public void actionPerformed(ActionEvent e){
			try{
				ModelData model = contPpal.getModel();
				String layers[] = model.getNotEmptyTypes();
				HashMap<String,ArrayList<String>> combos = new HashMap<String, ArrayList<String>>();  
				
				for (int i=0; i<layers.length; i++){
					ArrayList<String> atribs = new ArrayList<String>();
					FeatureType ft = model.getSchema(layers[i]);
					for (int j=0; j<ft.getAttributeCount(); j++){
						if (!Geometry.class.isAssignableFrom(ft.getAttributeType(j).getBinding())) 
							atribs.add(ft.getAttributeType(j).getLocalName());
					}
					combos.put(layers[i],atribs);
				}

				SelKMLData kml_data = new SelKMLData(contPpal.getMainGUI(),true,ContAccionesAplicacion.this,combos);
				kml_data.setLocation(200,100);
				kml_data.setVisible(true);
			}
			catch (Exception ex){
				ex.printStackTrace();
			}
		}
	}
	
	public void pgsql2kml(String output,String layer,String label){
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(output));
			out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
			out.write("<kml xmlns=\"http://earth.google.com/kml/2.2\">\n\n");
			out.write("<Document>\n");
			out.write("  <name>" + layer + "</name>\n\n");
			out.write("  <description><![CDATA[Capa generada a partir del layer <B>" + layer + "</B> del editor geogr�fico]]></description>\n\n");
			out.write("  <Style id=\"style\">\n");
			out.write("  </Style>\n\n");

			ModelData model = contPpal.getModel();
			FeatureType ft = model.getSchema(layer);
			FeatureCollection fc = model.getFeatureSource(layer).getFeatures();
			FeatureIterator fi = fc.features();
			
			int att_label = ft.find(label);
			int att_geo = -1;
			for (int i=0; i<ft.getAttributeCount(); i++){
				if (Geometry.class.isAssignableFrom(ft.getAttributeType(i).getBinding())) {
					att_geo = i;
					break;
				}
			}

			Object[] parameters = new Object[1];
			Class[] classes = new Class[] {String.class};
			
			while (fi.hasNext()){
				Feature f = fi.next();
				out.write("  <Placemark>\n");
				if (att_label == -1)
					out.write("    <name>" + f.getID() + "</name>\n");
				else
					out.write("    <name>" + f.getAttribute(att_label) + "</name>\n");
				out.write("    <description><![CDATA[<DIV><FONT color=\"#000066\">\n      ");
				if (att_label!=-1)
					out.write("<B>ID: "  + f.getID() + "</B><BR> ");				
				for (int i=0; i<ft.getAttributeCount(); i++){
					if (!Geometry.class.isAssignableFrom(ft.getAttributeType(i).getBinding()) && att_label!=i) {
						out.write(ft.getAttributeType(i).getLocalName() + ": "  + f.getAttribute(i) + " <BR> ");
					}
				}
				out.write("\n      </FONT></DIV>]]></description>\n");
			    out.write("    <styleUrl>#style</styleUrl>\n");
			    parameters[0] = f.getAttribute(att_geo).toString();
				String kml = (String)FactoryClientFacade.getIClientFacade().execute(
						"SIGServerEAR/EJBGISServicesBean/remote","asKML",parameters,classes);
				out.write("    " + kml + "\n");
				out.write("  </Placemark>\n\n");
			}
			out.write("</Document>\n");
			out.write("</kml>\n");
	        out.close();
		} 
		catch (Exception ex) {
			ex.printStackTrace();
		}

	}
}
