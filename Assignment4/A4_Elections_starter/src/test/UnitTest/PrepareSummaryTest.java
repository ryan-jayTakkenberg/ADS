package UnitTest;

import nl.hva.ict.ads.elections.models.*;

import nl.hva.ict.ads.utils.PathUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class PrepareSummaryTest {
    private static Election election;
    @BeforeAll
    public static void setUp() throws Exception {
        election = new Election("Test Election");

        // Add mock data for testing
        addMockData();
    }

    @Test
    void testElectionNotNull() {
        assertNotNull(election, "Election instance should not be null");
    }

    @Test
    void testNonEmptyCandidates() {
        List<Candidate> candidates = election.getAllCandidates();
        assertFalse(candidates.isEmpty(), "Candidates list should not be empty");
        assertEquals(3, candidates.size(), "Expected 3 candidates");
    }

    @Test
    void testNonEmptyParties() {
        Collection<Party> parties = election.getParties();
        assertFalse(parties.isEmpty(), "Parties list should not be empty");
        assertEquals(2, parties.size(), "Expected 2 parties");
    }

    @Test
    void testNonEmptyConstituencies() {
        Set<Constituency> constituencies = (Set<Constituency>) election.getConstituencies();
        assertFalse(constituencies.isEmpty(), "Constituencies list should not be empty");
        assertEquals(1, constituencies.size(), "Expected 1 constituency");
    }

    @Test
    void testGetCandidatesWithDuplicateNames() {
        Set<Candidate> candidatesWithDuplicateNames = election.getCandidatesWithDuplicateNames();
        assertFalse(candidatesWithDuplicateNames.isEmpty(), "Candidates with duplicate names should not be empty");
        assertEquals(1, candidatesWithDuplicateNames.size(), "Expected 1 candidate with duplicate name");
    }

    @Test
    void testPrepareSummaryForParty() {
        String summary = election.prepareSummary(1); // Assuming you have a party with id=1 in your data
        assertNotNull(summary, "Summary should not be null");
        assertTrue(summary.contains("Party A"), "Summary should contain the party name");
    }

    @Test
    void testPrepareSummaryForElection() {
        String summary = election.prepareSummary();
        assertNotNull(summary, "Election summary should not be null");
        assertTrue(summary.contains("Test Election"), "Summary should contain the election name");
    }

    // Add more tests based on your specific functionality and requirements

    private static void addMockData() {
        Party party1 = new Party(1, "Party A",
                new Candidate("John", "van", "Doe"));

        Party party2 = new Party(2, "Party B",
                new Candidate("Jane", "de", "Smith"));

        Set<Party> newParties = new HashSet<>(Set.of(party1, party2));

        election.setParties(newParties);
    }


}
