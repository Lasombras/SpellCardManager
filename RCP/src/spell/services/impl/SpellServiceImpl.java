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
import spell.model.Level;
import spell.model.Spell;
import spell.model.simple.SpellModelBox;
import spell.search.criteria.SpellSearchCriteria;
import spell.services.SpellService;


public class SpellServiceImpl implements SpellService {
	public final static String TABLE_NAME = "SPELL";
	public final static String TABLE_LEVEL_NAME = "SPELL_LEVEL";
	public final static String TABLE_COMPONENT_NAME = "SPELL_COMPONENT";

	private Spell[] cache;
	
	public boolean checkTable(boolean drop, Session session) {
		boolean res = true;
		Statement stat = null;
		try {
			stat = session.getConnection().createStatement();
			if(drop) {
				stat.executeUpdate("drop table if exists " + TABLE_LEVEL_NAME + ";");
				stat.executeUpdate("drop table if exists " + TABLE_COMPONENT_NAME + ";");
				stat.executeUpdate("drop table if exists " + TABLE_NAME + ";");
			}
			stat.executeUpdate("create table " + TABLE_NAME + " (id INTEGER PRIMARY KEY AUTOINCREMENT, name, area, castingTime, descriptor, detail, duration, effect, material, range, savingThrow, id_school, spellResistance, background, target, image, originalName, page, cardtext, searchname, sourceID);");
			stat.executeUpdate("create table " + TABLE_COMPONENT_NAME + " (id_spell , id_component);"); 
			stat.executeUpdate("create table " + TABLE_LEVEL_NAME + " (id_spell , id_player_class, level);");
			
			res = false;
		} catch (Exception e) {}
		finally {
			try {
				if(stat != null) stat.close();
			} catch (Exception e) {}
		}
		return res;
	}

	public boolean delete(Spell spell, Session session) {
		boolean res = false;
		Statement stat = null;
		try {
			stat = session.getConnection().createStatement();
			stat.executeUpdate("delete from " + TABLE_LEVEL_NAME + " where id_spell = " + spell.getId() + " ;");
			stat.executeUpdate("delete from " + TABLE_COMPONENT_NAME + " where id_spell = " + spell.getId() + " ;");
			res = stat.executeUpdate("delete from " + TABLE_NAME + " where id = " + spell.getId() + " ;") == 1;
			spell.setExist(false);
		} catch (Exception e) {}
		finally {
			try {
				if(stat != null) stat.close();
			} catch (Exception e) {}
		}
		return res;
	}

