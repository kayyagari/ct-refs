package com.kayyagari.ctrefs.server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mirth.connect.client.core.ControllerException;
import com.mirth.connect.model.Channel;
import com.mirth.connect.model.ChannelHeader;
import com.mirth.connect.model.ChannelSummary;
import com.mirth.connect.model.codetemplates.CodeTemplate;
import com.mirth.connect.model.codetemplates.CodeTemplateLibrary;
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

	public CodeTemplateUsageFinder() {
		chController = ControllerFactory.getFactory().createChannelController();
		ctController = ControllerFactory.getFactory().createCodeTemplateController();
	}

	public List<ChannelSummary> findUsagesOfCodeTemplate(String ctId) throws ControllerException {
		return findUsagesOf(ctId, false);
	}

	public List<ChannelSummary> findUsagesOfCodeTemplateLib(String ctLibId) throws ControllerException {
		return findUsagesOf(ctLibId, true);
	}

	private List<ChannelSummary> findUsagesOf(String id, boolean lib) throws ControllerException {
		Map<String, ChannelHeader> includedIn = new HashMap<>();
		//Set<String> cids = chController.getChannelIds();
		//List<Channel> channels = chController.getChannels(cids);
		ChannelHeader header = new ChannelHeader(0, null, false);
		
        List<CodeTemplateLibrary> codeTemplateLibraries = ctController.getLibraries(null, true);

        for(CodeTemplateLibrary ctl : codeTemplateLibraries) {
        	if(id.equals(ctl.getId())) {
        		Set<String> chIds = ctl.getEnabledChannelIds();
        		if(chIds != null) {
        			chIds.stream().forEach(i -> {
        				includedIn.put(i, header);
        			});
        		}
        		break;
        	}
        }

		return chController.getChannelSummary(includedIn, true);
	}
}
