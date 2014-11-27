package com.link_intersystems.tools.git.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.io.Serializable;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoundedRangeModel;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JProgressBar;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.WindowConstants;

import com.link_intersystems.tools.git.service.ProgressMonitor;

public class MainFrame implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 3532566379033471650L;

	public static final String MB_PATH_FILE = "file";
	public static final String MB_PATH_VIEW = "view";

	private JFrame mainFrame;
	private Component mainComponent;
	private JProgressBar jProgressBar;
	private ProgressMonitorComponentVisibilityAdapter progressBarVisibilityAdapter;
	private JMenuBar menuBar;
	private JMenu fileMenu;
	private JMenu viewMenu;

	public MainFrame() {
		mainFrame = new JFrame("GitDirStat");
		mainFrame.setSize(800, 600);
		mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		mainFrame.setLocationRelativeTo(null);
		Container contentPane = mainFrame.getContentPane();
		contentPane.setLayout(new BorderLayout());

		jProgressBar = new JProgressBar();
		jProgressBar.setVisible(true);
		jProgressBar.setStringPainted(true);
		BoundedRangeModel progressModel = jProgressBar.getModel();
		ProgressMonitor progressMonitor = new BoundedRangeModelProgressListener(
				progressModel);
		progressBarVisibilityAdapter = new ProgressMonitorComponentVisibilityAdapter(
				progressMonitor, jProgressBar);
		contentPane.add(jProgressBar, BorderLayout.SOUTH);

		menuBar = new JMenuBar();

		fileMenu = new JMenu("File");
		menuBar.add(fileMenu);
		viewMenu = new JMenu("View");
		menuBar.add(viewMenu);

		mainFrame.add(menuBar, BorderLayout.NORTH);
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
		contentPane.repaint();
	}

	public ProgressMonitor getProgressMonitor() {
		return progressBarVisibilityAdapter;
	}

	public void setVisible(boolean visible) {
		mainFrame.setVisible(visible);
	}

	public void addMenuBarActionGroup(String menubarPath, Action selected,
			Action... additionalActions) {
		if (menubarPath.startsWith(MB_PATH_VIEW)) {
			ButtonGroup group = new ButtonGroup();
			JRadioButtonMenuItem radioButton = new JRadioButtonMenuItem(
					selected);
			group.add(radioButton);
			viewMenu.add(radioButton);
			ButtonModel selectedButtonModel = radioButton.getModel();

			for (int i = 0; i < additionalActions.length; i++) {
				Action action = additionalActions[i];
				radioButton = new JRadioButtonMenuItem(action);
				group.add(radioButton);
				viewMenu.add(radioButton);
			}

			group.setSelected(selectedButtonModel, true);
		}
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