package UnitTest;

import nl.hva.ict.ads.elections.models.Candidate;
import nl.hva.ict.ads.elections.models.Party;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AdditionalScenariosTest {

    @Test
    public void testPartyEquality() {
        // Create two parties with the same ID and name
        Party party1 = new Party(1, "Test Party");
        Party party2 = new Party(1, "Test Party");

        // Ensure the parties are equal
        assertEquals(party1, party2);

        // Modify the name of one party
        party2 = new Party(1, "Modified Party");

        // Ensure the parties are not equal after the modification
        assertNotEquals(party1, party2);
    }

    @Test
    public void testPartyHashCode() {
        // Create two parties with the same ID and name
        Party party1 = new Party(1, "Test Party");
        Party party2 = new Party(1, "Test Party");

        // Ensure the hash codes are equal
        assertEquals(party1.hashCode(), party2.hashCode());

        // Modify the name of one party
        party2 = new Party(1, "Modified Party");

        // Ensure the hash codes are not equal after the modification
        assertNotEquals(party1.hashCode(), party2.hashCode());
    }
}
