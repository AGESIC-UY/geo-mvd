package imm.gis.core.model.event;

import imm.gis.core.model.ModelContext;

import java.util.EventObject;
import org.geotools.feature.Feature;

public class FeatureChangeEvent extends EventObject {
	
	private static final long serialVersionUID = 1L;

	static public final int CREATED_FEATURE = 0;

	static public final int DELETED_FEATURE = 1;

	static public final int MODIFIED_FEATURE = 2;

	static public final int ADDED_FEATURE = 3;
	
	static public final int CLEANED_CACHE = 4;
	
	static public final int OPEN_FORM_MODIFY_FEATURE = 5;
	
	static public final int CONFIRM_CHANGES = 6;
	
	static public final int APPLY_FILTER = 7;

	public static final int OPEN_FORM_SHOW_FEATURE = 8;
	
	public static final int EXPORT_ACTION = 9;
	
	static public final int OPEN_FORM_NEW_FEATURE = 10;
	
	static public final int NEW_FEATURE_CHILD_LAYER = 11;
	
	static public final int MODIFIED_ATTRIBUTE_FEATURE_CHILD_LAYER = 12;
	
	private Feature newFeature;

	private Feature oldFeature;

	private int operation;

	private String typeName;
	
	public FeatureChangeEvent(Object source, String typeName, Feature n, Feature o, int op) {
		super(source);
		this.oldFeature = o;
		this.newFeature = n;
		operation = op;
		this.typeName = typeName;
	}

	public Feature getNewFeature() {
		return newFeature;
	}

	public Feature getOldFeature() {
		return oldFeature;
	}

	public int getOperation() {
		return operation;
	}
	
	public String getTypeName() {
		return typeName;
	}
}
