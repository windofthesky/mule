/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.transport.http;

import org.mule.DefaultMuleMessage;
import org.mule.api.MuleException;
import org.mule.api.config.MuleProperties;
import org.mule.api.endpoint.InboundEndpoint;
import org.mule.module.client.MuleClient;
import org.mule.tck.DynamicPortTestCase;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import edu.emory.mathcs.backport.java.util.concurrent.CountDownLatch;
import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;

public class DispatchTestCase extends DynamicPortTestCase
{
    public void testEchoService() throws Exception
    {
        final int THREADS = 10;
        final CountDownLatch latch = new CountDownLatch(THREADS);

        final MuleClient client = new MuleClient(muleContext);
        
        final byte[] buf = new byte[8192];
        Arrays.fill(buf, (byte) 'a');
        
        client.send(((InboundEndpoint) client.getMuleContext().getRegistry().lookupObject("inEchoService")).getAddress(),
            new DefaultMuleMessage(new ByteArrayInputStream(buf), muleContext));

        for (int i = 0; i < THREADS; i++)
        {
            new Thread(new Runnable() 
            {
                public void run()
                {
                    Map<String, Object> props = new HashMap<String, Object>();
                    props.put(MuleProperties.MULE_REPLY_TO_PROPERTY, "vm://queue");
                    try
                    {
                        for (int i = 0; i < 20; i++) 
                        {
                            client.dispatch(((InboundEndpoint) client.getMuleContext().getRegistry().lookupObject("inEchoService")).getAddress(), 
                                new DefaultMuleMessage(buf, muleContext), props);
                        }

                    }
                    catch (MuleException e)
                    {
                        e.printStackTrace();
                    }
                    finally
                    {
                        latch.countDown();
                    }
                }
                
            }).start();
        }

        //wait for somewhere close to 15 seconds before the test times out
        latch.await(40, TimeUnit.SECONDS);

        int count = 0;
        while (client.request("vm://queue", RECEIVE_TIMEOUT) != null)
        {
            count++;
        }
        
        assertEquals(200, count);
    }
    
    @Override
    protected String getConfigResources()
    {
        return "dispatch-conf.xml";
    }

    @Override
    protected int getNumPortsToFind()
    {
        return 1;
    }

}
