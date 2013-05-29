package spell.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import spell.Activator;
import spell.databases.DatabaseManager;
import spell.databases.Session;
import spell.editors.SpellFormEditor;
import spell.editors.SpellFormEditorInput;
import spell.model.Spell;
import spell.model.simple.ISharedModelBoxIds;
import spell.model.simple.SharedSimpleModelBox;
import spell.services.ServiceFactory;
import spell.tools.LocaleManager;



public class NewSpellAction extends Action {
		
	public NewSpellAction(String label) {
       setText(label);
        // The id is used to refer to the action in a menu or toolbar
		setId(ICommandIds.CMD_NEW_SPELL);
        // Associate the action with a pre-defined command, to allow key bindings.
		//setActionDefinitionId(ICommandIds.CMD_NEW_SPELL);
		setImageDescriptor(Activator.getImageDescriptor(Activator.SPELL_ICON_ADD));
	}
	
	public void run() {
		execute();
	}
	
	public static void execute() {		
		try {
			//Creer un spell en BASE
			Spell selectedSpell = new Spell(-1);
			Session session = null;
			try {
				session = DatabaseManager.getInstance().openSession();
				session.beginTransaction();
				ServiceFactory.getSpellService().save(selectedSpell, session);
				session.commit();
			} catch (Exception e) {
				if(session != null) session.rollback();
				e.printStackTrace();
			} finally {
				if(session != null) session.close();
			}

			SharedSimpleModelBox.instance().get(ISharedModelBoxIds.BOX_SPELL).add(selectedSpell, true);
			//Ouvrir un editeur
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(new SpellFormEditorInput(selectedSpell.getTitle(), selectedSpell), SpellFormEditor.ID);
		} catch (PartInitException e) {
			MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), LocaleManager.instance().getMessage("error"), LocaleManager.instance().getMessage("errorNewSpell") + " " + e.getMessage());
		}
	}
}
