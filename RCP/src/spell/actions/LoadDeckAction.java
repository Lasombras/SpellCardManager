package spell.actions;

import java.io.BufferedReader;
import java.io.FileReader;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;

import spell.Activator;
import spell.model.MagicItem;
import spell.model.Spell;
import spell.model.simple.ISharedModelBoxIds;
import spell.model.simple.MagicItemModelBox;
import spell.model.simple.SharedSimpleModelBox;
import spell.model.simple.SpellModelBox;
import spell.views.ViewDeck;



public class LoadDeckAction extends Action {
	
	public final static String ICON = Activator.ICON_LOAD_DECK;
	
	public LoadDeckAction(String label) {
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

			
			FileDialog fDialog = new FileDialog(viewDeck.getTop().getShell(), SWT.OPEN | SWT.SINGLE);
			fDialog.setFilterExtensions(new String[]{"*.lst"});
			String file = fDialog.open();
	        if (file != null) {
	        	
	        	viewDeck.clear();
	        	FileReader fr = null;
	        	try {
	        		fr = new FileReader(file);
		        	BufferedReader bfr = new BufferedReader(fr);
		        	String line = bfr.readLine();
		        	while(line != null && !line.equals("")) {
		        		if(line.startsWith("magicItem#")) {
		        			line = line.substring("magicItem#".length());
			        		MagicItem magicItem = ((MagicItemModelBox)SharedSimpleModelBox.instance().get(ISharedModelBoxIds.BOX_MAGIC_ITEM)).get(line);
			        		magicItem.increaseSize();
			        		magicItem.fireSizeChanged();
		        		} else if(line.startsWith("playerClass#")) {
		        			viewDeck.setPlayerClassFilter(Integer.parseInt(line.substring("playerClass#".length())));
		        		} else if(line.startsWith("spell#")) {
		        			line = line.substring("spell#".length());
			        		Spell spell = ((SpellModelBox)SharedSimpleModelBox.instance().get(ISharedModelBoxIds.BOX_SPELL)).get(line);
			        		spell.increaseSize();
			        		spell.fireSizeChanged();
		        		} else {
			        		Spell spell = ((SpellModelBox)SharedSimpleModelBox.instance().get(ISharedModelBoxIds.BOX_SPELL)).get(line);
			        		spell.increaseSize();
			        		spell.fireSizeChanged();		        			
		        		}
		        		line = bfr.readLine();
					}
		        	bfr.close();
	        	} catch (Exception e2) {}
		    	finally {
		    		try {if(fr != null) fr.close();}catch (Exception e3) {}
		    	}

		    	//viewDeck.refreshToolBar();
	        	
	        }				
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
}
