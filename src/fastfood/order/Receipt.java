package fastfood.order;

import fastfood.item.SelectedItem;

import java.util.Vector;

public class Receipt extends Order {
    private final int  orderNum;
    private final String timestamp;

    //Constructors
    public Receipt(int orderNo, String custName, String ts) {
        super(custName);
        this.orderNum = orderNo;
        this.timestamp = ts;
    }

    public Receipt(int orderNo, String custName, Vector<SelectedItem> list, double total,  String ts) {
        super(custName, list, total);
        this.orderNum = orderNo;
        this.timestamp = ts;
    }

    public Receipt(int orderNo, String ts, OrderInProgress finishedOrder) {
        super(finishedOrder.getCustName());

        Vector<SelectedItem> temp1 = finishedOrder.getItemList();
        setItemList(temp1);

        double temp2 = finishedOrder.getTotal();
        setTotal(temp2);

        this.orderNum = orderNo;
        this.timestamp = ts;
    }

    //Getters and Setters
    public int getOrderNum(){return orderNum;}
    public String getDate(){return timestamp;}
}