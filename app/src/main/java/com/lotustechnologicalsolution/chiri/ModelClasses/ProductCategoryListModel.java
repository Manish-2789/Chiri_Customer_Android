package com.lotustechnologicalsolution.chiri.ModelClasses;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class ProductCategoryListModel implements Parcelable {

    private String listTitle;
    private ArrayList<String> listOfProducts;

    public ProductCategoryListModel() {
    }

    protected ProductCategoryListModel(Parcel in) {
        listTitle = in.readString();
        listOfProducts = in.createStringArrayList();
    }

    public static final Creator<ProductCategoryListModel> CREATOR = new Creator<ProductCategoryListModel>() {
        @Override
        public ProductCategoryListModel createFromParcel(Parcel in) {
            return new ProductCategoryListModel(in);
        }

        @Override
        public ProductCategoryListModel[] newArray(int size) {
            return new ProductCategoryListModel[size];
        }
    };

    public String getListTitle() {
        return listTitle;
    }

    public void setListTitle(String listTitle) {
        this.listTitle = listTitle;
    }

    public ArrayList<String> getListOfProducts() {
        return listOfProducts;
    }

    public void setListOfProducts(ArrayList<String> listOfProducts) {
        this.listOfProducts = listOfProducts;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(listTitle);
        dest.writeStringList(listOfProducts);
    }
}
