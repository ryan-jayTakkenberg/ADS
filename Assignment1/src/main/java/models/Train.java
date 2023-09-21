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
        return this.firstWagon != null; // If first wagon exists, train has wagons
    }

    /**
     * A train is a passenger train when its first wagon is a PassengerWagon
     * (we do not worry about the posibility of mixed compositions here)
     * @return
     */
    public boolean isPassengerTrain() {
        return this.firstWagon instanceof PassengerWagon; // Check whether firstWagon is a passenger wagon
    }

    /**
     * A train is a freight train when its first wagon is a FreightWagon
     * (we do not worry about the posibility of mixed compositions here)
     * @return
     */
    public boolean isFreightTrain() {
        return this.firstWagon instanceof FreightWagon; // Check whether first wagon is a freight wagon
    }

    public Locomotive getEngine() {
        return engine; // Return the engine of the train
    }

    public Wagon getFirstWagon() {
        return firstWagon;   // Return the first wagon of the train
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
        if (this.firstWagon == null) {
            return 0;
        }

        int totalOfWagons = 0;

        Wagon currentWagon = this.firstWagon;

        while(currentWagon != null) {
            totalOfWagons++;
            currentWagon = currentWagon.getNextWagon();
        }

        return totalOfWagons;
    }

    /**
     * @return  the last wagon attached to the train
     */
    public Wagon getLastWagonAttached() {
        if (this.firstWagon == null) {
            return null; // Return if train is empty
        }

        Wagon currentWagon = firstWagon; // Grab the first wagon

        // Loop through list until end reached
        while (currentWagon.getNextWagon() != null) {
            currentWagon = currentWagon.getNextWagon();
        }

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
        if (this.firstWagon == null) { // Don't execute if firstwagon is null
            return 0;
        }

        int weightCounter = 0;
        Wagon currentWagon = this.firstWagon; // Start with the first wagon

        while (currentWagon != null) {
            if (currentWagon instanceof FreightWagon) { // Check if it's a freight wagon
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
             if (index == position) {
                 return currentWagon; // Found the wagon at the desired position
             }
             index++;
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
        Wagon currentWagon = this.firstWagon;

        while (currentWagon != null) {
            if (currentWagon.getId() == wagonId) {
                return currentWagon; // Found the wagon with the given wagonId
            }
            currentWagon = currentWagon.getNextWagon();
        }

        return null; // No wagon found with the given wagonId
    }

    /**
     * Checks if the given wagon is already part of the train.
     *
     * @param wagon the wagon to check
     * @return true if the wagon is already part of the train, false otherwise
     */
    private boolean isWagonInTrain(Wagon wagon) {
        Wagon currentWagon = this.firstWagon;

        while (currentWagon != null) {
            if (currentWagon == wagon) {
                return true; // Wagon is part of the train
            }
            currentWagon = currentWagon.getNextWagon();
        }

        return false; // Wagon is not part of the train
    }

    /**
     * Counts the number of wagons in a sequence starting from the given wagon.
     *
     * @param wagon the head wagon of a sequence of wagons
     * @return the number of wagons in the sequence
     */
    private int countWagons(Wagon wagon) {
        int count = 0;
        Wagon current = wagon;

        while (current != null) {
            count++;
            current = current.getNextWagon();
        }

        return count;
    }

    /**
     * Determines if the given sequence of wagons can be attached to this train
     * Verifies if the type of wagons match the type of train (Passenger or Freight)
     * Verifies that the capacity of the engine is sufficient to also pull the additional wagons
     * Verifies that the wagon is not part of the train already
     * Ignores the predecessors before the head wagon, if any
     * @param wagon the head wagon of a sequence of wagons to consider for attachment
     * @return whether type and capacity of this train can accommodate attachment of the sequence
     */
    public boolean canAttach(Wagon wagon) {
        int totalWagonsToAttach = 0;

        // Check if Wagon is part of train
        if (isWagonInTrain(wagon)) {
            return false; // Wagon is already part of the train
        }

        // Unable to add different type of wagons to each other
        if (this.firstWagon instanceof PassengerWagon && wagon instanceof FreightWagon) {
            return false;
        }

        if (this.firstWagon instanceof FreightWagon && wagon instanceof PassengerWagon) {
            return false;
        }

        // Calculate how many wagons need to be attached
        while(wagon != null) {
            if (wagon instanceof PassengerWagon || wagon instanceof FreightWagon) {
                totalWagonsToAttach++;
                wagon = wagon.getNextWagon(); // loop till end
            }
        }

        int sum = this.getNumberOfWagons() + totalWagonsToAttach;

        // Don't attach more wagons than train capacity and instance of Passenger/Freight
        if (sum <= this.engine.getMaxWagons()) {
            return true;
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
        int totalOfWagons = 0;

        // Check if Wagon is part of train
        if (isWagonInTrain(wagon)) {
            return false; // Wagon is already part of the train
        }

        // Detach the head wagon from its predecessors (if any)
        if (wagon.hasPreviousWagon()) {
            wagon.getPreviousWagon().setNextWagon(null);
            wagon.setPreviousWagon(null);
        }

        // Attach the sequence of wagons to the rear of the train
        if (this.firstWagon == null) {
            this.firstWagon = wagon;
        } else {
            Wagon currentWagon = this.firstWagon;

            while (currentWagon.getNextWagon() != null) {
                currentWagon = currentWagon.getNextWagon();
                totalOfWagons++;
            }

            if (totalOfWagons < this.engine.getMaxWagons()) {
                currentWagon.setNextWagon(wagon);
                wagon.setPreviousWagon(currentWagon);
            }
        }

        return true; // Attachment successful
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
        int totalWagonsToAttach = 0;
        Wagon trackTotalNewWagons = wagon;

        if (this.firstWagon == null) {
            this.firstWagon = wagon;
            return true;
        }

        if (isWagonInTrain(wagon)) {
            return false; // Wagon is already part of the train
        }

        Wagon currentWagon = this.firstWagon;

        while(trackTotalNewWagons != null) { // Calculate how many wagons need to be added
            totalWagonsToAttach++;
            trackTotalNewWagons = trackTotalNewWagons.getNextWagon();
        }

        int sum = this.getNumberOfWagons() + totalWagonsToAttach;

        // Detach the head wagon from its predecessors (if any)
        if (wagon.hasPreviousWagon() && sum <= this.getEngine().getMaxWagons()) {
            wagon.getPreviousWagon().setNextWagon(null);
            wagon.setPreviousWagon(null);
        }

        if (sum <= this.getEngine().getMaxWagons()) {
            currentWagon.setPreviousWagon(wagon.getLastWagonAttached()); // Set new wagon head as previous wagon
            wagon.getLastWagonAttached().setNextWagon(currentWagon); // Attach current wagons to new wagon sequence
            this.firstWagon = wagon; // Push new wagon to front
            return true;
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
        // Check if Wagon is part of train
        if (isWagonInTrain(wagon)) {
            return false; // Wagon is already part of the train
        }

        // Verify that the engine capacity is sufficient
        int wagonsToAttach = countWagons(wagon);

        if (this.engine.getMaxWagons() < wagonsToAttach) {
            return false; // Insufficient engine capacity
        }

        // Verify that the given position is valid for insertion
        if (position < 0 || position > this.getNumberOfWagons()) {
            return false; // Invalid position
        }

        // Detach the head wagon from its predecessors (if any)
        if (wagon.getPreviousWagon() != null) {
            wagon.getPreviousWagon().setNextWagon(null);
            wagon.setPreviousWagon(null);
        }

        // Insert the sequence of wagons at the specified position
        if (position == 0) {
            if (this.firstWagon != null) {
                wagon.setNextWagon(this.firstWagon);
                this.firstWagon.setPreviousWagon(wagon);
            }
            this.firstWagon = wagon;
        } else {
            Wagon previousWagon = findWagonAtPosition(position - 1); // Find the previous wagon
            wagon.getLastWagonAttached().setNextWagon(previousWagon.getNextWagon()); // Assign next wagon to end of given wagon sequence
            wagon.setPreviousWagon(previousWagon);
            previousWagon.setNextWagon(wagon);
        }

        return true; // Insertion successful
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

        // Unable to add different type of wagons to each other
        if (this.firstWagon instanceof PassengerWagon && toTrain.getFirstWagon() instanceof FreightWagon) {
            return false;
        }

        if (this.firstWagon instanceof FreightWagon && toTrain.getFirstWagon() instanceof PassengerWagon) {
            return false;
        }

        if (toTrain.getEngine().getMaxWagons() <= toTrain.getNumberOfWagons()) {
            return false; // Insufficient engine capacity
        }


        Wagon currentWagon = this.firstWagon;

        while(currentWagon.getId() != wagonId) {
            currentWagon = currentWagon.getNextWagon();
        }

        // Give old values to neighbour wagons (if any)
        if (currentWagon.hasPreviousWagon()) {
            if (currentWagon.hasNextWagon()) {
                currentWagon.getPreviousWagon().setNextWagon(currentWagon.getNextWagon());
            } else {
                currentWagon.getPreviousWagon().setNextWagon(null);
            }
        }

        if (currentWagon.hasNextWagon()) {
            if (currentWagon.hasPreviousWagon()) {
                currentWagon.getNextWagon().setPreviousWagon(currentWagon.getPreviousWagon());
            } else {
                currentWagon.getNextWagon().setPreviousWagon(null);
                this.firstWagon = currentWagon.getNextWagon();
            }
        }

        currentWagon.setPreviousWagon(null);
        currentWagon.setNextWagon(null);

        if (toTrain.canAttach(currentWagon)) {
            toTrain.attachToRear(currentWagon); // Attach wagon to rear of new train
            return true;
        }

        return false;   // If wagon was unable to be attached to train
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
        // Check if position is valid for this train
        if (position < 0 || position >= this.engine.getMaxWagons()) {
            return false;
        }

        // Unable to add different type of wagons to each other
        if (this.firstWagon instanceof PassengerWagon && toTrain.getFirstWagon() instanceof FreightWagon) {
            return false;
        }

        if (this.firstWagon instanceof FreightWagon && toTrain.getFirstWagon() instanceof PassengerWagon) {
            return false;
        }

        // Find the wagon at the specified position
        Wagon beforeSplit = null;
        Wagon currentWagon = this.firstWagon;
        int currentPosition = 0;

        while (currentWagon != null && currentPosition < position) {
            beforeSplit = currentWagon;
            currentWagon = currentWagon.getNextWagon();
            currentPosition++;
        }

        // Verify that the engine capacity is sufficient
        int wagonsToAttach = countWagons(currentWagon);
        int sum = toTrain.getNumberOfWagons() + wagonsToAttach;

        if (sum > toTrain.getEngine().getMaxWagons()) {
            return false; // Insufficient engine capacity
        }

        if (currentWagon == null) {
            return false; // Invalid position
        }

        // Detach the head wagon from its predecessors (if any)
        if (currentWagon.hasPreviousWagon()) {
            currentWagon.setPreviousWagon(null);
        }

        // Disconnect the wagons before the split point
        if (beforeSplit != null) {
            beforeSplit.setNextWagon(null);
        } else {
            // If beforeSplit is null, we are splitting from the first wagon
            this.firstWagon = null;
        }

        // Move the split sequence to toTrain
        toTrain.attachToRear(currentWagon);


        return true; // Split and move successful
    }


    /**
     * Reverses the sequence of wagons in this train (if any)
     * i.e. the last wagon becomes the first wagon
     *      the previous wagon of the last wagon becomes the second wagon
     *      etc.
     * (No change if the train has no wagons or only one wagon)
     */
    public void reverse() {
        if (this.firstWagon == null || this.firstWagon.getNextWagon() == null) {   // No change needed for 0 or 1 wagons
            return;
        }

        Wagon prevWagon = null;
        Wagon currentWagon = firstWagon;
        Wagon nextWagon;

        while (currentWagon != null) {
            nextWagon = currentWagon.getNextWagon();
            currentWagon.setNextWagon(prevWagon);
            prevWagon = currentWagon;
            currentWagon = nextWagon;
        }

        // Update the firstWagon reference to point to the new first wagon
        this.firstWagon = prevWagon;
    }

    // TODO string representation of a train

    @Override
    public String toString() {
        String wagonsDisplayed = "";
        Wagon currentWagon = this.firstWagon;
        int wagonCounter = 0;

        while(currentWagon != null) {
            wagonsDisplayed += currentWagon;
            currentWagon = currentWagon.getNextWagon();
            wagonCounter++;
        }

        return "[Loc-" + this.engine.getLocNumber() + "]" + wagonsDisplayed + " with "
                + wagonCounter + " wagons from " + this.origin + " to " + this.destination;
    }
}
