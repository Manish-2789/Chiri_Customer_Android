package com.lotustechnologicalsolution.chiri.RecyclerViewAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.lotustechnologicalsolution.R;
import com.lotustechnologicalsolution.chiri.ModelClasses.ProductModel;
import com.lotustechnologicalsolution.databinding.ListItemBinding;

import java.util.ArrayList;

public class ProductCategoryAdapter extends RecyclerView.Adapter<ProductCategoryAdapter.MyViewHolder> {

    private ArrayList<ProductModel> productModels;
    private ProductCategoryItemSelectListener listener;

    public ProductCategoryAdapter(ArrayList<ProductModel> productModels, ProductCategoryItemSelectListener listener) {
        this.productModels = productModels;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.list_item, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ProductModel object = productModels.get(position);
        holder.setData(object);
    }

    @Override
    public int getItemCount() {
        return productModels != null ? productModels.size() : 0;
    }

    public interface ProductCategoryItemSelectListener {
        void onCategorySelect(int itemPosition, Object object);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ListItemBinding binding;
        private Context context;

        public MyViewHolder(@NonNull ListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            this.context = this.binding.getRoot().getContext();

            if (listener == null)
                listener = (ProductCategoryItemSelectListener) context;
        }

        public void setData(ProductModel productModel) {
            binding.expandedListItem.setText(productModel.getName());
            if (productModel.isSelected())
                binding.ivChecker.setImageResource(R.drawable.ic_checked);
            else
                binding.ivChecker.setImageResource(R.drawable.ic_unchecked);

            binding.rootLayout.setOnClickListener(v -> listener.onCategorySelect(getLayoutPosition(), productModel));
        }
    }
}
