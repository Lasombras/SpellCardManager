package spell.editors;

import spell.model.Spell;

public class SpellFormEditorInput extends FormEditorInput {

	private Spell spell;

	public SpellFormEditorInput(String name, Spell spell) {
		super(name);
		this.spell = spell;

	}

	public Spell getSpell() {
		return spell;
	}
}