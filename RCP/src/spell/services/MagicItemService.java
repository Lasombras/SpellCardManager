package spell.services;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import spell.databases.Session;
import spell.model.MagicItem;
import spell.model.simple.MagicItemModelBox;
import spell.search.criteria.MagicItemSearchCriteria;

public interface MagicItemService  {
	public boolean checkTable(boolean drop, Session session);
	public MagicItem get(int id, Session session);
	public MagicItem[] getAll(Session session);
	public MagicItemModelBox getAllInBox(Session session);
	public boolean save(MagicItem spell, Session session);
	public boolean delete(MagicItem spell, Session session);
	public MagicItem[] search(Session session, MagicItemSearchCriteria criteria);
	public MagicItem[] getCached();
	public MagicItem[] setCached(Session session);
	public Element exportXML(Document document, MagicItem[] spells, boolean androidExport);
	public MagicItem[] importXML(Element racine);
}
