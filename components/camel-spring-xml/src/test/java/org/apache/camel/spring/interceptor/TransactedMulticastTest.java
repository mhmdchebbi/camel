/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.spring.interceptor;

import org.apache.camel.builder.RouteBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransactedMulticastTest extends TransactionClientDataSourceSupport {

    @Test
    @Timeout(value = 10)
    public void testMulticast() throws Exception {
        getMockEndpoint("mock:result").expectedMessageCount(1);
        getMockEndpoint("mock:test1").expectedMessageCount(1);
        getMockEndpoint("mock:test2").expectedMessageCount(1);

        String response = template.requestBody("direct:start", "Hello World", String.class);
        assertEquals("Hi !!!", response);

        assertMockEndpointsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start").routeId("route1")
                        .transacted()
                        .to("direct:route2")
                        .log("will never get here")
                        .to("mock:result")
                        .setBody(constant("Hi !!!"));

                from("direct:route2").routeId("route2")
                        .multicast()
                        .to("mock:test1")
                        .to("mock:test2");
            }
        };
    }
}
