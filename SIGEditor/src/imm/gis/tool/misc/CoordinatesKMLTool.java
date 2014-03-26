package imm.gis.tool.misc;

import imm.gis.core.gui.GuiUtils;
import imm.gis.core.gui.VistaPpal;
import imm.gis.core.gui.worker.BlockingSwingWorker;
import imm.gis.core.interfaces.ICoreAccess;
import imm.gis.tool.AbstractTool;

import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JOptionPane;

import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;

public class CoordinatesKMLTool extends AbstractTool {
	private Cursor cursor;
	private ICoreAccess coreAccess;
	
	public CoordinatesKMLTool(ICoreAccess coreAccess) {
		this.coreAccess = coreAccess;

		Toolkit tkt = Toolkit.getDefaultToolkit();
		
		cursor = tkt.createCustomCursor(GuiUtils.loadIcon("Drag32.gif").getImage(), new Point(7, 7),"");
	}

	public Cursor getCursor() {
		return cursor;
	}

	public void mouseClicked(final Coordinate c) {
		
		try{
			final String titulo = (String)JOptionPane.showInputDialog(                    
                    "Ingresar una descripción de la ubicación para ser vista en Google Earth",                    
                    "Punto 1");


			if ((titulo != null) && (titulo.length() > 0)) {
						
				new BlockingSwingWorker((VistaPpal)coreAccess.getIUserInterface(), "Generando KML", "Espere, por favor...", true){
					Exception exception = null;
					
		            protected void doNonUILogic() throws RuntimeException {
						
						try {
				
							GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(),32721);
							Geometry point = geometryFactory.createPoint(c);
							
							CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:4326"); 
							CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:32721");  
							MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS); 
							Geometry targetGeometry = JTS.transform(point, transform);
							double x = ((com.vividsolutions.jts.geom.Point)targetGeometry).getCoordinate().x;
							double y = ((com.vividsolutions.jts.geom.Point)targetGeometry).getCoordinate().y;
							
											
							if (y < -35){
								//A veces pasa que la transformacion de sistemas de coordenadas intercambia
								//los valores de latitud y longitud
								double tmp = x;
								x = y;
								y = tmp;
							}
							
							File kml = File.createTempFile("coordenadas", ".kml" );
							String absolutePath = kml.getAbsolutePath();
				    	    System.out.println("KML path : " + absolutePath);
							
							BufferedWriter out = new BufferedWriter(new FileWriter(kml));
							out.write("<kml xmlns=\"http://www.opengis.net/kml/2.2\" xmlns:gx=\"http://www.google.com/kml/ext/2.2\" xmlns:kml=\"http://www.opengis.net/kml/2.2\" xmlns:atom=\"http://www.w3.org/2005/Atom\">" +
									"<Placemark>" +
									"<Style id=\"sh_blu-blank\">" +
									"<IconStyle>" +
									"<scale>1.3</scale>" +
									"<Icon>" +
									"<href>http://maps.google.com/mapfiles/kml/paddle/blu-blank.png</href>" +
									"</Icon>" +
									"<hotSpot x=\"32\" y=\"1\" xunits=\"pixels\" yunits=\"pixels\"/>" +
									"</IconStyle>" +
									"<ListStyle>" +
									"<ItemIcon>" +
									"<href>http://maps.google.com/mapfiles/kml/paddle/blu-blank-lv.png</href>" +
									"</ItemIcon>" +
									"</ListStyle>" +
									"</Style>" +
									"<Style id=\"sn_blu-blank\">" +
									"<IconStyle>" +
									"<scale>1.1</scale>" +
									"<Icon>" +
									"<href>http://maps.google.com/mapfiles/kml/paddle/blu-blank.png</href>" +
									"</Icon>" +
									"<hotSpot x=\"32\" y=\"1\" xunits=\"pixels\" yunits=\"pixels\"/>" +
									"</IconStyle>" +
									"<ListStyle>" +
									"<ItemIcon>" +
									"<href>http://maps.google.com/mapfiles/kml/paddle/blu-blank-lv.png</href>" +
									"</ItemIcon>" +
									"</ListStyle>" +
									"</Style>" +
									"<StyleMap id=\"msn_blu-blank\">" +
									"<Pair>" +
									"<key>normal</key>" +
									"<styleUrl>#sn_blu-blank</styleUrl>" +
									"</Pair>" +
									"<Pair>" +
									"<key>highlight</key>" +
									"<styleUrl>#sh_blu-blank</styleUrl>" +
									"</Pair>" +
									"</StyleMap>" +
									"<name>"+titulo+"</name>	" +
									"<LookAt>" +
									"<longitude>"+x+"</longitude>" +
									"<latitude>"+y+"</latitude>" +
									"<altitude>0</altitude>" +
									"<range>1000</range>" +
									"<tilt>0</tilt>" +
									"<heading>0</heading>" +
									"<altitudeMode>relativeToGround</altitudeMode>" +
									"</LookAt>" +
									"<styleUrl>#msn_blu-blank</styleUrl> " +
									"<Point>" +
									"<coordinates>"+x+","+y+"</coordinates>" +
									"</Point>" +
									"</Placemark>" +
									"</kml>");
							out.close();
							Desktop.getDesktop().open(kml);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							exception = e;
							e.printStackTrace();
						}
						
					}
		            
		            protected void doUIUpdateLogic() throws RuntimeException {
		            	if (exception != null){		            		
		            		javax.swing.JOptionPane.showMessageDialog((VistaPpal)coreAccess.getIUserInterface(), "ERROR! No se pudo abrir Google Earth\n" + exception.getMessage());
		            	}
		            }
		            		            
				}.start();				
				
			}
		}
		catch (Exception ex){
			ex.printStackTrace();
		}
		
	}
}
