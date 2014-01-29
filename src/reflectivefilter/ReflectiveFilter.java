package reflectivefilter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextField;

/**
 * A filter that uses reflection to allow JTextField input from multiple sources
 * to be compared against member data of an object. Each text field can be
 * linked to a particular "getter" public method of the object, and then the
 * filter methods can be run to determine if the strings within the text fields
 * are contained within the strings returned by the linked object getters.
 *
 * The advantage to this method is that this ReflectiveFilter is stand-alone,
 * adaptive, and can be used without hard coding filtering logic for individual
 * data sets.
 *
 * Developer notes: The purpose of this filtering method is primarily academic,
 * but also could be used for advanced filtering where the overhead of a
 * database is undesirable, but single String filtering doesn't apply.
 *
 * //TODO: Implement a way to accept a list of objects, and determine pass/fail
 * filter results for each one, then return the result list. The advantage to
 * this method is that if given O objects and G getters, O*G getter method calls
 * will occur. If the getter method calls can be consolidated, we can bring that
 * down to G calls, and simply iterate over the objects to be filtered per
 * usual.
 *
 *
 * @author Jacob Dorman
 */
public class ReflectiveFilter {

    private HashMap<JTextField, Method> map;
    private HashMap<JTextField, Boolean> lastResultMap;

    /**
     * Create a new ReflectiveFilter, ready for a data source and jTextField.
     */
    public ReflectiveFilter() {
        map = new HashMap<>();
        lastResultMap = new HashMap<>();
    }

    /**
     * Adds the passed JTextField/Getter method pair to the map of filters
     * that will be validated when passesFilter() is called.
     *
     * Example:
     * reflectiveFilter.addDataSource(jTextField1, TestClass.class, "getData");
     *
     * @param textField the JTextField acting as a filter
     * @param c the Class reference for the objects that hold data to be
     * filtered
     * @param getterMethodName the getter method for the target class
     */
    public void addDataSource(JTextField textField, Class c, String getterMethodName) {
        try {
            map.put(textField, c.getDeclaredMethod(getterMethodName));
        } catch (NoSuchMethodException | SecurityException ex) {
            Logger.getLogger(ReflectiveFilter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * A stand-alone filtering method that allows for one-off filtering. Returns
     * true if the filterText is found within the getter of the passed object.
     *
     * @param object the object the getter method will be called on
     * @param filterText the text present in the filter
     * @param getterName the data retrieval method name to be called on the
     * object
     * @return true if the filter text is within the string returned by the
     * getter method
     */
    public static boolean passesStandAloneFilter(Object object, String filterText, String getterName) {
        String data = "";
        try {
            data = (String) object.getClass().getMethod(getterName).invoke(object);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(ReflectiveFilter.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (!data.toLowerCase().contains(filterText.toLowerCase())) {
            return false;
        }
        return true;
    }

    /**
     * This method attempts to verify that each JTextField's contents are
     * present in the object's data, and returns true if they are.
     *
     * @param object the object to check against active filters
     * @return true if every mapped JTextField's text is contained within the
     * passed object's data
     */
    private boolean passesFilter(Object object) {
        boolean allPass = true; //Track whether every filter passes or not
        //Iterate over each filter, checking against the data of the object
        for (JTextField tf : map.keySet()) {
            String data = "";
            try {
                data = (String) map.get(tf).invoke(object);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                Logger.getLogger(ReflectiveFilter.class.getName()).log(Level.SEVERE, null, ex);
            }
            //Record whether the filter was passed or not
            if (!data.toLowerCase().contains(tf.getText().toLowerCase())) {
                lastResultMap.put(tf, Boolean.FALSE);
                allPass = false;
            } else {
                lastResultMap.put(tf, Boolean.TRUE);
            }
        }
        return allPass;
    }

    /**
     * @return the results of the last filter, and whether each field passed
     * or not
     */
    public HashMap<JTextField, Boolean> getLastResultMap() {
        return lastResultMap;
    }

    public static void main(String[] args) {
        //Create text fields to emulate user input
        JTextField jTextField = new JTextField("first piece");
        JTextField jTextField2 = new JTextField("second piece");
        //Create the test object that will hold a string
        TestClass testClass = new TestClass("this is the first piece of data",
                "this is the second piece of data");
        //Initialize a new reflective filter
        ReflectiveFilter reflectiveFilter = new ReflectiveFilter();
        //Add the text fields as attached filters, and link them with the
        //getter methods for each member of the class
        reflectiveFilter.addDataSource(jTextField, TestClass.class, "getTestData1");
        reflectiveFilter.addDataSource(jTextField2, TestClass.class, "getTestData2");
        //Store whether the testClass object contents pass the filter or not
        boolean passesFilter = reflectiveFilter.passesFilter(testClass);
        //Print the result
        System.out.println("Overall pass: " + passesFilter);
        System.out.println("");
        HashMap<JTextField, Boolean> lastResultMap1 = reflectiveFilter.getLastResultMap();
        for (JTextField tf : lastResultMap1.keySet()) {
            System.out.println("Textfield containing: \"" + tf.getText() + "\" passes: " + lastResultMap1.get(tf));
        }
    }
}
