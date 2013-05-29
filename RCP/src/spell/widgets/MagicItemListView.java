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
import spell.actions.NewMagicItemAction;
import spell.databases.DatabaseManager;
import spell.databases.Session;
import spell.editors.MagicItemFormEditor;
import spell.editors.MagicItemFormEditorInput;
import spell.model.MagicItem;
import spell.model.simple.ISharedModelBoxIds;
import spell.model.simple.MagicItemModelBox;
import spell.model.simple.SharedSimpleModelBox;
import spell.model.simple.SimpleModel;
import spell.services.ServiceFactory;
import spell.tools.LocaleManager;



public class MagicItemListView  extends ModelListView {

	private ToolItem itemDel;
	
	public MagicItemListView(Composite parent, int style, MagicItemModelBox modelBox) {
		super(parent, style, modelBox);
		
		
		ToolItem itemNew = new ToolItem(getToolbar(), SWT.NONE);
		itemNew.setImage(Activator.getImage(Activator.MAGIC_ITEM_ICON_ADD));
		itemNew.setToolTipText(LocaleManager.instance().getMessage("newMagicItem"));
		itemNew.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {}
			public void widgetSelected(SelectionEvent e) {
				NewMagicItemAction.execute();
			}			
		});

		itemDel = new ToolItem(getToolbar(), SWT.NONE);
		itemDel.setImage(Activator.getImage(Activator.MAGIC_ITEM_ICON_DELETE));
		itemDel.setToolTipText(LocaleManager.instance().getMessage("delete"));
		itemDel.setEnabled(false);
		itemDel.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {}
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection) getListViewer().getSelection();
				Object[] itSel = selection.toArray();
				String message = "confirmMagicItemDelete";
				if(itSel.length > 1)
					message = "confirmMagicItemDeletes";
				if(MessageDialog.openConfirm(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), LocaleManager.instance().getMessage("confirm"), LocaleManager.instance().getMessage(message))) {			

					Session session = null;
					try {
						session = DatabaseManager.getInstance().openSession();
						session.beginTransaction();

						for(Object obj : itSel) {
							MagicItem magicItem = (MagicItem)obj;			
							
							IEditorReference editor = getEditorReference(magicItem);
							if(editor != null)
								PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeEditor(editor.getEditor(false), false);
							ServiceFactory.getMagicItemService().delete(magicItem, session);
							SharedSimpleModelBox.instance().get(ISharedModelBoxIds.BOX_MAGIC_ITEM).remove(magicItem, true);
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
			
	private IEditorReference getEditorReference(MagicItem magicItem) throws PartInitException {
		IEditorReference[] editors = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getEditorReferences();
		for(int i = 0; i < editors.length; i++) {
			if(editors[i].getId().equals(MagicItemFormEditor.ID)) {
				MagicItem an = ((MagicItemFormEditorInput)editors[i].getEditorInput()).getMagicItem();
				if(an.getId() == magicItem.getId()) {
					return editors[i];
				}
			}
		}
		return null;
	}

	@Override
	protected boolean checkSimpleModelType(SimpleModel object) {
		return object instanceof MagicItem;
	}

}