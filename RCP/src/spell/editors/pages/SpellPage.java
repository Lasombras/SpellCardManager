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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import spell.Activator;
import spell.editors.SpellFormEditor;
import spell.editors.SpellFormEditorInput;
import spell.model.Source;
import spell.model.Component;
import spell.model.Level;
import spell.model.School;
import spell.model.Spell;
import spell.services.ServiceFactory;
import spell.tools.ConvertImageManager;
import spell.tools.LocaleManager;
import spell.viewers.table.ILevelListViewer;
import spell.viewers.table.TableViewerLevel;
import spell.widgets.ImageCombo;


public class SpellPage extends FormPage {

	private Text name;
	private Text originalName;
	private Text castingTime;
	private Text range;
	private Text target;
	private Text effect;
	private Text area;
	private Text descriptor;
	private Text material;
	private Text duration;
	private Text savingThrow;
	private Text pageNumber;
	private Button spellResistance;
	private TableViewerLevel tableLevel;
	private Label iconLabel;
	private ImageCombo school;
	private ImageCombo source;

	private Text detail;

	private Spell spellModel;		
	
	private final static int INPUT_SIZE_COL1 = 290;	
	private final static int ICON_CONTROL_SIZE = 5;	
	private FileDialog fDialog;
	private ConvertImageManager imgManager;

	
	public boolean isDirty() {
		return ((SpellFormEditorInput) this.getEditorInput()).getSpell().isDirty();
	}

