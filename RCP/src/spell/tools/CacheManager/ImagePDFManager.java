package spell.tools.CacheManager;

import java.util.Hashtable;
import java.util.Map;

import spell.Activator;

import com.itextpdf.text.Image;

public class ImagePDFManager {

	private Map<String, Image>imageCache;

	private static ImagePDFManager instance;
	public static ImagePDFManager instance() {
		if(instance==null) {
			instance= new ImagePDFManager();
		}
		return instance;
	}
	
	private ImagePDFManager() {
		imageCache = new Hashtable<String, Image>(11);
	}
	
	public Image get(String imgPath) {
		Image image = imageCache.get(imgPath);
		if(image == null) {
			try {
				image = Image.getInstance(Activator.getPath() + imgPath);
				if(image == null)
					return null;
			} catch (Exception e) {
				return null;
			}
			imageCache.put(imgPath, image);
		}
		return image;
	}
		
	public Image reload(String imgPath) {
		imageCache.remove(imgPath);
		return get(imgPath);
	}

}
