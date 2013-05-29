package spell.tools.CacheManager;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

import spell.Activator;

import com.itextpdf.text.pdf.BaseFont;

public class BaseFontManager {

	private static BaseFontManager instance = null;
	private Hashtable<String, BaseFont> fonts = null;
	
	private BaseFontManager() {
		fonts = new Hashtable<String, BaseFont>();
	}
	
	public static BaseFontManager instance() {
		if(instance == null)
			instance = new BaseFontManager();
		return instance;
	}
	
	public BaseFont getFont(String name) {
		BaseFont font = fonts.get(name);
		if(font == null) {
			try {
				font = BaseFont.createFont(name, BaseFont.CP1252, false);
				fonts.put(name, font);
	        } catch (Exception e) {
	        	e.printStackTrace();
			}
		}
		return font;
	}
	
	public BaseFont addFont(String name, String fileName) {
		BaseFont baseFont = null;
		try {
			InputStream ris = new FileInputStream(Activator.getPath() + fileName);
	        byte[] ttfAfm = readFully(ris);
	        ris.close();
	        baseFont = BaseFont.createFont(Activator.getPath() + fileName, BaseFont.IDENTITY_H, true, true, ttfAfm, null); 
	        fonts.put(name, baseFont);
        } catch (Exception e) {
        	e.printStackTrace();
		}
        return baseFont;
	}
	
	public void clear() {
		fonts.clear();
	}
	
	private static byte[] readFully(InputStream input) throws IOException
	{
	    byte[] buffer = new byte[8192];
	    int bytesRead;
	    ByteArrayOutputStream output = new ByteArrayOutputStream();
	    while ((bytesRead = input.read(buffer)) != -1)
	    {
	        output.write(buffer, 0, bytesRead);
	    }
	    return output.toByteArray();
	}

}
