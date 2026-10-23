import student.TestCase;

// -------------------------------------------------------------------------
/**
 *  tests the PodcastSeries class
 * 
 *  @author krish
 *  @version Oct 22, 2026
 */
public class PodcastSeriesTest extends TestCase
{

    
    //~ Fields ................................................................
    private PodcastSeries series;
    private Podcast ep1;
    private Podcast ep2;

    //~ Constructors ..........................................................

    //~Public  Methods ........................................................
    // ----------------------------------------------------------
    /**
     * Creates an empty series and two episodes before each test.
     */
    public void setUp()
    {
        series = new PodcastSeries("Series", "Host Name");
        ep1 = new Podcast("Episode 1", "Host Name", 1, 3000);
        ep2 = new Podcast("Episode 2", "Host Name", 2, 2800);
    }
    
    
    // ----------------------------------------------------------
    /**
     * normal case for constructor
     */
    public void testConstructor()
    {
        assertEquals("Series", series.getTitle());
        assertEquals("Host Name", series.getHost());
        assertTrue(series.isEmpty());
    }
    
    
    // ----------------------------------------------------------
    /**
     * Checks that extra spaces around title and host are trimmed
     */
    public void testConstructorTrims()
    {
        PodcastSeries s = new PodcastSeries("  Series  ", "  Host Name  ");
        assertEquals("Series", s.getTitle());
        assertEquals("Host Name", s.getHost());
    }
    
    
    // ----------------------------------------------------------
    /**
     * Checks that null, empty or blank title throws an illegal
     * argument exception
     */
    public void testConstructorBlankTitle()
    {
        String[] titles = { null, "", "   " };
        for (String t : titles)
        {
            Exception exception = null;
            try
            {
                new PodcastSeries(t, "Host Name");
            }
            catch (IllegalArgumentException e)
            {
                exception = e;
            }
            assertNotNull(exception);
            assertEquals("Title must not be blank", exception.getMessage());
        }
    }
    
    
    // ----------------------------------------------------------
    /**
     * Checks that a null or blank host becomes unknown.
     */
    public void testConstructorBlankHost()
    {
        assertEquals("Unknown", new PodcastSeries("Series", null).getHost());
        assertEquals("Unknown", new PodcastSeries("Series", "  ").getHost());
    }
    
    
    
    // ----------------------------------------------------------
    /**
     * Checks that episodes are added in order at the end of the series
     */
    public void testAddEpisode()
    {
        series.addEpisode(ep1);
        series.addEpisode(ep2);
        assertEquals(2, series.size());
        assertFalse(series.isEmpty());
        assertEquals(ep1, series.getEpisodes()[0]);
        assertEquals(ep2, series.getEpisodes()[1]);
    }
    
    
    // ----------------------------------------------------------
    /**
     * makes sure the same episode can be added more than once
     */
    public void testAddEpisodeDuplicate()
    {
        series.addEpisode(ep1);
        series.addEpisode(ep1);
        assertEquals(2, series.size());
    }
    
    
    
    // ----------------------------------------------------------
    /**
     * Checks that adding null throws an IllegalArgumentException
     * and makes sure the series stays the same
     */
    public void testAddEpisodeNull()
    {
        series.addEpisode(ep1);
        Exception exception = null;
        try
        {
            series.addEpisode(null);
        }
        catch (IllegalArgumentException e)
        {
            exception = e;
        }
        assertNotNull(exception);
        assertEquals("Episode must not be null", exception.getMessage());
        assertEquals(1, series.size());
    }
    
    
    
    // ----------------------------------------------------------
    /**
     * Checks that removeEpisode removes it and closes the gap
     */
    public void testRemoveEpisode()
    {
        series.addEpisode(ep1);
        series.addEpisode(ep2);
        assertTrue(series.removeEpisode(ep1));
        assertEquals(1, series.size());
        assertEquals(ep2, series.getEpisodes()[0]);
    }
    
    
    // ----------------------------------------------------------
    /**
     * Checks that remove episode removes the first duplicate
     */
    public void testRemoveEpisodeDuplicate()
    {
        series.addEpisode(ep1);
        series.addEpisode(ep2);
        series.addEpisode(ep1);
        assertTrue(series.removeEpisode(ep1));
        assertEquals(2, series.size());
        assertEquals(ep2, series.getEpisodes()[0]);
        assertEquals(ep1, series.getEpisodes()[1]);
    }
    
    
    
    // ----------------------------------------------------------
    /**
     * Checks that removeEpisode removes an episode with the same info, even
     * if it is a new object.
     */
    public void testRemoveEpisodeEqualCopy()
    {
        series.addEpisode(ep1);
        assertTrue(series.removeEpisode(
            new Podcast("Episode 1", "Host Name", 1, 3000)));
        assertTrue(series.isEmpty());
    }
    
    
    
    // ----------------------------------------------------------
    /**
     * tests that removing an episode returns false and changes nothing
     */
    public void testRemoveEpisodeNotFound()
    {
        series.addEpisode(ep1);
        assertFalse(series.removeEpisode(ep2));
        assertEquals(1, series.size());
    }
    
