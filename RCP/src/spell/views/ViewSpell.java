package spell.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import spell.model.simple.ISharedModelBoxIds;
import spell.model.simple.SharedSimpleModelBox;
import spell.model.simple.SpellModelBox;
import spell.tools.LocaleManager;
import spell.widgets.SpellListView;
import spell.widgets.SpellSearchView;



public class ViewSpell  extends ViewPart {
	public static final String ID = "Spell.view.Spell";

	private Composite top = null;
	private SpellListView listViewer = null;
	private SpellSearchView searchViewer;

	public ViewSpell() {
	}

	public void createPartControl(Composite parent) {
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		
		top = new Composite(parent, SWT.NONE );
		top.setLayout(new FillLayout());
		
		SpellModelBox spells = (SpellModelBox)SharedSimpleModelBox.instance().get(ISharedModelBoxIds.BOX_SPELL);
	
		CTabFolder tabFolder = new CTabFolder(top, SWT.NONE | SWT.BOTTOM);
		tabFolder.setLayoutData(gridData);

		CTabItem item = new CTabItem (tabFolder, SWT.NONE);
		item.setText (LocaleManager.instance().getMessage("tabList"));
		listViewer = new SpellListView(tabFolder, SWT.BORDER, spells);
		item.setControl(listViewer);
		listViewer.addListViewListener(new ListViewListener());

		item = new CTabItem (tabFolder, SWT.NONE);
		item.setText (LocaleManager.instance().getMessage("search"));
		searchViewer = new SpellSearchView(tabFolder, SWT.BORDER);
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
		this.setPartName(LocaleManager.instance().getMessage("spell") + " (" + listViewer.getItemCount() + ")");		
	}
	
	// Permet de mettre a jour l'arbre en même temps que le modele
	class ListViewListener implements IListViewListener {

		@Override
		public void sizeChanged() {
			updateTitle();
		}
		
	}
}
