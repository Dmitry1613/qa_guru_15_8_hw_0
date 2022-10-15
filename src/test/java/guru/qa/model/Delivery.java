package guru.qa.model;

import java.util.List;

public class Delivery {

    public int deliveryNumber;
    public String inboundTime;
    public String vendorName;
    public Items items;
    public List<String> warehouse;

    public static class Items {
        public int amount;
        public double price;
        public int productId;
        public String productName;
        public boolean isActive;
    }
}