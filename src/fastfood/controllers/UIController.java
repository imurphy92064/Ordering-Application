package fastfood.controllers;

import fastfood.item.MenuItem;
import fastfood.item.SelectedItem;
import fastfood.order.OrderInProgress;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

public class UIController extends JFrame {

    private JPanel contentPane;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    UIController frame = new UIController();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public UIController() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

        setContentPane(contentPane);
        contentPane.setLayout(null);
        

        JPanel panel = new JPanel();
        panel.setBounds(167, 92, 500, 89);
        contentPane.add(panel);

        DBController db = new DBController();
        Vector<MenuItem> items = db.getMenuItems();
        /*Vector<String> itemNames = new Vector<>();
        Vector<String> prices = new Vector<>();
        processMenuItems(items, itemNames, prices); */
        
        //OrderInProgress order = new OrderInProgress("Jimmy");
        
        OrderController OC = new OrderController("Jimmy");
        OrderInProgress order = OC.getOrder();
        
        //new stuff ---------------------------------------------------------------------
        
        //create checkout table
        JTable table = new JTable(new DefaultTableModel(new Object[]{"0", "1", "2", "3"}, 0));
        table.setEnabled(false);
        DefaultTableModel Tmodel = (DefaultTableModel) table.getModel();
        Tmodel.addRow(new Object[]{"Items", "Price", "Quantity", "Total"});
        table.setSize(260, 100);
        table.setLocation(24, 226);
        contentPane.add(table);
        
        JLabel totalAmount = new JLabel();
        totalAmount.setBounds(50,350, 148, 30);
        totalAmount.setText("Total Amount: ");
        contentPane.add(totalAmount);
        
        JButton checkOutButton = new JButton("Check Out");
        
        checkOutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Tmodel.setRowCount(1);

                for(SelectedItem i : order.getItemList())
                {
                    String I = i.getItemName(); 
                    String Q = "x" + Integer.toString(i.getQuantity()); 
                    String P = "$" +  Double.toString(i.getPrice());
                    String T = "$" + Double.toString(i.getQuantity() * i.getPrice());
                    Tmodel.addRow(new Object[]{I, P, Q, T});
                }
            
                totalAmount.setText("Total Amount: $" + Double.toString(order.getTotal()));
            }
        });
        checkOutButton.setBounds(940, 250, 174, 23);
        contentPane.add(checkOutButton); 
        
        
        //create summary
        JPanel sumGrid = new JPanel();
	    sumGrid.setLayout(new GridLayout(0, 2, 10, 0));
	    
	    JScrollPane scrollSumPane = new JScrollPane(sumGrid);
	    scrollSumPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
	    scrollSumPane.setLocation(750, 11);   
	    scrollSumPane.setSize(520, 200);
	    
	    contentPane.add(scrollSumPane, BorderLayout.CENTER);
	    
	    //create menu
        JPanel menuGrid =  new JPanel();
	    menuGrid.setLayout(new GridLayout(0, 1, 0, 0));
	    
	    for (int i=0; i<items.size(); i++)
	    {
	    	 MenuItem item = items.get(i);
	    	 JButton itemButton = new JButton( "<html><center>" + item.getItemName() + "<br/>" + "$" + item.getPrice() + "</center><html>");
	    	 itemButton.setToolTipText(item.getDescription());
	    	 // add event listener
	 	      itemButton.addActionListener(new ActionListener() {
	 	      @Override
	 	      public void actionPerformed(ActionEvent e) {
	 	    	  
	 	    	  OC.modifyOrder(true, item, null);
	 	    	  //order.addItem(item);
	 	    	  
	 	    	  //update summary when clicked
	 	    	  updateSummary(sumGrid, order, OC);
	 	      }
	 	    });
	    	 menuGrid.add(itemButton);
	    	 
	    }
	 
	    
	    JScrollPane scrollMenuPane = new JScrollPane(menuGrid);
	    scrollMenuPane.setLocation(331, 238);   
	    scrollMenuPane.setSize(310, 200);
	    
	    contentPane.add(scrollMenuPane, BorderLayout.CENTER); 
    }
    
    //update summary function
    public void updateSummary( JPanel sumGrid, OrderInProgress order, OrderController OC)
    {
	    sumGrid.removeAll();
	  
	    for (int i=0; i<order.getItemList().size(); i++)
	    {
	    	
	    	 SelectedItem item = order.getItemList().get(i);
	    	 
	    	 JPanel namePanel = new JPanel();
	    	 JLabel nameLabel = new JLabel( "  " + item.getItemName() + " x" + item.getQuantity());
	    	 nameLabel.setToolTipText(nameLabel.getText());
	    	 nameLabel.setPreferredSize(new Dimension(280,30));

	    	 namePanel.add(nameLabel);
	    	 sumGrid.add(namePanel);
	    	 
	    	 JPanel buttonPanel = new JPanel();
	    	 JButton minusButton = new JButton("-");
	    	 minusButton.setPreferredSize(new Dimension(100,30));
	    	 minusButton.addActionListener(new ActionListener() {
		 	      @Override
		 	      public void actionPerformed(ActionEvent e) { 
		 	    	 OC.modifyOrder(false, null, item);
		 	    	 //order.removeItem(item);
		 	    	 updateSummary(sumGrid, order, OC); //update summary each time we remove an item
		 	      } });
	    	 buttonPanel.add(minusButton);
	    	 sumGrid.add(buttonPanel);	 
	    }
	    
	    
	    sumGrid.revalidate();
	    sumGrid.repaint();;	
    }
    
    /*private void processMenuItems(Vector<MenuItem> items, Vector<String> name, Vector<String> prices){
        for(int i=0; i < items.size(); i++){
            name.add(items.get(i).getItemName());
            prices.add(String.valueOf(items.get(i).getPrice()));
        }
    }*/
}
