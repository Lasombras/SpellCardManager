package spell.services.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import spell.databases.Session;
import spell.model.Component;
import spell.model.simple.SimpleModelBox;
import spell.services.ComponentService;


public class ComponentServiceImpl implements ComponentService {
	public final static String TABLE_NAME = "COMPONENT";
	private Component[] cache;
	
	public ComponentServiceImpl() {
	}
	
	public Component get(int id, Session session) {
		Component res = null;
		try {
			Statement stat = session.getConnection().createStatement();
			ResultSet rs = stat.executeQuery("select * from " + TABLE_NAME + " where id = " + id + ";");
		    if(rs.next()) {
		    	res = new Component(rs.getInt("id"), rs.getString("name"), rs.getString("image"), rs.getString("shortname"));
		    	res.setExist(true);
		    }
		    rs.close();
		} catch (Exception e) {}
		return res;
	}

	public boolean delete(Component component, Session session) {
		boolean res = false;
		Statement stat = null;
		try {
			stat = session.getConnection().createStatement();
			res = stat.executeUpdate("delete from " + TABLE_NAME + " where id = " + component.getId() + " ;") == 1;
			component.setExist(false);
		} catch (Exception e) {}
		finally {
			try {
				if(stat != null) stat.close();
			} catch (Exception e) {}
		}
		return res;
	}

	public Component[] getAll(Session session) {
		ArrayList<Component> list = new ArrayList<Component>();
		try {
			Statement stat = session.getConnection().createStatement();
			ResultSet rs = stat.executeQuery("select * from " + TABLE_NAME + " order by name;");
		    while (rs.next()) {
		    	Component obj = new Component(rs.getInt("id"), rs.getString("name"), rs.getString("image"), rs.getString("shortname"));
		    	obj.setExist(true);
		    	list.add(obj);
		    }
		    rs.close();
		} catch (Exception e) {}
		Component[] tab = new Component[list.size()];
	    list.toArray(tab);
		return tab;
	}

	public SimpleModelBox getAllInBox(Session session) {
		return new SimpleModelBox(getAll(session));
	}

	public boolean save(Component component, Session session) {
		boolean res = false;
		PreparedStatement prep = null;
		try {
			if(component.isExist()) {
				prep = session.getConnection().prepareStatement("update " + TABLE_NAME + " set id = ?, name = ?, image = ?, shortname = ?);");
				prep.setInt(1, component.getId());
				prep.setString(2, component.getTitle());
				prep.setString(3, component.getImage());
				prep.setString(4, component.getShortName());
				res = prep.executeUpdate() == 1;				
			} else {
				prep = session.getConnection().prepareStatement("insert into " + TABLE_NAME + " values (?, ?, ?, ?);");
				prep.setNull(1, 0);
				prep.setString(2, component.getTitle());
				prep.setString(3, component.getImage());
				prep.setString(4, component.getShortName());
				res = prep.executeUpdate() == 1;
				ResultSet resKey = prep.getGeneratedKeys();
				if(resKey.next())
					component.setId(resKey.getInt("last_insert_rowid()"));
				resKey.close();
			}
			component.setExist(true);
			component.setDirty(false);
		} catch (Exception e) {e.printStackTrace();}
		finally {
			try {
				if(prep != null) prep.close();
			} catch (Exception e) {}
		}
		return res;
	}
	

	public boolean checkTable(boolean drop, Session session) {
		boolean res = true;
		Statement stat = null;
		try {
			stat = session.getConnection().createStatement();
			if(drop)
				stat.executeUpdate("drop table if exists " + TABLE_NAME + ";");
			stat.executeUpdate("create table " + TABLE_NAME + " (id INTEGER PRIMARY KEY AUTOINCREMENT, name, image, shortname);");
			res = false;
		} catch (Exception e) {}
		finally {
			try {
				if(stat != null) stat.close();
			} catch (Exception e) {}
		}
		return res;
	}
	
	public Component[] getCached() {
		return cache;
	}

	public Component[] setCached(Session session) {
		cache = getAll(session);
		return cache;
	}
	
	public Component getCached(int id) {
		for(Component component : cache)
			if(component.getId() == id)
				return component;
		return null;
	}
	
	public Element exportXML(Document document, Component[] components) {
		Element racine = null;
		try {						
			// Création de l'arborescence du DOM
			racine = document.createElement("Components");
			racine.appendChild(document.createComment("Liste des composantes"));
			
			for(Component component : components) {
				Element componentElem = document.createElement("Component");
				componentElem.setAttribute("id",component.getId()+"");
				componentElem.setAttribute("image",component.getImage());
				componentElem.setAttribute("shortName",component.getShortName());
				componentElem.setTextContent(component.getTitle());
				racine.appendChild(componentElem);	
			}
		} catch (Exception e) {}
		return racine;
	}

	public Component[] importXML(Element racine) {
		ArrayList<Component> components = new ArrayList<Component>();
		try {
			NodeList liste = racine.getElementsByTagName("Component");
			for(int idx = 0; idx < liste.getLength(); idx++) {
				Element componentElement = (Element)liste.item(idx);
				Component Component = new Component(
					Integer.parseInt(componentElement.getAttribute("id")),
					componentElement.getTextContent(),
					componentElement.getAttribute("image"),
					componentElement.getAttribute("shortName")
				);

				components.add(Component);
			}
		} catch (Exception e) {}
		Component[] result = new Component[components.size()];
		components.toArray(result);
		return result;
	}


}
