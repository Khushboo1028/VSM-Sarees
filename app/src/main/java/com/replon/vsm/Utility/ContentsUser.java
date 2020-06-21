package com.replon.vsm.Utility;

import android.os.Parcel;
import android.os.Parcelable;

public class ContentsUser implements Parcelable {
    String name;
    String phone;
    String company_name;
    String city;
    String gst_no;
    String dealer;
    String state;

    public ContentsUser(String name, String phone, String company_name, String city, String state, String gst_no, String dealer) {
        this.name = name;
        this.phone = phone;
        this.company_name = company_name;
        this.city = city;
        this.state=state;
        this.gst_no = gst_no;
        this.dealer=dealer;
    }

    protected ContentsUser(Parcel in) {
        name = in.readString();
        phone = in.readString();
        company_name = in.readString();
        city = in.readString();
        state=in.readString();
        gst_no = in.readString();
        dealer=in.readString();
    }

    public static final Creator<ContentsUser> CREATOR = new Creator<ContentsUser>() {
        @Override
        public ContentsUser createFromParcel(Parcel in) {
            return new ContentsUser(in);
        }

        @Override
        public ContentsUser[] newArray(int size) {
            return new ContentsUser[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getGst_no() {
        return gst_no;
    }

    public void setGst_no(String gst_no) {
        this.gst_no = gst_no;
    }

    public String getDealer() {
        return dealer;
    }

    public void setDealer(String dealer) {
        this.dealer = dealer;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(phone);
        dest.writeString(company_name);
        dest.writeString(city);
        dest.writeString(state);
        dest.writeString(gst_no);
        dest.writeString(dealer);
    }
}
