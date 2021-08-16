package com.lotustechnologicalsolution.chiri.ModelClasses;

import android.os.Parcel;
import android.os.Parcelable;

public class DeliveryTypeModel implements Parcelable {

    String id;
    String title;
    String description;
    String per_km_mile_charge;
    String image;
    String modeID;
    String VehicleType;
    String weightCapacity;
    private boolean isSelected;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPer_km_mile_charge() {
        return per_km_mile_charge;
    }

    public void setPer_km_mile_charge(String per_km_mile_charge) {
        this.per_km_mile_charge = per_km_mile_charge;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getModeID() {
        return modeID;
    }

    public void setModeID(String modeID) {
        this.modeID = modeID;
    }

    public String getVehicleType() {
        return VehicleType;
    }

    public void setVehicleType(String vehicleType) {
        VehicleType = vehicleType;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getWeightCapacity() {
        return weightCapacity;
    }

    public void setWeightCapacity(String weightCapacity) {
        this.weightCapacity = weightCapacity;
    }


    public DeliveryTypeModel() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeString(this.per_km_mile_charge);
        dest.writeString(this.image);
        dest.writeString(this.modeID);
        dest.writeString(this.VehicleType);
        dest.writeString(this.weightCapacity);
        dest.writeByte(this.isSelected ? (byte) 1 : (byte) 0);
    }

    public void readFromParcel(Parcel source) {
        this.id = source.readString();
        this.title = source.readString();
        this.description = source.readString();
        this.per_km_mile_charge = source.readString();
        this.image = source.readString();
        this.modeID = source.readString();
        this.VehicleType = source.readString();
        this.weightCapacity = source.readString();
        this.isSelected = source.readByte() != 0;
    }

    protected DeliveryTypeModel(Parcel in) {
        this.id = in.readString();
        this.title = in.readString();
        this.description = in.readString();
        this.per_km_mile_charge = in.readString();
        this.image = in.readString();
        this.modeID = in.readString();
        this.VehicleType = in.readString();
        this.weightCapacity = in.readString();
        this.isSelected = in.readByte() != 0;
    }

    public static final Creator<DeliveryTypeModel> CREATOR = new Creator<DeliveryTypeModel>() {
        @Override
        public DeliveryTypeModel createFromParcel(Parcel source) {
            return new DeliveryTypeModel(source);
        }

        @Override
        public DeliveryTypeModel[] newArray(int size) {
            return new DeliveryTypeModel[size];
        }
    };
}
