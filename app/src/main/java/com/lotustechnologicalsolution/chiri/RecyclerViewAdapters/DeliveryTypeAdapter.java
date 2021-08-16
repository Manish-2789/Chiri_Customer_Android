package com.lotustechnologicalsolution.chiri.RecyclerViewAdapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.lotustechnologicalsolution.R;
import com.lotustechnologicalsolution.chiri.ModelClasses.DeliveryTypeModel;
import com.lotustechnologicalsolution.databinding.DeliveryTypeItemViewBinding;

import java.util.ArrayList;

public class DeliveryTypeAdapter extends RecyclerView.Adapter<DeliveryTypeAdapter.MyViewHolder> {

    private final ArrayList<DeliveryTypeModel> deliveryTypeModels;
    private DeliveryTypeItemSelectListener listener;

    public DeliveryTypeAdapter(ArrayList<DeliveryTypeModel> deliveryTypeModels, DeliveryTypeItemSelectListener listener) {
        this.deliveryTypeModels = deliveryTypeModels;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        DeliveryTypeItemViewBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.delivery_type_item_view, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DeliveryTypeModel deliveryTypeModel = deliveryTypeModels.get(position);
        holder.setData(deliveryTypeModel);
    }

    @Override
    public int getItemCount() {
        return deliveryTypeModels != null ? deliveryTypeModels.size() : 0;
    }

    public interface DeliveryTypeItemSelectListener {
        void onTypeSelect(int itemPosition, Object object);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final DeliveryTypeItemViewBinding binding;
        private final Context context;

        public MyViewHolder(@NonNull DeliveryTypeItemViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            this.context = this.binding.getRoot().getContext();

            if (listener == null)
                listener = (DeliveryTypeItemSelectListener) context;
        }

        @SuppressLint("SetTextI18n")
        public void setData(DeliveryTypeModel deliveryTypeModel) {

            binding.tvVehicleName.setText(deliveryTypeModel.getVehicleType());
            // binding.tvVehicalInfo.setText(deliveryTypeModel.getDescription() != null ? deliveryTypeModel.getDescription() : "");
            binding.tvVehicalInfo.setText(deliveryTypeModel.getWeightCapacity() != null ? deliveryTypeModel.getWeightCapacity() : "");
            binding.tvVehiclePrice.setText(deliveryTypeModel.getPer_km_mile_charge());

            binding.relative1.setOnClickListener(v -> listener.onTypeSelect(getLayoutPosition(), deliveryTypeModel));

            if (deliveryTypeModel.isSelected()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    binding.relative1.setForeground(context.getDrawable(R.drawable.receiver_selector));
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    binding.relative1.setForeground(null);
            }
        }
    }
}
