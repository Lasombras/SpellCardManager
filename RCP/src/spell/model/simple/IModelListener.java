package spell.model.simple;

public interface IModelListener {
	String ADDED = "__added"; //$NON-NLS-1$

	String REMOVED = "__removed"; //$NON-NLS-1$

	String CHANGED = "__changed"; //$NON-NLS-1$
	
	String SIZED = "__sized"; //$NON-NLS-1$

	void modelChanged(SimpleModel object, String type);
}