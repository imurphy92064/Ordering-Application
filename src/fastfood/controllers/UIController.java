package fastfood.controllers;

import fastfood.item.MenuItem;
import fastfood.item.SelectedItem;
import fastfood.order.OrderInProgress;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

/*
12/04/2022 Changes:
    1. Added function headers for functions.
 */
public class UIController extends JFrame implements ComponentListener {
	private JPanel startPage; //page 1
    private JPanel menuPage;  //page 2
    private JPanel checkoutPage; //page 3
    private DBController db; //declaration for the database
    Dimension devScreenSize = new Dimension(1280,720); //the screen size of the UI developer's screen to be used as default comparison
    private Vector<Component> comps = new Vector<>();  //saves the components in page to be use in resize
    private Vector<Dimension> compSizes = new Vector<>(); //saves the default sizes of components for resize
    private Vector<Point> compLocs = new Vector<>(); //saves the  default locations of components for resize
    private Vector<Font> compFonts = new Vector<>(); //saves the default fonts of components for resize

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
         startPage.setBackground(new Color(255, 255, 255));
         startPage.setBorder(new EmptyBorder(5, 5, 5, 5));

         setContentPane(startPage);
         startPage.setLayout(null);
         
         JLabel HamburgerIMG = new JLabel();	
         HamburgerIMG.setBounds(10, 150, 700, 500);
         HamburgerIMG.setIcon(loadImage(HamburgerIMG,"src\\foodtable.jpg")); //call helper function to resize image to fit label
         startPage.add(HamburgerIMG);
         
         JLabel appNameLabel = new JLabel("Restaurant Food Ordering App");
         appNameLabel.setBackground(new Color(0, 0, 255));
         appNameLabel.setForeground(new Color(0, 0, 160));
         appNameLabel.setFont(new Font("Engravers MT", Font.BOLD | Font.ITALIC, 30));
         appNameLabel.setBounds(205, 24, 1150, 85);
         startPage.add(appNameLabel); 
         
         JLabel nameLabel = new JLabel("Name: ");
         nameLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
         nameLabel.setForeground(new Color(0, 0, 128));
         nameLabel.setBounds(907, 224, 218, 65);
         startPage.add(nameLabel); 
         
         JTextField nameText = new JTextField();
         nameText.setFont(new Font("Tahoma", Font.PLAIN, 18));
         nameText.setBounds(907, 300, 300, 37);
         startPage.add(nameText);
         
         JLabel errorLabel = new JLabel("Error: Pls Enter Name to Continue!");
         errorLabel.setForeground(new Color(255, 0, 0));
         errorLabel.setBounds(907, 350, 300, 33);
         startPage.add(errorLabel);
         errorLabel.setVisible(false);
         
         //order button
         JButton orderButton = new JButton("Order");
         orderButton.setForeground(new Color(255, 255, 255));
         orderButton.setBackground(new Color(0, 0, 128));
         
