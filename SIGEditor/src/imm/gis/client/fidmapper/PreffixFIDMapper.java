package imm.gis.client.fidmapper;

public class PreffixFIDMapper implements ILayerFIDMapper {
	String layerName; 
	String preffix;
	public PreffixFIDMapper(String layerName) {
		this.layerName = layerName;
	}
	public String getFID(Object id) {
		// TODO Auto-generated method stub
		return /*getPreffix()+*/id.toString();
	}
	
	public Object getID(String fID) {
		// TODO Auto-generated method stub
		
		return fID;
		
		/*
		String tmp = null;

			if (fID.split("\\.").length > 0) {
				tmp = fID.split("\\.")[1];
			}
		
		
		
		return tmp;
		*/
	}

}
