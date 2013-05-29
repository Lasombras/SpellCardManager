package spell.viewers.tree;

import java.util.ArrayList;

import spell.model.simple.SimpleModel;

public class SimpleModelTreeBox extends SimpleModel {

	private ArrayList<SimpleModel> models;
	private SimpleModelTreeBox parent;
	
	public SimpleModelTreeBox(int id, String title, String image, SimpleModelTreeBox parent) {
		super(id, title, image);
		this.parent = parent;
		models = new ArrayList<SimpleModel>();
	}
	
	public void add(SimpleModel item) {
		models.add(item);
	}
	
	public SimpleModel[] getContent() {
		SimpleModel[] contentsTab = new SimpleModel[models.size()];
		models.toArray(contentsTab);
		return contentsTab;
	}

	public SimpleModelTreeBox getParent() {
		return parent;
	}

	public void setParent(SimpleModelTreeBox parent) {
		this.parent = parent;
	}
	
	public boolean contains(SimpleModel item) {
		return models.contains(item);
	}
	
	public void remove(SimpleModel item) {
		models.remove(item);
	}
	
	public void removeAll() {
		models.clear();
	}
	
	
}
