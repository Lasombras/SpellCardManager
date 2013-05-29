package spell.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class ImageLabel extends Composite {

	private Label img;
	private Text lab;
		
	public final static int HORIZONTAL = 1;
	public final static int VERTICAL = 2;
	
	public ImageLabel(Composite parent, String label, Image image, int width, int align)  {
	    super (parent, SWT.NONE);
	    
	    if(align == HORIZONTAL) {
			GridLayout infoLayout = new GridLayout();
			infoLayout.numColumns = 2;
			infoLayout.verticalSpacing = 0;
			infoLayout.marginBottom = 0;
			infoLayout.marginTop = 0;
			infoLayout.marginHeight = 0;
			this.setLayout(infoLayout);
			img = new Label(this, SWT.SIMPLE);
			img.setImage(image);
			lab = new Text(this, SWT.SIMPLE);
			lab.setText(label);
			GridData gd = new GridData(SWT.FILL);
			gd.widthHint = width;
			lab.setLayoutData(gd);
	    } else {
			GridLayout infoLayout = new GridLayout();
			infoLayout.numColumns = 1;
			infoLayout.horizontalSpacing = infoLayout.verticalSpacing = infoLayout.marginBottom = infoLayout.marginTop = infoLayout.marginHeight = infoLayout.marginLeft = infoLayout.marginRight = infoLayout.marginWidth = 0;
			this.setLayout(infoLayout);
			this.setLayoutData(new GridData(GridData.CENTER | GridData.VERTICAL_ALIGN_BEGINNING));
			img = new Label(this, SWT.CENTER);
			img.setImage(image);
			GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL | GridData.CENTER);
			gd.widthHint = width;
			gd.grabExcessVerticalSpace = true;
			img.setLayoutData(gd);
			lab = new Text(this, SWT.WRAP | SWT.CENTER);
			lab.setText(label);
			gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL | GridData.CENTER);
			gd.widthHint = width;
			gd.grabExcessVerticalSpace = true;
			lab.setLayoutData(gd);
	    }
	}
	
	public void update(String label, Image image) {
		img.setImage(image);
		lab.setText(label);
	}

}
