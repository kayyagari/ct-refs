package com.kayyagari.ctrefs.client;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXTaskPane;

import com.kayyagari.ctrefs.shared.CodeTemplateUsageServletInterface;
import com.mirth.connect.plugins.ClientPlugin;

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
					JDialog dl = new JDialog(parent);
					dl.setTitle("CodeTemplate Usage Search");
					dl.setBounds(parent.getBounds());
					dl.getContentPane().add(panel);
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
