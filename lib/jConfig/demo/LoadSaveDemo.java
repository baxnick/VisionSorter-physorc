import org.jconfig.handler.*;
import org.jconfig.*;
import java.io.File;

public class LoadSaveDemo {

    private static final ConfigurationManager cm =
        ConfigurationManager.getInstance();

    public static void main(String[] args) {
        File file = new File("test.xml");
        XMLFileHandler handler = new XMLFileHandler();
        handler.setFile(file);
        try {
            System.out.println("trying to load file");
            cm.load(handler,"myConfig");
            System.out.println("file successfully processed");
            Configuration config = ConfigurationManager.getConfiguration("myConfig");
            config.setProperty("TEST","Added property");
            System.out.println("trying to save file");
            cm.save(handler,config); 
            System.out.println("file successfully saved");
            System.out.println("TEST:"+config.getProperty("TEST"));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
