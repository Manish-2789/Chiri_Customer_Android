package com.lotustechnologicalsolution.chiri.RecyclerViewAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lotustechnologicalsolution.R;
import com.lotustechnologicalsolution.chiri.Interfaces.AdapterClickListenerCallback;
import com.lotustechnologicalsolution.chiri.ModelClasses.CountryCodeModel;

import java.util.ArrayList;
import java.util.List;

public class CountryCodeAdapter extends RecyclerView.Adapter<CountryCodeAdapter.ViewHolder> implements Filterable {

    private List<CountryCodeModel> countryModelList, tempModellist;
    private AdapterClickListenerCallback click;
    private Context context;

    public CountryCodeAdapter(Context context, List<CountryCodeModel> modellist, AdapterClickListenerCallback click) {
        this.countryModelList = new ArrayList<>(modellist);
        this.tempModellist = modellist;
        this.click = click;
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_country_code, null);
        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CountryCodeModel model = tempModellist.get(position);

        holder.tvCountry.setText(model.country_name);

        /*if (model.country_name.equals(Select_Country_F.selectedCountry)) {
            holder.ivTick.setVisibility(View.VISIBLE);
        } else {
            holder.ivTick.setVisibility(View.GONE);
        }*/


        holder.bind(position, model, click);
    }


    @Override
    public int getItemCount() {
        return tempModellist.size();
    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }


    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<CountryCodeModel> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(countryModelList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (CountryCodeModel item : countryModelList) {
                    if (item.country_name.toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            tempModellist.clear();
            tempModellist.addAll((ArrayList) results.values);
            notifyDataSetChanged();
        }
    };


    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvCountry;
        //ImageView ivTick;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            //ivTick = itemView.findViewById(R.id.iv_tick);
            tvCountry = itemView.findViewById(R.id.tv_Country);

        }

        public void bind(final int item, final CountryCodeModel model,
                         final AdapterClickListenerCallback listener) {
            itemView.setOnClickListener(v -> {
                // This is OnClick of any list Item
                listener.OnItemClick(item, model, v);
            });
        }
    }
}
