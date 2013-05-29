package spell.editors.pages;


import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.IFormColors;
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
import spell.services.ServiceFactory;
import spell.tools.LinkManager;
import spell.tools.LocaleManager;



public class MagicItemViewPage extends FormPage {
	private MagicItem magicItemModel;	
	private FormToolkit toolkit;
	private Browser detail;

	public MagicItemViewPage(FormEditor editor) {
		super(editor, "pageViewMagicItem", LocaleManager.instance().getMessage("description"));		
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
			//this.detail.setText(encodeText(magicItemModel.getDetail()));
		}
	}
	
	protected void createFormContent(final IManagedForm managedForm) {
		ScrolledForm form = managedForm.getForm();
		form.setText(magicItemModel.getTitle());
		toolkit = managedForm.getToolkit();		
		Composite body = form.getBody(); 
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
			magicItemMasterCompositeLayout.numColumns = 1;
	        magicItemMasterComposite.setLayout(magicItemMasterCompositeLayout);
			GridData magicItemMasterCompositeLayoutData = new GridData();
			magicItemMasterCompositeLayoutData.horizontalAlignment = magicItemMasterCompositeLayoutData.verticalAlignment = GridData.FILL;
			magicItemMasterCompositeLayoutData.grabExcessHorizontalSpace =  magicItemMasterCompositeLayoutData.grabExcessVerticalSpace = true;
	        magicItemMasterComposite.setLayoutData(magicItemMasterCompositeLayoutData);


//			SECTION INFO
			Section magicItemInfoSection = toolkit.createSection(magicItemMasterComposite, Section.TWISTIE | Section.TITLE_BAR | Section.EXPANDED);
			magicItemInfoSection.setText(LocaleManager.instance().getMessage("informations"));
			magicItemInfoSection.setLayout(gridLayout);
			GridData magicItemInfoSectionLayoutData = new GridData();
			magicItemInfoSectionLayoutData.horizontalAlignment = GridData.FILL;
			magicItemInfoSectionLayoutData.grabExcessHorizontalSpace = true;
			magicItemInfoSection.setLayoutData(magicItemInfoSectionLayoutData);
			String defaultFont = "Arial";
			if(magicItemInfoSection.getFont().getFontData().length > 0)
				defaultFont = magicItemInfoSection.getFont().getFontData()[0].getName();

			Composite magicItemComposite = toolkit.createComposite(magicItemInfoSection);
			GridLayout glayout = new GridLayout();
			glayout.marginWidth = glayout.marginHeight = 2;
			glayout.numColumns = 2;
			magicItemComposite.setLayout(glayout);
			magicItemInfoSection.setClient(magicItemComposite);

			addLabel(magicItemComposite, LocaleManager.instance().getMessage("name") + " : ", magicItemModel.getTitle());
			addLabel(magicItemComposite, LocaleManager.instance().getMessage("originalName") + " : ", magicItemModel.getOriginalName());
			addLabel(magicItemComposite, LocaleManager.instance().getMessage("sourceName") + " : ", ServiceFactory.getSourceService().getCached(magicItemModel.getSourceId()).getTitle());
			addLabel(magicItemComposite, LocaleManager.instance().getMessage("pageNumber") + " : ", magicItemModel.getPage());
			addLabel(magicItemComposite, LocaleManager.instance().getMessage("itemType") + " : ", ServiceFactory.getItemTypeService().getCached(magicItemModel.getItemTypeId()).getTitle());
			addLabel(magicItemComposite, LocaleManager.instance().getMessage("aura")  + " : ", magicItemModel.getAura());
			addLabel(magicItemComposite, LocaleManager.instance().getMessage("casterLevelShort") + " : ", magicItemModel.getCasterLevel());
			addLabel(magicItemComposite, LocaleManager.instance().getMessage("slot") + " : ", ServiceFactory.getSlotService().getCached(magicItemModel.getSlotId()).getTitle());
			if(magicItemModel.getPrice() > 0)
				addLabel(magicItemComposite, LocaleManager.instance().getMessage("price") + " : ", Activator.formatPrice(magicItemModel.getPrice()));
			addLabel(magicItemComposite, LocaleManager.instance().getMessage("weight") + " : ", magicItemModel.getWeight());

			
			if(	magicItemModel.getConstructionRequirements() != null && !magicItemModel.getConstructionRequirements().isEmpty() &&
				magicItemModel.getConstructionCost() > 0) {
//			SECTION CONSTRUCTION
				Section magicItemConstructionSection = toolkit.createSection(magicItemMasterComposite, Section.TWISTIE | Section.TITLE_BAR | Section.EXPANDED | SWT.FILL);
				magicItemConstructionSection.setLayout(gridLayout);
				magicItemConstructionSection.setText(LocaleManager.instance().getMessage("construction"));	
				GridData magicItemConstructionSectionLayoutData = new GridData();
				magicItemConstructionSectionLayoutData.horizontalAlignment = GridData.FILL;
				magicItemConstructionSectionLayoutData.grabExcessHorizontalSpace = true;
				magicItemConstructionSection.setLayoutData(magicItemConstructionSectionLayoutData);
	
				
				Composite magicItemConstructionComposite = toolkit.createComposite(magicItemConstructionSection);
				GridLayout magicItemConstrcutionLayout = new GridLayout();
				magicItemConstrcutionLayout.marginWidth = magicItemConstrcutionLayout.marginHeight = 2;
				magicItemConstrcutionLayout.numColumns = 2;
				magicItemConstructionComposite.setLayout(magicItemConstrcutionLayout);
				magicItemConstructionSection.setClient(magicItemConstructionComposite);
				
				
				if(magicItemModel.getConstructionRequirements() != null && !magicItemModel.getConstructionRequirements().isEmpty()) {
					Label label = toolkit.createLabel(magicItemConstructionComposite, LocaleManager.instance().getMessage("requirements") + " : ");
					label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
					
					Browser link = new Browser(magicItemConstructionComposite, SWT.NONE | SWT.WRAP);
					GridData linkGridData = new GridData();
					linkGridData.horizontalAlignment = GridData.FILL;
					linkGridData.grabExcessHorizontalSpace = true;
					linkGridData.heightHint = 16;
					linkGridData.widthHint = 60;
					link.setLayoutData(linkGridData);
					link.setText(encodeText(magicItemModel.getConstructionRequirements(), defaultFont,8));
					link.addLocationListener(new LocationListener() {
						public void changed(LocationEvent event) {}
						public void changing(LocationEvent event) {
							LinkManager.goLink(event.location);
							event.doit = false;
						}
					});
				}
	
				if(magicItemModel.getConstructionCost() > 0)
					addLabel(magicItemConstructionComposite, LocaleManager.instance().getMessage("cost")+ " : ", Activator.formatPrice(magicItemModel.getConstructionCost()));
			}
			
//			SECTION DETAIL
			Section magicItemDetailSection = toolkit.createSection(magicItemMasterComposite, Section.TWISTIE | Section.TITLE_BAR | Section.EXPANDED | SWT.FILL);
			magicItemDetailSection.setLayout(gridLayout);
			magicItemDetailSection.setText(LocaleManager.instance().getMessage("description"));	
			GridData section3LayoutData = new GridData();
			section3LayoutData.horizontalAlignment = section3LayoutData.verticalAlignment = GridData.FILL;
			section3LayoutData.grabExcessHorizontalSpace =  section3LayoutData.grabExcessVerticalSpace = true;
			section3LayoutData.horizontalSpan = 3;
			magicItemDetailSection.setLayoutData(section3LayoutData);
			Composite detailComposite = toolkit.createComposite(magicItemDetailSection);
			GridLayout detailLayout = new GridLayout();
			detailLayout.marginWidth = detailLayout.marginHeight = 2;
			detailComposite.setLayout(detailLayout);
			magicItemDetailSection.setClient(detailComposite);
			
			detail = new Browser(detailComposite, SWT.NONE | SWT.WRAP);
			detail.setText(encodeText(magicItemModel.getDetail(),defaultFont, 9));
			//StyledText detail = new createNoBorderText(detailComposite, magicItemModel.getDetail(), SWT.MULTI | SWT.V_SCROLL | SWT.WRAP);
			GridData detailGridData = new GridData();
			detailGridData.horizontalAlignment = detailGridData.verticalAlignment = GridData.FILL;
			detailGridData.grabExcessHorizontalSpace = detailGridData.grabExcessVerticalSpace = true;
			//detailGridData.heightHint = 60;
			detailGridData.widthHint = 60;
			detail.setLayoutData(detailGridData);
			detail.addLocationListener(new LocationListener() {

				public void changed(LocationEvent event) {
					
				}

				public void changing(LocationEvent event) {
					event.doit = !LinkManager.goLink(event.location);
				}
				
			});
			
			toolkit.paintBordersFor(body);

		}
		catch (Exception e) {e.printStackTrace();}
		finally {if(session != null) session.close();}

		
		toolkit.paintBordersFor(body);
	}
	
	private String encodeText(String text, String defaultFont, int fontSize) {
		String style = "<style>"+
		"table {border: 1px solid black;border-collapse: collapse;font-size: " + fontSize + "pt;white-space: nowrap;}" +
		"caption {font-variant: small-caps;}"+
		"td {vertical-align: middle;}"+
		"td, th {border: 1px solid black;padding: 1px 5px 1px 5px;margin: 0;text-align: center;}"+
		"th {border-color:  black;background-color: #3f0a00;color: #ece5b2;white-space: normal;vertical-align: bottom;}"+
		"tr {background-color: #ece5b2;}"+
		"tr.alt {background-color: #f7f5df;}"+
		"tr.titre {border-color:  black;background-color: #3f0a00;color: #ece5b2;white-space: normal;vertical-align: bottom;}"+
		"tfoot td {border-style: none;white-space: normal;background-color: white;}"+
		"a:link, a:visited {color: #3f0a00;	text-decoration: underline;font-weight:bold;}"+
		"body {margin: 0px;border-width: 0px;padding: 0px;font-size: " + fontSize + "pt;font-family: " + defaultFont + ", Arial;}"+
		"</style>";
		
		text = text.replaceAll("</table>\\r\\n", "</table>");
		text = text.replaceAll("</table>\\n", "</table>");
		String startTab = "<table>";
		String endTag = "</table>";
		while(text.indexOf(startTab) > -1) {
			String strStart = text.substring(0,text.indexOf(startTab));
			String content = text.substring(text.indexOf(startTab), text.indexOf(endTag)+endTag.length());
			content = content.replaceAll("\\r\\n", "");
			content = content.replaceAll("\\n", "");
			content = content.replaceAll(startTab, "<table_ok>");
			content = content.replaceAll(endTag, "</table_ok>");
			String strEnd = text.substring(text.indexOf(endTag) + endTag.length());
			text = strStart + content + strEnd;
		}
		
		text = text.replaceAll("<table_ok>", "<table>");
		text = text.replaceAll("</table_ok>", "</table>");
		text = text.replaceAll("\\r\\n", "<br/>");
		text = text.replaceAll("\\n", "<br/>");
		return "<html><head>" + style + "</head><body>" + text + "</body></html>";
	}

	
	/*
	private Color backColor = new Color(null, 255,255,255);

	private Text createNoBorderText(Composite parent, String text, int style) {
		Text control = new Text(parent, style);
		control.setText(text);
		control.setEditable(false);
		control.setBackground(backColor);
		return control;
	}
	*/

	private void addLabel(Composite parent, String title, String info ) {
		if(info == null || info.isEmpty())  return;
		Label label = toolkit.createLabel(parent, title);
		label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		toolkit.createLabel(parent, info);		
	}
}