package models;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
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
     *
     * @param textLine
     * @param cars     een lijst van bekende auto's, geordend en doorzoekbaar op kentekenplaat
     *                 (dwz de indexOf-methode van de lijst houdt alleen rekening met de kentekenplaat bij het vergelijken van auto's)
     * @return een nieuwe Detection-instantie met de verstrekte informatie
     * of null als de tekstregel corrupt of onvolledig is
     */
    public static Detection fromLine(String textLine, List<Car> cars) {
        Detection nieuweDetectie = null;

        String[] detectionParts = textLine.split(",");

        // Zorg ervoor dat de tekstregel de verwachte indeling heeft (kentekenplaat, stad, datumTijd)
        if (detectionParts.length == 3) {
            String carPlate = detectionParts[0].trim();
            String city = detectionParts[1].trim();
            String dateTimeStr = detectionParts[2].trim();

            // Probeer de datumtijd te parsen naar een LocalDateTime-object
            LocalDateTime dateTime = null;
            try {
                dateTime = LocalDateTime.parse(dateTimeStr);
            } catch (Exception e) {
                // Als de parsing mislukt, kun je hier een foutafhandeling toevoegen
                e.printStackTrace();
            }

            if (dateTime != null) {
                int index = -1;

                // Zoek naar een overeenkomende auto in de lijst van bekende auto's
                for (int i = 0; i < cars.size(); i++) {
                    if (cars.get(i).getLicensePlate().equals(carPlate)) {
                        index = i;
                        break;
                    }
                }

                // Controleer of een overeenkomende auto is gevonden
                if (index != -1) {
                    // Een overeenkomende auto is gevonden, maak een nieuwe Detection-instantie aan
                    nieuweDetectie = new Detection(cars.get(index), city, dateTime);
                } else {
                    // Geen overeenkomende auto gevonden, maak een nieuwe Car en Detection-instantie aan
                    Car nieuweAuto = new Car(carPlate);
                    cars.add(nieuweAuto); // Voeg de nieuwe auto toe aan de lijst van bekende auto's
                    nieuweDetectie = new Detection(nieuweAuto, city, dateTime);
                }
            }
        }

        return nieuweDetectie;
    }

    /**
     * Validates a detection against the purple conditions for entering an environmentally restricted zone
     * I.e.:
     * Diesel trucks and diesel coaches with an emission category of below 6 may not enter a purple zone
     *
     * @return a Violation instance if the detection saw an offence against the purple zone rule/
     * null if no offence was found.
     */
    public Violation validatePurple() {
        Car chosenCar = car;

        if (car.getCarType() == CarType.valueOf("Truck") || car.getCarType() == CarType.valueOf("Coach")) {
            if (car.getFuelType() == FuelType.Diesel) {
                if (car.getEmissionCategory() >= 6) {
                    return new Violation(chosenCar, city);

                } else {
                    return null;
                }
            } else {
                return null;
            }
        } else {
            return null;

        }


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

        return car.getLicensePlate() + "/" + city + "/" + dateTime;       // replace by a proper outcome
    }


}
