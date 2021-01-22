/*
 * Created by Francisco Javier Paños Madrona on 6/12/20 10:04
 * Copyright (c) 2020 . All rights reserved.
 * Last modified 23/11/20 16:43
 */

package com.example.murbin.data.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.murbin.R;
import com.example.murbin.models.Streetlight;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class StreetlightsListAdapter extends FirestoreRecyclerAdapter<Streetlight, StreetlightsListAdapter.ViewHolder> {

    protected View.OnClickListener onClickListener;
    protected Context context;

    /**
     * Constructor
     *
     * @param options Options for the FirestoreRecyclerOptions
     * @param context Context received
     */
    public StreetlightsListAdapter(@NonNull FirestoreRecyclerOptions<Streetlight> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    public StreetlightsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(
                        R.layout.recycler_fragment_element,
                        parent,
                        false
                );
        view.setOnClickListener(onClickListener);

        return new StreetlightsListAdapter.ViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull StreetlightsListAdapter.ViewHolder holder, int position, @NonNull Streetlight streetlight) {
        holder.customizeView(streetlight, position);
        holder.itemView.setTag(position);
    }

    /**
     * Set the onClickListener
     *
     * @param onClick The onClickListener received
     */
    public void setOnItemClickListener(View.OnClickListener onClick) {
        onClickListener = onClick;
    }

    /**
     * Returns the snapshot position
     *
     * @param position Snapshot position
     * @return String
     */
    public String getId(int position) {
        return super.getSnapshots().getSnapshot(position).getId();
    }

    /**
     * Get the position from the id received as a parameter
     *
     * @param id Id searched
     * @return int
     */
    public int getPosition(String id) {
        int position = 0;

        while (position < getItemCount()) {
            if (getId(position).equals(id)) {

                return position;
            }
            position++;
        }

        return -1;
    }

    /**
     * Cream the ViewHolder, with the elements to modify
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_full_name;

        /**
         * Constructor
         *
         * @param itemView View
         */
        public ViewHolder(View itemView) {
            super(itemView);
            tv_full_name = itemView.findViewById(R.id.administrator_subzones_list_recyclerview_element_tv_name);
        }

        /**
         * Customize a View elements from a User
         */
        public void customizeView(Streetlight streetlight, int position) {
            String full_name = "Farola " + position;
            tv_full_name.setText(full_name);
        }
    }
}
