package parser;

/**
 *
 * @author  Andreas Mecky andreasmecky@yahoo.de
 * @author  Terry Dye terrydyq@yahoo.com
 */
import org.jconfig.*;

public class NestedCategoryDemo {
    
    
    public NestedCategoryDemo() {
    }
        
    public static void main(String[] args) {
        Configuration config = ConfigurationManager.getConfiguration("nesteddemo");
        String value = config.getProperty("hello",null,"inner/myinner/moreinner");
        System.out.println("value:"+value);
        value = config.getProperty("hello");
        System.out.println("value:"+value);
    }
    
}
