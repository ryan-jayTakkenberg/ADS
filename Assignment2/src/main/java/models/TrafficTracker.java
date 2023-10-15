package models;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;

public class TrafficTracker {
    private final String TRAFFIC_FILE_EXTENSION = ".txt";
    private final String TRAFFIC_FILE_PATTERN = ".+\\" + TRAFFIC_FILE_EXTENSION;

    private OrderedList<Car> cars;                  // the reference list of all known Cars registered by the RDW
    private OrderedList<Violation> violations;      // the accumulation of all offences by car and by city

    public TrafficTracker() {
        // Initialize cars with an empty ordered list which sorts items by licensePlate
        this.cars = new OrderedArrayList<>(Comparator.comparing(Car::getLicensePlate));

        // Initialize violations with an empty ordered list which sorts items by car and city
        this.violations = new OrderedArrayList<>((v1, v2) -> {
            int compareCars = v1.getCar().getLicensePlate().compareTo(v2.getCar().getLicensePlate());
            if (compareCars == 0) {
                return v1.getCity().compareTo(v2.getCity());
            } else {
                return compareCars;
            }
        });

    }

    /**
     * imports all registered cars from a resource file that has been provided by the RDW
     * @param resourceName
     */
    public void importCarsFromVault(String resourceName) {
        this.cars.clear();

        // load all cars from the text file
        int numberOfLines = importItemsFromFile(this.cars,
                createFileFromURL(TrafficTracker.class.getResource(resourceName)),
                Car::fromLine);

        // sort the cars for efficient later retrieval
        this.cars.sort();

        System.out.printf("Imported %d cars from %d lines in %s.\n", this.cars.size(), numberOfLines, resourceName);
    }

    /**
     * imports and merges all raw detection data of all entry gates of all cities from the hierarchical file structure of the vault
     * accumulates any offences against purple rules into this.violations
     * @param resourceName
     */
    public void importDetectionsFromVault(String resourceName) {
        this.violations.clear();

        int totalNumberOfOffences =
            this.mergeDetectionsFromVaultRecursively(
                    createFileFromURL(TrafficTracker.class.getResource(resourceName)));

        System.out.printf("Found %d offences among detections imported from files in %s.\n",
                totalNumberOfOffences, resourceName);
    }

    /**
     * traverses the detections vault recursively and processes every data file that it finds
     * @param file
     */
    private int mergeDetectionsFromVaultRecursively(File file) {
        int totalNumberOfOffences = 0;

        if (file.isDirectory()) {
            // the file is a folder (a.k.a. directory)
            //  retrieve a list of all files and sub folders in this directory
            File[] filesInDirectory = Objects.requireNonNullElse(file.listFiles(), new File[0]);

            // Iterate through the Directory
            for (File scannedFile : filesInDirectory) {
                // Check if sub-directory is a directory
                if (scannedFile.isDirectory()) {
                    // Scan files in sub-directories and add to array
                    File[] filesInSubDirectory = scannedFile.listFiles();

                    // Iterate through files recursively
                    for (File subFile: filesInSubDirectory) {
                        totalNumberOfOffences += this.mergeDetectionsFromVaultRecursively(subFile);
                    }
                }
            }

            // TODO recursively process all files and sub folders from the filesInDirectory list.
            //  also track the total number of offences found


        } else if (file.getName().matches(TRAFFIC_FILE_PATTERN)) {
            // the file is a regular file that matches the target pattern for raw detection files
            // process the content of this file and merge the offences found into this.violations
            totalNumberOfOffences += this.mergeDetectionsFromFile(file);
        }

        return totalNumberOfOffences;
    }

    /**
     * imports another batch detection data from the filePath text file
     * and merges the offences into the earlier imported and accumulated violations
     * @param file
     */
    private int mergeDetectionsFromFile(File file) {

        // re-sort the accumulated violations for efficient searching and merging
        this.violations.sort();

        // use a regular ArrayList to load the raw detection info from the file
        List<Detection> newDetections = new ArrayList<>();
        TrafficTracker.importItemsFromFile(newDetections, file, new Function<String, Detection>() {
            @Override
            public Detection apply(String s) {
                return Detection.fromLine(s, cars);
            }
        });


        System.out.printf("Imported %d detections from %s.\n", newDetections.size(), file.getPath());

        int totalNumberOfOffences = 0; // tracks the number of offences that emerges from the data in this file

        for (Detection newDetection : newDetections) {
            Violation violation = newDetection.validatePurple();

            boolean isNotNull = this.violations.merge(violation, Violation::combineOffencesCounts);

            if (isNotNull) {
                totalNumberOfOffences += 1;
            }
        }

        return totalNumberOfOffences;
    }


