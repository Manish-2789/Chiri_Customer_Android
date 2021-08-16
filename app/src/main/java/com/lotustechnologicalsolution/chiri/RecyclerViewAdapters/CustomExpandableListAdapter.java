package com.lotustechnologicalsolution.chiri.RecyclerViewAdapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lotustechnologicalsolution.R;
import com.lotustechnologicalsolution.chiri.ModelClasses.ProductModel;

import java.util.HashMap;
import java.util.List;

public class CustomExpandableListAdapter extends BaseExpandableListAdapter {
    private final Context context;
    private final List<String> expandableListTitle;
    private final HashMap<String, List<ProductModel>> expandableListDetail;

    public CustomExpandableListAdapter(Context context, List<String> expandableListTitle, HashMap<String, List<ProductModel>> expandableListDetail) {
        this.context = context;
        this.expandableListTitle = expandableListTitle;
        this.expandableListDetail = expandableListDetail;
    }

    @Override
    public int getGroupCount() {
        return this.expandableListTitle.size();
    }

    @Override
    public int getChildrenCount(int listPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
                .size();
    }

    @Override
    public Object getGroup(int listPosition) {
        return this.expandableListTitle.get(listPosition);
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
                .get(expandedListPosition);
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String listTitle = (String) getGroup(listPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_group, null);
        }

        TextView listTitleTextView = (TextView) convertView.findViewById(R.id.listTitle);
        TextView textViewIcon = (TextView) convertView.findViewById(R.id.tvIcon);

        listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(listTitle);

        if (isExpanded) {
            textViewIcon.setText("-");
        } else {
            textViewIcon.setText("+");
        }

        return convertView;
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final ProductModel expandedListText = (ProductModel) getChild(listPosition, expandedListPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_item, null);
        }

        LinearLayout linearLayout = convertView.findViewById(R.id.rootLayout);
        TextView expandedListTextView = convertView.findViewById(R.id.expandedListItem);
        ImageView imageView = convertView.findViewById(R.id.ivChecker);

        /*linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Drawable.ConstantState imgDrawable = imageView.getDrawable().getConstantState();
                Drawable.ConstantState tempDrawable = context.getResources().getDrawable(R.drawable.ic_checked).getConstantState();

                if (imgDrawable == tempDrawable)
                    imageView.setImageDrawable(parent.getResources().getDrawable(R.drawable.ic_unchecked));
                else
                    imageView.setImageDrawable(parent.getResources().getDrawable(R.drawable.ic_checked));
            }
        });*/

        expandedListTextView.setText(expandedListText.getName());
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }
}
