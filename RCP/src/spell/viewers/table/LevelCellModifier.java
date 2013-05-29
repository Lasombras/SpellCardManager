package spell.viewers.table;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.widgets.TableItem;

import spell.model.Level;

/**
 * This class implements an ICellModifier
 * An ICellModifier is called when the user modifes a cell in the 
 * tableViewer
 */

public class LevelCellModifier implements ICellModifier {
	private TableViewerLevel tableViewer;
	
	/**
	 * Constructor 
	 * @param TableViewerLevel an instance of a TableViewer 
	 */
	public LevelCellModifier(TableViewerLevel tableViewer) {
		super();
		this.tableViewer = tableViewer;
	}

	/**
	 * @see org.eclipse.jface.viewers.ICellModifier#canModify(java.lang.Object, java.lang.String)
	 */
	public boolean canModify(Object element, String property) {
		return true;
	}

	/**
	 * @see org.eclipse.jface.viewers.ICellModifier#getValue(java.lang.Object, java.lang.String)
	 */
	public Object getValue(Object element, String property) {

		// Find the index of the column
		int columnIndex = tableViewer.getColumnNames().indexOf(property);

		Object result = null;
		Level level = (Level) element;

		switch (columnIndex) {
			case 0 : // COMPLETED_COLUMN
				String stringValue =  tableViewer.getLevelList().getPlayerClass(level.getPlayerClassId());
				String[] choices = tableViewer.getChoices(property);
				int i = choices.length - 1;
				while (!stringValue.equals(choices[i]) && i > 0)
					--i;
				result = new Integer(i);	
				break;
			case 1 : // DESCRIPTION_COLUMN 
				result = level.getLevel() + "";
				break;
			default :
				result = "";
		}
		return result;	
	}

	/**
	 * @see org.eclipse.jface.viewers.ICellModifier#modify(java.lang.Object, java.lang.String, java.lang.Object)
	 */
	public void modify(Object element, String property, Object value) {	

		// Find the index of the column 
		int columnIndex	= tableViewer.getColumnNames().indexOf(property);
			
		TableItem item = (TableItem) element;
		Level level = (Level) item.getData();
		String valueString;

		switch (columnIndex) {
			case 0 : // COMPLETED_COLUMN 				
			    level.setPlayerClassId(tableViewer.getLevelList().getPlayerClass(tableViewer.getChoices(property)[((Integer)value).intValue()]));
				break;
			case 1 : // PERCENT_COLUMN
				valueString = ((String) value).trim();
				if (valueString.length() == 0)
					valueString = "0";
				if(valueString.matches("[0-9]{1,2}")) {
					level.setLevel(Integer.parseInt(valueString));
				}
				break;
			default :
			}
		tableViewer.getLevelList().levelChanged(level);
	}
}
