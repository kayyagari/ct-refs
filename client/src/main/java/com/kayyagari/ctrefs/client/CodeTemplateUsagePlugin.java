package com.kayyagari.ctrefs.client;

import java.lang.reflect.Field;

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
		parent.alertInformation(parent, CodeTemplateUsageServletInterface.PLUGIN_NAME);
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
