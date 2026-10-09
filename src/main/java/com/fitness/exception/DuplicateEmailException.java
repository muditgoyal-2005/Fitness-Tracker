package com.fitness.exception;

public class DuplicateEmailException extends Exception {
    public DuplicateEmailException() { super("An account with this email already exists."); }
}