package fastfood.item;

public class SelectedItem extends Item {
    private int quantity;

    //Constructor
    public SelectedItem(int itemID, String name, String description, double price, int quantity, int calories) {
        super(itemID, name, description, price, calories);
        this.quantity = quantity;
    }

    //Getter and Setter
    public int getQuantity(){return this.quantity;}
    public void setQuantity(int newQuantity){this.quantity = newQuantity;}
}