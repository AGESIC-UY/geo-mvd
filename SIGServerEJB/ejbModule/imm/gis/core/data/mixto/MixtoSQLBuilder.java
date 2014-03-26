package imm.gis.core.data.mixto;

import imm.gis.IAppManager;
import imm.gis.core.data.mixto.attributeio.DateAttributeIO;
import imm.gis.core.data.mixto.attributeio.ExternalAttributeIO;
import imm.gis.core.data.mixto.attributeio.FeatureReferenceAttributeIO;
import imm.gis.core.data.mixto.attributeio.LayerAttributeIO;
import imm.gis.core.data.mixto.attributeio.LayerIO;
import imm.gis.core.data.mixto.attributeio.OracleGeomAttributeIO;
import imm.gis.core.feature.ExternalAttribute;
import imm.gis.core.feature.FeatureReferenceAttribute;
import imm.gis.core.feature.MixtoAttributeTypeFactory;
import imm.gis.core.feature.Util;
import imm.gis.core.layer.Layer;
import imm.gis.core.layer.definition.DataOriginDefinition;
import imm.gis.core.layer.definition.DataSourceDefinition;
import imm.gis.core.layer.definition.LayerAttributeDefinition;
import imm.gis.core.layer.definition.LayerDefinition;
import imm.gis.core.layer.definition.TableAttributeDefinition;
import imm.gis.core.layer.definition.TableDefinition;

import java.io.IOException;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.geotools.data.Query;
import org.geotools.data.jdbc.FilterToSQLException;
import org.geotools.data.jdbc.attributeio.AttributeIO;
import org.geotools.data.jdbc.attributeio.BasicAttributeIO;
import org.geotools.data.jdbc.attributeio.WKTAttributeIO;
import org.geotools.data.jdbc.fidmapper.FIDMapper;
import org.geotools.data.postgis.attributeio.PgWKBAttributeIO;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.feature.AttributeType;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureType;
import org.geotools.feature.FeatureTypeBuilder;
import org.geotools.feature.SchemaException;
import org.geotools.filter.FidFilterImpl;
import org.geotools.filter.IllegalFilterException;
import org.geotools.filter.SQLEncoderOracle;
import org.opengis.filter.And;
import org.opengis.filter.BinaryComparisonOperator;
import org.opengis.filter.BinaryLogicOperator;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.Not;
import org.opengis.filter.Or;
import org.opengis.filter.PropertyIsEqualTo;
import org.opengis.filter.PropertyIsGreaterThan;
import org.opengis.filter.PropertyIsGreaterThanOrEqualTo;
import org.opengis.filter.PropertyIsLessThan;
import org.opengis.filter.PropertyIsLessThanOrEqualTo;
import org.opengis.filter.PropertyIsLike;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.sort.SortBy;
import org.opengis.filter.spatial.SpatialOperator;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKBWriter;
import com.vividsolutions.jts.io.WKTWriter;


/**
 * @author jbarone
 * 
 */
public class MixtoSQLBuilder implements ISQLBuilder {
	
	private GeomFilterToPostGisSQL encoderPostGis;
	private GeomFilterToOracleSQL encoderOracle;
	
	private boolean WKBEnabled = false;
	private boolean byteEnabled = false;
	
	private WKTWriter geometryWriter = new WKTWriter();
	
	public static final String ORA_DATE_FORMAT = "dd-MM-yyyy hh24:mi:ss";
	private SimpleDateFormat pgDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private SimpleDateFormat oraDateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
	private IAppManager appManager;
	private FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(GeoTools.getDefaultHints());
//	private static Logger log = Logger.getLogger(MixtoSQLBuilder.class.getName());
	
	public MixtoSQLBuilder(IAppManager appManager) {
		this.appManager = appManager;
		encoderPostGis = new GeomFilterToPostGisSQL();
		encoderOracle = new GeomFilterToOracleSQL();
	}

	public boolean isWKBEnabled() {
		return WKBEnabled;
	}

	public void setWKBEnabled(boolean enabled) {
		WKBEnabled = enabled;
	}
	
	protected String getGeometryInsertText(int backend, Geometry geom, int srid)
			throws IOException {
		String res = null;
		boolean isPostgis = (backend == DataSourceDefinition.TYPE_POSTGIS);
		
		if (isPostgis){
			if (WKBEnabled) {	
				WKBWriter writer = new WKBWriter();
				byte[] wkbBytes = writer.write(geom);
				String wkb = new String(wkbBytes);// WKBEncoder.encodeGeometryHex(geom);
				if (byteEnabled) {
					res = "setSRID('" + wkb + "'::geometry," + srid + ")";
				}else {
					res = "GeomFromWKB('" + wkb + "', " + srid + ")";
				}
			} else {
				String geoText = geometryWriter.write(geom);
				res = "ST_GeometryFromText('" + geoText + "', " + srid + ")";
			}			
		} else { // Oracle
			res = SQLEncoderOracle.toSDOGeom(geom, Util.SRID);			
//			String geoText = geometryWriter.write(geom);
//			res = "sdo_geometry('" + geoText + "'," + srid + ")";
		}

		return res;
	}	
	
