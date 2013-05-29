package spell.tools;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class ConvertImageManager
{

	private Toolkit toolkit;	
	public final static int RESIZE = 1;
	public final static int SCALE = 0;

	public ConvertImageManager() {
		toolkit = java.awt.Toolkit.getDefaultToolkit();
	}
		
	private BufferedImage toBufferedImage(Image image) {
			/** On test si l'image n'est pas déja une instance de BufferedImage */
			if( image instanceof BufferedImage ) {
				/** cool, rien à faire */
				return (BufferedImage)image;
			} else {
				/** On s'assure que l'image est complètement chargée */
				image = new ImageIcon(image).getImage();
                
				/** On crée la nouvelle image */
				BufferedImage bufferedImage = new BufferedImage(
													  image.getWidth(null),
													  image.getHeight(null),
													  BufferedImage.TYPE_INT_RGB );
				Graphics g = bufferedImage.createGraphics();
				g.drawImage(image,0,0,null);
				g.dispose();
				return bufferedImage ;
			} 
	}
		
	public boolean translate( String inFile, String outFile, int type, int heightMax , int widthMax ) {        
        try{ 
			if(isImage(inFile)) {
				
	        	 /** Création d'une nouvelle image */
		         if(type == SCALE)	{
		        	BufferedImage bf = this.toBufferedImage(toolkit.getImage(inFile));
	        		int largeur = bf.getWidth();
	        		int hauteur = bf.getHeight();
	        		float coeff = 1;
	        		if(largeur < hauteur)
	        			coeff = (float)hauteur / heightMax;
	        		else
	        			coeff = (float)largeur / widthMax;
	        		if(coeff < 1) coeff = 1;
	        		File output = new File(outFile);
	        		ImageIO.write(scale(bf,1/coeff), "JPEG", output);
	           	} else {
					Image image = toolkit.getImage(inFile).getScaledInstance(widthMax, heightMax, 1);
		            File output = new File(outFile);
		            ImageIO.write(toBufferedImage(image), "JPEG", output);
	        	}
	    	}else {
	    		return false;
	    	}
        } catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	    
	}
		
	private boolean isImage(String file)
	{
		int pos = file.lastIndexOf(".");
		if(pos > 0)
		{
			String ext = file.substring(file.lastIndexOf(".")+1);
			if(ext.equalsIgnoreCase("jpg"))
				return true;
			if(ext.equalsIgnoreCase("gif"))
				return true;
			if(ext.equalsIgnoreCase("jpeg"))
				return true;
			if(ext.equalsIgnoreCase("bmp"))
				return true;
			if(ext.equalsIgnoreCase("png"))
				return true;
		}
		return false;
	}
	
	private BufferedImage scale(BufferedImage bi, double scaleValue)
	{
        AffineTransform tx = new AffineTransform();
        tx.scale(scaleValue, scaleValue);       
        
        AffineTransformOp op = new AffineTransformOp(tx,
                AffineTransformOp.TYPE_BILINEAR);
        BufferedImage biNew = new BufferedImage( (int) (bi.getWidth() * scaleValue),
                (int) (bi.getHeight() * scaleValue),
                bi.getType());
        return op.filter(bi, biNew);
                
	}
}
