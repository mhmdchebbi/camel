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
package org.apache.camel.processor;

import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.file.GenericFile;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DeadLetterChannelUseOriginalInBodyWithFileTest extends ContextTestSupport {

    @Test
    public void testOriginalInBodyIsFile() throws Exception {
        MockEndpoint dead = getMockEndpoint("mock:dead");
        dead.expectedMessageCount(1);
        dead.message(0).body().isInstanceOf(GenericFile.class);
        dead.message(0).body(String.class).isEqualTo("Hello");

        template.sendBodyAndHeader(fileUri(), "Hello", Exchange.FILE_NAME, "hello.txt");

        assertMockEndpointsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            @Override
            public void configure() {
                errorHandler(deadLetterChannel("mock:dead").disableRedelivery().logStackTrace(false).useOriginalMessage());

                from(fileUri("?initialDelay=0&delay=10&noop=true")).transform(body().append(" World"))
                        .process(new MyThrowProcessor());
            }
        };
    }

    public static class MyThrowProcessor implements Processor {

        public MyThrowProcessor() {
        }

        @Override
        public void process(Exchange exchange) {
            assertEquals("Hello World", exchange.getIn().getBody(String.class));
            throw new IllegalArgumentException("Forced");
        }
    }

}
