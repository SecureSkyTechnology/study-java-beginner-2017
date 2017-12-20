package testgroup;
import com.google.common.base.Strings;
import javax.swing.JOptionPane;

public class App {
    public static void main(String[] args) {
        String msg = "Hello, " + Strings.repeat("World ", 5);
        System.out.println(msg);
        JOptionPane.showMessageDialog(null, msg);
    }
}
