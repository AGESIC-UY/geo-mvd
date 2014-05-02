package imm.gis.report;

import java.io.IOException;
import java.util.Map;
import imm.gis.IAppManager;

public interface ReportDataLoader {
	
	public ReportDataType loadData(IAppManager appManager, Map<String, Object> dataParameters) throws IOException;
	public Map<String, Object> reportParameters();
}
