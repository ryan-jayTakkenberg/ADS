package models;

import java.time.LocalDateTime;
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
        Detection newDetection = null;

        String[] detectionParts = textLine.split(",");

        // Ensure that the text line has the expected format (license plate, city, dateTime)
        if (detectionParts.length == 3) {
            String licensePlate = detectionParts[0].trim();
            String city = detectionParts[1].trim();
            String dateTimeStr = detectionParts[2].trim();

            // Attempt to parse the dateTime into a LocalDateTime object
            LocalDateTime dateTime = null;
            try {
                dateTime = LocalDateTime.parse(dateTimeStr);
            } catch (Exception e) {
                // You can add error handling here if parsing fails
                e.printStackTrace();
            }

            if (dateTime != null) {
                int index = -1;

                // Search for a matching car in the list of known cars
                for (int i = 0; i < cars.size(); i++) {
                    if (cars.get(i).getLicensePlate().equals(licensePlate)) {
                        index = i;
                        break;
                    }
                }

                // Check if a matching car was found
                if (index != -1) {
                    // A matching car was found, create a new Detection instance
                    newDetection = new Detection(cars.get(index), city, dateTime);
                } else {
                    // No matching car found, create a new Car and Detection instance
                    Car newCar = new Car(licensePlate);
                    cars.add(newCar); // Add the new car to the list of known cars
                    newDetection = new Detection(newCar, city, dateTime);
                }
            }
        }

        return newDetection;
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
            //check if de cartype is Truck or Coach
            if (car.getFuelType() == FuelType.Diesel) {
                //Chech if the fuel is Diesel
                if (car.getEmissionCategory() < 6) {
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

        return car.getLicensePlate() + "/" + city + "/" + dateTime;
    }


}
