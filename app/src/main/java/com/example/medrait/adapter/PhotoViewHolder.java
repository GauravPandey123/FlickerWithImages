package com.example.medrait.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import com.example.medrait.R;



public class PhotoViewHolder
        extends RecyclerView.ViewHolder {

    protected ImageView image;
    protected TextView textViewTitle;

    public PhotoViewHolder(View itemView) {
        super(itemView);
        image = itemView.findViewById(R.id.my_image_view);
        textViewTitle=itemView.findViewById(R.id.textViewTitle);
    }

}