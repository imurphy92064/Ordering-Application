package fastfood.item;

abstract class Item {
    protected String itemName;
    protected String description;
    protected double price;
    protected int calories;
    protected int itemID;

    protected Item(int itemID, String itemName, String description, double price, int calories) {
        this.itemID = itemID;
        this.itemName = itemName;
        this.description = description;
        this.price = price;
        this.calories = calories;
    }

    public int getItemID(){return this.itemID;}
    public String getDescription(){return this.description;}
    public double getPrice(){return this.price;}
    public int getCalories(){return this.calories;}
    public String getItemName() {return itemName;}

}













































//public class Item {
//    public String description;
//    public int price;
//    public int calories;
//    public int itemID;
//
//    public Item(String description, int price, int calories, int itemID) {
//        this.description = description;
//        this.price = price;
//        this.calories = calories;
//        this.itemID = itemID;
//    }
//
//    public String getDescription() {
//        return description;
//    }
//
//    public void setDescription(String description) {
//        this.description = description;
//    }
//
//    public int getPrice() {
//        return price;
//    }
//
//    public void setPrice(int price) {
//        this.price = price;
//    }
//
//    public int getCalories() {
//        return calories;
//    }
//
//    public void setCalories(int calories) {
//        this.calories = calories;
//    }
//
//    public int getItemID() {
//        return itemID;
//    }
//
//    public void setItemID(int itemID) {
//        this.itemID = itemID;
//    }
//}
