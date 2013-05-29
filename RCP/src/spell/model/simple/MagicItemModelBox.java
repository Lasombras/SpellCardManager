package spell.model.simple;

import spell.model.MagicItem;

public class MagicItemModelBox extends SimpleModelBox {

	public MagicItemModelBox(MagicItem[] objs) {
		super(objs);
	}

	public MagicItem get(int id) {
		return (MagicItem) super.get(id);
	}
	
	public MagicItem get(String originalName) {
		for(int i = 0; i < objects.size(); i++) {
			MagicItem magicItem = (MagicItem)objects.get(i);
			if(magicItem.getOriginalName().equalsIgnoreCase(originalName))
				return magicItem;
		}
		return null;
	}

}