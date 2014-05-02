package imm.gis.core.data.mixto.attributeio;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.geotools.data.DataSourceException;
import org.geotools.data.jdbc.attributeio.BasicAttributeIO;

public class DateAttributeIO extends BasicAttributeIO {
		
	public Object read(ResultSet rs, int position) throws IOException {
		Date result;
		
		try {
//			dateStr = rs.getString(position);
//			result = sdf.parse(dateStr);
			result = rs.getTimestamp(position);
//		} catch (ParseException e) {
//			throw new DataSourceException("Error parseando fecha", e);
		} catch (SQLException e1){
			throw new DataSourceException(e1);			
		}
		
		return result;
	}
}
