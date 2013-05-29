package spell.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;

import spell.databases.DatabaseManager;
import spell.databases.Session;
import spell.editors.pages.MagicItemCardPage;
import spell.editors.pages.MagicItemPage;
import spell.editors.pages.MagicItemViewPage;
import spell.model.MagicItem;
import spell.tools.LocaleManager;


public class MagicItemFormEditor extends FormEditor {

	public MagicItemPage pageMagicItem;
	public MagicItemViewPage viewPageMagicItem;
	public MagicItemCardPage pageCardMagicItem;
	public final static String ID = "Spell.magicFormItemEditor";

	public MagicItemFormEditor() {
	}
	
	public boolean isDirty() {
		return ((MagicItemFormEditorInput) this.getEditorInput()).getMagicItem().isDirty();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.forms.editor.FormEditor#addPages()
	 */
	protected void addPages() {
		try {
			viewPageMagicItem = new MagicItemViewPage(this);
			addPage(viewPageMagicItem);			
			pageMagicItem = new MagicItemPage(this);
			addPage(pageMagicItem);
			pageCardMagicItem = new MagicItemCardPage(this);
			addPage(pageCardMagicItem);
			if(getEditorInput().getName().equals(""))
				setActivePage(1);
		} catch (PartInitException e) {
			//
		}
	}

	
	public void doSave(IProgressMonitor monitor) {
		MagicItem magicItemModel = ((MagicItemFormEditorInput)this.getEditorInput()).getMagicItem();
		if(magicItemModel.isDirty()) {
			// Ouverture d'une session sur la base PACHA
			Session session = null;
			try {
				session = DatabaseManager.getInstance().openSession();
				// Passer en mode transaction
				session.beginTransaction();

				monitor.beginTask(LocaleManager.instance().getMessage("magicItemSaving"), 1);
				session.save(magicItemModel);
				monitor.worked(1);
				// Commit de la transaction
				session.commit();
			} catch (Exception e) {
				// Rollback de la transaction
				if (session != null)
					session.rollback();
				MessageDialog.openError(null, LocaleManager.instance().getMessage("errorDataSave"), e.getMessage());
			} finally {
				// Fermeture de la session
				if (session != null)
					session.close();
			}
		}
		if(pageMagicItem.getManagedForm() != null)
			pageMagicItem.getManagedForm().dirtyStateChanged();
		if(pageCardMagicItem.getManagedForm() != null)
			pageCardMagicItem.getManagedForm().dirtyStateChanged();
		monitor.done();
	}

	public void doSaveAs() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.ISaveablePart#isSaveAsAllowed()
	 */
	public boolean isSaveAsAllowed() {
		return false;
	}

	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		super.init(site, input);
		super.setPartName(input.getName());
	}
	
	public void setPartName(String name) {
		super.setPartName(name);
	}
}