package imm.gis.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

public class GenericDS implements JRDataSource {
	private ReportDataType data;
	private int pos = -1;
	
	public GenericDS(ReportDataType r){
		data = r;
	}
	
	public Object getFieldValue(JRField arg0) throws JRException {
		return data.getFieldValue(arg0.getName(), pos);
	}

	public boolean next() throws JRException {
		pos++;
		
		return pos < data.length();
	}

}
