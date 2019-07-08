package com.dapzthelegend.quickquiz;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SubjectsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public ImageView imageView;
    public TextView txtName;

    public SubjectsViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.imgSubject);
        txtName = itemView.findViewById(R.id.txtSubjectName);
    }

    @Override
    public void onClick(View v) {

    }
}