	private String getGeometrySelectText(String table, String attribute, int backEnd) {
		boolean isPostgis = (backEnd == DataSourceDefinition.TYPE_POSTGIS);
		
		if (isPostgis){
			if (WKBEnabled) {	
				if (byteEnabled) {
					return "bytea(AsBinary(ST_Force_2D (" + table + "." + formatearAtributo(attribute, backEnd)	+ "), 'XDR'))";
				}else {
					return "AsBinary(ST_Force_2D (" + table + "." + formatearAtributo(attribute, backEnd) + "), 'XDR')";
				}
			} else {
				return "ST_AsText(ST_Force_2D (" + table + "." + formatearAtributo(attribute, backEnd) + "))";
			}			
		} else { // Oracle
//			if (WKBEnabled) {
//				return "sdo_util.to_wkbgeometry(" + table + "." + attribute + ")";				
//			} else {
//				return "sdo_util.to_wktgeometry(" + table + "." + attribute + ")";				
//			}
			
			return table + "." + attribute;
		}
	}

	
	private String formatearAtributo(String atr, int backEnd) {
		return backEnd == DataSourceDefinition.TYPE_POSTGIS ? (atr.matches("[A-Z]+[_]*[A-Z]+") ? '"' + atr + '"' : atr) : atr;
	}

	private String formatearValor(Object o, int backend) throws IOException {

		if (o == null)
			return "NULL";
		else if (String.class.isAssignableFrom(o.getClass()))
			return "'"+o+"'";
		else if (ExternalAttribute.class.isAssignableFrom(o.getClass())) {
			ExternalAttribute lv = (ExternalAttribute) o;
			
			if (lv.getExternalFID() == null || lv.getExternalFID().equals(""))
				return "NULL";
			
			// Esto es analogo al ExternalAttribute
			
			FIDMapper fmapper = appManager.getFIDMapper(lv.getOriginLayer());
			
			// Por ahora no manejamos claves compuestas
			Object foreignKey[] = fmapper.getPKAttributes(lv.getExternalFID());
			return formatearValor(foreignKey[0],backend);
		}
		else if (FeatureReferenceAttribute.class.isAssignableFrom(o.getClass())) {
			FeatureReferenceAttribute lv = (FeatureReferenceAttribute) o;
			
			if (lv.getReferencedFeatureID() == null || lv.getReferencedFeatureID().equals(""))
				return "NULL";
				
			// Se supone que todo lo que esta por encima del DataStore tiene los FID's creados por el FIDMapper,
			// por lo que para grabar necesitamos traducir dicho FID al valor correcto para la base
			
			FIDMapper fmapper = appManager.getFIDMapper(lv.getReferencedLayer());
			
			// Por ahora no manejamos claves compuestas
			Object foreignKey[] = fmapper.getPKAttributes(lv.getReferencedFeatureID());
			return formatearValor(foreignKey[0],backend);
		}
		else if (Geometry.class.isAssignableFrom(o.getClass()))
			return getGeometryInsertText(backend, (Geometry) o, Util.SRID);
		else if (Date.class.isAssignableFrom(o.getClass())) {

	    	if (backend == DataSourceDefinition.TYPE_POSTGIS){
	    		return "'" + pgDateFormat.format((Date) o) + "'";
	    	} else {
	    		return "to_date('" + oraDateFormat.format((Date) o) + "','" + ORA_DATE_FORMAT + "')";
	    	}
		}
		else
			return o.toString();
	}
	
