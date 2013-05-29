package spell.viewers.table;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import spell.model.Level;
import spell.model.PlayerClass;



public class LevelList {

	private Vector<Level> levels = new Vector<Level>();
	private Set<ILevelListViewer> changeListeners = new HashSet<ILevelListViewer>();
	private String[] playerClassesStr;
	private PlayerClass[] playerClasses;

	/**
	 * Constructor
	 */
	public LevelList(Level[] levels, PlayerClass[] playerClasses) {
		super();
		for(Level level : levels)
			this.levels.add(level);
		this.playerClassesStr  = new String[playerClasses.length];
		for(int i = 0; i < playerClasses.length; i++)
			this.playerClassesStr[i] = playerClasses[i].getTitle();
		this.playerClasses = playerClasses;
	}
	
	public String[] getPlayerClasses() {
		return playerClassesStr;
	}
	
	public String getPlayerClass(int id) {
		for(PlayerClass playerClass : playerClasses)
			if(id == playerClass.getId())
				return playerClass.getTitle();
		return "";
	}
	public int getPlayerClass(String name) {
		for(PlayerClass playerClass : playerClasses)
			if(name.equals(playerClass.getTitle()))
				return playerClass.getId();
		return 0;
	}
	
	public Vector<Level> getLevels() {
		return levels;
	}
	
	public void addLevel() {
		Level level = new Level(playerClasses[0].getId(),0);
		levels.add(levels.size(), level);
		Iterator<ILevelListViewer> iterator = changeListeners.iterator();
		while (iterator.hasNext())
			iterator.next().addLevel(level);
	}

	public void removeLevel(Level level) {
		levels.remove(level);
		Iterator<ILevelListViewer> iterator = changeListeners.iterator();
		while (iterator.hasNext())
			iterator.next().removeLevel(level);
	}

	public void levelChanged(Level level) {
		Iterator<ILevelListViewer> iterator = changeListeners.iterator();
		while (iterator.hasNext())
			iterator.next().updateLevel(level);
	}

	public void removeChangeListener(ILevelListViewer viewer) {
		changeListeners.remove(viewer);
	}

	public void addChangeListener(ILevelListViewer viewer) {
		changeListeners.add(viewer);
	}

}
