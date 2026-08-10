package com.shopsmart.exception;

public class DuplicateCategoryNameException extends RuntimeException {
    public DuplicateCategoryNameException(String name) {
        super("Category with name '" + name + "' already exists");
    }
}