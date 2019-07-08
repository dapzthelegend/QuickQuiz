package com.dapzthelegend.quickquiz;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class KeyWordsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView txtName, txtDescription;
    public ItemClickListner itemClickListner;
    public ImageView imageView;
    public KeyWordsViewHolder(@NonNull View itemView) {
        super(itemView);
        txtDescription = (TextView) itemView.findViewById(R.id.txtWordDescription);
        txtName= (TextView) itemView.findViewById(R.id.txtWordName);
        imageView = (ImageView) itemView.findViewById(R.id.keyImageView);

    }


    @Override
    public void onClick(View v) {
        itemClickListner.onClick(v, getAdapterPosition(), false);

    }
}
