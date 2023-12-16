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

    private double selectedUserLatitude;
    private double selectedUserLongitude;

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
        editTextFirstName = findViewById(R.id.order_name);
        editTextLastName = findViewById(R.id.order_type);
        editTextMobileNumber = findViewById(R.id.order_count);
        editTextAddress = findViewById(R.id.order_amount);
        btnAddUpdateToFirebase = findViewById(R.id.btn_Add);

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
                    String userType = snapshot.child("userType").getValue(String.class);
                    String firstName = snapshot.child("firstName").getValue(String.class);
                    String lastName = snapshot.child("lastName").getValue(String.class);
                    String userId = snapshot.child("userId").getValue(String.class);
                    String displayName = firstName + " " + lastName;
                    Log.d("Firebase", "Populating spinner with user: " + displayName);

                    if (displayName != null && !isExcludedUser(userType)) {
                        mobileNumbers.add(displayName);
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
                        Log.d("Firebase", "Selected user ID from spinner: " + selectedSpinnerUserId);
                        String extractedUserId = getUserIdFromSelectedItem(selectedSpinnerUserId);
                        if (extractedUserId != null && !isExcludedUser(extractedUserId)) {
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
    private String getUserIdFromSelectedItem(String selectedItem) {
        // Split the selected item and extract the user ID
        String[] parts = selectedItem.split(" ");
        if (parts.length > 0) {
            return parts[parts.length - 1];
        }
        return null;
    }

    private boolean isExcludedUser(String userTypes) {
        // Add your logic here to determine if the user should be excluded
        // For example, if "Driver" or "Admin" user types should be excluded
        return userTypes != null && (userTypes.equals("Driver") || userTypes.equals("Admin"));
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
        editTextFirstName.setText(order.getOrder_name());
        editTextLastName.setText(order.getOrder_type());
        editTextMobileNumber.setText(order.getOrder_count());
        editTextAddress.setText(order.getOrder_amount());

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
        String order_name = editTextFirstName.getText().toString().trim();
        String order_type = editTextLastName.getText().toString().trim();
        String order_count = editTextMobileNumber.getText().toString().trim();
        String order_amount = editTextAddress.getText().toString().trim();

        // Validate input
        if (isValidInput(order_name, order_type, order_count, order_amount)) {
            // Check if the order already exists (based on some condition, e.g., mobile number)
            boolean orderExists = checkOrderExists(order_count);

            if (orderExists) {
                // Update existing order
                updateOrderInFirebase(order_name, order_type, order_count, order_amount);
            } else {
                // Add new order
                addOrderToFirebase(order_name, order_type, order_count, order_amount);
            }
        } else {
            // Show an error message or handle invalid input
            Toast.makeText(this, "Invalid input. Please fill in all fields.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValidInput(String order_name, String order_type, String order_count, String order_amount) {
        return !order_name.isEmpty() && !order_type.isEmpty() && !order_count.isEmpty() && !order_amount.isEmpty();
    }

    private boolean checkOrderExists(String order_count) {
        // Check if an order with the given mobile number already exists in the list
        for (Order order : orderList) {
            if (order_count != null && order_count.equals(order.getOrder_count())) {
                return true;
            }
        }
        return false;
    }


    private void addOrderToFirebase(String order_name, String order_type, String order_count, String order_amount) {

            // Retrieve data from the "LORA" table
            DatabaseReference loraTableRef = FirebaseDatabase.getInstance().getReference().child("LORA");
            loraTableRef.orderByChild("mobileNumber").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean userExists = false;
                    for (DataSnapshot loraData : snapshot.getChildren()) {
                        // Retrieve data from LORA
                        String userType = loraData.child("Admin").getValue(String.class);
                        String userType1 = loraData.child("Driver").getValue(String.class);

                        // Check if the user should be excluded
                        if (userType != null && userType1 != null && userType !="Admin" && userType1 !="Driver") {
                            // User is not excluded, proceed with retrieving other data
                            String firstName = loraData.child("firstName").getValue(String.class);
                            String lastName = loraData.child("lastName").getValue(String.class);
                            String mobileNumber = loraData.child("mobileNumber").getValue(String.class);

                            // Retrieve latitude and longitude
                            double latitude = loraData.child("latitude").getValue(Double.class);
                            double longitude = loraData.child("longitude").getValue(Double.class);

                            // Log the retrieved data
                            Log.d("Firebase", "Retrieved LORA Data - FirstName: " + firstName +
                                    ", LastName: " + lastName +
                                    ", MobileNumber: " + mobileNumber +
                                    ", Latitude: " + latitude +
                                    ", Longitude: " + longitude);

                            // Add a new order to the "ORDER" table with latitude and longitude
                            addOrderToFirebaseWithLocation(firstName, lastName, mobileNumber, order_name, order_type, order_count, order_amount, latitude, longitude);

                            userExists = true;
                            break;  // Exit the loop since we found the user
                        }
                    }

                    if (!userExists) {
                        // Handle the case where no data is found
                        Toast.makeText(Admin_transaction.this, "No data found in LORA for the selected user", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle errors
                    Toast.makeText(Admin_transaction.this, "Error retrieving data from LORA", Toast.LENGTH_SHORT).show();
                }
            });
    }



    private void addOrderToFirebaseWithLocation(String firstName, String lastName, String mobileNumber,
                                                String order_name, String order_type, String order_count,
                                                String order_amount, double latitude, double longitude) {
        // Create a reference to the new table ("AnotherTable")
        DatabaseReference anotherTableRef = FirebaseDatabase.getInstance().getReference("orders");

        // Add a new order to the "AnotherTable" with latitude and longitude
        String orderId = anotherTableRef.push().getKey();
        Order order = new Order(orderId, userId, firstName, lastName, mobileNumber, order_name, order_type, order_count, order_amount, latitude, longitude);
        anotherTableRef.child(orderId).setValue(order)
                .addOnSuccessListener(aVoid -> {
                    // Data added successfully to the "AnotherTable"
                    Toast.makeText(this, "Order added successfully to AnotherTable", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    Toast.makeText(this, "Error adding order to AnotherTable", Toast.LENGTH_SHORT).show();
                });
    }


    private void updateOrderInFirebase(String order_name, String order_type, String order_count, String order_amount) {
        // Find the existing order and update its fields
        for (Order order : orderList) {
            if (order.getOrder_count().equals(order_count)) {
                order.setOrder_name(order_name);
                order.setOrder_type(order_type);
                order.setOrder_amount(order_amount);

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