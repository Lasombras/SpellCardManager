package spell;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import spell.views.ViewDeck;
import spell.views.ViewMagicItem;
import spell.views.ViewSpell;


public class Perspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {		
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(true);

		IFolderLayout left = layout.createFolder("left", IPageLayout.LEFT, 0.20f, editorArea);
		left.addView(ViewSpell.ID);
		left.addView(ViewMagicItem.ID);
		layout.getViewLayout(ViewSpell.ID).setCloseable(false);
		layout.getViewLayout(ViewMagicItem.ID).setCloseable(false);
		
		IFolderLayout right = layout.createFolder("right", IPageLayout.RIGHT, 0.80f, editorArea);
		right.addView(ViewDeck.ID);
		layout.getViewLayout(ViewDeck.ID).setCloseable(false);
	}

}
