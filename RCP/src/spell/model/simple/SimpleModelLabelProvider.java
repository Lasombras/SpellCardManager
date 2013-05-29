package spell.model.simple;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import spell.Activator;

public class SimpleModelLabelProvider extends LabelProvider {

	public SimpleModelLabelProvider() {
	}

	public String getText(Object element) {
		return ((SimpleModel) element).getTitle();
	}

	public Image getImage(Object element) {
		// obtain the cached image corresponding to the descriptor
		return Activator.getImage(Activator.getSpellImageFolder() + ((SimpleModel) element).getImage(), Activator.getSpellImageFolder() + Activator.SPELL_NO_ICON);
	}

	public void dispose() {
	}
}
