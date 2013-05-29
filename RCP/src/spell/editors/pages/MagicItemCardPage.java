package spell.editors.pages;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import spell.Activator;
import spell.databases.DatabaseManager;
import spell.databases.Session;
import spell.editors.MagicItemFormEditorInput;
import spell.model.MagicItem;
import spell.tools.ConvertImageManager;
import spell.tools.GCCardBuilder;
import spell.tools.LocaleManager;



public class MagicItemCardPage extends FormPage {
	private MagicItem magicItemModel;	
	private Section magicItemCoverSection;
	private FileDialog fDialog;
	private ConvertImageManager imgManager;
	private Canvas imageLabel;
	private Text resumeCard;

	public MagicItemCardPage(FormEditor editor) {
		super(editor, "pageCardMagicItem", LocaleManager.instance().getMessage("card"));
		if (editor.getEditorInput() instanceof MagicItemFormEditorInput) {
			magicItemModel = ((MagicItemFormEditorInput)editor.getEditorInput()).getMagicItem();
		}
	}
		
	public void setActive(boolean active) {
		super.setActive(active);
		//TODO Optimiser en ne mettant à jour que s'il y a eu une modif depuis la derniére mise à jour
		if(active && magicItemModel.isDirty()) {
			this.getManagedForm().getForm().setText(magicItemModel.getTitle());
			//imageLabel.setImage(BuildCardManager.build(magicItemModel));
		}
	}
	
