package imm.gis.core.model.undo;

import imm.gis.core.model.FeatureOperation;

import java.util.ArrayList;
import java.util.List;

public class UndoableOperation {
	private List<FeatureOperation> items;
	private String description;
	
	public UndoableOperation(String d){
		items  = new ArrayList<FeatureOperation>();
		description = d;
	}
	
	public void addFeatureOperation(FeatureOperation fo){
		items.add(fo);
	}
	
	public void addAllFeatureOperation(FeatureOperation fo[]){
		items.addAll(java.util.Arrays.asList(fo));
	}
	
	public java.util.Collection getOperations(){
		return items;
	}
	
	public String toString(){
		StringBuffer tmp = new StringBuffer("UndoableOperation\n");
		
		for (java.util.Iterator it = items.iterator(); it.hasNext();){
			tmp.append(it.next());
		}
		
		return tmp.toString();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
