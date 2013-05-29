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
import spell.model.Source;
import spell.model.PlayerClass;
import spell.model.School;
import spell.model.Spell;
import spell.model.simple.CardModelLabelProvider;
import spell.model.simple.ISharedModelBoxIds;
import spell.model.simple.SharedSimpleModelBox;
import spell.model.simple.SimpleModelContentProvider;
import spell.model.simple.SpellModelBox;
import spell.search.criteria.SpellSearchCriteria;
import spell.services.ServiceFactory;
import spell.tools.LinkManager;
import spell.tools.LocaleManager;


public class SpellSearchView  extends Composite {

	private Composite resultComposite = null;
	private Composite searchComposite = null;
	private Table listResult = null;
	private TableViewer listViewer = null;

	private Spinner level;
	private Text titleField;
	private Text originalNameField;
	private ImageCombo playerClass;
	private ImageCombo school;
	private ImageCombo sourceCombo;
	private ImageCombo levelLabel;
	private Button searchButton;
	
	private PlayerClass[] playerClasses;
	private School[] schools;
	private Source[] sources;
		
	public void setDefaultButton() {
		if(searchButton != null)
			this.getShell().setDefaultButton(searchButton);		
	}

	public SpellSearchView(Composite parent, int style) {
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
			playerClasses = ServiceFactory.getPlayerClassService().getAll(session);
			schools = ServiceFactory.getSchoolService().getAll(session);
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
		label.setText(LocaleManager.instance().getMessage("spell") + " : ");
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
		label.setText(LocaleManager.instance().getMessage("school") + " : ");
		school = new ImageCombo(searchComposite, SWT.BORDER);
		school.setEditable(false);
		school.setBackground(titleField.getBackground());
		school.add("", Activator.getImage(Activator.ICON_CROSS));
		for(School schoolItem : schools) {
			school.add(schoolItem.getTitle(), Activator.getImage(Activator.FOLDER_IMAGES + schoolItem.getImage()));
		}
		school.setLayoutData(fieldGridData);

		levelLabel = new ImageCombo(searchComposite, SWT.NONE);
		levelLabel.setEditable(false);
		levelLabel.setBackground(label.getBackground());
		levelLabel.add(LocaleManager.instance().getMessage("level") + " >=", null);
		levelLabel.add(LocaleManager.instance().getMessage("level") + " =", null);
		levelLabel.add(LocaleManager.instance().getMessage("level") + " <=", null);
		levelLabel.add(LocaleManager.instance().getMessage("level") + " <>", null);
		levelLabel.select(0);

		level = new Spinner(searchComposite, SWT.BORDER);
		level.setMaximum(20);
		level.setMinimum(0);
		level.setPageIncrement(5);
		level.setIncrement(1);		
		level.setSelection(0);
		level.setLayoutData(fieldGridData);

		label = new Label(searchComposite, SWT.NONE);
		label.setText(LocaleManager.instance().getMessage("class") + " : ");
		playerClass = new ImageCombo(searchComposite, SWT.BORDER);
		playerClass.setEditable(false);
		playerClass.setBackground(titleField.getBackground());
		playerClass.add("", Activator.getImage(Activator.ICON_CROSS));
		for(PlayerClass playerClassItem : playerClasses) {
			playerClass.add(playerClassItem.getTitle(), Activator.getImage(Activator.FOLDER_IMAGES + playerClassItem.getImage()));
		}
		playerClass.setLayoutData(fieldGridData);


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
					SpellSearchCriteria criteria = new SpellSearchCriteria();
					criteria.setName(titleField.getText());
					criteria.setOriginalName(originalNameField.getText());
					criteria.setLevel(level.getSelection());
					if(levelLabel.getSelectionIndex() == 0) criteria.setLevelSign(SpellSearchCriteria.GREATHER_THAN);
					if(levelLabel.getSelectionIndex() == 1)	criteria.setLevelSign(SpellSearchCriteria.EQUAL);
					if(levelLabel.getSelectionIndex() == 2)	criteria.setLevelSign(SpellSearchCriteria.LEATHER_THAN);
					if(levelLabel.getSelectionIndex() == 3)	criteria.setLevelSign(SpellSearchCriteria.NOT_EQUAL);

					if(school.getSelectionIndex() <= 0)
						criteria.setSchoolId(-1);
					else
						criteria.setSchoolId(schools[school.getSelectionIndex()-1].getId());
					if(sourceCombo.getSelectionIndex() <= 0)
						criteria.setSourceId(-1);
					else
						criteria.setSourceId(sources[sourceCombo.getSelectionIndex()-1].getId());
					if(playerClass.getSelectionIndex() <= 0)
						criteria.setPlayerClassId(-1);
					else
						criteria.setPlayerClassId(playerClasses[playerClass.getSelectionIndex()-1].getId());
					session = DatabaseManager.getInstance().openSession();
					listViewer.setInput(new SpellModelBox(ServiceFactory.getSpellService().search(session, criteria)));
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
					Spell spell = (Spell)item;
					ids += Spell.class.getSimpleName() + "_" + spell.getId() +";";
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
				LinkManager.openSimpleModel(SharedSimpleModelBox.instance().get(ISharedModelBoxIds.BOX_SPELL).get(((Spell) selection.getFirstElement()).getId()));
			}
		});
		//this.setPartName(LocaleManager.instance().getMessage("search") + " (" + listViewer.getTable().getItemCount() + ")");
	}
	
}
