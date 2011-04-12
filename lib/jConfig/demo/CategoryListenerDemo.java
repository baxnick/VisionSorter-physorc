import org.jconfig.*;
import org.jconfig.Configuration;
import org.jconfig.event.*;


/**
 * Demonstrates a possible use of the CategoryListener.
 * This actually shows via the output how the events are
 * sent first to the PropertyListeners and then CategoryListeners.
 * 
 * @author Andreas Mecky <andreas.mecky@xcom.de>
 * @author Terry Dye <terry.dye@xcom.de>
 * @since Aug 27, 2003 10:18:36 AM
 */
public class CategoryListenerDemo implements CategoryListener {

	private static final Configuration configuration = ConfigurationManager.getConfiguration();
        
	public static void main(String[] args) {
		CategoryListenerDemo catDemo = new CategoryListenerDemo();
		configuration.addPropertyListener(catDemo);
		configuration.getCategory().addCategoryListener(catDemo);
		configuration.setProperty("what to say","hello world");
	}


	/**
	 * @see org.jconfig.event.CategoryListener#categoryChanged(org.jconfig.event.CategoryChangedEvent)
	 */
	public void categoryChanged(CategoryChangedEvent e) {
		System.out.println("Category " + e.getCategory().getCategoryName() + " has changed."); 
	}

	/**
	 * @see org.jconfig.event.PropertyListener#propertyChanged(org.jconfig.event.PropertyChangedEvent)
	 */
	public void propertyChanged(PropertyChangedEvent e) {
		System.out.println();
		System.out.println("property name '"+e.getPropertyName() +"' has changed.");
		System.out.println("old value '" + e.getOldValue() + "'");
		System.out.println("new value '" + e.getNewValue() + "'"); 
	}

}
