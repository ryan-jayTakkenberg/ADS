package models;

import java.util.Collections;
import java.util.Comparator;

public class Violation {
    private final Car car;
    private final String city;
    private int offencesCount;

    public Violation(Car car, String city) {
        this.car = car;
        this.city = city;
        this.offencesCount = 1;
    }

    public static int compareByLicensePlateAndCity(Violation v1, Violation v2) {
        // Compare the license plates
        int compareLicensePlates = v1.getCar().getLicensePlate().compareTo(v2.getCar().getLicensePlate());

        // If license plates are not similar, return the result
        if (compareLicensePlates != 0) {
            return compareLicensePlates;
        } else {
            // If license plates are similar, compare cities
            int compareCities = v1.getCity().compareTo(v2.getCity());

            // If cities are not similar, return the result
            if (compareCities != 0) {
                return compareCities;
            } else {
                // If license plates and cities equal, return 0
                return 0;
            }
        }
    }



    /**
     * Aggregates this violation with the other violation by adding their counts and
     * nullifying identifying attributes car and/or city that do not match
     * identifying attributes that match are retained in the result.
     * This method can be used for aggregating violations applying different grouping criteria
     * @param other
     * @return  a new violation with the accumulated offencesCount and matching identifying attributes.
     */
    public Violation combineOffencesCounts(Violation other) {
        Violation combinedViolation = new Violation(
                // nullify the car attribute iff this.car does not match other.car
                this.car != null && this.car.equals(other.car) ? this.car : null,
                // nullify the city attribute iff this.city does not match other.city
                this.city != null && this.city.equals(other.city) ? this.city : null);

        // add the offences counts of both original violations
        combinedViolation.setOffencesCount(this.offencesCount + other.offencesCount);

        return combinedViolation;
    }

    public Car getCar() {
        return car;
    }

    public String getCity() {
        return city;
    }

    public int getOffencesCount() {
        return offencesCount;
    }

    public void setOffencesCount(int offencesCount) {
        this.offencesCount = offencesCount;
    }

    @Override
    public String toString() {

        return car.getLicensePlate() + "/" + this.city + "/" + this.offencesCount;       // replace by a proper outcome
    }
}
