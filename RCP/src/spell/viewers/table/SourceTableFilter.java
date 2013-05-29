package spell.viewers.table;

import java.util.ArrayList;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import spell.model.MagicItem;
import spell.model.Spell;
import spell.viewers.IViewerFilterChangedListener;

public class SourceTableFilter extends ViewerFilter {

	
	private static SourceTableFilter instance;
	private ArrayList<Integer> sources;
	private ArrayList<IViewerFilterChangedListener> listeners;
	
	public final static SourceTableFilter instance() {
		if(instance == null) {
			instance = new SourceTableFilter();
		}
		return instance;
	}

	private SourceTableFilter() {
		sources = new ArrayList<Integer>();
		listeners = new ArrayList<IViewerFilterChangedListener>();
	}
	
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if(element instanceof Spell) {
			if(this.sources.contains(new Integer(((Spell)element).getSourceId())))
				return true;
		} else if(element instanceof MagicItem) {
			if(this.sources.contains(new Integer(((MagicItem)element).getSourceId())))
				return true;
		}
		return false;
	}

	public void addSource(int id) {
		sources.add(new Integer(id));
	}
	
	public void clear() {
		sources.clear();
	}
	
	public void addListener(IViewerFilterChangedListener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(IViewerFilterChangedListener listener) {
		listeners.remove(listener);
	}
	
	public void update() {
		for(IViewerFilterChangedListener listener : listeners) {
			listener.filterChanged();
		}
	}
}
