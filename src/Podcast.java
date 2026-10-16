import java.util.Objects;

// -------------------------------------------------------------------------
/**
 * a podcast has a title, host, episode number and length
 * 
 * @author Krish Patel
 * @version Oct 15, 2026
 */
public class Podcast implements Media
{
    // ~ Fields ................................................................
    private String title;
    private String host;
    private int episodeNumber;
    private int length;

    // ~ Constructors ..........................................................
    /**
     * makes a podcast episode
     * 
     * @param title
     *            the title of the podcast
     * @param host
     *            the name of the host
     * @param episodeNumber
     *            the episode
     * @param length
     *            the length in seconds
     */
    public Podcast(String title, String host, int episodeNumber, int length)
    {
        if (title == null || title.trim().isEmpty())
        {
            throw new IllegalArgumentException("Title must not be blank");
        }

        if (length <= 0)
        {
            throw new IllegalArgumentException("Length must be greater than 0");
        }

        if (episodeNumber <= 0)
        {
            throw new IllegalArgumentException(
                "Episode number must be greater than 0");
        }

        this.title = title;
        this.length = length;
        this.episodeNumber = episodeNumber;

        if (host == null || host.trim().isEmpty())
        {
            this.host = "Unknown";
        }
        else
        {
            this.host = host;
        }
    }
    
    // ~Public Methods ........................................................
    
    
    
    /**
     * Gets the podcast title
     * @return the podcast title
     */
    public String getTitle()
    {
        return title;
    }
    
    
    
    // ----------------------------------------------------------
    /**
     * gets the podcast host
     * @return the podcasts host
     */
    public String getHost()
    {
        return host;
    }

    // ----------------------------------------------------------
    /**
     * gets episode number
     * @return the podcasts episode number
     */
    public int getEpisodeNumber()
    {
        return episodeNumber;
    }

    // ----------------------------------------------------------
    /**
     * gets the length of the podcast in seconds
     * @return length of podcast in seconds
     */
    public int getLength()
    {
        return length;
    }
    
    /**
     * whether the podcast can be shuffled
     *
     * @return false because podcasts are not shuffled
     */
    public boolean isShuffleable()
    {
        return false;
    }
    
    /**
     * makes a hash code for the podcast
     *
     * @return the podcast hash code
     */
    @Override
    public int hashCode()
    {
        return Objects.hash(title, host, episodeNumber, length);
    }
    
    
    /**
     * compares this podcast with another object
     *
     * @param obj the object to compare
     * @return true if both podcasts have the same information
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (!(obj instanceof Podcast))
        {
            return false;
        }

        Podcast other = (Podcast)obj;

        return title.equals(other.title)
            && host.equals(other.host)
            && episodeNumber == other.episodeNumber
            && length == other.length;
    }
    
    
    /**
     * describes the podcast episode
     *
     * @return the podcast information with formatted length
     */
    public String describe()
    {
        int minutes = length / 60;
        int seconds = length % 60;

        return title + " #" + episodeNumber + " - " + host
            + " " + String.format("%d:%02d", minutes, seconds);
    }
}
