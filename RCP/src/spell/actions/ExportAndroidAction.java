package spell.actions;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.ui.PlatformUI;

import spell.Activator;
import spell.jobs.ExportJob;
import spell.model.simple.ISharedModelBoxIds;
import spell.model.simple.SharedSimpleModelBox;
import spell.model.simple.SimpleModel;
import spell.model.simple.SpellModelBox;
import spell.tools.LocaleManager;



public class ExportAndroidAction extends Action {
		
	public ExportAndroidAction(String label) {
       setText(label);
        // The id is used to refer to the action in a menu or toolbar
		setId(ICommandIds.CMD_EXPORT_ANDROID);
        // Associate the action with a pre-defined command, to allow key bindings.
		setImageDescriptor(Activator.getImageDescriptor(Activator.ICON_EXPORT_ANDROID));
	}
	
	public void run() {
				
		DirectoryDialog directoryDialog = new DirectoryDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		directoryDialog.setMessage(LocaleManager.instance().getMessage("selectExportAndroidDir"));
        
        String dir = directoryDialog.open();
        if (dir != null) {
        	;
        	
			ExportJob job = new ExportJob((SimpleModel[])((SpellModelBox)SharedSimpleModelBox.instance().get(ISharedModelBoxIds.BOX_SPELL)).getContents(), dir + File.separator + "datas.adf", true);
			try {
				new ProgressMonitorDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell()).run(false, true,job);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} 
			switch (job.getState()) {
				case ExportJob.ERROR:
					MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), LocaleManager.instance().getMessage("error") , job.getMessage());
					break;
				case ExportJob.CANCELED:
					MessageDialog.openWarning(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), LocaleManager.instance().getMessage("canceled") , job.getMessage());
					break;
				case ExportJob.OK:
					MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), LocaleManager.instance().getMessage("information"), job.getMessage());
					break;
			}
		}
	}
}
