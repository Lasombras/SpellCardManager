package spell.model.simple;

import java.util.ArrayList;


public class CardModelContentProvider extends SimpleModelContentProvider {
	private boolean sizedOnly = false;
	
	
	public CardModelContentProvider(boolean sizedOnly) {
		super();
		this.sizedOnly = sizedOnly;
	}
	
	public Object[] getElements(Object inputElement) {
		if(sizedOnly) {
			ArrayList<Object> items = new ArrayList<Object>();
			for(SimpleModel item : ((SimpleModelBox) inputElement).getContents()) {
				if(item.getSize() > 0)
					items.add(item);
			}
			return items.toArray();
		} 
		return ((SimpleModelBox) inputElement).getContents();
	}


}
