
package spell.splashHandlers;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.splash.BasicSplashHandler;

import spell.Activator;
import spell.databases.DatabaseManager;
import spell.databases.Session;
import spell.model.Source;
import spell.model.simple.ISharedModelBoxIds;
import spell.model.simple.SharedSimpleModelBox;
import spell.preferences.AppearancePage;
import spell.services.ServiceFactory;
import spell.tools.LocaleManager;
import spell.viewers.table.SourceTableFilter;

/**
 * @since 3.3
 *
 */
public class SplashHandler extends BasicSplashHandler {
			
	@Override
	public void init(final Shell splash) {
		java.awt.Toolkit.getDefaultToolkit(); // initialize toolkit here to prevent deadlock on the mac

		super.init(splash);
		
		setMessageRect(new Rectangle(10,305,445,12));
		setProgressRect(new Rectangle(0,295,455,10));

		configureUISplash();

		
		boolean forceRebuild = false;
		
		getBundleProgressMonitor().beginTask(LocaleManager.instance().getMessage("loading.datas"),6);
		//Initialisation de la base de données
		DatabaseManager.getInstance().initialize(forceRebuild);
		
		//Montée en cache des objets
		Session session = null;
		try {
			session = DatabaseManager.getInstance().openSession();
			getBundleProgressMonitor().subTask(LocaleManager.instance().getMessage("loading.components"));
			ServiceFactory.getComponentService().setCached(session);
			getBundleProgressMonitor().worked(1);
			getBundleProgressMonitor().subTask(LocaleManager.instance().getMessage("loading.classes"));
			ServiceFactory.getPlayerClassService().setCached(session);
			getBundleProgressMonitor().worked(1);
			getBundleProgressMonitor().subTask(LocaleManager.instance().getMessage("loading.schools"));
			ServiceFactory.getSchoolService().setCached(session);
			getBundleProgressMonitor().worked(1);
			getBundleProgressMonitor().subTask(LocaleManager.instance().getMessage("loading.slots"));
			ServiceFactory.getSlotService().setCached(session);
			getBundleProgressMonitor().worked(1);
			getBundleProgressMonitor().subTask(LocaleManager.instance().getMessage("loading.items"));
			ServiceFactory.getItemTypeService().setCached(session);
			getBundleProgressMonitor().worked(1);
			getBundleProgressMonitor().subTask(LocaleManager.instance().getMessage("loading.sources"));
			ServiceFactory.getSourceService().setCached(session);
			getBundleProgressMonitor().worked(1);

//1. Import du WIKI
//			Spell[] spells = WikiImport.importSpells(session);
//			
//			for(Spell spell : spells) {
//				System.out.println("Save : " + spell.getTitle());
//				ServiceFactory.getSpellService().save(spell, session);
//			}
					
//2. Test des sorts necessitant un résumé
//			PDFCardBuilder pdfCard = new PDFCardBuilder();
//			for(Spell item : spells) {
//				item.increaseSize();
//				pdfCard.add(item);
//			}
//			pdfCard.draw("aaa.pdf", false);

//3. Verification des erreurs Wiki
			//Verifier la presence de mots interdits dans les champs
//			for(Spell item : spells) {
//				if(item.getArea().indexOf('<') > -1)
//					System.err.println(item.getTitle());
//				if(item.getCastingTime().indexOf('<') > -1)
//					System.err.println(item.getTitle());
//				if(item.getDescriptor().indexOf('<') > -1)
//					System.err.println(item.getTitle());
//				if(item.getDuration().indexOf('<') > -1)
//					System.err.println(item.getTitle());
//				if(item.getEffect().indexOf('<') > -1)
//					System.err.println(item.getTitle());
//				if(item.getMaterial().indexOf('<') > -1)
//					System.err.println(item.getTitle());
//				if(item.getRange().indexOf('<') > -1)
//					System.err.println(item.getTitle());
//				if(item.getSavingThrow().indexOf('<') > -1)
//					System.err.println(item.getTitle());
//				if(item.getTarget().indexOf('<') > -1)
//					System.err.println(item.getTitle());
//				if(item.getDescriptor().indexOf("escription") > -1)
//					System.err.println(item.getTitle());
//				if(item.getMaterial() == null)
//					System.err.println(item.getTitle());
//				else if(item.getMaterial().indexOf("escription") > -1)
//					System.err.println(item.getTitle());
//				if(item.getArea().indexOf("escription") > -1)
//					System.err.println(item.getTitle());
//				if(item.getEffect().indexOf("escription") > -1)
//					System.err.println(item.getTitle());
//				if(item.getDetail().indexOf("<div") > -1)
//					System.err.println(item.getTitle());
//				if(item.getDetail().indexOf("<Div") > -1)
//					System.err.println(item.getTitle());
//				if(item.getDetail().indexOf("<DIV") > -1)
//					System.err.println(item.getTitle());
//				if(item.getDetail().endsWith("\n")) {
//					item.setDetail(item.getDetail().substring(0,item.getDetail().length()-1));
//					ServiceFactory.getSpellService().save(item, session);					
//					System.err.println(item.getTitle());
//				}
//				item.setDetail(item.getDetail().replaceAll(" CLASS=\"tablo\"",""));
//				item.setDetail(item.getDetail().replaceAll("CLASS=","class="));
//				if(item.getDetail().indexOf("<table") > -1 && item.getCardText().isEmpty())
//					System.err.println(item.getTitle());
//				ServiceFactory.getSpellService().save(item, session);
//			}
			
			/*
			MagicItem[] magicItems = ServiceFactory.getMagicItemService().getAll(session);
			for(MagicItem item : magicItems) {
				if(LinkManager.errorInLinks(item.getDetail()))
					System.out.println("MAGIC_ITEM - ERROR IN DETAIL - LINK - " + item.getTitle());
				if(LinkManager.errorInLinks(item.getConstructionRequirements()))
					System.out.println("MAGIC_ITEM - ERROR IN REQUIEREMENTS - LINK - " + item.getTitle());
			}

			Spell[] spells = ServiceFactory.getSpellService().getAll(session);
			for(Spell item : spells) {
				if(LinkManager.errorInLinks(item.getDetail()))
					System.out.println("SPELL - ERROR IN DETAIL - LINK - " + item.getTitle());
			}
			*/
			
			//Initialisation des listes de sorts et objets
			getBundleProgressMonitor().beginTask(LocaleManager.instance().getMessage("loading.lists"),3);	
			getBundleProgressMonitor().subTask(LocaleManager.instance().getMessage("loading.spells"));
			SharedSimpleModelBox.instance().add(ISharedModelBoxIds.BOX_SPELL,ServiceFactory.getSpellService().getAllInBox(session));
			getBundleProgressMonitor().worked(1);
			getBundleProgressMonitor().subTask(LocaleManager.instance().getMessage("loading.magicItems"));
			SharedSimpleModelBox.instance().add(ISharedModelBoxIds.BOX_MAGIC_ITEM,ServiceFactory.getMagicItemService().getAllInBox(session));
			getBundleProgressMonitor().worked(1);
			
			getBundleProgressMonitor().subTask(LocaleManager.instance().getMessage("loading.filters"));
			//Initialisation des filtres sur les sources
			for(Source source : ServiceFactory.getSourceService().getCached()) {
				if(
					Activator.getDefault().getPreferenceStore().getString(AppearancePage.APPEARANCE_SOURCE + "_" + source.getId()) == null ||	
					Activator.getDefault().getPreferenceStore().getString(AppearancePage.APPEARANCE_SOURCE + "_" + source.getId()).equals(""))
					SourceTableFilter.instance().addSource(source.getId());
			}
			getBundleProgressMonitor().worked(1);

			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(session != null) session.close();
		}

	}
	
	private void configureUISplash() {
		setMessageRect(new Rectangle(10,305,445,12));
		setProgressRect(new Rectangle(0,295,455,10));		
	}
}
