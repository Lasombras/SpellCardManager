package spell.services;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import spell.databases.Session;
import spell.model.Source;
import spell.model.simple.SimpleModelBox;

public interface SourceService  {
	public boolean checkTable(boolean drop, Session session);
	public Source get(int id, Session session);
	public Source[] getAll(Session session);
	public SimpleModelBox getAllInBox(Session session);
	public boolean save(Source source, Session session);
	public boolean delete(Source source, Session session);
	public Source[] getCached();
	public Source getCached(int id);
	public Source[] setCached(Session session);
	public Element exportXML(Document document, Source[] schools);
	public Source[] importXML(Element racine);

}
