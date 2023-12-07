package com.example.lorav4.Admin;// FirebaseDBHelper.java
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseDBHelper {

    private DatabaseReference databaseReference;

    public FirebaseDBHelper() {
        // Initialize the database reference with your specific URL and table
        databaseReference = FirebaseDatabase.getInstance().getReference().child("LORA");
    }

    // Add a method to retrieve data from the "LORA" table in the database
    public void getAllMobileNumbers(final DataCallback callback) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                callback.onDataReceived(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
            }
        });
    }

    // Callback interface to pass data to the calling activity/fragment
    public interface DataCallback {
        void onDataReceived(DataSnapshot dataSnapshot);
    }
}
