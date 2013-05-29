package spell.jobs;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import spell.Activator;
import spell.model.MagicItem;
import spell.model.Spell;
import spell.model.simple.SimpleModel;
import spell.services.ServiceFactory;
import spell.tools.LocaleManager;

public class ExportJob implements IRunnableWithProgress
{
	////////////////////////////////////////////////////////////////////////
	private String message;
	private int state = OK;
	private SimpleModel[] cards;
	private String exportFile;
	private boolean androidExport;
	
	public final static int OK = 0;
	public final static int CANCELED = 1;
	public final static int ERROR = 3;
	
	////////////////////////////////////////////////////////////////////////////
	public ExportJob(SimpleModel[] cards, String exportFile)
	{
		this(cards, exportFile, false);
	}

	public ExportJob(SimpleModel[] cards, String exportFile, boolean androidExport)
	{
		super();
		this.message = "";
		this.cards = cards;
		this.exportFile = exportFile;
		this.androidExport = androidExport;
	}

	public int getState() {
		return state;
	}

	public String getMessage() {
		return message;
	}
	
	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {
		try {

			monitor.beginTask(LocaleManager.instance().getMessage("exportTask"), 8);
	        
			FileOutputStream dest = new FileOutputStream(exportFile);
			//Ecriture des données dans un tableau de bytes			
			BufferedOutputStream bout = new BufferedOutputStream(dest);	
			//Création du flux zippé 
			ZipOutputStream zipout = new ZipOutputStream(bout);	
			zipout.setMethod(ZipOutputStream.DEFLATED);
			zipout.setLevel(Deflater.BEST_COMPRESSION);

			Document document = null;
			// Création d'un nouveau DOM
			DocumentBuilderFactory fabrique = DocumentBuilderFactory.newInstance();
			DocumentBuilder constructeur = fabrique.newDocumentBuilder();
			document = constructeur.newDocument();
			
			// Propriétés du DOM
			document.setXmlVersion("1.0");
			document.setXmlStandalone(true);

			Element infos = document.createElement("infos");
			monitor.subTask(LocaleManager.instance().getMessage("exportTask1"));
			infos.appendChild(ServiceFactory.getPlayerClassService().exportXML(document, ServiceFactory.getPlayerClassService().getCached()));
			monitor.worked(1);
			monitor.subTask(LocaleManager.instance().getMessage("exportTask2"));
			infos.appendChild(ServiceFactory.getComponentService().exportXML(document, ServiceFactory.getComponentService().getCached()));
			monitor.worked(1);
			monitor.subTask(LocaleManager.instance().getMessage("exportTask3"));
			infos.appendChild(ServiceFactory.getSchoolService().exportXML(document, ServiceFactory.getSchoolService().getCached()));
			monitor.worked(1);
			monitor.subTask(LocaleManager.instance().getMessage("exportTask4"));
			infos.appendChild(ServiceFactory.getSourceService().exportXML(document, ServiceFactory.getSourceService().getCached()));
			monitor.worked(1);
			monitor.subTask(LocaleManager.instance().getMessage("exportTask5"));
			ArrayList<Spell> spellList = new ArrayList<Spell>();
			ArrayList<MagicItem> magicItemList = new ArrayList<MagicItem>();
			for(SimpleModel card : cards) {
				if(card instanceof Spell) {
					spellList.add((Spell)card);
				} else if(card instanceof MagicItem) {
					magicItemList.add((MagicItem)card);
					
				}
			}
			Spell[] spells = new Spell[spellList.size()];
			spellList.toArray(spells);
			infos.appendChild(ServiceFactory.getSpellService().exportXML(document, spells, androidExport));
			monitor.worked(1);
			monitor.subTask(LocaleManager.instance().getMessage("exportTask6"));
			MagicItem[] magicItems = new MagicItem[magicItemList.size()];
			magicItemList.toArray(magicItems);
			infos.appendChild(ServiceFactory.getMagicItemService().exportXML(document, magicItems, androidExport));
			monitor.worked(1);
						
			monitor.subTask(LocaleManager.instance().getMessage("exportTask7"));
			document.appendChild(infos);
			if(document != null) {
	            // Création de la source DOM
	            Source source = new DOMSource(document);
	    
	            // Création du fichier de sortie
	            ByteArrayOutputStream out = new ByteArrayOutputStream();
	            Result result = new StreamResult(out);
	            
	            // Configuration du transformer
	            TransformerFactory builder = TransformerFactory.newInstance();
	            Transformer transformer = builder.newTransformer();
	            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	            transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
	            
	            transformer.transform(source, result);
				ZipEntry entry = new ZipEntry("cards.xml");
				zipout.putNextEntry(entry);
				zipout.write(out.toByteArray());
				zipout.closeEntry();
			}
			
			monitor.worked(1);
			
			
			
			monitor.subTask(LocaleManager.instance().getMessage("exportTask8"));
			int BUFFER = 2048;
			byte data[] = new byte[BUFFER];
			for(Spell spell : spells) {
				if(!spell.getImage().equals(Activator.SPELL_NO_ICON)) {
					File imageFile = new File(Activator.getPath() + Activator.getSpellImageFolder() + spell.getImage());
					if(imageFile.exists()) {
						ZipEntry entry = new ZipEntry("Spell_" + spell.getImage());
						zipout.putNextEntry(entry);
						FileInputStream fi = new FileInputStream(imageFile);
						BufferedInputStream buffi = new BufferedInputStream(fi, BUFFER);
						int count;
						while((count = buffi.read(data, 0, BUFFER)) != -1) {
							zipout.write(data, 0, count);
						}
						//Fermeture de la première entrée de l'archive
						zipout.closeEntry();
						buffi.close();
					}
				}
				if(!androidExport && !spell.getBackground().equals(Activator.SPELL_NO_BACKGROUND)) {
					File imageFile = new File(Activator.getPath() + Activator.getSpellImageFolder() + spell.getBackground());
					if(imageFile.exists()) {
						ZipEntry entry = new ZipEntry("Spell_" + spell.getBackground());
						zipout.putNextEntry(entry);
						FileInputStream fi = new FileInputStream(imageFile);
						BufferedInputStream buffi = new BufferedInputStream(fi, BUFFER);
						int count;
						while((count = buffi.read(data, 0, BUFFER)) != -1) {
							zipout.write(data, 0, count);
						}
						//Fermeture de la première entrée de l'archive
						zipout.closeEntry();
						buffi.close();
					}
				}
			}
			for(MagicItem magicItem : magicItems) {
				if(!magicItem.getImage().equals(Activator.MAGIC_ITEM_NO_ICON)) {
					File imageFile = new File(Activator.getPath() + Activator.getMagicItemImageFolder() + magicItem.getImage());
					if(imageFile.exists()) {
						ZipEntry entry = new ZipEntry("MagicItem_" + magicItem.getImage());
						zipout.putNextEntry(entry);
						FileInputStream fi = new FileInputStream(imageFile);
						BufferedInputStream buffi = new BufferedInputStream(fi, BUFFER);
						int count;
						while((count = buffi.read(data, 0, BUFFER)) != -1) {
							zipout.write(data, 0, count);
						}
						//Fermeture de la première entrée de l'archive
						zipout.closeEntry();
						buffi.close();
					}
				}
				if(!androidExport && !magicItem.getBackground().equals(Activator.MAGIC_ITEM_NO_BACKGROUND)) {
					File imageFile = new File(Activator.getPath() + Activator.getMagicItemImageFolder() + magicItem.getBackground());
					if(imageFile.exists()) {
						ZipEntry entry = new ZipEntry("MagicItem_" + magicItem.getBackground());
						zipout.putNextEntry(entry);
						FileInputStream fi = new FileInputStream(imageFile);
						BufferedInputStream buffi = new BufferedInputStream(fi, BUFFER);
						int count;
						while((count = buffi.read(data, 0, BUFFER)) != -1) {
							zipout.write(data, 0, count);
						}
						//Fermeture de la première entrée de l'archive
						zipout.closeEntry();
						buffi.close();
					}
				}
			}
			monitor.worked(1);

			
			
			//Fermeture de l'archive
			zipout.finish();
			//Fermeture du flux
			zipout.close();

			this.state = OK;
			this.message = LocaleManager.instance().getMessage("exportTaskOk");
				
        }catch(Exception e){
			this.state = ERROR;
			this.message = LocaleManager.instance().getMessage("exportTaskError");
			e.printStackTrace();	
        } finally {
        	monitor.done();
        }
	}
	
	
}
