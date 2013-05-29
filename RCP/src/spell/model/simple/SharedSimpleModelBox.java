package spell.model.simple;

import java.util.Hashtable;
import java.util.Map;

public class SharedSimpleModelBox {
	private Map<String, SimpleModelBox> shared;
	private static SharedSimpleModelBox instance;
	
	public final static SharedSimpleModelBox instance() {
		if(instance == null) {
			instance = new SharedSimpleModelBox();
		}
		return instance;
	}
	
	private SharedSimpleModelBox() {
		shared = new Hashtable<String, SimpleModelBox>();
	}

	public void add(String idRessource, SimpleModelBox box) {
		shared.put(idRessource, box);
	}
	
	public SimpleModelBox get(String idRessource) {
		return shared.get(idRessource);
	}
	
	public void remove(String idRessource) {
		shared.remove(idRessource);
	}
}
