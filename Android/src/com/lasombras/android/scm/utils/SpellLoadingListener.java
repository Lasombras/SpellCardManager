package com.lasombras.android.scm.utils;

import com.lasombras.android.scm.model.Spell;

public interface SpellLoadingListener {

	public void addSpell(Spell spell);
	public void setSpellCount(int count);
}
