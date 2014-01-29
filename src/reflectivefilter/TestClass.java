package reflectivefilter;

/**
 * A test class for use in demonstrating the reflective filter.
 *
 * @author Jacob Dorman
 */
public class TestClass {

    private String testData1;
    private String testData2;
    
    public TestClass(String testData1, String testData2) {
        this.testData1 = testData1;
        this.testData2 = testData2;
    }

    public String getTestData1() {
        return testData1;
    }

    public String getTestData2() {
        return testData2;
    }
}
