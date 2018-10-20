package com.travelquest.travelquest.database_handler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.travelquest.travelquest.R;

import java.util.List;

public class PoIAdapter extends ArrayAdapter<PoI> {
    public PoIAdapter(Context context, List<PoI> pois) {
        super(context, 0, pois);
    }

    public PoIAdapter(Context context, int textViewResourceId, List<PoI> pois) {
        super(context, textViewResourceId, pois);
    }

    public PoI getItem(int position){
        return super.getItem(position);
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        PoI poi = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(this.getContext())
                    .inflate(R.layout.poi_layout, parent, false);
        }

        TextView name = (TextView) convertView.findViewById(R.id.poiTitle);
        //TextView coord = (TextView) convertView.findViewById(R.id.myposition);
        name.setText(poi.getTitle());
        //coord.setText("Latitude: " + sensor.getPosition()[0] + "\nLongitude: " + sensor.getPosition()[1]);
        TextView poiCountry = (TextView) convertView.findViewById(R.id.poiCountry);
        poiCountry.setText("Country: " + poi.getCountry());


        return convertView;
    }

    public void notifyDataSetChanged(){
        super.notifyDataSetChanged();
    }
}
