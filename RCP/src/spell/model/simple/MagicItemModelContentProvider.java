package spell.model.simple;

import java.util.ArrayList;

import spell.model.MagicItem;

public class MagicItemModelContentProvider extends SimpleModelContentProvider {

	private boolean printOnly = false;
	
	public MagicItemModelContentProvider(boolean printOnly) {
		super();
		this.printOnly = printOnly;
	}
	
	public Object[] getElements(Object inputElement) {
		if(printOnly) {
			ArrayList<MagicItem> magicItems = new ArrayList<MagicItem>();
			for(SimpleModel magicItem : ((SimpleModelBox) inputElement).getContents()) {
				if(magicItem instanceof MagicItem) {
					if(((MagicItem)magicItem).getSize() > 0)
						magicItems.add((MagicItem)magicItem);
				}
			}
			return magicItems.toArray();
		} else
			return ((SimpleModelBox) inputElement).getContents();
	}

}
