package com.kayyagari.ctrefs.client;

import static com.mirth.connect.client.ui.ChannelPanel.DATA_TYPE_COLUMN_NAME;
import static com.mirth.connect.client.ui.ChannelPanel.DEPLOYED_REVISION_DELTA_COLUMN_NAME;
import static com.mirth.connect.client.ui.ChannelPanel.DESCRIPTION_COLUMN_NAME;
import static com.mirth.connect.client.ui.ChannelPanel.ID_COLUMN_NAME;
import static com.mirth.connect.client.ui.ChannelPanel.LAST_DEPLOYED_COLUMN_NAME;
import static com.mirth.connect.client.ui.ChannelPanel.LAST_MODIFIED_COLUMN_NAME;
import static com.mirth.connect.client.ui.ChannelPanel.LOCAL_CHANNEL_ID;
import static com.mirth.connect.client.ui.ChannelPanel.NAME_COLUMN_NAME;
import static com.mirth.connect.client.ui.ChannelPanel.STATUS_COLUMN_NAME;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdesktop.swingx.treetable.AbstractMutableTreeTableNode;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jdesktop.swingx.treetable.TreeTableNode;

import com.kayyagari.ctrefs.shared.CodeTemplateUsageServletInterface;
import com.mirth.connect.client.core.ClientException;
import com.mirth.connect.client.ui.AbstractChannelTableNode;
import com.mirth.connect.client.ui.ChannelGroupStatus;
import com.mirth.connect.client.ui.ChannelTableColumnFactory;
import com.mirth.connect.client.ui.ChannelTableNode;
import com.mirth.connect.client.ui.ChannelTableNodeFactory;
import com.mirth.connect.client.ui.ChannelTreeTableModel;
import com.mirth.connect.client.ui.Frame;
import com.mirth.connect.client.ui.Mirth;
import com.mirth.connect.client.ui.PlatformUI;
import com.mirth.connect.client.ui.UIConstants;
import com.mirth.connect.client.ui.codetemplate.CodeTemplateLibraryTreeTableNode;
import com.mirth.connect.client.ui.codetemplate.CodeTemplatePanel;
import com.mirth.connect.client.ui.codetemplate.CodeTemplateRootTreeTableNode;
import com.mirth.connect.client.ui.codetemplate.CodeTemplateTableColumnFactory;
import com.mirth.connect.client.ui.codetemplate.CodeTemplateTreeTableModel;
import com.mirth.connect.client.ui.codetemplate.CodeTemplateTreeTableNode;
import com.mirth.connect.client.ui.components.MirthTreeTable;
import com.mirth.connect.model.Channel;
import com.mirth.connect.model.ChannelGroup;
import com.mirth.connect.model.ChannelStatus;
import com.mirth.connect.model.ChannelSummary;
import com.mirth.connect.model.codetemplates.BasicCodeTemplateProperties;
import com.mirth.connect.model.codetemplates.CodeTemplate;
import com.mirth.connect.model.codetemplates.CodeTemplateLibrary;
import com.mirth.connect.model.codetemplates.CodeTemplateProperties.CodeTemplateType;

import net.miginfocom.swing.MigLayout;

public class CodeTemplateUsagePanel extends JPanel {

    private JSplitPane splitPane;
    private MirthTreeTable templateTreeTable;
    private MirthTreeTable channelTable;
    private JScrollPane templateTreeTableScrollPane;
    private JScrollPane channelScrollPane;
    private CodeTemplateUsageServletInterface ctUsageServlet;

    public CodeTemplateUsagePanel() {
    	initComponents();
    	ctUsageServlet = PlatformUI.MIRTH_FRAME.mirthClient.getServlet(CodeTemplateUsageServletInterface.class);
	}

    public void search() throws Exception {
    	Frame parent = PlatformUI.MIRTH_FRAME;
    	String selectedId = parent.codeTemplatePanel.getCurrentSelectedId();
    	System.out.println("selected CT/CTLib id " + selectedId);
    	
    	List<CodeTemplateLibrary> lst = parent.mirthClient.getAllCodeTemplateLibraries(true);
    	addCodeTemplates(lst);
    }

    private void addCodeTemplates(List<CodeTemplateLibrary> lst) {
        CodeTemplateTreeTableModel model = (CodeTemplateTreeTableModel) templateTreeTable.getTreeTableModel();
        CodeTemplateRootTreeTableNode root = new CodeTemplateRootTreeTableNode();

        for (CodeTemplateLibrary library : lst) {
            CodeTemplateLibraryTreeTableNode libraryNode = new CodeTemplateLibraryTreeTableNode(library);
            for (CodeTemplate codeTemplate : library.getCodeTemplates()) {
                libraryNode.add(new CodeTemplateTreeTableNode(codeTemplate));
            }
            root.add(libraryNode);
        }
        model.setRoot(root);
        templateTreeTable.expandAll();
        model.sort();
    }

