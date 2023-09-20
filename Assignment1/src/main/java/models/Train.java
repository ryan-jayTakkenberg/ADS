package models;

public class Train {
    private final String origin;
    private final String destination;
    private final Locomotive engine;
    private Wagon firstWagon;

    /* Representation invariants:
        firstWagon == null || firstWagon.previousWagon == null
        engine != null
     */

    public Train(Locomotive engine, String origin, String destination) {
        this.engine = engine;
        this.destination = destination;
        this.origin = origin;
    }

    /**
     * Indicates whether the train has at least one connected Wagon
     * @return
     */
    public boolean hasWagons() {
        // TODO
        return this.firstWagon != null; // replace by proper outcome
    }

    /**
     * A train is a passenger train when its first wagon is a PassengerWagon
     * (we do not worry about the posibility of mixed compositions here)
     * @return
     */
    public boolean isPassengerTrain() {
        // TODO
        return this.firstWagon instanceof PassengerWagon;
    }

    /**
     * A train is a freight train when its first wagon is a FreightWagon
     * (we do not worry about the posibility of mixed compositions here)
     * @return
     */
    public boolean isFreightTrain() {
        return this.firstWagon instanceof FreightWagon;
    }

    public Locomotive getEngine() {
        return engine;
    }

    public Wagon getFirstWagon() {
        return firstWagon;
    }

    /**
     * Replaces the current sequence of wagons (if any) in the train
     * by the given new sequence of wagons (if any)
     * @param wagon the first wagon of a sequence of wagons to be attached (can be null)
     */
    public void setFirstWagon(Wagon wagon) {
        this.firstWagon = wagon;
    }

    /**
     * @return  the number of Wagons connected to the train
     */
    public int getNumberOfWagons() {
        // TODO
        int wagonCount = 0; // Wagon counter

        for (int i = 0; i < this.engine.getMaxWagons(); i++) { // Check max wagons attached to locomotive
            wagonCount++;
        }

        return wagonCount;
    }

    /**
     * @return  the last wagon attached to the train
     */
    public Wagon getLastWagonAttached() {
        Wagon currentWagon = this.firstWagon;

        while(currentWagon.hasNextWagon()) {
            currentWagon = currentWagon.getNextWagon(); // Move to the next wagon
        }

        // Reached the end of wagons
        return currentWagon;
    }

    /**
     * @return  the total number of seats on a passenger train
     *          (return 0 for a freight train)
     */
    public int getTotalNumberOfSeats() {
        int passengerSeats = 0;
        Wagon currentWagon = this.firstWagon; // Start with the first wagon

        while (currentWagon != null) {
            if (currentWagon instanceof PassengerWagon) { // Check if it's a passenger wagon
                passengerSeats += ((PassengerWagon) currentWagon).getNumberOfSeats();
            }
            currentWagon = currentWagon.getNextWagon(); // Move to the next wagon
        }

        return passengerSeats;
    }

    /**
     * calculates the total maximum weight of a freight train
     * @return  the total maximum weight of a freight train
     *          (return 0 for a passenger train)
     *
     */
    public int getTotalMaxWeight() {
        int weightCounter = 0;
        Wagon currentWagon = this.firstWagon; // Start with the first wagon

        while (currentWagon != null) {
            if (currentWagon instanceof PassengerWagon) { // Check if it's a passenger wagon
                weightCounter += ((FreightWagon) currentWagon).getMaxWeight();
            }
            currentWagon = currentWagon.getNextWagon(); // Move to the next wagon
        }

        return weightCounter;
    }

     /**
     * Finds the wagon at the given position (starting at 0 for the first wagon of the train)
     * @param position
     * @return  the wagon found at the given position
     *          (return null if the position is not valid for this train)
     */
     public Wagon findWagonAtPosition(int position) {
         int index = 0; // Starting index pos
         Wagon currentWagon = this.firstWagon; // Start with the first wagon

         while (currentWagon != null) {
             if (currentWagon instanceof PassengerWagon) { // Check if it's a passenger wagon
                 if (index == position) {
                     return currentWagon; // Found the wagon at the desired position
                 }
                 index++;
             }
             currentWagon = currentWagon.getNextWagon(); // Move to the next wagon
         }

         // If the loop completes without finding the desired position
         return null;
     }

    /**
     * Finds the wagon with a given wagonId
     * @param wagonId
     * @return  the wagon found
     *          (return null if no wagon was found with the given wagonId)
     */
    public Wagon findWagonById(int wagonId) {
        int index = 0; // Starting index pos
        Wagon currentWagon = this.firstWagon; // Start with the first wagon

        while (currentWagon != null) {
            if (currentWagon instanceof PassengerWagon) { // Check if it's a passenger wagon
                if (index == wagonId) {
                    return currentWagon; // Found the wagon at the desired position
                }
                index++;
            }
            currentWagon = currentWagon.getNextWagon(); // Move to the next wagon
        }

        // If the loop completes without finding the desired position
        return null;
    }

