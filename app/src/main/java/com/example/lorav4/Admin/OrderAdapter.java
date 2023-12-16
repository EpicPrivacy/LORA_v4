package com.example.lorav4.Admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lorav4.R;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Order> orderList;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }
    public interface OnItemClickListener {
        void onItemClick(Order order);
    }


    public OrderAdapter(List<Order> orderList, OnItemClickListener listener) {
        this.orderList = orderList;
        this.onItemClickListener = listener;
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
        holder.bind(order);

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(order);
            }
        });

        holder.orderIdTextView.setText("Order ID: " + order.getOrderId());
        holder.orderIdTextView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.black));

        holder.firstNameTextView.setText("Order Name: " + order.getOrder_name());
        holder.firstNameTextView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.black));

        holder.lastNameTextView.setText("Order Type: " + order.getOrder_type());
        holder.lastNameTextView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.black));

        holder.mobileNumberTextView.setText("Order Count: " + order.getOrder_count());
        holder.mobileNumberTextView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.black));

        holder.addressTextView.setText("Amount: " + order.getOrder_amount());
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

        public void bind(final Order order) {
            orderIdTextView.setText("Order ID: " + order.getOrderId());
            firstNameTextView.setText("Order Name: " + order.getOrder_name());
            lastNameTextView.setText("Order Type: " + order.getOrder_type());
            mobileNumberTextView.setText("Order Count: " + order.getOrder_count());
            addressTextView.setText("Amount: " + order.getOrder_amount());
        }
    }


}
