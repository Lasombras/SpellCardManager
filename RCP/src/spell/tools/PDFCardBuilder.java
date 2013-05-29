package spell.tools;

import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

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
import spell.tools.CacheManager.BaseFontManager;
import spell.tools.CacheManager.ImagePDFManager;

import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.codec.wmf.Point;

public class PDFCardBuilder {
	
	public final static float CARD_RATION = 0.5f;
	public final static int NORMAL_FORMAT = 0;
	public final static int SPELLSOURCE_FORMAT = 1;
	public final static int CUSTOM_FORMAT = 2;
	public final static int MAX_WIDTH = 400;
	public final static int MARGE = 50;
		
	private final static int CARD_FULL = Integer.MAX_VALUE;
	private Document documentXML;
	private Vector<SimpleModel> simpleModels;	
	private String templateFolder;
	private int cardHeight;
	private int cardWidth;
	private int format;
	private int printFormat;

	
	public PDFCardBuilder() {
		try {
			DocumentBuilderFactory fabrique = DocumentBuilderFactory.newInstance();
			DocumentBuilder constructeur = fabrique.newDocumentBuilder();
			this.templateFolder = Activator.getDefault().getPreferenceStore().getString(ImpressionPage.CARD_TEMPLATE) + File.separator;
			this.printFormat = Activator.getDefault().getPreferenceStore().getInt(ImpressionPage.PRINT_FORMAT);
			File xml = new File(Activator.getPath() + Activator.FOLDER_TEMPLATES + templateFolder + "card.xml");
			documentXML = constructeur.parse(xml);	
			simpleModels = new Vector<SimpleModel>();
			
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
	
	public void add(SimpleModel model) {
		for(int i = 0; i < model.getSize(); i++) {
			simpleModels.add(model);
		}
	}
		
	public void draw(String pdfFileName, boolean frontBack) {
		try {
						
			Element racine = documentXML.getDocumentElement();
			int cardMarge = Activator.getDefault().getPreferenceStore().getInt(ImpressionPage.CARD_MARGE);
	
			//Chargement de la bibliotheque des Fonts
			NodeList fonts = racine.getElementsByTagName("font");
			for(int i = 0; i < fonts.getLength(); i++) {
				Element fontElement = ((Element)fonts.item(i));
				BaseFontManager.instance().addFont(fontElement.getAttribute("name"), Activator.FOLDER_TEMPLATES + templateFolder + fontElement.getAttribute("file"));
			}

	        com.itextpdf.text.Document document = null;
	        if(printFormat == ImpressionPage.PRINT_FORMAT_A4_PORTRAIT) {
	        	document = new com.itextpdf.text.Document(PageSize.A4, MARGE, MARGE, MARGE, MARGE);
	        } else {
	        	document = new com.itextpdf.text.Document(PageSize.A4.rotate(), MARGE, MARGE, MARGE, MARGE);	        	
	        }
	        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFileName));
	        document.open();
	        PdfContentByte cb = writer.getDirectContent();

	        Point drawPosition = new Point(0,0);
			for(int nbCard = 0; nbCard < simpleModels.size(); nbCard++) {
				SimpleModel model = simpleModels.get(nbCard);
				
				drawPosition = nextCardPosition(document, cb, cardMarge, drawPosition.x, drawPosition.y);
				
				Element cardElement = null;
				
				if(model instanceof Spell) {
					Spell spellModel = (Spell) model;
					NodeList cards = racine.getElementsByTagName("card");
					for(int i = 0; i < cards.getLength(); i++) {
						if(Integer.parseInt(((Element)cards.item(i)).getAttribute("school")) == spellModel.getSchoolId()) {
							cardElement = (Element)cards.item(i);
						}
					}		
					        
			        cb.beginText();
		  
			        
					//1. Background
			        NodeList backGroundNode = cardElement.getElementsByTagName("background");
			        if(!spellModel.getBackground().equalsIgnoreCase(Activator.SPELL_NO_BACKGROUND) && backGroundNode.getLength() > 0) {
			        	PDFCardElement backgroundElement =  new PDFCardElement((Element)backGroundNode.item(0),cb,drawPosition.x, drawPosition.y);
			        	Image background = ImagePDFManager.instance().get(Activator.getSpellImageFolder() + spellModel.getBackground());
			        	background.setAbsolutePosition((int)((drawPosition.x + backgroundElement.getX())*CARD_RATION), (drawPosition.y - backgroundElement.getY() - backgroundElement.getHeight())*CARD_RATION);
			        	background.scaleAbsolute(background.getWidth()*CARD_RATION, background.getHeight()*CARD_RATION); 		// Code 2
				        document.add(background);
			        }
						        
					//2. Logo
			        NodeList logoNode = cardElement.getElementsByTagName("logo");
			        if(logoNode.getLength() > 0) {
						Image logo = ImagePDFManager.instance().get(Activator.getSpellImageFolder() + spellModel.getImage());
						if(logo == null)
							logo = ImagePDFManager.instance().get(Activator.getSpellImageFolder() + Activator.SPELL_NO_ICON);
				        PDFCardElement logoElement = new PDFCardElement((Element)logoNode.item(0),cb,drawPosition.x, drawPosition.y);
				        logo.setAbsolutePosition((int)((drawPosition.x + logoElement.getX())*CARD_RATION), (drawPosition.y - logoElement.getY() - logoElement.getHeight())*CARD_RATION); 	// Code 1
				        logo.scaleAbsolute(logo.getWidth()*CARD_RATION, logo.getHeight()*CARD_RATION); 		// Code 2
				        document.add(logo);			
			        }
			        
			        //3. Mask
			        Image img = ImagePDFManager.instance().get(Activator.FOLDER_TEMPLATES + templateFolder + cardElement.getAttribute("pattern"));
			        img.setAbsolutePosition(drawPosition.x*CARD_RATION, (drawPosition.y- getCardHeight())*CARD_RATION); 	// Code 1
			        //img.setDpi(1200, 1200);
					img.scaleAbsolute(getCardWidth()*CARD_RATION, getCardHeight()*CARD_RATION); 		// Code 2
			        document.add(img);
			        
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
			    		PDFCardElement componentElement = new PDFCardElement(componentNode,cb,drawPosition.x, drawPosition.y);
			    		Element positionElement = null;
			    		if(componentNode.getAttribute("position").equals("auto")) {
			    			positionElement = (Element)positions.item(i);
			    		} else {
			    			positionElement = htPositions.get(componentNode.getAttribute("position"));
			    		}
			   			componentElement.setX(Integer.parseInt(positionElement.getAttribute("x")));
			   			componentElement.setY(Integer.parseInt(positionElement.getAttribute("y")));
			  		    		
			    		if(componentNode.getAttribute("image") != null && !componentNode.getAttribute("image").equals("")) {
			    			Image dot = ImagePDFManager.instance().get(Activator.FOLDER_TEMPLATES + templateFolder + componentNode.getAttribute("image"));
			    			dot.setAbsolutePosition((drawPosition.x + componentElement.getX())*CARD_RATION, (drawPosition.y - componentElement.getY() - dot.getHeight())*CARD_RATION); 	// Code 1
			    			dot.scaleAbsolute(dot.getWidth()*CARD_RATION, dot.getHeight()*CARD_RATION); 		// Code 2
			    			document.add(dot);
			    		}
			            componentElement.drawString(component.getShortName());
			     	}
					
			        
		
			    	//4. Levels
			    	int filterClass = Activator.getDefault().getPreferenceStore().getInt(ImpressionPage.PLAYER_CLASS);
			    	if(filterClass != ImpressionPage.PLAYER_CLASS_NONE) {
				        PDFCardElement levelElement = new PDFCardElement((Element)cardElement.getElementsByTagName("level").item(0),cb,drawPosition.x, drawPosition.y);
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
			    	PDFCardElement titleElement = new PDFCardElement((Element)cardElement.getElementsByTagName("title").item(0),cb,drawPosition.x, drawPosition.y);
			    	titleElement.drawString(spellModel.getTitle());
			    	
			   	
			        
			    	//6. School
			    	String school = ServiceFactory.getSchoolService().getCached(spellModel.getSchoolId()).getTitle();
			    	if(!spellModel.getDescriptor().isEmpty())
			    		school += " [" + spellModel.getDescriptor() + "]";
			    	PDFCardElement schoolElement = new PDFCardElement((Element)cardElement.getElementsByTagName("school").item(0),cb,drawPosition.x, drawPosition.y);
			    	schoolElement.drawString(school);
			    	
			        
			        
			       	Element descriptionNode = (Element)cardElement.getElementsByTagName("description").item(0);
			       	PDFCardElement descriptionElement = new PDFCardElement(descriptionNode,cb,drawPosition.x, drawPosition.y);   	
			       	PDFCardElement titleDescriptionElement =  new PDFCardElement((Element)descriptionNode.getElementsByTagName("title").item(0),cb,drawPosition.x, drawPosition.y);
			       	PDFCardElement valueDescriptionElement =  new PDFCardElement((Element)descriptionNode.getElementsByTagName("value").item(0),cb,drawPosition.x, drawPosition.y);
		
			       	
			    	ArrayList<String> titleList = new ArrayList<String>();
			    	ArrayList<String> contentList = new ArrayList<String>();
			    	titleList.add(LocaleManager.instance().getMessage("castingTime") + " : "); 	contentList.add(spellModel.getCastingTime());
			    	titleList.add(LocaleManager.instance().getMessage("material") + " : "); 	contentList.add(spellModel.getMaterial());
			    	titleList.add(LocaleManager.instance().getMessage("duration") + " : "); 	contentList.add(spellModel.getDuration());
			    	titleList.add(LocaleManager.instance().getMessage("range") + " : "); 	contentList.add(spellModel.getRange());
			    	titleList.add(LocaleManager.instance().getMessage("target") + " : "); 	contentList.add(spellModel.getTarget());
			    	titleList.add(LocaleManager.instance().getMessage("effect") + " : "); contentList.add(spellModel.getEffect());
					titleList.add(LocaleManager.instance().getMessage("area") + " : "); 	contentList.add(spellModel.getArea());
			    	titleList.add(LocaleManager.instance().getMessage("savingThrow") + " : "); 	contentList.add(spellModel.getSavingThrow());
			    	titleList.add(LocaleManager.instance().getMessage("spellResistance") + " : "); 	contentList.add(spellModel.isSpellResistance()?LocaleManager.instance().getMessage("yes"):LocaleManager.instance().getMessage("no"));
		   	
			    	int posY = descriptionElement.getY()-2;
			    	if(cardWidth > MAX_WIDTH) {
			    		//Simulation de l'espace
				    	int halfWidth = descriptionElement.getWidth() / 2;
				    	for(int i = 0; i < titleList.size(); i++) {
					    	posY = printText(titleList.get(i),contentList.get(i),cb,titleDescriptionElement, valueDescriptionElement, drawPosition.x, drawPosition.y, descriptionElement.getX(), posY, halfWidth, descriptionElement.getHeight()+descriptionElement.getY(), false);
				    	}
				    	int halfHeight = ((posY - descriptionElement.getY()) / 2) + descriptionElement.getY();
				    	
				    	int indentX = 0;
				    	int maxY = 0;
				    	posY = descriptionElement.getY();
				    	for(int i = 0; i < titleList.size(); i++) {
					    	posY = printText(titleList.get(i),contentList.get(i),cb,titleDescriptionElement, valueDescriptionElement, drawPosition.x, drawPosition.y, descriptionElement.getX() + indentX, posY, halfWidth, descriptionElement.getHeight()+descriptionElement.getY());
					    	if(posY > halfHeight) {
					    		if(posY > maxY)
					    			maxY = posY;
						    	indentX = halfWidth;
						    	posY = descriptionElement.getY();
					    	}
				    	}
				    	if(posY < maxY)
				    		posY = maxY;
			    	} else {
				    	for(int i = 0; i < titleList.size(); i++) {
					    	posY = printText(titleList.get(i),contentList.get(i),cb,titleDescriptionElement, valueDescriptionElement, drawPosition.x, drawPosition.y, descriptionElement.getX(), posY, descriptionElement.getWidth(), descriptionElement.getHeight()+descriptionElement.getY());
				    	}
			    	}
			    	
			    	String remark = "";
			    	if(!spellModel.getCardText().isEmpty()) {
			    		remark = "R";
			    		posY = printText("",spellModel.getCardText(),cb,titleDescriptionElement, valueDescriptionElement, drawPosition.x, drawPosition.y, descriptionElement.getX(), posY+3, descriptionElement.getWidth(), descriptionElement.getHeight()+descriptionElement.getY());    		
			    	} else {
			    		posY = printText("",spellModel.getDetail(false),cb,titleDescriptionElement, valueDescriptionElement, drawPosition.x, drawPosition.y, descriptionElement.getX(), posY+3, descriptionElement.getWidth(), descriptionElement.getHeight()+descriptionElement.getY());
			    	}
			    	if(posY == CARD_FULL) {
			    		remark = "I";
			    		System.err.println(spellModel.getTitle());
			    	}
			    	
			    	
			    	if(!remark.isEmpty()){
			    		Element remarkNode = (Element)cardElement.getElementsByTagName("remark").item(0);
			    		PDFCardElement remarkElement = new PDFCardElement(remarkNode,cb,drawPosition.x, drawPosition.y);
			    		if(remarkNode.getAttribute("image") != null && !remarkNode.getAttribute("image").equals("")) {
				   			Image dot = ImagePDFManager.instance().get(Activator.FOLDER_TEMPLATES + templateFolder + remarkNode.getAttribute("image"));
				   			dot.setAbsolutePosition((drawPosition.x + remarkElement.getX())*CARD_RATION, (drawPosition.y - remarkElement.getY() - dot.getHeight())*CARD_RATION); 	// Code 1
				   			dot.scaleAbsolute(dot.getWidth()*CARD_RATION, dot.getHeight()*CARD_RATION); 		// Code 2
					        document.add(dot);
			    		}
			        	remarkElement.drawString(remark);
			    	}

		    		Element pageNode = (Element)cardElement.getElementsByTagName("page").item(0);
		    		if(pageNode != null && !spellModel.getPage().equals("")) {
			    		PDFCardElement pageElement = new PDFCardElement(pageNode,cb,drawPosition.x, drawPosition.y);
			    		if(pageNode.getAttribute("image") != null && !pageNode.getAttribute("image").equals("")) {
				   			Image dot = ImagePDFManager.instance().get(Activator.FOLDER_TEMPLATES + templateFolder + pageNode.getAttribute("image"));
				   			dot.setAbsolutePosition((drawPosition.x + pageElement.getX())*CARD_RATION, (drawPosition.y - pageElement.getY() - dot.getHeight())*CARD_RATION); 	// Code 1
				   			dot.scaleAbsolute(dot.getWidth()*CARD_RATION, dot.getHeight()*CARD_RATION); 		// Code 2
					        document.add(dot);
			    		}
				        pageElement.drawString(spellModel.getPage());
			    	}

			    	Element sourceNode = (Element)cardElement.getElementsByTagName("source").item(0);
		    		if(sourceNode != null && spellModel.getSourceId() > 0) {
		    			String sourceName = ServiceFactory.getSourceService().getCached(spellModel.getSourceId()).getShortName();
		    			if(sourceName.length() > 0) {
				    		PDFCardElement sourceElement = new PDFCardElement(sourceNode,cb,drawPosition.x, drawPosition.y);
				    		if(sourceNode.getAttribute("image") != null && !sourceNode.getAttribute("image").equals("")) {
					   			Image dot = ImagePDFManager.instance().get(Activator.FOLDER_TEMPLATES + templateFolder + sourceNode.getAttribute("image"));
					   			dot.setAbsolutePosition((drawPosition.x + sourceElement.getX())*CARD_RATION, (drawPosition.y - sourceElement.getY() - dot.getHeight())*CARD_RATION); 	// Code 1
					   			dot.scaleAbsolute(dot.getWidth()*CARD_RATION, dot.getHeight()*CARD_RATION); 		// Code 2
						        document.add(dot);
				    		}
				    		sourceElement.drawString(sourceName);
		    			}
			    	}

			        cb.endText();
				} else if(model instanceof MagicItem) {
					MagicItem magicItem = (MagicItem)model;
					cardElement = (Element)racine.getElementsByTagName("item").item(0);
					
			        
			        cb.beginText();
		  			        
					//1. Background
			        NodeList backgroundNode = cardElement.getElementsByTagName("background");
			        if(!magicItem.getBackground().equalsIgnoreCase(Activator.MAGIC_ITEM_NO_BACKGROUND) && backgroundNode.getLength() > 0) {
			        	PDFCardElement backgroundElement =  new PDFCardElement((Element)backgroundNode.item(0),cb,drawPosition.x, drawPosition.y);
			        	Image background = ImagePDFManager.instance().get(Activator.getMagicItemImageFolder() + magicItem.getBackground());
			        	background.setAbsolutePosition((int)((drawPosition.x + backgroundElement.getX())*CARD_RATION), (drawPosition.y - backgroundElement.getY() - backgroundElement.getHeight())*CARD_RATION);
			        	background.scaleAbsolute(background.getWidth()*CARD_RATION, background.getHeight()*CARD_RATION); 		// Code 2
				        document.add(background);
			        }

					//2. Logo
			        NodeList logoNode = cardElement.getElementsByTagName("logo");
			        if(logoNode.getLength() > 0) {
			        	Image logo = ImagePDFManager.instance().get(Activator.getMagicItemImageFolder() + magicItem.getImage());
						if(logo == null)
							logo = ImagePDFManager.instance().get(Activator.getMagicItemImageFolder() + Activator.MAGIC_ITEM_NO_ICON);
				        PDFCardElement logoElement = new PDFCardElement((Element)logoNode.item(0),cb,drawPosition.x, drawPosition.y);
				        logo.setAbsolutePosition((int)((drawPosition.x + logoElement.getX())*CARD_RATION), (drawPosition.y - logoElement.getY() - logoElement.getHeight())*CARD_RATION); 	// Code 1
				        logo.scaleAbsolute(logo.getWidth()*CARD_RATION, logo.getHeight()*CARD_RATION); 		// Code 2
				        document.add(logo);			
			        }
			        
			        //3. Mask
			        Image img = ImagePDFManager.instance().get(Activator.FOLDER_TEMPLATES + templateFolder + cardElement.getAttribute("pattern"));
			        img.setAbsolutePosition(drawPosition.x*CARD_RATION, (drawPosition.y- getCardHeight())*CARD_RATION); 	// Code 1
					img.scaleAbsolute(getCardWidth()*CARD_RATION, getCardHeight()*CARD_RATION); 		// Code 2
			        document.add(img);
			        
			        //4. Item Type 
			 	    NodeList itemTypeImageNodeList = cardElement.getElementsByTagName("slot");
			 	    if(itemTypeImageNodeList.getLength() > 0) {
				    	Element itemTypeImageNode = (Element)itemTypeImageNodeList.item(0);
				    	Image slotImage = null;
						if(magicItem.getSlotId() > 1)
							slotImage = ImagePDFManager.instance().get(Activator.FOLDER_IMAGES + ServiceFactory.getSlotService().getCached(magicItem.getSlotId()).getImage());
						else 
							slotImage = ImagePDFManager.instance().get(Activator.FOLDER_IMAGES + ServiceFactory.getItemTypeService().getCached(magicItem.getItemTypeId()).getImage());						

			    		if(itemTypeImageNode != null) {
			    			PDFCardElement itemTypeImageElement = new PDFCardElement(itemTypeImageNode, cb,drawPosition.x, drawPosition.y);
				    		if(itemTypeImageNode.getAttribute("image") != null && !itemTypeImageNode.getAttribute("image").equals("")) {
				    			Image dot = ImagePDFManager.instance().get(Activator.FOLDER_TEMPLATES + templateFolder + itemTypeImageNode.getAttribute("image"));
					   			dot.setAbsolutePosition((drawPosition.x + itemTypeImageElement.getX())*CARD_RATION, (drawPosition.y - itemTypeImageElement.getY() - dot.getHeight())*CARD_RATION); 	// Code 1
					   			dot.scaleAbsolute(dot.getWidth()*CARD_RATION, dot.getHeight()*CARD_RATION); 		// Code 2
						        document.add(dot);
				    		}
				    		
				    		slotImage.setAbsolutePosition((drawPosition.x + itemTypeImageElement.getX() + (itemTypeImageElement.getWidth() - slotImage.getWidth())/2)*CARD_RATION, (drawPosition.y - itemTypeImageElement.getY() - slotImage.getHeight() - (itemTypeImageElement.getHeight() - slotImage.getHeight())/2)*CARD_RATION); 	// Code 1
				    		slotImage.scaleAbsolute(slotImage.getWidth()*CARD_RATION, slotImage.getHeight()*CARD_RATION); 		// Code 2
					        document.add(slotImage);
				    	}	
			 	    }
			        
			    	//5. Title
			 	    NodeList specificNodeList = cardElement.getElementsByTagName("specific");
			 	    if(specificNodeList.getLength() > 0 && magicItem.getTitle().lastIndexOf(',') > -1) {
			 	    	String title = magicItem.getTitle().substring(0, magicItem.getTitle().lastIndexOf(','));
			 	    	String specific = magicItem.getTitle().substring(magicItem.getTitle().lastIndexOf(',')+1).trim();
			 	    	PDFCardElement titleElement = new PDFCardElement((Element)cardElement.getElementsByTagName("title").item(0),cb,drawPosition.x, drawPosition.y);
				    	titleElement.drawString(title);
				    	PDFCardElement titleSpecificElement = new PDFCardElement((Element)specificNodeList.item(0),cb,drawPosition.x, drawPosition.y);
				    	titleSpecificElement.drawString(specific);
			 	    	
			 	    } else {
				    	PDFCardElement titleElement = new PDFCardElement((Element)cardElement.getElementsByTagName("title").item(0),cb,drawPosition.x, drawPosition.y);
				    	titleElement.drawString(magicItem.getTitle());
			 	    }
			    	
			   	
			        
			    	//6. School
			    	String itemType = ServiceFactory.getItemTypeService().getCached(magicItem.getItemTypeId()).getTitle();
			    	PDFCardElement schoolElement = new PDFCardElement((Element)cardElement.getElementsByTagName("itemType").item(0),cb,drawPosition.x, drawPosition.y);
			    	schoolElement.drawString(itemType);
			    	
			        
			        
			       	Element descriptionNode = (Element)cardElement.getElementsByTagName("description").item(0);
			       	PDFCardElement descriptionElement = new PDFCardElement(descriptionNode,cb,drawPosition.x, drawPosition.y);   	
			       	PDFCardElement titleDescriptionElement =  new PDFCardElement((Element)descriptionNode.getElementsByTagName("title").item(0),cb,drawPosition.x, drawPosition.y);
			       	PDFCardElement valueDescriptionElement =  new PDFCardElement((Element)descriptionNode.getElementsByTagName("value").item(0),cb,drawPosition.x, drawPosition.y);
		
			       	
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
		   	
			    	int posY = descriptionElement.getY();
			    	if(cardWidth > MAX_WIDTH) {
			    		//Simulation de l'espace
				    	int halfWidth = descriptionElement.getWidth() / 2;
				    	for(int i = 0; i < titleList.size(); i++) {
					    	posY = printText(titleList.get(i),contentList.get(i),cb,titleDescriptionElement, valueDescriptionElement, drawPosition.x, drawPosition.y, descriptionElement.getX(), posY, halfWidth, descriptionElement.getHeight()+descriptionElement.getY(), false);
				    	}
				    	int halfHeight = ((posY - descriptionElement.getY()) / 2) + descriptionElement.getY();
				    	
				    	int indentX = 0;
				    	int maxY = 0;
				    	posY = descriptionElement.getY()-2;
				    	for(int i = 0; i < titleList.size(); i++) {
					    	posY = printText(titleList.get(i),contentList.get(i),cb,titleDescriptionElement, valueDescriptionElement, drawPosition.x, drawPosition.y, descriptionElement.getX() + indentX, posY, halfWidth, descriptionElement.getHeight()+descriptionElement.getY());
					    	if(posY > halfHeight) {
					    		if(posY > maxY)
					    			maxY = posY;
						    	indentX = halfWidth;
						    	posY = descriptionElement.getY();
					    	}
				    	}
				    	if(posY < maxY)
				    		posY = maxY;
			    	} else {
				    	for(int i = 0; i < titleList.size(); i++) {
					    	posY = printText(titleList.get(i),contentList.get(i),cb,titleDescriptionElement, valueDescriptionElement, drawPosition.x, drawPosition.y, descriptionElement.getX(), posY, descriptionElement.getWidth(), descriptionElement.getHeight()+descriptionElement.getY());
				    	}
			    	}
			    	
			    	String remark = "";
			    	if(!magicItem.getCardText().isEmpty()) {
			    		remark = "R";
			    		posY = printText("",magicItem.getCardText(),cb,titleDescriptionElement, valueDescriptionElement, drawPosition.x, drawPosition.y, descriptionElement.getX(), posY+3, descriptionElement.getWidth(), descriptionElement.getHeight()+descriptionElement.getY());    		
			    	} else {
			    		posY = printText("",magicItem.getDetail(false),cb,titleDescriptionElement, valueDescriptionElement, drawPosition.x, drawPosition.y, descriptionElement.getX(), posY+3, descriptionElement.getWidth(), descriptionElement.getHeight()+descriptionElement.getY());
			    	}
			    	if(posY == CARD_FULL) {
			    		remark = "I";
			    		System.err.println(magicItem.getTitle());
			    	}
			    	
			    	
			    	if(!remark.isEmpty()){
			    		Element remarkNode = (Element)cardElement.getElementsByTagName("remark").item(0);
			    		PDFCardElement remarkElement = new PDFCardElement(remarkNode,cb,drawPosition.x, drawPosition.y);
			    		if(remarkNode.getAttribute("image") != null && !remarkNode.getAttribute("image").equals("")) {
				   			Image dot = ImagePDFManager.instance().get(Activator.FOLDER_TEMPLATES + templateFolder + remarkNode.getAttribute("image"));
				   			dot.setAbsolutePosition((drawPosition.x + remarkElement.getX())*CARD_RATION, (drawPosition.y - remarkElement.getY() - dot.getHeight())*CARD_RATION); 	// Code 1
				   			dot.scaleAbsolute(dot.getWidth()*CARD_RATION, dot.getHeight()*CARD_RATION); 		// Code 2
					        document.add(dot);
			    		}
			        	remarkElement.drawString(remark);
			    	}

		    		Element pageNode = (Element)cardElement.getElementsByTagName("page").item(0);
		    		if(pageNode != null && !magicItem.getPage().equals("")) {
			    		PDFCardElement pageElement = new PDFCardElement(pageNode,cb,drawPosition.x, drawPosition.y);
			    		if(pageNode.getAttribute("image") != null && !pageNode.getAttribute("image").equals("")) {
				   			Image dot = ImagePDFManager.instance().get(Activator.FOLDER_TEMPLATES + templateFolder + pageNode.getAttribute("image"));
				   			dot.setAbsolutePosition((drawPosition.x + pageElement.getX())*CARD_RATION, (drawPosition.y - pageElement.getY() - dot.getHeight())*CARD_RATION); 	// Code 1
				   			dot.scaleAbsolute(dot.getWidth()*CARD_RATION, dot.getHeight()*CARD_RATION); 		// Code 2
					        document.add(dot);
			    		}
				        pageElement.drawString(magicItem.getPage());
			    	}
			 		
			    	Element sourceNode = (Element)cardElement.getElementsByTagName("source").item(0);
		    		if(sourceNode != null && magicItem.getSourceId() > 0) {
		    			String sourceName = ServiceFactory.getSourceService().getCached(magicItem.getSourceId()).getShortName();
		    			if(sourceName.length() > 0) {
				    		PDFCardElement sourceElement = new PDFCardElement(sourceNode,cb,drawPosition.x, drawPosition.y);
				    		if(sourceNode.getAttribute("image") != null && !sourceNode.getAttribute("image").equals("")) {
					   			Image dot = ImagePDFManager.instance().get(Activator.FOLDER_TEMPLATES + templateFolder + sourceNode.getAttribute("image"));
					   			dot.setAbsolutePosition((drawPosition.x + sourceElement.getX())*CARD_RATION, (drawPosition.y - sourceElement.getY() - dot.getHeight())*CARD_RATION); 	// Code 1
					   			dot.scaleAbsolute(dot.getWidth()*CARD_RATION, dot.getHeight()*CARD_RATION); 		// Code 2
						        document.add(dot);
				    		}
				    		sourceElement.drawString(sourceName);
		    			}
			    	}

			        cb.endText();

				}
				
		        
				if(frontBack) {
					drawPosition = nextCardPosition(document, cb, cardMarge, drawPosition.x, drawPosition.y);
			        Image imgBack = ImagePDFManager.instance().get(Activator.FOLDER_TEMPLATES + templateFolder + cardElement.getAttribute("back"));
			        imgBack.setAbsolutePosition(drawPosition.x*CARD_RATION, (drawPosition.y- getCardHeight())*CARD_RATION); 	
			        imgBack.scaleAbsolute(getCardWidth()*CARD_RATION, getCardHeight()*CARD_RATION); 		
			        document.add(imgBack);
				}
			}
	        document.close();
	
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
 	}
	
	private int printText(String title, String text, PdfContentByte cb , PDFCardElement titleElem, PDFCardElement textElem, int xCard, int yCard, int x, int y, int width, int height) {
		return printText(title, text, cb , titleElem, textElem, xCard, yCard, x, y, width, height, true);
	}	
	
	private int printText(String title, String text, PdfContentByte cb , PDFCardElement titleElem, PDFCardElement textElem, int xCard, int yCard, int x, int y, int width, int height, boolean print) {
      	if(text == null || text.trim().isEmpty()) return y;
      	if(y >= height || y == CARD_FULL) return CARD_FULL;
      	int titleWidth = 0;
     	if(title != null && !title.trim().isEmpty()) {
     		    		
			titleWidth = (int)titleElem.getFont().getWidthPoint(title, titleElem.getFontSize());
			int titleHeight = titleElem.getFontHeight();

     		cb.setFontAndSize(titleElem.getFont(), titleElem.getFontSize());
     		cb.setColorFill(titleElem.getColor());
     		cb.setColorStroke(titleElem.getColor());
    		if(titleHeight + y > height) return CARD_FULL;
    		if(print)
    			showText(title, titleHeight, cb, xCard + x, yCard - y, titleElem.isBold(), titleElem.isItalic());
     	}
 

 		cb.setFontAndSize(textElem.getFont(), textElem.getFontSize());
 		cb.setColorFill(textElem.getColor());
		cb.setColorStroke(textElem.getColor());

 		String[] lines = text.split("\n");
        int textHeight = textElem.getFontHeight();
     	for(String line : lines) {
 			String[] words = line.split(" ");
	      	String lineToPrint = "";
	      	for(String word : words) {
	    		int textWidth = (int)textElem.getFont().getWidthPoint(lineToPrint + " " + word, textElem.getFontSize());
	      		if(textWidth + titleWidth > width) {
	        		if(textHeight + y > height) return CARD_FULL;
	        		if(print)
	        			showText(lineToPrint.trim(), textHeight, cb, xCard + x + titleWidth, yCard - y, textElem.isBold(), textElem.isItalic());
		    		textWidth = (int)textElem.getFont().getWidthPoint(lineToPrint.trim(), textElem.getFontSize());
		            titleWidth = 0;
	    			y += textHeight + 2;
	    			lineToPrint = word;
	      		} else {
	      			lineToPrint += " " + word;
	      		}
	      	}
	      	if(!lineToPrint.isEmpty()) {
	      		if(textHeight + y > height) return CARD_FULL;
	    		if(print)
	    			showText(lineToPrint.trim(), textHeight,  cb, xCard + x + titleWidth, yCard - y, textElem.isBold(), textElem.isItalic());
        		if(line.equals("") || line.equals("\r"))
        			y += textHeight;
        		else
        			y += textHeight + 2;
 	      	}
     	}
        return y+2;	
	}
	
	private void showText(String text, int height, PdfContentByte cb, int x, int y, boolean bold, boolean italic) {
        // simulate bold
        if(bold) {
        	cb.setLineWidth(0.25f);
        	cb.setTextRenderingMode(PdfContentByte.TEXT_RENDER_MODE_FILL_STROKE);
        	//cb.stroke();
        }
        if(!italic)
        	cb.setTextMatrix(1, 0, 0, 1, x, y - height);
        else
            cb.setTextMatrix(1, 0, 0.25f, 1, x, y - height);
       	
		cb.showText(text);
		if(bold) {
			//cb.closePathStroke();
        	cb.setTextRenderingMode(PdfContentByte.TEXT_RENDER_MODE_FILL);
	       	cb.setLineWidth(0f);
		}

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
	
	private Point nextCardPosition(com.itextpdf.text.Document document, PdfContentByte cb, int cardMarge, int currentXPosition, int currentYPosition) {
		int pageHeight = (int)(document.getPageSize().getHeight() * 1/CARD_RATION);
		int pageWidth = (int)(document.getPageSize().getWidth() * 1/CARD_RATION);
		
		if(currentXPosition == 0 && currentYPosition == 0) {
	        if(CARD_RATION != 1)
	        	cb.transform(AffineTransform.getScaleInstance(CARD_RATION, CARD_RATION));			
			return new Point(MARGE, pageHeight - MARGE);
		}
			
		
		int xPosition = currentXPosition + getCardWidth() + cardMarge*2;
		int yPosition = currentYPosition;
		if(xPosition + getCardWidth()  > pageWidth - MARGE) {
			xPosition = MARGE;
			yPosition = currentYPosition - cardMarge*2 - getCardHeight();
		}
		if(yPosition - getCardHeight() < MARGE) {
			yPosition = pageHeight - MARGE;
			document.newPage();
	        if(CARD_RATION != 1)
	        	cb.transform(AffineTransform.getScaleInstance(CARD_RATION, CARD_RATION));
		}
		return new Point(xPosition, yPosition);
	}

}
