package aust.fyp.pk.project.application.smartaccessrestaurant;

public class FoodDataModel {
    private String title,price,description,coveriamge;

    public String getTitle() {
        return title;
    }

    public String getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public String getCoveriamge() {
        return coveriamge;
    }

    public FoodDataModel(String title, String price, String description, String coveriamge) {
        this.title = title;
        this.price = price;
        this.description = description;
        this.coveriamge = coveriamge;
    }
}
