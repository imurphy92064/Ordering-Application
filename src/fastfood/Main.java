package fastfood;

import fastfood.controllers.DBController;
import fastfood.controllers.UIController;

public class Main {
    static UIController uiCon;
    static DBController dbCon;
    private static String name =  null;

    public static void main(String[] args){
        dbCon = new DBController();

        if(dbCon.dbConIsValid()){
            uiCon = new UIController(dbCon);
            uiCon.setVisible(true);
        }
    }
}