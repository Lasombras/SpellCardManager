package spell;

import java.io.File;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Hashtable;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import spell.preferences.AppearancePage;
import spell.preferences.ImpressionPage;
import spell.tools.LocaleManager;


/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "Spell";

	public final static int ICON_HEIGHT = 36;
	public final static int ICON_WIDTH = 36;
	
	private final static String FOLDER_ICONS = File.separator + "icons" + File.separator;
	public final static String FOLDER_IMAGES = File.separator + "images" + File.separator;
	public final static String FOLDER_TEMPLATES = File.separator + "templates" + File.separator;	
	public final static String FOLDER_DATAS = File.separator + "datas" + File.separator;
	
	public final static String SPELL_NO_ICON = "no_mini_background.jpg";
	public final static String SPELL_NO_BACKGROUND = "no_background.jpg";
	public final static String SPELL_ICON_DELETE = FOLDER_ICONS + "scroll_delete.png";
	public final static String SPELL_ICON_ADD = FOLDER_ICONS + "scroll_add.png";
	public final static String SPELL_ICON = FOLDER_ICONS + "scroll.png";
	public final static String SPELL_ICON_LIB = FOLDER_ICONS + "book_open.png";

	public final static String MAGIC_ITEM_NO_ICON = "no_mini_background.jpg";
	public final static String MAGIC_ITEM_NO_BACKGROUND = "no_background.jpg";
	public final static String MAGIC_ITEM_ICON_DELETE = FOLDER_ICONS + "ring_delete.png";
	public final static String MAGIC_ITEM_ICON_ADD = FOLDER_ICONS + "ring_add.png";
	public final static String MAGIC_ITEM = FOLDER_ICONS + "ring.png";
	public final static String MAGIC_ITEM_LIB = FOLDER_ICONS + "chest.png";

	
	public final static String DATABASE_FILENAME = "spells.db";



	
	public final static String ICON_INFO = FOLDER_ICONS + "info.png";
	public final static String ICON_CROSS = FOLDER_ICONS + "cross.png";
	public final static String ICON_MAGNIFIER = FOLDER_ICONS + "magnifier.png";
	public final static String ICON_PRINTER = FOLDER_ICONS + "printer.png";
	public final static String ICON_DELETE_CARD = FOLDER_ICONS + "image_delete.png";
	public final static String ICON_ADD_CARD = FOLDER_ICONS + "image_add.png";
	public final static String ICON_CLEAR = FOLDER_ICONS + "clear.png";
	public final static String ICON_FRONT_BACK = FOLDER_ICONS + "frontback.png";
	public final static String ICON_DELETE_DECK_CARD = FOLDER_ICONS + "image_delete.png";
	public final static String ICON_SAVE= FOLDER_ICONS + "disk.png";
	public final static String ICON_LOAD_DECK= FOLDER_ICONS + "folder_image.png";
	public final static String ICON_OPTIONS = FOLDER_ICONS + "wrench.png";
	public final static String ICON_EXPORT_CARD = FOLDER_ICONS + "image_export.png";
	public final static String ICON_IMPORT_CARD = FOLDER_ICONS + "image_import.png";
	public final static String ICON_EXPORT_ANDROID = FOLDER_ICONS + "android.png";
	
	
	// The shared instance
	private static Activator plugin;
	
	private static String path = "";
	private static DecimalFormat priceFormat;
	
	private static Map<String, Image>imageCache;
	private static Map<String, ImageDescriptor>imageDescriptorCache;
	
	/**
	 * The constructor
	 */
	public Activator() {
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		
		super.start(context);
		
		//Verification et chargement des confs par default
		ImpressionPage.setDefault();
		AppearancePage.setDefault();

		try {
	        URL url = context.getBundle().getEntry("/");
	        File fileT = null;
	        //  Resolve the URL
	        URL resolvedURL = FileLocator.resolve (url);
	        fileT = new File(resolvedURL.getFile ());
	        path = fileT.getAbsolutePath();
	        if(path.indexOf("file:")>0) {
	        	path = path.substring(0, path.indexOf("file:"));
	        }
	        path += File.separator;
		}catch (Exception e) {e.printStackTrace();}
				
		imageCache = new Hashtable<String, Image>(11);
		imageDescriptorCache = new Hashtable<String, ImageDescriptor>(11);
		
		
		//Mise en place des formats
		priceFormat = new DecimalFormat("#,##0 " + LocaleManager.instance().getMessage("gp"));
		DecimalFormatSymbols dfs = priceFormat.getDecimalFormatSymbols();
		if(LocaleManager.instance().getMessage("groupSeparator").length() > 0)
			dfs.setGroupingSeparator(LocaleManager.instance().getMessage("groupSeparator").charAt(0));
		else
			dfs.setGroupingSeparator(' ');			
		priceFormat.setDecimalFormatSymbols(dfs);

	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	public static void reloadImage(String path) {
		imageDescriptorCache.remove(path);
		imageCache.remove(path);
		getImageDescriptor(path);
	}
	
	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String imgPath) {
		return getImageDescriptor(imgPath, null);
	}
	
	@SuppressWarnings("deprecation")
	public static ImageDescriptor getImageDescriptor(String imgPath, String defaultPath) {
		if(imageDescriptorCache.containsKey(imgPath))
			return imageDescriptorCache.get(imgPath);
		ImageDescriptor image = imageDescriptorFromPlugin(PLUGIN_ID, imgPath);
		try {
			if(image == null) {
				File imageFile = new File(path + imgPath);
				if(imageFile.exists()) {
					image = ImageDescriptor.createFromURL(new File(path + imgPath).toURL());
				} else if(defaultPath != null) {
					image = ImageDescriptor.createFromURL(new File(path + defaultPath).toURL());
				} else {
					//Image vide
					image = ImageDescriptor.createFromURL(new File(path + imgPath).toURL());					
				}
			}
		} catch (Exception e) {	}
		imageDescriptorCache.put(imgPath, image);
		imageCache.put(imgPath, image.createImage());
		return image;
	}
	
	public static String getPath() {
        return path;
	}
	
	public static String formatPrice(int value) {
		return priceFormat.format(value);
	}
	
	public static Image getImage(String imgPath) {
		return getImage(imgPath, null);
	}
	

	public static Image getImage(String imgPath, String defaultPath) {
		if(imageCache.containsKey(imgPath))
			return imageCache.get(imgPath);
		return getImageDescriptor(imgPath, defaultPath).createImage();
	}

	public static String getDataFolder() {
		return FOLDER_DATAS + Activator.getDefault().getPreferenceStore().getString(AppearancePage.APPEARANCE_LANGUAGE) + File.separator;
	}
	
	public static String getSpellImageFolder() {
		return getDataFolder() + "spells" + File.separator;
	}
	
	public static String getMagicItemImageFolder() {
		return getDataFolder() + "items" + File.separator;
	}

}