    // ----------------------------------------------------------
    /**
     * checks that removing null returns false
     */
    public void testRemoveEpisodeNull()
    {
        series.addEpisode(ep1);
        assertFalse(series.removeEpisode(null));
        assertEquals(1, series.size());
    }
    
    
    // ----------------------------------------------------------
    /**
     * removing from an empty series returns false
     */
    public void testRemoveEpisodeEmptySeries()
    {
        assertFalse(series.removeEpisode(ep1));
        assertTrue(series.isEmpty());
    }
    
    
    
    // ----------------------------------------------------------
    /**
     * Checks that getEpisodes returns the episodes in the order they were
     * added
     */
    public void testGetEpisodes()
    {
        series.addEpisode(ep1);
        series.addEpisode(ep2);
        Podcast[] result = series.getEpisodes();
        assertEquals(2, result.length);
        assertEquals(ep1, result[0]);
        assertEquals(ep2, result[1]);
    }
    
    
    // ----------------------------------------------------------
    /**
     * Checks that getEpisodes returns an empty array for series
     */
    public void testGetEpisodesEmptySeries()
    {
        assertEquals(0, series.getEpisodes().length);
    }
    
    
    // ----------------------------------------------------------
    /**
     * Checks that getEpisodes gives a copy.
     */
    public void testGetEpisodesIsACopy()
    {
        series.addEpisode(ep1);
        Podcast[] result = series.getEpisodes();
        result[0] = ep2;
        assertEquals(ep1, series.getEpisodes()[0]);
    }
    
    
    
    // ----------------------------------------------------------
    /**
     * checks that size and isempty is correct as the series changes
     * 
     */
    public void testSizeAndIsEmpty()
    {
        assertEquals(0, series.size());
        assertTrue(series.isEmpty());
        series.addEpisode(ep1);
        series.addEpisode(ep2);
        assertEquals(2, series.size());
        assertFalse(series.isEmpty());
        series.removeEpisode(ep1);
        series.removeEpisode(ep2);
        assertEquals(0, series.size());
        assertTrue(series.isEmpty());
    }
    
    
    
    // ----------------------------------------------------------
    /**
     * checks that total length is the sum of every episodes length
     */
    public void testGetTotalLength()
    {
        series.addEpisode(ep1);
        series.addEpisode(ep2);
        assertEquals(5800, series.getTotalLength());
    }
    
    // ----------------------------------------------------------
    /**
     * checks that an empty series has a total length of 0
     */
    public void testGetTotalLengthEmptySeries()
    {
        assertEquals(0, series.getTotalLength());
    }
    
    
    
    
    // ----------------------------------------------------------
    /**
     * checks that episodes are added to the series, in order and stay there.
     */
    public void testAddToPlaylist()
    {
        series.addEpisode(ep1);
        series.addEpisode(ep2);
        Playlist p = new Playlist();
        Song other = new Song("Other", "x", "y", 10);
        p.add(other);
        series.addToPlaylist(p);
        assertEquals(3, p.size());
        assertEquals(other, p.getItems()[0]);
        assertEquals(ep1, p.getItems()[1]);
        assertEquals(ep2, p.getItems()[2]);
        assertEquals(2, series.size());
    }
    
    
    
    // ----------------------------------------------------------
    /**
     * Checks that adding an empty series leaves the playlist empty
     */
    public void testAddToPlaylistEmptySeries()
    {
        Playlist p = new Playlist();
        series.addToPlaylist(p);
        assertTrue(p.isEmpty());
    }
    
    
    
    // ----------------------------------------------------------
    /**
     * Checks that a null playlist throws an illegalargumentexception
     */
    public void testAddToPlaylistNull()
    {
        series.addEpisode(ep1);
        Exception exception = null;
        try
        {
            series.addToPlaylist(null);
        }
        catch (IllegalArgumentException e)
        {
            exception = e;
        }
        assertNotNull(exception);
        assertEquals("Playlist must not be null", exception.getMessage());
    }
    
    
    // ----------------------------------------------------------
    /**
     * Checks that describe shows the title, host, plural episode count, and
     * total length
     */
    public void testDescribe()
    {
        series.addEpisode(ep1);
        series.addEpisode(ep2);
        assertEquals("Series - Host Name (2 episodes) 96:40",
            series.describe());
    }
    
    
    
    // ----------------------------------------------------------
    /**
     * Checks that describe says 1 episode for 1 episode series
     */
    public void testDescribeOneEpisode()
    {
        series.addEpisode(ep1);
        assertEquals("Series - Host Name (1 episode) 50:00",
            series.describe());
    }
    
    
    
    
    // ----------------------------------------------------------
    /**
     * Checks that describe shows 0 episodes and 0 length for empty series
     */
    public void testDescribeEmptySeries()
    {
        assertEquals("Series - Host Name (0 episodes) 0:00",
            series.describe());
    }
    
    
    // ----------------------------------------------------------
    /**
     * Checks that describe formats the time as m:ss
     */
    public void testDescribeTimeFormat()
    {
        series.addEpisode(new Podcast("Episode 3", "Host Name", 3, 65));
        assertEquals("Series - Host Name (1 episode) 1:05",
            series.describe());
    }
    
    // ----------------------------------------------------------
    /**
     * checks that toString returns the same thing as describe
     */
    public void testToString()
    {
        series.addEpisode(ep1);
        assertEquals(series.describe(), series.toString());
    }
}
