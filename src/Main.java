import javax.swing.JFrame;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Aim Labs but Bad");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(960, 580);
        frame.setLocationRelativeTo(null);
        // create a DisplayPanel object
        DisplayPanel panel = new DisplayPanel();
        // add it to the frame
        frame.add(panel);
        // call setVisible after everything else
        frame.setVisible(true);
    }
}