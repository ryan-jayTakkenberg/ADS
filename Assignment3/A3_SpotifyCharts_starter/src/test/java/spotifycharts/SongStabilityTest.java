package spotifycharts;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SongStabilityTest {

    @Test
     void testCompareSameSong() {
        Song song = new Song("Artist", "Title", Song.Language.EN);
        song.setStreamsCountOfCountry(Song.Country.UK, 100);
        SongComparator comparator = new SongComparator();

        assertEquals(0, comparator.compare(song, song));
    }

    @Test
     void testCompareSymmetry() {
        Song song1 = new Song("Artist1", "Title1", Song.Language.NL);
        song1.setStreamsCountOfCountry(Song.Country.NL, 200);

        Song song2 = new Song("Artist2", "Title2", Song.Language.NL);
        song2.setStreamsCountOfCountry(Song.Country.NL, 150);

        SongComparator comparator = new SongComparator();

        int result1 = comparator.compare(song1, song2);
        int result2 = comparator.compare(song2, song1);

        assertEquals(result1, -result2);
    }


    @Test
    void testCompareSongsWithNoStreams() {
        // Test when comparing songs with no streams in any country
        Song song1 = new Song("Artist1", "Title1", Song.Language.EN);
        Song song2 = new Song("Artist2", "Title2", Song.Language.EN);
        SongComparator comparator = new SongComparator();

        int result = comparator.compare(song1, song2);

        // Since both songs have no streams, they should be considered equal
        assertEquals(0, result);
    }

    @Test
    void testCompareSongsWithEqualTotalStreams() {
        // Test when comparing songs with equal total streams but different distribution
        Song song1 = new Song("Artist1", "Title1", Song.Language.NL);
        song1.setStreamsCountOfCountry(Song.Country.NL, 100);
        song1.setStreamsCountOfCountry(Song.Country.BE, 50);

        Song song2 = new Song("Artist2", "Title2", Song.Language.NL);
        song2.setStreamsCountOfCountry(Song.Country.NL, 50);
        song2.setStreamsCountOfCountry(Song.Country.BE, 100);

        SongComparator comparator = new SongComparator();

        int result = comparator.compare(song1, song2);

        // Even though total streams are equal, the Dutch song (song1) should be ranked higher
        assertEquals(-1, result);
    }
}
