package spell.widgets;

import java.util.ArrayList;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;

import spell.Activator;
import spell.actions.ClearDeckAction;
import spell.actions.LoadDeckAction;
import spell.actions.SaveDeckAction;
import spell.databases.DatabaseManager;
import spell.databases.Session;
import spell.model.Level;
import spell.model.MagicItem;
import spell.model.PlayerClass;
import spell.model.Spell;
import spell.model.simple.ISharedModelBoxIds;
import spell.model.simple.MagicItemModelBox;
import spell.model.simple.SharedSimpleModelBox;
import spell.model.simple.SimpleModel;
import spell.model.simple.SimpleModelBox;
import spell.model.simple.SpellModelBox;
import spell.services.ServiceFactory;
import spell.tools.LinkManager;
import spell.tools.LocaleManager;
import spell.viewers.tree.CardModelTreeContentProvider;
import spell.viewers.tree.CardModelTreeLabelProvider;
import spell.viewers.tree.InventoryTreeBox;
import spell.viewers.tree.LevelTreeBox;
import spell.viewers.tree.PlayerClassTreeFilter;
import spell.viewers.tree.SimpleModelTreeBox;
import spell.views.ViewDeck;



public class DeckTreeView  extends Composite  {

	private TreeViewer treeViewer;
	private PlayerClassTreeFilter playerClassTreeFilter;
	private Menu playerClassMenu = null;
	private ToolItem itemDropDown;
	private ToolItem itemClear;
	private ToolItem itemDel;
	private ToolItem itemSave;

	public DeckTreeView(Composite parent, int style) {
		super(parent, style);
		GridLayout gridLayout = new GridLayout();
		gridLayout.horizontalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.verticalSpacing = 0;
		gridLayout.numColumns = 1;
		this.setLayout(gridLayout);
	
		
		GridData fieldGridData = new GridData();
		fieldGridData.horizontalAlignment = GridData.FILL;
		fieldGridData.grabExcessHorizontalSpace = true;

		buildMenu(this,SWT.FLAT | SWT.RIGHT);
		
		treeViewer = new TreeViewer(this);
		treeViewer.setContentProvider(new CardModelTreeContentProvider());
		treeViewer.setLabelProvider(new CardModelTreeLabelProvider());
		
		treeViewer.setUseHashlookup(true);

		Transfer[] transferarray = new Transfer[]{TextTransfer.getInstance()};
		treeViewer.addDropSupport(DND.DROP_COPY | DND.DROP_DEFAULT, transferarray, new DropTargetListener() {
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
				treeViewer.getTree().setRedraw(false);
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
				treeViewer.getTree().setRedraw(true);
			}
			public void dropAccept(DropTargetEvent event) {	}
		});

