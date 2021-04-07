package com.kayyagari.ctrefs.shared;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.mirth.connect.client.core.ClientException;
import com.mirth.connect.client.core.Operation.ExecuteType;
import com.mirth.connect.client.core.Permissions;
import com.mirth.connect.client.core.api.BaseServletInterface;
import com.mirth.connect.client.core.api.MirthOperation;
import com.mirth.connect.client.core.api.Param;
import com.mirth.connect.model.ChannelSummary;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 
 * @author Kiran Ayyagari (kayyagari@apache.org)
 */
@Path("/extensions/ct-refs")
@Tag(name = "CodeTemplate References Extension")
@Consumes(MediaType.APPLICATION_XML)
@Produces(MediaType.APPLICATION_XML)
public interface CodeTemplateUsageServletInterface extends BaseServletInterface {
	public static final String PLUGIN_NAME = "CT Refs Plugin";

	@GET
    @Path("/usageOfCodeTemplate")
    @Operation(summary = "Returns a List of all ChannelSummary objects that depend on the CodeTemplate identified by the given id")
    @MirthOperation(name = "findUsageOfCodeTemplate", display = "Find all the usages of the given CodeTemplate", permission = Permissions.CHANNELS_VIEW, type = ExecuteType.ASYNC, auditable = false)
    public List<ChannelSummary> findUsageOfCodeTemplate(@Param("ctId") @Parameter(description = "Identifier of the CodeTemplate", required = true) @QueryParam("ctId") String ctId) throws ClientException;

    @GET
    @Path("/usageOfCodeTemplateLib")
    @Operation(summary = "Returns a List of all ChannelSummary objects that depend on the CodeTemplateLibrary identified by the given id")
    @MirthOperation(name = "findUsageOfCodeTemplateLib", display = "Find all the usages of the given CodeTemplateLibrary", permission = Permissions.CHANNELS_VIEW, type = ExecuteType.ASYNC, auditable = false)
    public List<ChannelSummary> findUsageOfCodeTemplateLib(@Param("ctLibId") @Parameter(description = "Identifier of the CodeTemplateLibrary", required = true) @QueryParam("ctLibId") String ctLibId) throws ClientException;
}
