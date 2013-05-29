package spell.preferences;

import java.io.File;
import java.util.ArrayList;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

import spell.Activator;
import spell.databases.DatabaseManager;
import spell.databases.Session;
import spell.model.PlayerClass;
import spell.services.ServiceFactory;
import spell.tools.LocaleManager;
	      
	 public class ImpressionPage extends FieldEditorPreferencePage implements  
	         IWorkbenchPreferencePage {  
	   
		 
		 public final static String CARD_TEMPLATE = "CARD_TEMPLATE";
		 public final static String FRONT_BACK = "FRONT_BACK";
		 public final static String PLAYER_CLASS = "PLAYER_CLASS";
		 public final static String CARD_MARGE = "CARD_MARGE";
		 public final static String PRINT_FORMAT = "PRINT_FORMAT";
		 public final static String PRINT_LISTING = "PRINT_LISTING";
		 
		 public final static int PLAYER_CLASS_ALL = -2;
		 public final static int PLAYER_CLASS_BASE = -1;
		 public final static int PLAYER_CLASS_NONE = 0;
		 
		 public final static int PRINT_FORMAT_A4_LANDSCAPE = 0;
		 public final static int PRINT_FORMAT_A4_PORTRAIT = 1;

		 
		 public ImpressionPage() {  
	         super(GRID); 
	     }  
		 	   
	     public void createFieldEditors() {  
			addField(new BooleanFieldEditor(PRINT_LISTING,"&" + LocaleManager.instance().getMessage("printListing"), getFieldEditorParent()));  
			addField(new BooleanFieldEditor(FRONT_BACK,"&" + LocaleManager.instance().getMessage("frontBack"), getFieldEditorParent()));  
			
			File rep = new File(Activator.getPath() + Activator.FOLDER_TEMPLATES);
			File[] files  = rep.listFiles();
			ArrayList<String> templates = new ArrayList<String>();
			for(File file : files) {
				if(file.isDirectory()) {
					templates.add(file.getName());
				}
			}
			String[][] templatesTab = new String[templates.size()][2];
			for(int idx = 0; idx < templates.size(); idx++) {
				templatesTab[idx][0] = templates.get(idx);
				templatesTab[idx][1] = templates.get(idx);
			}
			
			addField(new ComboFieldEditor(CARD_TEMPLATE,"&" + LocaleManager.instance().getMessage("cardTemplate"),templatesTab,getFieldEditorParent()));

			addField(new IntegerFieldEditor(CARD_MARGE,"&" + LocaleManager.instance().getMessage("cardMarge"), getFieldEditorParent()));  

			String[][] printFormatsFilter = new String[2][2];;
			printFormatsFilter[0][1] = PRINT_FORMAT_A4_LANDSCAPE+"";
			printFormatsFilter[0][0] = LocaleManager.instance().getMessage("printFormatA4Landscape");
			printFormatsFilter[1][1] = PRINT_FORMAT_A4_PORTRAIT+"";
			printFormatsFilter[1][0] = LocaleManager.instance().getMessage("printFormatA4Portrait");
			
			addField(new ComboFieldEditor(PRINT_FORMAT,"&" + LocaleManager.instance().getMessage("printFormat"),printFormatsFilter,getFieldEditorParent()));
			
			Session session = null;
			String[][] playerClassesFilter = null;
			try {
				session = DatabaseManager.getInstance().openSession();
				PlayerClass[] playerClasses = ServiceFactory.getPlayerClassService().getAll(session);
				playerClassesFilter = new String[playerClasses.length+3][2];
				playerClassesFilter[0][1] = PLAYER_CLASS_ALL+"";
				playerClassesFilter[0][0] = LocaleManager.instance().getMessage("allClasses");
				playerClassesFilter[1][1] = PLAYER_CLASS_NONE+"";
				playerClassesFilter[1][0] = LocaleManager.instance().getMessage("noneInfo");
				playerClassesFilter[2][1] = PLAYER_CLASS_BASE+"";
				playerClassesFilter[2][0] = LocaleManager.instance().getMessage("basicClasses");
				
				for(int idx = 0; idx < playerClasses.length; idx++) {
					playerClassesFilter[idx+3][1] = playerClasses[idx].getId()+"";
					playerClassesFilter[idx+3][0] = playerClasses[idx].getTitle();
				}
			} catch (Exception e) {}
			finally {
				session.close();
			}
			addField(new ComboFieldEditor(PLAYER_CLASS,"&" + LocaleManager.instance().getMessage("levelInfo"),playerClassesFilter,getFieldEditorParent()));

	     }  
	   
	     @Override  
	     public void init(IWorkbench workbench) {  
	         setPreferenceStore(Activator.getDefault().getPreferenceStore());  
	         setDescription(LocaleManager.instance().getMessage("printPreferenceInfo")); 
	     }  
	     
	     @Override
		    public boolean performOk() {
		    	String oldTemplate = Activator.getDefault().getPreferenceStore().getString(CARD_TEMPLATE);
		    	boolean res = super.performOk();
		    	String newTemplate = Activator.getDefault().getPreferenceStore().getString(CARD_TEMPLATE);
		    	if(!oldTemplate.equals(newTemplate)) {
					PlatformUI.getWorkbench().restart();
		    	}

		    	return res;
		    }

	     public final static void setDefault() {
	 		Activator.getDefault().getPreferenceStore().setDefault(CARD_TEMPLATE, "basic_pathfinder");
	 		Activator.getDefault().getPreferenceStore().setDefault(FRONT_BACK, true);
	 		Activator.getDefault().getPreferenceStore().setDefault(PLAYER_CLASS, PLAYER_CLASS_BASE);
	 		Activator.getDefault().getPreferenceStore().setDefault(CARD_MARGE, 1);
	 		Activator.getDefault().getPreferenceStore().setDefault(PRINT_FORMAT, PRINT_FORMAT_A4_LANDSCAPE);
	 		Activator.getDefault().getPreferenceStore().setDefault(PRINT_LISTING, false);
	     }	 

	 }