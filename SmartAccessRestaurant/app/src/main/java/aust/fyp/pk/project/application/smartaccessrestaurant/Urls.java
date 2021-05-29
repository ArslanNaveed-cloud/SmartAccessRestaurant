package aust.fyp.pk.project.application.smartaccessrestaurant;

public class Urls {
    public static String IP = "192.168.100.9";
            public static String PORT = "3000";
            public static String DOMAIN1 = IP.trim() + ":" + PORT.trim();
           public static String DOMAIN = "http://"+IP.trim() + ":" + PORT.trim();

    public static String GET_FOODPRODUCTS = "http://" + DOMAIN1 + "/api/getfoodproducts";
    public static String PLACE_ORDER = "http://" + DOMAIN1 + "/api/placeorder";
    public static String GET_ORDERS = "http://" + DOMAIN1 + "/api/getorders";
    public static String SIGNUP_ACCOUNT = "http://" + DOMAIN1 + "/api/registeruser";
    public static String LOGIN = "http://" + DOMAIN1 + "/api/login";
    public static String FORGOTPASSWORD = "http://" + DOMAIN1 + "/api/forgotpassword";

    public static String CHANGENAME = "http://" + DOMAIN1 + "/api/changename";
    public static String CHANGEEMAIL = "http://" + DOMAIN1 + "/api/changeemail";
    public static String CAHNGEPHONE = "http://" + DOMAIN1 + "/api/changephone";
    public static String CHANGEPASSWORD = "http://" + DOMAIN1 + "/api/changepassword";
    public static String GENERRATE_BILL = "http://" + DOMAIN1 + "/api/generatebill";


}
