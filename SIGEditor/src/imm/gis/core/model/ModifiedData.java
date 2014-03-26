package imm.gis.core.model;

import java.util.Collection;

public class ModifiedData {
	private Collection data[];
	
	public ModifiedData(Collection created, Collection updated, Collection deleted){
		data = new Collection[]{created, updated, deleted};
	}
	
	public Collection getCreatedData(){
		return data[0];
	}
	
	public Collection getUpdatedData(){
		return data[1];
	}

	public Collection getDeletedData(){
		return data[2];
	}
}
