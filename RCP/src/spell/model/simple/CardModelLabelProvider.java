package spell.model.simple;

import org.eclipse.swt.graphics.Image;

import spell.Activator;
import spell.model.MagicItem;
import spell.model.Spell;

public class CardModelLabelProvider extends SimpleModelLabelProvider {
	
	private boolean showSizeInfo = false;
	
	public CardModelLabelProvider(boolean showSizeInfo) {
		this.showSizeInfo = showSizeInfo;
	}

	public String getText(Object element) {
		if(showSizeInfo)
			return "(x" + ((SimpleModel) element).getSize() + ") " + ((SimpleModel) element).getTitle();
		else
			return ((SimpleModel) element).getTitle();
	}

	public Image getImage(Object element) {
		if(element instanceof Spell)
			return Activator.getImage(Activator.getSpellImageFolder() + ((SimpleModel) element).getImage(), Activator.getSpellImageFolder() + Activator.SPELL_NO_ICON);
		else if (element instanceof MagicItem)
			return Activator.getImage(Activator.getMagicItemImageFolder() + ((SimpleModel) element).getImage(), Activator.getMagicItemImageFolder() + Activator.MAGIC_ITEM_NO_ICON);

		return super.getImage(element);
			
	}

	public void dispose() {	}
}

