package imm.gis.edition;

public interface IFeatureEditor {
	
	public boolean requiresAddPermission();
	public boolean requiresModifyPermission();
	public boolean requiresDeletePermission();
	public boolean requiresSelectVarious();
	
}
