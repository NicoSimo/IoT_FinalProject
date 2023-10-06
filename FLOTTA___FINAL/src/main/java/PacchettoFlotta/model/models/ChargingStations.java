package PacchettoFlotta.model.models;

public class ChargingStations{

    private String ID;

    private String name;

    private double[] position;

    private double latitude;

    private double longitude;

    public ChargingStations() {
    }

    public ChargingStations(String ID, String name, double[] position) {
        this.ID = ID;
        this.name = name;
        this.position = position;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return position[0];
    }

    public void setLatitude(double latitude) {
        this.latitude = position[0];
    }

    public double getLongitude() {
        return position[1];
    }

    public void setLongitude(double longitude) {
        this.longitude = position[1];
    }

    public double[] getPosition() {
        return position;
    }

    public void setPosition(double[] position) {
        this.position = position;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ChargerDescriptor{");
        sb.append("ID=").append(ID);
        sb.append("name=").append(name);
        sb.append("latitude=").append(latitude);
        sb.append(", longitude=").append(longitude);
        sb.append('}');
        return sb.toString();
    }
}
