package spell.tools;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import spell.editors.MagicItemFormEditor;
import spell.editors.MagicItemFormEditorInput;
import spell.editors.SpellFormEditor;
import spell.editors.SpellFormEditorInput;
import spell.model.MagicItem;
import spell.model.Spell;
import spell.model.simple.ISharedModelBoxIds;
import spell.model.simple.MagicItemModelBox;
import spell.model.simple.SharedSimpleModelBox;
import spell.model.simple.SimpleModel;
import spell.model.simple.SpellModelBox;

public abstract class LinkManager {

	public final static boolean goLink(String link) {
		link = link.replaceAll("about:blank", "");
		link = link.replaceAll("about:", "");
		link = link.replace("%20", " ");
		
		if(link.startsWith("spell#")) {
			link = link.substring("spell#".length());
			Spell selectedSpell = ((SpellModelBox)SharedSimpleModelBox.instance().get(ISharedModelBoxIds.BOX_SPELL)).get(link);
			if(selectedSpell != null) {
				try {	
					//Rechercher si le magicItem n'est pas déjà ouvert pour le selectionner
					IEditorReference[] editors = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getEditorReferences();
					IEditorInput editor = null;
					for(int i = 0; i < editors.length; i++) {
						if(editors[i].getId().equals(SpellFormEditor.ID)) {
							Spell an = ((SpellFormEditorInput)editors[i].getEditorInput()).getSpell();
							if(an.getId() == selectedSpell.getId()) {
								editor = editors[i].getEditorInput();
								break;
							}
						}
					}
					if(editor == null)
						editor = new SpellFormEditorInput(selectedSpell.getTitle(), selectedSpell);
					//Ouvrir un editeur
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(editor, SpellFormEditor.ID);
					
					return true;
				} catch (PartInitException e) {
					e.printStackTrace();
				}
			}
		} else if(link.startsWith("magicItem#")) {
			link = link.substring("magicItem#".length());
			MagicItem selectedMagicItem = ((MagicItemModelBox)SharedSimpleModelBox.instance().get(ISharedModelBoxIds.BOX_MAGIC_ITEM)).get(link);
			if(selectedMagicItem != null) {
				try {	
					//Rechercher si le magicItem n'est pas déjà ouvert pour le selectionner
					IEditorReference[] editors = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getEditorReferences();
					IEditorInput editor = null;
					for(int i = 0; i < editors.length; i++) {
						if(editors[i].getId().equals(MagicItemFormEditor.ID)) {
							MagicItem an = ((MagicItemFormEditorInput)editors[i].getEditorInput()).getMagicItem();
							if(an.getId() == selectedMagicItem.getId()) {
								editor = editors[i].getEditorInput();
								break;
							}
						}
					}
					if(editor == null)
						editor = new MagicItemFormEditorInput(selectedMagicItem.getTitle(), selectedMagicItem);
					//Ouvrir un editeur
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(editor, MagicItemFormEditor.ID);
					return true;
					
				} catch (PartInitException e) {
					e.printStackTrace();
				}
			}	
		} else {
			Spell selectedSpell = ((SpellModelBox)SharedSimpleModelBox.instance().get(ISharedModelBoxIds.BOX_SPELL)).get(link);
			if(selectedSpell != null) {
				try {	
					//Rechercher si le magicItem n'est pas déjà ouvert pour le selectionner
					IEditorReference[] editors = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getEditorReferences();
					IEditorInput editor = null;
					for(int i = 0; i < editors.length; i++) {
						if(editors[i].getId().equals(SpellFormEditor.ID)) {
							Spell an = ((SpellFormEditorInput)editors[i].getEditorInput()).getSpell();
							if(an.getId() == selectedSpell.getId()) {
								editor = editors[i].getEditorInput();
								break;
							}
						}
					}
					if(editor == null)
						editor = new SpellFormEditorInput(selectedSpell.getTitle(), selectedSpell);
					//Ouvrir un editeur
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(editor, SpellFormEditor.ID);
					return true;
					
				} catch (PartInitException e) {
					e.printStackTrace();
				}
			}			
		}
		return false;
	}
	
	public final static void openSimpleModel(SimpleModel model) {
		if(model == null)
			return;
		if(model instanceof MagicItem) {
			try {	
				//Rechercher si le spell n'est pas déjà ouvert pour le selectionner
				IEditorReference[] editors = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getEditorReferences();
				IEditorInput editor = null;
				for(int i = 0; i < editors.length; i++) {
					if(editors[i].getId().equals(MagicItemFormEditor.ID)) {
						MagicItem an = ((MagicItemFormEditorInput)editors[i].getEditorInput()).getMagicItem();
						if(an.getId() == model.getId()) {
							editor = editors[i].getEditorInput();
							break;
						}
					}
				}
				if(editor == null)
					editor = new MagicItemFormEditorInput(model.getTitle(), (MagicItem)model);
				//Ouvrir un editeur
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(editor, MagicItemFormEditor.ID);
				
			} catch (PartInitException e) {
				e.printStackTrace();
			}
		} else if(model instanceof Spell) {
			try {	
				//Rechercher si le spell n'est pas déjà ouvert pour le selectionner
				IEditorReference[] editors = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getEditorReferences();
				IEditorInput editor = null;
				for(int i = 0; i < editors.length; i++) {
					if(editors[i].getId().equals(SpellFormEditor.ID)) {
						Spell an = ((SpellFormEditorInput)editors[i].getEditorInput()).getSpell();
						if(an.getId() == model.getId()) {
							editor = editors[i].getEditorInput();
							break;
						}
					}
				}
				if(editor == null)
					editor = new SpellFormEditorInput(model.getTitle(), (Spell)model);
				//Ouvrir un editeur
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(editor, SpellFormEditor.ID);
				
			} catch (PartInitException e) {
				e.printStackTrace();
			}
		}
	}

	public static boolean errorInLinks(String currentStr) {
		String balise = "<a href=\"";
		while(currentStr.indexOf(balise) > -1) {
			
			String strEnd = currentStr.substring(currentStr.indexOf(balise)+balise.length());
			String link = strEnd.substring(0,strEnd.indexOf("\">")).trim();
			currentStr = strEnd.substring(strEnd.indexOf(">")+1);
			if(link.startsWith("spell#")) {
				link = link.substring("spell#".length());
				Spell selectedSpell = ((SpellModelBox)SharedSimpleModelBox.instance().get(ISharedModelBoxIds.BOX_SPELL)).get(link);
				if(selectedSpell == null) {
					System.out.println(link);
					return true;
				}
			} else if(link.startsWith("magicItem#")){
				link = link.substring("spell#".length());
				MagicItem selectedMagicItem = ((MagicItemModelBox)SharedSimpleModelBox.instance().get(ISharedModelBoxIds.BOX_MAGIC_ITEM)).get(link);
				if(selectedMagicItem == null) {
					System.out.println(link);
					return true;
				}
			} else {
				Spell selectedSpell = ((SpellModelBox)SharedSimpleModelBox.instance().get(ISharedModelBoxIds.BOX_SPELL)).get(link);
				if(selectedSpell == null) {
					System.out.println(link);
					return true;
				}
			}
		}
		
		return false;
	}

}
