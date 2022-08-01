package com.example.android.pocketartapp.ui;

import android.os.Parcel;
import android.os.Parcelable;

public class Painting implements Parcelable {
    public static final Creator<Painting> CREATOR = new Creator<Painting>() {

        @Override
        public Painting createFromParcel(Parcel in) {
            return new Painting(in);
        }

        @Override
        public Painting[] newArray(int size) {
            return new Painting[size];
        }
    };


    private int mId;
    private String mTitle;
    private String mAuthor;
    private String mBornDied;
    private String mDate;
    private String mTechnique;
    private String mLocation;
    private String mUrl;
    private String mForm;
    private String mSchool;
    private String mDescription;
    private String mTimeframe;
    private String mType;

    protected Painting(Parcel in) {
        mId = in.readInt();
        mTitle = in.readString();
        mAuthor = in.readString();
        mBornDied = in.readString();
        mDate = in.readString();
        mTechnique = in.readString();
        mLocation = in.readString();
        mUrl = in.readString();
        mForm = in.readString();
        mSchool = in.readString();
        mDescription = in.readString();
        mTimeframe = in.readString();
        mType = in.readString();
    }

    public Painting(int id, String title, String author, String bornDied, String date,
                    String technique, String location, String url, String form, String type,
                    String school, String description, String timeframe) {
        mId = id;
        mTitle = title;
        mAuthor = author;
        mBornDied = bornDied;
        mDate = date;
        mTechnique = technique;
        mLocation = location;
        mUrl = url;
        mForm = form;
        mSchool = school;
        mDescription = description;
        mTimeframe = timeframe;
        mType = type;
    }

    // used when data is retrieved from Firebase Realtime Database
    public Painting() {
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String author) {
        mAuthor = author;
    }

    public String getBornDied() {
        return mBornDied;
    }

    public void setBornDied(String bornDied) {
        mBornDied = bornDied;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public String getTechnique() {
        return mTechnique;
    }

    public void setTechnique(String technique) {
        mTechnique = technique;
    }

    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String location) {
        mLocation = location;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public String getForm() {
        return mForm;
    }

    public void setForm(String form) {
        mForm = form;
    }

    public String getSchool() {
        return mSchool;
    }

    public void setSchool(String school) {
        mSchool = school;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getTimeframe() {
        return mTimeframe;
    }

    public void setTimeframe(String timeFrame) {
        mTimeframe = timeFrame;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(mId);
        parcel.writeString(mTitle);
        parcel.writeString(mAuthor);
        parcel.writeString(mBornDied);
        parcel.writeString(mDate);
        parcel.writeString(mTechnique);
        parcel.writeString(mLocation);
        parcel.writeString(mUrl);
        parcel.writeString(mForm);
        parcel.writeString(mSchool);
        parcel.writeString(mDescription);
        parcel.writeString(mTimeframe);
        parcel.writeString(mType);

    }
}