    /**
     * Determines if the given sequence of wagons can be attached to this train [x]
     * Verifies if the type of wagons match the type of train (Passenger or Freight) [x]
     * Verifies that the capacity of the engine is sufficient to also pull the additional wagons [x]
     * Verifies that the wagon is not part of the train already [x]
     * Ignores the predecessors before the head wagon, if any [x]
     * @param wagon the head wagon of a sequence of wagons to consider for attachment
     * @return whether type and capacity of this train can accommodate attachment of the sequence
     */
    public boolean canAttach(Wagon wagon) {
        int currentTotalWagons = 0; // Calculate how many wagons are currently attached to train
        int currentWagonId; // Track current ID of wagon

        Wagon currentWagon = this.firstWagon;
        currentWagonId = currentWagon.getId(); // Assign current wagon ID to value

        while (currentWagon != null && wagon.id != currentWagonId) { // If wagon id doesn't exist yet
            currentTotalWagons++;
            currentWagon = currentWagon.getNextWagon(); // Move to the next wagon

            if (currentWagon == null) {
                if (wagon instanceof PassengerWagon || wagon instanceof FreightWagon) { // Check if it's a passenger/freight wagon
                    if (currentTotalWagons < this.engine.getMaxWagons()) {
                        return true;
                    }
                } else {
                    return false; // Don't add wagon if not instance of passenger/freight
                }
            }
        }

        // Return false if wagon was already attached to train
        return false;
    }

    /**
     * Tries to attach the given sequence of wagons to the rear of the train
     * No change is made if the attachment cannot be made.
     * (when the sequence is not compatible or the engine has insufficient capacity)
     * if attachment is possible, the head wagon is first detached from its predecessors, if any
     * @param wagon the head wagon of a sequence of wagons to be attached
     * @return  whether the attachment could be completed successfully
     */
    public boolean attachToRear(Wagon wagon) {
        int currentTotalWagons = 0;

        if (this.firstWagon == null) {
            this.firstWagon = wagon;
        } else {
            if (currentTotalWagons < this.engine.getMaxWagons()) {
                Wagon currentWagon = this.firstWagon;

                while(currentWagon.hasNextWagon()) {
                    currentWagon = currentWagon.getNextWagon(); // Move to the next wagon
                    currentTotalWagons++;
                }

                currentWagon.setNextWagon(wagon);
            }
        }

        // Return false if wagon was unable to be attached to train
        return false;
    }

    /**
     * Tries to insert the given sequence of wagons at the front of the train
     * (the front is at position one, before the current first wagon, if any)
     * No change is made if the insertion cannot be made.
     * (when the sequence is not compatible or the engine   insufficient capacity)
     * if insertion is possible, the head wagon is first detached from its predecessors, if any
     * @param wagon the head wagon of a sequence of wagons to be inserted
     * @return  whether the insertion could be completed successfully
     */
    public boolean insertAtFront(Wagon wagon) {
        int currentTotalWagons = 0;

        if (this.firstWagon == null) {
            this.firstWagon = wagon;
        } else {
            if (currentTotalWagons < this.engine.getMaxWagons()) {
                Wagon currentWagon = this.firstWagon;

                while(currentWagon.hasPreviousWagon()) {
                    currentWagon = currentWagon.getPreviousWagon(); // Move to the next wagon
                    currentTotalWagons++;
                }

                currentWagon.setPreviousWagon(wagon);

                if (currentTotalWagons == this.engine.getMaxWagons()) {
                    this.firstWagon = currentWagon;
                }
            }
        }

        // Return false if wagon was unable to be attached to train
        return false;
    }

    /**
     * Tries to insert the given sequence of wagons at/before the given position in the train.
     * (The current wagon at given position including all its successors shall then be reattached
     *    after the last wagon of the given sequence.)
     * No change is made if the insertion cannot be made.
     * (when the sequence is not compatible or the engine has insufficient capacity
     *    or the given position is not valid for insertion into this train)
     * if insertion is possible, the head wagon of the sequence is first detached from its predecessors, if any
     * @param position the position where the head wagon and its successors shall be inserted
     *                 0 <= position <= numWagons
     *                 (i.e. insertion immediately after the last wagon is also possible)
     * @param wagon the head wagon of a sequence of wagons to be inserted
     * @return  whether the insertion could be completed successfully
     */
    public boolean insertAtPosition(int position, Wagon wagon) {
        // TODO

        return false;   // replace by proper outcome
    }

    /**
     * Tries to remove one Wagon with the given wagonId from this train
     * and attach it at the rear of the given toTrain
     * No change is made if the removal or attachment cannot be made
     * (when the wagon cannot be found, or the trains are not compatible
     * or the engine of toTrain has insufficient capacity)
     * @param wagonId   the id of the wagon to be removed
     * @param toTrain   the train to which the wagon shall be attached
     *                  toTrain shall be different from this train
     * @return  whether the move could be completed successfully
     */
    public boolean moveOneWagon(int wagonId, Train toTrain) {
        // TODO

        return false;   // replace by proper outcome
     }

    /**
     * Tries to split this train before the wagon at given position and move the complete sequence
     * of wagons from the given position to the rear of toTrain.
     * No change is made if the split or re-attachment cannot be made
     * (when the position is not valid for this train, or the trains are not compatible
     * or the engine of toTrain has insufficient capacity)
     * @param position  0 <= position < numWagons
     * @param toTrain   the train to which the split sequence shall be attached
     *                  toTrain shall be different from this train
     * @return  whether the move could be completed successfully
     */
    public boolean splitAtPosition(int position, Train toTrain) {
        // TODO

        return false;   // replace by proper outcome
    }

    /**
     * Reverses the sequence of wagons in this train (if any)
     * i.e. the last wagon becomes the first wagon
     *      the previous wagon of the last wagon becomes the second wagon
     *      etc.
     * (No change if the train has no wagons or only one wagon)
     */
    public void reverse() {
        // TODO

    }

    // TODO string representation of a train

    @Override
    public String toString() {
        String wagonsDisplayed = "";
        Wagon currentWagon = this.firstWagon;

        while(currentWagon != null) {
            wagonsDisplayed += currentWagon;
            currentWagon = currentWagon.getNextWagon();
        }

        return "[Loc-" + this.engine.getLocNumber() + "]" + wagonsDisplayed;
    }
}
