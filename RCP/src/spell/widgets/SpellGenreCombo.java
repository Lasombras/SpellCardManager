package spell.widgets;

import java.util.Enumeration;
import java.util.Hashtable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class SpellGenreCombo extends Composite {

	private int itemSize;
	private Hashtable<String, ImageLabel> items;
	
	public SpellGenreCombo(Composite arg0, int nbCol, int itemSize) {
		super(arg0, SWT.NONE);
		this.itemSize = itemSize;
		GridLayout genreLayout = new GridLayout(nbCol, true);
		this.setLayout(genreLayout);
		this.setBackground(new Color(this.getDisplay(), 255,255,255));
		items = new Hashtable<String, ImageLabel>();
	}
	
	public void addItem(String name, Image img) {
		if(!items.contains(name))
			items.put(name, new ImageLabel(this,name, img, itemSize, ImageLabel.VERTICAL));
	}
	
	public void clean() {
		Enumeration<String> keys = items.keys();
		while(keys.hasMoreElements()) {
			String key = keys.nextElement();
			items.get(key).dispose();
			items.remove(key);
		}
		this.pack();
		this.redraw();
	}

}
