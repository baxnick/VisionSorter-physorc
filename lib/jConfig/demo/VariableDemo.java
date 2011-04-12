import org.jconfig.*;
import org.jconfig.parser.*;

/**
 * This class demonstrates the vary ways to use variables
 *
 * @auhtor Andreas Mecky andreasmecky@yahoo.de
 * @auhtor Terry Dye terrydy@yahoo.com
 */
public class VariableDemo {
            
    public static void main(String[] arg) {        
        // let the ConfigurationManager read in the vardemo_config.xml
        Configuration configuration = ConfigurationManager.getConfiguration("vardemo");
        // this one uses a simple variable
        String myProp = configuration.getProperty("upload_dir");
        System.out.println("upload_dir:"+myProp);
        // this one uses the user home directory accessed through a system property
        myProp = configuration.getProperty("next_dir");
        System.out.println("next_dir:"+myProp);
        // and this one use the temp-dir defined by a system property
        myProp = configuration.getProperty("third_dir");
        System.out.println("third_dir:"+myProp);
        
    }
}
