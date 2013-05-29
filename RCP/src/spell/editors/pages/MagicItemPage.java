package spell.editors.pages;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import spell.Activator;
import spell.editors.MagicItemFormEditor;
import spell.editors.MagicItemFormEditorInput;
import spell.model.Source;
import spell.model.ItemType;
import spell.model.MagicItem;
import spell.model.Slot;
import spell.services.ServiceFactory;
import spell.tools.ConvertImageManager;
import spell.tools.LocaleManager;
import spell.widgets.ImageCombo;


public class MagicItemPage extends FormPage {

	private Text name;
	private Text originalName;
	private Text pageNumber;
	private Label iconLabel;
	private ImageCombo itemType;
	private ImageCombo slot;
	private ImageCombo source;
	private Text weight;
	private Spinner priceSpinner;
	
	private Spinner constructionCostSpinner;
	private Text constructionRequirements;
	private Text detail;
	private Text casterLevel;
	private Text aura;

	private MagicItem magicItemModel;		
	
	private final static int INPUT_SIZE_COL1 = 150;	
	private final static int ICON_CONTROL_SIZE = 5;	
	private FileDialog fDialog;
	private ConvertImageManager imgManager;

	
	public boolean isDirty() {
		return ((MagicItemFormEditorInput) this.getEditorInput()).getMagicItem().isDirty();
	}

	/*
	public void doSave(IProgressMonitor monitor) {
		super.doSave(monitor);
		// Ouverture d'une session sur la base PACHA
		Session session = null;
		try {
			session = MagicItemDatabaseManager.getInstance().openSession();
			// Passer en mode transaction
			session.beginTransaction();

			monitor.beginTask("Sauvegarde du sort... ", 1);
			session.save(magicItemModel);
			monitor.worked(1);
			// Commit de la transaction
			session.commit();
		} catch (Exception e) {
			// Rollback de la transaction
			if (session != null)
				session.rollback();
			MessageDialog.openError(null, "Erreur sauvegarde des DataBases", e.getMessage());
		} finally {
			// Fermeture de la session
			if (session != null)
				session.close();
		}
		// Recalcul de l'etat du formulaire apres sauvegarde
		this.getManagedForm().dirtyStateChanged();
	}
	*/

	public MagicItemPage(FormEditor editor) {
		super(editor, "pageMagicItem", LocaleManager.instance().getMessage("edit"));	
		if (editor.getEditorInput() instanceof MagicItemFormEditorInput) {
			magicItemModel = ((MagicItemFormEditorInput)editor.getEditorInput()).getMagicItem();
		}
	}

