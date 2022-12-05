package fastfood.order;

import fastfood.controllers.DBController;
import fastfood.item.ItemFactory;
import fastfood.item.SelectedItem;

import java.sql.ResultSet;
import java.util.Vector;

public class OrderFactory {
    public static Order createOrder(String type, ResultSet receipt_rs, ResultSet orderList_rs, String name){
        switch(type){
            case "order in progress":
                if(name == null)
                    return new OrderInProgress(null);
                else
                    return new OrderInProgress(name);
            case "receipt":
                try{
                    if(receipt_rs == null) {
                        System.out.println("Result Set is null can't create receipt.");
                        return null;
                    }
                    else
                        return new Receipt(receipt_rs.getInt(1), receipt_rs.getString(2), getOrderList(orderList_rs), receipt_rs.getDouble(3), receipt_rs.getString(4));
                }catch(Exception e){
                    e.printStackTrace();
                    System.out.println(e.getMessage());
                }
            default:
                throw new IllegalArgumentException("Unknown Item Type was Passed: "+type);
        }
    }

    private static Vector<SelectedItem> getOrderList(ResultSet rs){
        Vector<SelectedItem> receiptList = new Vector<>();
        try{
            while(rs.next()){
                receiptList.add((SelectedItem) ItemFactory.createItem("selected item", rs, null));
            }
        }catch(Exception e){
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return receiptList;
    }
}
