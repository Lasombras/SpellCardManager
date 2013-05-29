package com.lasombras.android.scm.utils;

import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;

import com.lasombras.android.scm.R;

public class RessourceCacheManager {

	private static RessourceCacheManager fontTypeInstance;
	private Typeface titleFontType;
	private boolean loaded = false;	
	private HashMap<String, Bitmap> images;
	public final static String APP_FOLDER = "/sdcard/pfscm/";
	
	
	private RessourceCacheManager() {
		images = new HashMap<String, Bitmap>();
	}
	
	public final static RessourceCacheManager instance() {
		if(fontTypeInstance == null) {
			fontTypeInstance = new RessourceCacheManager();
		}
		return fontTypeInstance;
	}
	public void load(Context context) {
		if(loaded) return;
		titleFontType = Typeface.createFromAsset(context.getAssets(),"fonts/saberregular.ttf");
		loaded = true;
	}
	
	public void unload() {
		this.loaded = false;
		this.titleFontType = null;
		this.images.clear();
	}

	public Typeface getTitleFontType() {
		return titleFontType;
	}
	
	public int getListBackgroundId(int position) {
		switch(position%6) {
		case 0 : return R.drawable.list_bloc_01;
		case 1 : return R.drawable.list_bloc_02;
		case 2 : return R.drawable.list_bloc_03;
		case 3 : return R.drawable.list_bloc_04;
		case 4 : return R.drawable.list_bloc_05;
		case 5 : return R.drawable.list_bloc_06;
		}
		return R.drawable.list_bloc_01;
	}
	
	public Bitmap getSpellImage(String imageName) {
		if(imageName == null) return null;
		if(images.containsKey(imageName))
			return images.get(imageName);
        
		Bitmap image = null;
		try {
        	image = BitmapFactory.decodeFile(APP_FOLDER + "spells/" + imageName);
        } catch (Exception e) {}
        images.put(imageName, image);
		return image;
	}
	
	public void setSpellImage(String imageName, Bitmap image) {
	       images.put(imageName, image);		
	}

}
