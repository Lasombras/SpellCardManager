package spell.jobs;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import spell.Activator;
import spell.databases.DatabaseManager;
import spell.databases.Session;
import spell.dialogs.SourceMergeDialog;
import spell.dialogs.ComponentMergeDialog;
import spell.dialogs.MagicItemMergeDialog;
import spell.dialogs.OptionsImportDialog;
import spell.dialogs.PlayerClassMergeDialog;
import spell.dialogs.SchoolMergeDialog;
import spell.dialogs.SpellMergeDialog;
import spell.model.Source;
import spell.model.Component;
import spell.model.Level;
import spell.model.MagicItem;
import spell.model.PlayerClass;
import spell.model.School;
import spell.model.Spell;
import spell.services.ServiceFactory;
import spell.tools.LocaleManager;

public class ImportJob implements IRunnableWithProgress
{
	////////////////////////////////////////////////////////////////////////
	private Shell shell;
	private String message;
	private int state = OK;
	private String importFile;
	
	public final static int OK = 0;
	public final static int CANCELED = 1;
	public final static int ERROR = 3;
	
	////////////////////////////////////////////////////////////////////////////
	public ImportJob(Shell shell, String importFile)
	{
		super();
		this.shell = shell;
		this.message = "";
		this.importFile = importFile;
	}

	public int getState() {
		return state;
	}

	public String getMessage() {
		return message;
	}
	