    private void fetchUsageInfo() {
    	System.out.println("fetching usage info");
        int selectedRow = templateTreeTable.getSelectedRow();
        if (selectedRow >= 0) {
            TreePath selectedPath = templateTreeTable.getPathForRow(selectedRow);
            if (selectedPath != null) {
            	TreeTableNode selectedNode = (TreeTableNode) selectedPath.getLastPathComponent();
            	String id = (String) selectedNode.getValueAt(CodeTemplatePanel.TEMPLATE_ID_COLUMN);

            	try {
            		List<ChannelSummary> lst = null;
            		if (selectedNode instanceof CodeTemplateTreeTableNode) {
            			lst = ctUsageServlet.findUsageOfCodeTemplate(id);
            		}
            		else {
            			lst = ctUsageServlet.findUsageOfCodeTemplateLib(id);
            		}
            		
                	List<ChannelStatus> channelStatusLst = new ArrayList<>();
                	for(ChannelSummary cs : lst) {
                		channelStatusLst.add(cs.getChannelStatus());
                	}
                	ChannelGroup cg = new ChannelGroup();
                	ChannelGroupStatus cgs = new ChannelGroupStatus(cg, channelStatusLst);
                	ChannelTreeTableModel model = (ChannelTreeTableModel) channelTable.getTreeTableModel();

                	model.update(Collections.singletonList(cgs));
            	}
            	catch(ClientException e) {
            		PlatformUI.MIRTH_FRAME.alertThrowable(this, e);
            	}
            }
        }
    }

