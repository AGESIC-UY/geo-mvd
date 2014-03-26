package imm.gis.core.data.mixto.attributeio;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.geotools.data.jdbc.attributeio.AttributeIO;

public abstract class ReferencingAttributeIO implements AttributeIO {

	private String referencedLayer;
	
	public ReferencingAttributeIO(String referencedLayer) {
		this.referencedLayer = referencedLayer;
	}
	
	public abstract Object read(ResultSet rs, int position) throws IOException;
	public abstract void write(ResultSet rs, int position, Object value) throws IOException;
	public abstract void write(PreparedStatement ps, int position, Object value) throws IOException;

	protected String getReferencedLayer() {
		return referencedLayer;
	}
}