	public Spell get(int id, Session session) {
		Spell res = null;
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

	public Spell[] getAll(Session session) {
		ArrayList<Spell> list = new ArrayList<Spell>();
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
		Spell[] tab = new Spell[list.size()];
	    list.toArray(tab);
		return tab;
	}
	
	private void alimLevels(Spell spell, Session session) throws SQLException {
		Statement stat = null;
		ResultSet rs = null;
		try  {
			stat = session.getConnection().createStatement();
	    	rs = stat.executeQuery("SELECT * FROM " + TABLE_LEVEL_NAME + " WHERE ID_SPELL = " + spell.getId() + ";");
	    	while(rs.next()) {
	    		spell.addLevel(rs.getInt("ID_PLAYER_CLASS"), rs.getInt("LEVEL"));
	    	}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if(rs != null) rs.close();
			if(stat != null) stat.close();
		}
	}
	
	private void saveLevels(Spell spell, Session session) throws SQLException {
		Statement stat = null;
		PreparedStatement prep = null;
		try {
			stat = session.getConnection().createStatement();
			stat.executeUpdate("delete from " + TABLE_LEVEL_NAME + " where id_spell = " + spell.getId() + " ;");
			ArrayList<Level> levels = spell.getLevels();
			prep = session.getConnection().prepareStatement("insert into " + TABLE_LEVEL_NAME + " values (?, ?, ?);");
			for(Level level : levels) {
				prep.setInt(1, spell.getId());
				prep.setInt(2, level.getPlayerClassId());
				prep.setInt(3, level.getLevel());
				prep.addBatch();
			}
			prep.executeBatch();
		} catch (SQLException e) {
			throw e;
		} finally {
			if(stat != null) stat.close();
			if(prep != null) prep.close();
		}

	}

	private void alimComponents(Spell spell, Session session) throws SQLException {
		Statement stat = null;
		ResultSet rs = null;
		try  {
			stat = session.getConnection().createStatement();
	    	rs = stat.executeQuery("SELECT * FROM " + TABLE_COMPONENT_NAME + " WHERE ID_SPELL = " + spell.getId() + ";");
	    	while(rs.next()) {
	    		spell.addComponentId(rs.getInt("ID_COMPONENT"));
	    	}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if(rs != null) rs.close();
			if(stat != null) stat.close();
		}
	}
	
	private void saveComponents(Spell spell, Session session) throws SQLException {
		Statement stat = null;
		PreparedStatement prep = null;
		try {
			stat = session.getConnection().createStatement();
			stat.executeUpdate("delete from " + TABLE_COMPONENT_NAME + " where id_spell = " + spell.getId() + " ;");
			ArrayList<Integer> components = spell.getComponentsId();
			prep = session.getConnection().prepareStatement("insert into " + TABLE_COMPONENT_NAME + " values (?, ?);");
			for(Integer id : components) {
				prep.setInt(1, spell.getId());
				prep.setInt(2, id.intValue());
				prep.addBatch();
			}
			prep.executeBatch();
		} catch (SQLException e) {
			throw e;
		} finally {
			if(stat != null) stat.close();
			if(prep != null) prep.close();
		}

	}

	public SpellModelBox getAllInBox(Session session) {
		return new SpellModelBox(getAll(session));
	}

	public boolean save(Spell spell, Session session) {
		boolean res = false;
		PreparedStatement prep = null;
		try {
			if(spell.isExist()) {
				prep = session.getConnection().prepareStatement("update " + TABLE_NAME + " set " +
						" name = ?, area = ?, castingTime = ?, descriptor = ?, detail = ?, duration = ?, effect = ?, material = ?, " +
						" range = ?, savingThrow = ?, id_school = ?, spellResistance = ?, background = ?, target = ?, image = ?, originalName = ?," +
						" page = ?, cardtext = ?, searchname = ?, sourceID = ? " +
						" where id = ?;");
				prep.setString(1, spell.getTitle());
				prep.setString(2, spell.getArea());
				prep.setString(3, spell.getCastingTime());
				prep.setString(4, spell.getDescriptor());
				prep.setString(5, spell.getDetail());
				prep.setString(6, spell.getDuration());
				prep.setString(7, spell.getEffect());
				prep.setString(8, spell.getMaterial());
				prep.setString(9, spell.getRange());				
				prep.setString(10, spell.getSavingThrow());
				prep.setInt(11, spell.getSchoolId());
				prep.setBoolean(12, spell.isSpellResistance());
				prep.setString(13, spell.getBackground());
				prep.setString(14, spell.getTarget());
				prep.setString(15, spell.getImage());
				prep.setString(16, spell.getOriginalName());
				prep.setString(17, spell.getPage());
				prep.setString(18, spell.getCardText());
				prep.setString(19, DatabaseManager.getInstance().buildSearchString(spell.getTitle()));
				prep.setInt(20, spell.getSourceId());
				prep.setInt(21, spell.getId());
				res = prep.executeUpdate() == 1;				
			} else {
				prep = session.getConnection().prepareStatement("insert into " + TABLE_NAME + " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
				prep.setNull(1, 0);
				prep.setString(2, spell.getTitle());
				prep.setString(3, spell.getArea());
				prep.setString(4, spell.getCastingTime());
				prep.setString(5, spell.getDescriptor());
				prep.setString(6, spell.getDetail());
				prep.setString(7, spell.getDuration());
				prep.setString(8, spell.getEffect());
				prep.setString(9, spell.getMaterial());
				prep.setString(10, spell.getRange());				
				prep.setString(11, spell.getSavingThrow());
				prep.setInt(12, spell.getSchoolId());
				prep.setBoolean(13, spell.isSpellResistance());
				prep.setString(14, spell.getBackground());
				prep.setString(15, spell.getTarget());
				prep.setString(16, spell.getImage());
				prep.setString(17, spell.getOriginalName());
				prep.setString(18, spell.getPage());
				prep.setString(19, spell.getCardText());
				prep.setString(20, DatabaseManager.getInstance().buildSearchString(spell.getTitle()));
				prep.setInt(21, spell.getSourceId());
				res = prep.executeUpdate() == 1;
				ResultSet resKey = prep.getGeneratedKeys();
				if(resKey.next())
					spell.setId(resKey.getInt("last_insert_rowid()"));
				resKey.close();
			}
			prep.close();
			saveLevels(spell, session);
			saveComponents(spell, session);
			spell.setExist(true);
			spell.setDirty(false);
		} catch (Exception e) {e.printStackTrace();}
		finally {
			try {
				if(prep != null) prep.close();
			} catch (Exception e) {}
		}
		return res;
	}

	private Spell build(ResultSet rs, Session session) throws SQLException {
		Spell res = new Spell(rs.getInt("id"));
    	res.setTitle(rs.getString("name"));
    	res.setArea(rs.getString("area"));
    	res.setCastingTime(rs.getString("castingTime"));
    	res.setDescriptor(rs.getString("descriptor"));
    	res.setDetail(rs.getString("detail"));
    	res.setDuration(rs.getString("duration"));
    	res.setEffect(rs.getString("effect"));
    	res.setMaterial(rs.getString("material"));
    	res.setRange(rs.getString("range"));
    	res.setSavingThrow(rs.getString("savingThrow"));
    	res.setSchoolId(rs.getInt("id_school"));
    	res.setSpellResistance(rs.getBoolean("spellResistance"));
    	res.setBackground(rs.getString("background"));
    	res.setTarget(rs.getString("target"));
    	res.setImage(rs.getString("image"));
    	res.setOriginalName(rs.getString("originalName"));
    	res.setPage(rs.getString("page"));
    	res.setCardText(rs.getString("cardtext"));
    	res.setSourceId(rs.getInt("sourceID"));
    	res.setExist(true);	
    	alimLevels(res, session);
    	alimComponents(res, session);
    	return res;
	}
	
	public Spell[] search(Session session, SpellSearchCriteria criteria) {
		ArrayList<Spell> list = new ArrayList<Spell>();
		Statement stat = null;
		ResultSet rs = null;
		try {
			int nbCriteria = 0;
			StringBuffer sql = new StringBuffer();
			ArrayList<String> sqlLevels = new ArrayList<String>();
			if(criteria.getName() != null && criteria.getName().length() > 0) {
				nbCriteria++;
				sql.append((nbCriteria==1?"":" AND") + " SEARCHNAME LIKE '%" + DatabaseManager.getInstance().buildSearchString(criteria.getName().replaceAll("'", "''").replace('*', '%')) + "%'");
			}
			if(criteria.getOriginalName() != null && criteria.getOriginalName().length() > 0) {
				nbCriteria++;
				sql.append((nbCriteria==1?"":" AND") + " ORIGINALNAME LIKE '%" + criteria.getOriginalName().replaceAll("'", "''").replace('*', '%') + "%'");
			}
			if(criteria.getLevel() > -1) {
				String sqlTemp = "";
				sqlTemp += TABLE_LEVEL_NAME +  ".LEVEL ";
				switch (criteria.getLevelSign()) {
					case SpellSearchCriteria.EQUAL : sqlTemp +="=";break;
					case SpellSearchCriteria.GREATHER_THAN : sqlTemp +=">=";break;
					case SpellSearchCriteria.LEATHER_THAN : sqlTemp +="<=";break;
					case SpellSearchCriteria.NOT_EQUAL : sqlTemp +="<>";break;
					default : sqlTemp +="=";break;
				}
				sqlTemp +=" " + criteria.getLevel();
				sqlLevels.add(sqlTemp);
			}
			if(criteria.getPlayerClassId() > -1) {
				sqlLevels.add(TABLE_LEVEL_NAME +  ".ID_PLAYER_CLASS  = " +criteria.getPlayerClassId());
			}
			
			if(criteria.getSchoolId() > -1) {
				nbCriteria++;
				sql.append((nbCriteria==1?"":" AND") + " ID_SCHOOL = " + criteria.getSchoolId());
			}

			if(criteria.getSourceId() > -1) {
				nbCriteria++;
				sql.append((nbCriteria==1?"":" AND") + " SOURCEID = " + criteria.getSourceId());
			}

			//BUILD DU SELECT DE LA SOUS TABLE LEVEL
			if(sqlLevels.size() > 0) {
				nbCriteria++;
				sql.append((nbCriteria==1?"":" AND") + " (SELECT COUNT(*) FROM " + TABLE_LEVEL_NAME +  " WHERE ");
				boolean firstSQLLevel = true;
				for(String sqlTemp : sqlLevels) {
					if(!firstSQLLevel) sql.append(" AND ");
					sql.append(sqlTemp);
					firstSQLLevel = false;
				}
				sql.append(" AND " + TABLE_LEVEL_NAME +  ".ID_SPELL = ID) > 0");
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
		Spell[] tab = new Spell[list.size()];
	    list.toArray(tab);
		return tab;
	}
	
	public Spell[] getCached() {
		return cache;
	}

	public Spell[] setCached(Session session) {
		cache = getAll(session);
		return cache;
	}
	
	public Element exportXML(Document document, Spell[] spells, boolean isAndroidExport) {
		Element racine = null;
		try {						
			// Création de l'arborescence du DOM
			racine = document.createElement("spells");
			racine.setAttribute("count", ""+spells.length);
			racine.appendChild(document.createComment("Liste des sorts"));
			
			for(Spell spell : spells) {
				Element spellElem = document.createElement("spell");
				spellElem.setAttribute("id",spell.getId()+"");
				racine.appendChild(spellElem);

				Element originalName = document.createElement("originalName");
				originalName.setTextContent(spell.getOriginalName());
				spellElem.appendChild(originalName);

				Element title = document.createElement("title");
				title.setTextContent(spell.getTitle());
				spellElem.appendChild(title);

				Element area = document.createElement("area");
				area.setTextContent(spell.getArea());
				spellElem.appendChild(area);
	
				if(!isAndroidExport) {
					Element background = document.createElement("background");
					background.setTextContent(spell.getBackground());
					spellElem.appendChild(background);
		
					Element cardText = document.createElement("cardText");
					cardText.setTextContent(spell.getCardText());
					spellElem.appendChild(cardText);

					Element detail = document.createElement("detail");
					detail.setTextContent(spell.getDetail());
					spellElem.appendChild(detail);
				} else {
					//Format Detail
					Element detail = document.createElement("detail");
					String str = spell.getDetail();
					while(str.indexOf("<a target") > -1) {
						String strStart = str.substring(0,str.indexOf("<a target"));
						String strEnd = str.substring(str.indexOf("<a target"));
						strEnd = strEnd.substring(strEnd.indexOf(">")+1);
						strEnd = strEnd.replaceFirst("</a>", "");
						str = strStart + strEnd;
					}
					detail.setTextContent(str);
					spellElem.appendChild(detail);					
				}
				
				Element castingTime = document.createElement("castingTime");
				castingTime.setTextContent(spell.getCastingTime());
				spellElem.appendChild(castingTime);
	
				Element descriptor = document.createElement("descriptor");
				descriptor.setTextContent(spell.getDescriptor());
				spellElem.appendChild(descriptor);
		
				Element duration = document.createElement("duration");
				duration.setTextContent(spell.getDuration());
				spellElem.appendChild(duration);
	
				Element effect = document.createElement("effect");
				effect.setTextContent(spell.getEffect());
				spellElem.appendChild(effect);
				
				Element image = document.createElement("image");
				image.setTextContent(spell.getImage());
				spellElem.appendChild(image);
	
				Element material = document.createElement("material");
				material.setTextContent(spell.getMaterial());
				spellElem.appendChild(material);
	
				Element page = document.createElement("page");
				page.setTextContent(spell.getPage());
				spellElem.appendChild(page);
				
				Element range = document.createElement("range");
				range.setTextContent(spell.getRange());
				spellElem.appendChild(range);
				
				Element savingThrow = document.createElement("savingThrow");
				savingThrow.setTextContent(spell.getSavingThrow());
				spellElem.appendChild(savingThrow);
	
				Element target = document.createElement("target");
				target.setTextContent(spell.getTarget());
				spellElem.appendChild(target);
				
				Element spellResistance = document.createElement("spellResistance");
				spellResistance.setTextContent(spell.isSpellResistance()?"yes":"no");
				spellElem.appendChild(spellResistance);
				
				Element school = document.createElement("school");
				school.setTextContent(spell.getSchoolId()+"");
				spellElem.appendChild(school);
	
				Element sourceID = document.createElement("source");
				sourceID.setTextContent(spell.getSourceId()+"");
				spellElem.appendChild(sourceID);

				Element componentsElement = document.createElement("components");		
				for(Integer id : spell.getComponentsId()) {
					Element component = document.createElement("component");
					component.setTextContent(id.toString()+"");
					componentsElement.appendChild(component);				
				}
				spellElem.appendChild(componentsElement);
	
				Element levels = document.createElement("levels");		
				for(Level level : spell.getLevels()) {
					Element levelElem = document.createElement("level");
					levelElem.setAttribute("class", level.getPlayerClassId()+"");
					levelElem.setTextContent(level.getLevel()+"");
					levels.appendChild(levelElem);				
				}
				spellElem.appendChild(levels);
			}
			
		} catch (Exception e) {}
		return racine;
	}

	public Spell[] importXML(Element racine) {
		ArrayList<Spell> spells = new ArrayList<Spell>();
		try {
			NodeList liste = racine.getElementsByTagName("spell");
			for(int idxSpell = 0; idxSpell < liste.getLength(); idxSpell++) {
				Element spellElement = (Element)liste.item(idxSpell);
				Spell spell = new Spell(Integer.parseInt(spellElement.getAttribute("id")));
				
				Element elem = (Element)spellElement.getElementsByTagName("originalName").item(0);
				spell.setOriginalName(elem.getTextContent());
				elem = (Element)spellElement.getElementsByTagName("title").item(0);
				spell.setTitle(elem.getTextContent());
				elem = (Element)spellElement.getElementsByTagName("area").item(0);
				spell.setArea(elem.getTextContent());
				elem = (Element)spellElement.getElementsByTagName("background").item(0);
				spell.setBackground(elem.getTextContent());	
				elem = (Element)spellElement.getElementsByTagName("cardText").item(0);
				spell.setCardText(elem.getTextContent());
				elem = (Element)spellElement.getElementsByTagName("castingTime").item(0);
				spell.setCastingTime(elem.getTextContent());
				elem = (Element)spellElement.getElementsByTagName("descriptor").item(0);
				spell.setDescriptor(elem.getTextContent());
				elem = (Element)spellElement.getElementsByTagName("detail").item(0);
				spell.setDetail(elem.getTextContent());
				elem = (Element)spellElement.getElementsByTagName("duration").item(0);
				spell.setDuration(elem.getTextContent());
				elem = (Element)spellElement.getElementsByTagName("effect").item(0);
				spell.setEffect(elem.getTextContent());
				elem = (Element)spellElement.getElementsByTagName("image").item(0);
				spell.setImage(elem.getTextContent());
				elem = (Element)spellElement.getElementsByTagName("material").item(0);
				spell.setMaterial(elem.getTextContent());
				elem = (Element)spellElement.getElementsByTagName("page").item(0);
				spell.setPage(elem.getTextContent());
				elem = (Element)spellElement.getElementsByTagName("range").item(0);
				spell.setRange(elem.getTextContent());
				elem = (Element)spellElement.getElementsByTagName("savingThrow").item(0);
				spell.setSavingThrow(elem.getTextContent());
				elem = (Element)spellElement.getElementsByTagName("target").item(0);
				spell.setTarget(elem.getTextContent());
				elem = (Element)spellElement.getElementsByTagName("spellResistance").item(0);
				spell.setSpellResistance(elem.getTextContent().equals("yes"));
				elem = (Element)spellElement.getElementsByTagName("school").item(0);
				spell.setSchoolId(Integer.parseInt(elem.getTextContent()));
				spell.setSourceId(5);
				elem = (Element)spellElement.getElementsByTagName("source").item(0);
				if(elem != null && elem.getTextContent() != null && !elem.getTextContent().equals(""))
					spell.setSourceId(Integer.parseInt(elem.getTextContent()));
				
				
				NodeList componentsElement = spellElement.getElementsByTagName("component");		
				for(int nodeIdx = 0; nodeIdx < componentsElement.getLength(); nodeIdx++) {
					elem = (Element)componentsElement.item(nodeIdx);
					spell.addComponentId(Integer.parseInt(elem.getTextContent()));
				}

				NodeList levelsElement = spellElement.getElementsByTagName("level");		
				for(int nodeIdx = 0; nodeIdx < levelsElement.getLength(); nodeIdx++) {
					elem = (Element)levelsElement.item(nodeIdx);
					spell.addLevel(Integer.parseInt(elem.getAttribute("class")),Integer.parseInt(elem.getTextContent()));
				}

				spells.add(spell);
			}
		} catch (Exception e) {}
		Spell[] result = new Spell[spells.size()];
		spells.toArray(result);
		return result;
	}

}