package imm.gis.core.data.mixto.fidmapper;

import java.io.IOException;
import java.sql.Connection;

import org.geotools.data.jdbc.fidmapper.DefaultFIDMapperFactory;
import org.geotools.data.jdbc.fidmapper.FIDMapper;
import org.geotools.data.jdbc.fidmapper.TypedFIDMapper;

/**
 * Clase intermedia creada
 * para sobreescribir el metodo getMapper
 * para que el DefaultFIDMapperFactory no devuelva un TypedFIDMapper
 * @author Nacho
 *
 */
public class UnWrappedFIDMapperFactory extends DefaultFIDMapperFactory {

	public UnWrappedFIDMapperFactory() {
		super();
	}

	public UnWrappedFIDMapperFactory(boolean returnFIDColumnsAsAttributes) {
		super(returnFIDColumnsAsAttributes);
	}

	public FIDMapper getMapper(String catalog, String schema, String tableName, Connection connection)
			throws IOException {
				FIDMapper mapper = super.getMapper(catalog, schema, tableName, connection);
				if(mapper instanceof TypedFIDMapper){
					mapper = ((TypedFIDMapper)mapper).getWrappedMapper();
				}
				return mapper;
			}

}