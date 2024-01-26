package com.example.spshare;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class GridAdapter extends BaseAdapter {

    private Integer[] gridImages = {
        R.drawable.cat,
        R.drawable.dog,
        R.drawable.duck,
        R.drawable.fish,
        R.drawable.horse,
        R.drawable.rabbit,
        R.drawable.parrot
    };

    private String[] infoTitlesArray;
    private String[] infoDesArray;
    private LayoutInflater layoutInflater;

    public GridAdapter(Context con, String[] titles, String[] descriptions) {
        this.layoutInflater = LayoutInflater.from(con);
        this.infoTitlesArray = titles;
        this.infoDesArray = descriptions;
    }

    @Override
    public int getCount() {
        return gridImages.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        /*The variable convertView is a View object. We'll be setting our image and heading XML
        file as convertView. But we need to check if it's null or not. If it's null then we can go
        ahead and set some values. If you don't check for null then you'd get too many image and
        headings being created, and the app could crash. We only want to create an image and heading
        View if convertView is null.*/
        if(convertView == null) {
            convertView = layoutInflater.inflate(R.layout.grid_item, parent, false);

            TextView infoTitle = (TextView) convertView.findViewById(R.id.info_title);
            TextView infoDes = (TextView) convertView.findViewById(R.id.info_des);
            ImageView gridImage = (ImageView) convertView.findViewById(R.id.grid_image);

            infoTitle.setText(infoTitlesArray[position]);
            infoDes.setText(infoDesArray[position]);
            gridImage.setImageResource(gridImages[position]);
        }

        return convertView;
    }
}
