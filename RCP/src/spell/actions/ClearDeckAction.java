package spell.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;

import spell.Activator;
import spell.views.ViewDeck;



public class ClearDeckAction extends Action {
	
	public final static String ICON = Activator.ICON_CLEAR;
	
	public ClearDeckAction(String label) {
		setText(label);
        // The id is used to refer to the action in a menu or toolbar
		setId(ICommandIds.CMD_LOAD_LIST_SPELL);
        // Associate the action with a pre-defined command, to allow key bindings.
		//setActionDefinitionId(ICommandIds.CMD_LOAD_LIST_SPELL);
		setImageDescriptor(Activator.getImageDescriptor(ICON));
	}
	
	public void run() {
		execute();
	}
	
	public static void execute() {		
		try {
			ViewDeck viewDeck = null;
			IViewPart viewPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ViewDeck.ID);
			if(viewPart != null && viewPart instanceof ViewDeck) {
				viewDeck = (ViewDeck)viewPart;
			}
	    		
		    viewDeck.clear();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
}
