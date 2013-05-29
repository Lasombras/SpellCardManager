package spell.preferences;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

import spell.Activator;
import spell.model.Source;
import spell.services.ServiceFactory;
import spell.tools.LocaleManager;
import spell.viewers.table.SourceTableFilter;
	      
	 public class AppearancePage extends FieldEditorPreferencePage implements  
	         IWorkbenchPreferencePage {  
	   
		 
		 public final static String APPEARANCE_LANGUAGE = "APPEARANCE_LANGUAGE";
		 public final static String APPEARANCE_SOURCE = "APPEARANCE_SOURCE";
		 
		 public AppearancePage() {  
	         super(GRID);  
	     }  
	   
	     public void createFieldEditors() {  
			
			File rep = new File(Activator.getPath() + Activator.FOLDER_DATAS);
			File[] files  = rep.listFiles();
			ArrayList<String> languages = new ArrayList<String>();
			for(File file : files) {
				if(file.isDirectory()) {
					languages.add(file.getName());
				}
			}
			String[][] languagesTab = new String[languages.size()][2];
			for(int idx = 0; idx < languages.size(); idx++) {
				languagesTab[idx][0] = languages.get(idx);
				languagesTab[idx][1] = languages.get(idx);
			}
			
			
			addField(new ComboFieldEditor(APPEARANCE_LANGUAGE,"&" +  LocaleManager.instance().getMessage("language"),languagesTab,getFieldEditorParent()));

		    Group group1 = new Group(getFieldEditorParent(), SWT.SHADOW_IN);
		    group1.setText(LocaleManager.instance().getMessage("sourceName"));
		    group1.setLayout(new RowLayout(SWT.VERTICAL));
		    GridData gridData = new GridData(GridData.VERTICAL_ALIGN_END);
		    gridData.horizontalSpan = 2;
		    gridData.horizontalAlignment = GridData.FILL;
		    group1.setLayoutData(gridData);
		 		    
			for(Source source : ServiceFactory.getSourceService().getCached()) {
				Activator.getDefault().getPreferenceStore().setDefault(APPEARANCE_SOURCE + "_" + source.getId() ,true);
				addField(new BooleanFieldEditor(APPEARANCE_SOURCE + "_" + source.getId() ,"&" +  source.getTitle(),group1));
			}
			
	     }  
	   
	    @Override
	    public boolean performOk() {
	    	String oldLanguage = Activator.getDefault().getPreferenceStore().getString(APPEARANCE_LANGUAGE);
	    	boolean res = super.performOk();
	    	String newLanguage = Activator.getDefault().getPreferenceStore().getString(APPEARANCE_LANGUAGE);
	    	if(!oldLanguage.equals(newLanguage)) {
				PlatformUI.getWorkbench().restart();
	    	}
	    	SourceTableFilter.instance().clear();
	    	for(Source source : ServiceFactory.getSourceService().getCached()) {
				if(Activator.getDefault().getPreferenceStore().getBoolean(AppearancePage.APPEARANCE_SOURCE + "_" + source.getId()))
					SourceTableFilter.instance().addSource(source.getId());
			}
	    	SourceTableFilter.instance().update();

	    	return res;
	    }
	     
	     @Override  
	     public void init(IWorkbench workbench) {  
	         setPreferenceStore(Activator.getDefault().getPreferenceStore());  
	         setDescription(LocaleManager.instance().getMessage("appearencePreferenceInfo")); 
	     }  
	     
	     
	     public final static void setDefault() {
			if(Locale.getDefault().getLanguage().equals("fr")) {
				Activator.getDefault().getPreferenceStore().setDefault(APPEARANCE_LANGUAGE,"french");
			} else {
				Activator.getDefault().getPreferenceStore().setDefault(APPEARANCE_LANGUAGE,"english");
			}
	     }	     
	 }