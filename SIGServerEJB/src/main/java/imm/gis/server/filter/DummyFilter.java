package imm.gis.server.filter;

import org.opengis.filter.Filter;

import imm.gis.GisException;
import imm.gis.core.layer.filter.IUserFiltersProvider;

public class DummyFilter implements IUserFiltersProvider {

	public Filter addUserFilters(Filter fil, Object userInfo) throws GisException {
		return fil;
	}

}
