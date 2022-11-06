package fastfood.order;

import fastfood.item.SelectedItem;

import java.util.Vector;

abstract class Order {

    protected Vector<SelectedItem> itemList;
    protected double total;
    protected String customerName;

    protected Order(String customerName) {
        this.itemList = new Vector<>();
        this.total = 0.0;
        this.customerName = customerName;
    }

    public Vector<SelectedItem> getItemList(){return this.itemList;}
    protected void setItemList(Vector<SelectedItem> list){this.itemList = list;}
    public double getTotal(){return this.total;}
    public void setTotal(double newTotal){this.total = newTotal;}
    public String getCustName(){return this.customerName;}
    private void setCustName(String updatedName){this.customerName = updatedName;}
}























































//import java.util.Date;
//import java.util.List;
//import SelectedItem;
//
//public class Order {
//    List<SelectedItem> gfg;
//    public float orderTotal;
//    public int orderNum;
//    public Date timestamp;
//    public String customerName;
//
//    public Order(String customerName) {
//        this.gfg = new List<SelectedItem>();
//        this.orderTotal = 0;
//        this.orderNum = orderNum;
//        this.timestamp = this.timestamp.getTime();
//        this.customerName = customerName;
//    }
//    public List<SelectedItem> getGfg() {return gfg;}
//    public Float getOrderTotal() {return orderTotal;}
//    public void setOrderTotal(Float orderTotal) {this.orderTotal = orderTotal;}
//    public int getOrderNum() {return orderNum;}
//    public void setOrderNum(int orderNum) {this.orderNum = orderNum;}
//    public Date getTimestamp() {return timestamp;}
//    public void setTimestamp(Date timestamp) {this.timestamp = timestamp;}
//    public String getCustomerName() {return customerName;}
//    public void setCustomerName(String customerName) {this.customerName = customerName;}
//    public void reCalculateTotal(Float reTotal) {
//
//    }
//}