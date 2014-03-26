package imm.gis.edition.util;

import imm.gis.core.controller.IGeometryType;

import org.geotools.feature.FeatureType;

import com.vividsolutions.jts.geom.Geometry;

public class GeomUtil {

	public static boolean isPointGeometry(Geometry g) {
		return g.getClass().getCanonicalName().equals("com.vividsolutions.jts.geom.Point");
	}

	public static boolean isPointGeometry(FeatureType ft) {
		return ft.getDefaultGeometry().getBinding().getCanonicalName().equals("com.vividsolutions.jts.geom.Point");
	}

	public static boolean isLineGeometry(Geometry g) {
		return (g.getClass().getCanonicalName().equals(
				"com.vividsolutions.jts.geom.LineString") || g.getClass().getCanonicalName().equals(
						"com.vividsolutions.jts.geom.MultiLineString"));
	}

	public static boolean isLineGeometry(FeatureType ft) {
		return (ft.getDefaultGeometry().getBinding().getCanonicalName().equals(
				"com.vividsolutions.jts.geom.LineString") || ft
				.getDefaultGeometry().getBinding().getCanonicalName().equals(
						"com.vividsolutions.jts.geom.MultiLineString"));
	}

	public static boolean isPolygonGeometry(Geometry g) {
		return (g.getClass().getCanonicalName().equals("com.vividsolutions.jts.geom.Polygon"));
	}

	public static boolean isPolygonGeometry(FeatureType ft) {
		return (ft.getDefaultGeometry().getBinding().getCanonicalName().equals("com.vividsolutions.jts.geom.Polygon"));
	}

	public static int getGeometryType(FeatureType ft) {
		if (isPointGeometry(ft))
			return IGeometryType.POINT_GEOMETRY;
		else if (isLineGeometry(ft))
			return IGeometryType.LINE_GEOMETRY;
		else
			return IGeometryType.POLYGON_GEOMETRY;
	}

	public static int getGeometryType(Geometry g) {
		if (isPointGeometry(g))
			return IGeometryType.POINT_GEOMETRY;
		else if (isLineGeometry(g))
			return IGeometryType.LINE_GEOMETRY;
		else
			return IGeometryType.POLYGON_GEOMETRY;
	}	
}
