package spell.dialogs;


import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import spell.tools.LocaleManager;


public class OptionsImportDialog extends TitleAreaDialog {

	private Button importImage;
	private boolean importImageEnabled;
	
	public OptionsImportDialog(Shell parentShell) {
		super(parentShell);
	}
	
	@Override
	protected Point getInitialSize() {
		return new Point(340,165);
	}

	@Override
	public void create() {
		super.create();
		// Set the title
		setTitle(LocaleManager.instance().getMessage("importOptions"));
		// Set the message
		
		setMessage(	LocaleManager.instance().getMessage("importInformation") , IMessageProvider.INFORMATION);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		importImage = new Button(parent,SWT.CHECK);
		importImage.setSelection(true);
		importImage.setText(LocaleManager.instance().getMessage("importOptionImage"));
		
		GridLayout gridLayout = new GridLayout();
        gridLayout.marginWidth = 5;
        gridLayout.marginHeight = 2;
        gridLayout.numColumns = 1;
		parent.setLayout(gridLayout);

		return parent;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		parent.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER, GridData.FILL,true,true));
		// Create Add button
		// Own method as we need to overview the SelectionAdapter
		createOkButton(parent, OK, LocaleManager.instance().getMessage("validate"), true);
		// Add a SelectionListener

		// Create Cancel button
		Button cancelButton = createButton(parent, CANCEL, LocaleManager.instance().getMessage("cancel"), false);
		// Add a SelectionListener
		cancelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setReturnCode(CANCEL);
				close();
			}
		});
	}

	protected Button createOkButton(Composite parent, int id, String label,
			boolean defaultButton) {
		// increment the number of columns in the button bar
		((GridLayout) parent.getLayout()).numColumns++;
		Button button = new Button(parent, SWT.PUSH);
		button.setText(label);
		button.setFont(JFaceResources.getDialogFont());
		button.setData(new Integer(id));
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (isValidInput()) {
					okPressed();
				}
			}
		});
		if (defaultButton) {
			Shell shell = parent.getShell();
			if (shell != null) {
				shell.setDefaultButton(button);
			}
		}
		setButtonLayoutData(button);
		return button;
	}

	private boolean isValidInput() {
		boolean valid = true;
		return valid;
	}

	// We allow the user to resize this dialog
	@Override
	protected boolean isResizable() {
		return true;
	}

	// We need to have the textFields into Strings because the UI gets disposed
	// and the Text Fields are not accessible any more.
	private void saveInput() {
		importImageEnabled = importImage.getSelection();
	}

	@Override
	protected void okPressed() {
		saveInput();
		super.okPressed();
	}

	public boolean isImportImageEnabled() {
		return importImageEnabled;
	}


	
}