		GridData layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = true;
		layoutData.horizontalAlignment = GridData.FILL;
		layoutData.verticalAlignment = GridData.FILL;
		treeViewer.getControl().setLayoutData(layoutData);
		
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				itemDel.setEnabled(!event.getSelection().isEmpty());
			}			
		});

		treeViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				Object[] itSel = selection.toArray();
				for(Object item : itSel) {
					if(item instanceof SimpleModel)
						LinkManager.openSimpleModel((SimpleModel)item);
				}
			}			
		});

		treeViewer.setInput(getInitialInput());
		//treeViewer.expandAll();
		playerClassTreeFilter = new PlayerClassTreeFilter();
		treeViewer.addFilter(playerClassTreeFilter);
	}
	
	public int getPlayerClassId() {
		return playerClassTreeFilter.getPlayerClass();
	}
	
	public void setPlayerClassFilter(int id) {
		for(MenuItem item : playerClassMenu.getItems()) {
			if(((Integer)(item.getData("PlayerClassId"))).intValue() == id) {
				playerClassTreeFilter.setPlayerClass(id);
				itemDropDown.setImage(Activator.getImage(Activator.FOLDER_IMAGES + ((String)(item.getData("PlayerClassIcon")))));
			    itemDropDown.setToolTipText(item.getText());
				treeViewer.refresh();
				return;
			}
		}
		
	}

	private ToolBar buildMenu(Composite parent, int style) {
		final ToolBar toolbar = new ToolBar(parent,style );
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

		itemDel = new ToolItem(toolbar, SWT.NONE);
		itemDel.setImage(Activator.getImage(Activator.ICON_DELETE_CARD));
		itemDel.setToolTipText(LocaleManager.instance().getMessage("cardDrop"));
		itemDel.setEnabled(false);
		itemDel.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {}
			public void widgetSelected(SelectionEvent e) {
				ArrayList<SimpleModel> simpleModelsChanged = new ArrayList<SimpleModel>();
				Object[] itSel = ((ITreeSelection)treeViewer.getSelection()).toArray();
				for(Object element : itSel) {
					if(element instanceof SimpleModel) {
						SimpleModel sm = (SimpleModel)element;
						sm.decreaseSize();
						if(sm.getSize() < 0)
							sm.clearSize();
						simpleModelsChanged.add(sm);					
					}
				}
				for(SimpleModel sm : simpleModelsChanged)
					sm.fireSizeChanged();
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

		new ToolItem(toolbar, SWT.SEPARATOR);
		
		
		
		itemDropDown = new ToolItem(toolbar, SWT.DROP_DOWN);
	    itemDropDown.setImage(Activator.getImage(Activator.ICON_CROSS));
	    itemDropDown.setToolTipText(LocaleManager.instance().getMessage("cardClass"));

	    PlayerClass[] playerClasses = null;
	    Session session = null;
		try {
			session = DatabaseManager.getInstance().openSession();
			playerClasses = ServiceFactory.getPlayerClassService().getAll(session);
			session.close();
		}
		catch (Exception e) {e.printStackTrace();}
		finally {if(session != null) session.close();}

		MenuClassFilterSelectionListener menuListener = new MenuClassFilterSelectionListener();
		playerClassMenu = new Menu(parent.getShell(), SWT.POP_UP);
	    MenuItem classMenu = new MenuItem(playerClassMenu, SWT.RADIO);
	    classMenu.setText(LocaleManager.instance().getMessage("cardAll"));
	    classMenu.setData("PlayerClassId", new Integer(-1));
	    classMenu.setData("PlayerClassIcon", "cross.png");
	    classMenu.setSelection(true);
	    classMenu.addSelectionListener(menuListener);
		for(PlayerClass playerClassItem : playerClasses) {
		    classMenu = new MenuItem(playerClassMenu, SWT.RADIO);
		    classMenu.setText(playerClassItem.getTitle());
		    classMenu.setData("PlayerClassId", new Integer(playerClassItem.getId()));
		    classMenu.setData("PlayerClassIcon", playerClassItem.getImage());
		    classMenu.addSelectionListener(menuListener);
		}
	        
	    itemDropDown.addListener(SWT.Selection, new Listener() {
	    	public void handleEvent(Event event) {
		        if(event.detail == SWT.ARROW) {
		          Rectangle bounds = itemDropDown.getBounds();
		          Point point = toolbar.toDisplay(bounds.x, bounds.y + bounds.height);
		          playerClassMenu.setLocation(point);
		          playerClassMenu.setVisible(true);
		        }
	      	}
	    });

        return toolbar;
	}

	class MenuClassFilterSelectionListener implements SelectionListener {
		public void widgetDefaultSelected(SelectionEvent e) {}
		public void widgetSelected(SelectionEvent e) {
			MenuItem item = (MenuItem)e.getSource();
			if(item.getSelection()) {
				setPlayerClassFilter(((Integer)((MenuItem)e.getSource()).getData("PlayerClassId")).intValue());
			}
		}
	}	    	
		
	private SimpleModelBox getInitialInput() {
		SimpleModelBox input = SharedSimpleModelBox.instance().get(ISharedModelBoxIds.BOX_SPELL);
		//Decoupage par level
		SimpleModelBox root = new SimpleModelBox(new SimpleModel[0]);
		for(int i = 0; i < 10; i++) {
			LevelTreeBox levelBox = new LevelTreeBox(i,LocaleManager.instance().getMessage("level") + " " + i, "", null);
			for(Object spell : ((SimpleModelBox) input).getContents()) {
				if(spell instanceof Spell) {
					Spell spellItem = (Spell)spell;
					if(spellItem.getSize() > 0) {
						for(Level level : spellItem.getLevels()) {
							if(level.getLevel() == i) {
								if(!levelBox.contains(spellItem))
									levelBox.add(spellItem);
							}
						}
					}
						
				}
			}

			root.add(levelBox, false);
		}
		root.add(new InventoryTreeBox(LocaleManager.instance().getMessage("magicItem"), "", null),false);
		return root;
	}
		
	public void update(SimpleModel simpleModel) {
		updateInput((SimpleModelBox)treeViewer.getInput(),simpleModel);
		treeViewer.refresh();
		refreshButton();
	}
	
	public void clear() {
		treeViewer.getTree().setRedraw(false);
		for(Object objectBox : ((SimpleModelBox)treeViewer.getInput()).getContents()) {
			if(objectBox instanceof SimpleModelTreeBox) {
				SimpleModelTreeBox simpleModelTreeBox = (SimpleModelTreeBox)objectBox;
				simpleModelTreeBox.removeAll();
			}
		}
		treeViewer.getTree().setRedraw(true);
		treeViewer.refresh();
		refreshButton();
	}

	private void updateInput(SimpleModelBox input, SimpleModel simpleModel) {		
		treeViewer.getTree().setRedraw(false);
		for(Object objectBox : input.getContents()) {
			if(simpleModel instanceof Spell) {
				if(objectBox instanceof LevelTreeBox) {
					Spell spell = (Spell) simpleModel;
					LevelTreeBox levelBox = (LevelTreeBox)objectBox;
					if(levelBox.contains(spell)) 
						levelBox.remove(spell);
					if(spell.getSize() > 0 && spell.isExist()) {
						for(Level level : spell.getLevels()) {
							if(level.getLevel() == levelBox.getLevel()) {
								if(!levelBox.contains(spell)) {
									levelBox.add(spell);
									treeViewer.expandToLevel(levelBox, TreeViewer.ALL_LEVELS);
								}
							}
						}
					}
				}
			} else if(simpleModel instanceof MagicItem) {
				if(objectBox instanceof InventoryTreeBox) {
					MagicItem magicItem = (MagicItem) simpleModel;
					InventoryTreeBox inventoryBox = (InventoryTreeBox)objectBox;
					if(inventoryBox.contains(magicItem)) 
						inventoryBox.remove(magicItem);
					if(magicItem.getSize() > 0 && magicItem.isExist()) {
						inventoryBox.add(magicItem);
						treeViewer.expandToLevel(inventoryBox, TreeViewer.ALL_LEVELS);
					}
				}
			}
		}
		treeViewer.getTree().setRedraw(true);
	}

	private void refreshButton() {
		ViewDeck viewDeck = null;
		IViewPart viewPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ViewDeck.ID);
		if(viewPart != null && viewPart instanceof ViewDeck) {
			viewDeck = (ViewDeck)viewPart;
		}
		
		itemClear.setEnabled(viewDeck.getCards().length > 0);	
		itemSave.setEnabled(viewDeck.getCards().length > 0);
		itemDel.setEnabled(!((ITreeSelection)treeViewer.getSelection()).isEmpty());
		
	}

}