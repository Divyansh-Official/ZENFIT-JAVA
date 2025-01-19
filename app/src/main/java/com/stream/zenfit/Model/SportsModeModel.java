package com.stream.zenfit.Model;

public class SportsModeModel {
    private String sportsName;
    private String iconLink;
    private String imageLink;
    private String ytLinkLessWeight;
    private String ytLinkMoreWeight;
    private String ytLinkLessHeight;
    private String ytLinkMoreHeight;
    private String ytLinkLessCalories;
    private String ytLinkMoreCalories;

    // Constructor
    public SportsModeModel(String sportsName, String iconLink, String imageLink, String ytLinkLessWeight,
                           String ytLinkMoreWeight, String ytLinkLessHeight, String ytLinkMoreHeight,
                           String ytLinkLessCalories, String ytLinkMoreCalories) {
        this.sportsName = sportsName;
        this.iconLink = iconLink;
        this.imageLink = imageLink;
        this.ytLinkLessWeight = ytLinkLessWeight;
        this.ytLinkMoreWeight = ytLinkMoreWeight;
        this.ytLinkLessHeight = ytLinkLessHeight;
        this.ytLinkMoreHeight = ytLinkMoreHeight;
        this.ytLinkLessCalories = ytLinkLessCalories;
        this.ytLinkMoreCalories = ytLinkMoreCalories;
    }

    // Getters
    public String getSportsName() { return sportsName; }
    public String getIconLink() { return iconLink; }
    public String getImageLink() { return imageLink; }
    public String getYtLinkLessWeight() { return ytLinkLessWeight; }
    public String getYtLinkMoreWeight() { return ytLinkMoreWeight; }
    public String getYtLinkLessHeight() { return ytLinkLessHeight; }
    public String getYtLinkMoreHeight() { return ytLinkMoreHeight; }
    public String getYtLinkLessCalories() { return ytLinkLessCalories; }
    public String getYtLinkMoreCalories() { return ytLinkMoreCalories; }
}
