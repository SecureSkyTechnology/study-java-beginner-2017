import com.google.common.base.Strings;
import javax.swing.JOptionPane;

public class HelloGuava {
    public static void main(String[] args) {
        String msg = "Hello, " + Strings.repeat("World ", 5);
        // コンソール出力
        System.out.println(msg);
        // GUIメッセージボックス出力
        JOptionPane.showMessageDialog(null, msg);
    }
}