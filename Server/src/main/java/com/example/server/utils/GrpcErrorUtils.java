package com.example.server.utils;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;

public class GrpcErrorUtils {
    private GrpcErrorUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static StatusRuntimeException internalError(Throwable cause) {
        return Status.INTERNAL.withDescription("Unexpected internal error").withCause(cause).asRuntimeException();
    }
}
