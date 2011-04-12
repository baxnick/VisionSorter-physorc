import org.jconfig.Configuration;
import org.jconfig.ConfigurationManager;
import org.jconfig.utils.CategoryBeanMapper;

/*
 * CategoryBeanMapperDemo.java
 *
 * Created on 29. September 2004, 11:58
 */

/**
 *
 * @author  Administrator
 */
public class CategoryBeanMapperDemo {
    
    /** Creates a new instance of CategoryBeanMapperDemo */
    public CategoryBeanMapperDemo() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Configuration config = ConfigurationManager.getConfiguration("simple");
        JDBCBeanHelper helper = new JDBCBeanHelper();
        CategoryBeanMapper.mapBean(helper,"JDBC","simple");
        System.out.println("URL:"+helper.getURL());
        System.out.println("port:"+helper.getPORT());
    }
    
}
