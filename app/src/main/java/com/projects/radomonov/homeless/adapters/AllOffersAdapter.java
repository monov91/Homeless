package com.projects.radomonov.homeless.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.projects.radomonov.homeless.R;
import com.projects.radomonov.homeless.model.Offer;

import java.util.List;

/**
 * Created by Tom on 01.11.2017.
 */

public class AllOffersAdapter extends RecyclerView.Adapter<AllOffersAdapter.MyViewHolder> implements MyOffersAdapter.onOfferClickListener {

    private Context context;
    private List<Offer> allOffers;
    private LayoutInflater inflater;
    private MyOffersAdapter.onOfferClickListener listener;

    public AllOffersAdapter(Context context,List<Offer> allOffers,MyOffersAdapter.onOfferClickListener listener) {
        this.allOffers = allOffers;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.listener = listener;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.offer_item, parent, false);
        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Offer currentOffer = allOffers.get(position);
        holder.setData(currentOffer, position);
        holder.setClickListener(listener,currentOffer);
    }

    @Override
    public int getItemCount() {
        return allOffers.size();
    }

    @Override
    public void onOfferClick(Offer currentOffer) {

    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tvTitle;
        TextView tvPrice;
        TextView tvCurrency;
        TextView tvRooms;
        TextView tvNeighbourhood;
        ImageView imgPic;
        View mView;
        int position;


        public MyViewHolder(View itemView) {
            super(itemView);

            tvCurrency = itemView.findViewById(R.id.tv_currency_item);
            tvTitle = itemView.findViewById(R.id.tv_title_item);
            tvPrice = itemView.findViewById(R.id.tv_price_item);
            tvRooms = itemView.findViewById(R.id.tv_rooms_item);
            tvNeighbourhood = itemView.findViewById(R.id.tv_neighbourhood_item);
            imgPic = itemView.findViewById(R.id.img_item);
            mView = itemView;
        }

        private void setData(Offer current, int position){
            tvCurrency.setText(current.getCurrency().toString());
            tvTitle.setText(current.getTitle());
            tvPrice.setText("Price: "+current.getPrice());
            tvRooms.setText("Rooms: "+current.getRooms());
            tvNeighbourhood.setText(current.getNeighbourhood());
            setImage(context, current.getImageThumbnail());
            this.position = position;
        }

        private void setImage(Context context, String imgURL) {
            //Picasso.with(context).load(imgURL).into(imgPic);
            Glide.with(context).load(imgURL).override(100,80).into(imgPic);
        }

        private void setClickListener(final MyOffersAdapter.onOfferClickListener listener, final Offer currentOffer) {
            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onOfferClick(currentOffer);
                }
            });
        }


    }
}
