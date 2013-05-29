package spell.viewers.tree;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

import spell.model.simple.SimpleModelBox;

public class CardModelTreeContentProvider implements ITreeContentProvider {

	private static Object[] EMPTY_ARRAY = new Object[0];
	protected TreeViewer viewer;

	public CardModelTreeContentProvider() {
		super();
	}
	
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if(parentElement instanceof LevelTreeBox) {
			return ((LevelTreeBox)parentElement).getContent();
		} else if(parentElement instanceof InventoryTreeBox) {
			return ((InventoryTreeBox)parentElement).getContent();
		} else if(parentElement instanceof SimpleModelBox) {
			return ((SimpleModelBox)parentElement).getContents();
		}
		return EMPTY_ARRAY;
	}

	@Override
	public Object getParent(Object element) {
		if(element instanceof LevelTreeBox) {
			return ((LevelTreeBox)element).getParent();
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		return getChildren(element).length > 0;
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = (TreeViewer)viewer;
	}

	@Override
	public void dispose() {
		
	}
}
