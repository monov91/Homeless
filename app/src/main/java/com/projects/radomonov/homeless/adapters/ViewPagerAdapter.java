package com.projects.radomonov.homeless.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.projects.radomonov.homeless.R;
import com.projects.radomonov.homeless.model.Offer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.projects.radomonov.homeless.R.id.imageView;
import static java.security.AccessController.getContext;

/**
 * Created by Tom on 01.11.2017.
 */

public class ViewPagerAdapter extends PagerAdapter{

    private Context context;
    private LayoutInflater layoutInflater;

    private ArrayList<String> imgUrls;
    private Offer offer;

    private ImageView imageView;


    public ViewPagerAdapter(Context context, Offer offer) {
        this.offer = offer;
        this.context = context;
        this.imgUrls = new ArrayList<>();
        Iterator it = offer.getImageUrls().entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            imgUrls.add(pair.getValue().toString());
        }
    }

    @Override
    public int getCount() {
        return imgUrls.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.custom_layout, null);
        imageView = view.findViewById(R.id.imageView);
        Glide.with(context).load(Uri.parse(imgUrls.get(position))).into(imageView);

        ViewPager vp = (ViewPager) container;
        vp.addView(view, 0);
        return view;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        ViewPager vp = (ViewPager) container;
        View view = (View) object;
        vp.removeView(view);

    }
}