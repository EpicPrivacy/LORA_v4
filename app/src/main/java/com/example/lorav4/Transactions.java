package com.example.lorav4;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Transactions extends AppCompatActivity{

    String m_number;
    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    private List<Order> orderList;
    private DatabaseReference databaseReference;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Check if the user is signed in
        currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // User not signed in, you might want to redirect to the sign-in activity or take appropriate action
            Log.e("Firebase", "User not signed in.");
            // Handle this situation according to your app's logic
        } else {
            // User is signed in, continue with the rest of your initialization
            // Initialize RecyclerView
            recyclerView = findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            orderList = new ArrayList<>();
            recyclerView.setAdapter(orderAdapter);

            // Load data from Firebase
            loadDataFromFirebase();

            // Initialize Firebase Database
            databaseReference = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid()).child("orders");
        }

    }

    private void loadDataFromFirebase() {
        Query showTrans = databaseReference.orderByChild("mobileNumber").equalTo(m_number);

        showTrans.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        String transac = userSnapshot.child("mobileNumber").getValue(String.class);

                        if (transac.equals(m_number)) {
                            orderList.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Order order = dataSnapshot.getValue(Order.class);
                                orderList.add(order);
                            }
                            orderAdapter.notifyDataSetChanged();

                            Log.d("Firebase", "Data loaded successfully. Order count: " + orderList.size());

                            return; // Exit the method after successful login
                        }
                    }
                } else {
                    Log.e("Firebase", "No Data Found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
            }
        });
    }
    @Override
    protected void onStop() {
        super.onStop();
        // Remove Firebase listeners here
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}