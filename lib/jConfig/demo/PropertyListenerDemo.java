import org.jconfig.*;
import org.jconfig.event.*;

public class PropertyListenerDemo implements PropertyListener {
    
    private static final Configuration configuration = ConfigurationManager.getConfiguration();
    
    public static void main(String[] args) {
        configuration.addPropertyListener(new PropertyListenerDemo());
        configuration.setProperty("Does not matter","hello world");
    }
    
    /**
     * @see org.jconfig.event.PropertyListener#propertyChanged(org.jconfig.event.PropertyChangedEvent)
     */
    public void propertyChanged(PropertyChangedEvent e) {
        System.out.println("property name '"+e.getPropertyName() +"' has changed.");
        System.out.println("old value '" + e.getOldValue() + "'");
        System.out.println("new value '" + e.getNewValue() + "'");
    }
}
