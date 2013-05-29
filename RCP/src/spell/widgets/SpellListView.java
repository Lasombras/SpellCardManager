package spell.widgets;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import spell.Activator;
import spell.actions.NewSpellAction;
import spell.databases.DatabaseManager;
import spell.databases.Session;
import spell.editors.SpellFormEditor;
import spell.editors.SpellFormEditorInput;
import spell.model.Spell;
import spell.model.simple.ISharedModelBoxIds;
import spell.model.simple.SharedSimpleModelBox;
import spell.model.simple.SimpleModel;
import spell.model.simple.SpellModelBox;
import spell.services.ServiceFactory;
import spell.tools.LocaleManager;



public class SpellListView extends ModelListView {

	private ToolItem itemDel = null;

	public SpellListView(Composite parent, int style, SpellModelBox modelBox) {
		super(parent, style, modelBox);
		
		//Construction du menu
		ToolItem itemNew = new ToolItem(getToolbar(), SWT.NONE);
		itemNew.setImage(Activator.getImage(Activator.SPELL_ICON_ADD));
		itemNew.setToolTipText(LocaleManager.instance().getMessage("newSpell"));
		itemNew.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {}
			public void widgetSelected(SelectionEvent e) {
				NewSpellAction.execute();
			}			
		});

		itemDel = new ToolItem(getToolbar(), SWT.NONE);
		itemDel.setImage(Activator.getImage(Activator.SPELL_ICON_DELETE));
		itemDel.setToolTipText(LocaleManager.instance().getMessage("delete"));
		itemDel.setEnabled(false);
		itemDel.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {}
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection) getListViewer().getSelection();
				Object[] itSel = selection.toArray();
				String message = "confirmSpellDelete";
				if(itSel.length > 1)
					message = "confirmSpellDeletes";
				if(MessageDialog.openConfirm(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), LocaleManager.instance().getMessage("confirm"), LocaleManager.instance().getMessage(message))) {			

					Session session = null;
					try {
						session = DatabaseManager.getInstance().openSession();
						session.beginTransaction();

						for(Object obj : itSel) {
							Spell spellModel = (Spell)obj;						
							IEditorReference editor = getEditorReference(spellModel);
							if(editor != null)
								PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeEditor(editor.getEditor(false), false);
							ServiceFactory.getSpellService().delete(spellModel, session);
							SharedSimpleModelBox.instance().get(ISharedModelBoxIds.BOX_SPELL).remove(spellModel, true);
						}
						session.commit();
					} catch (Exception exception) {
						if(session != null) session.rollback();
						exception.printStackTrace();
					} finally {
						if(session != null) session.close();
					}
				}
			}			
		});
		
		getToolbar().setVisible(true);

		getListViewer().addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				itemDel.setEnabled(!event.getSelection().isEmpty());
			}			
		});

	}

	private IEditorReference getEditorReference(Spell spell) throws PartInitException {
		IEditorReference[] editors = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getEditorReferences();
		for(int i = 0; i < editors.length; i++) {
			if(editors[i].getId().equals(SpellFormEditor.ID)) {
				Spell an = ((SpellFormEditorInput)editors[i].getEditorInput()).getSpell();
				if(an.getId() == spell.getId()) {
					return editors[i];
				}
			}
		}
		return null;
	}

	@Override
	protected boolean checkSimpleModelType(SimpleModel object) {
		return object instanceof Spell;
	}	
}