	protected void createFormContent(final IManagedForm managedForm) {
		ScrolledForm form = managedForm.getForm();
		form.setText(magicItemModel.getTitle());
		FormToolkit toolkit = managedForm.getToolkit();		
		Composite body = form.getBody(); 
		imgManager = new ConvertImageManager();
		Session session = null;
		try {
			session = DatabaseManager.getInstance().openSession();

		    final GridLayout gridLayout = new GridLayout();
		    gridLayout.marginWidth = 2;
	        gridLayout.marginHeight = 0;
	        gridLayout.numColumns = 1;
	        body.setLayout(gridLayout);
	 
	
	        Composite magicItemMasterComposite = toolkit.createComposite(body, SWT.FILL);
			GridLayout magicItemMasterCompositeLayout = new GridLayout();
			magicItemMasterCompositeLayout.marginWidth = magicItemMasterCompositeLayout.marginHeight = 2;
		    if(GCCardBuilder.instance().getCardWidth() > GCCardBuilder.MAX_WIDTH) {
		    	magicItemMasterCompositeLayout.numColumns = 1;
		    } else {
		    	magicItemMasterCompositeLayout.numColumns = 2;
		    }
	        magicItemMasterComposite.setLayout(magicItemMasterCompositeLayout);
			GridData magicItemMasterCompositeLayoutData = new GridData();
			magicItemMasterCompositeLayoutData.horizontalAlignment = magicItemMasterCompositeLayoutData.verticalAlignment = GridData.FILL;
			magicItemMasterCompositeLayoutData.grabExcessHorizontalSpace =  magicItemMasterCompositeLayoutData.grabExcessVerticalSpace = true;
	        magicItemMasterComposite.setLayoutData(magicItemMasterCompositeLayoutData);
	        
	        
//			SECTION CARTE
	        magicItemCoverSection = toolkit.createSection(magicItemMasterComposite, Section.TWISTIE | Section.DESCRIPTION | Section.TITLE_BAR | Section.EXPANDED);
			magicItemCoverSection.setText(LocaleManager.instance().getMessage("playCard"));
			magicItemCoverSection.setDescription(LocaleManager.instance().getMessage("backgroundChoiceAdd") + "\n" +
					LocaleManager.instance().getMessage("backgroundChoiceRemove") + "\n" +
					LocaleManager.instance().getMessage("backgroundSize1") + " 350x490 " + LocaleManager.instance().getMessage("backgroundSize2"));
			magicItemCoverSection.setLayout(new GridLayout());
			GridData da = new GridData();
			da.verticalAlignment = GridData.BEGINNING;
			magicItemCoverSection.setLayoutData(da);
			Composite magicItemCoverComposite = toolkit.createComposite(magicItemCoverSection, SWT.FILL);
			GridData magicItemCoverCompositeLayoutData = new GridData();
			magicItemCoverCompositeLayoutData.horizontalAlignment = magicItemCoverCompositeLayoutData.verticalAlignment = GridData.FILL;
			magicItemCoverCompositeLayoutData.grabExcessHorizontalSpace =  true;
			magicItemCoverComposite.setLayoutData(magicItemCoverCompositeLayoutData);
			GridLayout cardLayout = new GridLayout();
			cardLayout.marginWidth = cardLayout.marginHeight = 2;
			cardLayout.marginLeft = 15;
			magicItemCoverComposite.setLayout(cardLayout);
			magicItemCoverSection.setClient(magicItemCoverComposite);
						
			imageLabel = new Canvas( magicItemCoverComposite, SWT.NONE);  
			imageLabel.addPaintListener(new PaintListener() {
				public void paintControl( PaintEvent arg0) {
					GCCardBuilder.instance().draw(arg0.gc, magicItemModel);
				}  
			} ); 
			fDialog = new FileDialog(form.getShell(), SWT.OPEN | SWT.SINGLE);
			imageLabel.addMouseListener(new MouseListener() {
				public void mouseDoubleClick(MouseEvent arg0) {
					if(arg0.button > 1) {
						magicItemModel.setBackground(Activator.MAGIC_ITEM_NO_BACKGROUND);
						magicItemModel.fireModelChanged();
						managedForm.dirtyStateChanged();
						imageLabel.redraw();
					} else {
						String file = fDialog.open();
				        if (file != null) {
				        	String targetName = "mini_" + magicItemModel.getId() + ".jpg";
				        	if(imgManager.translate(file, Activator.getPath() + Activator.getMagicItemImageFolder() + targetName, ConvertImageManager.RESIZE, GCCardBuilder.instance().getCardHeight(), GCCardBuilder.instance().getCardWidth())) {
				        		Activator.reloadImage(Activator.getMagicItemImageFolder() + targetName);
				        		magicItemModel.setBackground(targetName);
				        		magicItemModel.fireModelChanged();
								managedForm.dirtyStateChanged();
								imageLabel.redraw();
				        	}
				        }
					}
				}
				public void mouseDown(MouseEvent arg0) {}
				public void mouseUp(MouseEvent arg0) {}
			});
			imageLabel.setLayoutData(new GridData(GCCardBuilder.instance().getCardWidth(), GCCardBuilder.instance().getCardHeight()));
			
			Button printBtn = toolkit.createButton(magicItemCoverComposite, LocaleManager.instance().getMessage("addToDeck"), SWT.PUSH);
			printBtn.setImage(Activator.getImage(Activator.ICON_ADD_CARD));
			printBtn.addSelectionListener(new SelectionListener() {
				public void widgetDefaultSelected(SelectionEvent e) {}
				public void widgetSelected(SelectionEvent e) {
	        		magicItemModel.increaseSize();
	        		magicItemModel.fireSizeChanged();
				}
			});
			GridData buttonGridData = new GridData();
			buttonGridData.horizontalAlignment = GridData.CENTER;
			buttonGridData.grabExcessHorizontalSpace = true;
			printBtn.setLayoutData(buttonGridData);

//			SECTION RESUME
			Section magicItemResumeSection = toolkit.createSection(magicItemMasterComposite, Section.TWISTIE | Section.DESCRIPTION | Section.TITLE_BAR | Section.EXPANDED | SWT.FILL);
			magicItemResumeSection.setLayout(gridLayout);
			magicItemResumeSection.setText(LocaleManager.instance().getMessage("cardResume"));			
			magicItemResumeSection.setDescription(LocaleManager.instance().getMessage("cardResumeInfo"));	
			GridData section4LayoutData = new GridData();
			section4LayoutData.horizontalAlignment = section4LayoutData.verticalAlignment = GridData.FILL;
			section4LayoutData.grabExcessHorizontalSpace =  section4LayoutData.grabExcessVerticalSpace = true;
			magicItemResumeSection.setLayoutData(section4LayoutData);
			Composite resumeComposite = toolkit.createComposite(magicItemResumeSection);
			GridLayout resumeLayout = new GridLayout();
			resumeLayout.marginWidth = resumeLayout.marginHeight = 2;
			resumeComposite.setLayout(resumeLayout);
			magicItemResumeSection.setClient(resumeComposite);
			resumeCard = toolkit.createText(resumeComposite, magicItemModel.getCardText(), SWT.MULTI | SWT.V_SCROLL | SWT.WRAP);
			resumeCard.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					if (magicItemModel!=null) {
						magicItemModel.setCardText(resumeCard.getText());
						magicItemModel.fireModelChanged();
						managedForm.dirtyStateChanged();
						imageLabel.redraw();
					}
				}
			});
			GridData resumeGridData = new GridData();
			resumeGridData.horizontalAlignment = resumeGridData.verticalAlignment = GridData.FILL;
			resumeGridData.grabExcessHorizontalSpace = resumeGridData.grabExcessVerticalSpace = true;
			resumeGridData.widthHint = 60;
			resumeCard.setLayoutData(resumeGridData);

		}
		catch (Exception e) {e.printStackTrace();}
		finally {if(session != null) session.close();}

		
		toolkit.paintBordersFor(body);
	}

}