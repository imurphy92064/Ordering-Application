package fastfood.controllers;


import fastfood.item.ItemFactory;
import fastfood.item.MenuItem;
import fastfood.item.SelectedItem;
import fastfood.order.OrderInProgress;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.Vector;

/*
11/8/2022 Changes:
    1. Changed the statement objects to prepared statement objects.
    2. Changed and updates sql statements in,
        - getReceiptInfo()
        - insertReceipt()
        - insertReceiptInfo()
    3. Added a getMostRecentReceipt() method that returns the most recent receipt entry.
11/11/2022 Changes:
    1. Added two more boolean value to the insertReceipt() method to store the outcome of the insertReceiptInfo() method
        so that better debugging output can be given. The other boolean value added is to determine if both operations are successful.
12/04/2022 Changes:
    1. Added function headers for newly created functions.
    2. Created 2 new functions to query information about completed receipts. GetReceiptList returns a result set that
        contains information about selected items in that specific receipt. GetReceipt returns a result set that
        contains information about the receipt specified in the parameters.
 */

public class DBController {
    //Properties
    private Connection con;

    /*
    Description: This is the constructor for the DBController.
    Parameters: None.
    Local Variables: None.
    Returns: An instance of this class.
     */
    public DBController(){
        try{
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/fastfood", "root", "");
            if(!con.isValid(0)){
                System.out.println("Database Connection failed.\n");
            }else{
                System.out.println("Database Connection successful.\n");
            }
        }catch(Exception e){System.out.println(e.getMessage());}
    }

