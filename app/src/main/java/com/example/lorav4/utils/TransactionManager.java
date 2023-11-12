package com.example.lorav4.utils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

public class TransactionManager {

    private DatabaseReference databaseReference;

    public TransactionManager() {
        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("transactions");
    }

    // Read data
    public void readData(final ValueEventListener listener) {
        databaseReference.addValueEventListener(listener);
    }

    // Add data
    public void addTransaction(Transaction transaction) {
        String key = databaseReference.push().getKey();
        if (key != null) {
            databaseReference.child(key).setValue(transaction);
        }
    }

    // Update data
    public void updateTransaction(String transactionId, double newAmount) {
        databaseReference.child(transactionId).child("amount").setValue(newAmount);
    }

    // Delete data
    public void deleteTransaction(String transactionId) {
        databaseReference.child(transactionId).removeValue();
    }
}

