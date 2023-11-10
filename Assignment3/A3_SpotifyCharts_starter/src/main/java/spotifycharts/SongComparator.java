package spotifycharts;

import java.util.Comparator;

public class SongComparator implements Comparator<Song> {

    @Override
    public int compare(Song song1, Song song2) {
        // Compare songs based on the highest total number of streams
        int totalStreamsComparison = song2.getStreamsCountTotal() - song1.getStreamsCountTotal();

        if (totalStreamsComparison != 0) {
            return totalStreamsComparison;
        }

        // For Dutch national chart, order Dutch songs upfront and then by decreasing total number of streams
        int dutchComparison = Boolean.compare(song2.getLanguage() == Song.Language.NL, song1.getLanguage() == Song.Language.NL);

        if (dutchComparison != 0) {
            return dutchComparison;
        }

        return Integer.compare(song2.getStreamsCountTotal(), song1.getStreamsCountTotal());
    }
}
