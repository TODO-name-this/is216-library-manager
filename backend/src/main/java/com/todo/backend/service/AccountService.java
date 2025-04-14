package com.todo.backend.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import org.springframework.stereotype.Service;

@Service
public class AccountService {
    private static final String DUPLICATE_ACCOUNT_ERROR = "EMAIL_EXISTS";
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    // throw if account existed
    public void create(String email, String password) throws Exception {
        var request = new UserRecord.CreateRequest()
                .setEmail(email)
                .setPassword(password);

        try {
            firebaseAuth.createUser(request);
        } catch (FirebaseAuthException exception) {
            if (exception.getMessage().contains(DUPLICATE_ACCOUNT_ERROR)) {
                throw new Exception("Account with given email-id already exists");
            }
            throw exception;
        }
    }

    public void verifyEmail(String uid) throws FirebaseAuthException {
        var request = new UserRecord.UpdateRequest(uid)
                .setEmailVerified(true);
        firebaseAuth.updateUser(request);
    }
}
