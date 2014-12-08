package com.link_intersystems.tools.git.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;

import com.link_intersystems.swing.ProgressBarMonitor;
import com.link_intersystems.swing.ProgressDialogMonitor;
import com.link_intersystems.swing.ProgressMonitor;
import com.link_intersystems.swing.SingleActionSelectionMediator;

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
	private JPanel northPanel = new JPanel();

	private Map<String, SingleActionSelectionMediator> actionGroupMediator = new HashMap<String, SingleActionSelectionMediator>();

	private Component mainComponent;
	private ProgressMonitor progressMonitor;
	private JToolBar jToolBar = new JToolBar();

	public MainFrame(GitRepositoryModel gitRepositoryModel) {
		mainFrame.setSize(800, 600);
		mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		mainFrame.setLocationRelativeTo(null);

		Container contentPane = mainFrame.getContentPane();
		contentPane.setLayout(new BorderLayout());

		progressMonitor = createProgressMonitor(mainFrame);

		menuBar.add(fileMenu);
		menuBar.add(viewMenu);

		northPanel.setLayout(new BorderLayout());
		northPanel.add(menuBar, BorderLayout.NORTH);
		northPanel.add(jToolBar, BorderLayout.SOUTH);

		mainFrame.add(northPanel, BorderLayout.NORTH);
	}

	public void addToolbarAction(Action action) {
		this.jToolBar.add(action);
	}

	private ProgressMonitor createProgressMonitor(JFrame mainFrame) {
		ProgressMonitor progressMonitor = null;
		ProgressDialogMonitor progressDialogMonitor = new ProgressDialogMonitor(
				mainFrame);
		progressDialogMonitor.setMillisToDecideToPopup(100);
		progressMonitor = progressDialogMonitor;
		return progressMonitor;
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

	public ProgressMonitor getProgressMonitor() {
		return progressMonitor;
	}

	public void setVisible(boolean visible) {
		mainFrame.setVisible(visible);
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
