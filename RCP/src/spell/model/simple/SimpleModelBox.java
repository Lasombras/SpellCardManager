package spell.model.simple;

import java.util.ArrayList;

public class SimpleModelBox {
	private ArrayList<IModelListener> modelListeners;

	protected ArrayList<SimpleModel> objects;

	private ArrayList<SimpleModel> removeObjects;

	private ArrayList<SimpleModel> addObjects;

	public boolean isDirty() {
		boolean dirty = false;
		if (removeObjects.size() > 0)
			return true;
		if (addObjects.size() > 0)
			return true;
		int i = 0;
		while (i < objects.size() && !dirty) {
			dirty = objects.get(i).isDirty();
			i++;
		}
		return dirty;
	}

	public void setSauved() {
		removeObjects.clear();
		addObjects.clear();
		for (int i = 0; i < objects.size(); i++) {
			objects.get(i).setSaved();
		}
	}

	public SimpleModelBox(SimpleModel[] objs) {
		modelListeners = new ArrayList<IModelListener>();
		objects = new ArrayList<SimpleModel>();
		for (int i = 0; i < objs.length; i++) {
			objects.add(objs[i]);
			objs[i].setModel(this);
		}
		removeObjects = new ArrayList<SimpleModel>();
		addObjects = new ArrayList<SimpleModel>();
	}

	public void addModelListener(IModelListener listener) {
		if (!modelListeners.contains(listener))
			modelListeners.add(listener);
	}

	public void removeModelListener(IModelListener listener) {
		modelListeners.remove(listener);
	}

	public void fireModelChanged(SimpleModel object, String type) {
		for (int i = 0; i < modelListeners.size(); i++) {
			((IModelListener) modelListeners.get(i)).modelChanged(object, type);
		}
	}
	
	public void fireSizeChanged(SimpleModel object) {
		for (int i = 0; i < modelListeners.size(); i++) {
			((IModelListener) modelListeners.get(i)).modelChanged(object, IModelListener.SIZED);
		}
	}

	public SimpleModel[] getContents() {
		SimpleModel[] simpleModel = new SimpleModel[objects.size()];
		objects.toArray(simpleModel);
		return simpleModel;
	}

	public SimpleModel[] getRemoveContents() {
		SimpleModel[] simpleModel = new SimpleModel[removeObjects.size()];
		removeObjects.toArray(simpleModel);
		return simpleModel;
	}

	public SimpleModel[] getAddContents() {
		SimpleModel[] simpleModel = new SimpleModel[addObjects.size()];
		addObjects.toArray(simpleModel);
		return simpleModel;
	}

	public void add(SimpleModel object, boolean notify) {
		objects.add(object);
		addObjects.add(object);
		object.setModel(this);
		if (notify)
			fireModelChanged(object, IModelListener.ADDED); //$NON-NLS-1$
	}

	public void remove(SimpleModel object, boolean notify) {
		objects.remove(object);
		removeObjects.add(object);
		object.setModel(null);
		if (notify)
			fireModelChanged(object, IModelListener.REMOVED); //$NON-NLS-1$
	}
	
	public SimpleModel get(int id) {
		for(int i = 0; i < objects.size(); i++) {
			if(objects.get(i).getId() == id)
				return objects.get(i);
		}
		return null;
	}

}