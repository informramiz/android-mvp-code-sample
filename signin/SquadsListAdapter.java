package com.hypercubesoft.boatdisplay.signin;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hypercubesoft.boatdisplay.entity.Squad;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ramiz on 9/25/16.
 */

public class SquadsListAdapter extends ArrayAdapter<Squad> {

    @NonNull
    private ArrayList<Squad> mSquads = new ArrayList<>();
    @LayoutRes
    private int mLayoutResId;

    public SquadsListAdapter(Context context, int resource, ArrayList<Squad> squads) {
        super(context, resource, squads);
        mLayoutResId = resource;
        mSquads.addAll(squads);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = new ViewHolder();

        //check if we can an already inflated view which is ready to be recycled
        //it is recommended and increase efficiency
        if (convertView == null) {
            //inflate view needed to display squad name
            convertView = LayoutInflater.from(parent.getContext()).inflate(mLayoutResId, parent, false);
            viewHolder.textView = (TextView) convertView;

            //save view holder so that later we can recycle it
            convertView.setTag(viewHolder);

        } else {
            // view is ready to be recycled so get the view holder we saved as tag
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //get the squad for current view position
        Squad squad = mSquads.get(position);
        viewHolder.textView.setText(squad.squadName);

        return convertView;
    }

    private static class ViewHolder {
        TextView textView;
    }
}
