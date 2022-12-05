package fastfood.controllers;

import fastfood.item.MenuItem;
import fastfood.item.SelectedItem;
import fastfood.order.OrderInProgress;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

/*
12/04/2022 Changes:
    1. Added function headers for functions.
 */
public class UIController extends JFrame {
	private JPanel startPage;
    private JPanel menuPage;
    private JPanel checkoutPage;
    private DBController db;

    /*
    Description: This is the constructor for the UIController.
    Parameters:
        db_ - This is the DB Controller object that connects to the DB and allows the app to send data to the DB.
    Local Variables: None.
    Returns: An instance of this class.
     */
    public UIController(DBController db_) {
        db = db_;
    	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //setBounds(100, 100, 450, 300);
    	setExtendedState(JFrame.MAXIMIZED_BOTH); //max screen size
    	//create the start page
    	createStartPage();
    }

    /*
    Description: This method creates the start page the user will encounter when they first start the application
    Parameters: None.
    Local Variables:
        appNameLabel - This label shows the name of the app.
        nameLabel - This is a label that prompts the user for their name.
        nameText - This is a text field that the user enter their name into.
        errorLabel - If an invalid name is entered. Then we show this error label
        orderButton - This button tears down this page and starts the menu page for ordering.
        ffTechLabel - Label showing our company names.
    Returns: Nothing.
     */
    public void createStartPage() {
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
             	if(!nameText.getText().trim().equals("")) {
             		OrderController OC = new OrderController(nameText.getText(), db);
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

    /*
    Description: This method creates the menu page the user will encounter when they want to modify their order.
    Parameters:
        OC - This is an object of the order controller. So that when the user wants to do something to the order they
            have access to the Order Controller.
    Local Variables:
        panel - This is the panel where the widgets will be organized.
        checkoutButton - This button is for when the user wants to finalize their order and checkout.
        sumGrid - This is a grid that contains the menu item selected for their order.
        scrollSumPane - This allows that grid to be scrollable when there are too many items.
        menuGrid - This is a grid that contains the menu items for the user to choose from.
        item - This is a menu item from the menu.
        itemButton - This is a button the user can click on representing a menu item that they want to add to their order.
        scrollMenuPane - This allows the menu grid to scroll.
        cancelButton - This button destroys all objects being used by the menu page, tears down the menu page and creates
            the start page to start over for another customer.
    Returns: Nothing.
     */
    public void createMenuPage(OrderController OC) {

    	 menuPage = new JPanel();
         menuPage.setBorder(new EmptyBorder(5, 5, 5, 5));

         setContentPane( menuPage);
         menuPage.setLayout(null);
         

         JPanel panel = new JPanel();
         panel.setBounds(167, 92, 500, 89);
         menuPage.add(panel);

         //Get Menu Items
         Vector<MenuItem> items = db.getMenuItems();

         //checkout button
         JButton checkOutButton = new JButton("Check Out");
         
         checkOutButton.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent e) {
                 menuPage.setVisible(false);
                 createCheckoutPage(OC);
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
        updateSummary(sumGrid, OC.getOrder(), OC);
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
 	 	    	  updateSummary(sumGrid, OC.getOrder(), OC);
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

    /*
    Description: This method creates the checkout page the user will encounter when the user wants to check out their
        order.
    Parameters:
        OC - This is an object of the order controller. So that when the user wants to do something to the order they
            have access to the Order Controller.
    Local Variables:
        table - This is a table that hold the summary of the order being completed.
        totalAmount - This is the total amount of the order being completed.
        I - The name of the item selected.
        Q - This is the quantity of the item selected.
        P - The price of the item selected.
        T - The total price of the item selected with the quantity wanted for this item.
        backButton - This sends you back to the menu page to modify the current order again.
        cancelButton - This button destroys all objects being used by the menu page, tears down the menu page and creates
            the start page to start over for another customer.
        purchaseButton - This button will complete the order. Show a quick text field saying the order is completed and
            send you back to the start page for the next customer.
    Returns: Nothing.
     */
    public void createCheckoutPage(OrderController OC) {
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

        for(SelectedItem i : OC.getOrderList())
        {
            String I = i.getItemName(); 
            String Q = "x" + Integer.toString(i.getQuantity()); 
            String P = "$" +  Double.toString(i.getPrice());
            String T = "$" + Double.toString(i.getQuantity() * i.getPrice());
            Tmodel.addRow(new Object[]{I, P, Q, T});
        }
    
        totalAmount.setText("Total Amount: $" + OC.getOrderTotal());
        
        //back button
        JButton backButton = new JButton("Back");
        
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	checkoutPage.setVisible(false);
            	setContentPane(menuPage);
                menuPage.setVisible(true);
                createMenuPage(OC);
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
                OC.finalizeOrder();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                checkoutPage.setVisible(false);
                createStartPage();
            }
        });
        purchaseButton.setBounds(750, 500, 174, 23);
        checkoutPage.add(purchaseButton);     	
    }

    /*
    Description: This is a helper function that updates the sumGrid when new items are added or when items are removed.
    Parameters:
        OC - This is an object of the order controller. So that when the user wants to do something to the order they
            have access to the Order Controller.
        order - This is the current order being manipulated, it contains the up-to-date order list.
        sumGrid - This is the grid to be updated with the up-to-date order list from the OrderInProgress object.
    Local Variables:
        item - This is an item from the order list in the OrderInProgress object.
        namePanel - This is where the name label will go.
        nameLabel - This is where the name of the item will go.
        buttonPanel - This is where 'minus' buttons will go
        minusButton - These buttons will modify the quantity of the item selected by subtracting the quantity by 1 per click.
    Returns: Nothing. But the sumGrid is updated.
     */
    public void updateSummary( JPanel sumGrid, OrderInProgress order, OrderController OC) {
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
