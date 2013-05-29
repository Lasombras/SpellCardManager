package spell.widgets;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

import spell.Activator;
import spell.databases.DatabaseManager;
import spell.databases.Session;
import spell.model.ItemType;
import spell.model.MagicItem;
import spell.model.Slot;
import spell.model.Source;
import spell.model.simple.CardModelLabelProvider;
import spell.model.simple.ISharedModelBoxIds;
import spell.model.simple.MagicItemModelBox;
import spell.model.simple.SharedSimpleModelBox;
import spell.model.simple.SimpleModelContentProvider;
import spell.search.criteria.MagicItemSearchCriteria;
import spell.search.criteria.SpellSearchCriteria;
import spell.services.ServiceFactory;
import spell.tools.LinkManager;
import spell.tools.LocaleManager;


public class MagicItemSearchView  extends Composite {

	private Composite resultComposite = null;
	private Composite searchComposite = null;
	private Table listResult = null;
	private TableViewer listViewer = null;
	private Button searchButton;
	private ImageCombo itemTypeCombo = null;
	private ImageCombo slotCombo = null;
	private ImageCombo priceLabel = null;
	private ImageCombo sourceCombo = null;
	private Spinner price = null;

	private Text titleField;
	private Text originalNameField;
	private Slot[] slots = null;
	private ItemType[] itemTypes = null;
	private Source[] sources = null;

