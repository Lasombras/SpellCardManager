package spell.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import spell.model.MagicItem;
import spell.model.Spell;
import spell.model.simple.IModelListener;
import spell.model.simple.ISharedModelBoxIds;
import spell.model.simple.MagicItemModelBox;
import spell.model.simple.SharedSimpleModelBox;
import spell.model.simple.SimpleModel;
import spell.model.simple.SpellModelBox;
import spell.tools.LocaleManager;
import spell.widgets.DeckListView;
import spell.widgets.DeckTreeView;



public class ViewDeck  extends ViewPart {
	public static final String ID = "Spell.view.Deck";

	private DeckListView listViewer;
	private DeckTreeView treeViewer;
	private Composite top;
	
	public ViewDeck() {
	}

	public void createPartControl(Composite parent) {
		GridLayout gridLayout = new GridLayout();
		gridLayout.horizontalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.verticalSpacing = 0;
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		top = new Composite(parent, SWT.NONE );
		top.setLayout(gridLayout);
	
		CTabFolder tabFolder = new CTabFolder(top, SWT.NONE | SWT.BOTTOM);
		tabFolder.setLayoutData(gridData);

		CTabItem item = new CTabItem (tabFolder, SWT.NONE);
		item.setText (LocaleManager.instance().getMessage("tabList"));
		listViewer = new DeckListView(tabFolder, SWT.BORDER);
		item.setControl(listViewer);

		item = new CTabItem (tabFolder, SWT.NONE);
		item.setText(LocaleManager.instance().getMessage("tabTree"));
		treeViewer = new DeckTreeView(tabFolder, SWT.BORDER);
		item.setControl(treeViewer);

		tabFolder.setSelection(0);
		
		//tabFolder.pack ();
		SharedSimpleModelBox.instance().get(ISharedModelBoxIds.BOX_SPELL).addModelListener(new ListenerModel());
		SharedSimpleModelBox.instance().get(ISharedModelBoxIds.BOX_MAGIC_ITEM).addModelListener(new ListenerModel());
		updateTitle();

	}


	public void setFocus() {
	}
	
	// Permet de mettre a jour l'arbre en même temps que le modele
	class ListenerModel implements IModelListener {
		public void modelChanged(SimpleModel object, String type) {
			if (type.equals(IModelListener.REMOVED) ||
				type.equals(IModelListener.CHANGED) ||
				type.equals(IModelListener.SIZED)	) {
				listViewer.update(object);
				treeViewer.update(object);
			}
			updateTitle();
		}
	}

	public Composite getTop() {
		return top;
	}
	
	public void clear() {
		SpellModelBox spellsBox = (SpellModelBox)SharedSimpleModelBox.instance().get(ISharedModelBoxIds.BOX_SPELL);
		for(Object item : spellsBox.getContents()) {
			if(item instanceof Spell) {
				((Spell)item).clearSize();
			}
		}
		MagicItemModelBox magicItemsBox = (MagicItemModelBox)SharedSimpleModelBox.instance().get(ISharedModelBoxIds.BOX_MAGIC_ITEM);
		for(Object item : magicItemsBox.getContents()) {
			if(item instanceof MagicItem) {
				((MagicItem)item).clearSize();
			}
		}

		listViewer.clear();
		treeViewer.clear();
		updateTitle();
	}
	
	
	public SimpleModel[] getCards() {
		return listViewer.getCards();
	}
	
	public int getPlayerClassId() {
		return treeViewer.getPlayerClassId();
	}
	
	public void setPlayerClassFilter(int id) {
		treeViewer.setPlayerClassFilter(id);
	}
	
	public void updateTitle() {
		setPartName(LocaleManager.instance().getMessage("deck") + " (" + listViewer.getItemCount() + ")");		
	}
}