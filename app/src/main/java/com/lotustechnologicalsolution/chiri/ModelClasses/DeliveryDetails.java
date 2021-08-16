package com.lotustechnologicalsolution.chiri.ModelClasses;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class DeliveryDetails implements Parcelable, Serializable {

    private String orderDescription;
    private String parcelImagePath;
    private String parcelImageName;
    private double fromLatitude;
    private double fromLongitude;
    private double toLatitude;
    private double toLongitude;
    private String fromAddress;
    private String toAddress;
    private String receiverContactName;
    private String receiverContactNumber;
    private int weight;
    private String sizeOfPacket;
    private boolean isFragileParcel;
    private int deliverMode;
    private String deliveryTime;

    public DeliveryDetails() {}

    protected DeliveryDetails(Parcel in) {
        orderDescription = in.readString();
        parcelImagePath = in.readString();
        parcelImageName = in.readString();
        fromLatitude = in.readDouble();
        fromLongitude = in.readDouble();
        toLatitude = in.readDouble();
        toLongitude = in.readDouble();
        fromAddress = in.readString();
        toAddress = in.readString();
        receiverContactName = in.readString();
        receiverContactNumber = in.readString();
        weight = in.readInt();
        sizeOfPacket = in.readString();
        isFragileParcel = in.readByte() != 0;
        deliverMode = in.readInt();
        deliveryTime = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(orderDescription);
        dest.writeString(parcelImagePath);
        dest.writeString(parcelImageName);
        dest.writeDouble(fromLatitude);
        dest.writeDouble(fromLongitude);
        dest.writeDouble(toLatitude);
        dest.writeDouble(toLongitude);
        dest.writeString(fromAddress);
        dest.writeString(toAddress);
        dest.writeString(receiverContactName);
        dest.writeString(receiverContactNumber);
        dest.writeInt(weight);
        dest.writeString(sizeOfPacket);
        dest.writeByte((byte) (isFragileParcel ? 1 : 0));
        dest.writeInt(deliverMode);
        dest.writeString(deliveryTime);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DeliveryDetails> CREATOR = new Creator<DeliveryDetails>() {
        @Override
        public DeliveryDetails createFromParcel(Parcel in) {
            return new DeliveryDetails(in);
        }

        @Override
        public DeliveryDetails[] newArray(int size) {
            return new DeliveryDetails[size];
        }
    };

    public String getOrderDescription() {
        return orderDescription;
    }

    public void setOrderDescription(String orderDescription) {
        this.orderDescription = orderDescription;
    }

    public String getParcelImagePath() {
        return parcelImagePath;
    }

    public void setParcelImagePath(String parcelImagePath) {
        this.parcelImagePath = parcelImagePath;
    }

    public String getParcelImageName() {
        return parcelImageName;
    }

    public void setParcelImageName(String parcelImageName) {
        this.parcelImageName = parcelImageName;
    }

    public double getFromLatitude() {
        return fromLatitude;
    }

    public void setFromLatitude(double fromLatitude) {
        this.fromLatitude = fromLatitude;
    }

    public double getFromLongitude() {
        return fromLongitude;
    }

    public void setFromLongitude(double fromLongitude) {
        this.fromLongitude = fromLongitude;
    }

    public double getToLatitude() {
        return toLatitude;
    }

    public void setToLatitude(double toLatitude) {
        this.toLatitude = toLatitude;
    }

    public double getToLongitude() {
        return toLongitude;
    }

    public void setToLongitude(double toLongitude) {
        this.toLongitude = toLongitude;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public String getReceiverContactName() {
        return receiverContactName;
    }

    public void setReceiverContactName(String receiverContactName) {
        this.receiverContactName = receiverContactName;
    }

    public String getReceiverContactNumber() {
        return receiverContactNumber;
    }

    public void setReceiverContactNumber(String receiverContactNumber) {
        this.receiverContactNumber = receiverContactNumber;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getSizeOfPacket() {
        return sizeOfPacket;
    }

    public void setSizeOfPacket(String sizeOfPacket) {
        this.sizeOfPacket = sizeOfPacket;
    }

    public boolean isFragileParcel() {
        return isFragileParcel;
    }

    public void setFragileParcel(boolean fragileParcel) {
        isFragileParcel = fragileParcel;
    }

    public int getDeliverMode() {
        return deliverMode;
    }

    public void setDeliverMode(int deliverMode) {
        this.deliverMode = deliverMode;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }
}
