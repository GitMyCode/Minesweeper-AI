package root.ENUM;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by MB on 10/30/2014.
 */
public enum CASE {

    EMPTY(0),
    ONE(1),
    TWO(2),
    TREE(3),
    FOUR(4),
    FIVE(5),
    SIX(6),
    SEVEN(7),
    EIGHT(8),
    MINE(9),
    UNDISCOVERED(10),
    FLAG(11),
    ERROR_FLAG(12);

    CASE(int i){
        indexValue = i;
        java.net.URL imageUrl = getClass().getResource("../img/j"+i+".gif");
        image =  new ImageIcon(imageUrl).getImage();
    }

    private static final Map<Integer, CASE> intToTypeMap = new HashMap<Integer, CASE>();
    static {
        for (CASE type : CASE.values()) {
            intToTypeMap.put(type.indexValue, type);
        }
    }
    public static CASE caseFromInt(int i ){
        return intToTypeMap.get(i);
    }




    public Image image;
    public int indexValue;
    final String  pathfile= "../root.img/j";




}
