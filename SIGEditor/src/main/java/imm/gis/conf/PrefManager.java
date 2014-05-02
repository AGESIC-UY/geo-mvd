package imm.gis.conf;

import java.util.prefs.Preferences;

public class PrefManager {
	private static PrefManager instance = null;
	private Preferences preferences;
	
	private PrefManager(){
		preferences = Preferences.userNodeForPackage(getClass());
	}
	
	public static PrefManager getInstance(){
		if (instance == null){
			instance = new PrefManager();
		}
		
		return instance;
	}
	
	public String getString(String key){
		return preferences.get(key, null);
	}
	
	public int getInt(String key){
		return preferences.getInt(key, -1);
	}

	public void setString(String key, String value){
		preferences.put(key, value);
	}
	
	public void setInt(String key, int value){
		preferences.putInt(key, value);
	}
	
	public void removePreference(String key){
		preferences.remove(key);		
	}
	
	
}