	private AttributeIO getGeometryAttributeIO(int backend, String type) {
		boolean isPostGis = backend == DataSourceDefinition.TYPE_POSTGIS;

		if (isPostGis){
			if (isWKBEnabled()) {
				return new PgWKBAttributeIO(isByteEnabled());
			} else {
				return new WKTAttributeIO();
			}
		} else {
//			return new WKTAttributeIO();
			return new OracleGeomAttributeIO(appManager.getDataSource(type));
		}
	}

	
	public SQLBuilderResult sqlColumns(boolean withFID, String wantedAttributes[], String type, LayerIO parentLayerIO, int sqlPosition) {

		LayerAttributeDefinition da;
		TableDefinition td;
		TableAttributeDefinition dat;
		int backEnd;
		FIDMapper mapper; 
		LayerIO thisLayerIO;
		LayerAttributeIO layerAttributeIO;
		Collection<String> visitedLayers;
		AttributeIO attributeIO;
		SQLBuilderResult builderResult;
		int selectedColumnsCount;
		
		
		Layer c = appManager.getLayer(type);
		FeatureType ft = c.getFt();
		
		LayerDefinition ld = appManager.getLayerDefinition(type);

		backEnd = ld.getDataOriginDefinition().getDataSourceDefinition().getType();

		String columns = "";
		String column;
		boolean conComa = false;
		
		td = ld.getDataOriginDefinition().getTableDefinition();
		
		mapper = appManager.getFIDMapper(type);
		
		selectedColumnsCount = 0;
		
		if (withFID) {
			// Primero veo cuales son los atributos que hay que seleccionar para obtener la FID.
			// Por ahora asumimos que todas las tablas que se usen aca tienen pk.

			for (int i = 0; i < mapper.getColumnCount(); i++) {
				
				if (conComa)
					columns += ", ";
				
				columns += td.getAlias() + "." + formatearAtributo(mapper.getColumnName(i), backEnd);
				
				sqlPosition++;
				selectedColumnsCount++;
				conComa = true;
			}
		}
		
		// En el LayerIO se registra la correspondencia entre atributos de capa y atributos
		// de la tabla, asi como la posicion de dichos atributos en el futuro ResultSet. Por ultimo,
		// se tiene tambien, para cada atributo de la capa, el AttributeIO encargado de construirlo
		// a partir del resultado de la consulta SQL.
		
		// Si no me pasan uno, quiere decir que estoy ubicado en la capa principal, y me encargo de
		// construirlo.
		if (parentLayerIO == null)
			thisLayerIO = new LayerIO(type, mapper);
		// Si me pasan uno, me fijo si ya existe el correspondiente a ESTA capa.
		else if (parentLayerIO.getChildLayerIO(type) != null) {
			thisLayerIO = parentLayerIO.getChildLayerIO(type);
		}
		// Si no lo creo y lo agrego al padre.
		else {
			thisLayerIO = new LayerIO(type, mapper, parentLayerIO);
			parentLayerIO.addChildLayerIO(thisLayerIO);
		}
		
		// Recorro todos los atributos que me solicitan en la query, y trato de traducir.
		// Ahora supuestamente hay una tabla nomas por ori, supongo que va a quedar asi, dado
		// que los joins, etc, ahora se hacen "entre capas" y no "entre tablas".
		Map<String, LayerAttributeDefinition> atributosCapa = ld.getLayerAttributeDefinitions();
		
		String columnName;
		
		// La siguiente coleccion se utiliza para saber cuales capas ya fueron visitadas (debido
		// a la presencia de atributos externos en ESTA capa) para no poner nuevamente los
		// atributos de la PK externa nuevamente.
		
		visitedLayers = new ArrayList<String>();
		
		for (int i = 0; i < wantedAttributes.length; i++) {

			da = (LayerAttributeDefinition) atributosCapa.get(wantedAttributes[i]);

			if (conComa)
				columns += ", ";
			
			if (ExternalAttribute.class.isAssignableFrom(ft.getAttributeType(da.getName()).getBinding())) {
				
				if (visitedLayers.contains(da.getReferencedLayer()))
					builderResult = sqlColumns(false, new String[] {da.getName()}, da.getReferencedLayer(), thisLayerIO, sqlPosition);
				else {
					builderResult = sqlColumns(true, new String[] {da.getName()}, da.getReferencedLayer(), thisLayerIO, sqlPosition);
					visitedLayers.add(da.getReferencedLayer());
				}
			
				attributeIO = new ExternalAttributeIO(da.getReferencedLayer());
				
				builderResult.getLayerIO().getLayerAttributeIO(da.getName()).setAttributeIO(attributeIO);
				
				columns += builderResult.getSQL();
				sqlPosition += builderResult.getSelectedColumnsCount();
				selectedColumnsCount += builderResult.getSelectedColumnsCount();
			}
			else {
				
				dat = (TableAttributeDefinition) td.getAttributes().get(da.getName());
				
				// Si no se especifica el nombre del atributo en la tabla, se asume que es el mismo
				if (dat == null)
					columnName = da.getName();
				else
					columnName = dat.getTableName();
				
				if (Geometry.class.isAssignableFrom(ft.getAttributeType(da.getName()).getBinding())) {
					attributeIO = getGeometryAttributeIO(backEnd, type);
					layerAttributeIO = new LayerAttributeIO(sqlPosition, attributeIO);
					columns += getGeometrySelectText(td.getAlias(), columnName, backEnd);
				}
				else {
					column = td.getAlias() + "." + formatearAtributo(columnName, backEnd);
					
					if (FeatureReferenceAttribute.class.isAssignableFrom(ft.getAttributeType(da.getName()).getBinding()))
						attributeIO = new FeatureReferenceAttributeIO(da.getReferencedLayer(), appManager.getFIDMapper(da.getReferencedLayer()));
					else if(java.util.Date.class.isAssignableFrom(ft.getAttributeType(da.getName()).getBinding())){
						attributeIO = new DateAttributeIO();
						
					}
					else
						attributeIO = new BasicAttributeIO();
					
					layerAttributeIO = new LayerAttributeIO(sqlPosition, attributeIO, column);
					//formattedAttributes.put(da.getName(), column);
					columns += column;
				}
				
				sqlPosition++;
				selectedColumnsCount++;
				
				thisLayerIO.putLayerAttributeIO(da.getName(), layerAttributeIO);
			}
			
			conComa = true;
		}
		
		SQLBuilderResult result = new SQLBuilderResult();
		
		result.setLayerIO(thisLayerIO);
		result.setSQL(columns);
		result.setSelectedColumnsCount(selectedColumnsCount);
		
		return result;
	}

	
	
	public String sqlFrom(String type, String wantedAttributes[]) {
		LayerAttributeDefinition da;
		
		LayerDefinition layerDefinition = appManager.getLayerDefinition(type);
		FeatureType ft = appManager.getLayer(type).getFt();
		
		Map<String, LayerAttributeDefinition> attributes = layerDefinition.getLayerAttributeDefinitions();
		TableDefinition td = layerDefinition.getDataOriginDefinition().getTableDefinition();
		
		String tables = td.getFullName();
		// Para no meter una tabla en el from mas de una vez
		Collection<String> referencedLayers = new ArrayList<String>();
		
		// Todo lo que no es un atributo externo, corresponde a esta tabla, si es de un atributo externo
		// pido las tablas "a la capa del atributo externo"
		for (int i = 0; i < wantedAttributes.length; i++) {
			da = (LayerAttributeDefinition) attributes.get(wantedAttributes[i]);
			
			if (ExternalAttribute.class.isAssignableFrom(ft.getAttributeType(da.getName()).getBinding()) &&
				!referencedLayers.contains(da.getReferencedLayer())) {
				referencedLayers.add(da.getReferencedLayer());
				tables += ", " + sqlFrom(da.getReferencedLayer(), new String[] {da.getName()});
			}
		}
		
		return tables;
	}

