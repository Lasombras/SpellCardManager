package spell.viewers.table;
import java.util.Arrays;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import spell.model.Level;
import spell.model.PlayerClass;
import spell.tools.LocaleManager;

public class TableViewerLevel {
/**
	 * @param parent
	 */

	//	private Shell shell;
	private Table table;
	private TableViewer tableViewer;
	
	// Create a ExamplelevelList and assign it to an instance variable
	private LevelList levelList; 
	private LevelLabelProvider levelLabelProvider;

	// Set the table column property names
	private final String CLASS_COLUMN 	= LocaleManager.instance().getMessage("class");
	private final String LEVEL_COLUMN 	= LocaleManager.instance().getMessage("level");

	// Set column names
	private String[] columnNames = new String[] { 
			CLASS_COLUMN,
			LEVEL_COLUMN
			};

	public TableViewerLevel(Composite parent, Level[] levels,  PlayerClass[] playerClasses, int heightHint) {
		this.levelList = new LevelList(levels, playerClasses);
		this.levelLabelProvider = new LevelLabelProvider(playerClasses);
		this.addChildControls(parent, heightHint);
	}

	/**
	 * Release resources
	 */
	public void dispose() {
		
		// Tell the label provider to release its ressources
		tableViewer.getLabelProvider().dispose();
	}

	/**
	 * Create a new shell, add the widgets, open the shell
	 * @return the shell that was created	 
	 */
	private void addChildControls(Composite composite, int heightHint) {

		// Create a composite to hold the children
		GridData gridData = new GridData (GridData.HORIZONTAL_ALIGN_FILL | GridData.FILL_BOTH);
		composite.setLayoutData (gridData);

		// Set numColumns to 2 for the buttons 
		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = 4;
		composite.setLayout (layout);

		// Create the table 
		createTable(composite, heightHint);
		
		// Create and setup the TableViewer
		createTableViewer();
		tableViewer.setContentProvider(new LevelContentProvider());
		tableViewer.setLabelProvider(levelLabelProvider);
		// The input for the table viewer is the instance of ExamplelevelList
		tableViewer.setInput(levelList);

		// Add the buttons
		createButtons(composite);
	}

	/**
	 * Create the Table
	 */
	private void createTable(Composite parent, int heightHint) {
		int style = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | 
					SWT.FULL_SELECTION | SWT.HIDE_SELECTION;

		table = new Table(parent, style);
		
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalSpan = 3;
		gridData.heightHint = heightHint;
		table.setLayoutData(gridData);		
					
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		TableColumn column = new TableColumn(table, SWT.LEFT, 0);
		column.setText(CLASS_COLUMN);
		column.setWidth(90);

		column = new TableColumn(table, SWT.CENTER, 1);
		column.setText(LEVEL_COLUMN);
		column.setWidth(30);
	}

	/**
	 * Create the TableViewer 
	 */
	private void createTableViewer() {

		tableViewer = new TableViewer(table);
		tableViewer.setUseHashlookup(true);
		
		tableViewer.setColumnProperties(columnNames);

		// Create the cell editors
		CellEditor[] editors = new CellEditor[columnNames.length];

		// Column 1 : (Combo Box) 
		editors[0] = new ComboBoxCellEditor(table, levelList.getPlayerClasses(), SWT.READ_ONLY);

		// Column 2 : Level(Text with digits only)
		TextCellEditor textEditor = new TextCellEditor(table);
		editors[1] = textEditor;

		// Assign the cell editors to the viewer 
		tableViewer.setCellEditors(editors);
		// Set the cell modifier for the viewer
		tableViewer.setCellModifier(new LevelCellModifier(this));
	}

	class LevelContentProvider implements IStructuredContentProvider, ILevelListViewer {
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
			if (newInput != null)
				((LevelList) newInput).addChangeListener(this);
			if (oldInput != null)
				((LevelList) oldInput).removeChangeListener(this);
		}

		public void dispose() {
			levelList.removeChangeListener(this);
		}

		// Return the levels as an array of Objects
		public Object[] getElements(Object parent) {
			return levelList.getLevels().toArray();
		}

		/* (non-Javadoc)
		 * @see IlevelListViewer#addlevel(Examplelevel)
		 */
		public void addLevel(Level level) {
			tableViewer.add(level);
		}

		/* (non-Javadoc)
		 * @see IlevelListViewer#removelevel(Examplelevel)
		 */
		public void removeLevel(Level level) {
			tableViewer.remove(level);			
		}

		/* (non-Javadoc)
		 * @see IlevelListViewer#updatelevel(Examplelevel)
		 */
		public void updateLevel(Level level) {
			tableViewer.update(level, null);	
		}
	}
	
	/**
	 * Return the array of choices for a multiple choice cell
	 */
	public String[] getChoices(String property) {
		if (CLASS_COLUMN.equals(property))
			return levelList.getPlayerClasses();  // The ExamplelevelList knows about the choice of owners
		else
			return new String[]{};
	}

	/**
	 * Add the "Add", "Delete" and "Close" buttons
	 * @param parent the parent composite
	 */
	private void createButtons(Composite parent) {
		
		// Create and configure the "Add" button
		Button add = new Button(parent, SWT.PUSH | SWT.CENTER);
		add.setText(LocaleManager.instance().getMessage("add"));
		
		GridData gridData = new GridData (GridData.HORIZONTAL_ALIGN_BEGINNING);
		gridData.widthHint = 60;
		add.setLayoutData(gridData);
		add.addSelectionListener(new SelectionAdapter() {
       	
       		// Add a level to the ExamplelevelList and refresh the view
			public void widgetSelected(SelectionEvent e) {
				levelList.addLevel();
			}
		});

		//	Create and configure the "Delete" button
		Button delete = new Button(parent, SWT.PUSH | SWT.CENTER);
		delete.setText(LocaleManager.instance().getMessage("remove"));
		gridData = new GridData (GridData.HORIZONTAL_ALIGN_BEGINNING);
		gridData.widthHint = 60; 
		delete.setLayoutData(gridData); 

		delete.addSelectionListener(new SelectionAdapter() {
       	
			//	Remove the selection and refresh the view
			public void widgetSelected(SelectionEvent e) {
				Level level = (Level) ((IStructuredSelection) 
						tableViewer.getSelection()).getFirstElement();
				if (level != null) {
					levelList.removeLevel(level);
				} 				
			}
		});
		
	}

	/**
	 * Return the column names in a collection
	 * 
	 * @return List  containing column names
	 */
	public java.util.List<String> getColumnNames() {
		return Arrays.asList(columnNames);
	}

	/**
	 * @return currently selected item
	 */
	public ISelection getSelection() {
		return tableViewer.getSelection();
	}

	/**
	 * Return the ExamplelevelList
	 */
	public LevelList getLevelList() {
		return levelList;	
	}
	
	/**
	 * Return the parent composite
	 */
	public Control getControl() {
		return table.getParent();
	}

}