    /**
     * calculates the total revenue of fines from all violations,
     * Trucks pay €25 per offence, Coaches €35 per offence
     * @return      the total amount of money recovered from all violations
     */
    public double calculateTotalFines() {
        double totalFines = 0.0;

        for (Violation violation : violations) {
            Car car = violation.getCar();

            if (car != null) {
                Car.CarType vehicleType = car.getCarType();
                double fine = 0.0;

                if (vehicleType == Car.CarType.Truck) {
                    fine = 25.0;
                } else if (vehicleType == Car.CarType.Coach) {
                    fine = 35.0;
                }

                totalFines += fine * violation.getOffencesCount();
            }
        }

        return totalFines;
    }







    /**
     * Prepares a list of topNumber of violations that show the highest offencesCount
     * when this.violations are aggregated by car across all cities.
     * @param topNumber     the requested top number of violations in the result list
     * @return              a list of topNum items that provides the top aggregated violations
     */


    public List<Violation> topViolationsByCar(int topNumber) {
        // Maak een kopie van de lijst van violations
        List<Violation> copyViolations = new ArrayList<>(violations);

        // Sorteer de kopie van de lijst op decreasing offencesCount
        copyViolations.sort((v1, v2) -> Integer.compare(v2.getOffencesCount(), v1.getOffencesCount()));

        // Beperk de lijst tot de topNumber items
        if (topNumber < copyViolations.size()) {
            copyViolations = copyViolations.subList(0, topNumber);
        }

        return copyViolations;
    }

    public List<Violation> topViolationsByCity(int topNumber) {
        // Maak een kopie van de lijst van violations
        List<Violation> copyViolations = new ArrayList<>(violations);

        // Sorteer de kopie van de lijst op decreasing offencesCount
        copyViolations.sort((v1, v2) -> Integer.compare(v2.getOffencesCount(), v1.getOffencesCount()));

        // Beperk de lijst tot de topNumber items
        if (topNumber < copyViolations.size()) {
            copyViolations = copyViolations.subList(0, topNumber);
        }

        return copyViolations;
    }


    /**
     * imports a collection of items from a text file which provides one line for each item
     * @param items         the list to which imported items shall be added
     * @param file          the source text file
     * @param converter     a function that can convert a text line into a new item instance
     * @param <E>           the (generic) type of each item
     */
    public static <E> int importItemsFromFile(List<E> items, File file, Function<String,E> converter) {
        int numberOfLines = 0;

        Scanner scanner = createFileScanner(file);

        // read all source lines from the scanner,
        // convert each line to an item of type E
        // and add each successfully converted item into the list
        while (scanner.hasNext()) {
            // input another line with author information
            String line = scanner.nextLine();
            numberOfLines++;

            // Convert the line to an instance of E using the converter function
            try {
                E item = converter.apply(line);
                if (item != null) {

                    // Add the successfully converted item to the list of items
                    items.add(item);
                }
            } catch (Exception e) {
                // Handle any exceptions that may occur during conversion
                System.err.println("Error while converting line " + numberOfLines + ": " + e.getMessage());
            }
        }

        //System.out.printf("Imported %d lines from %s.\n", numberOfLines, file.getPath());
        return numberOfLines;
    }

    /**
     * helper method to create a scanner on a file and handle the exception
     * @param file
     * @return
     */
    private static Scanner createFileScanner(File file) {
        try {
            return new Scanner(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("FileNotFound exception on path: " + file.getPath());
        }
    }
    private static File createFileFromURL(URL url) {
        try {
            return new File(url.toURI().getPath());
        } catch (URISyntaxException e) {
            throw new RuntimeException("URI syntax error found on URL: " + url.getPath());
        }
    }

    public OrderedList<Car> getCars() {
        return this.cars;
    }

    public OrderedList<Violation> getViolations() {
        return this.violations;
    }
}
