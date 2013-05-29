package spell;

import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

import spell.actions.ExportAndroidAction;
import spell.actions.ExportCardAction;
import spell.actions.ImportCardAction;
import spell.actions.LoadDeckAction;
import spell.actions.NewMagicItemAction;
import spell.actions.NewSpellAction;
import spell.actions.PrintDeckAction;
import spell.actions.SaveDeckAction;
import spell.tools.LocaleManager;


/**
 * An action bar advisor is responsible for creating, adding, and disposing of
 * the actions added to a workbench window. Each window will be populated with
 * new actions.
 */
public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

	// Actions - important to allocate these only in makeActions, and then use
	// them
	// in the fill methods. This ensures that the actions aren't recreated
	// when fillActionBars is called with FILL_PROXY.
	private IWorkbenchAction exitAction;
    private NewSpellAction newSpellAction;
    private ExportAndroidAction exportAndroidAction;
    private NewMagicItemAction newMagicItemAction;
    private IWorkbenchAction saveAction;
    private IWorkbenchAction saveAllAction;
    private IWorkbenchAction closeAction;
    private IWorkbenchAction closeAllAction;
    private ExportCardAction exportAction;
    private ImportCardAction importAction;
    private PrintDeckAction printAction;
    private SaveDeckAction saveSpellListAction;
    private LoadDeckAction loadSpellListAction;

	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
	}

	protected void makeActions(final IWorkbenchWindow window) {
		// Creates the actions and registers them.
		// Registering is needed to ensure that key bindings work.
		// The corresponding commands keybindings are defined in the plugin.xml
		// file.
		// Registering also provides automatic disposal of the actions when
		// the window is closed.

		exitAction = ActionFactory.QUIT.create(window);
		exitAction.setText(LocaleManager.instance().getMessage("exit"));
		register(exitAction);
		
		newSpellAction = new NewSpellAction(LocaleManager.instance().getMessage("newSpell"));
        register(newSpellAction);

        
		newMagicItemAction = new NewMagicItemAction(LocaleManager.instance().getMessage("newMagicItem"));
        register(newMagicItemAction);

		exportAndroidAction = new ExportAndroidAction(LocaleManager.instance().getMessage("exportAndroid"));
        register(exportAndroidAction);

        saveAction = ActionFactory.SAVE.create(window);
        saveAction.setText(LocaleManager.instance().getMessage("save"));
		register(saveAction);

        saveAllAction = ActionFactory.SAVE_ALL.create(window);
        saveAllAction.setText(LocaleManager.instance().getMessage("saveAll"));
		register(saveAllAction);
		
        closeAction = ActionFactory.CLOSE.create(window);
        closeAction.setText(LocaleManager.instance().getMessage("close"));
		register(saveAllAction);
		
        closeAllAction = ActionFactory.CLOSE_ALL.create(window);
        closeAllAction.setText(LocaleManager.instance().getMessage("closeAll"));
		register(saveAllAction);
		
		exportAction = new ExportCardAction(window, LocaleManager.instance().getMessage("export"));
        register(exportAction);
        
		importAction = new ImportCardAction(window, LocaleManager.instance().getMessage("import"));
        register(importAction);
        
        printAction = new PrintDeckAction(LocaleManager.instance().getMessage("print"));
        register(printAction);

        loadSpellListAction = new LoadDeckAction(LocaleManager.instance().getMessage("load"));
        register(loadSpellListAction);

        saveSpellListAction = new SaveDeckAction(LocaleManager.instance().getMessage("save"));
        register(saveSpellListAction);

	}

	protected void fillMenuBar(IMenuManager menuBar) {
		MenuManager fileMenu = new MenuManager("&" + LocaleManager.instance().getMessage("file"),IWorkbenchActionConstants.M_FILE);
		menuBar.add(fileMenu);
		MenuManager deckMenu = new MenuManager("&"  + LocaleManager.instance().getMessage("deck"));
		menuBar.add(deckMenu);
		        
        // File
        fileMenu.add(newSpellAction);
        fileMenu.add(newMagicItemAction);
        fileMenu.add(new Separator());	
        fileMenu.add(exportAndroidAction);
        fileMenu.add(new Separator());	
        fileMenu.add(closeAction);
        fileMenu.add(closeAllAction);
        fileMenu.add(new Separator());	
        fileMenu.add(saveAction);
        fileMenu.add(saveAllAction);
        fileMenu.add(new Separator());	
		fileMenu.add(exitAction);
		
		//Deck
		deckMenu.add(loadSpellListAction);
		deckMenu.add(saveSpellListAction);
		deckMenu.add(new Separator());
		deckMenu.add(printAction);
		deckMenu.add(new Separator());
		deckMenu.add(exportAction);
		deckMenu.add(importAction);
		
	}

    protected void fillCoolBar(ICoolBarManager coolBar) {
        IToolBarManager toolbar = new ToolBarManager(SWT.FLAT | SWT.RIGHT);
        coolBar.add(new ToolBarContributionItem(toolbar, "main"));   
        toolbar.add(newSpellAction);
        toolbar.add(newMagicItemAction);
        toolbar.add(new Separator());	
        toolbar.add(saveAction);
        toolbar.add(saveAllAction);
        toolbar.add(new Separator());	
  }

}