	/*
	public void doSave(IProgressMonitor monitor) {
		super.doSave(monitor);
		// Ouverture d'une session sur la base PACHA
		Session session = null;
		try {
			session = SpellDatabaseManager.getInstance().openSession();
			// Passer en mode transaction
			session.beginTransaction();

			monitor.beginTask("Sauvegarde du sort... ", 1);
			session.save(spellModel);
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

	public SpellPage(FormEditor editor) {
		super(editor, "pageSpell", LocaleManager.instance().getMessage("edit"));	
		if (editor.getEditorInput() instanceof SpellFormEditorInput) {
			spellModel = ((SpellFormEditorInput)editor.getEditorInput()).getSpell();
		}
	}

	protected void createFormContent(final IManagedForm managedForm) {
						
		GridData inputControlGD_COL1 = new GridData(GridData.FILL_HORIZONTAL|GridData.VERTICAL_ALIGN_BEGINNING);
		inputControlGD_COL1.widthHint = INPUT_SIZE_COL1 + ICON_CONTROL_SIZE;

		GridData inputGD_COL1 = new GridData(GridData.FILL_HORIZONTAL|GridData.VERTICAL_ALIGN_BEGINNING);
		inputGD_COL1.widthHint = INPUT_SIZE_COL1;
		inputGD_COL1.horizontalIndent = ICON_CONTROL_SIZE;
		inputGD_COL1.heightHint = 18;
			
		GridData tableGD_COL1 = new GridData(GridData.FILL_HORIZONTAL|GridData.VERTICAL_ALIGN_BEGINNING);
		tableGD_COL1.widthHint = INPUT_SIZE_COL1;
		tableGD_COL1.horizontalIndent = ICON_CONTROL_SIZE;
		tableGD_COL1.heightHint = 65;
		
		imgManager = new ConvertImageManager();
		
		final ScrolledForm form = managedForm.getForm();
		form.setText(LocaleManager.instance().getMessage("spell") + " : " + spellModel.getTitle());
	
		FormToolkit toolkit = managedForm.getToolkit();
        		
	    Composite body = form.getBody(); 
	    final GridLayout gridLayout = new GridLayout();
        gridLayout.marginWidth = 2;
        gridLayout.marginHeight = 0;
        gridLayout.numColumns = 1;
        body.setLayout(gridLayout);
       
        Composite spellMasterComposite = toolkit.createComposite(body, SWT.FILL);
		GridLayout spellMasterCompositeLayout = new GridLayout();
		spellMasterCompositeLayout.marginWidth = spellMasterCompositeLayout.marginHeight = 2;
		spellMasterCompositeLayout.numColumns = 2;
        spellMasterComposite.setLayout(spellMasterCompositeLayout);
		GridData spellMasterCompositeLayoutData = new GridData();
		spellMasterCompositeLayoutData.horizontalAlignment = spellMasterCompositeLayoutData.verticalAlignment = GridData.FILL;
		spellMasterCompositeLayoutData.grabExcessHorizontalSpace =  spellMasterCompositeLayoutData.grabExcessVerticalSpace = true;
        spellMasterComposite.setLayoutData(spellMasterCompositeLayoutData);
        

		Composite spellInfosComposite = toolkit.createComposite(spellMasterComposite, SWT.FILL);
		spellInfosComposite.setLayout(gridLayout);
		GridData spellInfosCompositeLayoutData = new GridData();
		spellInfosCompositeLayoutData.horizontalAlignment = spellInfosCompositeLayoutData.verticalAlignment = GridData.FILL;
		spellInfosCompositeLayoutData.grabExcessHorizontalSpace =  true;
		spellInfosComposite.setLayoutData(spellInfosCompositeLayoutData);

//		SECTION INFO
		Section spellInfoSection = toolkit.createSection(spellInfosComposite, Section.TWISTIE | Section.TITLE_BAR | Section.DESCRIPTION | Section.EXPANDED);
		spellInfoSection.setText(LocaleManager.instance().getMessage("informations"));		
		spellInfoSection.setDescription(LocaleManager.instance().getMessage("informationsInfo"));
		spellInfoSection.setLayout(gridLayout);
		GridData spellInfoSectionLayoutData = new GridData();
		spellInfoSectionLayoutData.horizontalAlignment = GridData.FILL;
		spellInfoSectionLayoutData.grabExcessHorizontalSpace = true;
		spellInfoSection.setLayoutData(spellInfoSectionLayoutData);

		Composite spellComposite = toolkit.createComposite(spellInfoSection);
		TableWrapLayout spellLayout = new TableWrapLayout();
		spellLayout.topMargin = 5;
		spellLayout.leftMargin = 5;
		spellLayout.rightMargin = 2;
		spellLayout.bottomMargin = 2;
		spellLayout.numColumns = 1;
		spellComposite.setLayout(spellLayout);
		spellInfoSection.setClient(spellComposite);
		
		Section spellSection1 = toolkit.createSection(spellComposite, Section.NO_TITLE | Section.EXPANDED);		
		Composite spellComposite1 = toolkit.createComposite(spellSection1);
		GridLayout glayout = new GridLayout();
		glayout.marginWidth = glayout.marginHeight = 2;
		glayout.numColumns = 2;
		spellComposite1.setLayout(glayout);
		spellSection1.setClient(spellComposite1);

		
		Label label = toolkit.createLabel(spellComposite1, LocaleManager.instance().getMessage("icon") + " : ");
		label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		fDialog = new FileDialog(form.getShell(), SWT.OPEN | SWT.SINGLE);
		iconLabel = toolkit.createLabel(spellComposite1, "",SWT.RIGHT);
		Image iconSpell = Activator.getImage(Activator.getSpellImageFolder() +spellModel.getImage(), Activator.getSpellImageFolder() + Activator.SPELL_NO_ICON);
		iconLabel.setImage(iconSpell);
		iconLabel.addMouseListener(new MouseListener() {
			public void mouseDoubleClick(MouseEvent arg0) {
				if(arg0.button > 1) {
					spellModel.setImage(Activator.SPELL_NO_ICON);
					spellModel.fireModelChanged();
					managedForm.dirtyStateChanged();
					iconLabel.setImage(Activator.getImage(Activator.getMagicItemImageFolder() + spellModel.getImage()));
				} else {
					String file = fDialog.open();
			        if (file != null) {
			        	String targetName = "icon_" + spellModel.getId() + ".jpg";
			        	if(imgManager.translate(file, Activator.getPath() + Activator.getSpellImageFolder() + targetName, ConvertImageManager.RESIZE, Activator.ICON_HEIGHT, Activator.ICON_WIDTH)) {
			        		Activator.reloadImage(Activator.getSpellImageFolder()+ targetName);
			        		spellModel.setImage(targetName);
			        		spellModel.fireModelChanged();
							managedForm.dirtyStateChanged();
							iconLabel.setImage(Activator.getImage(Activator.getSpellImageFolder() + spellModel.getImage()));	        		
			        	}
			        }
				}
			}
			public void mouseDown(MouseEvent arg0) {}
			public void mouseUp(MouseEvent arg0) {}
		});
		iconLabel.setLayoutData(new GridData(Activator.ICON_WIDTH+ICON_CONTROL_SIZE, Activator.ICON_HEIGHT));

		//Saisie du nom		

		label = toolkit.createLabel(spellComposite1, LocaleManager.instance().getMessage("name") + " : ");
		label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		name = toolkit.createText(spellComposite1, spellModel.getTitle(), SWT.SINGLE);
		name.setText(spellModel.getTitle());
		name.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (spellModel!=null) {
					spellModel.setTitle(name.getText());
					spellModel.fireModelChanged();
					managedForm.dirtyStateChanged();
					form.setText(LocaleManager.instance().getMessage("spell") + " : " + spellModel.getTitle());
					if (getEditor() instanceof SpellFormEditor) {
						((SpellFormEditor)getEditor()).setPartName(spellModel.getTitle());
					}
				}
			}
		});
		name.setLayoutData(inputGD_COL1);
		
		
		label = toolkit.createLabel(spellComposite1, LocaleManager.instance().getMessage("originalName") + " : ");
		label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		originalName = toolkit.createText(spellComposite1, spellModel.getOriginalName(), SWT.SINGLE);
		createControlDecoration(originalName, LocaleManager.instance().getMessage("originalNameInfo"));
		originalName.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (spellModel!=null) {
					spellModel.setOriginalName(originalName.getText());
					spellModel.fireModelChanged();
					managedForm.dirtyStateChanged();
				}
			}
		});
		originalName.setLayoutData(inputGD_COL1);

		label = toolkit.createLabel(spellComposite1, LocaleManager.instance().getMessage("sourceName") + " : ");
		label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		source = new ImageCombo(spellComposite1, SWT.BORDER);
		source.setEditable(false);
		source.setBackground(name.getBackground());
		source.setLayoutData(inputGD_COL1);
		source.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent arg0) {}
			public void widgetSelected(SelectionEvent arg0) {
				if (spellModel!=null) {
					spellModel.setSourceId(ServiceFactory.getSourceService().getCached()[source.getSelectionIndex()].getId());
					spellModel.fireModelChanged();
					managedForm.dirtyStateChanged();
				}				
			}
		});
		for(Source sourceItem : ServiceFactory.getSourceService().getCached()) {
			source.add(sourceItem.getTitle(), Activator.getImage(Activator.FOLDER_IMAGES + sourceItem.getImage()));
			if(sourceItem.getId() == spellModel.getSourceId())
				source.select(source.getItemCount()-1);
		}

		
		
		label = toolkit.createLabel(spellComposite1, LocaleManager.instance().getMessage("pageNumber") + " : ");
		label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		pageNumber = toolkit.createText(spellComposite1, spellModel.getPage(), SWT.SINGLE);
		pageNumber.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (spellModel!=null) {
					spellModel.setPage(pageNumber.getText());
					spellModel.fireModelChanged();
					managedForm.dirtyStateChanged();
				}
			}
		});
		pageNumber.setLayoutData(inputGD_COL1);

		label = toolkit.createLabel(spellComposite1, LocaleManager.instance().getMessage("school") + " : ");
		label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		school = new ImageCombo(spellComposite1, SWT.BORDER);
		school.setEditable(false);
		school.setBackground(name.getBackground());
		school.setLayoutData(inputGD_COL1);
		school.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent arg0) {}
			public void widgetSelected(SelectionEvent arg0) {
				if (spellModel!=null) {
					spellModel.setSchoolId(ServiceFactory.getSchoolService().getCached()[school.getSelectionIndex()].getId());
					spellModel.fireModelChanged();
					managedForm.dirtyStateChanged();
				}				
			}
		});
		for(School schoolItem : ServiceFactory.getSchoolService().getCached()) {
			school.add(schoolItem.getTitle(), Activator.getImage(Activator.FOLDER_IMAGES + schoolItem.getImage()));
			if(schoolItem.getId() == spellModel.getSchoolId())
				school.select(school.getItemCount()-1);
		}
		
		label = toolkit.createLabel(spellComposite1, LocaleManager.instance().getMessage("descriptor") + " : ");
		label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		descriptor = toolkit.createText(spellComposite1, spellModel.getDescriptor(), SWT.SINGLE);
		createControlDecoration(descriptor, LocaleManager.instance().getMessage("descriptorInfo"));
		descriptor.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (spellModel!=null) {
					spellModel.setDescriptor(descriptor.getText());
					spellModel.fireModelChanged();
					managedForm.dirtyStateChanged();
				}
			}
		});
		descriptor.setLayoutData(inputGD_COL1);

		label = toolkit.createLabel(spellComposite1, LocaleManager.instance().getMessage("castingTime") + " : ");
		label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		castingTime = toolkit.createText(spellComposite1, spellModel.getCastingTime(), SWT.SINGLE);
		castingTime.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (spellModel!=null) {
					spellModel.setCastingTime(castingTime.getText());
					spellModel.fireModelChanged();
					managedForm.dirtyStateChanged();
				}
			}
		});
		castingTime.setLayoutData(inputGD_COL1);
		
		label = toolkit.createLabel(spellComposite1, LocaleManager.instance().getMessage("material") + " : ");
		label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		material = toolkit.createText(spellComposite1, spellModel.getMaterial(), SWT.SINGLE);
		createControlDecoration(material, LocaleManager.instance().getMessage("materialInfo"));
		material.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (spellModel!=null) {
					spellModel.setMaterial(material.getText());
					spellModel.fireModelChanged();
					managedForm.dirtyStateChanged();
				}
			}
		});
		material.setLayoutData(inputGD_COL1);

		label = toolkit.createLabel(spellComposite1, LocaleManager.instance().getMessage("range") + " : ");
		label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		range = toolkit.createText(spellComposite1, spellModel.getRange(), SWT.SINGLE);
		range.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (spellModel!=null) {
					spellModel.setRange(range.getText());
					spellModel.fireModelChanged();
					managedForm.dirtyStateChanged();
				}
			}
		});
		range.setLayoutData(inputGD_COL1);

		label = toolkit.createLabel(spellComposite1, LocaleManager.instance().getMessage("target") + " : ");
		label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		target = toolkit.createText(spellComposite1, spellModel.getTarget(), SWT.SINGLE);
		target.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (spellModel!=null) {
					spellModel.setTarget(target.getText());
					spellModel.fireModelChanged();
					managedForm.dirtyStateChanged();
				}
			}
		});
		target.setLayoutData(inputGD_COL1);
		
		label = toolkit.createLabel(spellComposite1, LocaleManager.instance().getMessage("effect") + " : ");
		label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		effect = toolkit.createText(spellComposite1, spellModel.getEffect(), SWT.SINGLE);
		effect.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (spellModel!=null) {
					spellModel.setEffect(effect.getText());
					spellModel.fireModelChanged();
					managedForm.dirtyStateChanged();
				}
			}
		});
		effect.setLayoutData(inputGD_COL1);

		label = toolkit.createLabel(spellComposite1, LocaleManager.instance().getMessage("area") + " : ");
		label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		area = toolkit.createText(spellComposite1, spellModel.getArea(), SWT.SINGLE);
		area.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (spellModel!=null) {
					spellModel.setArea(area.getText());
					spellModel.fireModelChanged();
					managedForm.dirtyStateChanged();
				}
			}
		});
		area.setLayoutData(inputGD_COL1);

		label = toolkit.createLabel(spellComposite1, LocaleManager.instance().getMessage("duration") + " : ");
		label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		duration = toolkit.createText(spellComposite1, spellModel.getDuration(), SWT.SINGLE);
		duration.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (spellModel!=null) {
					spellModel.setDuration(duration.getText());
					spellModel.fireModelChanged();
					managedForm.dirtyStateChanged();
				}
			}
		});
		duration.setLayoutData(inputGD_COL1);
		
		label = toolkit.createLabel(spellComposite1, LocaleManager.instance().getMessage("savingThrow") + " : ");
		label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		savingThrow = toolkit.createText(spellComposite1, spellModel.getSavingThrow(), SWT.SINGLE);
		savingThrow.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (spellModel!=null) {
					spellModel.setSavingThrow(savingThrow.getText());
					spellModel.fireModelChanged();
					managedForm.dirtyStateChanged();
				}
			}
		});
		savingThrow.setLayoutData(inputGD_COL1);
		
		label = toolkit.createLabel(spellComposite1, LocaleManager.instance().getMessage("spellResistance") + " : ");
		label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		spellResistance = toolkit.createButton(spellComposite1, "", SWT.CHECK);
		spellResistance.setSelection(spellModel.isSpellResistance());
		spellResistance.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
			public void widgetSelected(SelectionEvent arg0) {
				if (spellModel!=null) {
					spellModel.setSpellResistance(spellResistance.getSelection());
					spellModel.fireModelChanged();
					managedForm.dirtyStateChanged();
				}
			}
		});
		spellResistance.setLayoutData(inputGD_COL1);
		
        Composite rightComposite = toolkit.createComposite(spellMasterComposite, SWT.FILL);
		GridLayout rightCompositeLayout = new GridLayout();
		rightCompositeLayout.marginWidth = 2;
		rightCompositeLayout.marginHeight = 0;
		rightComposite.setLayout(rightCompositeLayout);
		GridData rightCompositeLayoutData = new GridData();
		rightCompositeLayoutData.horizontalAlignment = rightCompositeLayoutData.verticalAlignment = GridData.FILL;
		rightCompositeLayoutData.grabExcessHorizontalSpace =  true;
		rightComposite.setLayoutData(rightCompositeLayoutData);

		
//		SECTION LEVEL
		Section spellLevelSection = toolkit.createSection(rightComposite, Section.TWISTIE | Section.TITLE_BAR | Section.DESCRIPTION | Section.EXPANDED);
		spellLevelSection.setText(LocaleManager.instance().getMessage("spellLevel"));
		spellLevelSection.setDescription(LocaleManager.instance().getMessage("spellLevelClass"));
		spellLevelSection.setLayout(gridLayout);
		GridData spellLevelSectionLayoutData = new GridData();
		spellLevelSectionLayoutData.horizontalAlignment = GridData.FILL;
		spellLevelSectionLayoutData.grabExcessHorizontalSpace = true;
		spellLevelSectionLayoutData.verticalAlignment = GridData.BEGINNING;
		spellLevelSection.setLayoutData(spellLevelSectionLayoutData);

		Composite test = toolkit.createComposite(spellLevelSection);
		Level[] levelsTab = new Level[spellModel.getLevels().size()];
		for(int i = 0; i < spellModel.getLevels().size(); i++)
			levelsTab[i] = spellModel.getLevels().get(i);
		tableLevel = new TableViewerLevel(test, levelsTab, ServiceFactory.getPlayerClassService().getCached(), 120);
		tableLevel.getLevelList().addChangeListener(new ILevelListViewer() {
			public void addLevel(Level level) {
				spellModel.addLevel(level);
				spellModel.fireModelChanged();
				managedForm.dirtyStateChanged();
			}

			public void removeLevel(Level level) {
				spellModel.removeLevel(level);
				spellModel.fireModelChanged();
				managedForm.dirtyStateChanged();
			}

			public void updateLevel(Level level) {
				spellModel.fireModelChanged();
				managedForm.dirtyStateChanged();
			}			
		});
		spellLevelSection.setClient(test);
	  
		
//		SECTION COMPONENTS
		Section spellComponentSection = toolkit.createSection(rightComposite, Section.TWISTIE | Section.TITLE_BAR | Section.DESCRIPTION | Section.EXPANDED);
		spellComponentSection.setText(LocaleManager.instance().getMessage("components"));
		spellComponentSection.setDescription(LocaleManager.instance().getMessage("componentsInfo"));
		spellComponentSection.setLayout(gridLayout);
		GridData spellComponentSectionLayoutData = new GridData();
		spellComponentSectionLayoutData.horizontalAlignment = GridData.FILL;
		spellComponentSectionLayoutData.grabExcessHorizontalSpace = true;
		spellComponentSectionLayoutData.verticalAlignment = GridData.BEGINNING;
		spellComponentSection.setLayoutData(spellComponentSectionLayoutData);

		Table table = toolkit.createTable(spellComponentSection, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
	    for (Component component : ServiceFactory.getComponentService().getCached()) {
	    	TableItem item = new TableItem(table, SWT.NONE);
	    	item.setText(component.getTitle());
	    	item.setChecked(spellModel.containsComponents(component.getId()));
	    }
	    table.addListener(SWT.Selection, new Listener() {
	      public void handleEvent(Event event) {
	        if(event.detail == SWT.CHECK) {
				if (spellModel!=null) {
					int id = -1;
					TableItem tbItem = (TableItem)event.item;
				    for (Component component : ServiceFactory.getComponentService().getCached()) {
				    	if(component.getTitle().equals(tbItem.getText()))
				    		id = component.getId();
				    }
					if(tbItem.getChecked()) {
						spellModel.addComponentId(id);
					} else {
						spellModel.removeComponentId(id);						
					}
					spellModel.fireModelChanged();
					managedForm.dirtyStateChanged();
				}
	        }
	      }
	    });
	    table.setLayoutData(tableGD_COL1);
	    spellComponentSection.setClient(table);
        
//		SECTION DETAIL
		Section spellDetailSection = toolkit.createSection(spellMasterComposite, Section.TWISTIE | Section.TITLE_BAR | Section.EXPANDED | SWT.FILL);
		spellDetailSection.setLayout(gridLayout);
		spellDetailSection.setText(LocaleManager.instance().getMessage("description"));	
		GridData section3LayoutData = new GridData();
		section3LayoutData.horizontalAlignment = section3LayoutData.verticalAlignment = GridData.FILL;
		section3LayoutData.grabExcessHorizontalSpace =  section3LayoutData.grabExcessVerticalSpace = true;
		section3LayoutData.horizontalSpan = 2;
		spellDetailSection.setLayoutData(section3LayoutData);
		Composite detailComposite = toolkit.createComposite(spellDetailSection);
		GridLayout detailLayout = new GridLayout();
		detailLayout.marginWidth = detailLayout.marginHeight = 2;
		detailComposite.setLayout(detailLayout);
		spellDetailSection.setClient(detailComposite);
		
		detail = toolkit.createText(detailComposite, spellModel.getDetail(), SWT.MULTI | SWT.V_SCROLL | SWT.WRAP);
		detail.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (spellModel!=null) {
					spellModel.setDetail(detail.getText());
					spellModel.fireModelChanged();
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
		if(tableLevel != null) tableLevel.dispose();
	}
	
	private void createControlDecoration(Control item, String info) {
		ControlDecoration decoratedControl = new ControlDecoration(item, SWT.LEFT | SWT.TOP);
		decoratedControl.setShowOnlyOnFocus(true);
		decoratedControl.setDescriptionText(info);	
		decoratedControl.setImage(Activator.getImage(Activator.ICON_INFO));		

	}
}