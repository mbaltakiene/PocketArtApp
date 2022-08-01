package com.example.android.pocketartapp.ui;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.pocketartapp.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PaintingsListAdapter extends RecyclerView.Adapter<PaintingsListAdapter.PaintingsAdapterViewHolder> {

    private Context mContext;
    private ArrayList<Painting> mData;

    public PaintingsListAdapter(Context context, ArrayList<Painting> data) {
        mContext = context;
        mData = data;
    }

    @NonNull
    @Override
    public PaintingsAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.list_item_layout, viewGroup, false);

        final PaintingsAdapterViewHolder viewHolder = new PaintingsAdapterViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = null;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    bundle = ActivityOptions.
                            makeSceneTransitionAnimation(
                                    (Activity) mContext,
                                    viewHolder.mImageView,
                                    viewHolder.mImageView.getTransitionName()).toBundle();
                }
                Intent detailsIntent = new Intent(mContext, DetailsActivity.class);
                detailsIntent.putExtra(MainActivity.STARTING_POSITION_KEY, viewHolder.getAdapterPosition());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ((Activity) mContext).startActivityForResult(detailsIntent, MainActivity.REQUEST_CODE, bundle);
                } else {
                    ((Activity) mContext).startActivityForResult(detailsIntent, MainActivity.REQUEST_CODE);
                }
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final PaintingsAdapterViewHolder holder, int position) {
        String url = mData.get(position).getUrl();
        String author = mData.get(position).getAuthor();
        String title = mData.get(position).getTitle();
        if (!TextUtils.isEmpty(url)) {
            Picasso.with(mContext)
                    .load(url)
                    .into(holder.mImageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            Bitmap bitmap = ((BitmapDrawable) holder.mImageView.getDrawable()).getBitmap();
                            if (bitmap != null) {
                                Palette p = Palette.from(bitmap).generate();
                                int mutedColor = p.getDarkMutedColor(mContext.getResources().getColor(R.color.colorDarkGray)); //
                                holder.mTitleTextView.setBackgroundColor(mutedColor);
                                holder.mAuthorTextView.setBackgroundColor(mutedColor);
                            }
                        }
                        @Override
                        public void onError() {
                            //do nothing
                        }
                    });
        }
        holder.mImageView.setContentDescription(title);
        holder.mTitleTextView.setText(title);
        holder.mAuthorTextView.setText(author);
    }

    @Override
    public int getItemCount() {
        if (null == mData) return 0;
        return mData.size();
    }

    /**
     * The method sets the paintings data on Adapter if one is already created.
     *
     * @param data The new data to be displayed
     */
    public void setData(ArrayList<Painting> data) {
        mData = data;
        notifyDataSetChanged();
    }

    class PaintingsAdapterViewHolder extends RecyclerView.ViewHolder {

        final ImageView mImageView;
        final TextView mTitleTextView;
        final TextView mAuthorTextView;

        PaintingsAdapterViewHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.thumbnail);
            mTitleTextView = itemView.findViewById(R.id.painting_title);
            mAuthorTextView = itemView.findViewById(R.id.painting_author);
        }
    }
}