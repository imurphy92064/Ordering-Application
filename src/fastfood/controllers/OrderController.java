package fastfood.controllers;

import fastfood.item.MenuItem;
import fastfood.item.SelectedItem;
import fastfood.order.OrderFactory;
import fastfood.order.OrderInProgress;
import fastfood.controllers.DBController;

import java.util.Vector;
/*
11/11/2022 Changes:
    1. Implemented the modifyOrder() and finalizeOrder() methods
    2. I defined how to complete the order (Since we arent implementing payment gateways we just
        change a boolean value('completed') to true)
    3. Connected the OrderController and the DBController classes to initiate the insertion of a finalized order as a receipt.

12/04/2022 Changes:
    1. Added function headers for newly created functions.
 */

public class OrderController {
    private static OrderInProgress order;
    private DBController dbc;

    /*
    Description: This is a constructor for the OrderController.
    Parameters:
        db_ - This is the DB Controller object that connects to the DB and allows the app to send data to the DB.
        Cust - This is the name of the customer for this order.
    Local Variables: None.
    Returns: An instance of this class.
     */
    public OrderController(String Cust, DBController dbc_){
        order = (OrderInProgress) OrderFactory.createOrder("order in progress", null, null, Cust);
        dbc = dbc_;
    }

    /*
    Description: This is a constructor for the OrderController. Sets the customer name to null.
    Parameters:
        db_ - This is the DB Controller object that connects to the DB and allows the app to send data to the DB.
    Local Variables: None.
    Returns: An instance of this class.
     */
    public OrderController(DBController dbc_){
        order = (OrderInProgress) OrderFactory.createOrder("order in progress", null, null, null);
        dbc = dbc_;
    }

    //Getter for order member variable.
    public OrderInProgress getOrder() {return this.order;}

    //Getter for the order's item list.
    public Vector<SelectedItem> getOrderList(){
        return order.getItemList();
    }

    //Getter for the order's current total.
    public double  getOrderTotal() {return order.getTotal();}

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
    public void modifyOrder(boolean op, MenuItem mItem, SelectedItem sItem) {
        if(op){
            order.addItem(mItem);
        }
        else{
            order.removeItem(sItem);
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