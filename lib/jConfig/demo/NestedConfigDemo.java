import org.jconfig.*;
import org.jconfig.parser.*;
import org.jconfig.handler.*;
/**
 * This is a simple demo showing how to use the nested configuration.
 *
 * @auhtor Andreas Mecky andreasmecky@yahoo.de
 * @auhtor Terry Dye terrydy@yahoo.com
 */
public class NestedConfigDemo {
            
    public static void main(String[] arg) {
        // tell the ConfigurationManager that we want the nested config parser
        System.setProperty("jconfig.parser", NestedConfigParser.class.getName());
        // setup this error handler since it will report errors to the console
        System.setProperty("jconfig.errorhandler", "org.jconfig.error.SimpleErrorHandler");
        // now let the ConfigurationManager read in the nested_demo_config.xml
        Configuration configuration = ConfigurationManager.getConfiguration("nested_demo");
        String myProp = configuration.getProperty("hello");
        System.out.println("hello:"+myProp);
        String more = configuration.getProperty("hello",null,"inner/myinner/moreinner");
        System.out.println("hello:"+more);
        int newsCounter = configuration.getIntProperty("newscounter",-1,"inner/myinner/moreinner");
        if ( newsCounter == 1000 ) {
            System.out.println("We have found the correct value");
        }       
        else {
            System.out.println("we have found:"+newsCounter);
        }
        // now we remove the property
        configuration.removeProperty("hello","inner/myinner/moreinner");
        // this should give us the value for the property hello from the category general
        // since the defined category does no longer contain the property
        more = configuration.getProperty("hello",null,"inner/myinner/moreinner");
        System.out.println("hello:"+more);
        // this one will create all categories starting from this to category
        configuration.setCategory("this/is/a/new/category");
        // all categories are already created
        configuration.setProperty("stuff","great stuff","this/is/a/new/category");
        String val = configuration.getProperty("stuff",null,"this/is/a/new/category");
        System.out.println("stuff:"+val);
        // now let's save the new created config but this time to a different file
        String fileName = System.getProperty("java.io.tmpdir")+"my_test_config.xml";        
        XMLFileHandler handler = new XMLFileHandler(fileName);
	try {
            handler.store(configuration);
            System.out.println("file saved to:"+fileName);
        }
        catch (ConfigurationManagerException cme) {
            cme.printStackTrace();
        }
    }
}
