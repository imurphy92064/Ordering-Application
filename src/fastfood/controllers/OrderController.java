package fastfood.controllers;

import fastfood.item.SelectedItem;
import fastfood.order.OrderInProgress;

public class OrderController {
    private OrderInProgress order;

    //Constructor
    public OrderController(String Cust){this.order = new OrderInProgress(Cust);}

   //Getter
    public OrderInProgress getOrder() {return this.order;}

    /*
    Description:
    Parameters:
    Local Variables:
    Returns:
     */
    public void modifyOrder(boolean op, SelectedItem item) {
        if(op){
            this.order.addItem(item);
        }
        else{
            this.order.removeItem(item);
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