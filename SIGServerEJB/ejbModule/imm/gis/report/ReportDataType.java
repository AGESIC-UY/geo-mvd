package imm.gis.report;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ReportDataType {
	private Map<String, List<Object>> map = new HashMap<String, List<Object>>();
	
	public void addField(String fName){
		map.put(fName, new ArrayList<Object>());
	}
	
	public Object getFieldValue(String fName, int pos){
		return getFieldList(fName).get(pos);
	}
	
	public void addFieldValue(String fName, Object value){
		getFieldList(fName).add(value);	
	}

	private List<Object> getFieldList(String fName){
		return map.get(fName);
	}
	
	public int length() {
		return map.isEmpty() ? 0 : getFieldList((String)map.keySet().iterator().next()).size();
	}
}
