package spell.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import spell.Activator;
import spell.databases.DatabaseManager;
import spell.databases.Session;
import spell.editors.MagicItemFormEditor;
import spell.editors.MagicItemFormEditorInput;
import spell.model.MagicItem;
import spell.model.simple.ISharedModelBoxIds;
import spell.model.simple.SharedSimpleModelBox;
import spell.services.ServiceFactory;
import spell.tools.LocaleManager;



public class NewMagicItemAction extends Action {
		
	public NewMagicItemAction(String label) {
       setText(label);
        // The id is used to refer to the action in a menu or toolbar
		setId(ICommandIds.CMD_NEW_MAGIC_ITEM);
        // Associate the action with a pre-defined command, to allow key bindings.
		//setActionDefinitionId(ICommandIds.CMD_NEW_MAGIC_ITEM);
		setImageDescriptor(Activator.getImageDescriptor(Activator.MAGIC_ITEM_ICON_ADD));
	}
	
	public void run() {
		execute();
	}
	
	public static void execute() {		
		try {
			//Creer un magicItem en BASE
			MagicItem selectedMagicItem = new MagicItem(-1);
			Session session = null;
			try {
				session = DatabaseManager.getInstance().openSession();
				session.beginTransaction();
				ServiceFactory.getMagicItemService().save(selectedMagicItem, session);
				session.commit();
			} catch (Exception e) {
				if(session != null) session.rollback();
				e.printStackTrace();
			} finally {
				if(session != null) session.close();
			}

			SharedSimpleModelBox.instance().get(ISharedModelBoxIds.BOX_MAGIC_ITEM).add(selectedMagicItem, true);
			//Ouvrir un editeur
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(new MagicItemFormEditorInput(selectedMagicItem.getTitle(), selectedMagicItem), MagicItemFormEditor.ID);
		} catch (PartInitException e) {
			MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), LocaleManager.instance().getMessage("error"), LocaleManager.instance().getMessage("errorNewMagicItem") + " " + e.getMessage());
		}
	}
}
