package spell.tools;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.w3c.dom.Element;


public class GCCardElement {
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
	private Color foreground;
	private int style;
	private int fontSize;
	private String fontName;
	private GC gc;
	
	public GCCardElement(GC gc, Element element) {
		this.gc = gc;
		this.fontSize = (int)Math.round(12*.75);
		
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
		style = SWT.NORMAL;
		if(element.hasAttribute("font")) {
			fontName = element.getAttribute("font");
			if(element.getAttribute("bold").equals("true")) {
				if(element.getAttribute("italic").equals("true"))
					style = SWT.BOLD | SWT.ITALIC;
				else
					style = SWT.BOLD;
			} else if(element.getAttribute("italic").equals("true")) {
				style = SWT.ITALIC;				
			}					
			fontSize = (int)Math.round(Integer.parseInt(element.getAttribute("size"))*.75);
		} else {
			fontName = gc.getDevice().getSystemFont().getFontData()[0].getName();
		}
		if(element.hasAttribute("foreground")) {
			java.awt.Color colorTmp = java.awt.Color.decode(element.getAttribute("foreground"));
			foreground = new Color(gc.getDevice(),colorTmp.getRed(), colorTmp.getGreen(), colorTmp.getBlue());
		} else {
			foreground = gc.getDevice().getSystemColor(SWT.COLOR_BLACK);
		}
	}
	
	public void drawString(String text) {
		Font currentFont = createFont();
		gc.setFont(currentFont);
    	gc.setForeground(foreground);
    	
		int textWidth = gc.textExtent(text).x;
		
    	while(textWidth > this.getWidth() && fontSize > 4) {
    		fontSize--;
    		currentFont.dispose();
    		currentFont = createFont();
        	gc.setFont(currentFont);
        	textWidth = gc.textExtent(text).x;
    	}
		
       	int textHeigth = gc.textExtent(text).y;

       	int xPos = this.getX();
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
		int yPos = this.getY();
		switch (this.getValign()) {
			case TOP:
				yPos = this.getY();
				break;
			case CENTER:
				yPos = this.getY() + (this.getHeight() - textHeigth)/2;
			break;
			case BOTTOM:
				yPos = this.getY() + this.getHeight() - textHeigth;
			break;
		}	
	   	gc.drawString(text, xPos, yPos,true);
	   	currentFont.dispose();
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

	public Font createFont() {		
		return new Font(gc.getDevice(),fontName, fontSize,style);
	}
	
	public int getFontHeight() {
		return gc.textExtent("EptiPGlg").y;
	}
	
	public Color getForeground() {
		return foreground;
	}

}
