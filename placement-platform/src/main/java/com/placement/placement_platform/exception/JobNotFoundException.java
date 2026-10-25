package com.placement.placement_platform.exception;

public class JobNotFoundException extends RuntimeException{
    public JobNotFoundException(String message){
        super(message);
    }
}
