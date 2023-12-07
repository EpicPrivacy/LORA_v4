package com.example.lorav4;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.GoogleMap;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Order> orderList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onMapReady(GoogleMap map);

        void onItemClick(Order order);
    }

    public OrderAdapter(List<Order> orderList, OnItemClickListener listener) {
        this.orderList = orderList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);

        // Add the divider to the bottom of each item
        View divider = new View(parent.getContext());
        divider.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        divider.setBackgroundResource(R.drawable.item_divider);
        ((LinearLayout) view).addView(divider);

        return new OrderViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.bind(order, listener);

        holder.orderIdTextView.setText("Order ID: " + order.getOrderId());
        holder.orderIdTextView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.black));

        holder.firstNameTextView.setText("First Name: " + order.getFirstName());
        holder.firstNameTextView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.black));

        holder.lastNameTextView.setText("Last Name: " + order.getLastName());
        holder.lastNameTextView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.black));

        holder.mobileNumberTextView.setText("Mobile Number: " + order.getMobileNumber());
        holder.mobileNumberTextView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.black));

        holder.addressTextView.setText("Address: " + order.getAddress());
        holder.addressTextView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.black));
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {

        private TextView orderIdTextView;
        private TextView firstNameTextView;
        private TextView lastNameTextView;
        private TextView mobileNumberTextView;
        private TextView addressTextView;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIdTextView = itemView.findViewById(R.id.textViewOrderId);
            firstNameTextView = itemView.findViewById(R.id.textViewFirstName);
            lastNameTextView = itemView.findViewById(R.id.textViewLastName);
            mobileNumberTextView = itemView.findViewById(R.id.textViewMobileNumber);
            addressTextView = itemView.findViewById(R.id.textViewAddress);
        }

        public void bind(final Order order, final OnItemClickListener listener) {
            orderIdTextView.setText("Order ID: " + order.getOrderId());
            firstNameTextView.setText("First Name: " + order.getFirstName());
            lastNameTextView.setText("Last Name: " + order.getLastName());
            mobileNumberTextView.setText("Mobile Number: " + order.getMobileNumber());
            addressTextView.setText("Address: " + order.getAddress());

            itemView.setOnClickListener(v -> listener.onItemClick(order));
        }
    }


}

