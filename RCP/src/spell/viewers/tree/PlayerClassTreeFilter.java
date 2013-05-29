package spell.viewers.tree;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import spell.model.Level;
import spell.model.Spell;

public class PlayerClassTreeFilter extends ViewerFilter {

	private int playerClass = -1;
		
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		boolean view = true;
		if(element instanceof Spell && parentElement instanceof LevelTreeBox) {
			if(this.playerClass < 0)
				return true;
			view = false;
			Spell spell = (Spell) element;
			LevelTreeBox levelBox = (LevelTreeBox)parentElement;
			for(Level level : spell.getLevels()) {
				if(	level.getPlayerClassId() == this.playerClass && 
					levelBox.getLevel() == level.getLevel())
					view = true;
			}
		}
		return view;
	}

	public void setPlayerClass(int playerClass) {
		this.playerClass = playerClass;
	}

	public int getPlayerClass() {
		return playerClass;
	}

}
