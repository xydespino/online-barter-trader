package ca.dal.cs.onlinebartertrader.PostPages;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Contains logic for creating a Post object
 */
public class Post implements Serializable {
    private String itemName;
    private String itemDesc;
    private String quantity;
    private String quality;
    private String category;
    private String postType;
    private String sellerUsername;
    private String imagePath;
    private String status;
    private ArrayList<Double> location;
    private int distanceToUser;
    private long postId;


    public Post() {

    }

    //Builder
    public Post(String itemName, String itemDesc, String quantity, String quality, String category, String postType, String imagePath, String sellerUsername, ArrayList<Double> location) {
        this.itemName = itemName;
        this.itemDesc = itemDesc;
        this.quantity = quantity;
        this.quality = quality;
        this.category = category;
        this.postType = postType;
        this.sellerUsername = sellerUsername;
        this.imagePath = imagePath;
        this.location = location;
    }

    // Getters and Setters
    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemDesc() {
        return itemDesc;
    }

    public void setItemDesc(String itemDesc) {
        this.itemDesc = itemDesc;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPostType() {
        return postType;
    }

    public void setPostType(String postType) {
        this.postType = postType;
    }

    public String getSellerUsername() {
        return sellerUsername;
    }

    public void setSellerUsername(String sellerUsername) {
        this.sellerUsername = sellerUsername;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public ArrayList<Double> getLocation() {
        return location;
    }

    public void setLocation(ArrayList<Double> location) {
        this.location = location;
    }

    public int getDistanceToUser() {
        return this.distanceToUser;
    }

    public void setDistanceToUser(int distance) {
        this.distanceToUser = distance;
    }

    public long getPostId() {
        return postId;
    }

    public void setPostId(long postId) {
        this.postId = postId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
