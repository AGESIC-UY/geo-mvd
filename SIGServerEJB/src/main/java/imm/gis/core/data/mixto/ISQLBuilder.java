package imm.gis.core.data.mixto;

import java.io.IOException;

import org.geotools.data.Query;
import org.geotools.data.jdbc.FilterToSQLException;
import org.geotools.feature.Feature;
import org.opengis.filter.Filter;
import org.geotools.filter.IllegalFilterException;

public interface ISQLBuilder { 
    public Filter getPostQueryFilter(Filter filter);
    public Filter getPreQueryFilter(Filter filter);
    
    public SQLBuilderResult buildSQLQuery(Query q) throws FilterToSQLException, IllegalFilterException;
    public String buildSQLQueryInsert(Feature ft) throws IOException;
    public String buildSQLQueryUpdate(Feature ft) throws IOException;
    public String buildSQLQueryRemove(Feature ft) throws IOException;    
}
