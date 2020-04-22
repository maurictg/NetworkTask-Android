package com.maurict.networktask;

public enum RequestMethod {
    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DELETE");

    public String method;
    RequestMethod(String method) { this.method = method; }
}
