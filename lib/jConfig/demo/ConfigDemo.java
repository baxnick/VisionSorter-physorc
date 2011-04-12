import org.jconfig.*;
/**
 * This is a simple demo showing the basic usage
 *
 * @auhtor Andreas Mecky andreasmecky@yahoo.de
 * @auhtor Terry Dye terrydy@yahoo.com
 */
public class ConfigDemo {
    
    private static final Configuration configuration = ConfigurationManager.getConfiguration("simple");
    
    public static void main(String[] arg) {
        // get a property from the category general (the default category)
        String myProp = configuration.getProperty("MyProp");
        System.out.println("MyProp:"+myProp);
        // now a property from the category JDBC
        String jdbcUser = configuration.getProperty("USER",null,"JDBC");
        System.out.println("jdbcUser:"+jdbcUser);
        // let's get a primitive type
        int newsCounter = configuration.getIntProperty("NewsCounter",-1);
        if ( newsCounter == 10 ) {
            System.out.println("We have found the correct value");
        }
        // and now a boolean property
        boolean showNews = configuration.getBooleanProperty("showNews",false);
        if ( showNews ) {
            System.out.println("We have to show the news");
        }
    }
}
