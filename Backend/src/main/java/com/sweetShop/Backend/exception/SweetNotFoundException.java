package com.sweetShop.Backend.exception;

public class SweetNotFoundException extends RuntimeException {

    public SweetNotFoundException(String message) {
        super(message);
    }
}
