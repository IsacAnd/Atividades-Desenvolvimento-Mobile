package com.example.contatos;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HolderView extends RecyclerView.ViewHolder {

    android.widget.ImageView imgView;
    TextView nameView, numberView, emailView;


    public HolderView(@NonNull View itemView) {
        super(itemView);
        imgView = itemView.findViewById(R.id.imageView);
        nameView = itemView.findViewById(R.id.nameView);
        numberView = itemView.findViewById(R.id.numberView);
        emailView = itemView.findViewById(R.id.emailView);


    }
}
