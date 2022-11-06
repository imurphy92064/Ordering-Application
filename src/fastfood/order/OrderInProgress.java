package fastfood.order;


import fastfood.item.SelectedItem;

public class OrderInProgress extends Order {

    //Constructor
    public OrderInProgress(String custName) {super(custName);}

    /*
    Description: This function will add an item to the list if its not already in there. If the item is already in the
        list then we update the quantity of the item in the list and recalculate the total.
    Parameters:
        item - This is the SelectedItem object that represents the item that the customer wants to add to their order.
    Local Variables:
        success - Used return if the the method completed successfully or not.
        temp - Is a temp object that we used to get the item reference that is in the list.
    Returns: Returns a boolean value that indicates if the insertion was successful.
     */
    public boolean addItem(SelectedItem item){
        boolean success;
        if(this.itemList.contains(item)){
            System.out.println("Item is already in the order list.");

            SelectedItem temp = this.itemList.get(this.itemList.indexOf(item));//Get the item in the list that matches the parameter & save it in temp.
            int updatedQuantity = item.getQuantity() + temp.getQuantity();//ReCalculate the quantity of the item in the list.
            temp.setQuantity(updatedQuantity);//update the value.
            success = true;
            reCalculateTotal(true, temp.getPrice(), item.getQuantity());//Recalculate the total with the quantity from the parameter and the price of the item.
        }
        else{
            success = this.itemList.add(item);
            reCalculateTotal(true, item.getPrice(), item.getQuantity());//Recalculate the total with the quantity from the parameter and the price of the item.
        }
        return success;
    }

    /*
    Description: This fucntion will remove an item to the list if its in there. If the item isn't in the
        list then we report this to the console and return false. Otherwise we determine if the quantity of the item being
        removed is 0. If so we remove the item completely from the list and recalculate the total. If not zero keep in the list
        and recalculate the total.
    Parameters:
        item - This is the SelectedItem object that represents the item that the customer wants to remove from their order.
    Local Variables:
        success - Used return if the the method completed successfully or not.
        temp - Is a temp object that we used to get the item reference that is in the list.
    Returns: Returns a boolean value that indicates if the modification/deletion was successful.
     */
    public boolean removeItem(SelectedItem item){
        boolean success;
        if(this.itemList.contains(item)){
            SelectedItem temp = this.itemList.get(this.itemList.indexOf(item));//Get the item in the list that matches the parameter & save it in temp.
            int updatedQuantity = temp.getQuantity() - item.getQuantity();//ReCalculate the quantity of the item in the list.

            if(updatedQuantity <= 0){//If the quantity goes to 0 or below then we get rid of the item from the list
                success = (this.itemList.remove(this.itemList.indexOf(item)) != null);
                reCalculateTotal(false, temp.getPrice(), 0);//Recalculate the total with the quantity from the parameter and the price of the item.
            }
            else {
                temp.setQuantity(updatedQuantity);//update the value.
                success = true;
                reCalculateTotal(false, temp.getPrice(), item.getQuantity());//Recalculate the total with the quantity from the parameter and the price of the item.
            }
        }
        else{
            System.out.println("There is no "+item.getDescription()+" in the order list.");
            success = false;
        }
        return success;
    }

    /*
    Description: This function handles the recalculation of the order when a modification is made by the customer. The price and the quantity is used to determine
        the amount to add or subtract from the total.
    Parameters:
        op - This boolean value is used to determine if we are subtracting or adding to the total. False=Subtract; True=Add;
        price - This is the price of the item being added or subtracted.
        quantity - This is the quantity of the item being added or subtracted.
    Local Variables:
        newTotal - This holds the value of the product of price*quantity and is used to update the order total.
    Returns: None.
     */
    private void reCalculateTotal(boolean op, double price, int quantity) {
        if(op){//If op is true then we add 
            double subTotal = price*quantity;
            double curTotal = getTotal();

            subTotal += curTotal;
            setTotal(subTotal);
        }
        else{//If op is false we subtract
            double subTotal = price*quantity;
            double curTotal = getTotal();

            curTotal -= subTotal;
            setTotal(curTotal);
        }

    }
}