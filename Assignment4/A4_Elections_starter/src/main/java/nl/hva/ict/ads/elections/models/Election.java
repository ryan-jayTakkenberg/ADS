package nl.hva.ict.ads.elections.models;

import nl.hva.ict.ads.utils.PathUtils;
import nl.hva.ict.ads.utils.xml.XMLParser;

import javax.xml.stream.XMLStreamException;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Holds all election data per consituency
 * Provides calculation methods for overall election results
 */
public class Election {

    private String name;

    // all (unique) parties in this election, organised by Id
    // will be build from the XML
    protected Map<Integer, Party> parties;

    // all (unique) constituencies in this election, identified by Id
    protected Set<Constituency> constituencies;

    public Election(String name) {
        this.name = name;
        this.parties = new HashMap<>();
        this.constituencies = new HashSet<>();
    }

    /**
     * finds all (unique) parties registered for this election
     * @return all parties participating in at least one constituency, without duplicates
     */
    public Collection<Party> getParties() {
        // Return all unique parties from the constituencies
        return parties.values();
    }

    /**
     * finds the party with a given id
     * @param id
     * @return  the party with given id, or null if no such party exists.
     */
    public Party getParty(int id) {
        // Return the party with the given ID, or null if not found
        return parties.get(id);
    }

    public Set<? extends Constituency> getConstituencies() {
        return this.constituencies;
    }

    /**
     * finds all unique candidates across all parties across all constituencies
     * organised by increasing party-id
     * @return alle unique candidates organised by increasing party-id
     */
    public List<Candidate> getAllCandidates() {
        Map<Integer, Set<Candidate>> candidatesByPartyId = new TreeMap<>();

        // Iterate over all constituencies
        for (Constituency constituency : getConstituencies()) {
            // Iterate over all parties in the constituency
            for (Party party : constituency.getParties()) {
                // Iterate over all candidates in the party
                for (Candidate candidate : party.getCandidates()) {
                    // Get the party ID
                    int partyId = party.getId();

                    // Add the candidate to the corresponding set in the map
                    candidatesByPartyId
                            .computeIfAbsent(partyId, k -> new HashSet<>())
                            .add(candidate);
                }
            }
        }

        // Flatten the sets into a single list, ordered by party ID
        return candidatesByPartyId.values().stream()
                .flatMap(Set::stream)
                .collect(Collectors.toList());
    }

    /**
     * Retrieve for the given party the number of Candidates that have been registered per Constituency
     * @param party
     * @return
     */

    public Map<Constituency, Integer> numberOfRegistrationsByConstituency(Party party) {
        Map<Constituency, Integer> registrationsByConstituency = new HashMap<>();

        // Iterate over all constituencies
        for (Constituency constituency : getConstituencies()) {
            // Count the number of candidates from the specified party in each constituency
            long count = party.getCandidates().stream()
                    .filter(candidate -> candidate.getParty().equals(constituency))
                    .count();

            registrationsByConstituency.put(constituency, (int) count);
        }

        return registrationsByConstituency;
    }
    /**
     * Get candidates with duplicate names.
     *
     * @return Set of candidates with duplicate names.
     */
    public Set<Candidate> getCandidatesWithDuplicateNames() {
        Set<Candidate> candidatesWithDuplicateNames = new HashSet<>();
        Set<String> seenNames = new HashSet<>();

        // Iterate over all constituencies
        for (Constituency constituency : getConstituencies()) {
            // Iterate over all parties in the constituency
            for (Party party : constituency.getParties()) {
                // Iterate over all candidates in the party
                for (Candidate candidate : party.getCandidates()) {
                    // Get the trimmed full name of the candidate
                    String fullName = candidate.getFullName().trim();

                    // Check if the name is already seen (duplicate)
                    if (seenNames.contains(fullName)) {
                        candidatesWithDuplicateNames.add(candidate);
                    } else {
                        // Add the name to the seen names set
                        seenNames.add(fullName);
                    }
                }
            }
        }

        // Print debug information
        System.out.println("Total candidates: " + getTotalCandidates());
        System.out.println("Candidates with duplicate names: " + candidatesWithDuplicateNames.size());


        return candidatesWithDuplicateNames;
    }

    /**
     * Get total candidates in all constituencies and parties.
     *
     * @return Total number of candidates.
     */
    private int getTotalCandidates() {
        int totalCandidates = 0;

        // Iterate over all constituencies
        for (Constituency constituency : getConstituencies()) {
            // Iterate over all parties in the constituency
            for (Party party : constituency.getParties()) {
                // Add the number of candidates in the party to the total
                totalCandidates += party.getCandidates().size();
            }
        }

        return totalCandidates;
    }


