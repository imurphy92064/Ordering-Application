package fastfood.item;

import java.sql.ResultSet;

public class ItemFactory {

    //returns null if no given type matches "selected item" or "menu item"
    public static Item createItem(String type , ResultSet rs, MenuItem mItem){
        switch(type){
            case "selected item":
                if(rs == null)
                    return new SelectedItem(mItem.getItemID(), mItem.getItemName(), mItem.getDescription(), mItem.getPrice(), 1, mItem.getCalories());
                else
                    try{
                        return new SelectedItem(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getDouble(4), rs.getInt(5), rs.getInt(6));
                    }catch(Exception e){
                        e.printStackTrace();
                        System.out.println(e.getMessage());
                    }
            case "menu item":
                try{
                    return new MenuItem(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getDouble(4), rs.getInt(5));
                }catch(Exception e){
                    e.printStackTrace();
                    System.out.println(e.getMessage());
                }
            default:
                throw new IllegalArgumentException("Unknown Item Type was Passed: "+type);
        }
    }
}
