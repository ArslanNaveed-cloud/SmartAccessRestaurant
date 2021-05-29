package aust.fyp.pk.project.application.smartaccessrestaurant;

public class OrderDataModel {
    private String title;
    private String totalbill;
    private String quantity;
    public String date;
    public String tablenumber;
    public String status;

    public String getTitle() {
        return title;
    }

    public String getTotalbill() {
        return totalbill;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getDate() {
        return date;
    }

    public String getTablenumber() {
        return tablenumber;
    }

    public String getStatus() {
        return status;
    }

    public OrderDataModel(String title, String totalbill, String quantity, String date, String tablenumber, String status) {
        this.title = title;
        this.totalbill = totalbill;
        this.quantity = quantity;
        this.date = date;
        this.tablenumber = tablenumber;
        this.status = status;
    }
}
