package com.example.lorav4;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lorav4.utils.TransactionAdapter;
import com.example.lorav4.utils.TransactionManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class Transactions extends AppCompatActivity {

        private TransactionManager transactionManager;
        private EditText transactionNameEditText, transactionAmountEditText;
        private RecyclerView transactionRecyclerView;
        private TransactionAdapter transactionAdapter;

        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_transactions);

            transactionManager = new TransactionManager();
            transactionNameEditText = findViewById(R.id.transactionNameEditText);
            transactionAmountEditText = findViewById(R.id.transactionAmountEditText);
            transactionRecyclerView = findViewById(R.id.transactionRecyclerView);

            // Set up RecyclerView
            transactionAdapter = new TransactionAdapter(new ArrayList<>());
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            transactionRecyclerView.setLayoutManager(layoutManager);
            transactionRecyclerView.setAdapter(transactionAdapter);

            // Read data example
            readData();

            Button addTransactionButton = findViewById(R.id.addTransactionButton);
            addTransactionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Add data example
                    String name = transactionNameEditText.getText().toString();
                    double amount = Double.parseDouble(transactionAmountEditText.getText().toString());

                    // Get a reference to the "transactions" node in your database
                    DatabaseReference transactionsRef = FirebaseDatabase.getInstance().getReference().child("transactions");

                    // Push a new transaction with an auto-generated ID
                    DatabaseReference newTransactionRef = transactionsRef.push();

                    // Set the values for the new transaction
                    newTransactionRef.child("name").setValue(name);
                    newTransactionRef.child("amount").setValue(amount);

                    // You can also get the auto-generated ID
                    String transactionId = newTransactionRef.getKey();
                }
            });

            // ... (rest of your code)
        }

    private void readData() {
        transactionManager.readData(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<com.example.lorav4.utils.TransactionAdapter.Transaction> transactions = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    com.example.lorav4.utils.TransactionAdapter.Transaction transaction = dataSnapshot.getValue(com.example.lorav4.utils.TransactionAdapter.Transaction.class);
                    transactions.add(transaction);
                }
                transactionAdapter.setTransactions(transactions);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle read data error
            }
        });

        }
    }

