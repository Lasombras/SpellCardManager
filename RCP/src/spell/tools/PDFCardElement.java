package spell.tools;

import java.io.IOException;

import org.w3c.dom.Element;

import spell.tools.CacheManager.BaseFontManager;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;

public class PDFCardElement {
		private final static int LEFT = 0;
		private final static int RIGHT = 1;
		private final static int CENTER = 2;
		private final static int TOP = 0;
		private final static int BOTTOM = 1;

		private int x = 0;
		private int y = 0;
		private int width = 0;
		private int height = 0;
		private int align = LEFT;
		private int valign = TOP;
		private BaseColor color;
		private int fontSize;
		private PdfContentByte cb;
		private int xCard = 0;
		private int yCard = 0;
		private BaseFont baseFont = null;
		private boolean bold = false;
		private boolean italic = false;
		
		
		public PDFCardElement(Element element, PdfContentByte cb, int xCard, int yCard) {
			this.fontSize = 12;
			this.cb = cb;
			this.xCard = xCard;
			this.yCard = yCard;
			
			if(element.hasAttribute("x")) x = Integer.parseInt(element.getAttribute("x"));
			if(element.hasAttribute("y")) y = Integer.parseInt(element.getAttribute("y"));
			if(element.hasAttribute("width")) width = Integer.parseInt(element.getAttribute("width"));
			if(element.hasAttribute("height")) height = Integer.parseInt(element.getAttribute("height"));
			if(width > 0 && element.hasAttribute("align")) {
				if(element.getAttribute("align").equals("center")) align = CENTER;
				if(element.getAttribute("align").equals("right")) align = RIGHT;
			}
			if(height > 0 && element.hasAttribute("valign")) {
				if(element.getAttribute("valign").equals("center")) valign = CENTER;
				if(element.getAttribute("valign").equals("middle")) valign = CENTER;
				if(element.getAttribute("valign").equals("bottom")) valign = BOTTOM;
			}
			

			try {
				if(element.hasAttribute("font")) {
					String font = element.getAttribute("font");
					bold = element.getAttribute("bold").equals("true");
					italic = element.getAttribute("italic").equals("true");				
					fontSize = Integer.parseInt(element.getAttribute("size"));					
					if(font.equalsIgnoreCase(BaseFont.COURIER)) {
						if(bold && italic) {
							font = BaseFont.COURIER_BOLDOBLIQUE;
							bold = false;
							italic = false;
						} else if (bold) {
							font = BaseFont.COURIER_BOLD;
							bold = false;							
						} else if (italic) {
							font = BaseFont.COURIER_OBLIQUE;
							italic = false;							
						}
					}
					if(font.equalsIgnoreCase(BaseFont.HELVETICA)) {
						if(bold && italic) {
							font = BaseFont.HELVETICA_BOLDOBLIQUE;
							bold = false;
							italic = false;
						} else if (bold) {
							font = BaseFont.HELVETICA_BOLD;
							bold = false;							
						} else if (italic) {
							font = BaseFont.HELVETICA_OBLIQUE;
							italic = false;							
						}
					}
					if(font.equalsIgnoreCase(BaseFont.TIMES_ROMAN)) {
						if(bold && italic) {
							font = BaseFont.TIMES_BOLDITALIC;
							bold = false;
							italic = false;
						} else if (bold) {
							font = BaseFont.TIMES_BOLD;
							bold = false;							
						} else if (italic) {
							font = BaseFont.TIMES_ITALIC;
							italic = false;							
						}
					}
					baseFont = BaseFontManager.instance().getFont(font);
				} else {
					baseFont = BaseFontManager.instance().getFont(BaseFont.COURIER);
				}
			} catch (Exception e) {
				baseFont = BaseFontManager.instance().getFont(BaseFont.COURIER);
			}
			if(element.hasAttribute("foreground")) {
				java.awt.Color colorTmp = java.awt.Color.decode(element.getAttribute("foreground"));
				color = new BaseColor(colorTmp.getRed(), colorTmp.getGreen(), colorTmp.getBlue());
			} else {
				color = BaseColor.BLACK;
			}
		}
		
		
		public void drawString(String text) throws DocumentException, IOException {

	    	cb.setFontAndSize(baseFont, fontSize);
	    	cb.setColorFill(color);
        	cb.setColorStroke(color);


			float textWidth = baseFont.getWidthPoint(text, fontSize);
	    	while(textWidth > this.getWidth() && fontSize > 4) {
	    		fontSize--;
	    		cb.setFontAndSize(baseFont, fontSize);
	    		textWidth = baseFont.getWidthPoint(text, fontSize);
	    	}    	
			float ascend = baseFont.getAscentPoint(text, fontSize);
			float descend = baseFont.getDescentPoint(text, fontSize);
			float textHeight = ascend - descend;
            
			float xPos = this.getX();
			switch (this.getAlign()) {
				case LEFT:
					xPos = this.getX();
					break;
				case CENTER:
					xPos = this.getX() + (this.getWidth() - textWidth)/2;
					break;
				case RIGHT:
					xPos = this.getX() + this.getWidth() - textWidth;
					break;
			}
			float yPos = this.getY();
			switch (this.getValign()) {
				case TOP:
					yPos = this.getY() + ascend;
					break;
				case CENTER:
					//yPos = this.getY() + (this.getHeight() - textHeight)/2 + ascend;
					yPos = this.getY() + (this.getHeight() - textHeight)/2 + ascend - (descend/2);
					break;
				case BOTTOM:
					yPos = this.getY() + this.getHeight() + descend;
					break;
			}	
            
            // simulate bold
            if(bold) {
            	cb.setLineWidth(0.25f);
            	cb.setTextRenderingMode(PdfContentByte.TEXT_RENDER_MODE_FILL_STROKE);
            	//cb.stroke();
            }
            if(!italic)
            	cb.setTextMatrix(1, 0, 0, 1, xCard + xPos, yCard - yPos);
            else
                cb.setTextMatrix(1, 0, 0.25f, 1, xCard + xPos, yCard - yPos);
           	
			cb.showText(text);
			if(bold) {
				//cb.closePathStroke();
	        	cb.setTextRenderingMode(PdfContentByte.TEXT_RENDER_MODE_FILL);
            	cb.setLineWidth(0f);
			}
		}

		public int getX() {
			return x;
		}

		public void setX(int x) {
			this.x = x;
		}

		public int getY() {
			return y;
		}

		public void setY(int y) {
			this.y = y;
		}

		public int getWidth() {
			return width;
		}

		public void setWidth(int width) {
			this.width = width;
		}

		public int getHeight() {
			return height;
		}

		public void setHeight(int height) {
			this.height = height;
		}

		public int getAlign() {
			return align;
		}

		public void setAlign(int align) {
			this.align = align;
		}

		public int getValign() {
			return valign;
		}

		public void setValign(int valign) {
			this.valign = valign;
		}

		public int getFontSize() {
			return fontSize;
		}

		public void setFontSize(int fontSize) {
			this.fontSize = fontSize;
		}
		
		public BaseFont getFont() {
			return baseFont;
		}


		public BaseColor getColor() {
			return color;
		}


		public boolean isBold() {
			return bold;
		}


		public boolean isItalic() {
			return italic;
		}

		
		public int getFontHeight() {
			float titleAscend = this.getFont().getAscentPoint("EptiPGlg", this.getFontSize());
			float titleDescend = this.getFont().getDescentPoint("EptiPGlg", this.getFontSize());
            return (int)(titleAscend - titleDescend);
		}
		
		/*
		public int getFontHeight() {
			return fontSize;
		}
		*/
}
