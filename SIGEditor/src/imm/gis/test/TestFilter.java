package imm.gis.test;

import org.geotools.filter.ExpressionBuilder;
import org.geotools.filter.parser.ParseException;

public class TestFilter {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String filterStr = "[ the_geom bbox POLYGON ((574082 6137011, 574082 6137337, 574435 6137337, 574435 6137011, 574082 6137011)) ]";
		ExpressionBuilder builder = new ExpressionBuilder();
		try {
			Object result = builder.parser(filterStr);
			System.out.println(result);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(ExpressionBuilder.getFormattedErrorMessage(e, filterStr));
		}
		

	}

}
