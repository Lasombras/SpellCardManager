package spell.viewers.table;

import spell.model.Level;

public interface ILevelListViewer {
	
	public void addLevel(Level level);
	
	public void removeLevel(Level level);
	
	public void updateLevel(Level level);
}