    private void initComponents() {
    	setLayout(new MigLayout("insets 8, novisualpadding, hidemode 3, fill"));
        setBackground(UIConstants.BACKGROUND_COLOR);
        splitPane = new JSplitPane();
        splitPane.setBackground(getBackground());
        splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        splitPane.setBorder(BorderFactory.createEmptyBorder());
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(Preferences.userNodeForPackage(Mirth.class).getInt("height", UIConstants.MIRTH_HEIGHT) / 3);
        splitPane.setResizeWeight(0.5);

        templateTreeTable = new MirthTreeTable("CodeTemplate", new HashSet<String>(Arrays.asList(new String[] {"Name", "Description", "Revision", "Last Modified" })));

        DefaultTreeTableModel model = new CodeTemplateTreeTableModel();
        model.setColumnIdentifiers(Arrays.asList(new String[] { "Name", "Id", "Type", "Description", "Revision", "Last Modified" }));

        CodeTemplateRootTreeTableNode rootNode = new CodeTemplateRootTreeTableNode();
        model.setRoot(rootNode);

        templateTreeTable.setColumnFactory(new CodeTemplateTableColumnFactory());
        templateTreeTable.setTreeTableModel(model);
        templateTreeTable.setOpenIcon(null);
        templateTreeTable.setClosedIcon(null);
        templateTreeTable.setLeafIcon(null);
        templateTreeTable.setRootVisible(false);
        templateTreeTable.setDoubleBuffered(true);
        templateTreeTable.setDragEnabled(false);
        templateTreeTable.setRowSelectionAllowed(true);
        templateTreeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        templateTreeTable.setRowHeight(UIConstants.ROW_HEIGHT);
        templateTreeTable.setFocusable(true);
        templateTreeTable.setOpaque(true);
        templateTreeTable.getTableHeader().setReorderingAllowed(true);
        templateTreeTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        templateTreeTable.setEditable(false);
        templateTreeTable.setSortable(true);
        templateTreeTable.setAutoCreateColumnsFromModel(false);
        templateTreeTable.setShowGrid(true, true);
        templateTreeTable.restoreColumnPreferences();
        templateTreeTable.setMirthColumnControlEnabled(true);

        if (Preferences.userNodeForPackage(Mirth.class).getBoolean("highlightRows", true)) {
            templateTreeTable.setHighlighters(HighlighterFactory.createAlternateStriping(UIConstants.HIGHLIGHTER_COLOR, UIConstants.BACKGROUND_COLOR));
        }
        
        templateTreeTableScrollPane = new JScrollPane(templateTreeTable);
        templateTreeTableScrollPane.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0x6E6E6E)));
        
        templateTreeTable.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() > 1) {
					fetchUsageInfo();
				}
			}
        	
		});

        // Channel table
        List<String> columns = Arrays.asList(new String[] { STATUS_COLUMN_NAME,
        		DATA_TYPE_COLUMN_NAME, NAME_COLUMN_NAME, ID_COLUMN_NAME, LOCAL_CHANNEL_ID,
        		DESCRIPTION_COLUMN_NAME, DEPLOYED_REVISION_DELTA_COLUMN_NAME, LAST_DEPLOYED_COLUMN_NAME,
        		LAST_MODIFIED_COLUMN_NAME });

        channelTable = new MirthTreeTable("channelPanel", new LinkedHashSet<String>(columns));

        channelTable.setColumnFactory(new ChannelTableColumnFactory());

        ChannelTreeTableModel channelTreeModel = new ChannelTreeTableModel();
        channelTreeModel.setColumnIdentifiers(columns);
        channelTreeModel.setGroupModeEnabled(false);
        channelTreeModel.setNodeFactory(new ChannelTableNodeFactory() {
        	@Override
        	public AbstractChannelTableNode createNode(ChannelGroupStatus groupStatus) {
        		return new ChannelTableNode(groupStatus);
        	}
        	
        	@Override
        	public AbstractChannelTableNode createNode(ChannelStatus channelStatus) {
        		return new ChannelTableNode(channelStatus);
        	}
        });
        channelTable.setTreeTableModel(channelTreeModel);

        channelTable.setDoubleBuffered(true);
        channelTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        channelTable.getTreeSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        channelTable.setHorizontalScrollEnabled(true);
        channelTable.packTable(UIConstants.COL_MARGIN);
        channelTable.setRowHeight(UIConstants.ROW_HEIGHT);
        channelTable.setOpaque(true);
        channelTable.setRowSelectionAllowed(true);
        channelTable.setSortable(true);
        channelTable.putClientProperty("JTree.lineStyle", "Horizontal");
        channelTable.setAutoCreateColumnsFromModel(false);
        channelTable.setShowGrid(true, true);
        channelTable.restoreColumnPreferences();
        channelTable.setMirthColumnControlEnabled(true);

        channelTable.setDragEnabled(false);
        channelTable.setLeafIcon(UIConstants.ICON_CHANNEL);
        channelTable.setOpenIcon(UIConstants.ICON_GROUP);
        channelTable.setClosedIcon(UIConstants.ICON_GROUP);

        channelScrollPane = new JScrollPane(channelTable);
        channelScrollPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        
        splitPane.setTopComponent(templateTreeTableScrollPane);
        splitPane.setBottomComponent(channelScrollPane);
        
        add(splitPane);
    }
    
    private void addTestCodeTemplates() {
    	List<CodeTemplateLibrary> lst = new ArrayList<>();
    	lst.add(createCTLib());
    	addCodeTemplates(lst);
    }

    private CodeTemplateLibrary createCTLib() {
    	CodeTemplateLibrary ctl = new CodeTemplateLibrary();
    	ctl.setId(UUID.randomUUID().toString());
    	ctl.setDescription("CodeTemplateLibrary 1");
    	ctl.setName("CTL1");
    	ctl.setRevision(1);
    	ctl.setCodeTemplates(Collections.singletonList(createCT()));
    	
    	return ctl;
    }

    private CodeTemplate createCT() {
    	CodeTemplate ct = new CodeTemplate(UUID.randomUUID().toString());
    	ct.setLastModified(Calendar.getInstance());
    	ct.setName("ct1");
    	ct.setRevision(1);
    	BasicCodeTemplateProperties bctp = new BasicCodeTemplateProperties(CodeTemplateType.FUNCTION, "function a(){}");
    	ct.setProperties(bctp);
    	
    	return ct;
    }

    private void addTestChannel() {
    	ChannelGroup cg = new ChannelGroup();
    	List<ChannelStatus> lst = new ArrayList<>();
    	ChannelStatus cs = new ChannelStatus();
    	Channel ch = new Channel(UUID.randomUUID().toString());
    	ch.setDescription("test channel");
    	ch.setName("test channel");
    	ch.setNextMetaDataId(1);
    	ch.setRevision(1);
    	
    	cs.setChannel(ch);
    	cs.setCodeTemplatesChanged(false);
    	cs.setDeployedDate(Calendar.getInstance());
    	cs.setDeployedRevisionDelta(1);
    	//cs.setLocalChannelId(ch.g);
    	lst.add(cs);
    	ChannelGroupStatus cgs = new ChannelGroupStatus(cg, lst);
    	ChannelTreeTableModel model = (ChannelTreeTableModel) channelTable.getTreeTableModel();

    	model.update(Collections.singletonList(cgs));
    }

    public static void main(String[] args) {
		JFrame frame = new JFrame("CodeTemplate Usage");
		frame.setBounds(100, 100, 400, 400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		CodeTemplateUsagePanel panel = new CodeTemplateUsagePanel();
		panel.addTestCodeTemplates();
		panel.addTestChannel();
		frame.getContentPane().setLayout(new FlowLayout());
		frame.getContentPane().add(panel);
		//frame.pack();
		frame.setVisible(true);
	}
}
