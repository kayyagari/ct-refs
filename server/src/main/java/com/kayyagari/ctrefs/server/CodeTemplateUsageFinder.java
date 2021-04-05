package com.kayyagari.ctrefs.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.mirth.connect.model.Channel;
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

	public List<Channel> findUsagesOfCodeTemplate(String ctId) {
		return findUsagesOf(ctId, false);
	}

	public List<Channel> findUsagesOfCodeTemplateLib(String ctLibId) {
		return findUsagesOf(ctLibId, true);
	}

	private List<Channel> findUsagesOf(String id, boolean lib) {
		List<Channel> includedIn = new ArrayList<>();
		Set<String> cids = chController.getChannelIds();
		List<Channel> channels = chController.getChannels(cids);
		for(Channel ch : channels) {
			List<CodeTemplateLibrary> ctLibs = ch.getExportData().getCodeTemplateLibraries();
			if(ctLibs != null) {
			outer:
				for(CodeTemplateLibrary ctl : ctLibs) {
					if(lib) {
						if(id.equals(ctl.getId())) {
							includedIn.add(ch);
							break;
						}
					}
					else {
						for(CodeTemplate ct : ctl.getCodeTemplates()) {
							if(id.equals(ct.getId())) {
								includedIn.add(ch);
								break outer;
							}
						}
					}
				}
			}
		}
		
		return includedIn;
	}
}
