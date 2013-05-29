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
import spell.editors.SpellFormEditorInput;
import spell.model.Spell;
import spell.tools.ConvertImageManager;
import spell.tools.GCCardBuilder;
import spell.tools.LocaleManager;



public class SpellCardPage extends FormPage {
	private Spell spellModel;	
	private Section spellCoverSection;
	private FileDialog fDialog;
	private ConvertImageManager imgManager;
	private Canvas imageLabel;
	private Text resumeCard;

	public SpellCardPage(FormEditor editor) {
		super(editor, "pageCardSpell", LocaleManager.instance().getMessage("card"));
		if (editor.getEditorInput() instanceof SpellFormEditorInput) {
			spellModel = ((SpellFormEditorInput)editor.getEditorInput()).getSpell();
		}
	}
		
	public void setActive(boolean active) {
		super.setActive(active);
		//TODO Optimiser en ne mettant à jour que s'il y a eu une modif depuis la derniére mise à jour
		if(active && spellModel.isDirty()) {
			this.getManagedForm().getForm().setText(spellModel.getTitle());
			//imageLabel.setImage(BuildCardManager.build(spellModel));
		}
	}
	
	protected void createFormContent(final IManagedForm managedForm) {
		ScrolledForm form = managedForm.getForm();
		form.setText(spellModel.getTitle());
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
	 
	
	        Composite spellMasterComposite = toolkit.createComposite(body, SWT.FILL);
			GridLayout spellMasterCompositeLayout = new GridLayout();
			spellMasterCompositeLayout.marginWidth = spellMasterCompositeLayout.marginHeight = 2;
		    if(GCCardBuilder.instance().getCardWidth() > GCCardBuilder.MAX_WIDTH) {
		    	spellMasterCompositeLayout.numColumns = 1;
		    } else {
		    	spellMasterCompositeLayout.numColumns = 2;
		    }
	        spellMasterComposite.setLayout(spellMasterCompositeLayout);
			GridData spellMasterCompositeLayoutData = new GridData();
			spellMasterCompositeLayoutData.horizontalAlignment = spellMasterCompositeLayoutData.verticalAlignment = GridData.FILL;
			spellMasterCompositeLayoutData.grabExcessHorizontalSpace =  spellMasterCompositeLayoutData.grabExcessVerticalSpace = true;
	        spellMasterComposite.setLayoutData(spellMasterCompositeLayoutData);
	        
	        
//			SECTION CARTE
	        spellCoverSection = toolkit.createSection(spellMasterComposite, Section.TWISTIE | Section.DESCRIPTION | Section.TITLE_BAR | Section.EXPANDED);
			spellCoverSection.setText(LocaleManager.instance().getMessage("playCard"));
			spellCoverSection.setDescription(LocaleManager.instance().getMessage("backgroundChoiceAdd") + "\n" +
					LocaleManager.instance().getMessage("backgroundChoiceRemove") + "\n" +
					LocaleManager.instance().getMessage("backgroundSize1") + " 350x490 " + LocaleManager.instance().getMessage("backgroundSize2"));
			spellCoverSection.setLayout(new GridLayout());
			GridData da = new GridData();
			da.verticalAlignment = GridData.BEGINNING;
			spellCoverSection.setLayoutData(da);
			Composite spellCoverComposite = toolkit.createComposite(spellCoverSection, SWT.FILL);
			GridData spellCoverCompositeLayoutData = new GridData();
			spellCoverCompositeLayoutData.horizontalAlignment = spellCoverCompositeLayoutData.verticalAlignment = GridData.FILL;
			spellCoverCompositeLayoutData.grabExcessHorizontalSpace =  true;
			spellCoverComposite.setLayoutData(spellCoverCompositeLayoutData);
			GridLayout cardLayout = new GridLayout();
			cardLayout.marginWidth = cardLayout.marginHeight = 2;
			cardLayout.marginLeft = 15;
			spellCoverComposite.setLayout(cardLayout);
			spellCoverSection.setClient(spellCoverComposite);
						
			imageLabel = new Canvas( spellCoverComposite, SWT.NONE);  
			imageLabel.addPaintListener(new PaintListener() {
				public void paintControl( PaintEvent arg0) {
					GCCardBuilder.instance().draw(arg0.gc, spellModel);
				}  
			} ); 
			fDialog = new FileDialog(form.getShell(), SWT.OPEN | SWT.SINGLE);
			imageLabel.addMouseListener(new MouseListener() {
				public void mouseDoubleClick(MouseEvent arg0) {
					if(arg0.button > 1) {
		        		spellModel.setBackground(Activator.SPELL_NO_BACKGROUND);
		        		spellModel.fireModelChanged();
						managedForm.dirtyStateChanged();
						imageLabel.redraw();
					} else {
						String file = fDialog.open();
				        if (file != null) {
				        	String targetName = "mini_" + spellModel.getId() + ".jpg";
				        	if(imgManager.translate(file, Activator.getPath() + Activator.getSpellImageFolder() + targetName, ConvertImageManager.RESIZE, GCCardBuilder.instance().getCardHeight(), GCCardBuilder.instance().getCardWidth())) {
				        		Activator.reloadImage(Activator.getSpellImageFolder() + targetName);
				        		spellModel.setBackground(targetName);
				        		spellModel.fireModelChanged();
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
			
			Button printBtn = toolkit.createButton(spellCoverComposite, LocaleManager.instance().getMessage("addToDeck"), SWT.PUSH);
			printBtn.setImage(Activator.getImage(Activator.ICON_ADD_CARD));
			printBtn.addSelectionListener(new SelectionListener() {
				public void widgetDefaultSelected(SelectionEvent e) {}
				public void widgetSelected(SelectionEvent e) {
	        		spellModel.increaseSize();
	        		spellModel.fireSizeChanged();
				}
			});
			GridData buttonGridData = new GridData();
			buttonGridData.horizontalAlignment = GridData.CENTER;
			buttonGridData.grabExcessHorizontalSpace = true;
			printBtn.setLayoutData(buttonGridData);

//			SECTION RESUME
			Section spellResumeSection = toolkit.createSection(spellMasterComposite, Section.TWISTIE | Section.DESCRIPTION | Section.TITLE_BAR | Section.EXPANDED | SWT.FILL);
			spellResumeSection.setLayout(gridLayout);
			spellResumeSection.setText(LocaleManager.instance().getMessage("cardResume"));			
			spellResumeSection.setDescription(LocaleManager.instance().getMessage("cardResumeInfo"));	
			GridData section4LayoutData = new GridData();
			section4LayoutData.horizontalAlignment = section4LayoutData.verticalAlignment = GridData.FILL;
			section4LayoutData.grabExcessHorizontalSpace =  section4LayoutData.grabExcessVerticalSpace = true;
			spellResumeSection.setLayoutData(section4LayoutData);
			Composite resumeComposite = toolkit.createComposite(spellResumeSection);
			GridLayout resumeLayout = new GridLayout();
			resumeLayout.marginWidth = resumeLayout.marginHeight = 2;
			resumeComposite.setLayout(resumeLayout);
			spellResumeSection.setClient(resumeComposite);
			resumeCard = toolkit.createText(resumeComposite, spellModel.getCardText(), SWT.MULTI | SWT.V_SCROLL | SWT.WRAP);
			resumeCard.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					if (spellModel!=null) {
						spellModel.setCardText(resumeCard.getText());
						spellModel.fireModelChanged();
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