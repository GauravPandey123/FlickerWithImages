package com.example.medrait.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;


import androidx.recyclerview.widget.RecyclerView;

import com.example.medrait.R;
import com.example.medrait.model.ImageSize;
import com.example.medrait.model.PhotoModel;
import com.example.medrait.util.AppUtil;
import com.example.medrait.util.RowClickListener;

import java.util.ArrayList;
import java.util.List;


public class PhotoAdapter extends RecyclerView.Adapter implements Filterable {

    private static final int VIEW_ITEM = 1;
    private static final int VIEW_PROG = 0;
    private List<PhotoModel> contactListFiltered;

    private final List<PhotoModel> items = new ArrayList<>();
    private RowClickListener<PhotoModel> rowClickListener;

    public PhotoAdapter() {
        this.contactListFiltered = items;
    }

    @Override
    public int getItemViewType(int position) {
        return contactListFiltered.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_ITEM) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_photo, parent, false);
            return new PhotoViewHolder(itemView);
        } else {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_progress, parent, false);
            return new ProgressViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof PhotoViewHolder) {
            PhotoViewHolder vh = (PhotoViewHolder) holder;
            PhotoModel item = contactListFiltered.get(position);
            AppUtil.bindImage(item.getImageUrl(ImageSize.MEDIUM), vh.image, true);
            if (rowClickListener != null) {
                vh.image.setOnClickListener(view -> rowClickListener.onRowClicked(holder.getAdapterPosition(),
                        contactListFiltered.get(holder.getAdapterPosition())));
            }

            vh.textViewTitle.setText(item.title);
        }
    }

    @Override
    public int getItemCount() {
        return contactListFiltered.size();
    }

    public void setRowClickListener(RowClickListener<PhotoModel> rowClickListener) {
        this.rowClickListener = rowClickListener;
    }

    public void addAll(List<PhotoModel> newItems) {
        if (newItems == null) {
            contactListFiltered.add(null);
            notifyItemInserted(getItemCount() - 1);
        } else {
            contactListFiltered.addAll(newItems);
            notifyDataSetChanged();
        }
    }

    public List<PhotoModel> getAll() {
        return contactListFiltered;
    }

    public void remove(int index) {
        if (index == -1) return;
        contactListFiltered.remove(index);
        notifyItemRemoved(index);
    }

    public void clear() {
        contactListFiltered.clear();
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    contactListFiltered = items;
                } else {
                    List<PhotoModel> filteredList = new ArrayList<>();
                    for (PhotoModel row : items) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getTitle().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    contactListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = contactListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                contactListFiltered = (ArrayList<PhotoModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

}