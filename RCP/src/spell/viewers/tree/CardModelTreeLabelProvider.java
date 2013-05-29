package spell.viewers.tree;

import java.util.Hashtable;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import spell.Activator;
import spell.databases.DatabaseManager;
import spell.databases.Session;
import spell.model.ItemType;
import spell.model.MagicItem;
import spell.model.Slot;
import spell.model.Spell;
import spell.services.ServiceFactory;

public class CardModelTreeLabelProvider extends LabelProvider {
	
	private Hashtable<Integer, Image> slotImages = null;
	private Hashtable<Integer, Image> itemTypeImages = null;
	
	public CardModelTreeLabelProvider() {
		slotImages = new Hashtable<Integer, Image>();
		itemTypeImages = new Hashtable<Integer, Image>();
		Session session = null;
		try {
			session = DatabaseManager.getInstance().openSession();
			Slot[] slotsTab = ServiceFactory.getSlotService().getAll(session);
			for(Slot slot : slotsTab)
				slotImages.put(new Integer(slot.getId()), Activator.getImage(Activator.FOLDER_IMAGES + slot.getImage()));
			ItemType[] itemTypesTab = ServiceFactory.getItemTypeService().getAll(session);
			for(ItemType itemType : itemTypesTab)
				itemTypeImages.put(new Integer(itemType.getId()), Activator.getImage(Activator.FOLDER_IMAGES + itemType.getImage()));
			session.close();
		}
		catch (Exception e) {e.printStackTrace();}
		finally {if(session != null) session.close();}

	}

	public String getText(Object element) {
		if(element instanceof Spell) {
			return "(x" + ((Spell) element).getSize() + ") " + ((Spell) element).getTitle();
		} if(element instanceof MagicItem) {
			return "(x" + ((MagicItem) element).getSize() + ") " + ((MagicItem) element).getTitle();
		} else if(element instanceof LevelTreeBox) {
			return ((LevelTreeBox) element).getTitle();
		} else if(element instanceof InventoryTreeBox) {
			return ((InventoryTreeBox) element).getTitle();
		}
		return "";
		
	}

	public Image getImage(Object element) {
		if(element instanceof Spell) {
			return Activator.getImage(Activator.SPELL_ICON);
		} else if(element instanceof LevelTreeBox) {
			return Activator.getImage(Activator.SPELL_ICON_LIB);			
		} else if(element instanceof InventoryTreeBox) {
			return Activator.getImage(Activator.MAGIC_ITEM_LIB);			
		} else if(element instanceof MagicItem) {
			MagicItem item = (MagicItem)element;
			if(item.getSlotId() > 1)
				return slotImages.get(new Integer(item.getSlotId()));
			return itemTypeImages.get(new Integer(item.getItemTypeId()));
		}
		return null;
	}

	public void dispose() {	}
	
	
}

