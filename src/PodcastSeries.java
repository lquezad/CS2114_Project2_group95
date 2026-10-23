
// -------------------------------------------------------------------------
/**
 * A podcast series is a titled, ordered collection of podcast episodes with a
 * host.
 * 
 * @author Krish Patel krishp24
 * @version Oct 22, 2026
 */
public class PodcastSeries
{
    // ~ Fields ................................................................
    private static final String UNKNOWN = "Unknown";
    private final String title;
    private final String host;
    private final LinkedChain<Podcast> episodes;

    // ----------------------------------------------------------
    /**
     * Constructor Create a new PodcastSeries object.
     * 
     * @param title
     *            the series title
     * @param host
     *            the series host
     */
    public PodcastSeries(String title, String host)
    {
        if (title == null || title.trim().isEmpty())
        {
            throw new IllegalArgumentException("Title must not be blank");
        }
        this.title = title.trim();
        if (host == null || host.trim().isEmpty())
        {
            this.host = UNKNOWN;
        }
        else
        {
            this.host = host.trim();
        }
        this.episodes = new LinkedChain<Podcast>();
    }


    // ----------------------------------------------------------
    /**
     * gets title
     * 
     * @return the series title
     */
    public String getTitle()
    {
        return title;
    }


    // ----------------------------------------------------------
    /**
     * gets the series host
     * 
     * @return the series host
     */
    public String getHost()
    {
        return host;
    }


    // ----------------------------------------------------------
    /**
     * adds an episode
     * 
     * @param episode
     *            to be added
     */
    public void addEpisode(Podcast episode)
    {
        if (episode == null)
        {
            throw new IllegalArgumentException("Episode must not be null");
        }
        episodes.addToEnd(episode);
    }


    // ----------------------------------------------------------
    /**
     * removes an episode from series
     * 
     * @param episode
     *            to remove
     * @return true if removed
     */
    public boolean removeEpisode(Podcast episode)
    {
        return episodes.remove(episode);
    }


    // ----------------------------------------------------------
    /**
     * gets episodes in order
     * 
     * @return the episodes
     */
    public Podcast[] getEpisodes()
    {
        return episodes.toArray(new Podcast[episodes.size()]);
    }


    // ----------------------------------------------------------
    /**
     * gets number of episodes
     * 
     * @return number of episodes
     */
    public int size()
    {
        return episodes.size();
    }


    // ----------------------------------------------------------
    /**
     * checks if the series is empty
     * 
     * @return true if its empty, false if not
     */
    public boolean isEmpty()
    {
        return episodes.isEmpty();
    }


    // ----------------------------------------------------------
    /**
     * adds the length of every episode
     * 
     * @return total length in seconds
     */
    public int getTotalLength()
    {
        int total = 0;
        for (Podcast p : getEpisodes())
        {
            total += p.getLength();
        }
        return total;
    }


    // ----------------------------------------------------------
    /**
     * adds every episode in order to the end of the playlist
     * 
     * @param playlist
     *            where it gets added
     */
    public void addToPlaylist(Playlist playlist)
    {
        if (playlist == null)
        {
            throw new IllegalArgumentException("Playlist must not be null");
        }
        for (Podcast p : getEpisodes())
        {
            playlist.add(p);
        }
    }


    // ----------------------------------------------------------
    /**
     * Describes the series. Example: "Series Name - Host (# episodes) total
     * length".
     * 
     * @return the display line
     */
    public String describe()
    {
        int total = getTotalLength();
        String ending;
        if (episodes.size() == 1)
        {
            ending = " episode) ";
        }
        else
        {
            ending = " episodes) ";
        }
        return title + " - " + host + " (" + episodes.size() + ending
            + (total / 60) + ":" + String.format("%02d", total % 60);
    }


    /**
     * returns the same text as describe
     * 
     * @return the display line
     */
    @Override
    public String toString()
    {
        return describe();
    }

}
