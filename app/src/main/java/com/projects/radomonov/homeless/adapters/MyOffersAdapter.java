package com.projects.radomonov.homeless.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.projects.radomonov.homeless.R;
import com.projects.radomonov.homeless.model.Offer;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Tom on 21.10.2017.
 */

public class MyOffersAdapter extends RecyclerView.Adapter<MyOffersAdapter.MyViewHolder> {
    private Context context;
    private List<Offer> myOffers;
    private LayoutInflater inflater;

    public MyOffersAdapter(Context context,List<Offer> myOffers) {
        this.myOffers = myOffers;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.offer_item,parent,false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Offer currentOffer = myOffers.get(position);
        holder.setData(currentOffer, position);
    }

    @Override
    public int getItemCount() {
        return myOffers.size();
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

        private void setData(Offer current,int position){
            tvCurrency.setText(current.getCurrency());
            tvTitle.setText(current.getTitle());
            tvPrice.setText(current.getPrice());
            tvRooms.setText(current.getRooms());
            tvNeighbourhood.setText(current.getNeighbourhood());
            setImage(context, current.getImage());
            this.position = position;
        }

        private void setImage(Context context, String imgURL) {
            Picasso.with(context).load(Uri.parse(imgURL)).into(imgPic);
        }

    }
}
