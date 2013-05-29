package spell.services;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import spell.databases.Session;
import spell.model.Spell;
import spell.model.simple.SpellModelBox;
import spell.search.criteria.SpellSearchCriteria;

public interface SpellService  {
	public boolean checkTable(boolean drop, Session session);
	public Spell get(int id, Session session);
	public Spell[] getAll(Session session);
	public SpellModelBox getAllInBox(Session session);
	public boolean save(Spell spell, Session session);
	public boolean delete(Spell spell, Session session);
	public Spell[] search(Session session, SpellSearchCriteria criteria);
	public Spell[] getCached();
	public Spell[] setCached(Session session);
	public Element exportXML(Document document, Spell[] spells, boolean androidExport);
	public Spell[] importXML(Element racine);
}