	public void writeEntry(String entryName, String destFileName) throws Exception {
		FileInputStream fis = new FileInputStream(importFile);
		BufferedInputStream buffi = new BufferedInputStream(fis);
		ZipInputStream zis = new ZipInputStream(buffi);
		ZipEntry entry;
		int count;
		while((entry = zis.getNextEntry()) != null) {
			if(entry.getName().equals(entryName)) {
				final int BUFFER = 2048;
				byte data[] = new byte[BUFFER];			
				BufferedOutputStream dest = null;
				FileOutputStream fos = new FileOutputStream(destFileName);
				dest = new BufferedOutputStream(fos, BUFFER);
				while ((count = zis.read(data, 0, BUFFER)) != -1) {
					dest.write(data, 0, count);
				}
				dest.flush();
				dest.close();		
			}
		}
		zis.close();
	}
	
	
	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {
 			    	 
 		Session session = null;		
		try {

			OptionsImportDialog dialogOptionsImport = new OptionsImportDialog(shell);
			dialogOptionsImport.create();
			if (dialogOptionsImport.open() == Window.OK) {

				
				session = DatabaseManager.getInstance().openSession();
				
				DocumentBuilderFactory fabrique = DocumentBuilderFactory.newInstance();
				DocumentBuilder constructeur = fabrique.newDocumentBuilder();
				
				writeEntry("cards.xml", Activator.getPath() + "cards.xml");
				File xml = new File(Activator.getPath() + "cards.xml");
				xml.deleteOnExit();
				Document document = constructeur.parse(xml);
				xml.delete();
				Element infos = (Element)document.getElementsByTagName("infos").item(0);
				
				Spell[] spellsImport = ServiceFactory.getSpellService().importXML(infos);
				MagicItem[] magicItemsImport = ServiceFactory.getMagicItemService().importXML(infos);
				monitor.beginTask(LocaleManager.instance().getMessage("importTask"), 6 + spellsImport.length + magicItemsImport.length);
					
				monitor.subTask(LocaleManager.instance().getMessage("importTask1"));
				Component[] componentsImport = ServiceFactory.getComponentService().importXML(infos);
				Component[] componentsBase = ServiceFactory.getComponentService().getAll(session);
				Hashtable<Integer, Integer> componentsTranslate = new Hashtable<Integer, Integer>();
				monitor.worked(1);

				monitor.subTask(LocaleManager.instance().getMessage("importTask2"));
				PlayerClass[] playerClassesImport =  ServiceFactory.getPlayerClassService().importXML(infos);
				PlayerClass[] playerClassesBase =  ServiceFactory.getPlayerClassService().getAll(session);
				Hashtable<Integer, Integer> playerClassesTranslate = new Hashtable<Integer, Integer>();
				monitor.worked(1);
							
				monitor.subTask(LocaleManager.instance().getMessage("importTask3"));
				School[] schoolsImport = ServiceFactory.getSchoolService().importXML(infos);
				School[] schoolsBase = ServiceFactory.getSchoolService().getAll(session);
				Hashtable<Integer, Integer> schoolsTranslate = new Hashtable<Integer, Integer>();
				monitor.worked(1);
				
				monitor.subTask(LocaleManager.instance().getMessage("importTask4"));
				Spell[] spellsBase = ServiceFactory.getSpellService().getAll(session);
				monitor.worked(1);

				monitor.subTask(LocaleManager.instance().getMessage("importTask5"));
				MagicItem[] magicItemsBase = ServiceFactory.getMagicItemService().getAll(session);
				monitor.worked(1);

				monitor.subTask(LocaleManager.instance().getMessage("importTask6"));
				Source[] sourcesImport = ServiceFactory.getSourceService().importXML(infos);
				Source[] sourcesBase = ServiceFactory.getSourceService().getAll(session);
				Hashtable<Integer, Integer> sourcesTranslate = new Hashtable<Integer, Integer>();
				monitor.worked(1);

				for(Spell spellImport : spellsImport) {
					String importBackground = spellImport.getBackground();
					String importImage = spellImport.getImage();

					//1 Transformer les composantes
					monitor.subTask(LocaleManager.instance().getMessage("importTaskSpell") + " " + spellImport.getTitle());
					ArrayList<Integer> componentsArray = new ArrayList<Integer>();
					for(Integer id : spellImport.getComponentsId()) {
						Integer targetId = componentsTranslate.get(id);
						if(targetId == null) {
							Component sourceComponent = null;
							//Chercher une correspondance
							for(Component component : componentsImport) {
								if(component.getId() == id.intValue())
									sourceComponent = component;
							}
							Component targetComponent = null;
							for(Component component : componentsBase) {
								if(component.getTitle().equalsIgnoreCase(sourceComponent.getTitle()))
									targetComponent = component;
							}
							if(targetComponent == null) {
								ComponentMergeDialog dialog = new ComponentMergeDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),componentsBase ,sourceComponent);
								dialog.create();
								if (dialog.open() == Window.OK) {
									switch (dialog.getValue()) {
										case ComponentMergeDialog.CREATE:
											targetComponent = new Component(0,sourceComponent.getTitle(),sourceComponent.getImage(), sourceComponent.getShortName());
											ServiceFactory.getComponentService().save(targetComponent, session);											
											break;
										case ComponentMergeDialog.LINK:
											targetComponent = dialog.getTargetItem();
											break;
										default :
									}
								} else {
									session.rollback();
									monitor.setCanceled(true);
									monitor.done();
									this.state = CANCELED;
									this.message = LocaleManager.instance().getMessage("importTaskCanceled");
									return;
								}
							}
							targetId = new Integer(-1);
							if(targetComponent != null)
								targetId = new Integer(targetComponent.getId());
							componentsTranslate.put(id, targetId);
						}
						if(targetId.intValue() != -1)
							componentsArray.add(targetId);
					}
					spellImport.removeAllComponentId();
					for(Integer id : componentsArray) {
						spellImport.addComponentId(id.intValue());
					}

					//2 Transformer les classes
					ArrayList<Level> levelsArray = new ArrayList<Level>();
					for(Level level : spellImport.getLevels()) {
						Integer targetId = playerClassesTranslate.get(new Integer(level.getPlayerClassId()));
						if(targetId == null) {
							PlayerClass sourcePlayerClass = null;
							//Chercher une correspondance
							for(PlayerClass playerClass : playerClassesImport) {
								if(playerClass.getId() == level.getPlayerClassId())
									sourcePlayerClass = playerClass;
							}
							PlayerClass targetPlayerClass = null;
							for(PlayerClass playerClass : playerClassesBase) {
								if(playerClass.getTitle().equalsIgnoreCase(sourcePlayerClass.getTitle()))
									targetPlayerClass = playerClass;
							}
							if(targetPlayerClass == null) {
																			
								PlayerClassMergeDialog dialogPlayerClass = new PlayerClassMergeDialog(shell ,playerClassesBase ,sourcePlayerClass);
								dialogPlayerClass.create();
								if (dialogPlayerClass.open() == Window.OK) {
									switch (dialogPlayerClass.getValue()) {
										case PlayerClassMergeDialog.CREATE:
											targetPlayerClass = new PlayerClass(0,sourcePlayerClass.getTitle(),sourcePlayerClass.getImage(),sourcePlayerClass.getShortName(), sourcePlayerClass.isBase());
											ServiceFactory.getPlayerClassService().save(targetPlayerClass, session);											
											break;
										case PlayerClassMergeDialog.LINK:
											targetPlayerClass = dialogPlayerClass.getTargetItem();
											break;
										default :
									}
								} else {
									session.rollback();
									monitor.setCanceled(true);
									monitor.done();
									this.state = CANCELED;
									this.message = LocaleManager.instance().getMessage("importTaskCanceled");
									return;
								}

							}
							targetId = new Integer(-1);
							if(targetPlayerClass != null)
								targetId = new Integer(targetPlayerClass.getId());
							playerClassesTranslate.put(new Integer(level.getPlayerClassId()), targetId);
						}
						if(targetId.intValue() != -1)
							levelsArray.add(new Level(targetId, level.getLevel()));
					}
					spellImport.removeAllLevels();
					for(Level level : levelsArray) {
						spellImport.addLevel(level);
					}
					

					//3 Transformer l'ecole
					int scoolId = spellImport.getSchoolId();
					Integer targetId = schoolsTranslate.get(new Integer(scoolId));
					if(targetId == null) {
						School sourceSchool = null;
						//Chercher une correspondance
						for(School school : schoolsImport) {
							if(school.getId() == scoolId)
								sourceSchool = school;
						}
						School targetSchool = null;
						for(School school : schoolsBase) {
							if(school.getTitle().equalsIgnoreCase(sourceSchool.getTitle()))
								targetSchool = school;
						}
						if(targetSchool == null) {
							SchoolMergeDialog dialog = new SchoolMergeDialog(shell,schoolsBase ,sourceSchool);
							dialog.create();
							if (dialog.open() == Window.OK) {
								switch (dialog.getValue()) {
									case SchoolMergeDialog.CREATE:
										targetSchool = new School(0,sourceSchool.getTitle(),sourceSchool.getImage());
										ServiceFactory.getSchoolService().save(targetSchool, session);											
										break;
									case SchoolMergeDialog.LINK:
										targetSchool = dialog.getTargetItem();
										break;
									default :
								}
							} else {
								session.rollback();
								monitor.setCanceled(true);
								monitor.done();
								this.state = CANCELED;
								this.message = LocaleManager.instance().getMessage("importTaskCanceled");
								return;
							}
						}
						targetId = new Integer(-1);
						if(targetSchool != null)
							targetId = new Integer(targetSchool.getId());
						schoolsTranslate.put(new Integer(scoolId), targetId);
					}
					spellImport.setSchoolId(0);
					if(targetId.intValue() != -1)
						spellImport.setSchoolId(targetId.intValue());

					//4 Transformer la source
					if(sourcesImport.length > 0) {
						int sourceId = spellImport.getSourceId();
						Integer targetSourceId = sourcesTranslate.get(new Integer(sourceId));
						if(targetSourceId == null) {
							Source sourceSource = null;
							//Chercher une correspondance
							for(Source source : sourcesImport) {
								if(source.getId() == sourceId)
									sourceSource = source;
							}
							Source targetSource = null;
							for(Source source : sourcesBase) {
								if(source.getTitle().equalsIgnoreCase(sourceSource.getTitle()))
									targetSource = source;
							}
							if(targetSource == null) {
								SourceMergeDialog dialog = new SourceMergeDialog(shell,sourcesBase ,sourceSource);
								dialog.create();
								if (dialog.open() == Window.OK) {
									switch (dialog.getValue()) {
										case SourceMergeDialog.CREATE:
											targetSource = new Source(0,sourceSource.getTitle(),sourceSource.getImage(),sourceSource.getShortName());
											ServiceFactory.getSourceService().save(targetSource, session);											
											break;
										case SourceMergeDialog.LINK:
											targetSource = dialog.getTargetItem();
											break;
										default :
									}
								} else {
									session.rollback();
									monitor.setCanceled(true);
									monitor.done();
									this.state = CANCELED;
									this.message = LocaleManager.instance().getMessage("importTaskCanceled");
									return;
								}
							}
							targetSourceId = new Integer(-1);
							if(targetSource != null)
								targetSourceId = new Integer(targetSource.getId());
							sourcesTranslate.put(new Integer(sourceId), targetSourceId);
						}
						spellImport.setSourceId(1);
						if(targetSourceId.intValue() != -1)
							spellImport.setSourceId(targetSourceId.intValue());
					} else {
						spellImport.setSourceId(1);
					}
					
					Spell finalSpell = null;
					for(Spell spellTarget : spellsBase) {
						if(spellTarget.getTitle().equals(spellImport.getTitle())) {
							spellImport.setId(spellTarget.getId());
							spellImport.setImage(spellTarget.getImage());
							spellImport.setBackground(spellTarget.getBackground());
							spellImport.setExist(true);
							finalSpell = spellImport;
						} else if(spellTarget.getOriginalName().equals(spellImport.getOriginalName()) && spellTarget.getId() == spellImport.getId()) {
							spellImport.setExist(true);
							spellImport.setImage(spellTarget.getImage());
							spellImport.setBackground(spellTarget.getBackground());
							finalSpell = spellImport;								
						}
					}
					if(finalSpell == null) {
						SpellMergeDialog dialog = new SpellMergeDialog(shell,spellsBase ,spellImport);
						dialog.create();
						if (dialog.open() == Window.OK) {
							switch (dialog.getValue()) {
								case SpellMergeDialog.CREATE:
									finalSpell = spellImport;											
									break;
								case SpellMergeDialog.REPLACE:
									finalSpell = spellImport;
									finalSpell.setId(dialog.getTargetItem().getId());
									finalSpell.setImage(dialog.getTargetItem().getImage());
									finalSpell.setBackground(dialog.getTargetItem().getBackground());
									finalSpell.setExist(true);
									break;
								default :
							}
						} else {
							session.rollback();
							monitor.setCanceled(true);
							monitor.done();
							this.state = CANCELED;
							this.message = LocaleManager.instance().getMessage("importTaskCanceled");
							return;
						}
					}
					if(finalSpell != null) {
						finalSpell.setDirty(true);
						ServiceFactory.getSpellService().save(finalSpell, session);
						if(dialogOptionsImport.isImportImageEnabled()) {
							if(!importImage.equals(Activator.SPELL_NO_ICON)) {
								writeEntry("Spell_" + importImage, Activator.getPath() + Activator.getSpellImageFolder() +  "icon_" + finalSpell.getId() + ".jpg");
								finalSpell.setImage("icon_" + finalSpell.getId() + ".jpg");
								finalSpell.setDirty(true);
							}
							if(!importBackground.equals(Activator.SPELL_NO_BACKGROUND)) {
								writeEntry("Spell_" + importBackground, Activator.getPath() + Activator.getSpellImageFolder() + "mini_" + finalSpell.getId() + ".jpg");
								finalSpell.setBackground("mini_" + finalSpell.getId() + ".jpg");
								finalSpell.setDirty(true);
							}
							if(finalSpell.isDirty())
								ServiceFactory.getSpellService().save(finalSpell, session);	
						}
					}
					monitor.worked(1);
				}
				for(MagicItem magicItemImport : magicItemsImport) {
					//1 Transformer les composantes
					monitor.subTask(LocaleManager.instance().getMessage("importTaskMagicItem") + " " + magicItemImport.getTitle());
					String importBackground = magicItemImport.getBackground();
					String importImage = magicItemImport.getImage();
					
					MagicItem finalMagicItem = null;
					for(MagicItem magicItemTarget : magicItemsBase) {
						if(magicItemTarget.getTitle().equals(magicItemImport.getTitle())) {
							magicItemImport.setId(magicItemTarget.getId());
							magicItemImport.setImage(magicItemTarget.getImage());
							magicItemImport.setBackground(magicItemTarget.getBackground());
							magicItemImport.setExist(true);
							finalMagicItem = magicItemImport;
						} else if(magicItemTarget.getOriginalName().equals(magicItemImport.getOriginalName()) && magicItemTarget.getId() == magicItemImport.getId()) {
							magicItemImport.setExist(true);
							magicItemImport.setImage(magicItemTarget.getImage());
							magicItemImport.setBackground(magicItemTarget.getBackground());
							finalMagicItem = magicItemImport;								
						}
					}
					if(finalMagicItem == null) {
						MagicItemMergeDialog dialog = new MagicItemMergeDialog(shell,magicItemsBase ,magicItemImport);
						dialog.create();
						if (dialog.open() == Window.OK) {
							switch (dialog.getValue()) {
								case MagicItemMergeDialog.CREATE:
									finalMagicItem = magicItemImport;											
									break;
								case MagicItemMergeDialog.REPLACE:
									finalMagicItem = magicItemImport;
									finalMagicItem.setId(dialog.getTargetItem().getId());
									finalMagicItem.setImage(dialog.getTargetItem().getImage());
									finalMagicItem.setBackground(dialog.getTargetItem().getBackground());
									finalMagicItem.setExist(true);
									break;
								default :
							}
						} else {
							session.rollback();
							monitor.setCanceled(true);
							monitor.done();
							this.state = CANCELED;
							this.message = LocaleManager.instance().getMessage("importTaskCanceled");
							return;
						}
					}
					if(finalMagicItem != null) {
						finalMagicItem.setDirty(true);
						ServiceFactory.getMagicItemService().save(finalMagicItem, session);
						if(dialogOptionsImport.isImportImageEnabled()) {
							if(!importImage.equals(Activator.MAGIC_ITEM_NO_ICON)) {
								writeEntry("MagicItem_" + importImage, Activator.getPath() + Activator.getMagicItemImageFolder() +  "icon_" + finalMagicItem.getId() + ".jpg");
								finalMagicItem.setImage("icon_" + finalMagicItem.getId() + ".jpg");
								finalMagicItem.setDirty(true);
							}
							if(!importBackground.equals(Activator.MAGIC_ITEM_NO_BACKGROUND)) {
								writeEntry("MagicItem_" + importBackground, Activator.getPath() + Activator.getMagicItemImageFolder() + "mini_" + finalMagicItem.getId() + ".jpg");
								finalMagicItem.setBackground("mini_" + finalMagicItem.getId() + ".jpg");
								finalMagicItem.setDirty(true);
							}
							if(finalMagicItem.isDirty())
								ServiceFactory.getMagicItemService().save(finalMagicItem, session);	
						}
					}
					monitor.worked(1);
				}
				this.state = OK;
				this.message = LocaleManager.instance().getMessage("importTaskOk");
				session.commit();

			} else {
				this.state = CANCELED;
				this.message = LocaleManager.instance().getMessage("importTaskCanceled");

			}
			
		} catch (Exception e) {
			e.printStackTrace();
			this.state = ERROR;
			this.message = LocaleManager.instance().getMessage("importTaskError");
			return;
		}finally {
			if(session != null) session.close();
			monitor.done();
		}
		
	}
	
	
}
