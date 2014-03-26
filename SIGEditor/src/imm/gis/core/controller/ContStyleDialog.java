package imm.gis.core.controller;

import imm.gis.AppContext;
import imm.gis.core.gui.LinePatternRenderer;
import imm.gis.core.gui.StyleEditor;
import imm.gis.core.model.StyleModel;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;

import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Fill;
import org.geotools.styling.Graphic;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.Mark;
import org.geotools.styling.PointSymbolizer;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.SLDTransformer;
import org.geotools.styling.Stroke;
import org.geotools.styling.Style;
import org.geotools.styling.StyleBuilder;
import org.geotools.styling.Symbolizer;
import org.geotools.styling.TextSymbolizer;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.expression.Literal;

public class ContStyleDialog {
	static private Logger log = Logger.getLogger(ContStyleDialog.class.getName());
	private ContPpal contPpal;
	private HashMap<JComponent, Object> comps = new HashMap<JComponent, Object>();
	private boolean doEvents = true;
	private FilterFactory2 ff;
	private StyleEditor dialog;
	private StyleBuilder styleBuilder = new StyleBuilder();
	private SLDTransformer sldTransformer = new SLDTransformer();
	private StyleModel styleModel;
	private String editingLayer;
	private Style workingStyle;
	private Style originalStyle;
	private String originalStyleName;
	
	private boolean hasApplied = false;
	
	private float lineStyles [][] = {
			{}, // linea entera
			{20, 6}, 
			{10, 2},
			{20, 6, 10, 6} 
	};
	
	public ContStyleDialog(ContPpal c){
		log.debug("ContStyleDialog Constructor");
		contPpal = c;
		styleModel = AppContext.getInstance().getStyleModel();
		ff = CommonFactoryFinder.getFilterFactory2(GeoTools.getDefaultHints());
		sldTransformer.setIndentation(4);
		dialog = new StyleEditor(contPpal.getVistaPrincipal());
		dialog.setDefaultCloseOperation(javax.swing.JDialog.DO_NOTHING_ON_CLOSE);
		imm.gis.core.gui.GuiUtils.centerWindowOnScreen(dialog);
		dialog.cmbPatronLinea.setRenderer(new LinePatternRenderer());
		for (int i = 0; i < lineStyles.length; i++){
			dialog.cmbPatronLinea.addItem(lineStyles[i]);			
		}
		
		dialog.sldTransparencia.setMinimum(0);
		dialog.sldTransparencia.setMaximum(100);
		
		dialog.sldGrosorLinea.setMinimum(0);
		dialog.sldGrosorLinea.setMaximum(10);
		
		dialog.sldTamanioVertice.setMinimum(0);
		dialog.sldTamanioVertice.setMaximum(20);
		
		dialog.chkRelleno.setSelected(true);
		dialog.chkPatronRelleno.setSelected(true);
		dialog.chkLinea.setSelected(true);
		dialog.chkPatronLinea.setSelected(true);
		dialog.chkSincroLineaRelleno.setSelected(true);
		dialog.chkTamanioVertice.setSelected(true);
		setListeners();
	}
	
