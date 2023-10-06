package PacchettoFlotta.model.models;/*
package PacchettoFlotta.server.model;

import java.util.ArrayList;
import java.util.List;

public class ChargingStation {
    GPSModel stationMN = new GPSModel(45.16657032434162,10.852456452227955,0); //A22 --- MN nord
    GPSModel stationSUST = new GPSModel(45.069509, 11.021419, 0); //sustinente
    GPSModel stationCEREA = new GPSModel(45.192029, 11.213466, 0); //cerea  --- Tosano
    GPSModel stationMIRA = new GPSModel(44.87995, 11.076689, 0); //Mirandola --- Busuoli

    public List<GPSModel> stations = new ArrayList();



}

Fare una classe così non ha senso, Charging Station non è altro che un GPSModel (già creato),
dato che i suoi campi non sarebbero altro che quelli già visti (latitudine,longitudine,elevazione(?)),
di conseguenza vi è solamente da trovare il modo di creare una lista globale in cui siano contenute
le coordinate gps di queste stazioni di ricarica durante i tragitti.

*/