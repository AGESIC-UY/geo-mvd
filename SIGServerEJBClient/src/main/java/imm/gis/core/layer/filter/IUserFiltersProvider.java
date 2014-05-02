package imm.gis.core.layer.filter;

import imm.gis.GisException;

import org.opengis.filter.Filter;

public interface IUserFiltersProvider {
	Filter addUserFilters(Filter fil, Object userInfo) throws GisException;
}
