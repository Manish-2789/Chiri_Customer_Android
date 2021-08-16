package com.lotustechnologicalsolution.chiri.ModelClasses;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class CardModel implements Parcelable {

    @SerializedName("card_number")
    private String cardNumber;

    @SerializedName("id")
    private String cardId;

    @SerializedName("card_name")
    private String brand;

    @SerializedName("card_cvv")
    private String cvv;

    @SerializedName("card_expiry_month")
    private String expiryMonth;

    @SerializedName("card_expiry_year")
    private String expiryYear;

    private boolean isSelected;

    public CardModel() {
    }

    protected CardModel(Parcel in) {
        cardNumber = in.readString();
        cardId = in.readString();
        brand = in.readString();
        cvv = in.readString();
        expiryMonth = in.readString();
        expiryYear = in.readString();
        isSelected = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(cardNumber);
        dest.writeString(cardId);
        dest.writeString(brand);
        dest.writeString(cvv);
        dest.writeString(expiryMonth);
        dest.writeString(expiryYear);
        dest.writeByte((byte) (isSelected ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CardModel> CREATOR = new Creator<CardModel>() {
        @Override
        public CardModel createFromParcel(Parcel in) {
            return new CardModel(in);
        }

        @Override
        public CardModel[] newArray(int size) {
            return new CardModel[size];
        }
    };

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getExpiryMonth() {
        return expiryMonth;
    }

    public void setExpiryMonth(String expiryMonth) {
        this.expiryMonth = expiryMonth;
    }

    public String getExpiryYear() {
        return expiryYear;
    }

    public void setExpiryYear(String expiryYear) {
        this.expiryYear = expiryYear;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

}
