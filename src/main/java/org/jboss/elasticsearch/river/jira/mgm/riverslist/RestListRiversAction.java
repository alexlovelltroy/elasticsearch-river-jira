/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 */
package org.jboss.elasticsearch.river.jira.mgm.riverslist;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.rest.BytesRestResponse;
import org.elasticsearch.rest.RestChannel;
import org.elasticsearch.rest.RestController;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.rest.RestStatus;
import org.jboss.elasticsearch.river.jira.mgm.RestJRMgmBaseAction;

/**
 * REST action handler for Jira river get state operation.
 * 
 * @author Vlastimil Elias (velias at redhat dot com)
 */
public class RestListRiversAction extends RestJRMgmBaseAction {

	@Inject
	protected RestListRiversAction(Settings settings, Client client, RestController controller) {
		super(settings, controller, client);
		controller.registerHandler(org.elasticsearch.rest.RestRequest.Method.GET, "/_jira_river/list", this);
	}

	@Override
	public void handleRequest(final RestRequest restRequest, final RestChannel restChannel, Client client) {

		ListRiversRequest actionRequest = new ListRiversRequest();

		logger.debug("Go to look for jira rivers in cluster");
		client.admin().cluster()
				.execute(ListRiversAction.INSTANCE, actionRequest, new ActionListener<ListRiversResponse>() {

					@Override
					public void onResponse(ListRiversResponse response) {
						try {
							Set<String> allRivers = new HashSet<String>();
							for (NodeListRiversResponse node : response.getNodes()) {
								if (node.jiraRiverNames != null) {
									allRivers.addAll(node.jiraRiverNames);
								}
							}

							XContentBuilder builder = restChannel.newBuilder();
							builder.startObject();
							builder.field("jira_river_names", allRivers);
							builder.endObject();
							restChannel.sendResponse(new BytesRestResponse(RestStatus.OK, builder));
						} catch (Exception e) {
							onFailure(e);
						}
					}

					@Override
					public void onFailure(Throwable e) {
						try {
							restChannel.sendResponse(new BytesRestResponse(restChannel, e));
						} catch (IOException e1) {
							logger.error("Failed to send failure response", e1);
						}
					}

				});
	}
}