	protected void createFormContent(final IManagedForm managedForm) {
						
		GridData inputGD_COL1 = new GridData(GridData.FILL_HORIZONTAL|GridData.VERTICAL_ALIGN_BEGINNING);
		inputGD_COL1.widthHint = INPUT_SIZE_COL1;
		inputGD_COL1.horizontalIndent = ICON_CONTROL_SIZE;
		inputGD_COL1.heightHint = 13;
					
		imgManager = new ConvertImageManager();
		
		final ScrolledForm form = managedForm.getForm();
		form.setText(LocaleManager.instance().getMessage("magicItem") + " : " + magicItemModel.getTitle());
	
		FormToolkit toolkit = managedForm.getToolkit();
        		
	    Composite body = form.getBody(); 
	    final GridLayout gridLayout = new GridLayout();
        gridLayout.marginWidth = 2;
        gridLayout.marginHeight = 0;
        gridLayout.numColumns = 1;
        body.setLayout(gridLayout);
       
        Composite magicItemMasterComposite = toolkit.createComposite(body, SWT.FILL);
		GridLayout magicItemMasterCompositeLayout = new GridLayout();
		magicItemMasterCompositeLayout.marginWidth = magicItemMasterCompositeLayout.marginHeight = 2;
		magicItemMasterCompositeLayout.numColumns = 1;
        magicItemMasterComposite.setLayout(magicItemMasterCompositeLayout);
		GridData magicItemMasterCompositeLayoutData = new GridData();
		magicItemMasterCompositeLayoutData.horizontalAlignment = magicItemMasterCompositeLayoutData.verticalAlignment = GridData.FILL;
		magicItemMasterCompositeLayoutData.grabExcessHorizontalSpace =  magicItemMasterCompositeLayoutData.grabExcessVerticalSpace = true;
        magicItemMasterComposite.setLayoutData(magicItemMasterCompositeLayoutData);
        

		Composite magicItemInfosComposite = toolkit.createComposite(magicItemMasterComposite, SWT.FILL);
		magicItemInfosComposite.setLayout(gridLayout);
		GridData magicItemInfosCompositeLayoutData = new GridData();
		magicItemInfosCompositeLayoutData.horizontalAlignment = magicItemInfosCompositeLayoutData.verticalAlignment = GridData.FILL;
		magicItemInfosCompositeLayoutData.grabExcessHorizontalSpace =  true;
		magicItemInfosComposite.setLayoutData(magicItemInfosCompositeLayoutData);

//		SECTION INFO
		Section magicItemInfoSection = toolkit.createSection(magicItemInfosComposite, Section.TWISTIE | Section.TITLE_BAR | Section.DESCRIPTION | Section.EXPANDED);
		magicItemInfoSection.setText(LocaleManager.instance().getMessage("informations"));		
		magicItemInfoSection.setDescription(LocaleManager.instance().getMessage("informationsMagicItemInfo"));
		magicItemInfoSection.setLayout(gridLayout);
		GridData magicItemInfoSectionLayoutData = new GridData();
		magicItemInfoSectionLayoutData.horizontalAlignment = GridData.FILL;
		magicItemInfoSectionLayoutData.grabExcessHorizontalSpace = true;
		magicItemInfoSection.setLayoutData(magicItemInfoSectionLayoutData);
		
		Composite magicItemComposite1 = toolkit.createComposite(magicItemInfoSection);
		GridLayout glayout = new GridLayout();
		glayout.marginWidth = glayout.marginHeight = 2;
		glayout.numColumns = 4;
		magicItemComposite1.setLayout(glayout);
		magicItemInfoSection.setClient(magicItemComposite1);

		
		Label label = toolkit.createLabel(magicItemComposite1, LocaleManager.instance().getMessage("icon") + " : ");
		label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		fDialog = new FileDialog(form.getShell(), SWT.OPEN | SWT.SINGLE);
		iconLabel = toolkit.createLabel(magicItemComposite1, "",SWT.RIGHT);
		Image iconMagicItem = Activator.getImage(Activator.getMagicItemImageFolder() +magicItemModel.getImage(), Activator.getMagicItemImageFolder() + Activator.MAGIC_ITEM_NO_ICON);
		iconLabel.setImage(iconMagicItem);
		iconLabel.addMouseListener(new MouseListener() {
			public void mouseDoubleClick(MouseEvent arg0) {
				if(arg0.button > 1) {
					magicItemModel.setImage(Activator.MAGIC_ITEM_NO_ICON);
					magicItemModel.fireModelChanged();
					managedForm.dirtyStateChanged();
					iconLabel.setImage(Activator.getImage(Activator.getMagicItemImageFolder() + magicItemModel.getImage()));
				} else {
					String file = fDialog.open();
			        if (file != null) {
			        	String targetName = "icon_" + magicItemModel.getId() + ".jpg";
			        	if(imgManager.translate(file, Activator.getPath() + Activator.getMagicItemImageFolder() + targetName, ConvertImageManager.RESIZE, Activator.ICON_HEIGHT, Activator.ICON_WIDTH)) {
			        		Activator.reloadImage(Activator.getMagicItemImageFolder()+ targetName);
			        		magicItemModel.setImage(targetName);
			        		magicItemModel.fireModelChanged();
							managedForm.dirtyStateChanged();
							iconLabel.setImage(Activator.getImage(Activator.getMagicItemImageFolder() + magicItemModel.getImage()));	        		
			        	}
			        }
				}
			}
			public void mouseDown(MouseEvent arg0) {}
			public void mouseUp(MouseEvent arg0) {}
		});
		GridData iconLayoutData = new GridData(Activator.ICON_WIDTH+ICON_CONTROL_SIZE, Activator.ICON_HEIGHT);
		iconLayoutData.horizontalSpan = 3;
		iconLabel.setLayoutData(iconLayoutData);

		//Saisie du nom		

		label = toolkit.createLabel(magicItemComposite1, LocaleManager.instance().getMessage("name") + " : ");
		label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		name = toolkit.createText(magicItemComposite1, magicItemModel.getTitle(), SWT.SINGLE);
		name.setText(magicItemModel.getTitle());
		name.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (magicItemModel!=null) {
					magicItemModel.setTitle(name.getText());
					magicItemModel.fireModelChanged();
					managedForm.dirtyStateChanged();
					form.setText(LocaleManager.instance().getMessage("magicItem") + " : " + magicItemModel.getTitle());
					if (getEditor() instanceof MagicItemFormEditor) {
						((MagicItemFormEditor)getEditor()).setPartName(magicItemModel.getTitle());
					}
				}
			}
		});
		name.setLayoutData(inputGD_COL1);

		label = toolkit.createLabel(magicItemComposite1, LocaleManager.instance().getMessage("casterLevel") + " : ");
		label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		casterLevel = toolkit.createText(magicItemComposite1, magicItemModel.getCasterLevel(), SWT.SINGLE);
		casterLevel.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (magicItemModel!=null) {
					magicItemModel.setCasterLevel(casterLevel.getText());
					magicItemModel.fireModelChanged();
					managedForm.dirtyStateChanged();
				}
			}
		});
		casterLevel.setLayoutData(inputGD_COL1);

		label = toolkit.createLabel(magicItemComposite1, LocaleManager.instance().getMessage("originalName") + " : ");
		label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		originalName = toolkit.createText(magicItemComposite1, magicItemModel.getOriginalName(), SWT.SINGLE);
		createControlDecoration(originalName, LocaleManager.instance().getMessage("originalNameInfo"));
		originalName.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (magicItemModel!=null) {
					magicItemModel.setOriginalName(originalName.getText());
					magicItemModel.fireModelChanged();
					managedForm.dirtyStateChanged();
				}
			}
		});
		originalName.setLayoutData(inputGD_COL1);

		label = toolkit.createLabel(magicItemComposite1, LocaleManager.instance().getMessage("aura") + " : ");
		label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		aura = toolkit.createText(magicItemComposite1, magicItemModel.getAura(), SWT.SINGLE);
		aura.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (magicItemModel!=null) {
					magicItemModel.setAura(aura.getText());
					magicItemModel.fireModelChanged();
					managedForm.dirtyStateChanged();
				}
			}
		});
		aura.setLayoutData(inputGD_COL1);

		label = toolkit.createLabel(magicItemComposite1, LocaleManager.instance().getMessage("itemType") + " : ");
		label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		itemType = new ImageCombo(magicItemComposite1, SWT.BORDER);
		itemType.setEditable(false);
		itemType.setBackground(name.getBackground());
		itemType.setLayoutData(inputGD_COL1);
		itemType.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent arg0) {}
			public void widgetSelected(SelectionEvent arg0) {
				if (magicItemModel!=null) {
					magicItemModel.setItemTypeId(ServiceFactory.getItemTypeService().getCached()[itemType.getSelectionIndex()].getId());
					magicItemModel.fireModelChanged();
					managedForm.dirtyStateChanged();
				}				
			}
		});
		for(ItemType itemTypeItem : ServiceFactory.getItemTypeService().getCached()) {
			itemType.add(itemTypeItem.getTitle(), Activator.getImage(Activator.FOLDER_IMAGES + itemTypeItem.getImage()));
			if(itemTypeItem.getId() == magicItemModel.getItemTypeId())
				itemType.select(itemType.getItemCount()-1);
		}

		label = toolkit.createLabel(magicItemComposite1, LocaleManager.instance().getMessage("price") + " (" + LocaleManager.instance().getMessage("gp") + ") : ");
		label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		priceSpinner = new Spinner (magicItemComposite1, SWT.BORDER);
		priceSpinner.setMinimum(0);
		priceSpinner.setMaximum(Integer.MAX_VALUE);
		priceSpinner.setSelection(magicItemModel.getPrice());
		priceSpinner.setIncrement(10);
		priceSpinner.setPageIncrement(500);
		priceSpinner.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (magicItemModel!=null) {
					magicItemModel.setPrice(priceSpinner.getSelection());
					magicItemModel.fireModelChanged();
					managedForm.dirtyStateChanged();
				}
			}
		});
		priceSpinner.setLayoutData(inputGD_COL1);

		label = toolkit.createLabel(magicItemComposite1, LocaleManager.instance().getMessage("slot") + " : ");
		label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		slot = new ImageCombo(magicItemComposite1, SWT.BORDER);
		slot.setEditable(false);
		slot.setBackground(name.getBackground());
		slot.setLayoutData(inputGD_COL1);
		slot.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent arg0) {}
			public void widgetSelected(SelectionEvent arg0) {
				if (magicItemModel!=null) {
					magicItemModel.setSlotId(ServiceFactory.getSlotService().getCached()[slot.getSelectionIndex()].getId());
					magicItemModel.fireModelChanged();
					managedForm.dirtyStateChanged();
				}				
			}
		});
		for(Slot slotItem : ServiceFactory.getSlotService().getCached()) {
			slot.add(slotItem.getTitle(), Activator.getImage(Activator.FOLDER_IMAGES + slotItem.getImage()));
			if(slotItem.getId() == magicItemModel.getSlotId())
				slot.select(slot.getItemCount()-1);
		}							

		label = toolkit.createLabel(magicItemComposite1, LocaleManager.instance().getMessage("weight") + " : ");
		label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		weight = toolkit.createText(magicItemComposite1, magicItemModel.getWeight(), SWT.SINGLE);
		weight.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (magicItemModel!=null) {
					magicItemModel.setWeight(weight.getText());
					magicItemModel.fireModelChanged();
					managedForm.dirtyStateChanged();
				}
			}
		});
		weight.setLayoutData(inputGD_COL1);

		label = toolkit.createLabel(magicItemComposite1, LocaleManager.instance().getMessage("sourceName") + " : ");
		label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		source = new ImageCombo(magicItemComposite1, SWT.BORDER);
		source.setEditable(false);
		source.setBackground(name.getBackground());
		source.setLayoutData(inputGD_COL1);
		source.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent arg0) {}
			public void widgetSelected(SelectionEvent arg0) {
				if (magicItemModel!=null) {
					magicItemModel.setSourceId(ServiceFactory.getSourceService().getCached()[source.getSelectionIndex()].getId());
					magicItemModel.fireModelChanged();
					managedForm.dirtyStateChanged();
				}				
			}
		});
		for(Source sourceItem : ServiceFactory.getSourceService().getCached()) {
			source.add(sourceItem.getTitle(), Activator.getImage(Activator.FOLDER_IMAGES + sourceItem.getImage()));
			if(sourceItem.getId() == magicItemModel.getSourceId())
				source.select(source.getItemCount()-1);
		}

		label = toolkit.createLabel(magicItemComposite1, LocaleManager.instance().getMessage("pageNumber") + " : ");
		label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		pageNumber = toolkit.createText(magicItemComposite1, magicItemModel.getPage(), SWT.SINGLE);
		pageNumber.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (magicItemModel!=null) {
					magicItemModel.setPage(pageNumber.getText());
					magicItemModel.fireModelChanged();
					managedForm.dirtyStateChanged();
				}
			}
		});
		pageNumber.setLayoutData(inputGD_COL1);

		
