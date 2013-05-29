package spell.databases;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import spell.model.Component;
import spell.model.ItemType;
import spell.model.PlayerClass;
import spell.model.School;
import spell.model.Slot;
import spell.model.Source;
import spell.services.ServiceFactory;
import spell.services.impl.MagicItemServiceImpl;
import spell.services.impl.SpellServiceImpl;
import spell.tools.LocaleManager;


public class DatabaseManager extends SessionFactory {

	private static DatabaseManager instance;

	public final static DatabaseManager getInstance() {
		if (instance == null) {
			instance = new DatabaseManager();
			//instance.initialize();
		}
		return instance;
	}

	private DatabaseManager() {
		super();
	}

	public void initialize(boolean rebuild) {
		Session session = null;
		try {
			session = this.openSession();
			session.beginTransaction();
			int version = 0;
			
			try {
				Statement stat = session.getConnection().createStatement();
				ResultSet rs = stat.executeQuery("select * from VERSION;");
			    if(rs.next()) {
			    	version = rs.getInt("version");
			    }
			    rs.close();
			} catch (Exception e) {
				version = 1;
			}
			if(rebuild)
				version = 0;
			
			//Initialisation des Spells
	    	if(!ServiceFactory.getSpellService().checkTable(false, session)) {
	    		/*Spell spell = new Spell(0);
	    		spell.setTitle("Sort");
	    		spell.setOriginalName("Sort");
		    	session.save(spell);
		    	*/
	    	}
			
			if(version <= 1) {
				Statement stat = null;
				try {
					stat = session.getConnection().createStatement();
					stat.executeUpdate("create table VERSION (version);");
					stat.executeUpdate("INSERT INTO VERSION values (1);");				
				} catch (Exception e) {}
				finally {
					try {
						if(stat != null) stat.close();
					} catch (Exception e) {}
				}

				//Initialisation des PlayerClassService
		    	if(!ServiceFactory.getPlayerClassService().checkTable(true, session)) {
		    		session.save(new PlayerClass(0, LocaleManager.instance().getMessage("class.barbarian"), "class_barbarian.png","Bar",true));
		    		session.save(new PlayerClass(0, LocaleManager.instance().getMessage("class.bard"), "class_bard.png", "Brd",true));
		    		session.save(new PlayerClass(0, LocaleManager.instance().getMessage("class.druid"), "class_druid.png", "Dru",true));
		    		session.save(new PlayerClass(0, LocaleManager.instance().getMessage("class.sorcerer"), "class_sorcerer.png","Ens",true));
		    		session.save(new PlayerClass(0, LocaleManager.instance().getMessage("class.fighter"), "class_fighter.png","Gue",true));
		    		session.save(new PlayerClass(0, LocaleManager.instance().getMessage("class.wizard"), "class_wizard.png","Mag",true));
		    		session.save(new PlayerClass(0, LocaleManager.instance().getMessage("class.monk"), "class_monk.png","Moi",true));
		    		session.save(new PlayerClass(0, LocaleManager.instance().getMessage("class.paladin"), "class_paladin.png","Pal",true));
		    		session.save(new PlayerClass(0, LocaleManager.instance().getMessage("class.cleric"), "class_cleric.png","Pre",true));
		    		session.save(new PlayerClass(0, LocaleManager.instance().getMessage("class.ranger"), "class_ranger.png","Rod",true));
		    		session.save(new PlayerClass(0, LocaleManager.instance().getMessage("class.rogue"), "class_rogue.png","Rou",true));
		    		session.save(new PlayerClass(0, LocaleManager.instance().getMessage("class.alchemist"), "class_alchemist.png","Alc",false));
		    		session.save(new PlayerClass(0, LocaleManager.instance().getMessage("class.cavalier"), "class_knight.png", "Che",false));
		    		session.save(new PlayerClass(0, LocaleManager.instance().getMessage("class.summoner"), "class_conj.png", "Con",false));
		    		session.save(new PlayerClass(0, LocaleManager.instance().getMessage("class.inquisitor"), "class_inquisitor.png","Inq",false));
		    		session.save(new PlayerClass(0, LocaleManager.instance().getMessage("class.oracle"), "class_oracle.png","Ora",false));
		    		session.save(new PlayerClass(0, LocaleManager.instance().getMessage("class.witch"), "class_sorceress.png","Sor",false));
		    	}
		    	//Initialisation des School
		    	if(!ServiceFactory.getSchoolService().checkTable(true, session)) {
		    		session.save(new School(0, LocaleManager.instance().getMessage("school.universal"), "group.png"));
		    		session.save(new School(0, LocaleManager.instance().getMessage("school.abjuration"), "school_1.png"));
		    		session.save(new School(0, LocaleManager.instance().getMessage("school.divination"), "wand.png"));
		    		session.save(new School(0, LocaleManager.instance().getMessage("school.enchantment"), "school_3.png"));
		    		session.save(new School(0, LocaleManager.instance().getMessage("school.evocation"), "school_4.png"));
		    		session.save(new School(0, LocaleManager.instance().getMessage("school.illusion"), "school_5.png"));
		    		session.save(new School(0, LocaleManager.instance().getMessage("school.conjuration"), "school_6.png"));
		    		session.save(new School(0, LocaleManager.instance().getMessage("school.necromancy"), "school_7.png"));
		    		session.save(new School(0, LocaleManager.instance().getMessage("school.transmutation"), "school_8.png"));
		    	}

	    		Statement prep = session.getConnection().createStatement();
				prep.executeUpdate("update SPELL set id_school = id_school +1;");				
				prep.executeUpdate("update SPELL_LEVEL set id_player_class = id_player_class +1;");				
				prep.executeUpdate("update SPELL_COMPONENT set id_component = id_component +1;");				
				prep.close();
				
			}
			
			if(version <= 2) {
				//Initialisation des Component
		    	if(!ServiceFactory.getComponentService().checkTable(true, session)) {
			    	session.save(new Component(0, LocaleManager.instance().getMessage("component.verbal"), "ico1.gif", LocaleManager.instance().getMessage("component.verbal.short")));
			    	session.save(new Component(0, LocaleManager.instance().getMessage("component.somatic"), "ico1.gif", LocaleManager.instance().getMessage("component.somatic.short")));
			    	session.save(new Component(0, LocaleManager.instance().getMessage("component.material"), "ico1.gif", LocaleManager.instance().getMessage("component.material.short")));
			    	session.save(new Component(0, LocaleManager.instance().getMessage("component.focus"), "ico1.gif", LocaleManager.instance().getMessage("component.focus.short")));
			    	session.save(new Component(0, LocaleManager.instance().getMessage("component.divinefocus"), "ico1.gif", LocaleManager.instance().getMessage("component.divinefocus.short")));
		    	}
			}
	
			if(version <= 3) {

		    	if(!ServiceFactory.getSlotService().checkTable(true, session)) {
			    	session.save(new Slot(0, LocaleManager.instance().getMessage("slot.none"), "slot_none.png"));
			    	session.save(new Slot(0, LocaleManager.instance().getMessage("slot.armor"), "slot_armor.png"));
			    	session.save(new Slot(0, LocaleManager.instance().getMessage("slot.belts"), "slot_belts.png"));
			    	session.save(new Slot(0, LocaleManager.instance().getMessage("slot.body"), "slot_body.png"));
			    	session.save(new Slot(0, LocaleManager.instance().getMessage("slot.chest"), "slot_chest.png"));
			    	session.save(new Slot(0, LocaleManager.instance().getMessage("slot.eyes"), "slot_eyes.png"));
			    	session.save(new Slot(0, LocaleManager.instance().getMessage("slot.feet"), "slot_feet.png"));
			    	session.save(new Slot(0, LocaleManager.instance().getMessage("slot.hands"), "slot_hands.png"));
			    	session.save(new Slot(0, LocaleManager.instance().getMessage("slot.head"), "slot_head.png"));
			    	session.save(new Slot(0, LocaleManager.instance().getMessage("slot.headband"), "slot_headband.png"));
			    	session.save(new Slot(0, LocaleManager.instance().getMessage("slot.neck"), "slot_neck.png"));
			    	session.save(new Slot(0, LocaleManager.instance().getMessage("slot.ring"), "slot_ring.png"));
			    	session.save(new Slot(0, LocaleManager.instance().getMessage("slot.shield"), "slot_shield.png"));
			    	session.save(new Slot(0, LocaleManager.instance().getMessage("slot.shoulders"), "slot_shoulders.png"));
			    	session.save(new Slot(0, LocaleManager.instance().getMessage("slot.wrist"), "slot_wrist.png"));
		    	}
		    	if(!ServiceFactory.getItemTypeService().checkTable(true, session)) {
			    	session.save(new ItemType(0, LocaleManager.instance().getMessage("item.armors"), "item_armors.png"));
			    	session.save(new ItemType(0, LocaleManager.instance().getMessage("item.weapons"), "item_weapons.png"));
			    	session.save(new ItemType(0, LocaleManager.instance().getMessage("item.potions"), "item_potions.png"));
			    	session.save(new ItemType(0, LocaleManager.instance().getMessage("item.rings"), "item_rings.png"));
			    	session.save(new ItemType(0, LocaleManager.instance().getMessage("item.rods"), "item_rods.png"));
			    	session.save(new ItemType(0, LocaleManager.instance().getMessage("item.scrolls"), "item_scrolls.png"));
			    	session.save(new ItemType(0, LocaleManager.instance().getMessage("item.staves"), "item_staves.png"));
			    	session.save(new ItemType(0, LocaleManager.instance().getMessage("item.wands"), "item_wands.png"));
			    	session.save(new ItemType(0, LocaleManager.instance().getMessage("item.wondrous"), "item_wondrous.png"));
		    	}

		    	if(!ServiceFactory.getMagicItemService().checkTable(false, session)) {
		    		
		    		
		    	}
			}
			
			if(version <= 4) {
		    	if(!ServiceFactory.getSourceService().checkTable(true, session)) {
			    	session.save(new Source(0, LocaleManager.instance().getMessage("source.base"), "source.png", LocaleManager.instance().getMessage("source.base.short")));
			    	session.save(new Source(0, LocaleManager.instance().getMessage("source.APG"), "source_advanced.png", LocaleManager.instance().getMessage("source.APG.short")));
			    	session.save(new Source(0, LocaleManager.instance().getMessage("source.ultimateMagic"), "source_advanced.png", LocaleManager.instance().getMessage("source.ultimateMagic.short")));
			    	session.save(new Source(0, LocaleManager.instance().getMessage("source.ultimateCombat"), "source_advanced.png", LocaleManager.instance().getMessage("source.ultimateCombat.short")));
			    	session.save(new Source(0, LocaleManager.instance().getMessage("source.custom"), "source_custom.png", LocaleManager.instance().getMessage("source.custom.short")));
			    	session.save(new Source(0, LocaleManager.instance().getMessage("source.internet"), "source_internet.png", LocaleManager.instance().getMessage("source.internet.short")));
		    	}
		    	
	    		Statement prep = session.getConnection().createStatement();
				prep.executeUpdate("ALTER TABLE " + SpellServiceImpl.TABLE_NAME + " ADD sourceID;");				
				prep.executeUpdate("update " + SpellServiceImpl.TABLE_NAME + " set sourceID = 1;");				
				prep.executeUpdate("ALTER TABLE " + MagicItemServiceImpl.TABLE_NAME + " ADD sourceID;");				
				prep.executeUpdate("update " + MagicItemServiceImpl.TABLE_NAME + " set sourceID = 1;");				
				prep.close();
				
	    		session.save(new PlayerClass(0, LocaleManager.instance().getMessage("class.anti-paladin"), "class_anti-paladin.png","APa",false));
	    		session.save(new PlayerClass(0, LocaleManager.instance().getMessage("class.magus"), "class_magus.png", "Mgs",false));
	    		session.save(new PlayerClass(0, LocaleManager.instance().getMessage("class.ninja"), "class_ninja.png", "Nin",false));
	    		session.save(new PlayerClass(0, LocaleManager.instance().getMessage("class.pistolero"), "class_pistolero.png","Pis",false));
	    		session.save(new PlayerClass(0, LocaleManager.instance().getMessage("class.samourai"), "class_samourai.png","Sam",false));
			}
			
			Statement stat = null;
			try {
				stat = session.getConnection().createStatement();
				stat.executeUpdate("UPDATE VERSION set VERSION = 5;");				
			} catch (Exception e) {}
			finally {
				try {
					if(stat != null) stat.close();
				} catch (Exception e) {}
			}

			session.commit();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
			session.rollback();
		} finally {
			if(session != null)	session.close();
		}
		
	}

	public String buildSearchString(String name) {
		return buildSearchString(name, false);
	}
	public String buildSearchString(String name, boolean upper) {
		name = name.toLowerCase();
		name = name.replace('â','a');
		name = name.replace('à','a');
		name = name.replace('ä','a');
		name = name.replace('é','e');
		name = name.replace('è','e');
		name = name.replace('ê','e');
		name = name.replace('ë','e');
		name = name.replace('ì','i');
		name = name.replace('î','i');
		name = name.replace('ï','i');
		name = name.replace('ò','o');
		name = name.replace('ö','o');
		name = name.replace('ô','o');
		name = name.replace('ù','u');
		name = name.replace('û','u');
		name = name.replace('ü','u');
		name = name.replace('ÿ','y');
		if(upper) name = name.toUpperCase();
		return name;
	}
}