	private void setListeners(){
		dialog.radPorDefecto.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e){
				if (!doEvents) return;
				if (dialog.radPorDefecto.isSelected()){
					dialog.cmbPersonalizados.setEnabled(false);
					editStyleByName(editingLayer, StyleModel.DEFAULT, false);
				}
			}
		});
		
		dialog.radPersonalizado.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e){
				if (!doEvents) return;
				if (dialog.radPersonalizado.isSelected()) {
					
					dialog.cmbPersonalizados.setEnabled(true);
					
					if (dialog.cmbPersonalizados.getSelectedIndex() == -1){ // no hay estilos personalizados
						dialog.cmbPersonalizados.setSelectedItem("Estilo personalizado");
						editStyle(editingLayer, styleModel.getCurrentStyle(editingLayer), true);
					}
					else {
						editStyleByName(editingLayer, (String)dialog.cmbPersonalizados.getSelectedItem(), true);												
					}					
				}
			}
		});
		
		dialog.chkRelleno.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e){
				boolean selected = e.getStateChange() == ItemEvent.SELECTED;
				dialog.txtRelleno.setEnabled(selected);
				dialog.butRelleno.setEnabled(selected);
				//change(true);
			}
		});
		dialog.chkPatronRelleno.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e){
				boolean selected = e.getStateChange() == ItemEvent.SELECTED;
				dialog.cmbPatronRelleno.setEnabled(selected);
				//change(true);
			}			
		});
		dialog.chkLinea.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e){
				boolean selected = e.getStateChange() == ItemEvent.SELECTED;
				dialog.txtLinea.setEnabled(selected);
				dialog.butLinea.setEnabled(selected);
				//change(true);
			}			
		});
		dialog.chkPatronLinea.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e){
				boolean selected = e.getStateChange() == ItemEvent.SELECTED;
				dialog.cmbPatronLinea.setEnabled(selected);				
				//change(true);
			}			
		});
		
		dialog.chkSincroLineaRelleno.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e){
				boolean selected = e.getStateChange() == ItemEvent.SELECTED;
				if (selected){
					Color relleno = dialog.txtRelleno.getColor();
					Color linea = dialog.txtLinea.getColor();
					if (!relleno.equals(linea)){
						dialog.txtLinea.setColor(relleno);
					}
				}
				//change(true);
			}			
		});
		
		dialog.chkTamanioVertice.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e){
				boolean selected = e.getStateChange() == ItemEvent.SELECTED;
				dialog.txtTamanioVertice.setEnabled(selected);				
				dialog.sldTamanioVertice.setEnabled(selected);
				//change(true);
			}			
		});
		
		dialog.butLinea.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				Color color = JColorChooser.showDialog(dialog, "Elija un color de linea", dialog.txtLinea.getColor());
				if (color != null){
					Stroke stroke = (Stroke)comps.get(dialog.txtLinea);
					String strColor = codeColor(color);
					stroke.setColor(ff.literal(strColor));
					dialog.txtLinea.setColor(color);
					if (dialog.chkSincroLineaRelleno.isSelected()){
						dialog.txtRelleno.setColor(color);
					}
					//change(true);
				}
			}
		});
		
		dialog.butRelleno.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				Color color = JColorChooser.showDialog(dialog, "Elija un color de relleno", dialog.txtRelleno.getColor());
				if (color != null){
					Fill fill = (Fill)comps.get(dialog.txtRelleno);
					String strColor = codeColor(color);
					fill.setColor(ff.literal(strColor));
					dialog.txtRelleno.setColor(color);
					if (dialog.chkSincroLineaRelleno.isSelected()){
						dialog.txtLinea.setColor(color);
					}
					//change(true);
				}
			}
		});
		
		dialog.sldTransparencia.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e){
				if (!doEvents) return;

				int value = dialog.sldTransparencia.getValue();
				double newVal = value * 0.01;
				log.info("Seteando valor de transparencia a " + newVal);
				dialog.txtTransparencia.setText(String.valueOf(value));
				Object obj = comps.get(dialog.sldTransparencia);
				Literal le = ff.literal(newVal);
				if (obj instanceof Fill){
					((Fill)obj).setOpacity(le);					
				} else if (obj instanceof Stroke){
					((Stroke)obj).setOpacity(le);
				}
				//change(true);
			}
		});
		
		dialog.sldGrosorLinea.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e){
				if (!doEvents) return;

				int value = dialog.sldGrosorLinea.getValue();
				log.info("Seteando grosor de linea a " + value);
				dialog.txtGrosorLinea.setText(String.valueOf(value));
				Stroke stroke = (Stroke)comps.get(dialog.sldGrosorLinea);
				stroke.setWidth(ff.literal(value));
				//change(true);
			}
		});

		dialog.sldTamanioVertice.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e){
				if (!doEvents) return;

				int value = dialog.sldTamanioVertice.getValue();
				log.info("Seteando tamano vertice a " + value);
				dialog.txtTamanioVertice.setText(String.valueOf(value));
				Graphic graphic = (Graphic)comps.get(dialog.sldTamanioVertice);
				graphic.setSize(ff.literal(value));
				//change(true);
			}
		});
		
		dialog.cmbPatronLinea.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if (!doEvents) return;
				
				Stroke stroke = (Stroke)comps.get(dialog.cmbPatronLinea);
				float dash[] = (float[])lineStyles[dialog.cmbPatronLinea.getSelectedIndex()];
				log.info("Seteando patron linea " + dialog.cmbPatronLinea.getSelectedIndex());
				stroke.setDashArray(dash);
				//change(true);
			}
		});
		
		dialog.butAplicar.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				dialog.butCancelar.setEnabled(true);

				try {
					if (dialog.radPorDefecto.isSelected()) {
						styleModel.setCurrentStyle(editingLayer, StyleModel.DEFAULT);
						styleModel.updateCurrentStyle(editingLayer, styleModel.getDefaultStyle(editingLayer));
					}
					else {
						if (!styleModel.containsStyle(editingLayer, (String)dialog.cmbPersonalizados.getSelectedItem())) {
							styleModel.setNamedStyle(editingLayer, (String)dialog.cmbPersonalizados.getSelectedItem(), workingStyle);
						}
						
						styleModel.setCurrentStyle(editingLayer, (String)dialog.cmbPersonalizados.getSelectedItem());
						styleModel.updateCurrentStyle(editingLayer, workingStyle);
					}
					hasApplied = true;
					
					contPpal.getContMapa().refresh();
				}
				catch (Exception ex) {
					contPpal.getVistaPrincipal().showError("Error al actualizar el estilo");
					ex.printStackTrace();
				}
				
			}
		});
		
		dialog.butCancelar.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				
				try {
					styleModel.setCurrentStyle(editingLayer, originalStyleName);
					styleModel.updateCurrentStyle(editingLayer, originalStyle);
					contPpal.getContMapa().refresh();
					dialog.butCancelar.setEnabled(false);
				}
				catch (Exception ex) {
					contPpal.getVistaPrincipal().showError("ERROR", "No se pudieron deshacer los cambios");
					ex.printStackTrace();
				}
				//change(false);
			}
		});
		
		dialog.butGuardar.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				String selectedStyleName = (String)dialog.cmbPersonalizados.getSelectedItem();

				if (selectedStyleName != null  && selectedStyleName.length() > 0){
					String msg;
					if (styleModel.containsStyle(editingLayer, selectedStyleName)){
						msg = "Sobreescribe el estilo " + selectedStyleName;
					} else {
						msg = "Guarda el nuevo estilo " + selectedStyleName;
					}
					int res = javax.swing.JOptionPane.showConfirmDialog(dialog, msg, "Guardar estilo", javax.swing.JOptionPane.YES_NO_OPTION);
					if (res == javax.swing.JOptionPane.YES_OPTION){
						java.io.FileWriter fileWriter;
						java.io.File userDir = new java.io.File("user");
						try {
//							log.info(System.getProperty("user.dir"));
							log.info("Chequeando existencia de " + userDir.toString());
							if (!userDir.exists()){
								log.info("No existe el directorio user/");
								if (!userDir.mkdir()){
									log.info("No se pudo crear directorio user/");
									JOptionPane.showMessageDialog(dialog, "No se pudo crear el directorio\npara guardar estilos personalizados", "Guardando estilo personalizado", JOptionPane.ERROR_MESSAGE);
									return;
								}
							} 
							fileWriter = new java.io.FileWriter("user/" + selectedStyleName + ".sld");
							sldTransformer.transform(workingStyle, fileWriter);
							fileWriter.close();
							styleModel.setNamedStyle(editingLayer, selectedStyleName, workingStyle);	
							//change(false);
						} catch (Exception e1) {
							e1.printStackTrace();
						} finally{
							dialog.butCancelar.setEnabled(false);
						}
					}											
				} else {
					javax.swing.JOptionPane.showMessageDialog(dialog, "Es necesario elegir un estilo o poner el nombre del nuevo");					
				}
			}
		});
		
		dialog.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent evt){
				dialog.setVisible(false);
			}
		});
		
		dialog.cmbPersonalizados.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if (dialog.cmbPersonalizados.getSelectedIndex() >= 0){
					editStyleByName(editingLayer, (String)dialog.cmbPersonalizados.getSelectedItem(), true);									
				}
			}
		});
		
		/*
		dialog.butSetPorDefecto.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				imm.gis.conf.PrefManager prefs = imm.gis.conf.PrefManager.getInstance();

				if (dialog.radPorDefecto.isEnabled()){
					prefs.removePreference("Style" + editingLayer);
				} else {
					styleModel.setDefaultStyle(editingLayer, workingStyle);
					
					String selectedStyleName = (String)dialog.cmbPersonalizados.getSelectedItem();
					if (selectedStyleName != null  && selectedStyleName.length() > 0){
						log.info("Guardando preferencia " + "Style" + editingLayer);
						prefs.setString("Style" + editingLayer, selectedStyleName);
					}
				}
			}
		});
		*/
	}
	
	public JDialog getDialog(){
		return dialog;
	}

	public void editStyle(String layer){
		editingLayer = layer;
		originalStyleName = styleModel.getCurrentStyleName(editingLayer);
		originalStyle = styleModel.getCurrentStyle(editingLayer);
		
		loadUserStyles(layer);
		
		if (styleModel.isInDefaultStyle(layer)) {
			dialog.radPorDefecto.setSelected(true);
		}
		else {
			dialog.cmbPersonalizados.setSelectedItem(styleModel.getCurrentStyleName(editingLayer));
			dialog.radPersonalizado.setSelected(true);			
		}
		dialog.setVisible(true);
	}
	
	private void editStyleByName(String layer, String styleName, boolean edit){
		editStyle(layer, styleModel.getNamedStyle(layer, styleName), edit);
	}
	
	private void editStyle(String layer, Style st, boolean edit){
		dialog.butCancelar.setEnabled(false);
		workingStyle = copyStyle(st);
		FeatureTypeStyle fts = workingStyle.getFeatureTypeStyles()[0];
		fts.setFeatureTypeName(layer);
		dialog.setTitle("Estilo " + st.getTitle());
		doEvents = false;
		initDialog(layer, edit);
		log.info("Editar " + fts.getTitle());
		Rule rules[] = fts.getRules();
		Rule rule;
		for (int i = 0; i < rules.length; i++){
			rule = rules[i];
			log.info("Recorriendo rule " + rule.getTitle());
			Symbolizer syms[] = rule.getSymbolizers();
			for (int j = 0; j < syms.length; j++){
				processSymbolizer(syms[j], edit);
			}			
		}
		doEvents = true;
		dialog.setVisible(true);
	}

	/*
	private void change(boolean c){
		dialog.butSetPorDefecto.setEnabled(!c);
	}
	*/
	private void processSymbolizer(Symbolizer sym, boolean edit){
		// Componentes generales para todos
		
		dialog.lblTransparencia.setEnabled(edit);
		dialog.sldTransparencia.setEnabled(edit);
		dialog.txtTransparencia.setEnabled(edit);
		
		dialog.lblGrosorLinea.setEnabled(edit);
		dialog.sldGrosorLinea.setEnabled(edit);
		dialog.txtGrosorLinea.setEnabled(edit);
		
		if (sym instanceof LineSymbolizer){
			processLineSymbolizer((LineSymbolizer)sym, edit);
		} else if (sym instanceof PointSymbolizer){
			processPointSymbolizer((PointSymbolizer)sym, edit);
		} else if (sym instanceof PolygonSymbolizer){
			processPolygonSymbolizer((PolygonSymbolizer)sym, edit);
		} else if (sym instanceof TextSymbolizer){
			processTextSymbolizer((TextSymbolizer)sym, edit);
		} else {
			log.info("Tipo de simbolo no implementado: " + sym.getClass().getName());
		}
	}

	private void processLineSymbolizer(LineSymbolizer line, boolean edit){
		log.info("Recorriendo LineSymbolizer");
		
		Stroke stroke = line.getStroke();
		if (stroke != null){
			if (edit){
				dialog.chkLinea.setEnabled(true);
				dialog.chkPatronLinea.setEnabled(true);			
				dialog.chkLinea.setSelected(true);
				dialog.chkPatronLinea.setSelected(true);				
			}
			setOpacity(stroke);
			setLineWidthColor(stroke);
			setLinePattern(stroke);
		}		
	}

	private void processPointSymbolizer(PointSymbolizer point, boolean edit){
		log.info("Recorriendo PointSymbolizer");
		Graphic graphic = point.getGraphic();
		
		if (graphic != null){
			if (edit){
				dialog.chkRelleno.setEnabled(true);
				dialog.chkLinea.setEnabled(true);
				dialog.chkLinea.setSelected(true);
				dialog.chkPatronLinea.setEnabled(true);			
				dialog.chkPatronLinea.setSelected(true);
				dialog.chkPatronRelleno.setEnabled(true);
				dialog.chkPatronRelleno.setSelected(true);
				dialog.chkTamanioVertice.setEnabled(true);
				dialog.chkTamanioVertice.setSelected(true);				
			}
			
			Mark mark = point.getGraphic().getMarks()[0];
			Stroke stroke = mark.getStroke();
			if (stroke != null){
				setLineWidthColor(stroke);
				setLinePattern(stroke);
			}

			Fill fill = mark.getFill();
			if (fill != null){
				int intColor = decodeColor((String)((Literal)fill.getColor()).getValue());
				Color color = new Color(intColor);
				log.info("Color relleno: " + color);			
				dialog.txtRelleno.setColor(color);
				comps.put(dialog.txtRelleno, fill);
				setOpacity(fill);
				Fill copyFill = styleBuilder.createFill(color);
				copyFill.setOpacity(fill.getOpacity());
			}

			Number size = (Number)((Literal)point.getGraphic().getSize()).getValue();
			dialog.sldTamanioVertice.setValue(size.intValue());
			dialog.txtTamanioVertice.setText(size.toString());
			comps.put(dialog.sldTamanioVertice, point.getGraphic());
		}
	}

	private void processPolygonSymbolizer(PolygonSymbolizer pol, boolean edit){
		log.info("Recorriendo PolygonSymbolizer");
		if (edit){
			dialog.chkLinea.setEnabled(true);
			dialog.chkPatronLinea.setEnabled(true);			
			dialog.chkRelleno.setEnabled(true);
			dialog.chkPatronRelleno.setEnabled(true);
			dialog.chkRelleno.setSelected(true);
			dialog.chkPatronRelleno.setSelected(true);
			dialog.chkLinea.setSelected(true);
			dialog.chkPatronLinea.setSelected(true);			
		}

		Stroke stroke = pol.getStroke();
		if (stroke != null){
			setLineWidthColor(stroke);
			setLinePattern(stroke);
		}

		Fill fill = pol.getFill();
		if (fill != null){
			int intColor = decodeColor((String)((Literal)fill.getColor()).getValue());
			Color color = new Color(intColor);
			log.info("Color relleno: " + color);			
			dialog.txtRelleno.setColor(color);
			comps.put(dialog.txtRelleno, fill);
			setOpacity(fill);
		}
	}

	private void processTextSymbolizer(TextSymbolizer txt, boolean edit){
		log.info("Recorriendo TextSymbolizer");
//		LiteralExpression le = (LiteralExpression)txt.getFill().getColor();
//		int color = decodeColor((String)le.getLiteral());
//		AttributeExpression ae = (AttributeExpression)txt.getLabel();
	}

	private void setOpacity(Fill fill){
		comps.put(dialog.sldTransparencia, fill);
		setOpacityExpression((Literal)fill.getOpacity());
	}
	
	private void setOpacity(Stroke stroke){
		comps.put(dialog.sldTransparencia, stroke);
		setOpacityExpression((Literal)stroke.getOpacity());		
	}
	
	private void setOpacityExpression(Literal le){
		if (le != null){
			Number number = (Number)le.getValue();
			int value = Math.round(number.floatValue() * 100);
			log.info("Seteando transparencia a " + value);
			dialog.sldTransparencia.setValue(value);
			dialog.txtTransparencia.setText(String.valueOf(value));					
		}			
	}
	
	private void setLineWidthColor(Stroke stroke){
		Object obj = ((Literal)stroke.getWidth()).getValue();
		Number number = (Number)obj;
		int value = Math.round(number.floatValue());
		log.info("Seteando grosor linea a " + value);
		dialog.sldGrosorLinea.setValue(value);
		comps.put(dialog.sldGrosorLinea, stroke);

		obj = ((Literal)stroke.getColor()).getValue();
		log.info("Seteando color linea a " + obj);
		dialog.txtLinea.setColor(new Color(decodeColor((String)obj)));		
		comps.put(dialog.txtLinea, stroke);
	}
	
	private void setLinePattern(Stroke stroke){
		log.info("Seteando patron de linea");
		float dash[] = stroke.getDashArray();
		boolean equals;
		
		if (dash.length == 0){
			log.info("Patron de linea por defecto");
			dialog.cmbPatronLinea.setSelectedIndex(0);
		} else {
			for (int i = 0; i < lineStyles.length; i++){
				if (dash.length == lineStyles[i].length){
					equals = true;
					for (int j = 0; j < dash.length && equals; j++){
						equals = dash[j] == lineStyles[i][j];
					}
					if (equals){
						log.info("Patron de linea dashed " + i);
						dialog.cmbPatronLinea.setSelectedIndex(i);
						break;
					}					
				}
			}			
		}
		comps.put(dialog.cmbPatronLinea, stroke);
	}
	
	private int decodeColor(String rgb){
		return Integer.parseInt(rgb.substring(1, rgb.length()), 16);
	}

	private String codeColor(Color color){
		return "#" + Integer.toHexString(color.getRGB() & 0x00ffffff);
	}
	
	private void initDialog(String layer, boolean edit){
		dialog.txtLinea.setColor(null);
		dialog.txtRelleno.setColor(null);
		dialog.cmbPatronLinea.setSelectedItem(null);
		dialog.cmbPatronRelleno.setSelectedItem(null);
		
		dialog.chkRelleno.setSelected(false);
		dialog.chkPatronRelleno.setSelected(false);
		dialog.chkLinea.setSelected(false);
		dialog.chkPatronLinea.setSelected(false);
		dialog.chkSincroLineaRelleno.setSelected(false);
		dialog.chkTamanioVertice.setSelected(false);

		dialog.chkRelleno.setEnabled(false);
		dialog.chkPatronRelleno.setEnabled(false);
		dialog.chkLinea.setEnabled(false);
		dialog.chkPatronLinea.setEnabled(false);
		dialog.chkSincroLineaRelleno.setEnabled(false);
		dialog.chkTamanioVertice.setEnabled(false);

		dialog.butAplicar.setEnabled(true);
		
		dialog.butGuardar.setEnabled(edit);
		dialog.butCancelar.setEnabled(hasApplied);
		
		//change(false);
	}	

	private void loadUserStyles(String layer){
		dialog.cmbPersonalizados.removeAllItems();
		
		String styles[] = styleModel.getUserStyles(layer);
		for (int i = 0; i < styles.length; i++){
			dialog.cmbPersonalizados.addItem(styles[i]);
		}
	}
	
	private Style copyStyle(Style style){
		Style copy = styleBuilder.createStyle();
		
		copy.setAbstract(style.getAbstract());
		copy.setDefault(style.isDefault());
		copy.setName(style.getName());
		copy.setNote(style.getNote());
		copy.setTitle(style.getTitle());
		copyFeatureStyles(style, copy);
		
		return copy;
	}
	
	private void copyFeatureStyles(Style origin, Style destiny){
		FeatureTypeStyle fts[] = origin.getFeatureTypeStyles();
		
		if (fts != null && fts.length > 0){
			FeatureTypeStyle copy[] = new FeatureTypeStyle[fts.length];

			for (int i = 0; i < fts.length; i++){
				copy[i] = copyFeatureStyle(fts[i]);
			}
			
			destiny.setFeatureTypeStyles(copy);
		}
	}
	
	private FeatureTypeStyle copyFeatureStyle(FeatureTypeStyle fts){
		Rule rules[] = fts.getRules();
		Rule rulesCopy[] = new Rule[rules.length];
		FeatureTypeStyle copy = null;
		
		for (int i = 0; i < rules.length; i++){
			rulesCopy[i] = copyRule(rules[i]);
		}
		
		copy = styleBuilder.createFeatureTypeStyle(fts.getFeatureTypeName(), rulesCopy);
		copy.setAbstract(fts.getAbstract());
		copy.setNote(fts.getNote());
		copy.setTitle(fts.getTitle());
		copy.setSemanticTypeIdentifiers(fts.getSemanticTypeIdentifiers());

		return copy;
	}
	
	private Rule copyRule(Rule rule) {
		Rule copy = null;
		Symbolizer sym;
		Symbolizer symbs[] = rule.getSymbolizers();
		Symbolizer copySymbs[] = new Symbolizer[symbs.length];
		
		for (int i = 0; i < symbs.length; i++){
			sym = symbs[i];
			if (sym instanceof LineSymbolizer){
				copySymbs[i] = copyLineSymbolizer((LineSymbolizer)sym);				
			} else if (sym instanceof PointSymbolizer){
				copySymbs[i] = copyPointSymbolizer((PointSymbolizer)sym);				
			} else if (sym instanceof PolygonSymbolizer){
				copySymbs[i] = copyPolygonSymbolizer((PolygonSymbolizer)sym);				
			} else if (sym instanceof TextSymbolizer){
				copySymbs[i] = copyTextSymbolizer((TextSymbolizer)sym);				
			} else {
				log.info("Tipo de symbolizer no implementado " + sym);
				throw new RuntimeException("Tipo de symbolizer no implementado");
			}			
		}

		copy = styleBuilder.createRule(copySymbs, rule.getMinScaleDenominator(), rule.getMaxScaleDenominator());
		copy.setName(rule.getName());
		copy.setAbstract(rule.getAbstract());
		copy.setNote(rule.getNote());
		copy.setFilter(rule.getFilter());
		copy.setIsElseFilter(rule.hasElseFilter());
		
		return copy;
	}
	
	private Symbolizer copyLineSymbolizer(LineSymbolizer sym){
		LineSymbolizer copy = styleBuilder.createLineSymbolizer();
		
		copy.setGeometryPropertyName(sym.getGeometryPropertyName());
		copy.setNote(sym.getNote());
		copy.setStroke(copyStroke(sym.getStroke()));
		
		return copy;
	}

	private Symbolizer copyPointSymbolizer(PointSymbolizer sym){
		PointSymbolizer copy = styleBuilder.createPointSymbolizer();
		
		copy.setGeometryPropertyName(sym.getGeometryPropertyName());
		copy.setNote(sym.getNote());
		copy.setGraphic(copyGraphic(sym.getGraphic()));
			
		return copy;
	}

	private PolygonSymbolizer copyPolygonSymbolizer(PolygonSymbolizer sym){
		PolygonSymbolizer copy = styleBuilder.createPolygonSymbolizer();
		
		copy.setGeometryPropertyName(sym.getGeometryPropertyName());
		copy.setNote(sym.getNote());
		copy.setFill(copyFill(sym.getFill()));
		copy.setStroke(copyStroke(sym.getStroke()));
			
		return copy;
	}

	private TextSymbolizer copyTextSymbolizer(TextSymbolizer sym){
		TextSymbolizer copy = styleBuilder.createTextSymbolizer();
		
			
		return copy;
	}

	private Stroke copyStroke(Stroke stroke){
		Stroke copy = styleBuilder.createStroke();
		
		copy.setColor(stroke.getColor());
		copy.setDashArray(stroke.getDashArray());
		copy.setDashOffset(stroke.getDashOffset());
		copy.setGraphicFill(stroke.getGraphicFill());
		copy.setGraphicStroke(stroke.getGraphicStroke());
		copy.setLineCap(stroke.getLineCap());
		copy.setLineJoin(stroke.getLineJoin());
		copy.setNote(stroke.getNote());
		copy.setOpacity(stroke.getOpacity());
		copy.setWidth(stroke.getWidth());
		
		return copy;
	}
	
	private Graphic copyGraphic(Graphic graph){
		Graphic copy = styleBuilder.createGraphic();
		Mark marks[] = graph.getMarks();
		Mark copyMarks[] = new Mark[marks.length];
		Mark mark, copyMark;
		
		for (int i = 0; i < marks.length; i++){
			mark = marks[i];
			copyMark = styleBuilder.createMark((String)((Literal)mark.getWellKnownName()).getValue());
			copyMark.setStroke(copyStroke(mark.getStroke()));
			copyMark.setFill(copyFill(mark.getFill()));
			copyMarks[i] = copyMark;
		}
		copy.setOpacity(graph.getOpacity());
		copy.setMarks(copyMarks);
		copy.setSize(graph.getSize());
		
		return copy;
	}
	
	private Fill copyFill(Fill fill){
		Fill copy = styleBuilder.createFill();
		copy.setBackgroundColor(fill.getBackgroundColor());
		copy.setColor(fill.getColor());
		copy.setGraphicFill(fill.getGraphicFill());
		copy.setNote(fill.getNote());
		copy.setOpacity(fill.getOpacity());
		
		return copy;
	}
}