//		SECTION CONSTRUCTION
		Section magicItemConstructionSection = toolkit.createSection(magicItemMasterComposite, Section.TWISTIE | Section.TITLE_BAR | Section.EXPANDED | SWT.FILL);
		magicItemConstructionSection.setLayout(gridLayout);
		magicItemConstructionSection.setText(LocaleManager.instance().getMessage("construction"));	
		Composite constrcutionComposite = toolkit.createComposite(magicItemConstructionSection);
		GridLayout constructionLayout = new GridLayout();
		constructionLayout.marginWidth = constructionLayout.marginHeight = 2;
		constructionLayout.numColumns = 2;
		constrcutionComposite.setLayout(constructionLayout);
		GridData sectionConstructionLayoutData = new GridData();
		sectionConstructionLayoutData.horizontalAlignment = GridData.FILL;
		sectionConstructionLayoutData.grabExcessHorizontalSpace = true;
		magicItemConstructionSection.setLayoutData(sectionConstructionLayoutData);
		magicItemConstructionSection.setClient(constrcutionComposite);

// 		SECTION CONSTRUCTION
 
		label = toolkit.createLabel(constrcutionComposite, LocaleManager.instance().getMessage("requirements") + " : ");
		label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		constructionRequirements = toolkit.createText(constrcutionComposite, magicItemModel.getConstructionRequirements(), SWT.MULTI | SWT.V_SCROLL | SWT.WRAP);
		constructionRequirements.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (magicItemModel!=null) {
					magicItemModel.setConstructionRequirements(constructionRequirements.getText());
					magicItemModel.fireModelChanged();
					managedForm.dirtyStateChanged();
				}
			}
		});
		GridData constructionGridData = new GridData();
		constructionGridData.horizontalAlignment = constructionGridData.verticalAlignment = GridData.FILL;
		constructionGridData.grabExcessHorizontalSpace = constructionGridData.grabExcessVerticalSpace = true;
		constructionGridData.heightHint = 30;
		constructionGridData.widthHint = 60;
		constructionGridData.horizontalIndent = ICON_CONTROL_SIZE;
		constructionRequirements.setLayoutData(constructionGridData);

		label = toolkit.createLabel(constrcutionComposite, LocaleManager.instance().getMessage("cost") + " (" + LocaleManager.instance().getMessage("gp") + ") : ");
		label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		constructionCostSpinner = new Spinner (constrcutionComposite, SWT.BORDER);
		constructionCostSpinner.setMinimum(0);
		constructionCostSpinner.setMaximum(Integer.MAX_VALUE);
		constructionCostSpinner.setSelection(magicItemModel.getConstructionCost());
		constructionCostSpinner.setIncrement(10);
		constructionCostSpinner.setPageIncrement(500);
		constructionCostSpinner.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (magicItemModel!=null) {
					magicItemModel.setConstructionCost(constructionCostSpinner.getSelection());
					magicItemModel.fireModelChanged();
					managedForm.dirtyStateChanged();
				}
			}
		});
		constructionCostSpinner.setLayoutData(inputGD_COL1);
      
