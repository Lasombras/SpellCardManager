package spell.viewers.tree;


public class LevelTreeBox extends SimpleModelTreeBox {
	
	public LevelTreeBox(int level, String title, String image, SimpleModelTreeBox parent) {
		super(level, title, image, parent);
	}

	public int getLevel() {
		return super.getId();
	}

	public void setLevel(int level) {
		super.setId(level);
	}	

	
}
