package com.example.lorav4.Admin;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lorav4.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Admin_transaction extends AppCompatActivity implements OrderAdapter.OnItemClickListener {

    private FirebaseDBHelper firebaseDBHelper;
    private Spinner spinnerUsers;
    private String selectedSpinnerUserId;

    private String m_number;

    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    private List<Order> orderList;
    private DatabaseReference databaseReference;
    private EditText editTextFirstName, editTextLastName, editTextMobileNumber, editTextAddress;
    private Button btnAddUpdateToFirebase;

    // Firebase authentication
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_transaction);

        // Initialize Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("orders");

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();
        // Inside your Transaction activity or fragment
        currentUser = mAuth.getCurrentUser();

        userId = currentUser != null ? currentUser.getUid() : null;

        // Initialize UI components
        editTextFirstName = findViewById(R.id.editTextFirstName);
        editTextLastName = findViewById(R.id.editTextLastName);
        editTextMobileNumber = findViewById(R.id.editTextMobileNumber);
        editTextAddress = findViewById(R.id.editTextAddress);
        btnAddUpdateToFirebase = findViewById(R.id.btnAddUpdateToFirebase);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderList = new ArrayList<>();
        orderAdapter = new OrderAdapter(orderList, this);
        recyclerView.setAdapter(orderAdapter);


        // Initialize FirebaseDBHelper
        firebaseDBHelper = new FirebaseDBHelper();

        // Initialize Spinner
        spinnerUsers = findViewById(R.id.spinnerUsers);

        // Populate spinner with data from Firebase
        populateSpinner();

        // Set onClickListener for the add/update button
        btnAddUpdateToFirebase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addOrUpdateToFirebase();
            }
        });
        Button btnClearFields = findViewById(R.id.btnClearFields);

        btnClearFields.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear input fields
                editTextFirstName.setText("");
                editTextLastName.setText("");
                editTextMobileNumber.setText("");
                editTextAddress.setText("");
            }
        });


        // Load data from Firebase
        loadDataFromFirebase();
    }

    private void populateSpinner() {

        firebaseDBHelper.getAllMobileNumbers(new FirebaseDBHelper.DataCallback() {
            @Override
            public void onDataReceived(DataSnapshot dataSnapshot) {
                // Handle the data snapshot and populate the spinner
                Iterable<DataSnapshot> data = dataSnapshot.getChildren();
                List<String> mobileNumbers = new ArrayList<>();

                for (DataSnapshot snapshot : data) {
                    // Assuming "mobile_number" is the key in your database
                    String firstName = snapshot.child("firstName").getValue(String.class);
                    String lastName = snapshot.child("lastName").getValue(String.class);
                    String mobileNumber = snapshot.child("firstName").getValue(String.class) + " " + snapshot.child("lastName").getValue(String.class);

                    if (mobileNumber != null && !(firstName.equals("Super") && lastName.equals("Admin"))) {
                        mobileNumbers.add(mobileNumber);
                    }
                }

                // Create an ArrayAdapter and set it to the spinner
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        Admin_transaction.this,
                        android.R.layout.simple_spinner_item,
                        mobileNumbers
                );

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerUsers.setAdapter(adapter);

                // Set a listener to track the selected user ID when an item is selected
                spinnerUsers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                        // Get the selected user ID directly from the spinner item
                        selectedSpinnerUserId = (String) parentView.getSelectedItem();
                        if (selectedSpinnerUserId != null && selectedSpinnerUserId.equals("09000000000")) {
                            // Handle the case where the selected user ID is a specific value
                            // You can update this condition based on your logic
                        } else {
                            // Continue with your logic
                            loadDataFromFirebase();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                        // Do nothing here
                    }
                });

            }
        });
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

        builder.setPositiveButton("Update", (dialog, which) -> {
            // Get updated values from EditTexts
            String updatedFirstName = editTextFirstName.getText().toString().trim();
            String updatedLastName = editTextLastName.getText().toString().trim();
            String updatedMobileNumber = editTextMobileNumber.getText().toString().trim();
            String updatedAddress = editTextAddress.getText().toString().trim();

            // Validate input
            if (isValidInput(updatedFirstName, updatedLastName, updatedMobileNumber, updatedAddress)) {
                // Update the order in Firebase
                updateOrderInFirebase(updatedFirstName, updatedLastName, updatedMobileNumber, updatedAddress);
            } else {
                // Show an error message or handle invalid input
                Toast.makeText(this, "Invalid input. Please fill in all fields.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            // Do nothing, simply close the dialog
        });

        builder.show();
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
        databaseReference.orderByKey().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Order order = dataSnapshot.getValue(Order.class);
                    if (order != null) {
                        orderList.add(order);
                    }
                }
                orderAdapter.notifyDataSetChanged();

                Log.d("Firebase", "Data loaded successfully. Order count: " + orderList.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors
            }
        });
    }


    private void addOrUpdateToFirebase() {
        // Get input values
        String firstName = editTextFirstName.getText().toString().trim();
        String lastName = editTextLastName.getText().toString().trim();
        String mobileNumber = editTextMobileNumber.getText().toString().trim();
        String address = editTextAddress.getText().toString().trim();

        // Validate input
        if (isValidInput(firstName, lastName, mobileNumber, address)) {
            // Check if the order already exists (based on some condition, e.g., mobile number)
            boolean orderExists = checkOrderExists(mobileNumber);

            if (orderExists) {
                // Update existing order
                updateOrderInFirebase(firstName, lastName, mobileNumber, address);
            } else {
                // Add new order
                addOrderToFirebase(firstName, lastName, mobileNumber, address);
            }
        } else {
            // Show an error message or handle invalid input
            Toast.makeText(this, "Invalid input. Please fill in all fields.", Toast.LENGTH_SHORT).show();
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

    private void addOrderToFirebase(String firstName, String lastName, String mobileNumber, String address) {
        // Check if the order already exists based on the address
        boolean orderExists = checkOrderExists(address);

        if (!orderExists) {
            // The order does not exist, add a new order
            String orderId = databaseReference.push().getKey();
            Order order = new Order(orderId, userId, firstName, lastName, mobileNumber, address);
            databaseReference.child(orderId).setValue(order)
                    .addOnSuccessListener(aVoid -> {
                        // Data added successfully
                        // You might want to show a success message or take other actions
                        Toast.makeText(this, "Order added successfully", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        // Handle errors
                        // e.g., log the error or display an error message to the user
                        Toast.makeText(this, "Error adding order to Firebase", Toast.LENGTH_SHORT).show();
                    });
        } else {
            // Show a message that the order already exists
            Toast.makeText(this, "Order with the given address already exists", Toast.LENGTH_SHORT).show();
        }
    }


    private void updateOrderInFirebase(String firstName, String lastName, String mobileNumber, String address) {
        // Find the existing order and update its fields
        for (Order order : orderList) {
            if (order.getMobileNumber().equals(mobileNumber)) {
                order.setFirstName(firstName);
                order.setLastName(lastName);
                order.setAddress(address);

                order.setUserId(userId);  // Set the user ID
                databaseReference.child(order.getOrderId()).setValue(order)
                        .addOnSuccessListener(aVoid -> {
                            // Data updated successfully
                            // You might want to show a success message or take other actions
                            Toast.makeText(this, "Order updated successfully", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            // Handle errors
                            // e.g., log the error or display an error message to the user
                            Toast.makeText(this, "Error updating order in Firebase", Toast.LENGTH_SHORT).show();
                        });
                break;
            }
        }
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


}
