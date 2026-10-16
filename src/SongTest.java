import student.TestCase;

/**
 * Tests for Song.
 */
public class SongTest extends TestCase
{
    private Song yellow;


    public void setUp()
    {
        yellow = new Song("Yellow", "Coldplay", "Parachutes", 266);
    }


    public void testSongConstructor()
    {
        assertEquals("Yellow", yellow.getTitle());
        assertEquals("Coldplay", yellow.getArtist());
        assertEquals("Parachutes", yellow.getAlbum());
        assertEquals(266, yellow.getLength());
    }


    public void testSongConstructorBlankTitle()
    {
        String[] titles = { null, "", "   " };
        for (String t : titles)
        {
            try
            {
                new Song(t, "Coldplay", "Parachutes", 266);
                fail("Expected IllegalArgumentException for title: " + t);
            }
            catch (IllegalArgumentException e)
            {
                assertEquals("Title must not be blank", e.getMessage());
            }
        }
    }


    public void testSongConstructorInvalidLength()
    {
        int[] lengths = { 0, -1 };
        for (int len : lengths)
        {
            try
            {
                new Song("Yellow", "Coldplay", "Parachutes", len);
                fail("Expected IllegalArgumentException for length " + len);
            }
            catch (IllegalArgumentException e)
            {
                assertEquals("Length must be greater than 0", e.getMessage());
            }
        }
    }


    public void testSongConstructorBlankArtist()
    {
        assertEquals("Unknown",
            new Song("Yellow", null, "Parachutes", 266).getArtist());
        assertEquals("Unknown",
            new Song("Yellow", "  ", "Parachutes", 266).getArtist());
        assertEquals("Unknown",
            new Song("Yellow", "Coldplay", null, 266).getAlbum());
        assertEquals("Unknown",
            new Song("Yellow", "Coldplay", "", 266).getAlbum());
    }


    public void testGetTitleSong()
    {
        assertEquals("Yellow", yellow.getTitle());
    }


    public void testGetLengthSong()
    {
        assertEquals(266, yellow.getLength());
    }


    public void testGetArtist()
    {
        assertEquals("Coldplay", yellow.getArtist());
    }


    public void testGetArtistUnknown()
    {
        assertEquals("Unknown",
            new Song("Yellow", null, "Parachutes", 266).getArtist());
    }


    public void testGetAlbum()
    {
        assertEquals("Parachutes", yellow.getAlbum());
    }


    public void testGetAlbumUnknown()
    {
        assertEquals("Unknown",
            new Song("Yellow", "Coldplay", "   ", 266).getAlbum());
    }


    public void testDescribeSong()
    {
        assertEquals("Yellow - Coldplay (Parachutes) 4:26",
            yellow.describe());
    }


    public void testDescribeSongShortLength()
    {
        Song s = new Song("Blip", "Artist", "Album", 5);
        assertEquals("Blip - Artist (Album) 0:05", s.describe());
    }


    public void testIsShuffleableSong()
    {
        assertTrue(yellow.isShuffleable());
    }


    public void testEqualsSong()
    {
        Song other = new Song("Yellow", "Coldplay", "Parachutes", 266);
        assertTrue(yellow.equals(other));
        assertTrue(yellow.equals(yellow));
    }


    public void testEqualsSongDifferentArtist()
    {
        Song other = new Song("Yellow", "Someone Else", "Parachutes", 266);
        assertFalse(yellow.equals(other));
    }


    public void testEqualsSongDifferentTitle()
    {
        Song other = new Song("Green", "Coldplay", "Parachutes", 266);
        assertFalse(yellow.equals(other));
    }


    public void testEqualsSongDifferentAlbumAndLength()
    {
        assertFalse(yellow.equals(
            new Song("Yellow", "Coldplay", "Other", 266)));
        assertFalse(yellow.equals(
            new Song("Yellow", "Coldplay", "Parachutes", 100)));
    }


    public void testEqualsSongNull()
    {
        assertFalse(yellow.equals(null));
    }


    public void testEqualsSongPodcast()
    {
        Podcast p = new Podcast("Yellow", "Coldplay", 1, 266);
        assertFalse(yellow.equals(p));
    }


    public void testToStringSong()
    {
        assertEquals(yellow.describe(), yellow.toString());
        assertEquals("Yellow - Coldplay (Parachutes) 4:26", yellow.toString());
    }


    public void testHashCodeSong()
    {
        Song other = new Song("Yellow", "Coldplay", "Parachutes", 266);
        assertEquals(yellow.hashCode(), other.hashCode());
    }
}