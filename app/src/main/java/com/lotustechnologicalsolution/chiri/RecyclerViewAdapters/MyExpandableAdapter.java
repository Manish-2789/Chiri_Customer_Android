package com.lotustechnologicalsolution.chiri.RecyclerViewAdapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import androidx.databinding.DataBindingUtil;

import com.lotustechnologicalsolution.R;
import com.lotustechnologicalsolution.chiri.ModelClasses.ProductCategoryListModel;
import com.lotustechnologicalsolution.databinding.ListGroupBinding;
import com.lotustechnologicalsolution.databinding.ListItemBinding;

import java.util.ArrayList;

public class MyExpandableAdapter extends BaseExpandableListAdapter {

    private final Context context;
    private final ArrayList<ProductCategoryListModel> productCategoryListModels;

    public MyExpandableAdapter(Context context, ArrayList<ProductCategoryListModel> productCategoryListModels) {
        this.context = context;
        this.productCategoryListModels = productCategoryListModels;
    }

    @Override
    public int getGroupCount() {
        return productCategoryListModels != null ? productCategoryListModels.size() : 0;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        ArrayList<String> listOfProducts = productCategoryListModels.get(groupPosition).getListOfProducts();
        return listOfProducts.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return productCategoryListModels.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        ArrayList<String> listOfProducts = productCategoryListModels.get(groupPosition).getListOfProducts();
        return listOfProducts.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        ProductCategoryListModel productCategory = (ProductCategoryListModel) getGroup(groupPosition);

        ListGroupBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.list_group, parent, false);
        convertView = binding.getRoot();

        binding.listTitle.setText(productCategory.getListTitle());
        if (isExpanded)
            binding.tvIcon.setText("-");
        else
            binding.tvIcon.setText("+");

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String productTitle = (String) getChild(groupPosition, childPosition);

        ListItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.list_item, parent, false);
        convertView = binding.getRoot();

        binding.expandedListItem.setText(productTitle);

        binding.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Drawable.ConstantState imgDrawable = binding.ivChecker.getDrawable().getConstantState();
                Drawable.ConstantState tempDrawable = context.getResources().getDrawable(R.drawable.ic_checked).getConstantState();

                if (imgDrawable == tempDrawable)
                    binding.ivChecker.setImageDrawable(parent.getResources().getDrawable(R.drawable.ic_unchecked));
                else
                    binding.ivChecker.setImageDrawable(parent.getResources().getDrawable(R.drawable.ic_checked));
            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
