package fastfood.controllers;

import fastfood.item.SelectedItem;
import fastfood.order.OrderInProgress;

public class OrderController {
    private static OrderInProgress order;

    //Constructor
    public OrderController(String Cust){order = new OrderInProgress(Cust);}

   //Getter
    public OrderInProgress getOrder() {return order;}

    /*
    Description:
    Parameters:
    Local Variables:
    Returns:
     */
    public void modifyOrder(boolean op, SelectedItem item) {
        if(op){
            order.addItem(item);
        }
        else{
            order.removeItem(item);
        }
    }
    /*
    Description:
    Parameters:
    Local Variables:
    Returns:
     */
    public void finalizeOrder() {
        //Request that a receipt entity be added to the receipts table in the DB.

    }
}