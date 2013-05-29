package spell.editors;

import spell.model.MagicItem;

public class MagicItemFormEditorInput extends FormEditorInput {

	private MagicItem magicItem;

	public MagicItemFormEditorInput(String name, MagicItem magicItem) {
		super(name);
		this.magicItem = magicItem;

	}

	public MagicItem getMagicItem() {
		return magicItem;
	}
}