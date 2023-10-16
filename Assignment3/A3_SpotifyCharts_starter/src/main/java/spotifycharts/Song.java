package spotifycharts;

import java.util.HashMap;

public class Song {

    public enum Language {
        EN, // English
        NL, // Dutch
        DE, // German
        FR, // French
        SP, // Spanish
        IT, // Italian
    }

    public enum Country {
        UK, // United Kingdom
        NL, // Netherlands
        DE, // Germany
        BE, // Belgium
        FR, // France
        SP, // Spain
        IT  // Italy
    }

    private final String artist;
    private final String title;
    private final Language language;

    private HashMap<Country, Integer> streamsPerCountry;

    // TODO add instance variable(s) to track the streams counts per country
    //  choose a data structure that you deem to be most appropriate for this application.



    /**
     * Constructs a new instance of Song based on given attribute values
     */
    public Song(String artist, String title, Language language) {
        this.artist = artist;
        this.title = title;
        this.language = language;
        streamsPerCountry = new HashMap<>();


        // TODO initialise streams counts per country as appropriate.

    }

    /**
     * Sets the given streams count for the given country on this song
     * @param country
     * @param streamsCount
     */
    public void setStreamsCountOfCountry(Country country, int streamsCount) {
        // TODO register the streams count for the given country.
        streamsPerCountry.put(country,streamsCount);
    }

    /**
     * Retrieves the stream count of a given country for this song.
     * @param country The country for which you want to retrieve the stream count.
     * @return The stream count for the specified country, or 0 if the country is not found.
     */
    public int getStreamsCountOfCountry(Country country) {
        // Country not found, return 0 or an appropriate default value.
        // replace by the proper amount
        //getOrDefault first parameter is for the worth you want to get
        // the 0 is for the return if it didn't is there
        return streamsPerCountry.getOrDefault(country, 0);
    }
    /**
     * Calculates/retrieves the total of all streams counts across all countries from this song
     * @return
     */
    public int getStreamsCountTotal() {
        int sum = 0;

        for (int i : streamsPerCountry.values()){
            sum = sum + i;
        }
        // TODO calculate/get the total number of streams across all countries
        return sum; // replace by the proper amount
    }


    /**
     * compares this song with the other song
     * ordening songs with the highest total number of streams upfront
     * @param other     the other song to compare against
     * @return  negative number, zero or positive number according to Comparator convention
     */
    public int compareByHighestStreamsCountTotal(Song other) {
        // TODO compare the total of stream counts of this song across all countries
        //  with the total of the other song
        return other.getStreamsCountTotal() - this.getStreamsCountTotal();    // replace by proper result
    }

    /**
     * compares this song with the other song
     * ordening all Dutch songs upfront and then by decreasing total number of streams
     * @param other     the other song to compare against
     * @return  negative number, zero or positive number according to Comparator conventions
     */
    public int compareForDutchNationalChart(Song other) {
        // TODO compare this song with the other song
        //  ordening all Dutch songs upfront and
        //  then by decreasing total number of streams

        int dutchComp = Boolean.compare(other.language == Language.NL , this.language == Language.NL);

        if (dutchComp != 0) {
            return dutchComp;
        }

        return Integer.compare(other.getStreamsCountTotal(), this.getStreamsCountTotal());

    }


    public String getArtist() {
        return artist;
    }

    public String getTitle() {
        return title;
    }

    public Language getLanguage() {
        return language;
    }

    public String toString(){
        return artist+"/"+title+"{"+language+"}"+"("+getStreamsCountTotal()+")";
    }

    // TODO provide a toString implementation to format songs as in "artist/title{language}(total streamsCount)"


}
