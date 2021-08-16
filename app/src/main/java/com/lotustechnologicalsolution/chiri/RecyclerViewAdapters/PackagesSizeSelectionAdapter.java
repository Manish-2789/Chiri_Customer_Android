package com.lotustechnologicalsolution.chiri.RecyclerViewAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lotustechnologicalsolution.R;
import com.lotustechnologicalsolution.chiri.HelpingClasses.Preferences;
import com.lotustechnologicalsolution.chiri.Interfaces.AdapterClickListenerCallback;
import com.lotustechnologicalsolution.chiri.ModelClasses.PackagesSizeSelectionModel;
import java.util.ArrayList;


public class PackagesSizeSelectionAdapter extends RecyclerView.Adapter<PackagesSizeSelectionAdapter.ViewHolder> {

    private Context context;
    private ArrayList<PackagesSizeSelectionModel> list;
    private AdapterClickListenerCallback itemclick;

    public PackagesSizeSelectionAdapter(Context context, ArrayList<PackagesSizeSelectionModel> list, AdapterClickListenerCallback itemclick) {
        this.context = context;
        this.list = list;
        this.itemclick = itemclick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.packages_size_selection_item_view, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PackagesSizeSelectionModel item = list.get(position);

        holder.tvTitle.setText(item.getTitle());
        holder.tvDescription.setText(item.getDescription());
        holder.tvPrice.setText(holder.preferences.getKeyCurrencySymbol()+" "+item.getPrice());

        Glide.with(holder.itemView.getContext())
                .load(item.getImage())
                .placeholder(R.drawable.ic_box)
                .error(R.drawable.ic_box)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.ivProductImage);


        if (item.isSelected())
        {
            holder.mainTab.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(),R.drawable.ractengle_stroke_round_white));
        }
        else
        {
            holder.mainTab.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(),R.drawable.ractengle_stroke_round_transparent));
        }


        holder.bind(position, item,itemclick);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvDescription, tvPrice;
        ImageView ivProductImage;
        RelativeLayout mainTab;
        Preferences preferences;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            preferences=new Preferences(itemView.getContext());
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            tvDescription = (TextView) itemView.findViewById(R.id.tv_description);
            tvPrice = (TextView) itemView.findViewById(R.id.tv_price);
            ivProductImage = (ImageView) itemView.findViewById(R.id.iv_product_image);
            mainTab = (RelativeLayout) itemView.findViewById(R.id.main_tab);

        }

        void bind(final int pos, final PackagesSizeSelectionModel item, final AdapterClickListenerCallback onClickListner){
            mainTab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickListner.OnItemClick(pos,item,view);
                }
            });
        }
    }
}
