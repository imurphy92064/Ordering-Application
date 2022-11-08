package fastfood.controllers;


import fastfood.item.MenuItem;
import fastfood.item.SelectedItem;
import fastfood.order.OrderInProgress;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.Vector;

/*
DB Table Structure Reference

Contains(OrderNum, ItemID, Quantity)
MenuItem(ItemID, Description, Price, Calories)
Order(OrderNum, CustomerName, Total, Timestamp)

Table(1, 2, 3, ...) The Column Index Sequence
 */


/*
TODO:
    1. Determine how we want to check whether or not an OrderInProgress obj is completed or not before creating a
        receipt object & inserting it into the DB.
 */

/*
11/8/2022 Changes:
    1. Changed the statement objects to prepared statement objects.
    2. Changed and updates sql statements in,
        - getReceiptInfo()
        - insertReceipt()
        - insertReceiptInfo()
    3. Added a getMostRecentReceipt() method that returns the most recent receipt entry.
 */

public class DBController {
    //Properties
    private Connection con;

    /*
    Parameters: None
    Description: Class constructor
    Returns: Nothing
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
        try{
            //Create the statement and send the query to the backend DB
            statement = con.prepareStatement("SELECT * FROM `menuitems`");
            ResultSet rs = statement.executeQuery();

            //Populating the vector to return to the frontend UI
            while(rs.next()){
                MenuItem item = new MenuItem(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getInt(4), rs.getInt(5));
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
        successful - Hold the value of the outcome of the method.
        newReceipt - The receipt object that we use to insert into the DB.
    Returns: Returns a boolean dependent on if the method inserted successfully.
     */
    public boolean insertReceipt(@NotNull OrderInProgress order) {
        boolean successful = true;
        PreparedStatement statement;
        /*
        ToDo:
           This checks if the Order has been paid for. If not it will return the control back to front end
        */
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
            successful = insertReceiptInfo(receiptNum, order.getItemList());

        }catch(SQLException ex){
            System.out.println(ex.getMessage());
            successful = false;
        }

        if(successful){
            System.out.println("Successful Receipt Insertion.");
        }else{
            System.out.println("Unsuccessful Receipt Insertion.");
        }

        return successful;
    }

    /*
    Description: This method is executed after an order has been successfully added to the DB. This will go through
        all the items in the order and add them to the receipt_info table.
    Parameters:
        orderID - The ID of the order the orderItems list belongs to.
        selectedItems - A list of items that have been ordered, these objects have a quantity associated with them.
    Local Variables:
        successful - Hold the value of the outcome of the method.
    Returns: A boolean dependent on if the insertions were successful.
     */
    private boolean insertReceiptInfo(int orderID, Vector<SelectedItem> selectedItems){
        boolean successful = true;
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
            successful = false;
        }catch(Exception e){
            System.out.println(e.getMessage());
        }

        if(successful){
            System.out.println("Successful Receipt_Info Insertion.");
        }else{
            System.out.println("Unsuccessful Receipt_Info Insertion.");
        }

        return successful;
    }

    /*
    Description: This returns a quantity of the item in the order
    Parameters:
        orderNum - The Id of the order that we want to query
        itemId - The Item we want to know the quantity for.
    Local Variables:
        list - The list that will hold the itemID and quantity of the item, acquired from the query.
    Returns: A Vector of Strings each element in the list is two numbers separated by a comma no spaces.
        -The First number is the ItemID
        -The Second number is the quantity
     */
    public Vector<String> getReceiptInfo(int orderNum){
        Vector<String> list = new Vector<>();
        PreparedStatement statement;

        try{
            statement = con.prepareStatement("SELECT `receipt_info`.`ItemID`, `menuitems`.`ItemName`, `receipt_info`.`Quantity` FROM `receipt_info`, `menuitems` WHERE `receipt_info`.`ItemID` = `menuitems`.`ItemID` AND `receipt_info`.`OrderNum`= ?");
            statement.setInt(1, orderNum);
            ResultSet rs = statement.executeQuery();

            while(rs.next()){
                String temp = rs.getString(1)+","+rs.getString(2)+","+rs.getString(3);
                list.add(temp);
            }
        }catch(SQLException ex){
            System.out.println(ex.getMessage());
        }

        return list;
    }

    /*
    Description: This method returns the total of the receipt number passed as a parameter.
    Parameters:
        orderNum - This is the order number associated with the receipt entry in the DB
    Local Variables:
        total - The total amount of the order on the receipt
        statement - The preparedStatement that we use to pre-compile the sql statement.
        rs - The result from the query just executed.
    Returns: A double representing the total amount of the order on the receipt.
     */
    private double getReceiptTotal(int orderNum){
        double total = -1;
        PreparedStatement statement;

        try{
            statement = con.prepareStatement("SELECT `Total` FROM `receipts` WHERE `OrderNum` = ?");
            statement.setInt(1, orderNum);
            ResultSet rs = statement.executeQuery();

            if(!rs.next()){return total;}

            total = rs.getDouble(1);
        }catch(SQLException ex){System.out.println(ex.getMessage());}
        return total;
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



















    //public static void main(String args[]){

//This demos the Insertion of a receipt entry.
//    DBController dbc = new DBController();
//
//    Vector<MenuItem> menuItems = dbc.getMenuItems();
//    OrderInProgress orderIP1 = new OrderInProgress("Ian Murphy");
//    orderIP1.addItem(new SelectedItem(menuItems.get(7).getItemID(), menuItems.get(7).getItemName(), menuItems.get(7).getDescription(), menuItems.get(7).getPrice(), 2, menuItems.get(7).getCalories()));
//    orderIP1.addItem(new SelectedItem(menuItems.get(5).getItemID(), menuItems.get(5).getItemName(), menuItems.get(5).getDescription(), menuItems.get(5).getPrice(), 1, menuItems.get(5).getCalories()));
//    orderIP1.addItem(new SelectedItem(menuItems.get(3).getItemID(), menuItems.get(3).getItemName(), menuItems.get(3).getDescription(), menuItems.get(3).getPrice(), 4, menuItems.get(3).getCalories()));
//
//    for(SelectedItem item: orderIP1.getItemList()){
//        System.out.println(item.getItemName());
//    }
//
//    dbc.insertReceipt(orderIP1);



//This demos the retrieval of items from a receipt. Displaying its ID, Name, & Quantity bought.
//    DBController dbc = new DBController();
//    for(String entry: dbc.getReceiptInfo(12)){
//        String[] t = entry.split(",");
//        System.out.println("ID: "+t[0]+
//                            "\nName: "+t[1]+
//                            "\nQuantity: "+t[2]);
//    }
//    System.out.println("\nGrand Total: $"+dbc.getMostRecentReceipt());

    //}
}