    /*
    Description: Helper function to see if the db connection is valid still.
    Parameters: None.
    Local Variables:
        valid - This is the value of the state of the connection. True, DB has valid connection; False, DB has invalid connection.
    Returns: The status of the DB connection.
     */
    public boolean dbConIsValid(){
        boolean valid = false;
        try {
            valid = con.isValid(0);
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        return valid;
    }

    /*
    Description: This method send a query to backend DB to get the current
        up-to-date menu to send to the front end UI
    Parameters: None
    Local Variables
        curMenu - The list that will contain the menu items from the DB.
        rs - The result set object that contains the output from the query.
    Returns: A Vector<Item> object that has all the items in the menu.
     */
    public Vector<MenuItem> getMenuItems(){
        Vector<MenuItem> curMenu = new Vector<>(10, 5);
        PreparedStatement statement;
        ResultSet rs;

        try{
            //Create the statement and send the query to the backend DB
            statement = con.prepareStatement("SELECT * FROM `menuitems`");
            rs = statement.executeQuery();

            //Populating the vector to return to the frontend UI
            while(rs.next()){
                MenuItem item = (MenuItem) ItemFactory.createItem("menu item", rs, null);
                curMenu.add(item);
            }
        }catch(Exception e){System.out.println(e.getMessage());}

        return curMenu;
    }

    /*
    Description: This method will insert a receipt into the backend DB when the order is completed.
        Otherwise, an exception was thrown.
    Parameters:
        order - The Order object that the customer or employee has made.
        empId - If an employee made the order it will have their empId associated with it.
    Local Variables:
        fullSuccess - Is the boolean value when you 'AND' the 'receiptInsertSuccess' && 'receiptInfoInsertSuccess'
        receiptInsertSuccess - Hold the value of the outcome of the insertReceipt() method.
        receiptInfoInsertSuccess - Hold the value of the outcome of the insertReceiptInfo() method.
        newReceipt - The receipt object that we use to insert into the DB.
    Returns: Returns a boolean dependent on if the method inserted successfully.
     */
    public boolean insertReceipt(@NotNull OrderInProgress order) {
        boolean fullSuccess = false;
        boolean receiptInsertSuccess = true;
        boolean receiptInfoInsertSuccess = true;
        PreparedStatement statement;

        if(!order.isCompleted()){//If the order is not completed for some reason when reaching this point. We return false
            System.out.println("Order is not marked completed. Canceling insert request.");
            return fullSuccess;
        }

        try{
            statement = con.prepareStatement("INSERT INTO `receipts` (`OrderNum`,`CustomerName`, `Total`, `Timestamp`) VALUES (NULL, ?, ?, current_timestamp())");
            statement.setString(1, order.getCustName());
            statement.setDouble(2, order.getTotal());
            statement.executeUpdate();


            //Get the most recently created receipt. (Use the max(emp_id) in a query).
            int receiptNum = getMostRecentReceipt();
            //Check to see if we got a good receiptNum.
            if(receiptNum == -1){
                System.out.println("Something Bad happened.\ngetMostReceipt returned a -1.");
                return false;
            }
            //Start the item list
            receiptInfoInsertSuccess = insertReceiptInfo(receiptNum, order.getItemList());

        }catch(SQLException ex){
            System.out.println(ex.getMessage());
            receiptInsertSuccess = false;
        }

        if(receiptInsertSuccess && receiptInfoInsertSuccess){
            System.out.println("Successful Receipt/Receipt_Info Insertion.");
            fullSuccess = true;
        }else{
            System.out.println("Unsuccessful Receipt insertion or Receipt_Info insertion.");
        }

        return fullSuccess;
    }

    /*
    Description: This method is executed after an order has been successfully added to the DB. This will go through
        all the items in the order and add them to the receipt_info table.
    Parameters:
        orderID - The ID of the order the orderItems list belongs to.
        selectedItems - A list of items that have been ordered, these objects have a quantity associated with them.
    Local Variables:
        receiptInfoInsertSuccess - Hold the value of the outcome of the method.
    Returns: A boolean dependent on if the insertions were successful.
     */
    private boolean insertReceiptInfo(int orderID, Vector<SelectedItem> selectedItems){
        boolean receiptInfoInsertSuccess = true;
        PreparedStatement statement;

        try{
            String query;
            statement = con.prepareStatement("INSERT INTO `receipt_info` (`OrderNum`, `ItemID`, `Quantity`) VALUES (?, ?, ?)");
            statement.setInt(1, orderID);
            for(SelectedItem item : selectedItems){
                statement.setInt(2, item.getItemID());
                statement.setInt(3, item.getQuantity());
                statement.executeUpdate();
            }
        }catch(SQLException ex){
            System.out.println(ex.getMessage());
            receiptInfoInsertSuccess = false;
        }catch(Exception e){
            System.out.println(e.getMessage());
        }

        if(receiptInfoInsertSuccess){
            System.out.println("Successful Receipt_Info Insertion.");
        }else{
            System.out.println("Unsuccessful Receipt_Info Insertion.");
        }

        return receiptInfoInsertSuccess;
    }

    /*
    Description: This method queries the database for the most recently created entry in the receipts table.
        Since we are using Auto_Increment the most recently create receipt is the one with the highest number.
    Parameters: None.
    Local Variables:
        receiptNum - The variable where we store the result from the query.
        statement - The preparedStatement that we use to pre-compile the sql statement.
        rs - The result from the query just executed.
    Returns: An integer that represents the receipt number of the most recently created receipt entry.
     */
    private int getMostRecentReceipt(){
        int receiptNum = -1;
        PreparedStatement statement;
        ResultSet rs;

        try{
            statement = con.prepareStatement("SELECT MAX(`OrderNum`) FROM `receipts`");
            rs = statement.executeQuery();

            if(!rs.next()){return receiptNum;}//if there are no rows in the result we return with a -1.

            receiptNum = rs.getInt(1);
        }
        catch(SQLException ex){
            System.out.println(ex.getMessage());
        }

        return receiptNum;
    }

    /*
    Description: This returns information about items on the receipt.
    Parameters:
        orderNum - The Id of the order that we want to query.
    Local Variables:
        statement - The prepared statement object that pre-compiles the sql statement.
        rs - The result of the statement being executed by the DB.
    Returns: A ResultSet containing the information needed to created selected items queried.
     */
    public ResultSet getReceiptList(int orderNum){
        PreparedStatement statement;
        ResultSet rs = null;

        try{//Need more Selected Item columns
            statement = con.prepareStatement("SELECT receipt_info.ItemID, menuitems.ItemName, menuitems.Description, menuitems.Price, receipt_info.Quantity, menuitems.Calories FROM `receipt_info`, `menuitems` WHERE OrderNum = ? AND menuitems.ItemID = receipt_info.ItemID;");
            statement.setInt(1, orderNum);
            rs = statement.executeQuery();
        }catch(Exception e){
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return rs;
    }

    /*
    Description: This returns information about the receipt.
    Parameters:
        orderNum - The ID of the order that we want to query.
    Local Variables:
        statement - The prepared statement object that pre-compiles the sql statement.
        rs - The result of the statement being executed by the DB.
    Returns: A ResultSet containing the information needed to create the receipt queried.
     */
    public ResultSet getReceipt(int orderNum){
        PreparedStatement statement;
        ResultSet rs = null;

        try{//Need more Selected Item columns
            statement = con.prepareStatement("SELECT `OrderNum`, `CustomerName`, `Total`, `Timestamp` FROM `receipt` WHERE OrderNum = ?");
            statement.setInt(1, orderNum);
            rs = statement.executeQuery();
        }catch(Exception e){
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return rs;
    }
}