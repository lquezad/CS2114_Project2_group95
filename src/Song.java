import java.util.*;

// -------------------------------------------------------------------------
/**
 * Write a one-sentence summary of your class here. Follow it with additional
 * details about its purpose, what abstraction it represents, and how to use it.
 * 
 * @author Leo Quezada-Rocha
 * @version Oct 14, 2026
 */
public class Song implements Media
{
    // ~ Fields ................................................................
    private static final String UNKNOWN = "Unknown";

    private final String title;   // name of the song
    private final String artist;  // artist who made the song
    private final String album;   // album the song is part of
    private final int length;     // length in seconds
    // ~ Constructors ..........................................................

    // ~Public Methods ........................................................
    /**
     * Builds a song.
     *
     * @param title
     *            the song title; must not be null or blank
     * @param artist
     *            the artist; null or blank becomes "Unknown"
     * @param album
     *            the album; null or blank becomes "Unknown"
     * @param length
     *            the length in seconds; must be greater than 0
     * @throws IllegalArgumentException
     *             if title is null or blank, or length is 0 or less
     */
    public Song(String title, String artist, String album, int length)
    {
        if (title == null || title.trim().isEmpty())
        {
            throw new IllegalArgumentException("Title must not be blank");
        }
        if (length <= 0)
        {
            throw new IllegalArgumentException("Length must be greater than 0");
        }
        this.title = title.trim();
        this.artist = clean(artist);
        this.album = clean(album);
        this.length = length;
    }


    /**
     * Trims a text field, replacing null or blank with "Unknown".
     *
     * @param value
     *            the text to clean
     * @return the trimmed text, or "Unknown"
     */
    private static String clean(String value)
    {
        if (value == null || value.trim().isEmpty())
        {
            return UNKNOWN;
        }
        return value.trim();
    }


    /**
     * Gets the title.
     *
     * @return the song title
     */
    @Override
    public String getTitle()
    {
        return title;
    }


    /**
     * Gets the length.
     *
     * @return the length in seconds
     */
    @Override
    public int getLength()
    {
        return length;
    }


    /**
     * Gets the artist.
     *
     * @return the artist, or "Unknown"
     */
    public String getArtist()
    {
        return artist;
    }


    /**
     * Gets the album.
     *
     * @return the album, or "Unknown"
     */
    public String getAlbum()
    {
        return album;
    }


    /**
     * Describes this song on one line, e.g. "Yellow - Coldplay (Parachutes)
     * 4:26".
     *
     * @return the display line
     */
    @Override
    public String describe()
    {
        return title + " - " + artist + " (" + album + ") " + (length / 60)
            + ":" + String.format("%02d", length % 60);
    }


    /**
     * Songs can be shuffled.
     *
     * @return true
     */
    @Override
    public boolean isShuffleable()
    {
        return true;
    }


    /**
     * Two songs are equal when title, artist, album, and length match.
     *
     * @param obj
     *            the object to compare with
     * @return true if obj is an equal Song; false for null or another class
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null || getClass() != obj.getClass())
        {
            return false;
        }
        Song other = (Song)obj;
        return length == other.length && title.equals(other.title)
            && artist.equals(other.artist) && album.equals(other.album);
    }


    /**
     * Hash code consistent with equals.
     *
     * @return the hash code
     */
    @Override
    public int hashCode()
    {
        return Objects.hash(title, artist, album, length);
    }


    /**
     * Returns the same text as describe(), so a LinkedChain prints readably.
     *
     * @return the display line
     */
    @Override
    public String toString()
    {
        return describe();
    }
}
