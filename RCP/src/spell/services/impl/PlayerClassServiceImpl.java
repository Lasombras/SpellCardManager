package spell.services.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import spell.databases.Session;
import spell.model.PlayerClass;
import spell.model.simple.SimpleModelBox;
import spell.services.PlayerClassService;


public class PlayerClassServiceImpl implements PlayerClassService {
	public final static String TABLE_NAME = "PLAYER_CLASS";
	private PlayerClass[] cache;
	
	public PlayerClassServiceImpl() {
	}
	
	public PlayerClass get(int id, Session session) {
		PlayerClass res = null;
		try {
			Statement stat = session.getConnection().createStatement();
			ResultSet rs = stat.executeQuery("select * from " + TABLE_NAME + " where id = " + id + ";");
		    if(rs.next()) {
		    	res = new PlayerClass(rs.getInt("id"), rs.getString("name"), rs.getString("image"), rs.getString("shortname"), rs.getInt("base") == 1);
		    	res.setExist(true);
		    }
		    rs.close();
		} catch (Exception e) {}
		return res;
	}

	public boolean delete(PlayerClass playerClass, Session session) {
		boolean res = false;
		Statement stat = null;
		try {
			stat = session.getConnection().createStatement();
			res = stat.executeUpdate("delete from " + TABLE_NAME + " where id = " + playerClass.getId() + " ;") == 1;
			playerClass.setExist(false);
		} catch (Exception e) {}
		finally {
			try {
				if(stat != null) stat.close();
			} catch (Exception e) {}
		}
		return res;
	}

	public PlayerClass[] getAll(Session session) {
		ArrayList<PlayerClass> list = new ArrayList<PlayerClass>();
		try {
			Statement stat = session.getConnection().createStatement();
			ResultSet rs = stat.executeQuery("select * from " + TABLE_NAME + " order by name;");
		    while (rs.next()) {
		    	PlayerClass obj = new PlayerClass(rs.getInt("id"), rs.getString("name"), rs.getString("image"), rs.getString("shortname"), rs.getInt("base") == 1);
		    	obj.setExist(true);
		    	list.add(obj);
		    }
		    rs.close();
		} catch (Exception e) {}
		PlayerClass[] tab = new PlayerClass[list.size()];
	    list.toArray(tab);
		return tab;
	}

	public SimpleModelBox getAllInBox(Session session) {
		return new SimpleModelBox(getAll(session));
	}

	public boolean save(PlayerClass playerClass, Session session) {
		boolean res = false;
		PreparedStatement prep = null;
		try {
			if(playerClass.isExist()) {
				prep = session.getConnection().prepareStatement("update " + TABLE_NAME + " set id = ?, name = ?, image = ?, shortname = ?, base = ?);");
				prep.setInt(1, playerClass.getId());
				prep.setString(2, playerClass.getTitle());
				prep.setString(3, playerClass.getImage());
				prep.setString(4, playerClass.getShortName());
				prep.setInt(5, playerClass.isBase()?1:0);
				res = prep.executeUpdate() == 1;				
			} else {
				prep = session.getConnection().prepareStatement("insert into " + TABLE_NAME + " values (?, ?, ?, ?, ?);");
				prep.setNull(1, 0);
				prep.setString(2, playerClass.getTitle());
				prep.setString(3, playerClass.getImage());
				prep.setString(4, playerClass.getShortName());
				prep.setInt(5, playerClass.isBase()?1:0);
				res = prep.executeUpdate() == 1;
				ResultSet resKey = prep.getGeneratedKeys();
				if(resKey.next())
					playerClass.setId(resKey.getInt("last_insert_rowid()"));
				resKey.close();
			}
			playerClass.setExist(true);
			playerClass.setDirty(false);
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
			stat.executeUpdate("create table " + TABLE_NAME + " (id INTEGER PRIMARY KEY AUTOINCREMENT, name, image, shortname, base);");
			res = false;
		} catch (Exception e) {}
		finally {
			try {
				if(stat != null) stat.close();
			} catch (Exception e) {}
		}
		return res;
	}
	
	public PlayerClass[] getCached() {
		return cache;
	}
	
	public PlayerClass getCached(int id) {
		for(PlayerClass playerClass : cache)
			if(playerClass.getId() == id)
				return playerClass;
		return null;
	}

	public PlayerClass[] setCached(Session session) {
		cache = getAll(session);
		return cache;
	}
	
	public Element exportXML(Document document, PlayerClass[] playerClasses) {
		Element racine = null;
		try {						
			// Création de l'arborescence du DOM
			racine = document.createElement("PlayerClasses");
			racine.appendChild(document.createComment("Liste des classes"));
			
			for(PlayerClass playerClass : playerClasses) {
				Element playerClassElem = document.createElement("PlayerClass");
				playerClassElem.setAttribute("id",playerClass.getId()+"");
				playerClassElem.setAttribute("image",playerClass.getImage());
				playerClassElem.setAttribute("shortName",playerClass.getShortName());
				playerClassElem.setAttribute("base",(playerClass.isBase()?1:0)+"");
				playerClassElem.setTextContent(playerClass.getTitle());
				racine.appendChild(playerClassElem);	
			}
		} catch (Exception e) {}
		return racine;
	}

	public PlayerClass[] importXML(Element racine) {
		ArrayList<PlayerClass> playerClasses = new ArrayList<PlayerClass>();
		try {
			NodeList liste = racine.getElementsByTagName("PlayerClass");
			for(int idx = 0; idx < liste.getLength(); idx++) {
				Element playerClassElement = (Element)liste.item(idx);
				PlayerClass PlayerClass = new PlayerClass(
					Integer.parseInt(playerClassElement.getAttribute("id")),
					playerClassElement.getTextContent(),
					playerClassElement.getAttribute("image"),
					playerClassElement.getAttribute("shortName"),
					playerClassElement.getAttribute("base").equals("1")
				);

				playerClasses.add(PlayerClass);
			}
		} catch (Exception e) {}
		PlayerClass[] result = new PlayerClass[playerClasses.size()];
		playerClasses.toArray(result);
		return result;
	}

}
