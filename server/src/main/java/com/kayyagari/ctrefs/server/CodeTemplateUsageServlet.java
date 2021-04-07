package com.kayyagari.ctrefs.server;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

import com.kayyagari.ctrefs.shared.CodeTemplateUsageServletInterface;
import com.mirth.connect.client.core.ClientException;
import com.mirth.connect.client.core.ControllerException;
import com.mirth.connect.model.Channel;
import com.mirth.connect.model.ChannelSummary;
import com.mirth.connect.server.api.MirthServlet;

/**
 * 
 * @author Kiran Ayyagari (kayyagari@apache.org)
 */
public class CodeTemplateUsageServlet extends MirthServlet implements CodeTemplateUsageServletInterface {

	private CodeTemplateUsageFinder finder;

	public CodeTemplateUsageServlet(@Context HttpServletRequest request, @Context SecurityContext sc) {
        super(request, sc, CodeTemplateUsageServletInterface.PLUGIN_NAME);
        finder = new CodeTemplateUsageFinder();
    }

	@Override
	public List<ChannelSummary> findUsageOfCodeTemplate(String ctId) throws ClientException {
		try {
			return finder.findUsagesOfCodeTemplate(ctId);
		}
		catch(ControllerException e) {
			throw new ClientException(e);
		}
	}

	@Override
	public List<ChannelSummary> findUsageOfCodeTemplateLib(String ctLibId) throws ClientException {
		try {
			return finder.findUsagesOfCodeTemplateLib(ctLibId);
		}
		catch(ControllerException e) {
			throw new ClientException(e);
		}
	}
}
