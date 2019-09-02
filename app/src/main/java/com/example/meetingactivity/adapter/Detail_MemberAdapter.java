package com.example.meetingactivity.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.meetingactivity.R;
import com.example.meetingactivity.model.Detail_Todo;
import com.example.meetingactivity.model.MemberTest;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.List;

public class Detail_MemberAdapter extends ArrayAdapter<Detail_Todo> {
    Activity activity;
    int resource;
    ImageLoader imageLoader;
    DisplayImageOptions options;


    public Detail_MemberAdapter(Context context, int resource, List<Detail_Todo> objects) {
        super(context, resource, objects);
        activity = (Activity) context;
        this.resource = resource;
        imageLoaderInit();
    }

    private void imageLoaderInit() {
        imageLoader = ImageLoader.getInstance();
        if(!imageLoader.isInited()){
            ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(activity);
            imageLoader.init(configuration);
        }

        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
        builder.showImageOnLoading(R.drawable.ic_stub);
        builder.showImageOnFail(R.drawable.ic_error);
        builder.showImageForEmptyUri(R.drawable.ic_empty);
        builder.cacheInMemory(true);
        builder.cacheOnDisk(true);
        builder.considerExifParams(true);
        options = builder.build();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = activity.getLayoutInflater().inflate(resource, null);
        }

        Detail_Todo item = getItem(position);
        if(item != null){
            ImageView Detail_item_img = convertView.findViewById(R.id.memitem_img);
            TextView Detail_item_name = convertView.findViewById(R.id.memitem_name);
            TextView Detail_item_todo = convertView.findViewById(R.id.memitem_permit);
            TextView Detail_item_ex = convertView.findViewById(R.id.Detail_item_ex);
            CheckBox Detail_item_amount = convertView.findViewById(R.id.Detail_item_amount);

            if(!item.getDetail_item_img().equals("")){
                imageLoader.displayImage(item.getDetail_item_img(), Detail_item_img, options);
            }
            Detail_item_name.setText(item.getId());
            Detail_item_todo.setText(item.getTodo());
            Detail_item_ex.setText(item.getEx());
            if(item.getAmount() == 0 ){
                Detail_item_amount.setChecked(false);
            } else {
                Detail_item_amount.setChecked(true);
            }
        }

        return convertView;
    }
}
