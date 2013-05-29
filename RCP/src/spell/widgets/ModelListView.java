package spell.widgets;

import java.util.ArrayList;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.ToolBar;

import spell.model.simple.CardModelLabelProvider;
import spell.model.simple.IModelListener;
import spell.model.simple.SimpleModel;
import spell.model.simple.SimpleModelBox;
import spell.model.simple.SimpleModelContentProvider;
import spell.tools.LinkManager;
import spell.viewers.IViewerFilterChangedListener;
import spell.viewers.table.SourceTableFilter;
import spell.views.IListViewListener;



public abstract class ModelListView  extends Composite {

	private Table listEnv = null;
	private ToolBar toolbar = null;
	private TableViewer listViewer = null;
	private ArrayList<IListViewListener> listViewListener;

	public ModelListView(Composite parent, int style, SimpleModelBox modelBox) {
		super(parent, style);
		
		listViewListener = new ArrayList<IListViewListener>();
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.horizontalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.verticalSpacing = 0;
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		this.setLayout(gridLayout);
	
		this.toolbar = new ToolBar(this,SWT.FLAT | SWT.RIGHT);
		this.toolbar.setVisible(false);

		listEnv = new Table(this, SWT.BORDER | SWT.MULTI |  SWT.H_SCROLL | SWT.V_SCROLL);
		listEnv.setLayoutData(gridData);


		listViewer = new TableViewer(listEnv);
		listViewer.setContentProvider(new SimpleModelContentProvider());
		listViewer.setLabelProvider(new CardModelLabelProvider(false));
		listViewer.setInput(modelBox);	
		listViewer.addFilter(SourceTableFilter.instance());
		SourceTableFilter.instance().addListener(new ViewerFilterChangedListener());
		modelBox.addModelListener(new ModelListener());

		Transfer[] transferarray = new Transfer[]{TextTransfer.getInstance()};
		listViewer.addDragSupport(DND.DROP_COPY, transferarray, new DragSourceListener() {
			public void dragFinished(DragSourceEvent event) {}
			public void dragSetData(DragSourceEvent event) {
				IStructuredSelection selection = (IStructuredSelection) listViewer.getSelection();
				Object[] itSel = selection.toArray();
				String ids = "";
				for(Object obj : itSel) {
					SimpleModel model = (SimpleModel)obj;
					ids += model.getClass().getSimpleName() + "_" + model.getId() +";";
				}
				event.data = ids;
			}
			public void dragStart(DragSourceEvent event) {
				event.doit = ! listViewer.getSelection().isEmpty();
			}
		});
		
		listViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				LinkManager.openSimpleModel((SimpleModel) selection.getFirstElement());
			}
		});		
	}
	
	public void addListViewListener(IListViewListener listener) {
		listViewListener.add(listener);
	}
	
	public void removeListViewListener(IListViewListener listener) {
		listViewListener.remove(listener);
	}
	
	public void refresh() {
		int size = getItemCount();
		listViewer.refresh();
		if(size != getItemCount()) //Si modification de la taille apres un refresh du filtre
			doSizeChangedEvent();
	}

	public int getItemCount() {
		return listViewer.getTable().getItemCount();
	}
	
	public void clear() {
		listViewer.getTable().removeAll();
		doSizeChangedEvent();
	}
	
	// Permet de mettre a jour l'arbre en même temps que le modele
	class ViewerFilterChangedListener implements IViewerFilterChangedListener {
		@Override
		public void filterChanged() {
			refresh();		
		}
	}

	
	// Permet de mettre a jour l'arbre en même temps que le modele
	class ModelListener implements IModelListener {
		public void modelChanged(SimpleModel object, String type) {
			if(checkSimpleModelType(object)) {
				if (type.equals(IModelListener.ADDED)) {
					listViewer.add(object);
					listViewer.editElement(object, 1);
					doSizeChangedEvent();
				} else if (type.equals(IModelListener.REMOVED)) {
					listViewer.remove(object);
					doSizeChangedEvent();
				} else if (type.equals(IModelListener.CHANGED)) {
					listViewer.update(object, null);
					//refresh(); //rafraichit le filtre aprés modif de la carte
				}
			}
		}
	}

	protected abstract boolean checkSimpleModelType(SimpleModel object);

	public ToolBar getToolbar() {
		return toolbar;
	}
	
	private void doSizeChangedEvent() {
		for(IListViewListener listener : listViewListener)
			listener.sizeChanged();		
	}

	public TableViewer getListViewer() {
		return listViewer;
	}
}