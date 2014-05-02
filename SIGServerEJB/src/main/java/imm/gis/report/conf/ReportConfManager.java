package imm.gis.report.conf;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ReportConfManager {
	private static Map<String, Properties> reportProps = new HashMap<String, Properties>();
	
	public static String getURLReportPreffix(String idApp){
		Properties localProp = null; 
		if(!reportProps.containsKey(idApp)){
			localProp = new Properties();
			try {
				localProp.load(ReportConfManager.class.getResourceAsStream(idApp+".properties"));
				reportProps.put(idApp,localProp);
			} catch (IOException e) {
				e.printStackTrace();
				return null;
				
			}
			
		}
		return reportProps.get(idApp).getProperty("preffix.reports.url");
		
		
	}
	
}