	public MagicItemSearchView(Composite parent, int style) {
		super(parent, style);
		
		GridLayout parentGridLayout = new GridLayout();
		parentGridLayout.numColumns = 1;
		parentGridLayout.marginWidth = parentGridLayout.marginHeight = 0;
		GridData parentGridData = new GridData();
		parentGridData.horizontalAlignment = GridData.FILL;
		parentGridData.grabExcessHorizontalSpace = true;
		parentGridData.grabExcessVerticalSpace = true;
		parentGridData.verticalAlignment = GridData.FILL;
		this.setLayout(parentGridLayout);
		this.setLayoutData(parentGridData);
		
		Session session = null;
		try {
			session = DatabaseManager.getInstance().openSession();
			slots = ServiceFactory.getSlotService().getAll(session);
			itemTypes = ServiceFactory.getItemTypeService().getAll(session);
			sources = ServiceFactory.getSourceService().getAll(session);
			session.close();
		}
		catch (Exception e) {e.printStackTrace();}
		finally {if(session != null) session.close();}

	
		GridData fieldGridData = new GridData();
		fieldGridData.horizontalAlignment = GridData.FILL;
		fieldGridData.grabExcessHorizontalSpace = true;

		searchComposite = new Composite(this, SWT.NONE);
		GridLayout glayout = new GridLayout();
		glayout.marginWidth = glayout.marginHeight = 2;
		glayout.numColumns = 2;
		searchComposite.setLayout(glayout);
		searchComposite.setLayoutData(fieldGridData);		
		
		Label label = new Label(searchComposite, SWT.NONE);
		label.setText(LocaleManager.instance().getMessage("magicItem") + " : ");
		titleField = new Text(searchComposite, SWT.BORDER);
		titleField.setLayoutData(fieldGridData);
		
		label = new Label(searchComposite, SWT.NONE);
		label.setText(LocaleManager.instance().getMessage("originalName") + " : ");
		originalNameField = new Text(searchComposite, SWT.BORDER);
		originalNameField.setLayoutData(fieldGridData);

		label = new Label(searchComposite, SWT.NONE);
		label.setText(LocaleManager.instance().getMessage("sourceName") + " : ");
		sourceCombo = new ImageCombo(searchComposite, SWT.BORDER);
		sourceCombo.setEditable(false);
		sourceCombo.setBackground(titleField.getBackground());
		sourceCombo.add("", Activator.getImage(Activator.ICON_CROSS));
		for(Source source : sources) {
			sourceCombo.add(source.getTitle(), Activator.getImage(Activator.FOLDER_IMAGES + source.getImage()));
		}
		sourceCombo.setLayoutData(fieldGridData);

		label = new Label(searchComposite, SWT.NONE);
		label.setText(LocaleManager.instance().getMessage("itemType") + " : ");
		itemTypeCombo = new ImageCombo(searchComposite, SWT.BORDER);
		itemTypeCombo.setEditable(false);
		itemTypeCombo.setBackground(titleField.getBackground());
		itemTypeCombo.add("", Activator.getImage(Activator.ICON_CROSS));
		for(ItemType itemType : itemTypes) {
			itemTypeCombo.add(itemType.getTitle(), Activator.getImage(Activator.FOLDER_IMAGES + itemType.getImage()));
		}
		itemTypeCombo.setLayoutData(fieldGridData);

		label = new Label(searchComposite, SWT.NONE);
		label.setText(LocaleManager.instance().getMessage("slot") + " : ");
		slotCombo = new ImageCombo(searchComposite, SWT.BORDER);
		slotCombo.setEditable(false);
		slotCombo.setBackground(titleField.getBackground());
		slotCombo.add("", Activator.getImage(Activator.ICON_CROSS));
		for(Slot slot : slots) {
			slotCombo.add(slot.getTitle(), Activator.getImage(Activator.FOLDER_IMAGES + slot.getImage()));
		}
		slotCombo.setLayoutData(fieldGridData);

		priceLabel = new ImageCombo(searchComposite, SWT.NONE);
		priceLabel.setEditable(false);
		priceLabel.setBackground(label.getBackground());
		priceLabel.add(LocaleManager.instance().getMessage("price") + " >=", null);
		priceLabel.add(LocaleManager.instance().getMessage("price") + " =", null);
		priceLabel.add(LocaleManager.instance().getMessage("price") + " <=", null);
		priceLabel.add(LocaleManager.instance().getMessage("price") + " <>", null);
		priceLabel.select(0);

		price = new Spinner(searchComposite, SWT.BORDER);
		price.setMaximum(Integer.MAX_VALUE);
		price.setMinimum(0);
		price.setPageIncrement(500);
		price.setIncrement(10);
		price.setSelection(0);
		price.setLayoutData(fieldGridData);
		
		searchButton = new Button(searchComposite, SWT.PUSH);
		searchButton.setText(LocaleManager.instance().getMessage("searchStart"));
		searchButton.setImage(Activator.getImage(Activator.ICON_MAGNIFIER));
		GridData buttonGridData = new GridData();
		buttonGridData.horizontalAlignment = GridData.FILL;
		buttonGridData.grabExcessHorizontalSpace = true;
		buttonGridData.horizontalSpan = 2;
		searchButton.setLayoutData(buttonGridData);
		
		searchButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent arg0) {}
			public void widgetSelected(SelectionEvent arg0) {
				Session session = null;
				try {
					MagicItemSearchCriteria criteria = new MagicItemSearchCriteria();
					criteria.setName(titleField.getText());
					criteria.setOriginalName(originalNameField.getText());
					if(itemTypeCombo.getSelectionIndex() <= 0)
						criteria.setItemTypeId(-1);
					else
						criteria.setItemTypeId(itemTypes[itemTypeCombo.getSelectionIndex()-1].getId());
					if(sourceCombo.getSelectionIndex() <= 0)
						criteria.setSourceId(-1);
					else
						criteria.setSourceId(sources[sourceCombo.getSelectionIndex()-1].getId());
					if(slotCombo.getSelectionIndex() <= 0)
						criteria.setSlotId(-1);
					else
						criteria.setSlotId(slots[slotCombo.getSelectionIndex()-1].getId());
					
					criteria.setPrice(price.getSelection());
					if(priceLabel.getSelectionIndex() == 0) criteria.setPriceSign(SpellSearchCriteria.GREATHER_THAN);
					if(priceLabel.getSelectionIndex() == 1)	criteria.setPriceSign(SpellSearchCriteria.EQUAL);
					if(priceLabel.getSelectionIndex() == 2)	criteria.setPriceSign(SpellSearchCriteria.LEATHER_THAN);
					if(priceLabel.getSelectionIndex() == 3)	criteria.setPriceSign(SpellSearchCriteria.NOT_EQUAL);
					
					
					session = DatabaseManager.getInstance().openSession();
					listViewer.setInput(new MagicItemModelBox(ServiceFactory.getMagicItemService().search(session, criteria)));
					session.close();
					//setPartName(LocaleManager.instance().getMessage("search") + " (" + listViewer.getTable().getItemCount() + ")");
					
				}
				catch (Exception e1) {e1.printStackTrace();}
				finally {if(session != null) session.close();}
			}
			
		});

		resultComposite = new Composite(this, SWT.BORDER );
		resultComposite.setLayoutData(parentGridData);
		GridLayout gridLayout = new GridLayout();
		gridLayout.horizontalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.verticalSpacing = 0;
		resultComposite.setLayout(gridLayout);
		listResult = new Table(resultComposite, SWT.MULTI |  SWT.H_SCROLL | SWT.V_SCROLL);
		listResult.setLayoutData(parentGridData);
		listViewer = new TableViewer(listResult);
		listViewer.setContentProvider(new SimpleModelContentProvider());
		listViewer.setLabelProvider(new CardModelLabelProvider(false));	
		Transfer[] transferarray = new Transfer[]{TextTransfer.getInstance()};
		listViewer.addDragSupport(DND.DROP_COPY, transferarray, new DragSourceListener() {
			public void dragFinished(DragSourceEvent event) {}
			public void dragSetData(DragSourceEvent event) {
				IStructuredSelection selection = (IStructuredSelection) listViewer.getSelection();
				Object[] itSel = selection.toArray();
				String ids = "";
				for(Object item : itSel) {
					MagicItem magicItem = (MagicItem)item;
					ids += MagicItem.class.getSimpleName() + "_" + magicItem.getId() +";";
				}
				event.data = ids;
			}
			public void dragStart(DragSourceEvent event) {
				event.doit = ! listViewer.getSelection().isEmpty();
			}
		});
		listViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				LinkManager.openSimpleModel(SharedSimpleModelBox.instance().get(ISharedModelBoxIds.BOX_MAGIC_ITEM).get(((MagicItem) selection.getFirstElement()).getId()));
			}
		});
		//this.setPartName(LocaleManager.instance().getMessage("search") + " (" + listViewer.getTable().getItemCount() + ")");
	}
	
	public void setDefaultButton() {
		if(searchButton != null)
			this.getShell().setDefaultButton(searchButton);		
	}

}
