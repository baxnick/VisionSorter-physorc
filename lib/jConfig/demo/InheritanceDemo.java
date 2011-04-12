import org.jconfig.*;

public class InheritanceDemo {

    private static final Configuration configuration =
        ConfigurationManager.getConfiguration();

    public static void main(String[] args) {
        String myProp = configuration.getProperty("MyProp",null,"JDBC");
        System.out.println("MyProp:"+myProp);
    }
}
