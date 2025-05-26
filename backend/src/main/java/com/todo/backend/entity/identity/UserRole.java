package com.todo.backend.entity.identity;

// be careful when editing existing roles in this.
//! can fuck up firebase auth and database shit.
public enum UserRole {
    USER, ADMIN, LIBRARIAN
}
