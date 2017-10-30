package com.projects.radomonov.homeless.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.projects.radomonov.homeless.R;
import com.projects.radomonov.homeless.utilities.Utilities;

import java.util.List;

import static android.os.Build.VERSION_CODES.M;

/**
 * Created by admin on 30.10.2017.
 */

public class NeighbourhoodsAdapter extends RecyclerView.Adapter<NeighbourhoodsAdapter.NeighViewHolder> {

    private Context context;
    private List<Utilities.Neighbourhood> neighbourhoodList;
    private LayoutInflater inflater;

    public NeighbourhoodsAdapter(Context context, List<Utilities.Neighbourhood> neighbourhoodList){
        this.context = context;
        this.neighbourhoodList = neighbourhoodList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public NeighViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.neighbourhood_item,parent,false);
        NeighViewHolder holder = new NeighViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(NeighViewHolder holder, final int position) {
        holder.tvNeighbourhood.setText(neighbourhoodList.get(position).toString());
        holder.imgRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                neighbourhoodList.remove(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return neighbourhoodList.size();
    }

    class NeighViewHolder extends RecyclerView.ViewHolder{

        TextView tvNeighbourhood;
        ImageView imgRemove;

        public NeighViewHolder(View itemView) {
            super(itemView);

            tvNeighbourhood = itemView.findViewById(R.id.tv_neighbourhood_item);
            imgRemove = itemView.findViewById(R.id.img_remove_neighbourhood_item);
        }
    }
}
