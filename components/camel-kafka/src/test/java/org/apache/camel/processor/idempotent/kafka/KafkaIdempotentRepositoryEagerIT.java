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
package org.apache.camel.processor.idempotent.kafka;

import java.util.UUID;

import org.apache.camel.BindToRegistry;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.kafka.integration.common.KafkaTestUtil;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test for eager idempotentRepository usage.
 */
public class KafkaIdempotentRepositoryEagerIT extends SimpleIdempotentTest {

    private static final String REPOSITORY_TOPIC = "TEST_EAGER_" + UUID.randomUUID();

    @BeforeAll
    public static void createRepositoryTopic() {
        KafkaTestUtil.createTopic(service, REPOSITORY_TOPIC, 1);
    }

    @BindToRegistry("kafkaIdempotentRepositoryEager")
    private final KafkaIdempotentRepository idempotentRepository
            = new KafkaIdempotentRepository(REPOSITORY_TOPIC, service.getBootstrapServers());

    @Override
    protected RouteBuilder createRouteBuilder() {
        // Every instance of the repository must use a different topic to guarantee isolation between tests

        return new RouteBuilder() {
            @Override
            public void configure() {
                from("direct:in").to("mock:before").idempotentConsumer(header("id"))
                        .idempotentRepository("kafkaIdempotentRepositoryEager").to("mock:out").end();
            }
        };
    }

    @Test
    public void testRemovesDuplicates() {
        ProducerTemplate template = contextExtension.getProducerTemplate();

        for (int i = 0; i < 10; i++) {
            template.sendBodyAndHeader("direct:in", "Test message", "id", i % 5);
        }

        MockEndpoint mockOut = contextExtension.getMockEndpoint("mock:out");
        assertEquals(5, mockOut.getReceivedCounter());

        MockEndpoint mockBefore = contextExtension.getMockEndpoint("mock:before");
        assertEquals(10, mockBefore.getReceivedCounter());
    }

    @Test
    public void testRollsBackOnException() {
        MockEndpoint mockOut = contextExtension.getMockEndpoint("mock:out");
        mockOut.whenAnyExchangeReceived(exchange -> {
            int id = exchange.getIn().getHeader("id", Integer.class);
            if (id == 0) {
                throw new IllegalArgumentException("Boom!");
            }
        });

        ProducerTemplate template = contextExtension.getProducerTemplate();
        for (int i = 0; i < 10; i++) {
            try {
                template.sendBodyAndHeader("direct:in", "Test message", "id", i % 5);
            } catch (CamelExecutionException cex) {
                // no-op; expected
            }
        }

        assertEquals(5, mockOut.getReceivedCounter(),
                "Only the 5 messages from the previous test should have been received ");
        MockEndpoint mockBefore = contextExtension.getMockEndpoint("mock:before");
        assertEquals(20, mockBefore.getReceivedCounter(),
                "Test should have received 20 messages in total from all the tests");
    }

}
