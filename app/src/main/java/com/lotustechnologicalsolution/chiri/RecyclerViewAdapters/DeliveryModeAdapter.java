package com.lotustechnologicalsolution.chiri.RecyclerViewAdapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.lotustechnologicalsolution.R;
import com.lotustechnologicalsolution.chiri.ModelClasses.DeliveryModeModel;
import com.lotustechnologicalsolution.databinding.ItemDeliveryModeBinding;

import java.util.ArrayList;

public class DeliveryModeAdapter extends RecyclerView.Adapter<DeliveryModeAdapter.MyViewHolder> {

    private final ArrayList<DeliveryModeModel> deliveryTypeModels;
    private ModeItemSelectListener listener;

    public DeliveryModeAdapter(ArrayList<DeliveryModeModel> deliveryTypeModels, ModeItemSelectListener listener) {
        this.deliveryTypeModels = deliveryTypeModels;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDeliveryModeBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_delivery_mode, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DeliveryModeModel object = deliveryTypeModels.get(position);
        holder.setData(object);
    }

    @Override
    public int getItemCount() {
        return deliveryTypeModels != null ? deliveryTypeModels.size() : 0;
    }

    public interface ModeItemSelectListener {
        void onModeSelect(int itemPosition, Object object);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final ItemDeliveryModeBinding binding;
        private final Context context;

        public MyViewHolder(@NonNull ItemDeliveryModeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            this.context = this.binding.getRoot().getContext();

            if (listener == null)
                listener = (ModeItemSelectListener) context;
        }

        public void setData(DeliveryModeModel deliveryModeModel) {
            binding.tvModeTitle.setText(deliveryModeModel.getModeName());

            if (deliveryModeModel.isSelected()) {
                binding.line.setVisibility(View.VISIBLE);
                binding.tvModeTitle.setTextColor(Color.parseColor("#000000"));
            } else {
                binding.line.setVisibility(View.GONE);
                binding.tvModeTitle.setTextColor(Color.parseColor("#8E8E93"));
            }

            binding.rootLayout.setOnClickListener(v -> listener.onModeSelect(getLayoutPosition(), deliveryModeModel));
        }
    }
}
