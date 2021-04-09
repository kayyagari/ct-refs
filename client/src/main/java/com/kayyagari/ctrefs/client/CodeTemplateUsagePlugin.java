package com.kayyagari.ctrefs.client;

import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXTaskPane;

import com.kayyagari.ctrefs.shared.CodeTemplateUsageServletInterface;
import com.mirth.connect.client.ui.UIConstants;
import com.mirth.connect.plugins.ClientPlugin;

import net.miginfocom.swing.MigLayout;

/**
 * 
 * @author Kiran Ayyagari (kayyagari@apache.org)
 */
public class CodeTemplateUsagePlugin extends ClientPlugin {

	private JFrame ctSearchFrame;
	public CodeTemplateUsagePlugin(String name) {
		super(CodeTemplateUsageServletInterface.PLUGIN_NAME);
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
        ClassLoader cl = getClass().getClassLoader();
        ImageIcon icon = new ImageIcon(cl.getResource("zoom.png"));

        JXTaskPane channelTasks = parent.channelPanel.channelTasks;
		parent.addTask("searchCode", "Search Code", "Search CodeTemplate References in Channels", "s", icon, channelTasks, null, this);
	}

	public void searchCode() {
		if(ctSearchFrame != null) {
			ctSearchFrame.requestFocus();
			return;
		}

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					ctSearchFrame = new JFrame();
					ctSearchFrame.setTitle("CodeTemplate Usage Search");
					ctSearchFrame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					ctSearchFrame.addWindowListener(new WindowAdapter() {
						@Override
						public void windowClosed(WindowEvent e) {
							ctSearchFrame = null;
						}
					});
					CodeTemplateUsagePanel panel = new CodeTemplateUsagePanel();
					panel.search();
					Rectangle r = parent.getBounds();
					ctSearchFrame.getContentPane().setLayout(new MigLayout("insets 2, novisualpadding, hidemode 3, fill"));
					ctSearchFrame.getContentPane().add(panel, "growx");
					ctSearchFrame.setBounds(r.x + 150, r.y + 100, r.width - 200, r.height - 200);
					ctSearchFrame.setVisible(true);
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
