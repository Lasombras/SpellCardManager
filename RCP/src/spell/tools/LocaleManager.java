package spell.tools;

import java.io.FileInputStream;
import java.util.Properties;

import spell.Activator;


public class LocaleManager {
	private static LocaleManager instance = null;
	private Properties prop;
	
	
	private LocaleManager() {
		prop = new Properties(); 
		
		try {
			prop.load(new FileInputStream(Activator.getPath() + Activator.getDataFolder() + "strings.props"));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public static LocaleManager instance() {
		if(instance == null)
			instance = new LocaleManager();
		return instance;
	}
	
	public String getMessage(String key) {
		return prop.getProperty(key);
	}
}
