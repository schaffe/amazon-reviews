package com.dzidzoiev.amazonreviews;

import java.io.Serializable;
import java.util.Objects;

public class ReviewItem implements Serializable {
    private final int id;
    private final String productId;
//    private final String userId;
    private final String profileName;
//    private final int score;
//    private final long time;
//    private final String summary;
    private final String text;

    public ReviewItem(int id, String productId, String profileName, String text) {
        this.id = id;
        this.productId = productId;
        this.profileName = profileName;
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public String getProductId() {
        return productId;
    }

//    public String getUserId() {
//        return userId;
//    }

    public String getProfileName() {
        return profileName;
    }

//    public int getScore() {
//        return score;
//    }
//
//    public long getTime() {
//        return time;
//    }
//
//    public String getSummary() {
//        return summary;
//    }

    public String getText() {
        return text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReviewItem item = (ReviewItem) o;
        return id == item.id &&
                Objects.equals(productId, item.productId) &&
                Objects.equals(profileName, item.profileName) &&
                Objects.equals(text, item.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, productId, profileName, text);
    }
}
