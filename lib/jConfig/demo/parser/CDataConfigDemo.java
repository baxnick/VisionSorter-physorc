package parser;

/**
 *
 * @author  Andreas Mecky andreasmecky@yahoo.de
 * @author  Terry Dye terrydyq@yahoo.com
 */
import org.jconfig.*;

public class CDataConfigDemo {
        
    public CDataConfigDemo() {
    }
        
    public static void main(String[] args) {
        Configuration config = ConfigurationManager.getConfiguration("advanceddemo");
        String value = config.getProperty("escapetest");
        System.out.println("value:"+value);
        value = config.getProperty("hello");
        System.out.println("value:"+value);
        value = config.getProperty("hello",null,"inner");
        // we do not have this one but in the general category
        System.out.println("value:"+value);
    }
    
}
