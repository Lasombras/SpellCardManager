package spell.services.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import spell.databases.Session;
import spell.model.ItemType;
import spell.model.simple.SimpleModelBox;
import spell.services.ItemTypeService;


public class ItemTypeServiceImpl implements ItemTypeService {
	public final static String TABLE_NAME = "ITEM_TYPE";
	private ItemType[] cache;
	
	public ItemTypeServiceImpl() {
	}
	
	public ItemType get(int id, Session session) {
		ItemType res = null;
		try {
			Statement stat = session.getConnection().createStatement();
			ResultSet rs = stat.executeQuery("select * from " + TABLE_NAME + " where id = " + id + ";");
		    if(rs.next()) {
		    	res = new ItemType(rs.getInt("id"), rs.getString("name"), rs.getString("image"));
		    	res.setExist(true);
		    }
		    rs.close();
		} catch (Exception e) {}
		return res;
	}

	public boolean delete(ItemType itemType, Session session) {
		boolean res = false;
		Statement stat = null;
		try {
			stat = session.getConnection().createStatement();
			res = stat.executeUpdate("delete from " + TABLE_NAME + " where id = " + itemType.getId() + " ;") == 1;
			itemType.setExist(false);
		} catch (Exception e) {}
		finally {
			try {
				if(stat != null) stat.close();
			} catch (Exception e) {}
		}
		return res;
	}

	public ItemType[] getAll(Session session) {
		ArrayList<ItemType> list = new ArrayList<ItemType>();
		try {
			Statement stat = session.getConnection().createStatement();
			ResultSet rs = stat.executeQuery("select * from " + TABLE_NAME + " order by name;");
		    while (rs.next()) {
		    	ItemType obj = new ItemType(rs.getInt("id"), rs.getString("name"), rs.getString("image"));
		    	obj.setExist(true);
		    	list.add(obj);
		    }
		    rs.close();
		} catch (Exception e) {}
		ItemType[] tab = new ItemType[list.size()];
	    list.toArray(tab);
		return tab;
	}

	public SimpleModelBox getAllInBox(Session session) {
		return new SimpleModelBox(getAll(session));
	}

	public boolean save(ItemType itemType, Session session) {
		boolean res = false;
		PreparedStatement prep = null;
		try {
			if(itemType.isExist()) {
				prep = session.getConnection().prepareStatement("update " + TABLE_NAME + " set id = ?, name = ?, image = ?);");
				prep.setInt(1, itemType.getId());
				prep.setString(2, itemType.getTitle());
				prep.setString(3, itemType.getImage());
				res = prep.executeUpdate() == 1;				
			} else {
				prep = session.getConnection().prepareStatement("insert into " + TABLE_NAME + " values (?, ?, ?);");
				prep.setNull(1, 0);
				prep.setString(2, itemType.getTitle());
				prep.setString(3, itemType.getImage());
				res = prep.executeUpdate() == 1;
				ResultSet resKey = prep.getGeneratedKeys();
				if(resKey.next())
					itemType.setId(resKey.getInt("last_insert_rowid()"));
				resKey.close();
			}
			itemType.setExist(true);
			itemType.setDirty(false);
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
	
	public ItemType[] getCached() {
		return cache;
	}

	public ItemType[] setCached(Session session) {
		cache = getAll(session);
		return cache;
	}
	
	public ItemType getCached(int id) {
		for(ItemType itemType : cache)
			if(itemType.getId() == id)
				return itemType;
		return null;
	}
	
	public Element exportXML(Document document, ItemType[] itemTypes) {
		Element racine = null;
		try {						
			// Création de l'arborescence du DOM
			racine = document.createElement("ItemTypes");
			racine.appendChild(document.createComment("Liste des itemTypes"));
			
			for(ItemType itemType : itemTypes) {
				Element itemTypeElem = document.createElement("ItemType");
				itemTypeElem.setAttribute("id",itemType.getId()+"");
				itemTypeElem.setAttribute("image",itemType.getImage());
				itemTypeElem.setTextContent(itemType.getTitle());
				racine.appendChild(itemTypeElem);	
			}
		} catch (Exception e) {}
		return racine;
	}

	public ItemType[] importXML(Element racine) {
		ArrayList<ItemType> itemTypes = new ArrayList<ItemType>();
		try {
			NodeList liste = racine.getElementsByTagName("ItemType");
			for(int idx = 0; idx < liste.getLength(); idx++) {
				Element itemTypeElement = (Element)liste.item(idx);
				ItemType ItemType = new ItemType(
					Integer.parseInt(itemTypeElement.getAttribute("id")),
					itemTypeElement.getTextContent(),
					itemTypeElement.getAttribute("image")
				);

				itemTypes.add(ItemType);
			}
		} catch (Exception e) {}
		ItemType[] result = new ItemType[itemTypes.size()];
		itemTypes.toArray(result);
		return result;
	}


}
