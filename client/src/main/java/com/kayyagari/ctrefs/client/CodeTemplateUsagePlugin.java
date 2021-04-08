package com.kayyagari.ctrefs.client;

import java.awt.Rectangle;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXTaskPane;

import com.kayyagari.ctrefs.shared.CodeTemplateUsageServletInterface;
import com.mirth.connect.plugins.ClientPlugin;

import net.miginfocom.swing.MigLayout;

/**
 * 
 * @author Kiran Ayyagari (kayyagari@apache.org)
 */
public class CodeTemplateUsagePlugin extends ClientPlugin {

	public CodeTemplateUsagePlugin(String name) {
		super(CodeTemplateUsageServletInterface.PLUGIN_NAME);
		System.out.println("initializing plugin " + name);
	}

	@Override
	public String getPluginPointName() {
		return CodeTemplateUsageServletInterface.PLUGIN_NAME;
	}

	@Override
	public void reset() {
	}

	@Override
	public void start() {
		System.out.println("starting CodeTemplateUsagePlugin...");

        ClassLoader cl = getClass().getClassLoader();
        ImageIcon icon = new ImageIcon(cl.getResource("zoom.png"));

        JXTaskPane channelTasks = parent.channelPanel.channelTasks;
		parent.addTask("searchCode", "Search Code", "Search CodeTemplate References in Channels", "s", icon, channelTasks, null, this);
		System.out.println("added task");
	}

	public void searchCode() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					CodeTemplateUsagePanel panel = new CodeTemplateUsagePanel();
					panel.search();
					JDialog dl = new JDialog(parent, false);
					dl.setTitle("CodeTemplate Usage Search");
					Rectangle r = parent.getBounds();
					dl.setBounds(r.x + 150, r.y, r.width, r.height);
					dl.getContentPane().setLayout(new MigLayout("insets 8, novisualpadding, hidemode 3, fill"));
					dl.getContentPane().add(panel);
					dl.pack();
					dl.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dl.setVisible(true);
				}
				catch(Exception e) {
					parent.alertThrowable(parent, e);
				}
			}
		});
	}

	@Override
	public void stop() {
	}
}
