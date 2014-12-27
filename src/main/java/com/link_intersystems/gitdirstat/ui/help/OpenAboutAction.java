package com.link_intersystems.gitdirstat.ui.help;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import com.link_intersystems.gitdirstat.ui.UIContext;

public class OpenAboutAction extends AbstractAction {

	private static final long serialVersionUID = 6445777324047111173L;
	private UIContext uiContext;

	public OpenAboutAction(UIContext uiContext) {
		this.uiContext = uiContext;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Window mainFrame = uiContext.getMainFrame();
		AboutPanel aboutPanel = new AboutPanel(uiContext);
		UIManagerMemento uiManagerMemento = new UIManagerMemento();
		uiManagerMemento.save();
		try {
			UIManager.put("OptionPane.background", Color.WHITE);
			UIManager.put("Panel.background", Color.WHITE);
			JOptionPane.showMessageDialog(mainFrame, aboutPanel, "About",
					JOptionPane.PLAIN_MESSAGE);
		} finally {
			uiManagerMemento.restore();
		}
	}

	private static class AboutPanel extends JPanel {

		private static final long serialVersionUID = 710144772808747198L;

		public AboutPanel(UIContext uiContext) {
			setLayout(new BorderLayout());
			JEditorPane editorPane = new JEditorPane();
			editorPane.setPreferredSize(new Dimension(600, 450));
			editorPane.setEditable(false);
			JScrollPane scrollPane = new JScrollPane(editorPane);
			scrollPane
					.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			scrollPane
					.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
			add(scrollPane, BorderLayout.CENTER);

			URL aboutUrl = AboutPanel.class
					.getResource("/html/help/about.html");
			try {
				editorPane.setPage(aboutUrl);
			} catch (IOException e) {
			}

			editorPane.addHyperlinkListener(new HyperlinkListener() {
				@Override
				public void hyperlinkUpdate(HyperlinkEvent hle) {
					if (HyperlinkEvent.EventType.ACTIVATED.equals(hle
							.getEventType())) {
						openWebpage(hle.getURL());
					}
				}
			});
		}

		public static void openWebpage(URI uri) {
			Desktop desktop = Desktop.isDesktopSupported() ? Desktop
					.getDesktop() : null;
			if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
				try {
					desktop.browse(uri);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		public static void openWebpage(URL url) {
			try {
				openWebpage(url.toURI());
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
	}

	private static class UIManagerMemento {

		private Object optionPaneBackground;
		private Object panelBackground;

		public void save() {
			optionPaneBackground = UIManager.get("OptionPane.background");
			panelBackground = UIManager.get("Panel.background");
		}

		public void restore() {
			UIManager.put("OptionPane.background", optionPaneBackground);
			UIManager.put("Panel.background", panelBackground);
		}
	}
}
