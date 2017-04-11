package BeerFinder;

/**
 * This entity class wraps the API responses into many of these objects.
 * @author lukasmohs
 */
public class Bar {
    private String name;
    private String address;
    private String lat;
    private String lon;
    private String price;

    /**
     * Constructor
     * @param name
     * @param address
     * @param lat
     * @param lon
     * @param price 
     */
    public Bar(String name, String address, String lat, String lon, String price) {
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lon = lon;
        this.price = price;
    }

    /**
     * 
     * @return name of the bar as String
     */
    public String getName() {
        return name;
    }
    /**
     * 
     * @return address of the bar as String
     */
    public String getAddress() {
        return address;
    }
    /**
     * 
     * @return latitude of the bar as String
     */
    public String getLat() {
        return lat;
    }
    /**
     * 
     * @return longitude of the bar as String
     */
    public String getLon() {
        return lon;
    }
    /**
     * 
     * @return price categorization of the bar
     */
    public String getPrice() {
        return price;
    }
    
    
}
