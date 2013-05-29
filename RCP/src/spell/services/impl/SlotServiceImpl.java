package spell.services.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import spell.databases.Session;
import spell.model.Slot;
import spell.model.simple.SimpleModelBox;
import spell.services.SlotService;


public class SlotServiceImpl implements SlotService {
	public final static String TABLE_NAME = "SLOT";
	private Slot[] cache;
	
	public SlotServiceImpl() {
	}
	
	public Slot get(int id, Session session) {
		Slot res = null;
		try {
			Statement stat = session.getConnection().createStatement();
			ResultSet rs = stat.executeQuery("select * from " + TABLE_NAME + " where id = " + id + ";");
		    if(rs.next()) {
		    	res = new Slot(rs.getInt("id"), rs.getString("name"), rs.getString("image"));
		    	res.setExist(true);
		    }
		    rs.close();
		} catch (Exception e) {}
		return res;
	}

	public boolean delete(Slot slot, Session session) {
		boolean res = false;
		Statement stat = null;
		try {
			stat = session.getConnection().createStatement();
			res = stat.executeUpdate("delete from " + TABLE_NAME + " where id = " + slot.getId() + " ;") == 1;
			slot.setExist(false);
		} catch (Exception e) {}
		finally {
			try {
				if(stat != null) stat.close();
			} catch (Exception e) {}
		}
		return res;
	}

	public Slot[] getAll(Session session) {
		ArrayList<Slot> list = new ArrayList<Slot>();
		try {
			Statement stat = session.getConnection().createStatement();
			ResultSet rs = stat.executeQuery("select * from " + TABLE_NAME + " order by name;");
		    while (rs.next()) {
		    	Slot obj = new Slot(rs.getInt("id"), rs.getString("name"), rs.getString("image"));
		    	obj.setExist(true);
		    	list.add(obj);
		    }
		    rs.close();
		} catch (Exception e) {}
		Slot[] tab = new Slot[list.size()];
	    list.toArray(tab);
		return tab;
	}

	public SimpleModelBox getAllInBox(Session session) {
		return new SimpleModelBox(getAll(session));
	}

	public boolean save(Slot slot, Session session) {
		boolean res = false;
		PreparedStatement prep = null;
		try {
			if(slot.isExist()) {
				prep = session.getConnection().prepareStatement("update " + TABLE_NAME + " set id = ?, name = ?, image = ?);");
				prep.setInt(1, slot.getId());
				prep.setString(2, slot.getTitle());
				prep.setString(3, slot.getImage());
				res = prep.executeUpdate() == 1;				
			} else {
				prep = session.getConnection().prepareStatement("insert into " + TABLE_NAME + " values (?, ?, ?);");
				prep.setNull(1, 0);
				prep.setString(2, slot.getTitle());
				prep.setString(3, slot.getImage());
				res = prep.executeUpdate() == 1;
				ResultSet resKey = prep.getGeneratedKeys();
				if(resKey.next())
					slot.setId(resKey.getInt("last_insert_rowid()"));
				resKey.close();
			}
			slot.setExist(true);
			slot.setDirty(false);
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
	
	public Slot[] getCached() {
		return cache;
	}

	public Slot[] setCached(Session session) {
		cache = getAll(session);
		return cache;
	}
	
	public Slot getCached(int id) {
		for(Slot slot : cache)
			if(slot.getId() == id)
				return slot;
		return null;
	}
	
	public Element exportXML(Document document, Slot[] slots) {
		Element racine = null;
		try {						
			// Création de l'arborescence du DOM
			racine = document.createElement("Slots");
			racine.appendChild(document.createComment("Liste des slots"));
			
			for(Slot slot : slots) {
				Element slotElem = document.createElement("Slot");
				slotElem.setAttribute("id",slot.getId()+"");
				slotElem.setAttribute("image",slot.getImage());
				slotElem.setTextContent(slot.getTitle());
				racine.appendChild(slotElem);	
			}
		} catch (Exception e) {}
		return racine;
	}

	public Slot[] importXML(Element racine) {
		ArrayList<Slot> slots = new ArrayList<Slot>();
		try {
			NodeList liste = racine.getElementsByTagName("Slot");
			for(int idx = 0; idx < liste.getLength(); idx++) {
				Element slotElement = (Element)liste.item(idx);
				Slot Slot = new Slot(
					Integer.parseInt(slotElement.getAttribute("id")),
					slotElement.getTextContent(),
					slotElement.getAttribute("image")
				);

				slots.add(Slot);
			}
		} catch (Exception e) {}
		Slot[] result = new Slot[slots.size()];
		slots.toArray(result);
		return result;
	}


}
