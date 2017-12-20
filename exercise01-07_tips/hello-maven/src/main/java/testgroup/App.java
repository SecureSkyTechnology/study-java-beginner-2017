package testgroup;
import com.google.common.base.Strings;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.swing.JOptionPane;

public class App {
    public static String getYourName(String reslocation) throws IOException {
        try (InputStream is = App.class.getClassLoader().getResourceAsStream(reslocation)) {
            Properties prop = new Properties();
            prop.load(is);
            return prop.getProperty("yourname");
        }
    }
    public static void main(String[] args) throws IOException {
        String repeatName = getYourName("config/myconfig.properties");
        String msg = "こんにちは, " + Strings.repeat(repeatName + " ", 5);
        System.out.println(msg);
        JOptionPane.showMessageDialog(null, msg);
    }
}
