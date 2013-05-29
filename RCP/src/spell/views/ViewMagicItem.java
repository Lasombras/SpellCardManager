package spell.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import spell.model.simple.ISharedModelBoxIds;
import spell.model.simple.MagicItemModelBox;
import spell.model.simple.SharedSimpleModelBox;
import spell.tools.LocaleManager;
import spell.widgets.MagicItemListView;
import spell.widgets.MagicItemSearchView;



public class ViewMagicItem  extends ViewPart {
	public static final String ID = "Spell.view.MagicItem";

	private Composite top = null;
	private MagicItemListView listViewer = null;
	private MagicItemSearchView searchViewer;

	public ViewMagicItem() {
	}

	public void createPartControl(Composite parent) {
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		
		top = new Composite(parent, SWT.NONE );
		top.setLayout(new FillLayout());
		
		MagicItemModelBox magicItems = (MagicItemModelBox)SharedSimpleModelBox.instance().get(ISharedModelBoxIds.BOX_MAGIC_ITEM);

		CTabFolder tabFolder = new CTabFolder(top, SWT.NONE | SWT.BOTTOM);
		tabFolder.setLayoutData(gridData);

		CTabItem item = new CTabItem (tabFolder, SWT.NONE);
		item.setText (LocaleManager.instance().getMessage("tabList"));
		listViewer = new MagicItemListView(tabFolder, SWT.BORDER, magicItems);
		item.setControl(listViewer);
		listViewer.addListViewListener(new ListViewListener());
		
		item = new CTabItem (tabFolder, SWT.NONE);
		item.setText (LocaleManager.instance().getMessage("search"));
		searchViewer = new MagicItemSearchView(tabFolder, SWT.BORDER);
		item.setControl(searchViewer);

		tabFolder.setSelection(0);
		
		//tabFolder.pack ();

		updateTitle();

		//this.getSite().setSelectionProvider(listViewer);
		//window = this.getSite().getWorkbenchWindow();
	}

	public void setFocus() {
		searchViewer.setDefaultButton();
	}
	
	public void updateTitle() {
		setPartName(LocaleManager.instance().getMessage("item") + " (" + listViewer.getItemCount() + ")");		
	}
	// Permet de mettre a jour l'arbre en même temps que le modele
	class ListViewListener implements IListViewListener {

		@Override
		public void sizeChanged() {
			updateTitle();
		}
		
	}
}
