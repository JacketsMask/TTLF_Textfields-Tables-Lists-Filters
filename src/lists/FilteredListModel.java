package lists;

import java.util.ArrayList;
import javax.swing.AbstractListModel;
import javax.swing.JTextField;

/**
 * A list model that updates items visible in the list to the content
 * dynamically typed into a related JTextField, irrespective of character case.
 *
 * For simplicity's sake currently the only way to modify the full element list
 * is to retrieve the data ArrayList with getData() and then work on that
 * ArrayList directly. 
 *
 * Elements added to the list won't be shown until the filter is refreshed,
 * though this process can be forced with the clearFilter() method.
 *
 * @author Jacob Dorman
 */
public class FilteredListModel<G> extends AbstractListModel {

    //Holds all list data, whether it be visible or not
    private ArrayList<G> data;
    //Holds visible list data if the search is active
    private ArrayList<G> visibleData;
    //Whether a filter search is active or not
    private boolean searchCompleted;
    //The text field that acts as a filter to the visible list data
    private JTextField filterTextField;

    /**
     * Creates a new FilteredListModel, and assigns the passed JTextField as the
     * filter for this list.
     *
     * @param filterTextField the filter of the list
     */
    public FilteredListModel(final JTextField filterTextField) {
        this.data = new ArrayList<>();
        visibleData = new ArrayList<>();
        searchCompleted = false;
        this.filterTextField = filterTextField;
        //Add listener to JTextField to update filter
        filterTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                updateVisibleListFromFilter();
            }
        });
    }

    /**
     * Searches the stored data for the passed string, and returns an ArrayList
     * containing all resulting matches.
     *
     * @param searchTerm the term to search with
     * @return an ArrayList containing all matches
     */
    private void updateVisibleListFromFilter() {
        //Get the search query from the JTextField
        String searchQuery = filterTextField.getText();
        if (searchQuery.equals("")) {
            searchCompleted = false;
            fireContentsChanged(this, 0, 0);
            return;
        }
        //Create an ArrayList to store query results
        ArrayList<G> results = new ArrayList<>();
        //Make the search term lower case for easier searching
        searchQuery = searchQuery.toLowerCase();
        //Search through the list, looking for things that contain the search term
        for (G nextElement : data) {
            String stringOfElement = nextElement.toString();
            //Check to see if the search term is included in the list
            if (stringOfElement.toLowerCase().contains(searchQuery)) {
                //Add the resulting element to the list
                results.add(nextElement);
            }
        }
        //Clear the visible data
        visibleData.clear();
        //Add each result to the super (visible) vector, respecting original order
        for (G e : results) {
            visibleData.add(0, e);
        }
        searchCompleted = true;
        fireContentsChanged(this, 0, 0);
    }

    /**
     * @return the ArrayList containing all this lists elements
     */
    public ArrayList<G> getDataArrayList() {
        return data;
    }

    /**
     * If you really want to completely change the data stored for the list,
     * pass an ArrayList to this.
     *
     * @param data the new ArrayList to be used as list elements
     */
    public void setDataArrayList(ArrayList<G> data) {
        this.data = data;
        //Clears the filtering text field
        filterTextField.setText("");
        clearFilter();
    }

    /**
     * Resets the filter JTextField's text to an empty string, and then
     * refreshes visible list.
     */
    public void clearFilter() {
        filterTextField.setText("");
        //Clear the visible data
        visibleData.clear();
        searchCompleted = false;
    }

    /**
     * Adds the passed data element to the list.
     * @param data 
     */
    public void addDataElement(G data) {
        this.data.add(data);
    }

    /**
     * Attempts to get the data element at the passed index.
     * @param index
     * @return the element at the passed index
     */
    public G getDataElement(int index) {
        return this.data.get(index);
    }

    /**
     * @return the number of elements in the list
     */
    @Override
    public int getSize() {
        if (searchCompleted) {
            return visibleData.size();
        } else {
            return data.size();
        }
    }

    /**
     * @param index
     * @return the element at the passed index
     */
    @Override
    public Object getElementAt(int index) {
        if (searchCompleted) {
            return visibleData.get(index);
        } else {
            return data.get(index);
        }
    }
}
