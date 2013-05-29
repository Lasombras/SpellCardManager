package spell.tools;

import java.io.FileOutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Vector;

import spell.Activator;
import spell.model.Level;
import spell.model.MagicItem;
import spell.model.Spell;
import spell.model.simple.SimpleModel;
import spell.preferences.ImpressionPage;
import spell.services.ServiceFactory;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.html.simpleparser.StyleSheet;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPRow;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class PDFListBuilder {
	
	public final static float CARD_RATION = 0.5f;
	public final static int MARGE = 50;
		
	private Vector<SimpleModel> simpleModels;	
	private int printFormat;

	
	public PDFListBuilder() {
		try {
			this.printFormat = Activator.getDefault().getPreferenceStore().getInt(ImpressionPage.PRINT_FORMAT);
			simpleModels = new Vector<SimpleModel>();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void add(SimpleModel model) {
		for(int i = 0; i < model.getSize(); i++) {
			simpleModels.add(model);
		}
	}
		
	public void draw(String pdfFileName) {
        Document document = null;
		try {
			
			int nbCol = 1;
	        if(printFormat == ImpressionPage.PRINT_FORMAT_A4_PORTRAIT) {
	        	document = new com.itextpdf.text.Document(PageSize.A4, MARGE, MARGE, MARGE, MARGE);
	        	nbCol = 2;
	        } else {
	        	document = new com.itextpdf.text.Document(PageSize.A4.rotate(), MARGE, MARGE, MARGE, MARGE);	        	
	        	nbCol = 3;
	        }
			float widthCol = document.getPageSize().getWidth()/nbCol - ((3.0f/2.0f)/nbCol)*MARGE;
			
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFileName));
	        document.open();
	        PdfContentByte cb = writer.getDirectContent();			
	        BaseColor titleBackgroundColor = new BaseColor(63,10,0);
	        	        
	        Font myFont = new Font(FontFamily.HELVETICA, 8, Font.NORMAL);
	        Font myFontTitle = new Font(FontFamily.HELVETICA, 12, Font.BOLD, new BaseColor(236,229,178));
	        Font myFontBold =  new Font(FontFamily.HELVETICA, 8, Font.BOLD);
	        Font myFontBoldItalic =  new Font(FontFamily.HELVETICA, 8, Font.BOLDITALIC);
	        float normalLeading = myFontBold.getCalculatedLeading(1)+2;
			StyleSheet styles = new StyleSheet();
			styles.loadTagStyle("body", "size", "8px");
			styles.loadTagStyle("body", "face", "helvetica");   
			styles.loadTagStyle("body", "leading", normalLeading + "");
			styles.loadTagStyle("caption", "size", "6px");
			styles.loadTagStyle("span", "leading", normalLeading + "");
			styles.loadTagStyle("p", "leading", normalLeading + "");
			styles.loadTagStyle("i", "leading", normalLeading + "");
			styles.loadTagStyle("b", "leading", normalLeading + "");
			styles.loadTagStyle("div", "leading", normalLeading + "");
			styles.loadTagStyle("center", "leading", normalLeading + "");
			styles.loadTagStyle("table", "border", "1");
			styles.loadTagStyle("table", "leading", normalLeading + "");
			styles.loadTagStyle("td", "valign", "middle");
			styles.loadTagStyle("td", "leading", normalLeading + "");
//			styles.loadTagStyle("td", "padding", "1px 5px 1px 5px");
			styles.loadTagStyle("td", "align", "center");
			styles.loadTagStyle("th", "valign", "middle");
			styles.loadTagStyle("th", "leading", normalLeading + "");
//			styles.loadTagStyle("th", "padding", "1px 5px 1px 5px");
			styles.loadTagStyle("th", "align", "center");
			styles.loadTagStyle("th", "bgcolor", "#3f0a00");
			styles.loadTagStyle("tr", "leading", normalLeading + "");

		
			styles.loadStyle("titre", "color", "#ece5b2");
			styles.loadStyle("titre", "bgcolor", "#3f0a00");
			styles.loadStyle("titre", "border-color", "black");			
			styles.loadStyle("soustitre", "color", "#ece5b2");
			styles.loadStyle("soustitre", "bgcolor", "#3f0a00");
			styles.loadStyle("soustitre", "border-color", "black");			
			
			styles.loadStyle("trDefault", "bgcolor", "#ece5b2");		
			styles.loadStyle("premier", "bgcolor", "#ece5b2");		
			styles.loadStyle("alt", "bgcolor", "#f7f5df");
			

            ColumnText ct = new ColumnText(cb);
			cb.setLineWidth(1);
			for(int nbCard = 0; nbCard < simpleModels.size(); nbCard++) {
				SimpleModel model = simpleModels.get(nbCard);
				if(model instanceof Spell) {
					Spell spellModel = (Spell) model;
			    	Phrase phrase = new Phrase(spellModel.getTitle().toUpperCase(),myFontTitle);
			    	PdfPTable tableTitle = new PdfPTable(1);		
			    	tableTitle.setWidthPercentage(100);
			    	tableTitle.setSpacingBefore(10);
					PdfPCell cellTitle = new PdfPCell(phrase);
					cellTitle.setPaddingBottom(3);
					cellTitle.setPaddingLeft(8);
					cellTitle.setPaddingTop(0);
					cellTitle.setBackgroundColor(titleBackgroundColor);
					tableTitle.addCell(cellTitle);
			    	ct.addElement(tableTitle);
			    	
			    	String school = ServiceFactory.getSchoolService().getCached(spellModel.getSchoolId()).getTitle();
			    	if(!spellModel.getDescriptor().isEmpty())
			    		school += " [" + spellModel.getDescriptor() + "]";
			    	
			    	phrase = new Phrase();
			    	phrase.add(new Phrase(LocaleManager.instance().getMessage("school")+ " ",myFontBold));
			    	phrase.add(new Phrase(school+ "; ",myFont ));
			    	phrase.setLeading(normalLeading);
			    	ct.addElement(phrase);

					String levelStr = "";
			        for(Level level : spellModel.getLevels()) {
			        	if(!levelStr.isEmpty()) levelStr += ", ";
			        	levelStr +=  ServiceFactory.getPlayerClassService().getCached(level.getPlayerClassId()).getTitle() + " " + level.getLevel();
			        }		        
			    	phrase = new Phrase();			        
			    	phrase.add(new Phrase(LocaleManager.instance().getMessage("level")+ " ",myFontBold));
			    	phrase.add(new Phrase(levelStr,myFont));
			    	phrase.setLeading(normalLeading);
			    	ct.addElement(phrase);

					if(spellModel.getCastingTime() != null && !spellModel.getCastingTime().equals("")) {
				    	phrase = new Phrase();			        
				    	phrase.add(new Phrase(LocaleManager.instance().getMessage("castingTime") + " ",myFontBold));
				    	phrase.add(new Phrase(spellModel.getCastingTime(),myFont));
				    	phrase.setLeading(normalLeading);
				    	ct.addElement(phrase);
					}	
					
			    	String componentsStr = "";
			        for(int component : spellModel.getComponentsId()) {
			        	if(!componentsStr.isEmpty()) componentsStr += ", ";
			        	componentsStr +=  ServiceFactory.getComponentService().getCached(component).getShortName();
			        }		
					if(!componentsStr.equals("")) {
				    	phrase = new Phrase();			        
				    	phrase.add(new Phrase(LocaleManager.instance().getMessage("components") + " ",myFontBold));
				    	phrase.add(new Phrase(componentsStr,myFont));
				    	phrase.setLeading(normalLeading);
				    	ct.addElement(phrase);
					}	
					if(spellModel.getMaterial() != null && !spellModel.getMaterial().equals("")) {
				    	phrase = new Phrase();			        
				    	phrase.add(new Phrase(LocaleManager.instance().getMessage("material") + " ",myFontBold));
				    	phrase.add(new Phrase(spellModel.getMaterial(),myFont));
				    	phrase.setLeading(normalLeading);
				    	ct.addElement(phrase);
					}	

					if(spellModel.getRange() != null && !spellModel.getRange().equals("")) {
				    	phrase = new Phrase();			        
				    	phrase.add(new Phrase(LocaleManager.instance().getMessage("range") + " ",myFontBold));
				    	phrase.add(new Phrase(spellModel.getRange(),myFont));
				    	phrase.setLeading(normalLeading);
				    	ct.addElement(phrase);
					}	
					if(spellModel.getArea() != null && !spellModel.getArea().equals("")) {
				    	phrase = new Phrase();			        
				    	phrase.add(new Phrase(LocaleManager.instance().getMessage("area") + " ",myFontBold));
				    	phrase.add(new Phrase(spellModel.getArea(),myFont));
				    	phrase.setLeading(normalLeading);
				    	ct.addElement(phrase);
					}	
					if(spellModel.getTarget() != null && !spellModel.getTarget().equals("")) {
				    	phrase = new Phrase();			        
				    	phrase.add(new Phrase(LocaleManager.instance().getMessage("target") + " ",myFontBold));
				    	phrase.add(new Phrase(spellModel.getTarget(),myFont));
				    	phrase.setLeading(normalLeading);
				    	ct.addElement(phrase);
					}	
					if(spellModel.getDuration() != null && !spellModel.getDuration().equals("")) {
				    	phrase = new Phrase();			        
				    	phrase.add(new Phrase(LocaleManager.instance().getMessage("duration") + " ",myFontBold));
				    	phrase.add(new Phrase(spellModel.getDuration(),myFont));
				    	phrase.setLeading(normalLeading);
				    	ct.addElement(phrase);
					}	
					if(spellModel.getSavingThrow() != null && !spellModel.getSavingThrow().equals("")) {
				    	phrase = new Phrase();			        
				    	phrase.add(new Phrase(LocaleManager.instance().getMessage("savingThrow") + " ",myFontBold));
				    	phrase.add(new Phrase(spellModel.getSavingThrow() +"; ",myFont));
				    	phrase.add(new Phrase(LocaleManager.instance().getMessage("spellResistance") + " ",myFontBoldItalic));
				    	phrase.add(new Phrase(spellModel.isSpellResistance()?LocaleManager.instance().getMessage("yes"):LocaleManager.instance().getMessage("no"),myFont));
				    	phrase.setLeading(normalLeading);
				    	ct.addElement(phrase);
					}	
					ArrayList<Element> elements = HTMLWorker.parseToList(new StringReader(encodeText(spellModel.getDetail(true))), styles);
					for(Element element : elements) {
						if(element instanceof Paragraph) {
							((Paragraph)element).setLeading(normalLeading);
						} else if(element instanceof PdfPTable) {
							PdfPTable table = (PdfPTable)element;
							for(PdfPRow row : table.getRows()) {
								for(PdfPCell cell : row.getCells()) {
									if(cell != null)
										cell.setPadding(0);
								}
								row.calculateHeights();
							}
						}
						ct.addElement(element);
					}
				    //ct.addElement(new Phrase(spellModel.getDetail(false),myFont));                
				   ct.addElement(new Phrase("",myFont));                
				} else if(model instanceof MagicItem) {
					MagicItem magicItemModel = (MagicItem) model;	
			    	Phrase phrase = new Phrase(magicItemModel.getTitle().toUpperCase(),myFontTitle);
			    	PdfPTable tableTitle = new PdfPTable(1);		
			    	tableTitle.setWidthPercentage(100);
			    	tableTitle.setSpacingBefore(10);
					PdfPCell cellTitle = new PdfPCell(phrase);
					cellTitle.setPaddingBottom(3);
					cellTitle.setPaddingLeft(8);
					cellTitle.setPaddingTop(0);
					cellTitle.setBackgroundColor(titleBackgroundColor);
					tableTitle.addCell(cellTitle);
			    	ct.addElement(tableTitle);

			    	phrase = new Phrase();
			    	phrase.add(new Phrase(LocaleManager.instance().getMessage("itemType") + " ",myFontBold));
			    	phrase.add(new Phrase(ServiceFactory.getItemTypeService().getCached(magicItemModel.getItemTypeId()).getTitle(),myFont));
			    	phrase.setLeading(normalLeading);
			    	ct.addElement(phrase);
	
			    	phrase = new Phrase();
					String itemStr = magicItemModel.getAura();
					if(itemStr == null || itemStr.equals(""))
						itemStr = "---";						
					phrase.add(new Phrase(LocaleManager.instance().getMessage("aura") + " ",myFontBold));
					phrase.add(new Phrase(itemStr + "; ",myFont));
		            itemStr = magicItemModel.getCasterLevel();
					if(itemStr == null || itemStr.equals(""))
						itemStr = "---";						
					phrase.add(new Phrase(LocaleManager.instance().getMessage("casterLevelShort") + " ",myFontBold));
					phrase.add(new Phrase(itemStr,myFont));
			    	phrase.setLeading(normalLeading);
			    	ct.addElement(phrase);
		            
			    	phrase = new Phrase();
			    	phrase.add(new Phrase(LocaleManager.instance().getMessage("slot") + " ",myFontBold));
			    	phrase.add(new Phrase(ServiceFactory.getSlotService().getCached(magicItemModel.getSlotId()).getTitle() + "; ",myFont));
					if(magicItemModel.getPrice() > 0) {
						phrase.add(new Phrase(LocaleManager.instance().getMessage("price") + " ",myFontBold));
						phrase.add(new Phrase(Activator.formatPrice(magicItemModel.getPrice()) + "; ",myFont));						
					}
					itemStr = magicItemModel.getWeight();
					if(itemStr == null || itemStr.equals(""))
						itemStr = "---";						
					phrase.add(new Phrase(LocaleManager.instance().getMessage("weight") + " ",myFontBold));
					phrase.add(new Phrase(itemStr,myFont));
			    	phrase.setLeading(normalLeading);
			    	ct.addElement(phrase);

		    		ArrayList<Element> elements = HTMLWorker.parseToList(new StringReader(encodeText(magicItemModel.getDetail(true))), styles);
		    		for(Element element : elements) {
		    			if(element instanceof Paragraph) {
		    				((Paragraph)element).setLeading(normalLeading);
		    			} else if(element instanceof PdfPTable) {
		    				PdfPTable table = (PdfPTable)element;
		    				for(PdfPRow row : table.getRows()) {
		    					for(PdfPCell cell : row.getCells()) {
		    						cell.setPadding(0);
		    					}
		    					row.calculateHeights();
		    				}
		    			}
		    			ct.addElement(element);
		    		}

				   // ct.addElement(new Phrase(magicItemModel.getDetail(false) + "\n\n",myFont));
				}
			}
			
			int column = 0;
			int status = ColumnText.START_COLUMN;
			while (ColumnText.hasMoreText(status)) {
				ct.setSimpleColumn(	MARGE + widthCol * column,
									MARGE ,
									widthCol * (column+1) + MARGE/2,
									document.getPageSize().getHeight() - MARGE, 10,
									Element.ALIGN_JUSTIFIED);
				status = ct.go();
				column++;
				if (column > nbCol-1) {
					column = 0;
					document.newPage();
				}
			} 
            
	
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(document != null) document.close();
		}
    	
 	}
	
	
	private String encodeText(String text) {
		
		text = tagNewLineDrop(text, "table");
		text = tagNewLineDrop(text, "tr");
		text = tagNewLineDrop(text, "td");
		text = tagNewLineDrop(text, "center");
		text = tagNewLineDrop(text, "ul");
		text = tagNewLineDrop(text, "li");

		text = text.replaceAll("\">\\r\\n", "\">");
		text = text.replaceAll("\">\\n", "\">");

		text = text.replaceAll("\\r\\n", "<br/>");
		text = text.replaceAll("\\n", "<br/>");
		
		text = text.replaceAll("<tr>", "<tr class=\"trDefault\">");
		
		text = removeParameterTag(text, "a");
		text = text.replaceAll("</a>", "");

		
		return "<html><head></head><body>" + text + "</body></html>";
	}
	
	private String removeParameterTag(String str, String tag) {
		tag = "<" + tag + " ";
		while(str.indexOf(tag) > -1) {
			String strStart = str.substring(0,str.indexOf(tag));
			String strEnd = str.substring(str.indexOf(tag));
			strEnd = strEnd.substring(strEnd.indexOf(">")+1);
			str = strStart + strEnd;
		}
		return str;		
	}

	
	private String tagNewLineDrop(String text, String tag) {
		text = text.replaceAll("<" + tag + ">\\r\\n", "<" + tag + ">");
		text = text.replaceAll("<" + tag + ">\\n", "<" + tag + ">");
		text = text.replaceAll("</" + tag + ">\\r\\n", "</" + tag + ">");
		text = text.replaceAll("</" + tag + ">\\n", "</" + tag + ">");
		return text;
	}

}
