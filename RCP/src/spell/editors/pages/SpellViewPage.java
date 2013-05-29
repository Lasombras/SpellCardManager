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
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import spell.databases.DatabaseManager;
import spell.databases.Session;
import spell.editors.SpellFormEditorInput;
import spell.model.Level;
import spell.model.Spell;
import spell.services.ServiceFactory;
import spell.tools.LinkManager;
import spell.tools.LocaleManager;



public class SpellViewPage extends FormPage {
	private Spell spellModel;	
	private FormToolkit toolkit;
	private Browser detail;

	public SpellViewPage(FormEditor editor) {
		super(editor, "pageViewSpell", LocaleManager.instance().getMessage("description"));		
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
			//this.detail.setText(encodeText(spellModel.getDetail()));
		}
	}
	
	protected void createFormContent(final IManagedForm managedForm) {
		ScrolledForm form = managedForm.getForm();
		form.setText(spellModel.getTitle());
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
	 
	
	        Composite spellMasterComposite = toolkit.createComposite(body, SWT.FILL);
			GridLayout spellMasterCompositeLayout = new GridLayout();
			spellMasterCompositeLayout.marginWidth = spellMasterCompositeLayout.marginHeight = 2;
			spellMasterCompositeLayout.numColumns = 1;
	        spellMasterComposite.setLayout(spellMasterCompositeLayout);
			GridData spellMasterCompositeLayoutData = new GridData();
			spellMasterCompositeLayoutData.horizontalAlignment = spellMasterCompositeLayoutData.verticalAlignment = GridData.FILL;
			spellMasterCompositeLayoutData.grabExcessHorizontalSpace =  spellMasterCompositeLayoutData.grabExcessVerticalSpace = true;
	        spellMasterComposite.setLayoutData(spellMasterCompositeLayoutData);
	        
//			SECTION INFO
			Section spellInfoSection = toolkit.createSection(spellMasterComposite, Section.TWISTIE | Section.TITLE_BAR | Section.EXPANDED);
			spellInfoSection.setText(LocaleManager.instance().getMessage("informations"));
			spellInfoSection.setLayout(gridLayout);
			GridData spellInfoSectionLayoutData = new GridData();
			spellInfoSectionLayoutData.horizontalAlignment = GridData.FILL;
			spellInfoSectionLayoutData.grabExcessHorizontalSpace = true;
			spellInfoSection.setLayoutData(spellInfoSectionLayoutData);
			String defaultFont = "Arial";
			if(spellInfoSection.getFont().getFontData().length > 0)
				defaultFont = spellInfoSection.getFont().getFontData()[0].getName();

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

			
			addLabel(spellComposite1, LocaleManager.instance().getMessage("name") + " : ", spellModel.getTitle());
			addLabel(spellComposite1,LocaleManager.instance().getMessage("originalName") + " : ", spellModel.getOriginalName());
			addLabel(spellComposite1,LocaleManager.instance().getMessage("sourceName") + " : ", ServiceFactory.getSourceService().getCached(spellModel.getSourceId()).getTitle());
			addLabel(spellComposite1, LocaleManager.instance().getMessage("pageNumber") + " : ", spellModel.getPage());
	    	String school = ServiceFactory.getSchoolService().getCached(spellModel.getSchoolId()).getTitle();
	    	if(!spellModel.getDescriptor().isEmpty())
	    		school += " [" + spellModel.getDescriptor() + "]";
			addLabel(spellComposite1, LocaleManager.instance().getMessage("school") + " : ", school);
	    	String levelStr = "";
	        for(Level level : spellModel.getLevels()) {
	        	if(!levelStr.isEmpty()) levelStr += ", ";
	        	levelStr +=  ServiceFactory.getPlayerClassService().getCached(level.getPlayerClassId()).getTitle() + " " + level.getLevel();
	        }
			addLabel(spellComposite1, LocaleManager.instance().getMessage("level") + " : ", levelStr);
			addLabel(spellComposite1, LocaleManager.instance().getMessage("castingTime") + " : ", spellModel.getCastingTime());
	    	String componentsStr = "";
	        for(int component : spellModel.getComponentsId()) {
	        	if(!componentsStr.isEmpty()) componentsStr += ", ";
	        	componentsStr +=  ServiceFactory.getComponentService().getCached(component).getTitle();
	        }		
			addLabel(spellComposite1, LocaleManager.instance().getMessage("components") + " : ", componentsStr);
			addLabel(spellComposite1, LocaleManager.instance().getMessage("material") + " : ", spellModel.getMaterial());
			addLabel(spellComposite1, LocaleManager.instance().getMessage("range") + " : ", spellModel.getRange());
			addLabel(spellComposite1, LocaleManager.instance().getMessage("target") + " : ", spellModel.getTarget());
			addLabel(spellComposite1, LocaleManager.instance().getMessage("effect") + " : ", spellModel.getEffect());
			addLabel(spellComposite1, LocaleManager.instance().getMessage("area") + " : ", spellModel.getArea());
			addLabel(spellComposite1, LocaleManager.instance().getMessage("duration") + " : ", spellModel.getDuration());
			addLabel(spellComposite1, LocaleManager.instance().getMessage("savingThrow") + " : ", spellModel.getSavingThrow());
			addLabel(spellComposite1, LocaleManager.instance().getMessage("spellResistance") + " : ", spellModel.isSpellResistance()?LocaleManager.instance().getMessage("yes"):LocaleManager.instance().getMessage("no"));
	  
//			SECTION DETAIL
			Section spellDetailSection = toolkit.createSection(spellMasterComposite, Section.TWISTIE | Section.TITLE_BAR | Section.EXPANDED | SWT.FILL);
			spellDetailSection.setLayout(gridLayout);
			spellDetailSection.setText(LocaleManager.instance().getMessage("description"));	
			GridData section3LayoutData = new GridData();
			section3LayoutData.horizontalAlignment = section3LayoutData.verticalAlignment = GridData.FILL;
			section3LayoutData.grabExcessHorizontalSpace =  section3LayoutData.grabExcessVerticalSpace = true;
			section3LayoutData.horizontalSpan = 3;
			spellDetailSection.setLayoutData(section3LayoutData);
			Composite detailComposite = toolkit.createComposite(spellDetailSection);
			GridLayout detailLayout = new GridLayout();
			detailLayout.marginWidth = detailLayout.marginHeight = 2;
			detailComposite.setLayout(detailLayout);
			spellDetailSection.setClient(detailComposite);
			
			detail = new Browser(detailComposite, SWT.MULTI | SWT.WRAP);
			detail.setText(encodeText(spellModel.getDetail(),defaultFont,9));
			//StyledText detail = new createNoBorderText(detailComposite, spellModel.getDetail(), SWT.MULTI | SWT.V_SCROLL | SWT.WRAP);
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