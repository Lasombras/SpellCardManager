package spell.widgets;

import java.util.ArrayList;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import spell.Activator;
import spell.actions.ClearDeckAction;
import spell.actions.LoadDeckAction;
import spell.actions.PrintDeckAction;
import spell.actions.SaveDeckAction;
import spell.model.MagicItem;
import spell.model.Spell;
import spell.model.simple.CardModelContentProvider;
import spell.model.simple.CardModelLabelProvider;
import spell.model.simple.ISharedModelBoxIds;
import spell.model.simple.MagicItemModelBox;
import spell.model.simple.SharedSimpleModelBox;
import spell.model.simple.SimpleModel;
import spell.model.simple.SimpleModelBox;
import spell.model.simple.SpellModelBox;
import spell.tools.LinkManager;
import spell.tools.LocaleManager;



public class DeckListView  extends Composite {

	private Table listEnv = null;
	private TableViewer listViewer = null;
	private ToolItem itemClear;
	private ToolItem itemDel;
	private ToolItem itemPrint;
	private ToolItem itemSave;

	public DeckListView(Composite parent, int style) {
		super(parent, style);
		
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
		this.setLayout(gridLayout);
		buildMenu(this,SWT.FLAT | SWT.RIGHT);
		
		listEnv = new Table(this, SWT.BORDER | SWT.MULTI |  SWT.H_SCROLL);
		listEnv.setLayoutData(gridData);
		listViewer = new TableViewer(listEnv);
		listViewer.setContentProvider(new CardModelContentProvider(true));
		listViewer.setLabelProvider(new CardModelLabelProvider(true));
		//listViewer.setSorter(new ViewerSorter());
		Transfer[] transferarray = new Transfer[]{TextTransfer.getInstance()};
		listViewer.addDropSupport(DND.DROP_COPY | DND.DROP_DEFAULT, transferarray, new DropTargetListener() {
			public void dragEnter(DropTargetEvent event) {
				if(event.detail == DND.DROP_DEFAULT) event.detail = DND.DROP_COPY;
			}
			public void dragLeave(DropTargetEvent event) {}
			public void dragOperationChanged(DropTargetEvent event) {
				if (event.detail == DND.DROP_DEFAULT) event.detail = DND.DROP_COPY;
			}
			public void dragOver(DropTargetEvent event) {}
			public void drop(DropTargetEvent event) {
				String ids[] = ((String)event.data).split(";");
				for(int i = 0; i < ids.length; i++) {
					if(ids[i] != null && !ids[i].equals("")) {
						if(ids[i].startsWith(Spell.class.getSimpleName() + "_")) {
							int id = Integer.parseInt(ids[i].substring((Spell.class.getSimpleName() + "_").length()));
							Spell spellModel = ((SpellModelBox)SharedSimpleModelBox.instance().get(ISharedModelBoxIds.BOX_SPELL)).get(id);
							if(spellModel != null) {
								spellModel.increaseSize();
								spellModel.fireSizeChanged();
							}						
						} else if(ids[i].startsWith(MagicItem.class.getSimpleName() + "_")) {
							int id = Integer.parseInt(ids[i].substring((MagicItem.class.getSimpleName() + "_").length()));
							MagicItem magicItemModel = ((MagicItemModelBox)SharedSimpleModelBox.instance().get(ISharedModelBoxIds.BOX_MAGIC_ITEM)).get(id);
							if(magicItemModel != null) {
								magicItemModel.increaseSize();
								magicItemModel.fireSizeChanged();
							}						
						}

					}
				}
			}
			public void dropAccept(DropTargetEvent event) {	}
		});

		listViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				itemDel.setEnabled(!event.getSelection().isEmpty());
			}			
		});
		listViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				Object[] itSel = selection.toArray();
				for(Object item : itSel) {
					if(item instanceof SimpleModel)
						LinkManager.openSimpleModel((SimpleModel)item);
				}
			}			
		});
		listViewer.setInput(new SimpleModelBox(new SimpleModel[0]));		
		refreshButton();		
	}
	
	private ToolBar buildMenu(Composite parent, int style) {
		ToolBar toolbar = new ToolBar(parent,style );
		ToolItem itemLoad = new ToolItem(toolbar, SWT.NONE);
		itemLoad.setImage(Activator.getImage(LoadDeckAction.ICON));
		itemLoad.setToolTipText(LocaleManager.instance().getMessage("deckLoad"));
		itemLoad.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {}
			public void widgetSelected(SelectionEvent e) {
				LoadDeckAction.execute();
			}			
		});

		itemSave = new ToolItem(toolbar, SWT.NONE);
		itemSave.setImage(Activator.getImage(SaveDeckAction.ICON));
		itemSave.setToolTipText(LocaleManager.instance().getMessage("deckSave"));
		itemSave.setEnabled(false);
		itemSave.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {}
			public void widgetSelected(SelectionEvent e) {
				SaveDeckAction.execute();
			}			
		});
				
		
		new ToolItem(toolbar, SWT.SEPARATOR);
		
		itemPrint = new ToolItem(toolbar, SWT.NONE);
		itemPrint.setImage(Activator.getImage(PrintDeckAction.ICON));
		itemPrint.setToolTipText(LocaleManager.instance().getMessage("cardPrint"));
		itemPrint.setEnabled(false);
		itemPrint.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {}
			public void widgetSelected(SelectionEvent e) {
				PrintDeckAction.execute();
			}		
		});

		itemDel = new ToolItem(toolbar, SWT.NONE);
		itemDel.setImage(Activator.getImage(Activator.ICON_DELETE_CARD));
		itemDel.setToolTipText(LocaleManager.instance().getMessage("cardDrop"));
		itemDel.setEnabled(false);
		itemDel.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {}
			public void widgetSelected(SelectionEvent e) {
				ArrayList<SimpleModel> simpleModelsChanged = new ArrayList<SimpleModel>();
				for(TableItem item : listViewer.getTable().getSelection()) {
					if(item.getData() instanceof SimpleModel) {
						SimpleModel sm = ((SimpleModel)item.getData());
						sm.decreaseSize();
						simpleModelsChanged.add(sm);
					}
				}
				for(SimpleModel sp : simpleModelsChanged)
					sp.fireSizeChanged();
			}			
		});

		itemClear = new ToolItem(toolbar, SWT.NONE);
		itemClear.setImage(Activator.getImage(Activator.ICON_CLEAR));
		itemClear.setToolTipText(LocaleManager.instance().getMessage("cardClear"));
		itemClear.setEnabled(false);
		itemClear.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {}
			public void widgetSelected(SelectionEvent e) {
				ClearDeckAction.execute();
			}			
		});

        return toolbar;
	}
		
	public SimpleModel[] getCards() {
		ArrayList<SimpleModel> list = new ArrayList<SimpleModel>();
		for(TableItem item : listViewer.getTable().getItems()) {
			if(item.getData() instanceof SimpleModel) {
				list.add((SimpleModel)item.getData());
			}
		}
		SimpleModel[] result = new SimpleModel[list.size()];
		list.toArray(result);
		return result;
	}
	
	public boolean contains(SimpleModel simpleModel) {
		for(TableItem item : listViewer.getTable().getItems()) {
			if(item.getData() == simpleModel) {
				return true;
			}
		}
		return false;
		
	}
	
	private void refreshButton() {
		itemClear.setEnabled(listViewer.getTable().getItems().length > 0);
		itemDel.setEnabled(listViewer.getTable().getSelection().length > 0);
		itemPrint.setEnabled(listViewer.getTable().getItems().length > 0);
		itemSave.setEnabled(listViewer.getTable().getItems().length > 0);
	}
	
	public void update(SimpleModel simpleModel) {
		if(simpleModel.isExist() && simpleModel.getSize() > 0 ) {
			if(contains(simpleModel))
				listViewer.update(simpleModel, null);	
			else
				listViewer.add(simpleModel);
		} else {
			listViewer.remove(simpleModel);			
		}
		refreshButton();		
	}
	
	public void refresh() {
		listViewer.refresh();
		refreshButton();
	}

	public int getItemCount() {
		return listViewer.getTable().getItemCount();
	}
	
	public void clear() {
		listViewer.getTable().removeAll();
		refreshButton();
	}
}