	public String sqlJoin(String type, String wantedAttributes[]) {
		
		LayerAttributeDefinition da;
		TableAttributeDefinition dat;
		LayerDefinition layerDefinition;
		DataOriginDefinition dataOriginDefinition;
		String joins;
		String externalJoins;
		String thisTable;
		Map<String, LayerAttributeDefinition> layerAttributes;
		Map<String, TableAttributeDefinition> tableAttributes;
		FIDMapper fidMapper;
		int backEnd;
		FeatureType featureType;
		ArrayList<String> joinedLayers;
		
		// Por ahora los joins se deben unicamente al uso de atributos externos
		joins = "";
		
		featureType = appManager.getLayer(type).getFt();
		layerDefinition = appManager.getLayerDefinition(type);
		
		layerAttributes = layerDefinition.getLayerAttributeDefinitions();
		
		dataOriginDefinition = layerDefinition.getDataOriginDefinition();
		backEnd = dataOriginDefinition.getDataSourceDefinition().getType();
		
		thisTable = dataOriginDefinition.getTableDefinition().getAlias();
		tableAttributes = dataOriginDefinition.getTableDefinition().getAttributes();
		
		// Todo lo que no es un lov, corresponde a esta tabla, si es de un lov agrego un join con "la capa del LOV"
		// Lo que hago primero es recorrer todos los atributos de la capa para ver cuales son las capas a las que
		// se hace referencia.
		
		joinedLayers = new ArrayList<String>();
		
		for (int i = 0; i < wantedAttributes.length; i++) {
			da = (LayerAttributeDefinition) layerAttributes.get(wantedAttributes[i]);
			
			if (ExternalAttribute.class.isAssignableFrom(featureType.getAttributeType(da.getName()).getBinding()) &&
					!joinedLayers.contains(da.getReferencedLayer())) {
				
				// Meto el join para juntar esta con la tabla del lov
				dat = (TableAttributeDefinition) tableAttributes.get(da.getName());
				
				if (joins.length() > 0)
					joins += " AND ";
				
				// Primero pongo el atributo de mi lado
				joins += thisTable + "." + formatearAtributo(dat.getTableName(), backEnd) + " = ";
				
				// Para ver cual es la clave del otro lado utilizo el fidmapper de la capa.
				// Por ahora supongo que las tablas de los LOV no tienen PK compuesta.
				fidMapper = appManager.getFIDMapper(da.getReferencedLayer());
				dataOriginDefinition = appManager.getLayerDefinition(da.getReferencedLayer()).getDataOriginDefinition();
				
				// Meto el nombre de la tabla de la capa referenciada
				joins += dataOriginDefinition.getTableDefinition().getAlias();
				
				// Meto la PK (que asumimos atomica)
				joins += "." + formatearAtributo(fidMapper.getColumnName(0), backEnd);
				
				// Meto otros joins que pueda tener la capa referenciada
				externalJoins = sqlJoin(da.getReferencedLayer(),  new String[] {da.getName()});
			
				if (externalJoins.length() > 0)
					joins += " AND " + externalJoins;
				
				// Registro el hecho de que ya se realizo el join contra la tabla de esta
				// capa referenciada
				joinedLayers.add(da.getReferencedLayer());				
			}
		}
		
		return joins;
	} 

	
	public String sqlWhere(String type, Filter filt, LayerIO layerIO) throws IllegalFilterException, FilterToSQLException {
		String where = "";
		boolean isPostgis = appManager.getLayerDefinition(type).
							getDataOriginDefinition().getDataSourceDefinition().
							getType() == DataSourceDefinition.TYPE_POSTGIS;
		
		if (filt != Filter.EXCLUDE) {
			if (filt == Filter.INCLUDE)
				where = "'1' = '0'";
			else {
				Filter newFilter;
				if (filt instanceof SpatialOperator || filt instanceof FidFilterImpl){
					newFilter = filt;
				} else {
					// Tanto el encoder de PostGIS como el de Oracle asumen que los nombres de las columnas son aquellos
					// de los atributos del FeatureType, el cual en general no es nuestro caso. Lo que se hace entonces es
					// crear un FeatureType trucho para pasarle al encoder, conteniendo como atributos las columnas de la
					// tabla correspondientes a los atributos del FeatureType original. Otro camino seria implementar nuestro
					// encoder (ja)
					FeatureType f = traducirFeatureType(appManager.getSchema(type), layerIO);

					// Se hace lo mismo con los nombres de los atributos referenciados en el filtro. Ademas, es traducirFiltro
					// quien se encarga de cuestiones del estilo de traducir un literal correspondiente a un FID a los
					// correspondientes valores de la PK de la tabla, etc.
					// Los filtros geometricos o de fid's no necesitan traducci�n
					newFilter = traducirFiltro(appManager.getSchema(type), filt, f, layerIO);
				}
				
				if (isPostgis)
					where = encoderPostGis.encodeToString(newFilter);
				else
					where = encoderOracle.encodeToString(newFilter);				

				where = where.replaceFirst("WHERE ","");
			}
		}
		
		return where;
	}

