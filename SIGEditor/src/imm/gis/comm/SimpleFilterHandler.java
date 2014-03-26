package imm.gis.comm;

import org.opengis.filter.Filter;
import org.geotools.filter.FilterHandler;
import org.xml.sax.helpers.DefaultHandler;

public class SimpleFilterHandler extends DefaultHandler implements
		FilterHandler {

	private Filter filter;

	public void filter(Filter filter) {
		this.filter = filter;
	}

	public Filter getFilter() {
		return filter;
	}

}
