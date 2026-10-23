import student.TestCase;

/**
 * Tests for Album.
 */
public class AlbumTest
    extends TestCase
{
    private Album album;
    private Song yellow;
    private Song trouble;
    private Song shiver;

    public void setUp()
    {
        album = new Album("Parachutes", "Coldplay");
        yellow = new Song("Yellow", "Coldplay", "Parachutes", 266);
        trouble = new Song("Trouble", "Coldplay", "Parachutes", 270);
        shiver = new Song("Shiver", "Coldplay", "Parachutes", 299);
    }


    private void fillAll()
    {
        album.add(yellow);
        album.add(trouble);
        album.add(shiver);
    }


    /**
     * Checks that a new album has its title and artist and starts empty.
     */
    public void testAlbumConstructor()
    {
        assertEquals("Parachutes", album.getTitle());
        assertEquals("Coldplay", album.getArtist());
        assertEquals(0, album.size());
        assertTrue(album.isEmpty());
    }


    /**
     * Checks that extra spaces around the title and artist are removed.
     */
    public void testAlbumConstructorTrims()
    {
        Album a = new Album("  Parachutes  ", "  Coldplay ");
        assertEquals("Parachutes", a.getTitle());
        assertEquals("Coldplay", a.getArtist());
    }


    /**
     * Checks that a null, empty, or blank title throws an
     * IllegalArgumentException.
     */
    public void testAlbumConstructorBlankTitle()
    {
        String[] titles = { null, "", "   " };
        for (String t : titles)
        {
            try
            {
                new Album(t, "Coldplay");
                fail("Expected IllegalArgumentException for title: " + t);
            }
            catch (IllegalArgumentException e)
            {
                assertEquals("Title must not be blank", e.getMessage());
            }
        }
    }


    /**
     * Checks that a null or blank artist becomes "Unknown".
     */
    public void testAlbumConstructorBlankArtist()
    {
        assertEquals("Unknown", new Album("Parachutes", null).getArtist());
        assertEquals("Unknown", new Album("Parachutes", "  ").getArtist());
    }


    /**
     * Checks that songs are added in order at the end of the album.
     */
    public void testAdd()
    {
        album.add(yellow);
        album.add(trouble);
        assertEquals(2, album.size());
        assertFalse(album.isEmpty());
        assertEquals(yellow, album.getSongs()[0]);
        assertEquals(trouble, album.getSongs()[1]);
    }


    /**
     * Checks that the same song can be added more than once.
     */
    public void testAddDuplicate()
    {
        album.add(yellow);
        album.add(yellow);
        assertEquals(2, album.size());
    }


    /**
     * Checks that adding null throws an IllegalArgumentException and leaves the
     * album unchanged.
     */
    public void testAddNull()
    {
        album.add(yellow);
        try
        {
            album.add(null);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals(1, album.size());
        }
    }


    /**
     * Checks that removeAt returns the song at that position and closes the
     * gap.
     */
    public void testRemoveAt()
    {
        fillAll();
        assertEquals(trouble, album.removeAt(1));
        assertEquals(2, album.size());
        assertEquals(yellow, album.getSongs()[0]);
        assertEquals(shiver, album.getSongs()[1]);
    }


    /**
     * Checks that removeAt returns null and changes nothing for an index out of
     * range.
     */
    public void testRemoveAtInvalidIndex()
    {
        fillAll();
        assertNull(album.removeAt(-1));
        assertNull(album.removeAt(3));
        assertEquals(3, album.size());
        assertNull(new Album("Empty", "x").removeAt(0));
    }


    /**
     * Checks that remove takes out only the first copy of a duplicated song.
     */
    public void testRemove()
    {
        album.add(yellow);
        album.add(trouble);
        album.add(yellow);
        assertTrue(album.remove(yellow));
        assertEquals(2, album.size());
        assertEquals(trouble, album.getSongs()[0]);
        assertEquals(yellow, album.getSongs()[1]);
    }


    /**
     * Checks that removing a song that is not in the album returns false and
     * changes nothing.
     */
    public void testRemoveNotFound()
    {
        fillAll();
        assertFalse(album.remove(new Song("Z", "z", "z", 5)));
        assertEquals(3, album.size());
    }


    /**
     * Checks that removing null returns false and changes nothing.
     */
    public void testRemoveNull()
    {
        fillAll();
        assertFalse(album.remove(null));
        assertEquals(3, album.size());
    }


    /**
     * Checks that removing from an empty album returns false.
     */
    public void testRemoveEmptyAlbum()
    {
        assertFalse(album.remove(yellow));
    }


    /**
     * Checks that get returns the song at the given position.
     */
    public void testGet()
    {
        fillAll();
        assertEquals(shiver, album.get(2));
    }


    /**
     * Checks that get returns null for an index out of range.
     */
    public void testGetInvalidIndex()
    {
        fillAll();
        assertNull(album.get(-1));
        assertNull(album.get(3));
        assertNull(new Album("Empty", "x").get(0));
    }


    /**
     * Checks that contains finds a song, including an equal song that is a
     * different object.
     */
    public void testContains()
    {
        fillAll();
        assertTrue(album.contains(trouble));
        assertTrue(
            album.contains(new Song("Trouble", "Coldplay", "Parachutes", 270)));
    }


    /**
     * Checks that contains is false for a missing song, null, or an empty
     * album.
     */
    public void testContainsNotFoundOrNull()
    {
        fillAll();
        assertFalse(album.contains(new Song("Z", "z", "z", 5)));
        assertFalse(album.contains(null));
        assertFalse(new Album("Empty", "x").contains(yellow));
    }


    /**
     * Checks that indexOf returns the position of a song.
     */
    public void testIndexOf()
    {
        fillAll();
        assertEquals(2, album.indexOf(shiver));
    }


    /**
     * Checks that indexOf returns -1 for a missing song or null.
     */
    public void testIndexOfNotFoundOrNull()
    {
        fillAll();
        assertEquals(-1, album.indexOf(new Song("Z", "z", "z", 5)));
        assertEquals(-1, album.indexOf(null));
    }


    /**
     * Checks that changing the array from getSongs does not change the album.
     */
    public void testGetSongsIsACopy()
    {
        fillAll();
        Song[] songs = album.getSongs();
        songs[0] = shiver;
        assertEquals(yellow, album.getSongs()[0]);
    }


    // ----------------------------------------------------------
    /**
     * Checks that getSongs returns an empty array for an empty album.
     */
    public void testGetSongsEmptyAlbum()
    {
        assertEquals(0, album.getSongs().length);
    }


    // ----------------------------------------------------------
    /**
     * Checks that size and isEmpty stay correct as songs are added and removed.
     */
    public void testSizeAndIsEmpty()
    {
        assertTrue(album.isEmpty());
        fillAll();
        assertEquals(3, album.size());
        assertFalse(album.isEmpty());
        album.removeAt(0);
        album.removeAt(0);
        album.removeAt(0);
        assertTrue(album.isEmpty());
    }


    // ----------------------------------------------------------
    /**
     * Checks that the total length is the sum of every song's length.
     */
    public void testGetTotalLength()
    {
        fillAll();
        assertEquals(266 + 270 + 299, album.getTotalLength());
    }


    // ----------------------------------------------------------
    /**
     * Checks that an empty album has a total length of 0.
     */
    public void testGetTotalLengthEmptyAlbum()
    {
        assertEquals(0, album.getTotalLength());
    }


    // ----------------------------------------------------------
    /**
     * Checks that the album's songs are added in order to the end of a playlist
     * and stay in the album.
     */
    public void testAddToPlaylist()
    {
        fillAll();
        Playlist p = new Playlist();
        Song other = new Song("Other", "x", "y", 10);
        p.add(other);
        album.addToPlaylist(p);
        assertEquals(4, p.size());
        assertEquals(other, p.getItems()[0]);
        assertEquals(yellow, p.getItems()[1]);
        assertEquals(trouble, p.getItems()[2]);
        assertEquals(shiver, p.getItems()[3]);
        assertEquals(3, album.size());
    }


    // ----------------------------------------------------------
    /**
     * Checks that adding an empty album leaves the playlist empty.
     */
    public void testAddToPlaylistEmptyAlbum()
    {
        Playlist p = new Playlist();
        album.addToPlaylist(p);
        assertTrue(p.isEmpty());
    }


    // ----------------------------------------------------------
    /**
     * Checks that a null playlist throws an IllegalArgumentException.
     */
    public void testAddToPlaylistNull()
    {
        fillAll();
        try
        {
            album.addToPlaylist(null);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals("Playlist must not be null", e.getMessage());
        }
    }


    // ----------------------------------------------------------
    /**
     * Checks that describe shows the title, artist, plural song count, and
     * total length.
     */
    public void testDescribe()
    {
        album.add(yellow);
        album.add(trouble);
        assertEquals("Parachutes - Coldplay (2 songs) 8:56", album.describe());
    }


    // ----------------------------------------------------------
    /**
     * Checks that describe says "1 song" for a one-song album.
     */
    public void testDescribeOneSong()
    {
        album.add(yellow);
        assertEquals("Parachutes - Coldplay (1 song) 4:26", album.describe());
    }


    // ----------------------------------------------------------
    /**
     * Checks that describe shows "0 songs" and a length of 0:00 for an empty
     * album.
     */
    public void testDescribeEmptyAlbum()
    {
        assertEquals("Parachutes - Coldplay (0 songs) 0:00", album.describe());
    }


    // ----------------------------------------------------------
    /**
     * Checks that toString returns the same text as describe.
     */
    public void testToString()
    {
        album.add(yellow);
        assertEquals(album.describe(), album.toString());
    }
}
