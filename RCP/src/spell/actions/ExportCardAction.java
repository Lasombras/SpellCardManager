package spell.actions;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;

import spell.Activator;
import spell.jobs.ExportJob;
import spell.tools.LocaleManager;
import spell.views.ViewDeck;



public class ExportCardAction extends Action {
	
	private final IWorkbenchWindow window;
	
	public ExportCardAction(IWorkbenchWindow window, String label) {
		this.window = window;
        setText(label);
        // The id is used to refer to the action in a menu or toolbar
		setId(ICommandIds.CMD_EXPORT_SPELL);
        // Associate the action with a pre-defined command, to allow key bindings.
		//setActionDefinitionId(ICommandIds.CMD_EXPORT_SPELL);
		setImageDescriptor(Activator.getImageDescriptor(Activator.ICON_EXPORT_CARD));
	}
	
	public void run() {
		if(window != null) {	
			IViewPart viewPart = window.getActivePage().findView(ViewDeck.ID);
			
			if(viewPart != null && viewPart instanceof ViewDeck) {
				ViewDeck viewDeck = (ViewDeck)viewPart;
				
				FileDialog fDialog = new FileDialog(window.getShell(), SWT.SAVE | SWT.SINGLE);
				fDialog.setFilterExtensions(new String[]{"*.cards"});
				fDialog.setOverwrite(true);
				String exportFile = fDialog.open();
		        if (exportFile != null) {

					ExportJob job = new ExportJob(viewDeck.getCards(), exportFile);
					try {
						new ProgressMonitorDialog(window.getShell()).run(false, true,job);
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					} 
					switch (job.getState()) {
						case ExportJob.ERROR:
							MessageDialog.openError(window.getShell(), LocaleManager.instance().getMessage("error") , job.getMessage());
							break;
						case ExportJob.CANCELED:
							MessageDialog.openWarning(window.getShell(), LocaleManager.instance().getMessage("canceled") , job.getMessage());
							break;
						case ExportJob.OK:
							MessageDialog.openInformation(window.getShell(), LocaleManager.instance().getMessage("information"), job.getMessage());
							break;
					}
		        }
			}
		}
	}
}
