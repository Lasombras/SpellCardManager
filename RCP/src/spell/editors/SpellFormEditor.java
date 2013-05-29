package spell.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;

import spell.databases.DatabaseManager;
import spell.databases.Session;
import spell.editors.pages.SpellCardPage;
import spell.editors.pages.SpellPage;
import spell.editors.pages.SpellViewPage;
import spell.model.Spell;
import spell.tools.LocaleManager;


public class SpellFormEditor extends FormEditor {

	public final static String ID = "Spell.spellFormEditor";

	public SpellPage pageSpell;
	public SpellViewPage viewPageSpell;
	public SpellCardPage pageCardSpell;

	public SpellFormEditor() {
	}
	
	public boolean isDirty() {
		return ((SpellFormEditorInput) this.getEditorInput()).getSpell().isDirty();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.forms.editor.FormEditor#addPages()
	 */
	protected void addPages() {
		try {
			viewPageSpell = new SpellViewPage(this);
			addPage(viewPageSpell);			
			pageSpell = new SpellPage(this);
			addPage(pageSpell);
			pageCardSpell = new SpellCardPage(this);
			addPage(pageCardSpell);
			if(getEditorInput().getName().equals(""))
				setActivePage(1);
		} catch (PartInitException e) {
			//
		}
	}

	public void doSave(IProgressMonitor monitor) {
			Spell spellModel = ((SpellFormEditorInput)this.getEditorInput()).getSpell();
			if(spellModel.isDirty()) {
				// Ouverture d'une session sur la base PACHA
				Session session = null;
				try {
					session = DatabaseManager.getInstance().openSession();
					// Passer en mode transaction
					session.beginTransaction();
	
					monitor.beginTask(LocaleManager.instance().getMessage("spellSaving"), 1);
					session.save(spellModel);
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
			if(pageSpell.getManagedForm() != null)
				pageSpell.getManagedForm().dirtyStateChanged();
			if(pageCardSpell.getManagedForm() != null)
				pageCardSpell.getManagedForm().dirtyStateChanged();
		//if (pageSpell.isDirty())
		//	pageSpell.doSave(monitor);
		//if (pageSecurity.isDirty())
		//	pageSecurity.doSave(monitor);
		//if (pageFolder.isDirty())
		//	pageFolder.doSave(monitor);
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