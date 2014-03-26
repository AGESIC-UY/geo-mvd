package imm.gis.core.data.mixto.attributeio;

import imm.gis.core.datasource.IDataSource;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import oracle.jdbc.OracleConnection;
import oracle.sql.ARRAY;
import oracle.sql.Datum;
import oracle.sql.NUMBER;
import oracle.sql.STRUCT;

import org.apache.log4j.Logger;
import org.geotools.data.DataSourceException;
import org.geotools.data.jdbc.attributeio.AttributeIO;
import org.geotools.data.oracle.sdo.GeometryConverter;
import org.geotools.data.oracle.sdo.SDO;
import org.jboss.resource.adapter.jdbc.WrappedConnection;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

public class OracleGeomAttributeIO implements AttributeIO {
	private GeometryConverter converter;
	private IDataSource dataSource;
	private GeometryFactory geometryFactory;

	static private final Logger LOGGER = Logger
			.getLogger(OracleGeomAttributeIO.class.getName());

	public OracleGeomAttributeIO(IDataSource ds) {
		dataSource = ds;
		geometryFactory = new GeometryFactory();
	}

	public Object read(ResultSet rs, int position) throws IOException {

		try {
			Geometry geom = null;
			Object struct = rs.getObject(position);
			geom = asGeometry((STRUCT) struct);
			return geom;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DataSourceException(e);
		}
	}

	public void write(ResultSet rs, int position, Object value)
			throws IOException {
		OracleConnection conn = null;
		try {
			conn = (OracleConnection) ((WrappedConnection) dataSource
					.getConnection()).getUnderlyingConnection();
			converter = new GeometryConverter(conn);
			Geometry geom = (Geometry) value;
			STRUCT struct = converter.toSDO(geom);
			rs.updateObject(position, struct);
		} catch (SQLException sqlException) {
			String msg = "SQL Exception writing geometry column";
			LOGGER.debug(msg, sqlException);
			throw new DataSourceException(msg, sqlException);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void write(PreparedStatement ps, int position, Object value)
			throws IOException {
		OracleConnection conn = null;
		try {
			conn = (OracleConnection) ((WrappedConnection) dataSource
					.getConnection()).getUnderlyingConnection();
			converter = new GeometryConverter(conn);
			Geometry geom = (Geometry) value;
			STRUCT struct = converter.toSDO(geom);
			ps.setObject(position, struct);
		} catch (SQLException sqlException) {
			String msg = "SQL Exception writing geometry column";
			LOGGER.debug(msg, sqlException);
			throw new DataSourceException(msg, sqlException);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private Geometry asGeometry(STRUCT sdoGeometry) throws SQLException {
		if (sdoGeometry == null)
			return null;

		Datum data[] = sdoGeometry.getOracleAttributes();
		final int GTYPE = asInteger(data[0], 0);
		final int SRID = asInteger(data[1], SDO.SRID_NULL);
		final double POINT[] = asDoubleArray((STRUCT) data[2], Double.NaN);
		final int ELEMINFO[] = asIntArray((ARRAY) data[3], 0);
		final double ORDINATES[] = asDoubleArray((ARRAY) data[4], Double.NaN);
		;

		return SDO.create(geometryFactory, GTYPE, SRID, POINT, ELEMINFO,
				ORDINATES);
	}

	protected int asInteger(Datum datum, final int DEFAULT) throws SQLException {
		if (datum == null)
			return DEFAULT;
		return ((NUMBER) datum).intValue();
	}

	protected double[] asDoubleArray(STRUCT struct, final double DEFAULT)
			throws SQLException {
		if (struct == null)
			return null;
		return asDoubleArray(struct.getOracleAttributes(), DEFAULT);
	}

	protected double[] asDoubleArray(ARRAY array, final double DEFAULT)
			throws SQLException {
		if (array == null)
			return null;
		if (DEFAULT == 0)
			return array.getDoubleArray();

		return asDoubleArray(array.getOracleArray(), DEFAULT);
	}

	protected double[] asDoubleArray(Datum data[], final double DEFAULT)
			throws SQLException {
		if (data == null)
			return null;
		double array[] = new double[data.length];
		for (int i = 0; i < data.length; i++) {
			array[i] = asDouble(data[i], DEFAULT);
		}
		return array;
	}

	protected double asDouble(Datum datum, final double DEFAULT)
			throws SQLException {
		if (datum == null)
			return DEFAULT;
		return ((NUMBER) datum).doubleValue();
	}

	protected int[] asIntArray(ARRAY array, int DEFAULT) throws SQLException {
		if (array == null)
			return null;
		if (DEFAULT == 0)
			return array.getIntArray();

		return asIntArray(array.getOracleArray(), DEFAULT);
	}

	protected int[] asIntArray(Datum data[], final int DEFAULT)
			throws SQLException {
		if (data == null)
			return null;
		int array[] = new int[data.length];
		for (int i = 0; i < data.length; i++) {
			array[i] = asInteger(data[i], DEFAULT);
		}
		return array;
	}

}
