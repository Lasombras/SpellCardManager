package spell.services;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import spell.databases.Session;
import spell.model.PlayerClass;
import spell.model.simple.SimpleModelBox;

public interface PlayerClassService  {
	public boolean checkTable(boolean drop, Session session);
	public PlayerClass get(int id, Session session);
	public PlayerClass[] getAll(Session session);
	public SimpleModelBox getAllInBox(Session session);
	public boolean save(PlayerClass playerClass, Session session);
	public boolean delete(PlayerClass playerClass, Session session);
	public PlayerClass[] getCached();
	public PlayerClass getCached(int id);
	public PlayerClass[] setCached(Session session);
	public Element exportXML(Document document, PlayerClass[] playerClasses);
	public PlayerClass[] importXML(Element racine);
}
