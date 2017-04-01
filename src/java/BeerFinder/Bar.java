package BeerFinder;

/**
 *
 * @author lukasmohs
 */
public class Bar {
    private String name;
    private String address;
    private String lat;
    private String lon;
    private String price;

    public Bar(String name, String address, String lat, String lon, String price) {
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lon = lon;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getLat() {
        return lat;
    }

    public String getLon() {
        return lon;
    }

    public String getPrice() {
        return price;
    }
    
    
}
