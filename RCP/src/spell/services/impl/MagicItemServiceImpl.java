package spell.services.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import spell.databases.DatabaseManager;
import spell.databases.Session;
import spell.model.MagicItem;
import spell.model.simple.MagicItemModelBox;
import spell.search.criteria.MagicItemSearchCriteria;
import spell.search.criteria.SpellSearchCriteria;
import spell.services.MagicItemService;


public class MagicItemServiceImpl implements MagicItemService {
	public final static String TABLE_NAME = "MAGIC_ITEM";

	private MagicItem[] cache;
	
	public boolean checkTable(boolean drop, Session session) {
		boolean res = true;
		Statement stat = null;
		try {
			stat = session.getConnection().createStatement();
			if(drop) {
				stat.executeUpdate("drop table if exists " + TABLE_NAME + ";");
			}
			stat.executeUpdate("create table " + TABLE_NAME + " (id INTEGER PRIMARY KEY AUTOINCREMENT," +
															" name," +
															" originalName," +
															" detail," +
															" cardText," +
															" page," +
															" background," +
															" image," +
															" searchName," +
															" aura," +
															" casterLevel," +
															" slotID," +
															" itemTypeID," +
															" price," +
															" weight," +
															" constructionRequirements," +
															" constructionCost," +
															" sourceID);");
			
			res = false;
		} catch (Exception e) {}
		finally {
			try {
				if(stat != null) stat.close();
			} catch (Exception e) {}
		}
		return res;
	}

	public boolean delete(MagicItem magicItem, Session session) {
		boolean res = false;
		Statement stat = null;
		try {
			stat = session.getConnection().createStatement();
			res = stat.executeUpdate("delete from " + TABLE_NAME + " where id = " + magicItem.getId() + " ;") == 1;
			magicItem.setExist(false);
		} catch (Exception e) {}
		finally {
			try {
				if(stat != null) stat.close();
			} catch (Exception e) {}
		}
		return res;
	}

	public MagicItem get(int id, Session session) {
		MagicItem res = null;
		Statement stat = null;
		ResultSet rs = null;
		try {
			session.beginTransaction();
			stat = session.getConnection().createStatement();
			rs = stat.executeQuery("select * from " + TABLE_NAME + " where id = " + id + ";");
		    if(rs.next())
		    	res = build(rs, session);
		    
		} catch (Exception e) {e.printStackTrace();}
		finally {
			try{
				if(rs != null) rs.close();
				if(stat != null) stat.close();
			}catch (Exception e) {}
		}
		return res;
	}

	public MagicItem[] getAll(Session session) {
		ArrayList<MagicItem> list = new ArrayList<MagicItem>();
		Statement stat = null;
		ResultSet rs = null;
		try {
			session.beginTransaction();
			stat = session.getConnection().createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			rs = stat.executeQuery("select * from " + TABLE_NAME + " order by searchname;");
		    while (rs.next()) {
		    	list.add(build(rs, session));
		    }
		} catch (Exception e) {e.printStackTrace();}
		finally {
			try{
				if(rs != null) rs.close();
				if(stat != null) stat.close();
			}catch (Exception e) {}
		}
		MagicItem[] tab = new MagicItem[list.size()];
	    list.toArray(tab);
		return tab;
	}

	public MagicItemModelBox getAllInBox(Session session) {
		return new MagicItemModelBox(getAll(session));
	}

