package com.fitness.model;

public record Result<T>(boolean success, T data, String message) {
    public static <T> Result<T> ok(T data, String message) { return new Result<>(true, data, message); }
    public static <T> Result<T> error(String message) { return new Result<>(false, null, message); }
}