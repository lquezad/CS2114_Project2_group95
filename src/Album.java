// -------------------------------------------------------------------------
/**
 * An album is a titled, ordered collection of songs by one artist. The songs
 * are stored in a LinkedChain, in track order, and duplicates are allowed.
 *
 * @author Leo Quezada-Rocha
 * @version Oct 22, 2026
 */
public class Album
{
    // ~ Fields ................................................................
    private static final String UNKNOWN = "Unknown";

    private final String title;
    private final String artist;
    private final LinkedChain<Song> tracks;

    // ~ Constructors ..........................................................
    /**
     * Creates an empty album.
     *
     * @param title
     *            the album title; must not be null or blank
     * @param artist
     *            the artist; null or blank becomes "Unknown"
     * @throws IllegalArgumentException
     *             if title is null or blank
     */
    public Album(String title, String artist)
    {
        if (title == null || title.trim().isEmpty())
        {
            throw new IllegalArgumentException("Title must not be blank");
        }
        this.title = title.trim();
        if (artist == null || artist.trim().isEmpty())
        {
            this.artist = UNKNOWN;
        }
        else
        {
            this.artist = artist.trim();
        }
        this.tracks = new LinkedChain<Song>();
    }


    // ~Public Methods ........................................................
    /**
     * Gets the title.
     *
     * @return the album title
     */
    public String getTitle()
    {
        return title;
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
     * Adds a song to the end of the album. Duplicates are allowed.
     *
     * @param song
     *            the song to add
     * @throws IllegalArgumentException
     *             if song is null
     */
    public void add(Song song)
    {
        tracks.addToEnd(song);
    }


    /**
     * Removes and returns the song at the given track position.
     *
     * @param index
     *            the zero-based position to remove
     * @return the removed song, or null if index is out of range
     */
    public Song removeAt(int index)
    {
        if (index < 0 || index >= tracks.size())
        {
            return null;
        }
        return tracks.removeAt(index);
    }


    /**
     * Removes the first song that equals the given song.
     *
     * @param song
     *            the song to remove
     * @return true if a song was removed, or false if song is null or was not
     *             found
     */
    public boolean remove(Song song)
    {
        return tracks.remove(song);
    }


    /**
     * Gets the song at the given track position.
     *
     * @param index
     *            the zero-based position
     * @return the song, or null if index is out of range
     */
    public Song get(int index)
    {
        if (index < 0 || index >= tracks.size())
        {
            return null;
        }
        return tracks.get(index);
    }


    /**
     * Tells whether the album holds a song equal to the given one.
     *
     * @param song
     *            the song to look for
     * @return true if found, or false if not found or song is null
     */
    public boolean contains(Song song)
    {
        return tracks.contains(song);
    }


    /**
     * Gets the position of the first song equal to the given one.
     *
     * @param song
     *            the song to look for
     * @return the zero-based position, or -1 if not found or song is null
     */
    public int indexOf(Song song)
    {
        return tracks.indexOf(song);
    }


    /**
     * Gets a copy of the songs in track order.
     *
     * @return the songs, or an array of length 0 if the album is empty
     */
    public Song[] getSongs()
    {
        return tracks.toArray(new Song[tracks.size()]);
    }


    /**
     * Gets the number of songs.
     *
     * @return the number of songs
     */
    public int size()
    {
        return tracks.size();
    }


    /**
     * Sees whether the album has no songs.
     *
     * @return true if empty, or false if not
     */
    public boolean isEmpty()
    {
        return tracks.isEmpty();
    }


    /**
     * Adds up the length of every song.
     *
     * @return the total length in seconds (0 for an empty album)
     */
    public int getTotalLength()
    {
        int total = 0;
        for (Song s : getSongs())
        {
            total += s.getLength();
        }
        return total;
    }


    /**
     * Adds every song, in track order, to the end of a playlist.
     *
     * @param playlist
     *            the playlist to add to
     * @throws IllegalArgumentException
     *             if playlist is null
     */
    public void addToPlaylist(Playlist playlist)
    {
        if (playlist == null)
        {
            throw new IllegalArgumentException("Playlist must not be null");
        }
        for (Song s : getSongs())
        {
            playlist.add(s);
        }
    }


    /**
     * Describes this album on one line, e.g. "Parachutes - Coldplay (2 songs)
     * 8:12".
     *
     * @return the display line
     */
    public String describe()
    {
        int total = getTotalLength();
        String ending;
        if (tracks.size() == 1)
        {
            ending = " song) ";
        }
        else
        {
            ending = " songs) ";
        }
        return title + " - " + artist + " (" + tracks.size() + ending
            + (total / 60) + ":" + String.format("%02d", total % 60);
    }


    /**
     * Returns the same text as describe().
     *
     * @return the display line
     */
    @Override
    public String toString()
    {
        return describe();
    }
}