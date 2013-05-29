package spell.services.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import spell.databases.Session;
import spell.model.School;
import spell.model.simple.SimpleModelBox;
import spell.services.SchoolService;


public class SchoolServiceImpl implements SchoolService {
	public final static String TABLE_NAME = "SCHOOL";
	
	private School[] cache;
	
	public SchoolServiceImpl() {
	}
	
	public School get(int id, Session session) {
		School res = null;
		try {
			Statement stat = session.getConnection().createStatement();
			ResultSet rs = stat.executeQuery("select * from " + TABLE_NAME + " where id = " + id + ";");
		    if(rs.next()) {
		    	res = new School(rs.getInt("id"), rs.getString("name"), rs.getString("image"));
		    	res.setExist(true);
		    }
		    rs.close();
		} catch (Exception e) {}
		return res;
	}

	public boolean delete(School school, Session session) {
		boolean res = false;
		Statement stat = null;
		try {
			stat = session.getConnection().createStatement();
			res = stat.executeUpdate("delete from " + TABLE_NAME + " where id = " + school.getId() + " ;") == 1;
			school.setExist(false);
		} catch (Exception e) {}
		finally {
			try {
				if(stat != null) stat.close();
			} catch (Exception e) {}
		}
		return res;
	}

	public School[] getAll(Session session) {
		ArrayList<School> list = new ArrayList<School>();
		try {
			Statement stat = session.getConnection().createStatement();
			ResultSet rs = stat.executeQuery("select * from " + TABLE_NAME + " order by name;");
		    while (rs.next()) {
		    	School obj = new School(rs.getInt("id"), rs.getString("name"), rs.getString("image"));
		    	obj.setExist(true);
		    	list.add(obj);
		    }
		    rs.close();
		} catch (Exception e) {}
		School[] tab = new School[list.size()];
	    list.toArray(tab);
		return tab;
	}

	public SimpleModelBox getAllInBox(Session session) {
		return new SimpleModelBox(getAll(session));
	}

	public boolean save(School school, Session session) {
		boolean res = false;
		PreparedStatement prep = null;
		try {
			if(school.isExist()) {
				prep = session.getConnection().prepareStatement("update " + TABLE_NAME + " set id = ?, name = ?, image = ?);");
				prep.setInt(1, school.getId());
				prep.setString(2, school.getTitle());
				prep.setString(3, school.getImage());
				res = prep.executeUpdate() == 1;				
			} else {
				prep = session.getConnection().prepareStatement("insert into " + TABLE_NAME + " values (?, ?, ?);");
				prep.setNull(1, 0);
				prep.setString(2, school.getTitle());
				prep.setString(3, school.getImage());
				res = prep.executeUpdate() == 1;
				ResultSet resKey = prep.getGeneratedKeys();
				if(resKey.next())
					school.setId(resKey.getInt("last_insert_rowid()"));
				resKey.close();
			}
			school.setExist(true);
			school.setDirty(false);
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
			stat.executeUpdate("create table " + TABLE_NAME + " (id INTEGER PRIMARY KEY AUTOINCREMENT, name, image);");
			res = false;
		} catch (Exception e) {}
		finally {
			try {
				if(stat != null) stat.close();
			} catch (Exception e) {}
		}
		return res;
	}
	
	public School[] getCached() {
		return cache;
	}

	public School[] setCached(Session session) {
		cache = getAll(session);
		return cache;
	}

	public School getCached(int id) {
		for(School school : cache)
			if(school.getId() == id)
				return school;
		return null;
	}
	
	public Element exportXML(Document document, School[] schools) {
		Element racine = null;
		try {						
			// Création de l'arborescence du DOM
			racine = document.createElement("schools");
			racine.appendChild(document.createComment("Liste des ecoles"));
			
			for(School school : schools) {
				Element schoolElem = document.createElement("school");
				schoolElem.setAttribute("id",school.getId()+"");
				schoolElem.setAttribute("image",school.getImage());
				schoolElem.setTextContent(school.getTitle());
				racine.appendChild(schoolElem);	
			}
			
		} catch (Exception e) {}
		return racine;
	}

	public School[] importXML(Element racine) {
		ArrayList<School> schools = new ArrayList<School>();
		try {
			NodeList liste = racine.getElementsByTagName("school");
			for(int idx = 0; idx < liste.getLength(); idx++) {
				Element schoolElement = (Element)liste.item(idx);
				School school = new School(
					Integer.parseInt(schoolElement.getAttribute("id")),
					schoolElement.getTextContent(),
					schoolElement.getAttribute("image")
				);

				schools.add(school);
			}
		} catch (Exception e) {}
		School[] result = new School[schools.size()];
		schools.toArray(result);
		return result;
	}

}
