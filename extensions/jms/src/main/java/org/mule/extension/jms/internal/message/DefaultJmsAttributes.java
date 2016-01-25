/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.jms.internal.message;

import org.mule.extension.jms.api.message.JmsAttributes;
import org.mule.extension.jms.api.message.JmsHeaders;

import java.util.Map;

public class DefaultJmsAttributes implements JmsAttributes
{
    private Map<String, Object> properties;
    private JmsHeaders headers;

    public DefaultJmsAttributes(Map<String, Object> properties, JmsHeaders headers)
    {
        this.properties = properties;
        this.headers = headers;
    }

    public Map<String, Object> getProperties()
    {
        return properties;
    }

    public JmsHeaders getHeaders()
    {
        return headers;
    }

    @Override
    public void acknowlewdge()
    {

    }

}
