/*
 * Copyright © 2021 Apple Inc. and the ServiceTalk project authors
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
package io.servicetalk.grpc.api;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GrpcStatusTest {

    @Test
    void testGrpcStatusFromIntCodeValue() {
        final GrpcStatus grpcStatus = GrpcStatus.fromCodeValue(0);
        assertEquals(GrpcStatusCode.OK, grpcStatus.code());
    }

    @Test
    void testGrpcStatusFromStringCodeValue() {
        final GrpcStatus grpcStatus = GrpcStatus.fromCodeValue("0");
        assertEquals(GrpcStatusCode.OK, grpcStatus.code());
    }

    @Test
    void testGrpcStatusUnknownFromIntCodeValue() {
        int unknownCode = 255;
        final GrpcStatus grpcStatus = GrpcStatus.fromCodeValue(unknownCode);
        assertEquals(GrpcStatusCode.UNKNOWN, grpcStatus.code());
        assertEquals("Unknown code: " + unknownCode, grpcStatus.description());
    }

    @Test
    void testGrpcStatusUnknownFromStringCodeValue() {
        final String unknownCode = "255";
        final GrpcStatus grpcStatus = GrpcStatus.fromCodeValue(unknownCode);
        assertEquals(GrpcStatusCode.UNKNOWN, grpcStatus.code());
        assertEquals("Unknown code: " + unknownCode, grpcStatus.description());
    }

    @Test
    void testGrpcStatusUnknownFromUnparsableStringCodeValue() {
        final String unknownCode = "1f";
        final GrpcStatus grpcStatus = GrpcStatus.fromCodeValue(unknownCode);
        assertEquals(GrpcStatusCode.UNKNOWN, grpcStatus.code());
        assertEquals("Status code value not a number: " + unknownCode, grpcStatus.description());
    }

    @Test
    void testGrpcStatusExceptionFromGrpcStatusCode() {
        final String exceptionMessage = "denied!";
        GrpcStatusException grpcStatusException = GrpcStatusCode.PERMISSION_DENIED.withDescription(exceptionMessage).asException();
        Exception thrownGrpcStatusException = assertThrows(GrpcStatusException.class, () -> {
            throw grpcStatusException;
        });
        final String expectedExceptionMessage = exceptionMessage;
        final String actualMessage = thrownGrpcStatusException.getMessage();
        assertTrue(actualMessage.contains(expectedExceptionMessage));
    }
}
