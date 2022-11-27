package fastfood.controllers;

import fastfood.item.MenuItem;
import fastfood.item.SelectedItem;
import fastfood.order.OrderInProgress;
import fastfood.controllers.DBController;
/*
11/11/2022 Changes:
    1. Implemented the modifyOrder() and finalizeOrder() methods
    2. I defined how to complete the order (Since we arent implementing payment gateways we just
        change a boolean value('completed') to true)
    3. Connected the OrderController and the DBController classes to initiate the insertion of a finalized order as a receipt.
 */

/*
11/11/2022 Changes:
    1. Implemented the modifyOrder() and finalizeOrder() methods
    2. I defined how to complete the order (Since we arent implementing payment gateways we just
        change a boolean value('completed') to true)
    3. Connected the OrderController and the DBController classes to initiate the insertion of a finalized order as a receipt.
 */

public class OrderController {
    private static OrderInProgress order;
    private DBController dbc;

    //Constructor
    public OrderController(String Cust){
        order = new OrderInProgress(Cust);
        dbc = new DBController();
    }

   //Getter
    public OrderInProgress getOrder() {return order;}


    //Methods
    /*
    Description: This method will modify the order based on the input from the user. The user can either add an item or remove an
        item to modify the order
    Parameters:
        op - This is the operation to be performed on the customer's order.
        item - This is the item that needs to be removed or added to the order.
    Local Variables: None.
    Returns: Nothing.
     */
    public void modifyOrder(boolean op, MenuItem Mitem, SelectedItem Sitem) {
        if(op){
            order.addItem(Mitem);
        }
        else{
            order.removeItem(Sitem);
        }
    }
    /*
    Description: This method will change the state of the OrderInProgress object to indicate that it's ready to be completed.
    Parameters: None.
    Local Variables: None.
    Returns: This returns the boolean value representing the success of the insertReceipt() and insertReceiptInfo() methods.
     */
    public boolean finalizeOrder() {
        //Request that a receipt entity be added to the receipts table in the DB.
        order.setCompleted(true);
        return dbc.insertReceipt(this.order);
    }
}