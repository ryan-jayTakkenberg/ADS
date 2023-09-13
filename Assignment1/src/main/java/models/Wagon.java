package models;

public abstract class Wagon {
    public int id;               // some unique ID of a Wagon
    private Wagon nextWagon;
                                 // another wagon that is appended at the tail of this wagon
                                    // a.k.a. the successor of this wagon in a sequence
                                    // set to null if no successor is connected
    private Wagon previousWagon; // another wagon that is prepended at the front of this wagon
                                    // a.k.a. the predecessor of this wagon in a sequence
                                    // set to null if no predecessor is connected


    // representation invariant propositions:
    // tail-connection-invariant:   wagon.nextWagon == null or wagon == wagon.nextWagon.previousWagon
    // front-connection-invariant:  wagon.previousWagon == null or wagon = wagon.previousWagon.nextWagon

    public Wagon (int wagonId) {
        this.id = wagonId;
    }

    public int getId() {
        return id;
    }

    public Wagon getNextWagon() {
        return nextWagon;
    }

    public Wagon getPreviousWagon() {
        return previousWagon;
    }

    /**
     * @return  whether this wagon has a wagon appended at the tail
     */
    public boolean hasNextWagon() {
        return nextWagon != null;
    }

    /**
     * @return  whether this wagon has a wagon prepended at the front
     */
    public boolean hasPreviousWagon() {
        return previousWagon!= null;
    }

    /**
     * Returns the last wagon attached to it,
     * if there are no wagons attached to it then this wagon is the last wagon.
     * @return  the last wagon
     */
    public Wagon getLastWagonAttached() {
        Wagon currentWagon = this;// zorgt voor de trein die we pakken steeds vernieuwd naar de gene die we hebben aangevinkt */

        while(currentWagon.hasNextWagon()){ // blijft lopen totdat we bij de laatste zijn
            currentWagon = currentWagon.getNextWagon();//als er nog eentje achter is update hij totdat we bij de laatste zijn
        }
            return currentWagon; // en als we de laatste hebben stopt de while en pakken we de laatst e


    }

    /**
     * @return  the length of the sequence of wagons towards the end of its tail
     * including this wagon itself.
     */
    public int getSequenceLength() {
        int sum = 1;
        Wagon currentWagon = this;

        while (currentWagon.hasNextWagon()){
           if(currentWagon instanceof  FreightWagon) {
               sum++;
           }
            currentWagon = currentWagon.getNextWagon();

        }

        return sum;
    }

    /**
     * Attaches the tail wagon and its connected successors behind this wagon,
     * if and only if this wagon has no wagon attached at its tail
     * and if the tail wagon has no wagon attached in front of it.
     * @param tail the wagon to attach behind this wagon.
     * @throws IllegalStateException if this wagon already has a wagon appended to it.
     * @throws IllegalStateException if tail is already attached to a wagon in front of it.
     *          The exception should include a message that reports the conflicting connection,
     *          e.g.: "%s is already pulling %s"
     *          or:   "%s has already been attached to %s"
     */
    public void attachTail(Wagon tail) {
        // TODO verify the exceptions
        Wagon currentWagon = this;

        if (currentWagon.hasPreviousWagon()) {// als de huidige wagon een voorganger heeft krijg je een error
            throw new IllegalStateException(String.format("%s is already pulling %s", currentWagon, tail));
        }else if (tail.hasNextWagon()){// als de tail al een voorganger heeft ook error
            throw new IllegalStateException(String.format("%s has already been attached to %s", currentWagon, tail));
        } else {// als het beide niety zo is dan wordt de tail toegevoeg aan de voortganger
            this.setNextWagon(tail);
            tail.setPreviousWagon(this);
        }


        // TODO attach the tail wagon to this wagon (sustaining the invariant propositions).
    }

    /**
     * Detaches the tail from this wagon and  * Detaches this wagon from the wagon in front of it.
     * No action if this wagon has no previous wagon attached.
     * @return  the former previousWagon that has been detached from,
     *          or <code>null</code> if it had no previousWagon. returns the first wagon of this tail.
     * @return the first wagon of the tail that has been detached
     *          or <code>null</code> if it had no wagons attached to its tail.
     */
    public Wagon detachTail() {
        // TODO detach the tail from this wagon (sustaining the invariant propositions).
        //  and return the head wagon of that tail


        if (this.nextWagon != null){
            Wagon headOfTail = this.nextWagon;//kijken wat de staart is
            this.nextWagon = null; //loskoppelen

            if (headOfTail.previousWagon != null){ // checken of hij een vorige heeft
                headOfTail.getPreviousWagon().nextWagon = null;// zo ja loskoppelen
                headOfTail.setPreviousWagon(null);
            }
            return headOfTail;


        }


        return null;
    }

    /**
     * Detaches this wagon from the wagon in front of it.
     * No action if this wagon has no previous wagon attached.
     * @return  the former previousWagon that has been detached from,
     *          or <code>null</code> if it had no previousWagon.
     */
    public Wagon detachFront() {
        // TODO detach this wagon from its predecessor (sustaining the invariant propositions).
        //   and return that predecessor

        if (this.previousWagon != null){
            Wagon frontOfTail;
            frontOfTail = null;
            if (frontOfTail.previousWagon != null){ // checken of hij een vorige heeft
                frontOfTail.getNextWagon().nextWagon = null;// zo ja loskoppelen
                frontOfTail.setNextWagon(null);
            }
            return frontOfTail;


        }


        return null;



    }

    /**
     * Replaces the tail of the <code>front</code> wagon by this wagon and its connected successors
     * Before such reconfiguration can be made,
     * the method first disconnects this wagon form its predecessor,
     * and the <code>front</code> wagon from its current tail.
     * @param front the wagon to which this wagon must be attached to.
     */
    public void reAttachTo(Wagon front) {
        // TODO detach any existing connections that will be rearrange

        Wagon previous = detachFront(); // voorkant eraf gehaald
        Wagon tail = front.detachTail(); //huide achterkant wordt eraf gehaald

        tail.attachTail(this);
        if (previous != null){
            tail.attachTail(null);
        }





        // TODO attach this wagon to its new predecessor front (sustaining the invariant propositions).
    }

    /**
     * Removes this wagon from the sequence that it is part of,
     * and reconnects its tail to the wagon in front of it, if any.
     */
    public void removeFromSequence() {

        // TODO
    }


    /**
     * Reverses the order in the sequence of wagons from this Wagon until its final successor.
     * The reversed sequence is attached again to the wagon in front of this Wagon, if any.
     * No action if this Wagon has no succeeding next wagon attached.
     * @return the new start Wagon of the reversed sequence (with is the former last Wagon of the original sequence)
     */
    public Wagon reverseSequence() {
        // TODO provide an iterative implementation,
        //   using attach- and detach methods of this class

        return null;
    }

    // TODO string representation of a Wagon


    public void setNextWagon(Wagon nextWagon) {
        this.nextWagon = nextWagon;
    }

    public void setPreviousWagon(Wagon previousWagon) {
        this.previousWagon = previousWagon;
    }
}
