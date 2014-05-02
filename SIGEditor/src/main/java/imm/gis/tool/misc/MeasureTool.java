package imm.gis.tool.misc;

import imm.gis.core.controller.ContUtils;
import imm.gis.core.gui.GuiUtils;
import imm.gis.core.interfaces.ICoreAccess;
import imm.gis.edition.util.CoordinateCollector;
import imm.gis.gui.toolbar.ToolBarFactory;
import imm.gis.tool.AbstractTool;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;

import javax.swing.JLabel;
import javax.swing.JToolBar;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

public class MeasureTool extends AbstractTool implements ActionListener {

	private CoordinateCollector measureCoordinates;
	
	private ICoreAccess coreAccess;
	private Cursor cursor;
	
	private JToolBar toolBar;
	public JLabel lblLargoTotal = new JLabel("0 mts.");
	public JLabel lblLargoTramo = new JLabel("0 mts.");
	public JLabel lblArea = new JLabel("0 mts. cuad.");

	private double distTotal = 0;
	private double distTramo = 0;
	private double area = 0;

	
	public MeasureTool(ICoreAccess coreAccess) {
		this.coreAccess = coreAccess;

		Toolkit tkt = Toolkit.getDefaultToolkit();
		
		cursor = tkt.createCustomCursor(GuiUtils.loadIcon("Medir32.gif").getImage(), new Point(16, 16),"");
		
		measureCoordinates = new CoordinateCollector(coreAccess.getIModel().getGeometryFactory(), coreAccess.getIMap());
		
		// Creo la tool bar donde se va a mostrar la medicion
		Object tmp[] = new Object[]{
				new JLabel("Tramo: "),
				lblLargoTramo,				
				null, 
				new JLabel("Acumulado: "),
				lblLargoTotal, 
				null, 
				new JLabel("Area: "),
				lblArea 
		};
		
		toolBar = ToolBarFactory.createToolBar(true, tmp);
		
		coreAccess.getIUserInterface().addToolBar(toolBar);
		coreAccess.getIModel().addGUIListener(this);
	}


	public Cursor getCursor() {
		return cursor;
	}
	
	
	public void mouseDoubleClicked(Coordinate c) {
		measureCoordinates.clearCoordinates();
	}
	
	public void mouseClicked(Coordinate c) {
		Coordinate previousCoordinate = measureCoordinates.getLastCoordinate();
		
		measureCoordinates.addCoordinate(c);
		
		if (previousCoordinate != null)
			distTotal += c.distance((Coordinate) previousCoordinate);
		else {
			distTotal = 0;
			lblLargoTotal.setText("0 mts.");
			lblLargoTramo.setText("0 mst.");
			lblArea.setText("0 mts. cuad.");
			coreAccess.getIDrawPanel().setTmpShape(null);
			coreAccess.getIDrawPanel().updateLayer();
		}
	}
	
	public void mouseMoved(Coordinate c) {

		double tmpTotalDistance;
		
		if (!measureCoordinates.isEmpty()) {
			
			distTramo = c.distance(measureCoordinates.getLastCoordinate());
			
			tmpTotalDistance = distTotal + distTramo;

			updatePathShape(c);
	
			if (tmpTotalDistance > 1000)
				lblLargoTotal.setText(ContUtils.formatNumber(tmpTotalDistance / 1000) + " kms.");
			else
				lblLargoTotal.setText(ContUtils.formatNumber(tmpTotalDistance) + " mts.");
	
			if (distTramo > 1000)
				lblLargoTramo.setText(ContUtils.formatNumber(distTramo / 1000)	+ " kms.");
			else
				lblLargoTramo.setText(ContUtils.formatNumber(distTramo)	+ " mts.");
	
			if (measureCoordinates.numCoordinates() > 2) {
				Geometry g = measureCoordinates.createPolygon();
				
				area = g.getArea();

				if (area > 10000) {
					if (area > 1000000)
						lblArea.setText(ContUtils.formatNumber(area / 10000) + " km. cuad.");
					else
						lblArea.setText(ContUtils.formatNumber(area / 10000) + " hts.");
				}
				else
					lblArea.setText(ContUtils.formatNumber(area) + " mts. cuad.");
			}
		}
	}
	
	private void updatePathShape(Coordinate c) {
		GeneralPath measurePath;
		
		if (measureCoordinates.numCoordinates() > 1) {
			measurePath = measureCoordinates.createLinestringShape();
			measurePath.append(new Line2D.Double(coreAccess.getIMap().worldToPixel(measureCoordinates.getLastCoordinate()), coreAccess.getIMap().worldToPixel(c)), true);
		}
		else
			measurePath = new GeneralPath(new Line2D.Double(coreAccess.getIMap().worldToPixel(measureCoordinates.getLastCoordinate()), coreAccess.getIMap().worldToPixel(c)));
		
		coreAccess.getIDrawPanel().setTmpShape(measurePath);
		coreAccess.getIDrawPanel().updateLayer();
	}


	public void deactivate() {
		coreAccess.getIDrawPanel().setTmpShape(null);
		coreAccess.getIModel().removeGUIListener(this);
		coreAccess.getIUserInterface().removeToolBar(toolBar);
	}


	public void actionPerformed(ActionEvent e) {
		if (measureCoordinates.numCoordinates() > 0)
			updatePathShape(measureCoordinates.getLastCoordinate());
	}
}
