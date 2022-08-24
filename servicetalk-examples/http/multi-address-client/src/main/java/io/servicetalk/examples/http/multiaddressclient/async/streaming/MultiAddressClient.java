/*
 * Copyright © 2022 Apple Inc. and the ServiceTalk project authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.servicetalk.examples.http.multiaddressclient.async.streaming;

import io.servicetalk.http.api.MultiAddressHttpClientBuilder;
import io.servicetalk.http.api.SingleAddressHttpClientBuilder;
import io.servicetalk.http.api.StreamingHttpClient;
import io.servicetalk.http.netty.HttpClients;
import io.servicetalk.transport.api.ClientSslConfigBuilder;
import io.servicetalk.transport.api.HostAndPort;

import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.function.Supplier;

import static io.servicetalk.http.api.HttpSerializers.appSerializerUtf8FixLen;

public final class MultiAddressClient {

    private static final String SCHEME = "http";

    public static void main(String[] args) throws Exception {

        final MultiAddressHttpClientBuilder.SingleAddressInitializer<HostAndPort, InetSocketAddress> initializer = (scheme, address, clientBuilder) -> {
            clientBuilder.sslConfig(new ClientSslConfigBuilder().build());
            // ... more configurations such as logging filter only for specific host and port
        };

        Supplier<StreamingHttpClient> clientBuilder;

        SingleAddressHttpClientBuilder<HostAndPort, InetSocketAddress> singleClientBuilder =
                HttpClients.forSingleAddress(HostAndPort.of("localhost", 8080));
        initializer.initialize(SCHEME, HostAndPort.of("localhost", 8080), singleClientBuilder);
        clientBuilder = singleClientBuilder::buildStreaming;

        try (StreamingHttpClient client = Objects.requireNonNull(clientBuilder.get())) {
            client.request(client.get("http://localhost:8080/sayHello"))
                    .beforeOnSuccess(response -> System.out.println(response.toString((name, value) -> value)))
                    .flatMapPublisher(resp -> resp.payloadBody(appSerializerUtf8FixLen()))
                    .whenOnNext(System.out::println)
                    // This example is demonstrating asynchronous execution, but needs to prevent the main thread from exiting
                    // before the response has been processed. This isn't typical usage for an asynchronous API but is useful
                    // for demonstration purposes.
                    .ignoreElements().toFuture().get();
        }
    }
}