	public boolean save(MagicItem magicItem, Session session) {
		boolean res = false;
		PreparedStatement prep = null;
		try {
			if(magicItem.isExist()) {
				prep = session.getConnection().prepareStatement("update " + TABLE_NAME + " set " +
						" name = ?," +
						" detail = ?," +
						" cardText = ?," +
						" background = ?," +
						" image = ?," +
						" originalName = ?," +
						" page= ?," +
						" searchName = ?," +
						" aura = ?," +
						" casterLevel = ?," +
						" slotID = ?," +
						" itemTypeID = ?," +
						" price = ?," +
						" weight = ?," +
						" constructionRequirements = ?," +
						" constructionCost = ?," +
						" sourceID = ?" +
						" where id = ?;");
				prep.setString(1, magicItem.getTitle());
				prep.setString(2, magicItem.getDetail());
				prep.setString(3, magicItem.getCardText());
				prep.setString(4, magicItem.getBackground());
				prep.setString(5, magicItem.getImage());
				prep.setString(6, magicItem.getOriginalName());
				prep.setString(7, magicItem.getPage());
				prep.setString(8, DatabaseManager.getInstance().buildSearchString(magicItem.getTitle()));
				prep.setString(9, magicItem.getAura());
				prep.setString(10, magicItem.getCasterLevel());
				prep.setInt(11, magicItem.getSlotId());
				prep.setInt(12, magicItem.getItemTypeId());
				prep.setInt(13, magicItem.getPrice());
				prep.setString(14, magicItem.getWeight());
				prep.setString(15, magicItem.getConstructionRequirements());
				prep.setInt(16, magicItem.getConstructionCost());
				prep.setInt(17, magicItem.getSourceId());
				prep.setInt(18, magicItem.getId());
				res = prep.executeUpdate() == 1;				
			} else {
				prep = session.getConnection().prepareStatement("insert into " + TABLE_NAME + " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
				prep.setNull(1, 0);
				prep.setString(2, magicItem.getTitle());
				prep.setString(3, magicItem.getOriginalName());
				prep.setString(4, magicItem.getDetail());
				prep.setString(5, magicItem.getCardText());
				prep.setString(6, magicItem.getPage());
				prep.setString(7, magicItem.getBackground());
				prep.setString(8, magicItem.getImage());
				prep.setString(9, DatabaseManager.getInstance().buildSearchString(magicItem.getTitle()));
				prep.setString(10, magicItem.getAura());
				prep.setString(11, magicItem.getCasterLevel());
				prep.setInt(12, magicItem.getSlotId());
				prep.setInt(13, magicItem.getItemTypeId());
				prep.setInt(14, magicItem.getPrice());
				prep.setString(15, magicItem.getWeight());
				prep.setString(16, magicItem.getConstructionRequirements());
				prep.setInt(17, magicItem.getConstructionCost());
				prep.setInt(18, magicItem.getSourceId());
				res = prep.executeUpdate() == 1;
				ResultSet resKey = prep.getGeneratedKeys();
				if(resKey.next())
					magicItem.setId(resKey.getInt("last_insert_rowid()"));
				resKey.close();
			}
			prep.close();
			magicItem.setExist(true);
			magicItem.setDirty(false);
		} catch (Exception e) {e.printStackTrace();}
		finally {
			try {
				if(prep != null) prep.close();
			} catch (Exception e) {}
		}
		return res;
	}

	private MagicItem build(ResultSet rs, Session session) throws SQLException {
		MagicItem res = new MagicItem(rs.getInt("id"));
    	res.setTitle(rs.getString("name"));
     	res.setDetail(rs.getString("detail"));
     	res.setBackground(rs.getString("background"));
    	res.setImage(rs.getString("image"));
    	res.setOriginalName(rs.getString("originalName"));
    	res.setPage(rs.getString("page"));
    	res.setCardText(rs.getString("cardText"));
    	res.setAura(rs.getString("aura"));
    	res.setCasterLevel(rs.getString("casterLevel"));
    	res.setSlotId(rs.getInt("slotID"));
    	res.setItemTypeId(rs.getInt("itemTypeID"));
       	res.setPrice(rs.getInt("price"));
       	res.setWeight(rs.getString("weight"));
      	res.setConstructionRequirements(rs.getString("constructionRequirements"));
     	res.setConstructionCost(rs.getInt("constructionCost"));
     	res.setSourceId(rs.getInt("sourceID"));
		res.setExist(true);	
    	return res;
	}
	
	public MagicItem[] search(Session session, MagicItemSearchCriteria criteria) {
		ArrayList<MagicItem> list = new ArrayList<MagicItem>();
		Statement stat = null;
		ResultSet rs = null;
		try {
			int nbCriteria = 0;
			StringBuffer sql = new StringBuffer();
			if(criteria.getName() != null && criteria.getName().length() > 0) {
				nbCriteria++;
				sql.append((nbCriteria==1?"":" AND") + " SEARCHNAME LIKE '%" + DatabaseManager.getInstance().buildSearchString(criteria.getName().replaceAll("'", "''").replace('*', '%')) + "%'");
			}
			if(criteria.getOriginalName() != null && criteria.getOriginalName().length() > 0) {
				nbCriteria++;
				sql.append((nbCriteria==1?"":" AND") + " ORIGINALNAME LIKE '%" + criteria.getOriginalName().replaceAll("'", "''").replace('*', '%') + "%'");
			}
			if(criteria.getItemTypeId() > -1) {
				nbCriteria++;
				sql.append((nbCriteria==1?"":" AND") + " ITEMTYPEID = " + criteria.getItemTypeId());
			}
			if(criteria.getSlotId() > -1) {
				nbCriteria++;
				sql.append((nbCriteria==1?"":" AND") + " SLOTID = " + criteria.getSlotId());
			}
			if(criteria.getSourceId() > -1) {
				nbCriteria++;
				sql.append((nbCriteria==1?"":" AND") + " SOURCEID = " + criteria.getSourceId());
			}
			if(criteria.getPrice() > 0) {
				String sqlTemp = "";
				switch (criteria.getPriceSign()) {
					case SpellSearchCriteria.EQUAL : sqlTemp +="=";break;
					case SpellSearchCriteria.GREATHER_THAN : sqlTemp +=">=";break;
					case SpellSearchCriteria.LEATHER_THAN : sqlTemp +="<=";break;
					case SpellSearchCriteria.NOT_EQUAL : sqlTemp +="<>";break;
					default : sqlTemp +="=";break;
				}
				sqlTemp +=" " + criteria.getPrice();
				nbCriteria++;
				sql.append((nbCriteria==1?"":" AND") + " PRICE " + sqlTemp);
			}

			session.beginTransaction();
			stat = session.getConnection().createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			String query = "SELECT * FROM " + TABLE_NAME;
			if(nbCriteria > 0) {
				query += " WHERE " + sql.toString();
			}
			query += " ORDER BY SEARCHNAME;";
			rs = stat.executeQuery(query);
		    while (rs.next()) {
		    	list.add(build(rs, session));
		    }
		} catch (Exception e) {e.printStackTrace();}
		finally {
			try{
				if(rs != null) rs.close();
				if(stat != null) stat.close();
			}catch (Exception e) {}
		}
		MagicItem[] tab = new MagicItem[list.size()];
	    list.toArray(tab);
		return tab;
	}
	
	public MagicItem[] getCached() {
		return cache;
	}

	public MagicItem[] setCached(Session session) {
		cache = getAll(session);
		return cache;
	}
	
	public Element exportXML(Document document, MagicItem[] magicItems, boolean isAndroidExport) {
		Element racine = null;
		try {						
			// Création de l'arborescence du DOM
			racine = document.createElement("magicItems");
			racine.appendChild(document.createComment("Liste des objets magiques"));
			
			for(MagicItem magicItem : magicItems) {
				Element magicItemElem = document.createElement("magicItem");
				magicItemElem.setAttribute("id",magicItem.getId()+"");
				racine.appendChild(magicItemElem);

				Element originalName = document.createElement("originalName");
				originalName.setTextContent(magicItem.getOriginalName());
				magicItemElem.appendChild(originalName);

				Element title = document.createElement("title");
				title.setTextContent(magicItem.getTitle());
				magicItemElem.appendChild(title);

				Element casterLevel = document.createElement("casterLevel");
				casterLevel.setTextContent(magicItem.getCasterLevel());
				magicItemElem.appendChild(casterLevel);
	
				Element background = document.createElement("background");
				background.setTextContent(magicItem.getBackground());
				magicItemElem.appendChild(background);
	
				Element cardText = document.createElement("cardText");
				cardText.setTextContent(magicItem.getCardText());
				magicItemElem.appendChild(cardText);
	
				Element aura = document.createElement("aura");
				aura.setTextContent(magicItem.getAura());
				magicItemElem.appendChild(aura);
	
				Element detail = document.createElement("detail");
				detail.setTextContent(magicItem.getDetail());
				magicItemElem.appendChild(detail);
				
				Element image = document.createElement("image");
				image.setTextContent(magicItem.getImage());
				magicItemElem.appendChild(image);
	
				Element page = document.createElement("page");
				page.setTextContent(magicItem.getPage());
				magicItemElem.appendChild(page);
				
				Element slotID = document.createElement("slotID");
				slotID.setTextContent(magicItem.getSlotId()+"");
				magicItemElem.appendChild(slotID);

				Element itemTypeID = document.createElement("itemTypeID");
				itemTypeID.setTextContent(magicItem.getItemTypeId()+"");
				magicItemElem.appendChild(itemTypeID);

				Element price = document.createElement("price");
				price.setTextContent(magicItem.getPrice()+"");
				magicItemElem.appendChild(price);

				Element weight = document.createElement("weight");
				weight.setTextContent(magicItem.getWeight());
				magicItemElem.appendChild(weight);

				Element sourceID = document.createElement("sourceID");
				sourceID.setTextContent(magicItem.getSourceId()+"");
				magicItemElem.appendChild(sourceID);

				Element constructionRequirements = document.createElement("constructionRequirements");
				constructionRequirements.setTextContent(magicItem.getConstructionRequirements());
				magicItemElem.appendChild(constructionRequirements);

				Element constructionCost = document.createElement("constructionCost");
				constructionCost.setTextContent(magicItem.getConstructionCost()+"");
				magicItemElem.appendChild(constructionCost);
		     	
			}
			
		} catch (Exception e) {}
		return racine;
	}

	public MagicItem[] importXML(Element racine) {
		ArrayList<MagicItem> magicItems = new ArrayList<MagicItem>();
		try {
			NodeList liste = racine.getElementsByTagName("magicItem");
			for(int idxSpell = 0; idxSpell < liste.getLength(); idxSpell++) {
				Element magicItemElement = (Element)liste.item(idxSpell);
				MagicItem magicItem = new MagicItem(Integer.parseInt(magicItemElement.getAttribute("id")));
				
				Element elem = (Element)magicItemElement.getElementsByTagName("originalName").item(0);
				magicItem.setOriginalName(elem.getTextContent());
				elem = (Element)magicItemElement.getElementsByTagName("title").item(0);
				magicItem.setTitle(elem.getTextContent());
				elem = (Element)magicItemElement.getElementsByTagName("casterLevel").item(0);
				magicItem.setCasterLevel(elem.getTextContent());
				elem = (Element)magicItemElement.getElementsByTagName("background").item(0);
				magicItem.setBackground(elem.getTextContent());	
				elem = (Element)magicItemElement.getElementsByTagName("cardText").item(0);
				magicItem.setCardText(elem.getTextContent());
				elem = (Element)magicItemElement.getElementsByTagName("aura").item(0);
				magicItem.setAura(elem.getTextContent());
				elem = (Element)magicItemElement.getElementsByTagName("detail").item(0);
				magicItem.setDetail(elem.getTextContent());
				elem = (Element)magicItemElement.getElementsByTagName("image").item(0);
				magicItem.setImage(elem.getTextContent());
				elem = (Element)magicItemElement.getElementsByTagName("page").item(0);
				magicItem.setPage(elem.getTextContent());
				elem = (Element)magicItemElement.getElementsByTagName("slotID").item(0);
				magicItem.setSlotId(Integer.parseInt(elem.getTextContent()));
				elem = (Element)magicItemElement.getElementsByTagName("itemTypeID").item(0);
				magicItem.setItemTypeId(Integer.parseInt(elem.getTextContent()));
				elem = (Element)magicItemElement.getElementsByTagName("price").item(0);
				magicItem.setPrice(Integer.parseInt(elem.getTextContent()));
				elem = (Element)magicItemElement.getElementsByTagName("weight").item(0);
				magicItem.setWeight(elem.getTextContent());
				elem = (Element)magicItemElement.getElementsByTagName("constructionRequirements").item(0);
				magicItem.setConstructionRequirements(elem.getTextContent());
				elem = (Element)magicItemElement.getElementsByTagName("constructionCost").item(0);
				magicItem.setConstructionCost(Integer.parseInt(elem.getTextContent()));
				magicItem.setSourceId(5);
				elem = (Element)magicItemElement.getElementsByTagName("sourceID").item(0);
				if(elem != null && elem.getTextContent() != null && !elem.getTextContent().equals(""))
					magicItem.setSourceId(Integer.parseInt(elem.getTextContent()));
				
				magicItems.add(magicItem);
			}
		} catch (Exception e) {}
		MagicItem[] result = new MagicItem[magicItems.size()];
		magicItems.toArray(result);
		return result;
	}

}