package models;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static models.Car.CarType;
import static models.Car.FuelType;

public class Detection {
    private final Car car;                  // the car that was detected
    private final String city;              // the name of the city where the detector was located
    private final LocalDateTime dateTime;   // date and time of the detection event

    /* Representation Invariant:
     *      every Detection shall be associated with a valid Car
     */

    public Detection(Car car, String city, LocalDateTime dateTime) {
        this.car = car;
        this.city = city;
        this.dateTime = dateTime;
    }

    /**
     * Parseert detectie-informatie van een tekstregel over een auto die een milieugecontroleerde zone van een opgegeven stad is binnengekomen.
     * Het formaat van de tekstregel is: kentekenplaat, stad, datumTijd.
     * De kentekenplaat moet overeenkomen met een auto uit de verstrekte lijst.
     * Als er geen overeenkomende auto kan worden gevonden, wordt een nieuwe Car geïnstantieerd met de opgegeven kentekenplaat en toegevoegd aan de lijst
     * (behalve het kentekenplaatnummer zal er geen andere informatie beschikbaar zijn over deze auto)
     * @param textLine
     * @param cars     een lijst van bekende auto's, geordend en doorzoekbaar op kentekenplaat
     *                 (dwz de indexOf-methode van de lijst houdt alleen rekening met de kentekenplaat bij het vergelijken van auto's)
     * @return een nieuwe Detection-instantie met de verstrekte informatie
     * of null als de tekstregel corrupt of onvolledig is
     */
    public static Detection fromLine(String textLine, List<Car> cars) {
        Detection nieuweDetectie = null;

        cars.indexOf(cars);

        // TODO zet de informatie in de tekstregel om in een nieuwe Detection-instantie
        //  gebruik cars.indexOf om de auto te vinden die is gekoppeld aan de kentekenplaat van de detectie
        //  als er geen auto kan worden gevonden, wordt een nieuwe Car geïnstantieerd en toegevoegd aan de lijst en gekoppeld aan de detectie

        return nieuweDetectie;
    }


    /**
     * Validates a detection against the purple conditions for entering an environmentally restricted zone
     * I.e.:
     * Diesel trucks and diesel coaches with an emission category of below 6 may not enter a purple zone
     * @return a Violation instance if the detection saw an offence against the purple zone rule/
     *          null if no offence was found.
     */
    public Violation validatePurple() {
        // TODO validate that diesel trucks and diesel coaches have an emission category of 6 or above


        return null;
    }

    public Car getCar() {
        return car;
    }

    public String getCity() {
        return city;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }


    @Override
    public String toString() {
        // TODO represent the detection in the format: licensePlate/city/dateTime

        return car+"/"+city+"/"+dateTime;       // replace by a proper outcome
    }

}
