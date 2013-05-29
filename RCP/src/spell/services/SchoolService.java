package spell.services;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import spell.databases.Session;
import spell.model.School;
import spell.model.simple.SimpleModelBox;

public interface SchoolService  {
	public boolean checkTable(boolean drop, Session session);
	public School get(int id, Session session);
	public School[] getAll(Session session);
	public SimpleModelBox getAllInBox(Session session);
	public boolean save(School school, Session session);
	public boolean delete(School school, Session session);
	public School[] getCached();
	public School getCached(int id);
	public School[] setCached(Session session);
	public Element exportXML(Document document, School[] schools);
	public School[] importXML(Element racine);

}
