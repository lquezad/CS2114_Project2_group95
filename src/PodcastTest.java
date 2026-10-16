import student.TestCase;

/**
 * Tests for Podcast.
 */
public class PodcastTest extends TestCase
{
    private Podcast hh;


    public void setUp()
    {
        hh = new Podcast("Hardcore History", "Dan", 12, 3600);
    }


    public void testPodcastConstructor()
    {
        assertEquals("Hardcore History", hh.getTitle());
        assertEquals("Dan", hh.getHost());
        assertEquals(12, hh.getEpisodeNumber());
        assertEquals(3600, hh.getLength());
    }


    public void testPodcastConstructorBlankTitle()
    {
        String[] titles = { null, "", "   " };
        for (String t : titles)
        {
            try
            {
                new Podcast(t, "Dan", 12, 3600);
                fail("Expected IllegalArgumentException for title: " + t);
            }
            catch (IllegalArgumentException e)
            {
                assertEquals("Title must not be blank", e.getMessage());
            }
        }
    }


    public void testPodcastConstructorInvalidLength()
    {
        int[] lengths = { 0, -1 };
        for (int len : lengths)
        {
            try
            {
                new Podcast("Hardcore History", "Dan", 12, len);
                fail("Expected IllegalArgumentException for length " + len);
            }
            catch (IllegalArgumentException e)
            {
                assertEquals("Length must be greater than 0", e.getMessage());
            }
        }
    }


    public void testPodcastConstructorInvalidEpisodeNumber()
    {
        int[] eps = { 0, -3 };
        for (int ep : eps)
        {
            try
            {
                new Podcast("Hardcore History", "Dan", ep, 3600);
                fail("Expected IllegalArgumentException for episode " + ep);
            }
            catch (IllegalArgumentException e)
            {
                assertEquals("Episode number must be greater than 0",
                    e.getMessage());
            }
        }
    }


    public void testPodcastConstructorBlankHost()
    {
        assertEquals("Unknown",
            new Podcast("Hardcore History", null, 12, 3600).getHost());
        assertEquals("Unknown",
            new Podcast("Hardcore History", "  ", 12, 3600).getHost());
    }


    public void testGetTitlePodcast()
    {
        assertEquals("Hardcore History", hh.getTitle());
    }


    public void testGetLengthPodcast()
    {
        assertEquals(3600, hh.getLength());
    }


    public void testGetHost()
    {
        assertEquals("Dan", hh.getHost());
    }


    public void testGetHostUnknown()
    {
        assertEquals("Unknown",
            new Podcast("Hardcore History", null, 12, 3600).getHost());
    }


    public void testGetEpisodeNumber()
    {
        assertEquals(12, hh.getEpisodeNumber());
    }


    public void testGetEpisodeNumberSmallest()
    {
        Podcast p = new Podcast("Hardcore History", "Dan", 1, 3600);
        assertEquals(1, p.getEpisodeNumber());
    }


    public void testDescribePodcast()
    {
        assertEquals("Hardcore History #12 - Dan 60:00", hh.describe());
    }


    public void testDescribePodcastShortLength()
    {
        Podcast p = new Podcast("Short", "Dan", 1, 59);
        assertEquals("Short #1 - Dan 0:59", p.describe());
    }


    public void testIsShuffleablePodcast()
    {
        assertFalse(hh.isShuffleable());
    }


    public void testEqualsPodcast()
    {
        Podcast other = new Podcast("Hardcore History", "Dan", 12, 3600);
        assertTrue(hh.equals(other));
        assertTrue(hh.equals(hh));
    }


    public void testEqualsPodcastDifferentEpisodeNumber()
    {
        Podcast other = new Podcast("Hardcore History", "Dan", 13, 3600);
        assertFalse(hh.equals(other));
    }


    public void testEqualsPodcastDifferentTitle()
    {
        Podcast other = new Podcast("Other Show", "Dan", 12, 3600);
        assertFalse(hh.equals(other));
    }


    public void testEqualsPodcastDifferentHostAndLength()
    {
        assertFalse(hh.equals(
            new Podcast("Hardcore History", "Other", 12, 3600)));
        assertFalse(hh.equals(
            new Podcast("Hardcore History", "Dan", 12, 100)));
    }


    public void testEqualsPodcastNull()
    {
        assertFalse(hh.equals(null));
    }


    public void testHashCodePodcast()
    {
        Podcast other = new Podcast("Hardcore History", "Dan", 12, 3600);
        assertEquals(hh.hashCode(), other.hashCode());
    }
}