package fastfood.controllers;

import fastfood.item.MenuItem;
import fastfood.item.SelectedItem;
import fastfood.order.OrderInProgress;
import fastfood.order.Receipt;

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
	
	private JPanel startPage;
    private JPanel menuPage;
    private JPanel checkoutPage;
    private DBController db;
    
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
     * Constructor
     */
    public UIController() {
    	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //setBounds(100, 100, 450, 300);
    	setExtendedState(JFrame.MAXIMIZED_BOTH); //max screen size
    	//create the start page
    	createStartPage();
    }
    
    
    //create start page function
    public void createStartPage()
    {
         startPage = new JPanel();
         startPage.setBorder(new EmptyBorder(5, 5, 5, 5));

         setContentPane(startPage);
         startPage.setLayout(null);
         
         JLabel appNameLabel = new JLabel("Restaurant Food Ordering App");
         appNameLabel.setForeground(new Color(128, 0, 255));
         appNameLabel.setFont(new Font("Times New Roman", Font.BOLD, 16));
         appNameLabel.setBounds(520, 200, 400, 26);
         startPage.add(appNameLabel); 
         
         JLabel nameLabel = new JLabel("Name: ");
         nameLabel.setBounds(520, 300, 174, 23);
         startPage.add(nameLabel); 
         
         JTextField nameText = new JTextField();
         nameText.setBounds(580, 300, 174, 23);
         startPage.add(nameText);
         
         JLabel errorLabel = new JLabel("Error: Pls Enter Name to Continue!");
         errorLabel.setForeground(new Color(255, 0, 0));
         errorLabel.setBounds(520, 250, 300, 23);
         startPage.add(errorLabel);
         errorLabel.setVisible(false);
         
         //order button
         JButton orderButton = new JButton("Order");
         
         orderButton.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent e) {
             	if(!nameText.getText().trim().equals(""))
             	{
             		errorLabel.setVisible(false);
             		OrderController OC = new OrderController(nameText.getText());
 	                startPage.setVisible(false);
 	                createMenuPage(OC);
             	} 
             	else
             		errorLabel.setVisible(true);
             }
         });
         orderButton.setBounds(520, 400, 174, 23);
         startPage.add(orderButton);
         
         JLabel FFTechLabel = new JLabel("Created By FFTech");
         FFTechLabel.setBounds(520, 500, 300, 23);
         startPage.add(FFTechLabel); 
    }
    
    
    //create Menu Page function
    public void createMenuPage(OrderController OC)
    {
    	 menuPage = new JPanel();
         menuPage.setBorder(new EmptyBorder(5, 5, 5, 5));

         setContentPane( menuPage);
         menuPage.setLayout(null);
         

         JPanel panel = new JPanel();
         panel.setBounds(167, 92, 500, 89);
         menuPage.add(panel);

         db = new DBController();
         Vector<MenuItem> items = db.getMenuItems();
         
         OrderInProgress order = OC.getOrder();      
         
         //checkout button
         JButton checkOutButton = new JButton("Check Out");
         
         checkOutButton.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent e) {
                 order.setCompleted(true);//If the order isn't completed. The DBController rejects the insertion request.
                 menuPage.setVisible(false);
                 createCheckoutPage(order);
             }
         });
         checkOutButton.setBounds(940, 250, 174, 23);
         menuPage.add(checkOutButton); 
         
         
        //create summary section
        JPanel sumGrid = new JPanel();
 	    sumGrid.setLayout(new GridLayout(0, 2, 10, 0));
 	    
 	    JScrollPane scrollSumPane = new JScrollPane(sumGrid);
 	    scrollSumPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
 	    scrollSumPane.setLocation(750, 11);   
 	    scrollSumPane.setSize(520, 200);
 	    
 	    menuPage.add(scrollSumPane, BorderLayout.CENTER);
 	    
 	    //create menu button list
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
 	 	    	  //update summary when clicked
 	 	    	  updateSummary(sumGrid, order, OC);
 	 	      }
 	 	    });
 	    	 menuGrid.add(itemButton);	 
 	    }
 	  
 	    JScrollPane scrollMenuPane = new JScrollPane(menuGrid);
 	    scrollMenuPane.setLocation(331, 238);   
 	    scrollMenuPane.setSize(310, 200);
 	    
 	    menuPage.add(scrollMenuPane, BorderLayout.CENTER); 
 	    
 	   //cancel button 
 	   JButton cancelButton = new JButton("Cancel Order");
       
       cancelButton.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
        	   menuPage.setVisible(false);
        	   createStartPage();
           }
       });
       cancelButton.setBounds(940, 300, 174, 23);
       menuPage.add(cancelButton); 
    }
    
    
    //create checkout page function
    public void createCheckoutPage(OrderInProgress order)
    {
    	checkoutPage = new JPanel();
        checkoutPage.setBorder(new EmptyBorder(5, 5, 5, 5));

        setContentPane(checkoutPage);
        checkoutPage.setLayout(null);
        
    	//create checkout table
        JTable table = new JTable(new DefaultTableModel(new Object[]{"0", "1", "2", "3"}, 0));
        table.setEnabled(false);
        DefaultTableModel Tmodel = (DefaultTableModel) table.getModel();
        Tmodel.addRow(new Object[]{"Items", "Price", "Quantity", "Total"});
        table.setSize(800, 200);
        table.setLocation(200, 126);
        checkoutPage.add(table);
        
        JLabel totalAmount = new JLabel();
        totalAmount.setBounds(500, 400, 148, 30);
        totalAmount.setText("Total Amount: ");
        checkoutPage.add(totalAmount);
        
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
        
        //back button
        JButton backButton = new JButton("Back");
        
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	checkoutPage.setVisible(false);
            	setContentPane(menuPage);
                menuPage.setVisible(true);
            }
        });
        backButton.setBounds(50, 50, 174, 23);
        checkoutPage.add(backButton); 
        
        //cancel button
        JButton cancelButton = new JButton("Cancel Order");
        
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
         	   checkoutPage.setVisible(false);
         	   createStartPage();
            }
        });
        cancelButton.setBounds(300, 500, 174, 23);
        checkoutPage.add(cancelButton); 
        
        //purchase button
        JButton purchaseButton = new JButton("Purchase");
        
        purchaseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               db.insertReceipt(order); //FIX THIS 
            }
        });
        purchaseButton.setBounds(750, 500, 174, 23);
        checkoutPage.add(purchaseButton);     	
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
		 	    	 updateSummary(sumGrid, order, OC); //update summary each time we remove an item
		 	      } });
	    	 buttonPanel.add(minusButton);
	    	 sumGrid.add(buttonPanel);	 
	    }
	    
	    //update render 
	    sumGrid.revalidate();
	    sumGrid.repaint();;	
    }
}
