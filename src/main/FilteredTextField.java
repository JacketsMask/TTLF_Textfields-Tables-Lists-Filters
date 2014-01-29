package main;

import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JTextField;

/**
 * A FilteredTextField that stores an ArrayList of data that can be filtered by
 * typing into the text field.
 *
 * In order to use this, a JList must be used to display the model.
 *
 * @author Jacob Dorman
 */
public class FilteredTextField<G> extends JTextField {

    //The JList that displays the data
    private JList list;
    //The model storing the visible data
    private DefaultListModel model;
    //The arraylist that holds the sum of the data
    private ArrayList<G> data;

    public FilteredTextField() {
        super();
        model = new DefaultListModel();
        //JList will be added later
        registerListener();
    }

    public FilteredTextField(JList connectedList) {
        super();
        model = new DefaultListModel();
        this.list = connectedList;
        registerListener();
    }

    public void setData(ArrayList<G> data) {
        this.data = data;
        fillModelFromArrayList(this.data);
    }

    public ArrayList<G> getData() {
        return data;
    }

    /**
     * Register the listener that updates the visible elements of the list.
     */
    private void registerListener() {
        this.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                updateVisibleElements(evt);
            }
        });
    }

    private void updateVisibleElements(java.awt.event.KeyEvent evt) {
        //Get list, filter list, update visible list
        String searchQuery = this.getText();
        ArrayList<G> results = this.searchData(searchQuery);
        fillModelFromArrayList(results);
    }

    /**
     * Fills the model for the list with the contains of the passed ArrayList.
     *
     * @param entries an ArrayList of entries
     */
    private void fillModelFromArrayList(ArrayList<G> entries) {
        model.clear();
        for (G e : entries) {
            model.add(0, e);
        }
        list.clearSelection();
    }

    public JList getConnectedList() {
        return list;
    }

    public void setConnectedList(JList connectedList) {
        this.list = connectedList;
        this.list.setModel(model);
    }

    /**
     * Clears the search filter.
     */
    public void clearFilter() {
        this.setText("");
        ArrayList<G> results = this.searchData("");
        fillModelFromArrayList(results);
    }

    public ArrayList<G> searchData(String searchTerm) {
        ArrayList<G> results = new ArrayList<>();
        //Make the search term lower case for easier searching
        searchTerm = searchTerm.toLowerCase();
        //Search through the list, looking for things that contain the search term
        for (G nextElement : data) {
            String stringOfElement = nextElement.toString();
            //Check to see if the search term is included in the list
            if (stringOfElement.toLowerCase().contains(searchTerm)) {
                //Add the password/source to the result list
                results.add(nextElement);
            }
        }
        return results;
    }
}
