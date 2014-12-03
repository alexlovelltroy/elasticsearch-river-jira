/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 */
package org.jboss.elasticsearch.river.jira.mgm.incrementalupdate;

import junit.framework.Assert;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.client.ClusterAdminClient;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit test for {@link IncrementalUpdateRequestBuilder}
 * 
 * @author Vlastimil Elias (velias at redhat dot com)
 */
public class IncrementalUpdateRequestBuilderTest {

	@Test
	public void test() {

		ClusterAdminClient client = Mockito.mock(ClusterAdminClient.class);

		{
			IncrementalUpdateRequestBuilder tested = new IncrementalUpdateRequestBuilder(client);
			Assert.assertNull(tested.request().getRiverName());
			Assert.assertNull(tested.request().getProjectKey());

			try {
				tested.doExecute(null);
				Assert.fail("IllegalArgumentException must be thrown");
			} catch (IllegalArgumentException e) {
				// OK
			}

			Assert.assertEquals(tested, tested.setRiverName("my river"));
			Assert.assertEquals("my river", tested.request().getRiverName());
			Assert.assertNull(tested.request().getProjectKey());
			tested.doExecute(null);
		}

		{
			IncrementalUpdateRequestBuilder tested = new IncrementalUpdateRequestBuilder(client);
			Assert.assertEquals(tested, tested.setProjectKey("ORG"));
			Assert.assertNull(tested.request().getRiverName());
			Assert.assertEquals("ORG", tested.request().getProjectKey());
			try {
				tested.doExecute(null);
				Assert.fail("IllegalArgumentException must be thrown");
			} catch (IllegalArgumentException e) {
				// OK
			}

			Assert.assertEquals(tested, tested.setRiverName("my river"));
			Assert.assertEquals("my river", tested.request().getRiverName());
			Assert.assertEquals("ORG", tested.request().getProjectKey());

			ActionListener<IncrementalUpdateResponse> al = new ActionListener<IncrementalUpdateResponse>() {

				@Override
				public void onResponse(IncrementalUpdateResponse response) {
				}

				@Override
				public void onFailure(Throwable e) {
				}
			};
			tested.doExecute(al);
			Mockito.verify(client).execute(IncrementalUpdateAction.INSTANCE, tested.request(), al);

		}
	}

}