//		SECTION DETAIL
		Section magicItemDetailSection = toolkit.createSection(magicItemMasterComposite, Section.TWISTIE | Section.TITLE_BAR | Section.EXPANDED | SWT.FILL);
		magicItemDetailSection.setLayout(gridLayout);
		magicItemDetailSection.setText(LocaleManager.instance().getMessage("description"));	
		GridData sectionDetailLayoutData = new GridData();
		sectionDetailLayoutData.horizontalAlignment = sectionDetailLayoutData.verticalAlignment = GridData.FILL;
		sectionDetailLayoutData.grabExcessHorizontalSpace =  sectionDetailLayoutData.grabExcessVerticalSpace = true;
		magicItemDetailSection.setLayoutData(sectionDetailLayoutData);
		Composite detailComposite = toolkit.createComposite(magicItemDetailSection);
		GridLayout detailLayout = new GridLayout();
		detailLayout.marginWidth = detailLayout.marginHeight = 2;
		detailComposite.setLayout(detailLayout);
		magicItemDetailSection.setClient(detailComposite);
		
		detail = toolkit.createText(detailComposite, magicItemModel.getDetail(), SWT.MULTI | SWT.V_SCROLL | SWT.WRAP);
		detail.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (magicItemModel!=null) {
					magicItemModel.setDetail(detail.getText());
					magicItemModel.fireModelChanged();
					managedForm.dirtyStateChanged();
				}
			}
		});
		GridData detailGridData = new GridData();
		detailGridData.horizontalAlignment = detailGridData.verticalAlignment = GridData.FILL;
		detailGridData.grabExcessHorizontalSpace = detailGridData.grabExcessVerticalSpace = true;
		detailGridData.heightHint = 60;
		detailGridData.widthHint = 60;
		detail.setLayoutData(detailGridData);
		

		toolkit.paintBordersFor(body);
	}
	
	public void dispose() {
		super.dispose();
	}
	
	private void createControlDecoration(Control item, String info) {
		ControlDecoration decoratedControl = new ControlDecoration(item, SWT.LEFT | SWT.TOP);
		decoratedControl.setShowOnlyOnFocus(true);
		decoratedControl.setDescriptionText(info);	
		decoratedControl.setImage(Activator.getImage(Activator.ICON_INFO));		

	}
}