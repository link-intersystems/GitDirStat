package com.link_intersystems.gitdirstat.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JRootPane;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;

import org.apache.commons.io.IOUtils;

import com.link_intersystems.gitdirstat.domain.GitRepositoryAccess;
import com.link_intersystems.gitdirstat.domain.TreeObject;
import com.link_intersystems.gitdirstat.ui.UIContext.IconType;
import com.link_intersystems.gitdirstat.ui.help.OpenAboutAction;
import com.link_intersystems.swing.ListModelSelectionMediator;
import com.link_intersystems.swing.ProgressDialogMonitor;
import com.link_intersystems.swing.ProgressMonitor;
import com.link_intersystems.swing.SingleActionSelectionMediator;

public class MainFrame implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 3532566379033471650L;

	public static final String MB_PATH_FILE = "file";
	public static final String MB_PATH_VIEW = "view";
	public static final String MB_PATH_HELP = "help";

	private static class ButtonGroupActionSelectionSync implements
			PropertyChangeListener {

		private ButtonGroup buttonGroup;

		public ButtonGroupActionSelectionSync(ButtonGroup buttonGroup) {
			this.buttonGroup = buttonGroup;
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			Action selectedAction = (Action) evt.getNewValue();
			Enumeration<AbstractButton> elements = buttonGroup.getElements();
			while (elements.hasMoreElements()) {
				AbstractButton abstractButton = elements.nextElement();
				Action action = abstractButton.getAction();
				buttonGroup.setSelected(abstractButton.getModel(),
						selectedAction.equals(action));

			}
		}
	};

	private class UIContextImpl implements UIContext {

		private MainFrame mainFrame;

		public UIContextImpl(MainFrame mainFrame) {
			this.mainFrame = mainFrame;
		}

		@Override
		public Window getMainFrame() {
			return mainFrame.mainFrame;
		}

		@Override
		public ImageIcon getIcon(IconType iconType) {
			String iconFromClasspath = getIconFromClasspath(iconType.getName());
			return getIcon(iconFromClasspath);
		}

		public ImageIcon getIcon(String classpath) {
			InputStream resourceAsStream = getClass().getClassLoader()
					.getResourceAsStream(classpath);
			try {
				byte[] imageData = IOUtils.toByteArray(resourceAsStream);
				ImageIcon imageIcon = new ImageIcon(imageData);
				return imageIcon;
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		private String getIconFromClasspath(String iconName) {
			String resolution = "16x16";
			return MessageFormat.format("icons/{0}/{1}", resolution, iconName);
		}

		@Override
		public ProgressMonitor getProgressMonitor() {
			return MainFrame.this.progressDialogMonitor;
		}

	}

	private Map<String, SingleActionSelectionMediator> actionGroupMediator = new HashMap<String, SingleActionSelectionMediator>();
	private UIContextImpl uiContext = new UIContextImpl(this);

	private JFrame mainFrame = new JFrame("GitDirStat");
	private JToolBar jToolBar = new JToolBar();
	private JMenuBar menuBar = new JMenuBar();
	private JMenu fileMenu = new JMenu("File");
	private JMenu viewMenu = new JMenu("View");
	private JMenu helpMenu = new JMenu("Help");
	private JPanel actionPanel = new JPanel();
	private Component mainComponent;
	private ProgressDialogMonitor progressDialogMonitor = new ProgressDialogMonitor(
			mainFrame);

	private OpenRepositoryAction openRepositoryAction;
	private OpenAction openRepoAction;
	private Action startupAction;

	private GitRepositoryModel repoModel;
	private ListModelSelectionMediator<TreeObject> listSelectionMediator;

	public MainFrame(GitDirStatUIArguments arguments,
			GitRepositoryAccess repoAccess) {
		repoModel = new GitRepositoryModel();
		File gitRepositoryDir = arguments.getGitRepositoryDir();
		if (gitRepositoryDir != null) {
			repoModel.setGitDir(gitRepositoryDir);
			startupAction = new OpenRepositoryAction(uiContext, repoModel,
					repoAccess);
		}

		configureMainFrame();

		progressDialogMonitor.setMillisToDecideToPopup(100);
		progressDialogMonitor.setETAEnabled(true);

		GitRepositoryView gitRepositoryView = new GitRepositoryView();
		gitRepositoryView.setModel(repoModel);
		setMainComponent(gitRepositoryView);

		openRepositoryAction = new OpenRepositoryAction(uiContext, repoModel,
				repoAccess);
		openRepositoryAction.setDisableWhileRunning(true);

		openRepoAction = new OpenAction(repoModel, repoAccess, uiContext);

		addMenuBarAction(MB_PATH_FILE, openRepoAction);
		addMenuBarAction(MB_PATH_FILE, openRepositoryAction);

		OpenAboutAction openAboutAction = new OpenAboutAction(uiContext);
		openAboutAction.putValue(Action.NAME, "About");
		addMenuBarAction(MB_PATH_HELP, openAboutAction);

		Action showTableAction = gitRepositoryView.getSetTableAction();
		showTableAction.putValue(Action.NAME, "Show table");
		Action showTreeAction = gitRepositoryView.getSetTreeAction();
		showTreeAction.putValue(Action.NAME, "Show tree");

		addMenuBarActionGroup(MB_PATH_VIEW, showTableAction, showTreeAction);

		addToolbarActions(repoAccess);
	}

	private void addToolbarActions(GitRepositoryAccess repoAccess) {
		addUpdateRepositoryAction();
		addRemovePathAction(repoAccess);
	}

	private void addUpdateRepositoryAction() {
		openRepositoryAction.putValue(Action.NAME, "Update");
		openRepositoryAction.putValue(Action.SHORT_DESCRIPTION,
				"Update repository");

		openRepositoryAction.addDisabledActionWhileRunning(openRepoAction);
		addToolbarAction(openRepositoryAction);
	}

	private void addRemovePathAction(GitRepositoryAccess repoAccess) {
		RemovePathAction removePathAction = new RemovePathAction(repoModel,
				repoAccess, uiContext);
		removePathAction.putValue(Action.SMALL_ICON,
				uiContext.getIcon(IconType.CLEAN));
		removePathAction.setProgressMonitor(progressDialogMonitor);

		removePathAction.putValue(Action.NAME, "Remove selected paths");
		removePathAction.putValue(Action.SHORT_DESCRIPTION,
				"Remove selected paths from repository");
		addToolbarAction(removePathAction);

		PathModel pathListModel = repoModel.getPathModel();
		listSelectionMediator = new ListModelSelectionMediator<TreeObject>();
		listSelectionMediator.setSelectionModel(pathListModel
				.getSelectionModel());
		listSelectionMediator
				.addDisabledActionOnEmptySelection(removePathAction);
	}

	private void configureMainFrame() {

		Dimension size = getInitialSize();

		mainFrame.setSize(size);
		mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		mainFrame.setLocationRelativeTo(null);

		ImageIcon gitIcon = uiContext.getIcon("icons/Git-Icon-1788C.png");
		mainFrame.setIconImage(gitIcon.getImage());

		JRootPane rootPane = mainFrame.getRootPane();
		rootPane.setJMenuBar(menuBar);

		menuBar.add(fileMenu);
		menuBar.add(viewMenu);
		menuBar.add(helpMenu);

		actionPanel.setLayout(new BorderLayout());

		actionPanel.add(jToolBar, BorderLayout.CENTER);

		mainFrame.add(actionPanel, BorderLayout.NORTH);
	}

	private Dimension getInitialSize() {
		GraphicsEnvironment lge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		GraphicsDevice defaultScreenDevice = lge.getDefaultScreenDevice();
		GraphicsConfiguration defaultConfiguration = defaultScreenDevice
				.getDefaultConfiguration();
		Rectangle bounds = defaultConfiguration.getBounds();
		int initialWidth = (int) (bounds.width * 0.8);
		int initialHeight = (int) (bounds.height * 0.8);
		return new Dimension(initialWidth, initialHeight);
	}

	public void addToolbarAction(Action action) {
		this.jToolBar.add(action);
	}

	public void addMenuBarAction(String menubarPath, Action action) {
		JMenuItem actionItem = new JMenuItem(action);
		JMenu menu = getMenu(menubarPath);
		menu.add(actionItem);
	}

	public void setMainComponent(JComponent component) {
		Container contentPane = mainFrame.getContentPane();
		if (mainComponent != null) {
			contentPane.remove(mainComponent);
		}
		contentPane.add(component, BorderLayout.CENTER);
		mainComponent = component;
		contentPane.invalidate();
		contentPane.validate();
		contentPane.repaint();
	}

	public void setVisible(boolean visible) {
		mainFrame.setVisible(visible);
		if (startupAction != null) {
			startupAction.actionPerformed(null);
		}
	}

	public void addMenuBarActionGroup(String menubarPath, Action... actions) {
		JMenu menu = getMenu(menubarPath);
		final ButtonGroup group = new ButtonGroup();
		SingleActionSelectionMediator actionGroupMediator = getActionGroupMediator(menubarPath);
		actionGroupMediator.setActionGroup(actions);
		ButtonGroupActionSelectionSync buttonGroupActionSelectionSync = new ButtonGroupActionSelectionSync(
				group);
		actionGroupMediator.addPropertyChangeListener(
				SingleActionSelectionMediator.PROP_SELECTED_ACTION,
				buttonGroupActionSelectionSync);

		JRadioButtonMenuItem radioButton = null;
		for (int i = 0; i < actions.length; i++) {
			Action action = actions[i];
			radioButton = new JRadioButtonMenuItem(action);
			group.add(radioButton);
			menu.add(radioButton);
			Boolean selected = (Boolean) action.getValue(Action.SELECTED_KEY);
			boolean isSelected = selected != null && selected;
			group.setSelected(radioButton.getModel(), isSelected);
		}
	}

	private JMenu getMenu(String menubarPath) {
		JMenu menu = null;
		if (menubarPath.startsWith(MB_PATH_FILE)) {
			menu = fileMenu;
		} else if (menubarPath.startsWith(MB_PATH_VIEW)) {
			menu = viewMenu;
		} else if (menubarPath.startsWith(MB_PATH_HELP)) {
			menu = helpMenu;
		} else {
			throw new IllegalArgumentException("Menubar path " + menubarPath
					+ " is not supported");
		}
		return menu;
	}

	private SingleActionSelectionMediator getActionGroupMediator(
			String menubarPath) {
		SingleActionSelectionMediator selectionMediator = actionGroupMediator
				.get(menubarPath);
		if (selectionMediator == null) {
			selectionMediator = new SingleActionSelectionMediator();
			actionGroupMediator.put(menubarPath, selectionMediator);
		}
		return selectionMediator;
	}

	public Action createMainComponentSetterAction(String actionName,
			JComponent component) {
		class MainComponentSetterAction extends AbstractAction {

			/**
			 *
			 */
			private static final long serialVersionUID = 3614889449548477302L;
			private JComponent component;

			public MainComponentSetterAction(String name, JComponent component) {
				super(name);
				this.component = component;
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				setMainComponent(component);
			}
		}
		return new MainComponentSetterAction(actionName, component);
	}

	public JFrame getMainFrame() {
		return mainFrame;
	}

}
