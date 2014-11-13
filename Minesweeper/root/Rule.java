package root;

import javax.swing.*;
import java.awt.*;

/**
 * Created by MB on 10/29/2014.
 */
class Rule extends JPanel {


    private JLabel[] indicateurs;
        public Rule(int xOry,int length){
            super();
            indicateurs=null;
            if(xOry == 1){
                JLabel placeholder = new JLabel();
                placeholder.setText("x-y");
                placeholder.setForeground(Color.BLACK);
                placeholder.setFont(new Font("Arial",Font.BOLD,8));
                add(placeholder);
            }
            indicateurs=  new JLabel[length];
            for(int i=0;i<length;i++){
                JLabel num = new JLabel();
                num.setText(Integer.toString(i));
                num.setForeground(Color.BLUE);
                num.setFont(new Font("Serif", Font.PLAIN,10));
                indicateurs[i] = num;
                add(num);
            }
        }

}