	private String orderBy(SortBy[] sortBy, LayerIO layerIO) {
		if (sortBy != null && sortBy.length > 0){
			String dbName = layerIO.getLayerAttributeIO(sortBy[0].getPropertyName().toString()).getDatabaseName();
			return " ORDER by " + dbName;
		}
		
		
		return null;
	}
	
	
	/**
	 * Convierte un FeatureType, cambiando el nombre de los atributos de la capa
	 * por sus correspondientes en la base.
	 * 
	 * @param ft
	 *            FeatureType a convertir
	 * 
	 * @return El nuevo FeatureType
	 * 
	 * @throws IllegalFilterException
	 */
	protected FeatureType traducirFeatureType(FeatureType ft, LayerIO layerIO) throws IllegalFilterException {

		String dbName;
		String localName;
		
		// Estos son los atributos de la capa tal cual en la definicion en el XML
		AttributeType layerAttributes[] = ft.getAttributeTypes();

		// Estos van a ser los nuevos atributos, para que encare el filtro
		ArrayList<AttributeType> dbAttributes = new ArrayList<AttributeType>();
		for (int i = 0; i < layerAttributes.length; i++) {

			if (Geometry.class.isAssignableFrom(layerAttributes[i].getBinding()))
				dbAttributes.add(layerAttributes[i]);
			else {
				localName = layerAttributes[i].getLocalName();
				if (layerIO.getLayerAttributeIO(localName) != null){ // Si es NULL, no se requiere leer o escribir este campo
					dbName = layerIO.getLayerAttributeIO(localName).getDatabaseName();
					
					dbAttributes.add(
						MixtoAttributeTypeFactory.newAttributeType(
							dbName,
							layerAttributes[i].getBinding(),
							layerAttributes[i].isNillable(),
							0,
							layerAttributes[i].createDefaultValue()
						)
					);										
				}
			}
		}

		FeatureType nuevoFT;

		try {
			nuevoFT = FeatureTypeBuilder.newFeatureType(
					(AttributeType[]) dbAttributes.toArray(new AttributeType[dbAttributes.size()]),
					ft.getTypeName());
		} catch (SchemaException e) {
			e.printStackTrace();
			throw new IllegalFilterException(e);
		}

		return nuevoFT;
	}

