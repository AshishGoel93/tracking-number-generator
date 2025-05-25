package com.assignment.tracking.exception;

public class TrackingServiceException extends RuntimeException {
    public TrackingServiceException(String message) {
        super(message);
    }
    public TrackingServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}