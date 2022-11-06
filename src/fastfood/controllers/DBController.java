package fastfood.controllers;


import fastfood.item.MenuItem;
import fastfood.item.SelectedItem;
import fastfood.order.OrderInProgress;
import fastfood.order.Receipt;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Vector;

/*
DB Table Structure Reference

Employee(EmpID, Title, Job, HourlyPay, DateStarted)
Contains(OrderNum, ItemID, Quantity)
MenuItem(ItemID, Description, Price, Calories)
Order(OrderNum, CustomerName, Date)

Table(1, 2, 3, ...) The Column Index Sequence
 */


/*
TODO:
    1. Change all statement objects to preparedStatement objects and make querys and execute statements that way.
    2. Determine how we want to check whether or not an OrderInProgress obj is completed or not before creating a
        receipt object & inserting it into the DB.
 */

public class DBController {
    private int orderIDGen = 100;
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
    Local Variables:
        curMenu - The list that will contain the menu items from the DB.
        rs - The result set object that contains the output from the query.
    Returns: A Vector<Item> object that has all the items in the menu.
     */
    public Vector<MenuItem> getMenuItems(){
        Vector<MenuItem> curMenu = new Vector<>(10, 5);
        Statement statement;
        try{
            //Create the statement and send the query to the backend DB
            statement = con.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM menuitems");

            //Populating the vector to return to the frontend UI
            while(rs.next()){
                MenuItem item = new MenuItem(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getInt(4), rs.getInt(5));
                curMenu.add(item);
            }
        }catch(Exception e){System.out.println(e.getMessage());}

        return curMenu;
    }

    /*
    Description: This is a helper function that gets the current date and time from the local time zone.
    Parameters: None
    Local Variables:
        dtf - The formatter for the date and time
        now - The current local time right now.
    Returns: A string representation of the current date and time.
     */
    private @NotNull String getDateTime(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }

    private int increment(){return orderIDGen++;}

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
    public boolean insertReceipt(OrderInProgress order, int... empId)throws Exception {
        boolean successful = true;
        Statement statement;
        /*
        ToDo:
           This checks if the Order has been paid for. If not it will return the control back to front end
        */

        //use this new receipt to make prepared statements.
        Receipt newReceipt = new Receipt(increment(), getDateTime(), order);//New Receipt created from the complete OrderInProgress Obj.

        try{
            String query;

            if(empId.length == 0){
                query = "INSERT INTO `receipts` (`OrderNumber`, `EmpID`, `CustomerName`, `Date`, `Total`) VALUES (NULL, NULL, `"+order.getCustName()+"`, NULL)";
                statement = con.createStatement();
                statement.executeUpdate(query);
            }
            else{
                query = "INSERT INTO `receipts` (`OrderNumber`, `EmpID`, `CustomerName`, `Date`) VALUES (NULL, `"+empId[0]+"`, `"+order.getCustName()+"`, NULL)";
                statement = con.createStatement();
                statement.executeUpdate(query);
            }

            insertReceiptInfo(newReceipt.getOrderNum(), newReceipt.getItemList());

        }catch(SQLException ex){
            System.out.println(ex.getMessage());
            successful = false;
        }
        catch(Exception e){
            System.out.println(e.getMessage());
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
        Statement statement;

        try{
            String query;
            statement = con.createStatement();
            for(SelectedItem item : selectedItems){
                query = "INSERT INTO `receipt_info` (`OrderNumber`, `ItemID`, `Quantity`) VALUES (`"+orderID+"`, `"+item.getItemID()+"`, `"+item.getQuantity()+"`)";
                statement.executeUpdate(query);
            }
        }catch(SQLException ex){
            System.out.println(ex.getMessage());
            successful = false;
        }catch(Exception e){
            System.out.println(e.getMessage());
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
        Statement statement;

        try{
            statement = con.createStatement();
            ResultSet rs = statement.executeQuery("SELECT `ItemID`, `Quantity` FROM receipt_info WHERE `OrderNumber`="+orderNum);

            while(rs.next()){
                String temp = rs.getString(1)+","+rs.getString(2);
                list.add(temp);
            }
        }catch(SQLException ex){
            System.out.println(ex.getMessage());
        }catch(Exception e){
            System.out.println(e.getMessage());
        }

        return list;
    }





















//    public static void main(String args[]){
//        OrderInProgress orderIP1 = new OrderInProgress("Ian Murphy");
//        orderIP1.addItem(new SelectedItem(1, "Hamburger", 5.50, 2, 128));
//        orderIP1.addItem(new SelectedItem(2, "CheeseBurger", 6.00, 1, 152));
//        orderIP1.addItem(new SelectedItem(3, "Chicken Nuggets", 5.25, 3, 140));
//
//        System.out.println("\nCustomer Name: "+orderIP1.getCustName()+"\nTotal: "+orderIP1.getTotal()+"\n");
//
//        Receipt receipt1 = new Receipt(100, getDateTime(), orderIP1);
//
//        System.out.println("Order Number: "+receipt1.getOrderNum()+
//                            "\nCustomer Name: "+receipt1.getCustName()+
//                            "\nTotal: "+receipt1.getTotal()+
//                            "\nOrder Take On: "+receipt1.getDate()+"\n\nOrder List:\n");
//
//        for(SelectedItem item: receipt1.getItemList()){
//            System.out.println("Item ID: "+item.getItemID()+
//                    "\nDescription: "+item.getDescription()+
//                    "\nPrice: "+item.getPrice()+
//                    "\nCalories: "+item.getCalories()+"\n");
//        }
//        System.out.println("End.");
//
//
//
//
//
//
//        try{
//            //Registering the Driver for the connection.
//            Class.forName("com.mysql.jdbc.Driver");//Make sure to download the mysql.connector-java.jar AND add it to the project dependencies for this driver to work.-> https://dbschema.com/jdbc-driver/MySql.html
//
//            //Making the connection to the database
//            Connection con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/fastfood", "root", "");//If pulled from remote repo, make sure the DB URL matches your local environment!!
//            //Here fastfood is the database, root is the user, and "" is the password
//
//            if(!con.isValid(0)){
//                System.out.println("Database Connection failed.\n");
//            }else{
//                System.out.println("Database Connection successful.\n");
//            }
//
//            Statement statement = con.createStatement();
//
//            ResultSet rs = statement.executeQuery("SELECT * FROM menu");
//
//            //For each row print the columns
//            while(rs.next()){
//                System.out.println("ItemID: "+rs.getInt(1)+"\nName: "+rs.getString(2)+"\nDescription: "+rs.getString(3)+"\nPrice: "+rs.getDouble(4)+"\nCalories: "+rs.getInt(5)+"\n");
//            }
//
//            //Statement below Inserted into the table menu and the make another query so that we can show the newly added McDouble item
//            statement.executeUpdate("INSERT INTO `menu` (`ItemID`, `Name`, `Description`, `Price`, `Calories`) VALUES ('730283105', 'McDouble', 'Good McDouble, Yum!', '4.99', '290')");
//
//            rs = statement.executeQuery("SELECT * FROM menu");
//
//            while(rs.next()){
//                System.out.println("ItemID: "+rs.getInt(1)+"\nName: "+rs.getString(2)+"\nDescription: "+rs.getString(3)+"\nPrice: "+rs.getDouble(4)+"\nCalories: "+rs.getInt(5)+"\n");
//            }
//            con.close();
//
//        }catch(Exception e){System.out.println(e.getMessage());}
//    }
}