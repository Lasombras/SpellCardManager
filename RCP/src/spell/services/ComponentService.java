package spell.services;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import spell.databases.Session;
import spell.model.Component;
import spell.model.simple.SimpleModelBox;

public interface ComponentService  {
	public boolean checkTable(boolean drop, Session session);
	public Component get(int id, Session session);
	public Component[] getAll(Session session);
	public SimpleModelBox getAllInBox(Session session);
	public boolean save(Component component, Session session);
	public boolean delete(Component component, Session session);
	public Component[] getCached();
	public Component getCached(int id);
	public Component[] setCached(Session session);
	public Element exportXML(Document document, Component[] components);
	public Component[] importXML(Element racine);
}