	/**
	 * Convierte un filtro, traduciendo los nombres de los atributos en la capa,
	 * a los nombres de los atributos en la base. Es simplemente un metodo
	 * recursivo.
	 * 
	 * @param f
	 *            Filtro a traducir
	 * @param ft
	 *            FeatureType de la capa
	 * 
	 * @return El filtro que usa los nombres de los atributos en la base
	 * 
	 * @throws IllegalFilterException
	 */
	@SuppressWarnings("unchecked")
	protected Filter traducirFiltro(FeatureType original, Filter f, FeatureType nueva, LayerIO layerIO) throws IllegalFilterException {
		if (f instanceof SpatialOperator){
			return f;
		}
		
		if (f instanceof And || f instanceof Or){
			Filter blo = null;
			List<Filter> list = ((BinaryLogicOperator)f).getChildren();
			Filter tmp;
			
			for (int i = 0; i < list.size(); i++){
				tmp = (Filter)list.get(i);
				list.set(i, traducirFiltro(original, tmp, nueva, layerIO));			
			}
			
			if (f instanceof And){ 
				blo = ff.and(list);
			} else {
				blo = ff.or(list);
			}
			
			return blo;
		} else if (f instanceof Not){
			Filter nf = ff.not(traducirFiltro(original, ((Not)f).getFilter(), nueva, layerIO));
			
			return nf;
		}	else if (f instanceof BinaryComparisonOperator){
			
			// Lo que sigue es un parche por lo siguiente. A veces nos mandan buscar en base a un FID. En dicho caso, el
			// FID debe traducirse a los valores correspondientes de los campos pk (por ahora no se manejan claves compuestas)
			// antes de darselo al encoder, para que el loco genere un WHERE sql con el valor real de dichos atributos. No se puede hacer
			// a nivel de traducirExpresion porque ahi no se tiene el literal.
			
			Expression e = ((BinaryComparisonOperator)f).getExpression1();
			Expression property = e;
			Expression newValue = ((BinaryComparisonOperator)f).getExpression2();
			String referencedLayer;
			FIDMapper fidMapper;
			Object pk;
			String attrPath;
			
			if (e instanceof PropertyName) {
				attrPath = ((PropertyName)e).getPropertyName();
				if (FeatureReferenceAttribute.class.isAssignableFrom(original.getAttributeType(attrPath).getBinding())) {
					referencedLayer = ((LayerAttributeDefinition) appManager.getLayerDefinition(original.getTypeName()).getLayerAttributeDefinitions().get(attrPath)).getReferencedLayer();
					e = ((BinaryComparisonOperator)f).getExpression2();
					if (e instanceof Literal && ((Literal)e).getValue() instanceof String) {
						fidMapper = appManager.getFIDMapper(referencedLayer);
						try {
							pk = fidMapper.getPKAttributes((String)((Literal)e).getValue())[0];
							if (pk instanceof Long)
								pk = new Integer(((Long) pk).intValue());
							else if (pk instanceof BigInteger)
								pk = new Integer(((BigInteger) pk).intValue());
							newValue = ff.literal(pk);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
			
			}
			else {
				e = ((BinaryComparisonOperator)f).getExpression2();
				property = e;
				newValue = ((BinaryComparisonOperator)f).getExpression1();
				if (e instanceof PropertyName) {
					attrPath = ((PropertyName)e).getPropertyName();
					if (FeatureReferenceAttribute.class.isAssignableFrom(original.getAttributeType(attrPath).getBinding())) {
							referencedLayer = ((LayerAttributeDefinition) appManager.getLayerDefinition(original.getTypeName()).getLayerAttributeDefinitions().get(attrPath)).getReferencedLayer();
							e = ((BinaryComparisonOperator)f).getExpression1();
							if (e instanceof Literal && ((Literal)e).getValue() instanceof String) {
								fidMapper = appManager.getFIDMapper(referencedLayer);
								try {
									pk = fidMapper.getPKAttributes((String) ((Literal) e).getValue())[0];
									if (pk instanceof Long)
										pk = new Integer(((Long) pk).intValue());
									newValue = ff.literal(pk);
								} catch (IOException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							}
						}
				}
			}
			
			// Aca termina el parche
			BinaryComparisonOperator bco;			
		    if (f instanceof PropertyIsEqualTo){
				bco = ff.equals(traducirExpresion(
						original,
						property,
						nueva,
						layerIO,
						false
				), newValue);
			} else if (f instanceof PropertyIsGreaterThan){
				bco = ff.greater(traducirExpresion(
						original,
						property,
						nueva,
						layerIO,
						false
				), newValue);
				
			} else if (f instanceof PropertyIsGreaterThanOrEqualTo){
				bco = ff.greaterOrEqual(traducirExpresion(
						original,
						property,
						nueva,
						layerIO,
						false
				), newValue);
			} else if (f instanceof PropertyIsLessThan){
				bco = ff.less(traducirExpresion(
						original,
						property,
						nueva,
						layerIO,
						false
				), newValue);
			} else if (f instanceof PropertyIsLessThanOrEqualTo){
				bco = ff.lessOrEqual(traducirExpresion(
						original,
						property,
						nueva,
						layerIO,
						false
				), newValue);
			} else {
				throw new IllegalFilterException("Filtro " + f + " no considerado...");
			}
		    
			return bco;

		}
		else if (f instanceof PropertyIsLike) {
			Expression value = traducirExpresion(
					original, 
					((PropertyIsLike)f).getExpression(), 
					nueva, 
					layerIO, 
					true
			);
			return ff.like(
					value, 
					((PropertyIsLike)f).getLiteral(), 
					((PropertyIsLike)f).getWildCard(), 
					((PropertyIsLike)f).getSingleChar(), 
					((PropertyIsLike)f).getEscape()
			);
		}
		else
			return f;
	}

	/**
	 * Traduce una expresion. Es decir, en caso de que se trate de una
	 * AttributeExpression, se cambia el nombre del atributo (que es el
	 * correspondiente a la capa) por el nombre del atributo en la base.
	 * 
	 * @param e
	 *            Expresion a traducir
	 * @param ft
	 *            FeatureType de la capa
	 * 
	 * @return La expresion traducida
	 * 
	 * @throws IllegalFilterException
	 */
	private Expression traducirExpresion(FeatureType original, Expression e, FeatureType nueva, LayerIO layerIO, boolean likeFilter)
			throws IllegalFilterException {
		if (e instanceof PropertyName) {
			String layerName = ((PropertyName)e).getPropertyName();
			return ff.property(layerIO.getLayerAttributeIO(layerName).getDatabaseName());
		}
		else
			return e;
	}

	public String sqlUpdate(Feature ft) throws IOException {
		String setChain;
		String columnName;
		String where;
		
		LayerDefinition layerDefinition;
		DataOriginDefinition dataOriginDefinition;
		LayerAttributeDefinition layerAttribute;
		TableDefinition tableDefinition;
		TableAttributeDefinition tableAttribute;
		Map<String, TableAttributeDefinition> tableColumns;
		
		boolean conComa;
		
		FIDMapper fmapper;
		

		// Se hace con el formato viejo del update. Con el SQL 2003 o 2002, no me acuerdo, se agregan
		// los update (col1, .., coln) = (val1, .., valn)
	
		layerDefinition = appManager.getLayerDefinition(ft.getFeatureType().getTypeName());
		dataOriginDefinition = layerDefinition.getDataOriginDefinition();
		tableDefinition =  dataOriginDefinition.getTableDefinition();
			
		tableColumns = tableDefinition.getAttributes();
		
		
		// Bueno, construyo la lista de pares col = val
		Iterator<LayerAttributeDefinition> i = layerDefinition.getLayerAttributeDefinitions().values().iterator(); 
		
		conComa = false;
		setChain = "";
		
		
		// Para no meter mas de una vez una FK en la consulta SQL
		Collection<String> referencedLayers = new ArrayList<String>();
		
		while (i.hasNext()) {
			
			layerAttribute = (LayerAttributeDefinition) i.next();
			tableAttribute = (TableAttributeDefinition) tableColumns.get(layerAttribute.getName());
			
			boolean readOnly = appManager.getLayer(ft.getFeatureType().getTypeName())
								.getMetadata().getAttributeMetadata(layerAttribute.getName()).isReadOnly();
			
			boolean updateAttribute = !readOnly && ((!ExternalAttribute.class.isAssignableFrom(ft.getFeatureType().getAttributeType(layerAttribute.getName()).getBinding())) ||
										(!((ExternalAttribute)ft.getAttribute(layerAttribute.getName())).getExternalFID().equals("") && !referencedLayers.contains(layerAttribute.getReferencedLayer())));
			
			if (updateAttribute) {
				if (ExternalAttribute.class.isAssignableFrom(ft.getFeatureType().getAttributeType(layerAttribute.getName()).getBinding()))
					referencedLayers.add(layerAttribute.getReferencedLayer());
				
				if (tableAttribute != null)
					columnName = formatearAtributo(tableAttribute.getTableName(), dataOriginDefinition.getDataSourceDefinition().getType());
				else
					columnName = formatearAtributo(layerAttribute.getName(), dataOriginDefinition.getDataSourceDefinition().getType());
				
				if (conComa)
					setChain += ", ";
				
				setChain += columnName + " = " + formatearValor(ft.getAttribute(layerAttribute.getName()), dataOriginDefinition.getDataSourceDefinition().getType());
				
				conComa = true;
			}
		}
		
		// Obtengo los valores de la clave primaria del feature
		fmapper = appManager.getFIDMapper(ft.getFeatureType().getTypeName());
		Object pkValues[] = fmapper.getPKAttributes(ft.getID());

		// Construyo la condicion
		
		where = "";
		
		for (int j = 0; j < pkValues.length; j++) {
			
			if (j > 0)
				where += " AND ";
			
			where += formatearAtributo(fmapper.getColumnName(j), dataOriginDefinition.getDataSourceDefinition().getType());
			where += " = " + formatearValor(pkValues[j], dataOriginDefinition.getDataSourceDefinition().getType());
		}
		
		return "UPDATE " + tableDefinition.getFullName() + " SET " + setChain + " WHERE " + where;
	}

	public String sqlInsert(Feature ft) throws IOException {
		
		FIDMapper fmapper;
		String sql;
		String columns;
		String values;
		DataOriginDefinition dod;
		LayerDefinition layerDefinition;
		LayerAttributeDefinition layerAttribute;
		TableAttributeDefinition tableAttribute;
		boolean conComa;
		Object attrValue;
		Map<String, TableAttributeDefinition> tableColumns;
		String columnName;
		
		Layer layer;
		
		
		layer = appManager.getLayer(ft.getFeatureType().getTypeName());
		layerDefinition = appManager.getLayerDefinition(ft.getFeatureType().getTypeName());
		dod = layerDefinition.getDataOriginDefinition();
		
		fmapper = appManager.getFIDMapper(layer.getNombre());
		
		columns = "";
		values = "";
		conComa = false;

		
		// Aca veo de grabar la clave. Si el getPKAttributes devuelve null,
		// quiere decir que no es necesario especificar la clave, por ejemplo,
		// por que esta se genera a partir de una secuencia en la base.
		Object pkAttributes[] = fmapper.getPKAttributes(ft.getID());
		
		if (pkAttributes != null) {
			
			for (int i = 0; i < pkAttributes.length; i++) {
			
				// Al final no le doy bola al hecho de que la columna sea autoincrementante o no,
				// ya que a la hora de grabar ya se tiene que saber el FID del feature. Si el FID se saco de
				// una secuencia o no es otro tema, pero la idea es que cuando se llega aca ya se sabe el FID
				// definitivo del feature.
				
				if (conComa) {
					columns += ", ";
					values += ", ";
				}
				
				columns += formatearAtributo(fmapper.getColumnName(i), dod.getDataSourceDefinition().getType());
				values += formatearValor(pkAttributes[i], dod.getDataSourceDefinition().getType());
				
				conComa = true;
			}
		}
		
		// Ahora agrego "inserts" para los atributos de la capa propiamente dicho
		Iterator<LayerAttributeDefinition> i = layerDefinition.getLayerAttributeDefinitions().values().iterator();
		
		tableColumns = dod.getTableDefinition().getAttributes();
		
		// Para llevar la cuenta de cuales llaves foraneas ya puse
		Collection<String> referencedLayers = new ArrayList<String>();

		while (i.hasNext()) {
			
			layerAttribute = i.next();
			
			attrValue = ft.getAttribute(layerAttribute.getName());
			
			// Los nulos no los meto en el INSERT, si en el UPDATE
			boolean insertAttribute = attrValue != null;
			
			if (insertAttribute
				&& ExternalAttribute.class.isAssignableFrom(
						ft.getFeatureType().getAttributeType(layerAttribute.getName()).getBinding()
						))
				insertAttribute &= ((ExternalAttribute) attrValue).getExternalFID() != null
									&& !((ExternalAttribute) attrValue).getExternalFID().equals("")
									&& !referencedLayers.contains(layerAttribute.getReferencedLayer());
			
			if (insertAttribute
				&& FeatureReferenceAttribute.class.isAssignableFrom(
						ft.getFeatureType().getAttributeType(layerAttribute.getName()).getBinding()
						))
				insertAttribute &= ((FeatureReferenceAttribute) attrValue).getReferencedFeatureID() != null
									&& !((FeatureReferenceAttribute) attrValue).getReferencedFeatureID().equals("");
				
			//	(!ExternalAttribute.class.isAssignableFrom(ft.getFeatureType().getAttributeType(layerAttribute.getName()).getType())) ||
			//	(!((ExternalAttribute)attrValue).getExternalFID().equals("") && !referencedLayers.contains(layerAttribute.getReferencedLayer()));
			
			if (insertAttribute) {
				if (ExternalAttribute.class.isAssignableFrom(ft.getFeatureType().getAttributeType(layerAttribute.getName()).getBinding()))
					referencedLayers.add(layerAttribute.getReferencedLayer());
				
				if (conComa) {
					columns += ", ";
					values += ", ";
				}
				
				tableAttribute = tableColumns.get(layerAttribute.getName());
				
				if (tableAttribute != null)
					columnName = formatearAtributo(tableAttribute.getTableName(), dod.getDataSourceDefinition().getType());
				else
					columnName = formatearAtributo(layerAttribute.getName(), dod.getDataSourceDefinition().getType());
				
				columns += columnName;
				values += formatearValor(attrValue, dod.getDataSourceDefinition().getType());
				
				conComa = true;
			}
		}
		
		// Modificado por Gabriela G 18/09/2009
		//sql = "INSERT INTO " + dod.getTableDefinition().getFullName() + " (" + columns + ") VALUES (" + values + ")";
		sql = "INSERT INTO " + dod.getTableDefinition().getNameSinAlias() + " (" + columns + ") VALUES (" + values + ")";

		// Aca tiene que ir despues el tema de que si alguna de las capas referenciadas no es readOnly, hay que hacer inserts en ellas tambien.
		// Seria una llamada recursiva, habria que ver de meterle algun parametro mas para que use un nombre de capa que le pasemos
		// y no el que viene como type del feature. Ademas deberia ir al principio, para meter las tuplas foraneas antes que
		// la que tenga la FK.
		
		return sql;
	}

	public String sqlRemove(Feature ft) throws IOException {

		FIDMapper fidMapper;
		Object pkValues[];
		String sql;
		String tableName;
		DataOriginDefinition dataOriginDefinition;
		Layer layer;
		
		layer = appManager.getLayer(ft.getFeatureType().getTypeName()); 
		dataOriginDefinition = appManager.getLayerDefinition(ft.getFeatureType().getTypeName()).getDataOriginDefinition();
		
		tableName = dataOriginDefinition.getTableDefinition().getFullName();
		
			
		sql = "DELETE FROM " + tableName + " WHERE ";
		
		// Obtengo los valores de la clave primaria del feature
		fidMapper = appManager.getFIDMapper(layer.getNombre());
		pkValues = fidMapper.getPKAttributes(ft.getID());

		// Agrego la condicion al delete
		for (int i = 0; i < pkValues.length; i++) {
			
			if (i > 0)
				sql += " AND ";
			
			sql += formatearAtributo(fidMapper.getColumnName(i), dataOriginDefinition.getDataSourceDefinition().getType());
			sql += " = " + formatearValor(pkValues[i], dataOriginDefinition.getDataSourceDefinition().getType());
		}
			
		return sql;
	}

	public SQLBuilderResult buildSQLQuery(Query q) throws IllegalFilterException, FilterToSQLException {

		String wantedAttributes[];
		
		String from;
		String join;
		String where;
		String orderBy = null;
		
		SQLBuilderResult builderResult;
		
		wantedAttributes = q.getPropertyNames();
		
		// Veo que atributos recuperar en base a los atributos del layer solicitados
		builderResult = sqlColumns(true, wantedAttributes, q.getTypeName(), null, 1);
		
		// El conjunto de tablas necesario para recuperar dichos atributos
		from = sqlFrom(q.getTypeName(), wantedAttributes);

		// Como relaciono dichas tablas
		join = sqlJoin(q.getTypeName(), wantedAttributes);
		
		encoderOracle.setFIDMapper(appManager.getFIDMapper(q.getTypeName()));
		encoderPostGis.setFIDMapper(appManager.getFIDMapper(q.getTypeName()));
		
		// Condiciones varias (filtros, etc.)
		where =	sqlWhere(q.getTypeName(), q.getFilter(), builderResult.getLayerIO());
		
		orderBy = orderBy(q.getSortBy(), builderResult.getLayerIO());
		
		if (where.length() == 0)
			where = join;
		else if (join.length() > 0)
			where = "(" + where + ") AND " + join;
		
		if (where.length() > 0)
			builderResult.setSQL("SELECT " + builderResult.getSQL() + " FROM " + from + " WHERE " + where);
		else
			builderResult.setSQL("SELECT " + builderResult.getSQL() + " FROM " + from);
		
		if (orderBy != null){
			builderResult.setSQL(builderResult.getSQL() + orderBy);
		}
		
		return builderResult;
	}

	public String buildSQLQueryUpdate(Feature ft)
			throws IOException {
		String sql;

		sql = sqlUpdate(ft);

		return sql;
	}

	public String buildSQLQueryInsert(Feature ft) throws IOException {
		String sql;

		sql = sqlInsert(ft);

		return sql;
	}

	public String buildSQLQueryRemove(Feature ft) throws IOException {
		String sql = sqlRemove(ft); 
		return sql;
	}

	public Filter getPostQueryFilter(Filter filter) {
		return null;
	}

	public Filter getPreQueryFilter(Filter filter) {
		return null;
	}

	public boolean isByteEnabled() {
		return byteEnabled;
	}

	public void setByteEnabled(boolean byteEnabled) {
		this.byteEnabled = byteEnabled;
	}
}
