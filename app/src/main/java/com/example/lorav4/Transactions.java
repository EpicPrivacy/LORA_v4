package com.example.lorav4;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.GoogleMap;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Transactions extends AppCompatActivity implements OrderAdapter.OnItemClickListener{

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
            orderAdapter = new OrderAdapter(orderList, this);
            recyclerView.setAdapter(orderAdapter);

            // Load data from Firebase
            loadDataFromFirebase();

            // Initialize Firebase Database
            databaseReference = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid()).child("orders");
        }



    }

    private void showOptionsDialog(Order order) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Options")
                .setMessage("Select an option:")
                .setPositiveButton("Update", (dialog, which) -> {
                    // Handle update
                    showUpdateDialog(order);
                })
                .setNegativeButton("Delete", (dialog, which) -> {
                    // Handle delete
                    deleteOrderFromFirebase(order.getOrderId());
                })
                .setNeutralButton("Cancel", (dialog, which) -> {
                    // Do nothing, simply close the dialog
                })
                .show();
    }

    private void showUpdateDialog(Order order) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Order");

        // Inflate a layout containing EditTexts for user input
        View view = getLayoutInflater().inflate(R.layout.dialog_update_order, null);
        EditText editTextFirstName = view.findViewById(R.id.editTextUpdateFirstName);
        EditText editTextLastName = view.findViewById(R.id.editTextUpdateLastName);
        EditText editTextMobileNumber = view.findViewById(R.id.editTextUpdateMobileNumber);
        EditText editTextAddress = view.findViewById(R.id.editTextUpdateAddress);

        // Set initial values based on the existing order
        editTextFirstName.setText(order.getFirstName());
        editTextLastName.setText(order.getLastName());
        editTextMobileNumber.setText(order.getMobileNumber());
        editTextAddress.setText(order.getAddress());

        builder.setView(view);

    }

    private void deleteOrderFromFirebase(String orderId) {
        // Remove the order from the "orders" node
        databaseReference.child(orderId).removeValue()
                .addOnSuccessListener(aVoid -> {
                    // Data deleted successfully
                    // You might want to show a success message or take other actions
                    Toast.makeText(this, "Order deleted successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    // e.g., log the error or display an error message to the user
                    Toast.makeText(this, "Error deleting order from Firebase", Toast.LENGTH_SHORT).show();
                });
    }

    private void loadDataFromFirebase() {
        if (currentUser != null) {
            // Load data only for the current user's "orders" node
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    orderList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Order order = dataSnapshot.getValue(Order.class);
                        orderList.add(order);
                    }
                    orderAdapter.notifyDataSetChanged();

                    Log.d("Firebase", "Data loaded successfully. Order count: " + orderList.size());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle errors
                }
            });
        } else {
            Log.e("Firebase", "User not signed in. Unable to load data from Firebase.");
        }
    }

    private boolean isValidInput(String firstName, String lastName, String mobileNumber, String address) {
        return !firstName.isEmpty() && !lastName.isEmpty() && !mobileNumber.isEmpty() && !address.isEmpty();
    }

    private boolean checkOrderExists(String mobileNumber) {
        // Check if an order with the given mobile number already exists in the list
        for (Order order : orderList) {
            if (order.getMobileNumber().equals(mobileNumber)) {
                return true;
            }
        }
        return false;
    }



    @Override
    public void onMapReady(GoogleMap map) {

    }

    public void onItemClick(Order order) {
        // Handle item click, e.g., show a dialog for update/delete options
        showOptionsDialog(order);
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