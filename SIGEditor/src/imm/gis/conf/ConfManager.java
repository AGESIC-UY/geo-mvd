package imm.gis.conf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

public class ConfManager {

	private static Properties properties;
	public static String JEDITOR_PATH_PROPERTY = "jeditor.conf.dir";
	//private static String DEFAULT_RESOURCE_JAR_PATH = get
	private static ConfManager instance = null;
	private String jarResourcePath =null;
	private ConfManager(){
		
	}
	
	public static ConfManager getInstance(String jPath){
		if( instance == null){
			instance = new ConfManager();
		}
		instance.setJarPropertiesPath(jPath);
		
		return instance;
		
	}
	private void loadProperties(){
		properties = new Properties();
		try {
			URL url = new URL(jarResourcePath);//"jar:file:/home/duke/duke.jar!/");
			 URLConnection jarConnection = url.openConnection();
			properties.load(jarConnection.getInputStream());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				properties.load(new FileInputStream("jeditor.properties"));
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public  String getAppId(){
		return properties.getProperty("application");
	}
	
	public String getStylesPath(){
		return properties.getProperty("stylesPath");
	}
	
	public  void setJarPropertiesPath(String uri){
		if(jarResourcePath!=null && (uri==null || uri.equals(jarResourcePath))){
			//si la uri  es la misma que la actual o es null y la actual no es null 
			//no se hace nada.
			return;
		}
		if(jarResourcePath == null && uri == null){
			jarResourcePath = getDefaultJarResourcePath();
		}
		else if(uri == null){
			jarResourcePath = uri;
		}
		loadProperties();
	}
	
	
	private static String getDefaultJarResourcePath(){
		String userDir = System.getProperty("user.dir");
		
		String urlDir ="";
		try {
			urlDir = new File(userDir+"/JEditor.jar!/").toURL().toString();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return urlDir;
	}
	
	public static String getUserURLDir(){
		String urlDir = null;
		try {
			urlDir = new File(System.getProperty("user.dir")).toURL().toString();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return urlDir;
	}
}
