package spell.actions;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import spell.Activator;
import spell.jobs.ImportJob;
import spell.tools.LocaleManager;



public class ImportCardAction extends Action {
	
	private final IWorkbenchWindow window;
	
	public ImportCardAction(IWorkbenchWindow window, String label) {
		this.window = window;
        setText(label);
        // The id is used to refer to the action in a menu or toolbar
		setId(ICommandIds.CMD_IMPORT_SPELL);
        // Associate the action with a pre-defined command, to allow key bindings.
		//setActionDefinitionId(ICommandIds.CMD_IMPORT_SPELL);
		setImageDescriptor(Activator.getImageDescriptor(Activator.ICON_IMPORT_CARD));
	}
	
	public void run() {
		if(window != null) {			
			FileDialog fDialog = new FileDialog(window.getShell(), SWT.OPEN | SWT.SINGLE);
			fDialog.setFilterExtensions(new String[]{"*.cards"});
			String file = fDialog.open();
	        if (file != null) {
	        	//Copie database
	        	try {
		        	File inputFile = new File(Activator.getPath() + Activator.getDataFolder() + Activator.DATABASE_FILENAME);
		            File outputFile = new File(Activator.getPath() + Activator.getDataFolder() + Activator.DATABASE_FILENAME + ".back");
	
		            FileReader in = new FileReader(inputFile);
		            FileWriter out = new FileWriter(outputFile);
		            int c;
	
		            while ((c = in.read()) != -1)
		              out.write(c);
	
		            in.close();
		            out.close();
	        	} catch (Exception e) {}
	        	
				ImportJob job = new ImportJob(window.getShell(), file);
				try {
					new ProgressMonitorDialog(window.getShell()).run(false, true,job);
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} 
				switch (job.getState()) {
					case ImportJob.ERROR:
						MessageDialog.openError(window.getShell(), LocaleManager.instance().getMessage("error") , job.getMessage());
						break;
					case ImportJob.CANCELED:
						MessageDialog.openWarning(window.getShell(), LocaleManager.instance().getMessage("canceled") , job.getMessage());
						break;
					case ImportJob.OK:
						MessageDialog.openInformation(window.getShell(), LocaleManager.instance().getMessage("information") , job.getMessage() + "\n\n" + LocaleManager.instance().getMessage("infoRestart"));
						PlatformUI.getWorkbench().restart();
						break;
				}
	        }
		}
	}
}
