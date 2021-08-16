package com.lotustechnologicalsolution.chiri.ModelClasses;

import android.os.Parcel;
import android.os.Parcelable;

public class DeliveryModeModel implements Parcelable {
    String modeID;
    String modeName;
    boolean isSelected;

    protected DeliveryModeModel(Parcel in) {
        modeID = in.readString();
        modeName = in.readString();
        isSelected = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(modeID);
        dest.writeString(modeName);
        dest.writeByte((byte) (isSelected ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DeliveryModeModel> CREATOR = new Creator<DeliveryModeModel>() {
        @Override
        public DeliveryModeModel createFromParcel(Parcel in) {
            return new DeliveryModeModel(in);
        }

        @Override
        public DeliveryModeModel[] newArray(int size) {
            return new DeliveryModeModel[size];
        }
    };

    public String getModeID() {
        return modeID;
    }

    public void setModeID(String modeID) {
        this.modeID = modeID;
    }

    public String getModeName() {
        return modeName;
    }

    public void setModeName(String modeName) {
        this.modeName = modeName;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public DeliveryModeModel() {
    }
}
