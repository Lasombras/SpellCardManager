package spell.services;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import spell.databases.Session;
import spell.model.ItemType;
import spell.model.simple.SimpleModelBox;

public interface ItemTypeService  {
	public boolean checkTable(boolean drop, Session session);
	public ItemType get(int id, Session session);
	public ItemType[] getAll(Session session);
	public SimpleModelBox getAllInBox(Session session);
	public boolean save(ItemType itemType, Session session);
	public boolean delete(ItemType itemType, Session session);
	public ItemType[] getCached();
	public ItemType getCached(int id);
	public ItemType[] setCached(Session session);
	public Element exportXML(Document document, ItemType[] itemTypes);
	public ItemType[] importXML(Element racine);
}
