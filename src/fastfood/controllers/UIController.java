package fastfood.controllers;

import fastfood.item.MenuItem;

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
    private final JLabel lblNewLabel = new JLabel("Food Menu:");
    private JTable table;

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
        lblNewLabel.setBounds(232, 51, 71, 14);
        contentPane.add(lblNewLabel);

        JPanel panel = new JPanel();
        panel.setBounds(167, 92, 213, 89);
        contentPane.add(panel);

        DBController db = new DBController();
        Vector<MenuItem> items = db.getMenuItems();
        Vector<String> itemNames = new Vector<>();
        Vector<String> prices = new Vector<>();
        processMenuItems(items, itemNames, prices);


        JList itemList = new JList();
        itemList.setModel(new AbstractListModel() {
            Vector<String> values = itemNames;
            public int getSize() {
                return values.size();
            }
            public Object getElementAt(int index) {return values.get(index);}
        });

        itemList.setSelectionModel(new DefaultListSelectionModel() {
            public void setSelectionInterval(int index0, int index1) {
                if (isSelectedIndex(index0))
                    super.removeSelectionInterval(index0, index1);
                else
                    super.addSelectionInterval(index0, index1);
            }
        });

        //list.getSelectionModel().addSelectionInterval(0, ABORT);
        panel.add(itemList);

        JList priceList = new JList();
        priceList.setEnabled(false);
        priceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        priceList.setModel(new AbstractListModel() {
            Vector<String> values = prices;
            public int getSize() {
                return values.size();
            }
            public Object getElementAt(int index) {return values.get(index);}
        });
        panel.add(priceList);

        JList quantityList = new JList();
        quantityList.setEnabled(false);
        quantityList.setModel(new AbstractListModel() {
            String[] values = new String[] {"x0", "x0", "x0", "x0"};
            public int getSize() {
                return values.length;
            }
            public Object getElementAt(int index) {
                return values[index];
            }
        });
        quantityList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(quantityList);

        JList plusList = new JList();
        plusList.setModel(new AbstractListModel() {
            String[] values = new String[] {"+", "+", "+", "+"};
            public int getSize() {
                return values.length;
            }
            public Object getElementAt(int index) {
                return values[index];
            }
        });
        plusList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(plusList);

        JList minusList = new JList();
        minusList.setFont(new Font("Times New Roman", Font.PLAIN, 13));
        minusList.setModel(new AbstractListModel() {
            String[] values = new String[] {"-", "-", "-", "-"};
            public int getSize() {
                return values.length;
            }
            public Object getElementAt(int index) {
                return values[index];
            }
        });
        minusList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(minusList);

        plusList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if(e.getValueIsAdjusting()){
                    int index = plusList.getSelectedIndex();
                    plusList.removeSelectionInterval(index, index);

                    String[] newValues = new String[quantityList.getModel().getSize()];

                    for(int i = 0; i< quantityList.getModel().getSize(); i++)
                    {
                        if(i != index)
                            newValues[i] = quantityList.getModel().getElementAt(i).toString();
                        else
                        {
                            String str = quantityList.getModel().getElementAt(i).toString();
                            int num = Integer.parseInt(str.substring(1)) + 1;
                            System.out.println(num);
                            newValues[i] = "x"+ num;
                        }
                    }
                    quantityList.setModel(new AbstractListModel() {

                        String[] values = newValues;
                        public int getSize() {
                            return values.length;
                        }
                        public Object getElementAt(int index) {
                            return values[index];
                        }
                    });
                }
            }
        });

        minusList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                int index = minusList.getSelectedIndex();
                minusList.removeSelectionInterval(index, index);

                if(e.getValueIsAdjusting() && index != -1 && !quantityList.getModel().getElementAt(index).toString().equals("x0")){

                    String[] newValues = new String[quantityList.getModel().getSize()];

                    for(int i = 0; i< quantityList.getModel().getSize(); i++)
                    {
                        if(i != index)
                            newValues[i] = quantityList.getModel().getElementAt(i).toString();
                        else
                        {
                            String str = quantityList.getModel().getElementAt(i).toString();
                            int num = Integer.parseInt(str.substring(1)) - 1;
                            System.out.println(num);
                            newValues[i] = "x"+ num;
                        }
                    }
                    quantityList.setModel(new AbstractListModel() {

                        String[] values = newValues;
                        public int getSize() {
                            return values.length;
                        }
                        public Object getElementAt(int index) {
                            return values[index];
                        }
                    });
                }
            }
        });

        JLabel lblYouSelected = new JLabel("You Selected: ");
        lblYouSelected.setBounds(399, 67, 394, 33);

        contentPane.add(lblYouSelected);

        JMenuBar menuBar = new JMenuBar();
        menuBar.setBounds(10, 33, 99, 22);
        contentPane.add(menuBar);

        JMenu mnNewMenu = new JMenu("Hamburger");
        menuBar.add(mnNewMenu);

        JMenuItem mntmNewMenuItem = new JMenuItem("$5");
        mntmNewMenuItem.setBounds(29, 123, 135, 27);
        //contentPane.add(mntmNewMenuItem);

        mnNewMenu.add(mntmNewMenuItem);

        JTable table = new JTable(new DefaultTableModel(new Object[]{"0", "1", "2"}, 0));
        table.setEnabled(false);
        DefaultTableModel Tmodel = (DefaultTableModel) table.getModel();
        Tmodel.addRow(new Object[]{"Items", "Quantity", "Price"});
        table.setSize(260, 100);
        table.setLocation(24, 226);
        contentPane.add(table);

        JButton btnNewButton = new JButton("Check Out");
        btnNewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Tmodel.setRowCount(1);

                for(int x : itemList.getSelectedIndices())
                {
                    String I = itemList.getModel().getElementAt(x).toString();
                    String Q = quantityList.getModel().getElementAt(x).toString();
                    String P = "$" + String.valueOf(Integer.parseInt(Q.substring(1)) * Integer.parseInt(priceList.getModel().getElementAt(x).toString().substring(1)));
                    Tmodel.addRow(new Object[]{I, Q, P});
                }
            }
        });
        btnNewButton.setBounds(175, 192, 174, 23);
        contentPane.add(btnNewButton);

    }

    private void processMenuItems(Vector<MenuItem> items, Vector<String> name, Vector<String> prices){
        for(int i=0; i < items.size(); i++){
            name.add(items.get(i).getItemName());
            prices.add(String.valueOf(items.get(i).getPrice()));
        }
    }
}
