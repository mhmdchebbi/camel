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
package org.apache.camel.component.file;

import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FileToFileWithFlattenTest extends ContextTestSupport {

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
        template.sendBodyAndHeader(fileUri("flatten-in"), "Bye World", Exchange.FILE_NAME, "bye.txt");
        template.sendBodyAndHeader(fileUri("flatten-in"), "Hello World", Exchange.FILE_NAME, "sub/hello.txt");
        template.sendBodyAndHeader(fileUri("flatten-in"), "Goodday World", Exchange.FILE_NAME, "sub/sub2/goodday.txt");
    }

    @Override
    @AfterEach
    public void tearDown() throws Exception {
        context.stop();
        super.tearDown();
    }

    @Test
    public void testFlatternConsumer() throws Exception {
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() {
                from(fileUri("flatten-in?initialDelay=0&delay=10&recursive=true&flatten=true"))
                        .to(fileUri("flatten-out"), "mock:result");
            }
        });
        context.start();

        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(3);

        // flatten files
        mock.expectedFileExists(testFile("flatten-out/bye.txt"));
        mock.expectedFileExists(testFile("flatten-out/hello.txt"));
        mock.expectedFileExists(testFile("flatten-out/goodday.txt"));

        // default move files
        mock.expectedFileExists(testFile("flatten-in/.camel/bye.txt"));
        mock.expectedFileExists(testFile("flatten-in/sub/.camel/hello.txt"));
        mock.expectedFileExists(testFile("flatten-in/sub/sub2/.camel/goodday.txt"));

        assertMockEndpointsSatisfied();
    }

    @Test
    public void testFlatternProducer() throws Exception {
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() {
                from(fileUri("flatten-in?initialDelay=0&delay=10&recursive=true"))
                        .to(fileUri("flatten-out?flatten=true"), "mock:result");
            }
        });
        context.start();

        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(3);

        // flatten files
        mock.expectedFileExists(testFile("flatten-out/bye.txt"));
        mock.expectedFileExists(testFile("flatten-out/hello.txt"));
        mock.expectedFileExists(testFile("flatten-out/goodday.txt"));

        // default move files
        mock.expectedFileExists(testFile("flatten-in/.camel/bye.txt"));
        mock.expectedFileExists(testFile("flatten-in/sub/.camel/hello.txt"));
        mock.expectedFileExists(testFile("flatten-in/sub/sub2/.camel/goodday.txt"));

        assertMockEndpointsSatisfied();
    }

}
