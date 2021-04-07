package com.kayyagari.ctrefs.client;

import java.lang.reflect.Field;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXTaskPane;

import com.kayyagari.ctrefs.shared.CodeTemplateUsageServletInterface;
import com.mirth.connect.client.ui.codetemplate.CodeTemplatePanel;
import com.mirth.connect.plugins.ClientPlugin;

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
		CodeTemplatePanel ctPanel = parent.codeTemplatePanel;
		JXTaskPane codeTemplateTasks = getTaskPane();
		int index = parent.addTask("showUsage", "Show Usage", "tool tip", "", null, codeTemplateTasks, null, this);
		System.out.println("added task");
	}

	public void showUsage() {
		//parent.alertInformation(parent, CodeTemplateUsageServletInterface.PLUGIN_NAME);
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

	private JXTaskPane getTaskPane() {
		try {
			Field f = CodeTemplatePanel.class.getDeclaredField("codeTemplateTasks");
			f.setAccessible(true);
			return (JXTaskPane) f.get(parent.codeTemplatePanel);
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
			throw new RuntimeException(e);
		}
	}
}
