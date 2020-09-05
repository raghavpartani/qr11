package com.example.qrapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomAdapter
        extends RecyclerView.Adapter<CustomAdapter.MyHolder> {
    Context context;
    ArrayList<String> qr;

    public CustomAdapter(Context context, ArrayList<String> qr) {
        this.context = context;
        this.qr = qr;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(
            @NonNull ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(context).inflate(R.layout.mylayout, viewGroup, false);

        MyHolder holder = new MyHolder(v);

        return holder;
    }

    @Override
    public void onBindViewHolder(
            @NonNull MyHolder myHolder, int i) {

        myHolder.tv.setText(qr.get(i));
    }

    @Override
    public int getItemCount() {
        return qr.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {
        TextView tv;
        ImageView iv;

        public MyHolder(@NonNull View v) {
            super(v);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String s = "https://qrphp.000webhostapp.com/" + tv.getText().toString() + ".png";
                    Intent intent = new Intent(context, Suggestion.class);
                    intent.putExtra("key", "server");
                    intent.putExtra("url", s);
                    context.startActivity(intent);
                    //         Toast.makeText(context, ""+tv.getText().toString(), Toast.LENGTH_SHORT).show();
                }
            });
            tv = v.findViewById(R.id.qr);

        }

    }
}
