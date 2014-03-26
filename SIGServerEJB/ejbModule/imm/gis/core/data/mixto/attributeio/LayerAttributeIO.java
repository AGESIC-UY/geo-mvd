package imm.gis.core.data.mixto.attributeio;

import java.io.IOException;
import java.sql.ResultSet;

import org.geotools.data.jdbc.attributeio.AttributeIO;

public class LayerAttributeIO {

	private int attributePosition;
	
	private AttributeIO attributeIO;
	
	private String databaseName = null;
	
	public LayerAttributeIO(int attributePosition, AttributeIO attributeIO) {
		this.attributePosition = attributePosition;
		this.attributeIO = attributeIO;
	}
	
	public LayerAttributeIO(int attributePosition, AttributeIO attributeIO, String databaseName) {
		this.attributePosition = attributePosition;
		this.attributeIO = attributeIO;
		this.databaseName = databaseName;
	}
	
	public Object read(ResultSet rs) throws IOException {
		return attributeIO.read(rs, attributePosition);
	}
	
	public void write(ResultSet rs, Object value) throws IOException {
		attributeIO.write(rs, attributePosition, value);
	}
	
	public void setActualFID(String actualFID) {
		if (attributeIO instanceof ExternalAttributeIO)
			((ExternalAttributeIO) attributeIO).setReferencedFID(actualFID);
	}
	
	public String getDatabaseName() {
		return databaseName;
	}
	
	public void setAttributeIO(AttributeIO attributeIO) {
		this.attributeIO = attributeIO;
	}
}
