
package spell.viewers.table;


import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import spell.model.Level;
import spell.model.PlayerClass;


/**
 * Label provider for the TableViewerExample
 * 
 * @see org.eclipse.jface.viewers.LabelProvider 
 */
public class LevelLabelProvider 
	extends LabelProvider
	implements ITableLabelProvider {
	
	private PlayerClass[] playerClasses;
	
	public LevelLabelProvider(PlayerClass[] playerClasses) {
		this.playerClasses = playerClasses;
	}
	
	public String getColumnText(Object element, int columnIndex) {
		String result = "";
		Level level = (Level) element;
		switch (columnIndex) {
			case 0:  // COMPLETED_COLUMN
				for(PlayerClass playerClass : playerClasses)
					if(playerClass.getId() == level.getPlayerClassId())
						result = playerClass.getTitle();
				break;
			case 1 :
				result = level.getLevel() + "";
				break;
			default :
				break; 	
		}
		return result;
	}

	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

}
