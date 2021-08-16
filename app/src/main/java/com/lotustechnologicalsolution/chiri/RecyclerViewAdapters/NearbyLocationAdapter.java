package com.lotustechnologicalsolution.chiri.RecyclerViewAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.lotustechnologicalsolution.chiri.Interfaces.AdapterClickListenerCallback;
import com.lotustechnologicalsolution.chiri.ModelClasses.NearbyLocationModel;
import com.lotustechnologicalsolution.R;

import java.util.ArrayList;
import java.util.List;

public class NearbyLocationAdapter extends ArrayAdapter {
    private ArrayList<NearbyLocationModel> list;
    private AdapterClickListenerCallback onitemclick;
    private Context context;
    public NearbyLocationAdapter(@NonNull Context context, @NonNull ArrayList<NearbyLocationModel> objects, AdapterClickListenerCallback onClickListner) {
        super(context, R.layout.near_by_search_item_view, objects);
        this.list = objects;
        this.context = context;
        this.onitemclick = onClickListner;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        NearbyLocationModel model = list.get(position);
        View view = layoutInflater.inflate(R.layout.near_by_search_item_view,null);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.tvLoc.setText(model.getTitle());
        viewHolder.tvLocationDesc.setText(model.getAddress());

        viewHolder.bind(position, model, onitemclick);

        return view;
    }

   /* public void updateList(ArrayList<NearbyLocationModel> sorted_list) {
        try {
            list.clear();
            notifyDataSetChanged();
            list = sorted_list;
            notifyDataSetChanged();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }*/

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvLoc, tvLocationDesc;

        ViewHolder(View itemView) {
            super(itemView);

            tvLoc = itemView.findViewById(R.id.tv_loc);

            tvLocationDesc = itemView.findViewById(R.id.tv_location_desc);


        }


        void bind(final int pos, final NearbyLocationModel item, final AdapterClickListenerCallback listener) {

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.OnItemClick(pos, item, view);
                }
            });

        }
    }
}
/*public class NearbyLocationAdapter extends RecyclerView.Adapter<NearbyLocationAdapter.ViewHolder> {

    private List<NearbyLocationModel> list;
    private AdapterClickListenerCallback onitemclick;

    public NearbyLocationAdapter(List<NearbyLocationModel> list, AdapterClickListenerCallback onClickListner) {
        this.list = list;
        this.onitemclick = onClickListner;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.near_by_search_item_view, null);
        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {

        try {
            NearbyLocationModel model = list.get(i);

            viewHolder.tvLoc.setText(model.getTitle());
            viewHolder.tvLocationDesc.setText(model.getAddress());

            viewHolder.bind(i, model, onitemclick);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }


    public void updateList(List<NearbyLocationModel> sorted_list) {
        try {
            list.clear();
            notifyDataSetChanged();
            list = sorted_list;
            notifyDataSetChanged();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvLoc, tvLocationDesc;

        ViewHolder(View itemView) {
            super(itemView);

            tvLoc = itemView.findViewById(R.id.tv_loc);

            tvLocationDesc = itemView.findViewById(R.id.tv_location_desc);


        }


        void bind(final int pos, final NearbyLocationModel item, final AdapterClickListenerCallback listener) {

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.OnItemClick(pos, item, view);
                }
            });

        }
    }
}*/
