package com.streamnow.lindaumobile.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.streamnow.lindaumobile.R;
import com.streamnow.lindaumobile.activities.SettingsActivity;
import com.streamnow.lindaumobile.interfaces.IMenuPrintable;

import java.util.ArrayList;

/**
 * Created by Miguel Angel on 29/06/2016.
 */

public class SettingsAdapter extends BaseAdapter {

    private ArrayList <String> items;
    private Context context;
    public SettingsAdapter(Context context , ArrayList<String> items){

        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if( convertView == null )
        {
            convertView = ((Activity) context).getLayoutInflater().inflate(R.layout.settings_menu_row, parent, false);
        }

        LinearLayout row_settings_bgnd = (LinearLayout)convertView.findViewById(R.id.row_settings_bgnd);
        row_settings_bgnd.setBackgroundColor(Lindau.getInstance().getCurrentSessionUser().userInfo.partner.colorService);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.row_settings_icon);
        TextView textView = (TextView) convertView.findViewById(R.id.row_settings_text);
        textView.setTextColor(Lindau.getInstance().getCurrentSessionUser().userInfo.partner.fontColorSmartphone);
        /*if(position==0){
            Picasso.with(context)
                    .load(R.drawable.profile)
                    .into(imageView);
            // imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_name));
            textView.setText(items.get(position));
        }else if(position==1){
            Picasso.with(context)
                    .load(R.drawable.general_docs_icon)
                    .into(imageView);
            // imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_name));
            textView.setText(items.get(position));
        } else if(position==2){
            Picasso.with(context)
                    .load(R.drawable.logout)
                    .into(imageView);
            // imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_name));
            textView.setText(items.get(position));
        } else if(position==3){
            Picasso.with(context)
                    .load(R.drawable.ic_action_name)
                    .into(imageView);
            // imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_name));
            textView.setText(items.get(position));
        }*/


        if(position==0){
            imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.profile));
            textView.setText(items.get(position));
        }else if(position==1){
            imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.general_docs_icon));
            textView.setText(items.get(position));
        } else if(position==2){
            imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.logout));
            textView.setText(items.get(position));
        } else if(position==3){
            imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_name));
            textView.setText(items.get(position));
        }

        return convertView;
    }
}
