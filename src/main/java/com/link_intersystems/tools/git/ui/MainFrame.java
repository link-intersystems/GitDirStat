package com.link_intersystems.tools.git.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;

import com.link_intersystems.swing.AsyncActionMediator;
import com.link_intersystems.swing.CompositeAction;
import com.link_intersystems.swing.ProgressDialogMonitor;
import com.link_intersystems.swing.SingleActionSelectionMediator;
import com.link_intersystems.tools.git.GitDirStatArguments;
import com.link_intersystems.tools.git.domain.GitRepositoryAccess;

public class MainFrame implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 3532566379033471650L;

	public static final String MB_PATH_FILE = "file";
	public static final String MENU_PATH_VIEW = "view";

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

	private JFrame mainFrame = new JFrame("GitDirStat");

	private JMenuBar menuBar = new JMenuBar();
	private JMenu fileMenu = new JMenu("File");
	private JMenu viewMenu = new JMenu("View");
	private JPanel actionPanel = new JPanel();
	private ProgressDialogMonitor progressDialogMonitor = new ProgressDialogMonitor(
			mainFrame);

	private Map<String, SingleActionSelectionMediator> actionGroupMediator = new HashMap<String, SingleActionSelectionMediator>();

	private Component mainComponent;
	private JToolBar jToolBar = new JToolBar();

	private UpdateRefsAction updateRefsAction;

	private UpdateTreeObjectAction updateTreeObjectAction;

	private LoadRepositoryAction updateRepositoryAction;

	private OpenAction openRepoAction;

	private GitRepositoryModel repoModel;

	public MainFrame(GitDirStatArguments arguments,
			GitRepositoryAccess repoAccess) {
		repoModel = new GitRepositoryModel();
		File gitRepositoryDir = arguments.getGitRepositoryDir();
		if (gitRepositoryDir != null) {
			repoModel.setGitDir(gitRepositoryDir);
		}

		configureMainFrame();

		progressDialogMonitor.setMillisToDecideToPopup(100);
		progressDialogMonitor.setETAEnabled(true);

		SizeMetricsView sizeMetricsView = new SizeMetricsView();
		setMainComponent(sizeMetricsView);
		sizeMetricsView.setModel(repoModel);

		updateRefsAction = new UpdateRefsAction(repoAccess, repoModel);
		updateTreeObjectAction = new UpdateTreeObjectAction(repoAccess,
				repoModel, progressDialogMonitor);
		updateRepositoryAction = new LoadRepositoryAction("Update Repository",
				updateRefsAction, updateTreeObjectAction);

		openRepoAction = new OpenAction(repoModel, updateRepositoryAction);

		addMenuBarAction(MainFrame.MB_PATH_FILE, openRepoAction);
		addMenuBarAction(MainFrame.MB_PATH_FILE, updateRepositoryAction);
		Action updateBranchSelection = sizeMetricsView
				.createApplyBranchSelectionAction();
		updateBranchSelection.putValue(Action.NAME, "Apply selection");
		CompositeAction applyBranchSelectionAction = new CompositeAction(
				updateBranchSelection, updateRefsAction, updateRepositoryAction);

		AsyncActionMediator asyncActionMediator = new AsyncActionMediator(
				updateTreeObjectAction);
		asyncActionMediator
				.addDisabledActionWhileRunning(applyBranchSelectionAction);
		addToolbarAction(applyBranchSelectionAction);

		Action showTableAction = sizeMetricsView.getSetTableAction();
		showTableAction.putValue(Action.NAME, "Show table");
		Action showTreeAction = sizeMetricsView.getSetTreeAction();
		showTreeAction.putValue(Action.NAME, "Show tree");

		addMenuBarActionGroup(MainFrame.MENU_PATH_VIEW, showTableAction,
				showTreeAction);

		RemovePathAction removePathAction = new RemovePathAction(repoModel, repoAccess, progressDialogMonitor);
		removePathAction.putValue(Action.NAME, "Remove selected paths");
		addToolbarAction(removePathAction);
	}

	private void configureMainFrame() {
		mainFrame.setSize(800, 600);
		mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		mainFrame.setLocationRelativeTo(null);

		menuBar.add(fileMenu);
		menuBar.add(viewMenu);

		actionPanel.setLayout(new BorderLayout());
		actionPanel.add(menuBar, BorderLayout.NORTH);
		actionPanel.add(jToolBar, BorderLayout.SOUTH);

		mainFrame.add(actionPanel, BorderLayout.NORTH);
	}

	public void addToolbarAction(Action action) {
		this.jToolBar.add(action);
	}

	public void addMenuBarAction(String menubarPath, Action action) {
		if (menubarPath.startsWith(MB_PATH_FILE)) {
			JMenuItem actionItem = new JMenuItem(action);
			fileMenu.add(actionItem);
		}
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
		if (repoModel.isGitDirSet()) {
			updateRepositoryAction.actionPerformed(null);
		}
	}

	public void addMenuBarActionGroup(String menubarPath, Action... actions) {
		if (menubarPath.startsWith(MENU_PATH_VIEW)) {
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
				viewMenu.add(radioButton);
				Boolean selected = (Boolean) action
						.getValue(Action.SELECTED_KEY);
				boolean isSelected = selected != null && selected;
				group.setSelected(radioButton.getModel(), isSelected);
			}
		}
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

}
