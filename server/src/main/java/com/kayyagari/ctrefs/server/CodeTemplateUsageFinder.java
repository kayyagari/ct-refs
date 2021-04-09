package com.kayyagari.ctrefs.server;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.FunctionNode;

import com.mirth.connect.client.core.ControllerException;
import com.mirth.connect.model.Channel;
import com.mirth.connect.model.ChannelHeader;
import com.mirth.connect.model.ChannelSummary;
import com.mirth.connect.model.codetemplates.CodeTemplate;
import com.mirth.connect.model.codetemplates.CodeTemplateLibrary;
import com.mirth.connect.model.converters.ObjectXMLSerializer;
import com.mirth.connect.server.controllers.ChannelController;
import com.mirth.connect.server.controllers.CodeTemplateController;
import com.mirth.connect.server.controllers.ControllerFactory;

/**
 * 
 * @author Kiran Ayyagari (kayyagari@apache.org)
 */
public class CodeTemplateUsageFinder {
	private ChannelController chController;
	private CodeTemplateController ctController;
	private ObjectXMLSerializer objSerializer;
	
	public CodeTemplateUsageFinder() {
		chController = ControllerFactory.getFactory().createChannelController();
		ctController = ControllerFactory.getFactory().createCodeTemplateController();
		objSerializer = ObjectXMLSerializer.getInstance();
	}

	public List<ChannelSummary> findUsagesOfCodeTemplate(String ctId) throws ControllerException {
		List<ChannelSummary> summaryLst = Collections.EMPTY_LIST;

		CodeTemplate ct = ctController.getCodeTemplateById(ctId);
		if(ct != null) {
			Parser parser = new Parser();
			AstRoot root = parser.parse(ct.getCode(), null, 1);
			FunctionNodeVisitor fnv = new FunctionNodeVisitor();
			root.visitAll(fnv);
			List<String> functions = fnv.getFunctionNames();
			
			Map<String, ChannelHeader> includedIn = new HashMap<>();
			ChannelHeader header = new ChannelHeader(0, null, false);
			
			Set<String> cids = chController.getChannelIds();
			List<Channel> channels = chController.getChannels(cids);
			for(Channel ch : channels) {
				String xml = objSerializer.serialize(ch);
				for(String name : functions) {
					if(StringUtils.isBlank(name)) {
						continue;
					}
					
					if(xml.contains(name)) {
						includedIn.put(ch.getId(), header);
						break;
					}
				}
			}
			summaryLst = chController.getChannelSummary(includedIn, true);
		}

		return summaryLst;
	}

	public List<ChannelSummary> findUsagesOfCodeTemplateLib(String ctLibId) throws ControllerException {
		Map<String, ChannelHeader> includedIn = new HashMap<>();
		ChannelHeader header = new ChannelHeader(0, null, false);
		
        List<CodeTemplateLibrary> codeTemplateLibraries = ctController.getLibraries(null, true);

        for(CodeTemplateLibrary ctl : codeTemplateLibraries) {
        	if(ctLibId.equals(ctl.getId())) {
        		Set<String> chIds = ctl.getEnabledChannelIds();
        		if(chIds != null) {
        			chIds.stream().forEach(i -> {
        				includedIn.put(i, header);
        			});
        		}
        		break;
        	}
        }

        List<ChannelSummary> lst = chController.getChannelSummary(includedIn, true);
		return lst;
	}
}
