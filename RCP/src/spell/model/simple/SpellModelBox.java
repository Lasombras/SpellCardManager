package spell.model.simple;

import spell.model.Spell;

public class SpellModelBox extends SimpleModelBox {

	public SpellModelBox(Spell[] objs) {
		super(objs);
	}

	public Spell get(int id) {
		return (Spell) super.get(id);
	}
	
	public Spell get(String originalName) {
		for(int i = 0; i < objects.size(); i++) {
			Spell spell = (Spell)objects.get(i);
			if(spell.getOriginalName().equalsIgnoreCase(originalName))
				return spell;
		}
		return null;
	}

}