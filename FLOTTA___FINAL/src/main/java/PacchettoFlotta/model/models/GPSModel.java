package PacchettoFlotta.model.models;

public class GPSModel {

    private double latitude;

    private double longitude;

    private double elevation;

    public GPSModel() {
    }

    public GPSModel(double latitude, double longitude, double elevation) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.elevation = elevation;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getElevation() {
        return elevation;
    }

    public void setElevation(double elevation) {
        this.elevation = elevation;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("GPSCoordinates {");
        sb.append("latitude=").append(latitude);
        sb.append(", longitude=").append(longitude);
        sb.append(", elevation=").append(elevation).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
