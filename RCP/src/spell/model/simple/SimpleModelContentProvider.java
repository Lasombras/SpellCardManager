package spell.model.simple;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class SimpleModelContentProvider implements IStructuredContentProvider {

	public Object[] getElements(Object inputElement) {
		return ((SimpleModelBox) inputElement).getContents();
	}

	public void dispose() {
	}

	public void inputChanged(Viewer v, Object o, Object n) {
	}
}