    /**
     * Retrieve from all constituencies the combined sub set of all polling stations that are located within the area of the specified zip codes
     * i.e. firstZipCode <= pollingStation.zipCode <= lastZipCode
     * All valid zip codes adhere to the pattern 'nnnnXX' with 1000 <= nnnn <= 9999 and 'AA' <= XX <= 'ZZ'
     * @param firstZipCode
     * @param lastZipCode
     * @return      the sub set of polling stations within the specified zipCode range
     */
    public Collection<PollingStation> getPollingStationsByZipCodeRange(String firstZipCode, String lastZipCode) {
        return getConstituencies().stream()
                .flatMap(constituency -> constituency.getPollingStations().stream())
                .filter(pollingStation -> {
                    String zipCode = pollingStation.getZipCode();
                    return zipCode.compareTo(firstZipCode) >= 0 && zipCode.compareTo(lastZipCode) <= 0;
                })
                .collect(Collectors.toList());
    }


    /**
     * Retrieves per party the total number of votes across all candidates, constituencies and polling stations
     * @return
     */
    public Map<Party, Integer> getVotesByParty() {
        return getConstituencies().stream()
                .flatMap(constituency -> constituency.getVotesByParty().entrySet().stream())
                .collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.summingInt(Map.Entry::getValue)));
    }



    /**
     * Retrieves per party the total number of votes across all candidates,
     * that were cast in one out of the given collection of polling stations.
     * This method is useful to prepare an election result for any sub-area of a Constituency.
     * Or to obtain statistics of special types of voting, e.g. by mail.
     * @param pollingStations the polling stations that cover the sub-area of interest
     * @return
     */
    public Map<Party, Integer> getVotesByPartyAcrossPollingStations(Collection<PollingStation> pollingStations) {
        return pollingStations.stream()
                .flatMap(pollingStation -> pollingStation.getVotesByParty().entrySet().stream())
                .collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.summingInt(Map.Entry::getValue)));
    }



    /**
     * Transforms and sorts decreasingly vote counts by party into votes percentages by party
     * The party with the highest vote count shall be ranked upfront
     * The votes percentage by party is calculated from  100.0 * partyVotes / totalVotes;
     * @return  the sorted list of (party,votesPercentage) pairs with the highest percentage upfront
     */
    public static List<Map.Entry<Party, Double>> sortedElectionResultsByPartyPercentage(int tops, Map<Party, Integer> votesCounts) {
        int totalVotes = votesCounts.values().stream().mapToInt(Integer::intValue).sum();
        return votesCounts.entrySet().stream()
                .sorted(Map.Entry.<Party, Integer>comparingByValue().reversed())
                .limit(tops)
                .map(entry -> Map.entry(entry.getKey(), 100.0 * entry.getValue() / totalVotes))
                .collect(Collectors.toList());
    }

    /**
     * Find the most representative Polling Station, which has got its votes distribution across all parties
     * the most alike the distribution of overall total votes.
     * A perfect match is found, if for each party the percentage of votes won at the polling station
     * is identical to the percentage of votes won by the party overall in the election.
     * The most representative Polling Station has the smallest deviation from that perfect match.
     *
     * There are different metrics possible to calculate a relative deviation between distributions.
     * You may use the helper method {@link #euclidianVotesDistributionDeviation(Map, Map)}
     * which calculates a relative least-squares deviation between two distributions.
     *
     * @return the most representative polling station.
     */
    public PollingStation findMostRepresentativePollingStation() {
        Map<Party, Integer> overallDistribution = getVotesByParty();
        return getConstituencies().stream()
                .flatMap(constituency -> constituency.getPollingStations().stream())
                .min(Comparator.comparingDouble(pollingStation ->
                        euclidianVotesDistributionDeviation(pollingStation.getVotesByParty(), overallDistribution)))
                .orElse(null);
    }

    /**
     * Calculates the Euclidian distance between the relative distribution across parties of two voteCounts.
     * If the two relative distributions across parties are identical, then the distance will be zero
     * If some parties have relatively more votes in one distribution than the other, the outcome will be positive.
     * The lower the outcome, the more alike are the relative distributions of the voteCounts.
     * ratign of votesCounts1 relative to votesCounts2.
     * see https://towardsdatascience.com/9-distance-measures-in-data-science-918109d069fa
     *
     * @param votesCounts1 one distribution of votes across parties.
     * @param votesCounts2 another distribution of votes across parties.
     * @return de relative distance between the two distributions.
     */
    private double euclidianVotesDistributionDeviation(Map<Party, Integer> votesCounts1, Map<Party, Integer> votesCounts2) {
        // calculate total number of votes in both distributions
        int totalNumberOfVotes1 = integersSum(votesCounts1.values());
        int totalNumberOfVotes2 = integersSum(votesCounts2.values());

        // we calculate the distance as the sum of squares of relative voteCount distribution differences per party
        // if we compare two voteCounts that have the same relative distribution across parties, the outcome will be zero

        return votesCounts1.entrySet().stream()
                .mapToDouble(e -> Math.pow(e.getValue()/(double)totalNumberOfVotes1 -
                        votesCounts2.getOrDefault(e.getKey(),0)/(double)totalNumberOfVotes2, 2))
                .sum();
    }

    /**
     * auxiliary method to calculate the total sum of a collection of integers
     * @param integers
     * @return
     */
    public static int integersSum(Collection<Integer> integers) {
        return integers.stream().reduce(Integer::sum).orElse(0);
    }


    public String prepareSummary(int partyId) {
        Party party = getParty(partyId);
        StringBuilder summary = new StringBuilder()
                .append("\nSummary of ").append(party).append(":\n");

        // Total number of candidates in the given party
        int totalCandidates = party.getCandidates().size();
        summary.append("Total number of candidates: ").append(totalCandidates).append("\n");

        // List with all candidates in the given party
        summary.append("List of all candidates:\n");
        party.getCandidates().forEach(candidate -> summary.append(candidate).append("\n"));

        // Total number of registrations for the given party
        int totalRegistrations = party.getId();
        summary.append("Total number of registrations: ").append(totalRegistrations).append("\n");

        // Map of number of registrations by constituency for the given party
        Map<Constituency, Integer> registrationsByConstituency = (Map<Constituency, Integer>) party.getCandidates();
        summary.append("Registrations by constituency:\n");
        registrationsByConstituency.forEach((constituency, registrations) ->
                summary.append(constituency.getName()).append(": ").append(registrations).append("\n"));

        return summary.toString();
    }


    public String prepareSummary() {
        StringBuilder summary = new StringBuilder()
                .append("\nElection summary of ").append(this.name).append(":\n");

        // Total number of parties in the election
        int totalParties = getParties().size();
        summary.append("Total number of parties: ").append(totalParties).append("\n");

        // List of all parties ordered by increasing party-Id
        summary.append("List of all parties:\n");
        getParties().forEach(p -> summary.append(p).append("\n"));

        // Total number of constituencies in the election
        int totalConstituencies = getConstituencies().size();
        summary.append("Total number of constituencies: ").append(totalConstituencies).append("\n");

        // Total number of polling stations in the election
        int totalPollingStations = getParties().size();
        summary.append("Total number of polling stations: ").append(totalPollingStations).append("\n");

        // Total number of (different) candidates in the election
        List<Candidate> allCandidates = getAllCandidates();
        int totalCandidates = allCandidates.size();
        summary.append("Total number of candidates: ").append(totalCandidates).append("\n");

        // List with all candidates with a duplicate name in a different party
        Set<Candidate> candidatesWithDuplicateNames = getCandidatesWithDuplicateNames();
        summary.append("Candidates with duplicate names:\n");
        candidatesWithDuplicateNames.forEach(candidate -> summary.append(candidate).append("\n"));

        // Sorted list of overall election results ordered by decreasing party percentage
        List<Map.Entry<Party, Double>> electionResults = sortedElectionResultsByPartyPercentage(totalParties, getVotesByParty());
        summary.append("Overall election results (sorted by party percentage):\n");
        electionResults.forEach(entry -> summary.append(entry.getKey()).append(": ").append(entry.getValue()).append("%\n"));

        // Polling stations within the Amsterdam Wibautstraat area with zipcodes between 1091AA and 1091ZZ
        Collection<PollingStation> pollingStationsInArea = getPollingStationsByZipCodeRange("1091AA", "1091ZZ");
        summary.append("Polling stations in the Amsterdam Wibautstraat area:\n");
        pollingStationsInArea.forEach(pollingStation -> summary.append(pollingStation).append("\n"));

        // Top 10 sorted election results within the Amsterdam Wibautstraat area
        List<Map.Entry<Party, Double>> topResultsInArea = sortedElectionResultsByPartyPercentage(10, getVotesByPartyAcrossPollingStations(pollingStationsInArea));
        summary.append("Top 10 election results in the Amsterdam Wibautstraat area:\n");
        topResultsInArea.forEach(entry -> summary.append(entry.getKey()).append(": ").append(entry.getValue()).append("%\n"));

        // Most representative polling station across the election
        PollingStation mostRepresentativeStation = findMostRepresentativePollingStation();
        summary.append("Most representative polling station:\n");
        summary.append(mostRepresentativeStation).append("\n");

        // Sorted election results by decreasing party percentage of the most representative polling station
        List<Map.Entry<Party, Double>> resultsOfRepresentativeStation = sortedElectionResultsByPartyPercentage(totalParties, getVotesByPartyAcrossPollingStations(Collections.singletonList(mostRepresentativeStation)));
        summary.append("Election results of the most representative polling station:\n");
        resultsOfRepresentativeStation.forEach(entry -> summary.append(entry.getKey()).append(": ").append(entry.getValue()).append("%\n"));

        return summary.toString();
    }


    /**
     * Reads all data of Parties, Candidates, Contingencies and PollingStations from available files in the given folder and its subfolders
     * This method can cope with any structure of sub folders, but does assume the file names to comply with the conventions
     * as found from downloading the files from https://data.overheid.nl/dataset/verkiezingsuitslag-tweede-kamer-2021
     * So, you can merge folders after unpacking the zip distributions of the data, but do not change file names.
     * @param folderName    the root folder with the data files of the election results
     * @return een Election met alle daarbij behorende gegevens.
     * @throws XMLStreamException bij fouten in een van de XML bestanden.
     * @throws IOException als er iets mis gaat bij het lezen van een van de bestanden.
     */
    public static Election importFromDataFolder(String folderName) throws XMLStreamException, IOException {
        System.out.println("Loading election data from " + folderName);
        Election election = new Election(folderName);
        int progress = 0;
        Map<Integer, Constituency> kieskringen = new HashMap<>();
        for (Path constituencyCandidatesFile : PathUtils.findFilesToScan(folderName, "Kandidatenlijsten_TK2021_")) {
            XMLParser parser = new XMLParser(new FileInputStream(constituencyCandidatesFile.toString()));
            Constituency constituency = Constituency.importFromXML(parser, election.parties);
            //election.constituenciesM.put(constituency.getId(), constituency);
            election.constituencies.add(constituency);
            showProgress(++progress);
        }
        System.out.println();
        progress = 0;
        for (Path votesPerPollingStationFile : PathUtils.findFilesToScan(folderName, "Telling_TK2021_gemeente")) {
            XMLParser parser = new XMLParser(new FileInputStream(votesPerPollingStationFile.toString()));
            election.importVotesFromXml(parser);
            showProgress(++progress);
        }
        System.out.println();
        return election;
    }

    protected static void showProgress(final int progress) {
        System.out.print('.');
        if (progress % 50 == 0) System.out.println();
    }

    /**
     * Auxiliary method for parsing the data from the EML files
     * This methode can be used as-is and does not require your investigation or extension.
     */
    public void importVotesFromXml(XMLParser parser) throws XMLStreamException {
        if (parser.findBeginTag(Constituency.CONSTITUENCY)) {

            int constituencyId = 0;
            if (parser.findBeginTag(Constituency.CONSTITUENCY_IDENTIFIER)) {
                constituencyId = parser.getIntegerAttributeValue(null, Constituency.ID, 0);
                parser.findAndAcceptEndTag(Constituency.CONSTITUENCY_IDENTIFIER);
            }

            //Constituency constituency = this.constituenciesM.get(constituencyId);
            final int finalConstituencyId = constituencyId;
            Constituency constituency = this.constituencies.stream()
                    .filter(c -> c.getId() == finalConstituencyId)
                    .findFirst()
                    .orElse(null);

            //parser.findBeginTag(PollingStation.POLLING_STATION_VOTES);
            while (parser.findBeginTag(PollingStation.POLLING_STATION_VOTES)) {
                PollingStation pollingStation = PollingStation.importFromXml(parser, constituency, this.parties);
                if (pollingStation != null) constituency.add(pollingStation);
            }

            parser.findAndAcceptEndTag(Constituency.CONSTITUENCY);
        }
    }

    /**
     * HINTS:
     * getCandidatesWithDuplicateNames:
     *  Approach-1: first build a Map that counts the number of candidates per given name
     *              then build the collection from all candidates, excluding those whose name occurs only once.
     *  Approach-2: build a stream that is sorted by name
     *              apply a mapMulti that drops unique names but keeps the duplicates
     *              this approach probably requires complex lambda expressions that are difficult to justify
     */

}
