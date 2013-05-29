package spell.services;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import spell.databases.Session;
import spell.model.Slot;
import spell.model.simple.SimpleModelBox;

public interface SlotService  {
	public boolean checkTable(boolean drop, Session session);
	public Slot get(int id, Session session);
	public Slot[] getAll(Session session);
	public SimpleModelBox getAllInBox(Session session);
	public boolean save(Slot slot, Session session);
	public boolean delete(Slot slot, Session session);
	public Slot[] getCached();
	public Slot getCached(int id);
	public Slot[] setCached(Session session);
	public Element exportXML(Document document, Slot[] slots);
	public Slot[] importXML(Element racine);
}
