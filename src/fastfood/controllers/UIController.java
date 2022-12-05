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
    	//createMenuPage(null);
    }
    
    
    //create start page function
    public void createStartPage()
    {	
         startPage = new JPanel();
         startPage.setBackground(new Color(255, 255, 255));
         startPage.setBorder(new EmptyBorder(5, 5, 5, 5));

         setContentPane(startPage);
         startPage.setLayout(null);
         
         JLabel HamburgerIMG = new JLabel();
    	 HamburgerIMG.setIcon(new ImageIcon("src\\foodtable.jpg"));
         HamburgerIMG.setBounds(10, -110, 1000, 1000);
         startPage.add(HamburgerIMG); 
         
         JLabel appNameLabel = new JLabel("Restaurant Food Ordering App");
         appNameLabel.setBackground(new Color(0, 0, 255));
         appNameLabel.setForeground(new Color(0, 0, 160));
         appNameLabel.setFont(new Font("Engravers MT", Font.BOLD | Font.ITALIC, 30));
         appNameLabel.setBounds(255, 24, 1150, 85);
         startPage.add(appNameLabel); 
         
         JLabel nameLabel = new JLabel("Name: ");
         nameLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
         nameLabel.setForeground(new Color(0, 0, 128));
         nameLabel.setBounds(957, 224, 218, 65);
         startPage.add(nameLabel); 
         
         JTextField nameText = new JTextField();
         nameText.setFont(new Font("Tahoma", Font.PLAIN, 18));
         nameText.setBounds(957, 300, 300, 37);
         startPage.add(nameText);
         
         JLabel errorLabel = new JLabel("Error: Pls Enter Name to Continue!");
         errorLabel.setForeground(new Color(255, 0, 0));
         errorLabel.setBounds(957, 350, 300, 23);
         startPage.add(errorLabel);
         errorLabel.setVisible(false);
         
         //order button
         JButton orderButton = new JButton("Order");
         orderButton.setForeground(new Color(255, 255, 255));
         orderButton.setBackground(new Color(0, 0, 128));
         
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
         orderButton.setBounds(957, 400, 174, 23);
         startPage.add(orderButton);
         
         JLabel FFTechLabel = new JLabel("Created By FFTech");
         FFTechLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
         FFTechLabel.setForeground(new Color(0, 0, 128));
         FFTechLabel.setBounds(967, 546, 300, 23);
         startPage.add(FFTechLabel); 
    }
    
    
    //create Menu Page function
    public void createMenuPage(OrderController OC)
    {
    	 menuPage = new JPanel();
    	 menuPage.setBackground(new Color(255, 255, 255));
         menuPage.setBorder(new EmptyBorder(5, 5, 5, 5));

         setContentPane( menuPage);
         menuPage.setLayout(null);
         
         JLabel fftechLogo = new JLabel();
    	 fftechLogo.setIcon(new ImageIcon("src\\fftech_logo.png"));
         fftechLogo.setBounds(10, -247, 1000, 1000);
         menuPage.add(fftechLogo); 

         db = new DBController();
         Vector<MenuItem> items = db.getMenuItems();
         
         OrderInProgress order = OC.getOrder();      
         
         //checkout button
         JButton checkOutButton = new JButton("Check Out");
         checkOutButton.setForeground(new Color(255, 255, 255));
         checkOutButton.setBackground(new Color(0, 0, 128));
         
         checkOutButton.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent e) {
                 menuPage.setVisible(false);
                 createCheckoutPage(order);
             }
         });
         checkOutButton.setBounds(859, 538, 174, 23);
         menuPage.add(checkOutButton); 
         
         
        //create summary section
        JPanel sumGrid = new JPanel();
 	    sumGrid.setLayout(new GridLayout(0, 2, 10, 0));
 	    
 	    JScrollPane scrollSumPane = new JScrollPane(sumGrid);
 	    scrollSumPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
 	    scrollSumPane.setLocation(750, 11);   
 	    scrollSumPane.setSize(520, 436);
 	    
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
 	    scrollMenuPane.setLocation(267, 48);   
 	    scrollMenuPane.setSize(392, 426);
 	    
 	    menuPage.add(scrollMenuPane, BorderLayout.CENTER); 
 	    
 	   //cancel button 
 	   JButton cancelButton = new JButton("Cancel Order");
 	   cancelButton.setForeground(new Color(255, 255, 255));
 	   cancelButton.setBackground(new Color(0, 0, 128));
       
       cancelButton.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
        	   menuPage.setVisible(false);
        	   createStartPage();
           }
       });
       cancelButton.setBounds(317, 538, 174, 23);
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
