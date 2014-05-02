/*
package imm.gis.core.controller;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;

public class ContLineDrawer {
	
	//private ContPpal contPpal;
	private Shape shape;
	private int lineType;
	public static final int NORMAL_LINE_TYPE = 0;
	public static final int DASHED_LINE_TYPE = 1;
	
	
	//public ContLineDrawer(ContPpal p) {
	public ContLineDrawer() {
		//contPpal = p;
		lineType = NORMAL_LINE_TYPE;
	}

	public Shape getShape() {
		return shape;
	}

	public void setShape(Shape shape) {
		this.shape = shape;
	}

	public int getLineType() {
		return lineType;
	}

	public void setLineType(int lineType) {
		this.lineType = lineType;
	}

	public void drawShape(Graphics2D g){
		Rectangle r = shape.getBounds();
		g.drawString("distancia: ", r.x, r.y);
		g.draw(shape);
	}
}
*/