package com.example.lorav4;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lorav4.Admin.Order;
import com.example.lorav4.Admin.OrderAdapter;
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

        // Initialize Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("orders");

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();
        // Inside your Transaction activity or fragment
        currentUser = mAuth.getCurrentUser();


        m_number = getIntent().getStringExtra("m_number");

        if (m_number != null && m_number.length() > 3) {

            String formatNum = m_number.substring(3);


            m_number = "0" + formatNum;
        } else {

            Log.e("Error", "Invalid mobile number received from intent");

        }


        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        orderList = new ArrayList<>();
        orderAdapter = new OrderAdapter(orderList, null);
        recyclerView.setAdapter(orderAdapter);

        // Load data from Firebase
        loadDataFromFirebase();


    }

    private void loadDataFromFirebase() {
        if (currentUser != null) {
            String userId = currentUser.getUid();
            Query showTrans = databaseReference.orderByChild("userId").equalTo(userId);

            showTrans.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        orderList.clear(); // Clear the list before adding new data

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Order order = dataSnapshot.getValue(Order.class);
                            if (order != null) {
                                orderList.add(order);
                            }
                        }

                        orderAdapter.notifyDataSetChanged();

                        Log.d("Firebase", "Data loaded successfully. Order count: " + orderList.size());
                    } else {
                        Log.e("Firebase", "No Data Found");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle database error
                }
            });
        } else {
            Log.e("Firebase", "User not signed in. Unable to load data from Firebase.");
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        // Remove Firebase listeners here
    }

}