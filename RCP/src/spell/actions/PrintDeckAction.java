package spell.actions;


import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;

import spell.Activator;
import spell.model.simple.SimpleModel;
import spell.preferences.ImpressionPage;
import spell.tools.PDFCardBuilder;
import spell.tools.PDFListBuilder;
import spell.views.ViewDeck;



public class PrintDeckAction extends Action {
	
	public final static String ICON = Activator.ICON_PRINTER;
	
	public PrintDeckAction(String label) {
        setText(label);
        // The id is used to refer to the action in a menu or toolbar
		setId(ICommandIds.CMD_PRINT_SPELL);
        // Associate the action with a pre-defined command, to allow key bindings.
		//setActionDefinitionId(ICommandIds.CMD_PRINT_SPELL);
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
				fDialog.setFilterExtensions(new String[]{"*.pdf"});
				fDialog.setOverwrite(true);
				String file = fDialog.open();
		        if (file != null) {
		        	
		        	if(!Activator.getDefault().getPreferenceStore().getBoolean(ImpressionPage.PRINT_LISTING)) {
						PDFCardBuilder cardPrinter = new PDFCardBuilder();
						for(SimpleModel card : cards) {
							cardPrinter.add(card);
						}
						cardPrinter.draw(file, Activator.getDefault().getPreferenceStore().getBoolean(ImpressionPage.FRONT_BACK));
		        	} else {
						PDFListBuilder cardPrinter = new PDFListBuilder();
						for(SimpleModel card : cards) {
							cardPrinter.add(card);
						}
						cardPrinter.draw(file);
		        	}
	        }
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
}
