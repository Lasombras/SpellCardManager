package spell.tools;


import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import spell.Activator;
import spell.model.Component;
import spell.model.Level;
import spell.model.MagicItem;
import spell.model.PlayerClass;
import spell.model.Spell;
import spell.model.simple.SimpleModel;
import spell.preferences.ImpressionPage;
import spell.services.ServiceFactory;

public class GCCardBuilder {
			
	private final static int CARD_FULL = Integer.MAX_VALUE;
	public final static int NORMAL_FORMAT = 0;
	public final static int SPELLSOURCE_FORMAT = 1;
	public final static int CUSTOM_FORMAT = 2;
	public final static int MAX_WIDTH = 400;
	
	private static GCCardBuilder instance;
	public static GCCardBuilder instance() {
		if(instance==null) {
			instance= new GCCardBuilder();
		}
		return instance;
	}

	private int cardHeight;
	private int cardWidth;
	private int format;

	private Document documentXML;
	private String templateFolder;
	private GCCardBuilder() {
		try {
			this.templateFolder = Activator.getDefault().getPreferenceStore().getString(ImpressionPage.CARD_TEMPLATE) + File.separator;
			String fileName = Activator.getPath() + Activator.FOLDER_TEMPLATES + templateFolder  + "card.xml";
			DocumentBuilderFactory fabrique = DocumentBuilderFactory.newInstance();
			DocumentBuilder constructeur = fabrique.newDocumentBuilder();
			File xml = new File(fileName);
			documentXML = constructeur.parse(xml);	
			
			format = NORMAL_FORMAT;
			cardHeight = 490;
			cardWidth = 350;			
			Element racine = documentXML.getDocumentElement();
			NodeList formatNode = racine.getElementsByTagName("format");
			if(formatNode.getLength() > 0) {
				Element formatElement = ((Element)formatNode.item(0));
				String type = formatElement.getAttribute("type");
				if(type.equalsIgnoreCase("spellSource")) {
					format = SPELLSOURCE_FORMAT;
					cardHeight = 490;
					cardWidth = 700;
				} else if(type.equalsIgnoreCase("custom")) {
					format = CUSTOM_FORMAT;
					cardHeight = Integer.parseInt(formatElement.getAttribute("height"));
					cardWidth = Integer.parseInt(formatElement.getAttribute("width"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void draw(GC gc, SimpleModel model) {
	 	
		try {
			Device display = gc.getDevice();
			Element cardElement = null;
			Element racine = documentXML.getDocumentElement();
						
			//Chargement de la bibliotheque des Fonts
			NodeList fonts = racine.getElementsByTagName("font");
			for(int i = 0; i < fonts.getLength(); i++) {
				Element fontElement = ((Element)fonts.item(i));
				display.loadFont(Activator.getPath() + Activator.FOLDER_TEMPLATES + templateFolder + fontElement.getAttribute("file"));
			}
	
			if(model instanceof Spell) {
				Spell spellModel = (Spell)model;
				NodeList cards = racine.getElementsByTagName("card");
				for(int i = 0; i < cards.getLength(); i++) {
					if(Integer.parseInt(((Element)cards.item(i)).getAttribute("school")) == spellModel.getSchoolId()) {
						cardElement = (Element)cards.item(i);
					}
				}
				
		
				
				//1. Background
		        NodeList backGroundNode = cardElement.getElementsByTagName("background");
		        if(!spellModel.getBackground().equalsIgnoreCase(Activator.SPELL_NO_BACKGROUND) && backGroundNode.getLength() > 0) {
		        	GCCardElement backgroundElement =  new GCCardElement(gc,(Element)backGroundNode.item(0));
		            Image background = Activator.getImage(Activator.getSpellImageFolder() + spellModel.getBackground());
		            gc.drawImage(background, 0, 0, background.getImageData().width, background.getImageData().height,
		            		backgroundElement.getX(),
		            		backgroundElement.getY(),
		            		backgroundElement.getWidth(),
		            		backgroundElement.getHeight());
		        }
	
		        //2. Logo
		        NodeList logoNode = cardElement.getElementsByTagName("logo");
		        if(logoNode.getLength() > 0) {
					Image logo = Activator.getImage(Activator.getSpellImageFolder() + spellModel.getImage(), Activator.getSpellImageFolder() + Activator.SPELL_NO_ICON);
			        GCCardElement logoElement = new GCCardElement(gc, (Element)logoNode.item(0));
			        gc.drawImage(logo, logoElement.getX(), logoElement.getY());
		        }
		        
		        //3. Mask
			    Image mask = Activator.getImage(Activator.FOLDER_TEMPLATES + templateFolder + cardElement.getAttribute("pattern"));
		 	    gc.drawImage(mask,0, 0);
				
		        //3. Components  
		    	Element componentsElement = (Element)cardElement.getElementsByTagName("components").item(0);
		    	//Charger les positions des Composantes
		    	Hashtable<String, Element> htPositions = new Hashtable<String, Element>();
				NodeList positions = componentsElement.getElementsByTagName("position");
				for(int i = 0; i < positions.getLength(); i++) {
					Element position = (Element)positions.item(i);
					htPositions.put(position.getAttribute("id"), position);
				}
		 
		    	ArrayList<Integer> compsID = spellModel.getComponentsId();
		    	for(int i = 0; i < compsID.size(); i++) {
		    		int compID = compsID.get(i).intValue();
		    		Component component = ServiceFactory.getComponentService().getCached(compID);
		    		// Get Component Info in XML
		    		NodeList components = componentsElement.getElementsByTagName("component");
		    		Element componentNode = null;
		    		for(int j = 0; j < components.getLength(); j++) {
		    			if(Integer.parseInt(((Element)components.item(j)).getAttribute("id")) == compID) {
		    				componentNode = (Element)components.item(j);
		    			}
		    		}
		    		GCCardElement componentElement = new GCCardElement(gc, componentNode);
		    		Element positionElement = null;
		    		if(componentNode.getAttribute("position").equals("auto")) {
		    			positionElement = (Element)positions.item(i);
		    		} else {
		    			positionElement = htPositions.get(componentNode.getAttribute("position"));
		    		}
		   			componentElement.setX(Integer.parseInt(positionElement.getAttribute("x")));
		   			componentElement.setY(Integer.parseInt(positionElement.getAttribute("y")));
		  		    		
		    		if(componentNode.getAttribute("image") != null && !componentNode.getAttribute("image").equals("")) {
		    			Image dot = Activator.getImage(Activator.FOLDER_TEMPLATES + templateFolder+ componentNode.getAttribute("image"));
		    			gc.drawImage(dot, componentElement.getX(), componentElement.getY());
		    		}
		            componentElement.drawString(component.getShortName());
		     	}
	
	
		    	int filterClass = Activator.getDefault().getPreferenceStore().getInt(ImpressionPage.PLAYER_CLASS);
		    	if(filterClass != ImpressionPage.PLAYER_CLASS_NONE) {
			    	GCCardElement levelElement = new GCCardElement(gc,(Element)cardElement.getElementsByTagName("level").item(0));
			    	String levelStr = "";
			        for(Level level : spellModel.getLevels()) {
			        	PlayerClass playerClass = ServiceFactory.getPlayerClassService().getCached(level.getPlayerClassId());
			        	if( 	(filterClass == ImpressionPage.PLAYER_CLASS_ALL) ||
			        			(filterClass == ImpressionPage.PLAYER_CLASS_BASE && playerClass.isBase()) ||
			        			(filterClass == level.getPlayerClassId()) ){
				        	if(!levelStr.isEmpty()) levelStr += ", ";
				        	levelStr +=  playerClass.getShortName() + " " + level.getLevel();
			        	}
			        }
			        levelElement.drawString(levelStr);
		    	}
		    	
		    	//5. Title
		    	GCCardElement titleElement = new GCCardElement(gc,(Element)cardElement.getElementsByTagName("title").item(0));
		    	titleElement.drawString(spellModel.getTitle());
		 	
		   	
		    	//6. School
		    	String school = ServiceFactory.getSchoolService().getCached(spellModel.getSchoolId()).getTitle();
		    	if(!spellModel.getDescriptor().isEmpty())
		    		school += " [" + spellModel.getDescriptor() + "]";
		    	GCCardElement schoolElement = new GCCardElement(gc,(Element)cardElement.getElementsByTagName("school").item(0));
		    	schoolElement.drawString(school);
		    			
		        
		    	
		    	/*
		    	int y = 0;
		    	for(int i = 0; i < spellModel.getOriginalName().length(); i++){
		    		String charStr = spellModel.getOriginalName().charAt(i) + "";
		    		Point pt = gc.textExtent(charStr);
					int padding = (10 - pt.x)/2;
		        	gc.drawString(charStr, padding + 6, 6+y, true);
		        	y +=pt.y;
		    	}
		    	*/
		    	
	    	
		       	Element descriptionNode = (Element)cardElement.getElementsByTagName("description").item(0);
		    	GCCardElement descriptionElement = new GCCardElement(gc,descriptionNode);   	
		    	GCCardElement titleDescriptionElement =  new GCCardElement(gc,(Element)descriptionNode.getElementsByTagName("title").item(0));
		    	GCCardElement valueDescriptionElement =  new GCCardElement(gc,(Element)descriptionNode.getElementsByTagName("value").item(0));
		
		    	Font textFont = valueDescriptionElement.createFont();
		    	Font titleFont = titleDescriptionElement.createFont();
		    	
		    	ArrayList<String> titleList = new ArrayList<String>();
		    	ArrayList<String> contentList = new ArrayList<String>();
		    	titleList.add(LocaleManager.instance().getMessage("castingTime") + " : "); 	contentList.add(spellModel.getCastingTime());
		    	titleList.add(LocaleManager.instance().getMessage("material") + " : "); 	contentList.add(spellModel.getMaterial());
		    	titleList.add(LocaleManager.instance().getMessage("duration") + " : "); 	contentList.add(spellModel.getDuration());
		    	titleList.add(LocaleManager.instance().getMessage("range") + " : "); 	contentList.add(spellModel.getRange());
		    	titleList.add(LocaleManager.instance().getMessage("target") + " : "); 	contentList.add(spellModel.getTarget());
		    	titleList.add(LocaleManager.instance().getMessage("area") + " : "); 	contentList.add(spellModel.getArea());
		    	titleList.add(LocaleManager.instance().getMessage("savingThrow") + " : "); 	contentList.add(spellModel.getSavingThrow());
		    	titleList.add(LocaleManager.instance().getMessage("spellResistance") + " : "); 	contentList.add(spellModel.isSpellResistance()?LocaleManager.instance().getMessage("yes"):LocaleManager.instance().getMessage("no"));
	   	
		    	int posY = descriptionElement.getY()-2;
		    	if(cardWidth > MAX_WIDTH) {
		    		//Simulation de l'espace
			    	int halfWidth = descriptionElement.getWidth() / 2;
			    	for(int i = 0; i < titleList.size(); i++) {
				    	posY = printText(titleList.get(i),contentList.get(i),gc,valueDescriptionElement, textFont, titleDescriptionElement, titleFont, posY, descriptionElement.getHeight()+descriptionElement.getY(), halfWidth, descriptionElement.getX(),false);
			    	}
			    	int halfHeight = ((posY - descriptionElement.getY() - 2) / 2) + descriptionElement.getY() - 2;
			    	
			    	int indentX = 0;
			    	int maxY = 0;
			    	posY = descriptionElement.getY()-2;
			    	for(int i = 0; i < titleList.size(); i++) {
				    	posY = printText(titleList.get(i),contentList.get(i),gc,valueDescriptionElement, textFont, titleDescriptionElement, titleFont, posY, descriptionElement.getHeight()+descriptionElement.getY(), halfWidth, descriptionElement.getX()+indentX);
				    	if(posY > halfHeight) {
				    		if(posY > maxY)
				    			maxY = posY;
					    	indentX = halfWidth;
					    	posY = descriptionElement.getY()-2;
				    	}
			    	}
			    	if(posY < maxY)
			    		posY = maxY;
		    	} else {
			    	for(int i = 0; i < titleList.size(); i++) {
				    	posY = printText(titleList.get(i),contentList.get(i),gc,valueDescriptionElement, textFont, titleDescriptionElement, titleFont, posY, descriptionElement.getHeight()+descriptionElement.getY(), descriptionElement.getWidth(), descriptionElement.getX());
			    	}
		    	}
	
				gc.setTextAntialias(SWT.ON);			
				gc.setAntialias(SWT.ON);			

				String remark = "";
		    	if(!spellModel.getCardText().isEmpty()) {
		    		remark = "R";
		    		posY = printText("",spellModel.getCardText(),gc,valueDescriptionElement, textFont, titleDescriptionElement, titleFont, posY+5, descriptionElement.getHeight()+descriptionElement.getY(), descriptionElement.getWidth(), descriptionElement.getX());    		
		    	} else {
		    		posY = printText("",spellModel.getDetail(false),gc,valueDescriptionElement, textFont, titleDescriptionElement, titleFont, posY+5, descriptionElement.getHeight()+descriptionElement.getY(), descriptionElement.getWidth(), descriptionElement.getX());
		    	}
		    	if(posY == CARD_FULL)
		    		remark = "I";	    	
		    	
		    	if(!remark.isEmpty()){
		    		Element remarkNode = (Element)cardElement.getElementsByTagName("remark").item(0);
		        	GCCardElement remarkElement = new GCCardElement(gc, remarkNode);
		    		if(remarkNode.getAttribute("image") != null && !remarkNode.getAttribute("image").equals("")) {
		    			Image dot = Activator.getImage(Activator.FOLDER_TEMPLATES + templateFolder + remarkNode.getAttribute("image"));
		    			gc.drawImage(dot, remarkElement.getX(), remarkElement.getY());
		    		}
		        	remarkElement.drawString(remark);
		    	}
		    	textFont.dispose();
		    	titleFont.dispose();
		      
		    	Element pageNode = (Element)cardElement.getElementsByTagName("page").item(0);
	    		if(pageNode != null && !spellModel.getPage().equals("")) {
		    		GCCardElement pageElement = new GCCardElement(gc,pageNode);
		    		if(pageNode.getAttribute("image") != null && !pageNode.getAttribute("image").equals("")) {
		    			Image dot = Activator.getImage(Activator.FOLDER_TEMPLATES + templateFolder + pageNode.getAttribute("image"));
		    			gc.drawImage(dot, pageElement.getX(), pageElement.getY());
		    		}
			        pageElement.drawString(spellModel.getPage());
		    	}
		    	Element sourceNode = (Element)cardElement.getElementsByTagName("source").item(0);
	    		if(sourceNode != null && spellModel.getSourceId() > 0) {
	    			String sourceName = ServiceFactory.getSourceService().getCached(spellModel.getSourceId()).getShortName();
	    			if(sourceName.length() > 0) {
			    		GCCardElement pageElement = new GCCardElement(gc,sourceNode);
			    		if(sourceNode.getAttribute("image") != null && !sourceNode.getAttribute("image").equals("")) {
			    			Image dot = Activator.getImage(Activator.FOLDER_TEMPLATES + templateFolder + sourceNode.getAttribute("image"));
			    			gc.drawImage(dot, pageElement.getX(), pageElement.getY());
			    		}
				        pageElement.drawString(sourceName);
	    			}
		    	}
			} else if(model instanceof MagicItem) {
				MagicItem magicItem = (MagicItem)model;
				cardElement = (Element)racine.getElementsByTagName("item").item(0);
			
				//1. Background
		        NodeList backgroundNode = cardElement.getElementsByTagName("background");
		        if(!magicItem.getBackground().equalsIgnoreCase(Activator.MAGIC_ITEM_NO_BACKGROUND) && backgroundNode.getLength() > 0) {
		        	GCCardElement backgroundElement =  new GCCardElement(gc,(Element)backgroundNode.item(0));
		            Image background = Activator.getImage(Activator.getMagicItemImageFolder() + magicItem.getBackground());
		            gc.drawImage(background, 0, 0, background.getImageData().width, background.getImageData().height,
		            		backgroundElement.getX(),
		            		backgroundElement.getY(),
		            		backgroundElement.getWidth(),
		            		backgroundElement.getHeight());
		        }
	
		        //2. Logo
		        NodeList logoNode = cardElement.getElementsByTagName("logo");
		        if(logoNode.getLength() > 0) {
					Image logo = Activator.getImage(Activator.getMagicItemImageFolder() + magicItem.getImage(), Activator.getMagicItemImageFolder() + Activator.MAGIC_ITEM_NO_ICON);
			        GCCardElement logoElement = new GCCardElement(gc, (Element)logoNode.item(0));
			        gc.drawImage(logo, logoElement.getX(), logoElement.getY());
		        }
		        
		        //3. Mask
			    Image mask = Activator.getImage(Activator.FOLDER_TEMPLATES + templateFolder + cardElement.getAttribute("pattern"));
		 	    gc.drawImage(mask,0, 0);
				
		        //4. Item Type 
		 	    NodeList itemTypeImageNodeList = cardElement.getElementsByTagName("slot");
		 	    if(itemTypeImageNodeList.getLength() > 0) {
			    	Element itemTypeImageNode = (Element)itemTypeImageNodeList.item(0);
			    	Image slotImage = null;
					if(magicItem.getSlotId() > 1)
						slotImage = Activator.getImage(Activator.FOLDER_IMAGES + ServiceFactory.getSlotService().getCached(magicItem.getSlotId()).getImage());
					else 
						slotImage = Activator.getImage(Activator.FOLDER_IMAGES + ServiceFactory.getItemTypeService().getCached(magicItem.getItemTypeId()).getImage());						

		    		if(itemTypeImageNode != null) {
			    		GCCardElement itemTypeImageElement = new GCCardElement(gc,itemTypeImageNode);
			    		if(itemTypeImageNode.getAttribute("image") != null && !itemTypeImageNode.getAttribute("image").equals("")) {
			    			Image dot = Activator.getImage(Activator.FOLDER_TEMPLATES + templateFolder + itemTypeImageNode.getAttribute("image"));
			    			gc.drawImage(dot, itemTypeImageElement.getX(), itemTypeImageElement.getY());
			    		}
			    		
			    		gc.drawImage(slotImage, itemTypeImageElement.getX() + (itemTypeImageElement.getWidth() - 16)/2, itemTypeImageElement.getY() + (itemTypeImageElement.getHeight() - 16)/2);
			    	}	
		 	    }
		 	    
		    	//5. Title
		 	    NodeList specificNodeList = cardElement.getElementsByTagName("specific");
		 	    if(specificNodeList.getLength() > 0 && magicItem.getTitle().lastIndexOf(',') > -1) {
		 	    	String title = magicItem.getTitle().substring(0, magicItem.getTitle().lastIndexOf(','));
		 	    	String specific = magicItem.getTitle().substring(magicItem.getTitle().lastIndexOf(',')+1).trim();
			    	GCCardElement titleElement = new GCCardElement(gc,(Element)cardElement.getElementsByTagName("title").item(0));
			    	titleElement.drawString(title);
			    	GCCardElement titleSpecificElement = new GCCardElement(gc,(Element)specificNodeList.item(0));
			    	titleSpecificElement.drawString(specific);
		 	    	
		 	    } else {
			    	GCCardElement titleElement = new GCCardElement(gc,(Element)cardElement.getElementsByTagName("title").item(0));
			    	titleElement.drawString(magicItem.getTitle());		 	    	
		 	    }
		    	
		    			 	
		    	//6. ItemType
		    	String itemType = ServiceFactory.getItemTypeService().getCached(magicItem.getItemTypeId()).getTitle();
		    	GCCardElement itemTypeElement = new GCCardElement(gc,(Element)cardElement.getElementsByTagName("itemType").item(0));
		    	itemTypeElement.drawString(itemType);
		    				    	
		       	Element descriptionNode = (Element)cardElement.getElementsByTagName("description").item(0);
		    	GCCardElement descriptionElement = new GCCardElement(gc,descriptionNode);   	
		    	GCCardElement titleDescriptionElement =  new GCCardElement(gc,(Element)descriptionNode.getElementsByTagName("title").item(0));
		    	GCCardElement valueDescriptionElement =  new GCCardElement(gc,(Element)descriptionNode.getElementsByTagName("value").item(0));
		
		    	Font textFont = valueDescriptionElement.createFont();
		    	Font titleFont = titleDescriptionElement.createFont();
		    	
		    	ArrayList<String> titleList = new ArrayList<String>();
		    	ArrayList<String> contentList = new ArrayList<String>();
		    	titleList.add(LocaleManager.instance().getMessage("aura") + " : ");
		    	contentList.add(magicItem.getAura());
		    	titleList.add(LocaleManager.instance().getMessage("casterLevelShort") + " : ");
		    	contentList.add(magicItem.getCasterLevel());
	    		String slotStr = ServiceFactory.getSlotService().getCached(magicItem.getSlotId()).getTitle();
		    	titleList.add(LocaleManager.instance().getMessage("slot") + " : ");
		    	contentList.add(slotStr);
		    	if(magicItem.getPrice() > 0) {
		    		titleList.add(LocaleManager.instance().getMessage("price") + " : ");
		    		contentList.add(Activator.formatPrice(magicItem.getPrice()));
		    	}
		    	titleList.add(LocaleManager.instance().getMessage("weight") + " : ");
		    	contentList.add(magicItem.getWeight());
	   	
		    	int posY = descriptionElement.getY()-2;
		    	if(cardWidth > MAX_WIDTH) {
		    		//Simulation de l'espace
			    	int halfWidth = descriptionElement.getWidth() / 2;
			    	for(int i = 0; i < titleList.size(); i++) {
				    	posY = printText(titleList.get(i),contentList.get(i),gc,valueDescriptionElement, textFont, titleDescriptionElement, titleFont, posY, descriptionElement.getHeight()+descriptionElement.getY(), halfWidth, descriptionElement.getX(),false);
			    	}
			    	int halfHeight = ((posY - descriptionElement.getY() - 2) / 2) + descriptionElement.getY() - 2;
			    	
			    	int indentX = 0;
			    	int maxY = 0;
			    	posY = descriptionElement.getY()-2;
			    	for(int i = 0; i < titleList.size(); i++) {
				    	posY = printText(titleList.get(i),contentList.get(i),gc,valueDescriptionElement, textFont, titleDescriptionElement, titleFont, posY, descriptionElement.getHeight()+descriptionElement.getY(), halfWidth, descriptionElement.getX()+indentX);
				    	if(posY > halfHeight) {
				    		if(posY > maxY)
				    			maxY = posY;
					    	indentX = halfWidth;
					    	posY = descriptionElement.getY()-2;
				    	}
			    	}
			    	if(posY < maxY)
			    		posY = maxY;
		    	} else {
			    	for(int i = 0; i < titleList.size(); i++) {
				    	posY = printText(titleList.get(i),contentList.get(i),gc,valueDescriptionElement, textFont, titleDescriptionElement, titleFont, posY, descriptionElement.getHeight()+descriptionElement.getY(), descriptionElement.getWidth(), descriptionElement.getX());
			    	}
		    	}
		    	
				gc.setTextAntialias(SWT.ON);			
				gc.setAntialias(SWT.ON);			

				String remark = "";
		    	if(!magicItem.getCardText().isEmpty()) {
		    		remark = "R";
		    		posY = printText("",magicItem.getCardText(),gc,valueDescriptionElement, textFont, titleDescriptionElement, titleFont, posY+5, descriptionElement.getHeight()+descriptionElement.getY(), descriptionElement.getWidth(), descriptionElement.getX());    		
		    	} else {
		    		posY = printText("",magicItem.getDetail(false),gc,valueDescriptionElement, textFont, titleDescriptionElement, titleFont, posY+5, descriptionElement.getHeight()+descriptionElement.getY(), descriptionElement.getWidth(), descriptionElement.getX());
		    	}
		    	if(posY == CARD_FULL)
		    		remark = "I";	    	
		    	
		    	if(!remark.isEmpty()){
		    		Element remarkNode = (Element)cardElement.getElementsByTagName("remark").item(0);
		        	GCCardElement remarkElement = new GCCardElement(gc, remarkNode);
		    		if(remarkNode.getAttribute("image") != null && !remarkNode.getAttribute("image").equals("")) {
		    			Image dot = Activator.getImage(Activator.FOLDER_TEMPLATES + templateFolder + remarkNode.getAttribute("image"));
		    			gc.drawImage(dot, remarkElement.getX(), remarkElement.getY());
		    		}
		        	remarkElement.drawString(remark);
		    	}
		    	textFont.dispose();
		    	titleFont.dispose();
		      
		    	Element pageNode = (Element)cardElement.getElementsByTagName("page").item(0);
	    		if(pageNode != null && !magicItem.getPage().equals("")) {
		    		GCCardElement pageElement = new GCCardElement(gc,pageNode);
		    		if(pageNode.getAttribute("image") != null && !pageNode.getAttribute("image").equals("")) {
		    			Image dot = Activator.getImage(Activator.FOLDER_TEMPLATES + templateFolder + pageNode.getAttribute("image"));
		    			gc.drawImage(dot, pageElement.getX(), pageElement.getY());
		    		}
			        pageElement.drawString(magicItem.getPage());
		    	}

		    	Element sourceNode = (Element)cardElement.getElementsByTagName("source").item(0);
	    		if(sourceNode != null && magicItem.getSourceId() > 0) {
	    			String sourceName = ServiceFactory.getSourceService().getCached(magicItem.getSourceId()).getShortName();
	    			if(sourceName.length() > 0) {
			    		GCCardElement pageElement = new GCCardElement(gc,sourceNode);
			    		if(sourceNode.getAttribute("image") != null && !sourceNode.getAttribute("image").equals("")) {
			    			Image dot = Activator.getImage(Activator.FOLDER_TEMPLATES + templateFolder + sourceNode.getAttribute("image"));
			    			gc.drawImage(dot, pageElement.getX(), pageElement.getY());
			    		}
				        pageElement.drawString(sourceName);
	    			}
		    	}

			}

		
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}

	
	private int printText(String title, String text, GC gc , GCCardElement textElement, Font textFont, GCCardElement titleElement, Font titleFont, int startY, int maxY, int width, int x) {
		return printText(title, text, gc , textElement, textFont, titleElement, titleFont, startY, maxY, width, x, true);
	}
	
	private int printText(String title, String text, GC gc , GCCardElement textElement, Font textFont, GCCardElement titleElement, Font titleFont, int startY, int maxY, int width, int startX, boolean print) {
      	if(text == null || text.trim().isEmpty()) return startY;
      	if(startY >= maxY || startY == CARD_FULL) return CARD_FULL;
     	int xTitle = 0;
      	if(title != null && !title.trim().isEmpty()) {
    		gc.setForeground(titleElement.getForeground());
     		gc.setFont(titleFont);

    		xTitle = gc.textExtent(title).x;
    		if(titleElement.getFontHeight() + startY > maxY) return CARD_FULL;
    		if(print)
    			gc.drawString(title, startX , startY,true);
    		   		
     	}
     	
     	gc.setForeground(textElement.getForeground());
		gc.setFont(textFont);
		String[] lines = text.split("\n");
		int fontHeight = textElement.getFontHeight();
     	for(String line : lines) {
			String[] words = line.split(" ");
	      	String lineToPrint = "";
	      	for(String word : words) {
		    	int textWidth = gc.textExtent(lineToPrint + " " + word).x;
	      		if(textWidth + xTitle > width) {
	        		if(fontHeight+ startY > maxY) return CARD_FULL;
	        		if(print)
	        			gc.drawString(lineToPrint.trim(), startX + xTitle , startY,true);
	    			xTitle = 0;
	    			startY += fontHeight;
	    			lineToPrint = word;
	      		} else {
	      			lineToPrint = lineToPrint + " " + word;
	      		}
	      	}
	      	if(!lineToPrint.isEmpty()) {
	    		if(fontHeight + startY > maxY) return CARD_FULL;
	    		if(print)
	    			gc.drawString(lineToPrint.trim(), startX + xTitle, startY,true);
				startY += fontHeight;
	      	}
     	}
        return startY;	
	}

	public int getCardHeight() {
		return cardHeight;
	}

	public int getCardWidth() {
		return cardWidth;
	}


	public int getFormat() {
		return format;
	}

	
}
