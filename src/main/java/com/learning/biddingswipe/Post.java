package com.learning.biddingswipe;

public class Post {
    private String itemName;
    private String description;
    private String baseBid;
    private String imageUrl;
    private String endTime;
    private String HighestBidder = "Username";
    private String BidAmount = "0000000";
    private String Key = "";

    public Post() {}

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBaseBid() {
        return baseBid;
    }

    public void setBaseBid(String baseBid) {
        this.baseBid = baseBid;
    }

    public String getHighestBidder() {
        return HighestBidder;
    }

    public void setHighestBidder(String highestBidder) {
        HighestBidder = highestBidder;
    }

    public String getBidAmount() {
        return BidAmount;
    }

    public void setBidAmount(String bidAmount) {
        BidAmount = bidAmount;
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }
}
