package spell.model.simple;

public class SimpleModel {
	protected SimpleModelBox model;

	private String title;

	private String image;

	private int id;

	private boolean exist;
	private int size;
	private boolean dirty;

	public SimpleModel(int id, String title, String image) {
		this.title = title;
		this.image = image;
		this.id = id;
		this.exist = false;
		this.dirty = false;
		this.size = 0;
	}

	public String getImage() {
		return image == null ? "empty.gif" : image;
	}

	public String getTitle() {
		return title == null ? "" : title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public void setModel(SimpleModelBox model) {
		this.model = model;
	}

	public void fireModelChanged() {
		this.setDirty(true);
		if (model != null) {
			model.fireModelChanged(this, IModelListener.CHANGED);
		}
	}
	
	public void fireSizeChanged() {
		if (model != null) {
			model.fireSizeChanged(this);
		}
	}

	public boolean isExist() {
		return exist;
	}

	public void setExist(boolean exist) {
		this.exist = exist;
	}

	public boolean isDirty() {
		return dirty;
	}

	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	public void setSaved() {
		this.setDirty(false);
		this.setExist(true);
//		if (model != null) {
//			model.fireModelChanged(this, IModelListener.SAVED);
//		}
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getSize() {
		return size;
	}

	public void increaseSize() {
		this.size++;
	}
	
	public void decreaseSize() {
		this.size--;
	}
	
	public void clearSize() {
		this.size = 0;
	}
	
}