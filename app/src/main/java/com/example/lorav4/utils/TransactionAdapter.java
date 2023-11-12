package com.example.lorav4.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lorav4.R;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>{
    private List<Transaction> transactions;

    public TransactionAdapter(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.activity_transactions, parent, false);
        return new TransactionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);
        holder.bind(transaction);
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView amountTextView;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.transactionNameTextView);
            amountTextView = itemView.findViewById(R.id.transactionAmountTextView);
        }

        public void bind(Transaction transaction) {
            nameTextView.setText(transaction.getDescription());
            amountTextView.setText(String.valueOf(transaction.getAmount()));
        }
    }

    public static class Transaction {
        private String description;
        private double amount;

        // Constructors, setters, and other methods...

        // Getter for description
        public String getDescription() {
            return description;
        }

        // Getter for amount
        public double getAmount() {
            return amount;
        }
    }
}