         orderButton.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent e) {
             	if(!nameText.getText().trim().equals("")) {
             		OrderController OC = new OrderController(nameText.getText(), db);
 	                startPage.setVisible(false);
 	                clearResizeInfo();
 	                createMenuPage(OC);
             	} 
             	else
             		errorLabel.setVisible(true);
             }
         });
         orderButton.setBounds(907, 400, 174, 33);
         startPage.add(orderButton);
         
         JLabel FFTechLabel = new JLabel("Created By FFTech");
         FFTechLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
         FFTechLabel.setForeground(new Color(0, 0, 128));
         FFTechLabel.setBounds(917, 546, 300, 33);
         startPage.add(FFTechLabel);
        
         configResize(startPage); //save values in vectors and setup listener for resize
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
    	 menuPage.setBackground(new Color(255, 255, 255));
         menuPage.setBorder(new EmptyBorder(5, 5, 5, 5));

         setContentPane( menuPage);
         menuPage.setLayout(null);
         
         JLabel fftechLogo = new JLabel();	
         fftechLogo.setBounds(15, 0, 100, 500);
         fftechLogo.setIcon(loadImage(fftechLogo,"src\\fftech_logo.png"));
         menuPage.add(fftechLogo);
         
         //Get Menu Items
         Vector<MenuItem> items = db.getMenuItems();

         //checkout button
         JButton checkOutButton = new JButton("Check Out");
         checkOutButton.setForeground(new Color(255, 255, 255));
         checkOutButton.setBackground(new Color(0, 0, 128));
         
         checkOutButton.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent e) {
            	 if(OC.getOrderTotal() > 0) //if empty can't go to checkout page
            	 {
            		 menuPage.setVisible(false);
            		 clearResizeInfo();
            		 createCheckoutPage(OC); 
            	 }     
             }
         });
         checkOutButton.setBounds(859, 538, 174, 33);
         menuPage.add(checkOutButton); 
         
         
        //create summary section
        JPanel sumGrid = new JPanel();
 	    sumGrid.setLayout(new GridLayout(0, 2, 10, 0));
 	    sumGrid.setBackground(new Color(255, 255, 255));
 	    
 	    JScrollPane scrollSumPane = new JScrollPane(sumGrid);
 	    scrollSumPane.setLocation(750, 40);   
 	    scrollSumPane.setSize(520, 436);
 	    //scrollSumPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
 	    menuPage.add(scrollSumPane, BorderLayout.CENTER);
 	    
        updateSummary(sumGrid, OC.getOrder(), OC);
        
 	    //create menu button list
        JPanel menuGrid =  new JPanel();
 	    menuGrid.setLayout(new GridLayout(0, 1, 0, 0));
 	   
 	    for (int i=0; i<items.size(); i++)
 	    {
 	    	 MenuItem item = items.get(i);
 	    	 JButton itemButton = new JButton( "<html><center>" + item.getItemName() + "<br/>" + "$" + String.format("%.2f",item.getPrice()) + "</center><html>");
 	    	 itemButton.setForeground(new Color(0, 0, 128));
 	    	 itemButton.setBackground(new Color(255, 255, 255));
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
 	    	
 	    	itemButton.setSize(500,30);
 	    	JPanel itemPanel = new JPanel();
 	    	itemPanel.setBackground(new Color(255, 255, 255));
 	    	itemPanel.add(itemButton);
 	    	menuGrid.add(itemPanel);
 	    	
 	    	comps.add(itemButton);
 	    	compSizes.add(itemButton.getSize());
 	      	compLocs.add(itemButton.getLocation());
 	      	compFonts.add(itemButton.getFont());
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
        	   clearResizeInfo();
        	   createStartPage();
           }
       });
       cancelButton.setBounds(317, 538, 174, 33);
       menuPage.add(cancelButton); 
       
       configResize(menuPage); //save values in vectors and setup listener for resize
      
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
    	checkoutPage.setBackground(new Color(255, 255, 255));
        checkoutPage.setBorder(new EmptyBorder(5, 5, 5, 5));

        setContentPane(checkoutPage);
        checkoutPage.setLayout(null);
        
    	//create checkout table
        JTable table = new JTable(new DefaultTableModel(new Object[]{"", "", "", ""}, 0));
        table.setEnabled(false);
        DefaultTableModel Tmodel = (DefaultTableModel) table.getModel();
        Tmodel.addRow(new Object[]{"Items", "Price", "Quantity", "Total"});
        table.setPreferredSize(new Dimension(600, 50 * (OC.getOrderList().size() + 1))); //table grows to fit amount of items in order
        //table.setLocation(250, 126);
        JPanel tPanel = new JPanel();
        tPanel.add(table);
        JScrollPane scrollTPane = new JScrollPane(tPanel);
 	    scrollTPane.setLocation(300, 100);   
 	    scrollTPane.setSize(670, 300);
 	    checkoutPage.add(scrollTPane, BorderLayout.CENTER);
        //checkoutPage.add(table);
        
        JLabel totalAmount = new JLabel();
        totalAmount.setBounds(550, 450, 700, 30);
        totalAmount.setText("Total Amount: ");
        checkoutPage.add(totalAmount);
        
        Tmodel.setRowCount(1);

        for(SelectedItem i : OC.getOrderList())
        {
            String I = i.getItemName(); 
            String Q = "x" + Integer.toString(i.getQuantity()); 
            String P = "$" +  String.format("%.2f", i.getPrice());
            String T = "$" +  String.format("%.2f", Math.round(i.getQuantity() * i.getPrice() * 100.0) / 100.0);
            Tmodel.addRow(new Object[]{I, P, Q, T});
        }
        
        table.getColumnModel().getColumn(0).setPreferredWidth(300);
        table.setRowHeight(20);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setFont(new Font("Serif", Font.BOLD, 16));
        
        comps.add(table);
	    compSizes.add(table.getPreferredSize());
	    compLocs.add(table.getLocation());
	    compFonts.add(table.getFont());
        
        totalAmount.setText("Total Amount: $" + String.format("%.2f", OC.getOrderTotal()));
        totalAmount.setFont(new Font("Serif", Font.BOLD, 20));
        
        //back button
        JButton backButton = new JButton("Back");
        
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	checkoutPage.setVisible(false);
            	setContentPane(menuPage);
                //menuPage.setVisible(true);
            	clearResizeInfo();
                createMenuPage(OC);
            }
        });
        backButton.setBounds(50, 50, 174, 33);
        backButton.setForeground(new Color(255, 255, 255));
        backButton.setBackground(new Color(0, 0, 128));
        checkoutPage.add(backButton); 
        
        //cancel button
        JButton cancelButton = new JButton("Cancel Order");
        
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
         	   checkoutPage.setVisible(false);
         	   clearResizeInfo();
         	   createStartPage();
            }
        });
        cancelButton.setBounds(320, 550, 174, 33);
        cancelButton.setForeground(new Color(255, 255, 255));
        cancelButton.setBackground(new Color(0, 0, 128));
        checkoutPage.add(cancelButton); 
        
        JLabel SuccessOrError = new JLabel();
        SuccessOrError.setBounds(550, 50, 200, 30);
    	checkoutPage.add(SuccessOrError);
        
        //purchase button
        JButton purchaseButton = new JButton("Purchase");
        
        purchaseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(OC.finalizeOrder()) {
                	SuccessOrError.setText("Success: Order Sent");
	               
	            	int delay = 2000; //milliseconds
	            	Timer waitTimer = new Timer(delay, null);
	            	ActionListener taskPerformer = new ActionListener() {
	            	      public void actionPerformed(ActionEvent evt) {
	            	    	checkoutPage.setVisible(false);
	            	    	startPage = null;
	            	    	menuPage = null;
	            	    	checkoutPage = null;
	            	    	clearResizeInfo();
	      	                createStartPage();
	            	      }
	            	  };
	            	waitTimer.addActionListener(taskPerformer);
	            	waitTimer.start();
                }
                else
                	SuccessOrError.setText("ERROR: Order Failed");  	
                	
            }
        });
        purchaseButton.setBounds(770, 550, 174, 33);
        purchaseButton.setForeground(new Color(255, 255, 255));
        purchaseButton.setBackground(new Color(0, 0, 128));
        checkoutPage.add(purchaseButton);     	
        
        configResize(checkoutPage); //save values in vectors and setup listener for resize
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
    	Dimension nameSize = new Dimension(280,30);
    	Dimension minusSize = new Dimension(100,30);
    	
	    sumGrid.removeAll();
	  
	    for (int i=0; i<order.getItemList().size(); i++)
	    {
	    	
	    	 SelectedItem item = order.getItemList().get(i);
	    	 
	    	 JPanel namePanel = new JPanel();
	    	 namePanel.setBackground(new Color(255, 255, 255));
	    	 
	    	 JLabel nameLabel = new JLabel( "  " + item.getItemName() + " x" + item.getQuantity());
	    	 nameLabel.setToolTipText(nameLabel.getText());
	    	 nameLabel.setPreferredSize(new Dimension(280,30));
	    	 nameLabel.setForeground(new Color(0, 0, 128));

	    	 namePanel.add(nameLabel);
	    	 sumGrid.add(namePanel);
	    	 
	    	 comps.add(nameLabel);
	 	     compSizes.add(nameLabel.getPreferredSize());
	 	     compLocs.add(nameLabel.getLocation());
	 	     compFonts.add(nameLabel.getFont());
	    	 
	    	 JPanel minusPanel = new JPanel();
	    	 minusPanel.setBackground(new Color(255, 255, 255));
	    	 
	    	 JButton minusButton = new JButton("-");
	    	 minusButton.setPreferredSize(new Dimension(100,30));
	    	 minusButton.setForeground(new Color(255, 255, 255));
 	    	 minusButton.setBackground(new Color(0, 0, 128));
	    	 minusButton.addActionListener(new ActionListener() {
		 	      @Override
		 	      public void actionPerformed(ActionEvent e) { 
		 	    	 OC.modifyOrder(false, null, item);
		 	    	 updateSummary(sumGrid, order, OC); //update summary each time we remove an item
		 	      } });
	    	 minusPanel.add(minusButton);
	    	 sumGrid.add(minusPanel);	 
	    	 
	    	 comps.add(minusButton);
	 	     compSizes.add(minusButton.getPreferredSize());
	 	     compLocs.add(minusButton.getLocation());
	 	     compFonts.add(minusButton.getFont());
	 	     
	 	     ComponentEvent e = new ComponentEvent(menuPage, 0);
	 	     componentResized(e);
	    }
	    
	    //update render 
	    sumGrid.revalidate();
	    sumGrid.repaint();;	
    }
    
    /*
    Description: This is a helper function to load the images.
    Parameters:
        label - the label the image is attached too
        imagePath - the image path to locate the image in the directory.   
    Returns: ImageIcon for the image.
     */
    public ImageIcon loadImage(JLabel label, String imagePath) {
    	
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(imagePath));
        } catch (IOException e) {
            e.printStackTrace();
        } 
        Image dimg = img.getScaledInstance(label.getWidth(), label.getHeight(),
       	        Image.SCALE_SMOOTH);
        ImageIcon imageIcon = new ImageIcon(dimg);
        imageIcon.setDescription(imagePath);
        return imageIcon;
    }
    
    /*
    Description: This is a helper function to configure the resize for the components of the given page.
    			 It saves the default component sizes, default locations and default fonts. 
    			 Additionally it sets up the resize event listener
    Parameters:
        page - the page (main page, start page or checkout page)   
    Returns: Nothing.
     */
    public void configResize(JPanel page) {
    	System.out.println(page.getComponents().length);
       for (int i = 0; i < page.getComponents().length; i++)
  	   {
    	   	 comps.add(page.getComponent(i));
        	 compSizes.add(page.getComponent(i).getSize());
        	 compLocs.add(page.getComponent(i).getLocation());
        	 compFonts.add(page.getComponent(i).getFont());
  	   }
       page.addComponentListener(this);
    }
    
    
    /*
    Description: small helper function to quickly clear the lists that contain the resize info
    Parameters:
        None 
    Returns: Nothing.
     */
    public void clearResizeInfo()
    {
    	comps.clear();
    	compSizes.clear();
        compLocs.clear();
        compFonts.clear();
    }
    
    /*
    Description: The resize event function that is triggered when the window is resized. 
    			  multiplies the default location, size and font size for the components by the percent the window grew or shrank
    Parameters:
        e - ComponentEvent that contains the page that triggered the event.   
    Returns: Nothing.
     */
    public void componentResized(ComponentEvent e) {
    	
    	Component eComp = e.getComponent();
    	if(eComp instanceof Container) {
    		//Component components[] = ((Container)e.getComponent()).getComponents();
    		
    	    for (int i = 0; i < comps.size(); i++)
    	    {
    	    	Component comp = comps.get(i);
    	    	int newX = (int) (1 + compLocs.get(i).x * (eComp.getWidth()/devScreenSize.getWidth()));
    	    	int newY = (int) (1 + compLocs.get(i).y * (eComp.getHeight()/devScreenSize.getHeight()));
    	    	int newWidth = (int) (1 + compSizes.get(i).width * (eComp.getWidth()/devScreenSize.getWidth()));
    	    	int newHeight = (int) (1 + compSizes.get(i).height * (eComp.getHeight()/devScreenSize.getHeight()));
    	    	
    	    	if(comp.isPreferredSizeSet() == false)
    	    		comp.setBounds(newX, newY, newWidth, newHeight);
    	    	else
    	    	{
    	    		comp.setLocation(newX, newY);
    	    		comp.setPreferredSize(new Dimension(newWidth, newHeight));
    	    	}
    	        
    	        comp.setFont(new Font(compFonts.get(i).getFontName(), compFonts.get(i).getStyle(), (int) (compFonts.get(i).getSize() * ((eComp.getWidth()/devScreenSize.getWidth()) + (eComp.getHeight()/devScreenSize.getHeight()))/2)));
    	        
    	        
    	        if(comp.getClass().getName().equals("javax.swing.JLabel"))
    	        {
    	        	JLabel lcomp = ((JLabel) comp);
    	        	if(lcomp.getIcon() != null)
    	        		((JLabel) comp).setIcon(loadImage(lcomp, ((ImageIcon)lcomp.getIcon()).getDescription()));
    	        }
    	    }
    	    eComp.revalidate();
            eComp.repaint();
    	}
        
    }

	@Override
	public void componentMoved(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentShown(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentHidden(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}
}
