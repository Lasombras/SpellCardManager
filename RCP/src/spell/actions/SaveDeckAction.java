package spell.actions;

import java.io.BufferedWriter;
import java.io.FileWriter;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;

import spell.Activator;
import spell.model.MagicItem;
import spell.model.Spell;
import spell.model.simple.SimpleModel;
import spell.views.ViewDeck;



public class SaveDeckAction extends Action {
	
	public final static String ICON = Activator.ICON_SAVE;
	
	public SaveDeckAction(String label) {
        setText(label);
        // The id is used to refer to the action in a menu or toolbar
		setId(ICommandIds.CMD_SAVE_LIST_SPELL);
        // Associate the action with a pre-defined command, to allow key bindings.
		//setActionDefinitionId(ICommandIds.CMD_SAVE_LIST_SPELL);
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

			SimpleModel[] cards = viewDeck.getCards();
			if(cards != null && cards.length > 0) {
				FileDialog fDialog = new FileDialog(viewDeck.getTop().getShell(), SWT.SAVE | SWT.SINGLE);
				fDialog.setFilterExtensions(new String[]{"*.lst"});
				fDialog.setOverwrite(true);
				String file = fDialog.open();
		        if (file != null) {
		        	FileWriter fos = null;
		        	try {
			    		fos = new FileWriter(file, false);
			        	BufferedWriter bfw = new BufferedWriter(fos);
	
			        	
			        	bfw.write("playerClass#" + viewDeck.getPlayerClassId());
						bfw.newLine();
						for(SimpleModel card : cards) {
							for(int i = 0; i < card.getSize(); i++) {
								if(card instanceof Spell) {
									bfw.write("spell#" + ((Spell)card).getOriginalName());
									bfw.newLine();
								} else if(card instanceof MagicItem) {
									bfw.write("magicItem#" + ((MagicItem)card).getOriginalName());
									bfw.newLine();
								}
							}
						}
						bfw.close();
		        	} catch (Exception e2) {}
			    	finally {
			    		try {if(fos != null) fos.close();}catch (Exception e3) {}
			    	}
		        }				
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
}
