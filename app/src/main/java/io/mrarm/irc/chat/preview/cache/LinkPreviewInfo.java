package io.mrarm.irc.chat.preview.cache;

import android.graphics.Bitmap;

import java.util.Date;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "link_previews")
public class LinkPreviewInfo {

    public static int TYPE_WEBSITE = 0;
    public static int TYPE_IMAGE = 1;

    @PrimaryKey
    @ColumnInfo(name = "url")
    @NonNull
    private String mUrl;

    @ColumnInfo(name = "type")
    private int mType;

    @ColumnInfo(name = "title")
    private String mTitle;

    @ColumnInfo(name = "desc")
    private String mDescription;

    @ColumnInfo(name = "image")
    private String mImageUrl;

    @ColumnInfo(name = "last_used")
    private long mLastUsed;


    private transient Bitmap mImage;
    private transient int mImageSourceWidth;
    private transient int mImageSourceHeight;

    public LinkPreviewInfo() {
    }

    private LinkPreviewInfo(@NonNull String url, int type, String title, String description,
                            String imageUrl, Bitmap image) {
        mUrl = url;
        mType = type;
        mTitle = title;
        mDescription = description;
        mImageUrl = imageUrl;
        mImage = image;
        updateLastUsed();
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    public int getType() {
        return mType;
    }

    public void setType(int mType) {
        this.mType = mType;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }

    public Bitmap getImage() {
        return mImage;
    }

    public void setImage(Bitmap image, int srcW, int srcH) {
        mImage = image;
        mImageSourceWidth = srcW;
        mImageSourceHeight = srcH;
    }

    public int getImageSourceWidth() {
        return mImageSourceWidth;
    }

    public int getImageSourceHeight() {
        return mImageSourceHeight;
    }

    public long getLastUsed() {
        return mLastUsed;
    }

    public void setLastUsed(long value) {
        mLastUsed = value;
    }

    public void updateLastUsed() {
        mLastUsed = new Date().getTime();
    }

    public static LinkPreviewInfo fromWebsite(String url, String title, String description,
                                              String imageUrl, Bitmap image) {
        return new LinkPreviewInfo(url, TYPE_WEBSITE, title, description, imageUrl, image);
    }

    public static LinkPreviewInfo fromImage(String url, Bitmap image) {
        return new LinkPreviewInfo(url, TYPE_IMAGE, null, null, url, image);
    }

}
