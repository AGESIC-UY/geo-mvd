package imm.gis.core.interfaces;

import com.vividsolutions.jts.geom.Coordinate;

public interface CoordinateListener {

	public void coordinateChanged(Coordinate c);
}
