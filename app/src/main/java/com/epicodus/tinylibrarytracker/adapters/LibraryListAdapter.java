package com.epicodus.tinylibrarytracker.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.epicodus.tinylibrarytracker.R;
import com.epicodus.tinylibrarytracker.models.Library;
import com.epicodus.tinylibrarytracker.ui.LibraryActivity;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Guest on 7/26/16.
 */
public class LibraryListAdapter extends RecyclerView.Adapter<LibraryListAdapter.LibraryViewHolder>{
    private static final int MAX_WIDTH = 200;
    private static final int MAX_HEIGHT = 200;

    private ArrayList<Library> mLibraries = new ArrayList<>();
    private Context mContext;

    public LibraryListAdapter(Context context, ArrayList<Library> libraries) {
        mContext = context;
        mLibraries = libraries;
    }

    @Override
    public LibraryListAdapter.LibraryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.library_list_item, parent, false);
        LibraryViewHolder viewHolder = new LibraryViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(LibraryListAdapter.LibraryViewHolder holder, int position) {
        holder.bindLibrary(mLibraries.get(position));
    }

    @Override
    public int getItemCount() {
        return mLibraries.size();
    }

    public class LibraryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @Bind(R.id.libraryImageView) ImageView mLibraryImageView;
        @Bind(R.id.libraryAddress) TextView mLibraryAddress;
        @Bind(R.id.libraryCharterNumber) TextView mLibraryCharterNumber;

        private Context mContext;
        private int zipCode;

        public LibraryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext = itemView.getContext();
            itemView.setOnClickListener(this);
        }

        public void bindLibrary(Library library) {
            zipCode = library.getZipCode();

            Picasso.with(mContext)
                    .load(library.getImage())
                    .resize(MAX_WIDTH, MAX_HEIGHT)
                    .centerCrop()
                    .into(mLibraryImageView);

            mLibraryAddress.setText(library.getAddress());
            int charterNumber = library.getCharterNumber();
            if (charterNumber != -1) {
                mLibraryCharterNumber.setText("Charter#: " + String.valueOf(charterNumber));
            } else {
                mLibraryCharterNumber.setText("");
            }
        }

        @Override
        public void onClick(View view) {
            int itemPosition = getLayoutPosition();
            Intent intent = new Intent(mContext, LibraryActivity.class);
            intent.putExtra("zipCode", String.valueOf(zipCode));
            intent.putExtra("position", itemPosition + "");
            intent.putExtra("libraries", Parcels.wrap(mLibraries));
            mContext.startActivity(intent);
        }
    }

}
