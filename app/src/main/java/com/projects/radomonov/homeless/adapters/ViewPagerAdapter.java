package com.projects.radomonov.homeless.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.projects.radomonov.homeless.R;

import static com.projects.radomonov.homeless.R.id.imageView;

/**
 * Created by Tom on 01.11.2017.
 */

public class ViewPagerAdapter extends PagerAdapter{

    private Context context;
    private LayoutInflater layoutInflater;
    private Integer[] images = {R.drawable.slide4, R.drawable.slide5, R.drawable.slide6};

    private ImageView imageView;

    public ViewPagerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.fragment_offer_view, null);
        imageView = view.findViewById(R.id.img_view_offer_info);
        if(imageView != null) {
            imageView.setImageResource(images[position]);
        } else {
            Toast.makeText(context, "Nullllll", Toast.LENGTH_SHORT).show();
        }


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








