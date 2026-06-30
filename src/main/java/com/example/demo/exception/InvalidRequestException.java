package com.example.demo.exception;

public class InvalidRequestException extends RuntimeException {

    public InvalidRequestException(String message) {
        super(message);
        
        //RunTimeException ile extends etmemizin nedeni Exceptiondan ederse throws ile belirtirken kod kalabalıklaşırr 
        //bundan kurtulmak için RunTimeException dan extends edilir
    }
}