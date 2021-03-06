package com.projects.radomonov.homeless.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.projects.radomonov.homeless.R;
import com.projects.radomonov.homeless.model.Offer;

import java.util.List;

/**
 * Created by admin on 31.10.2017.
 */

public class OfferPhotosAdapter extends RecyclerView.Adapter<OfferPhotosAdapter.ViewHolder> {

    public interface deleteClickListener{
        void onDeleteClick(Uri uri);
    }
    private List<Uri> offerPhotos;
    private Context context;
    private LayoutInflater inflater;
    private deleteClickListener listener;

    public OfferPhotosAdapter(Context context,List<Uri> offerPhotos,deleteClickListener listener){
        this.context = context;
        this.offerPhotos = offerPhotos;
        this.inflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.offer_photo_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Glide.with(context).load(offerPhotos.get(position)).override(100,80).into(holder.image);
        holder.setClickListener(listener,offerPhotos.get(position));
        /*holder.delete_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                offerPhotos.remove(position);
                notifyDataSetChanged();
                Log.i("photosize", String.valueOf(offerPhotos.size()));
            }
        });*/

    }

    @Override
    public int getItemCount() {
        return offerPhotos.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        ImageView delete_icon;

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.img_offer_photo);
            delete_icon = itemView.findViewById(R.id.img_delete_offer_photo);
        }

        private void setClickListener(final deleteClickListener listener, final Uri uri) {
            delete_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onDeleteClick(uri);
                }
            });
        }
    }
}
