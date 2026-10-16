import java.util.Optional;
import java.util.Random;
import student.TestCase;

/**
 * Tests for Playlist.
 */
public class PlaylistTest extends TestCase
{
    private Playlist playlist;
    private Song a;
    private Song b;
    private Song c;


    public void setUp()
    {
        playlist = new Playlist();
        a = new Song("A", "ArtistA", "AlbumA", 100);
        b = new Song("B", "ArtistB", "AlbumB", 200);
        c = new Song("C", "ArtistC", "AlbumC", 300);
    }


    private void fillABC()
    {
        playlist.add(a);
        playlist.add(b);
        playlist.add(c);
    }


    public void testPlaylistConstructor()
    {
        assertTrue(playlist.isEmpty());
        assertEquals(0, playlist.size());
        assertEquals(-1, playlist.getCurrentIndex());
        assertFalse(playlist.isLoopMode());
    }


    public void testPlaylistConstructorGetCurrent()
    {
        assertFalse(playlist.getCurrent().isPresent());
    }


    public void testPlaylistRandomConstructor()
    {
        Playlist p1 = new Playlist(new Random(42));
        Playlist p2 = new Playlist(new Random(42));
        assertTrue(p1.isEmpty());
        for (Playlist p : new Playlist[] { p1, p2 })
        {
            for (int i = 1; i <= 6; i++)
            {
                p.add(new Song("S" + i, "x", "y", 10 * i));
            }
            p.shuffle();
        }
        Media[] items1 = p1.getItems();
        Media[] items2 = p2.getItems();
        assertEquals(items1.length, items2.length);
        for (int i = 0; i < items1.length; i++)
        {
            assertEquals(items1[i], items2[i]);
        }
    }


    public void testPlaylistRandomConstructorNull()
    {
        try
        {
            new Playlist(null);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            // expected
        }
    }


    public void testAdd()
    {
        playlist.add(a);
        playlist.add(b);
        assertEquals(2, playlist.size());
        assertEquals(b, playlist.getItems()[1]);
    }


    public void testAddDuplicate()
    {
        playlist.add(a);
        playlist.add(a);
        assertEquals(2, playlist.size());
    }


    public void testAddNull()
    {
        try
        {
            playlist.add(null);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals(0, playlist.size());
        }
    }


    public void testRemoveAt()
    {
        fillABC();
        playlist.play(2);
        assertEquals(a, playlist.removeAt(0));
        assertEquals(2, playlist.size());
        assertEquals(b, playlist.getItems()[0]);
        assertEquals(c, playlist.getItems()[1]);
        assertEquals(1, playlist.getCurrentIndex());
    }


    public void testRemoveAtCurrentItem()
    {
        fillABC();
        playlist.play(1);
        assertEquals(b, playlist.removeAt(1));
        assertEquals(1, playlist.getCurrentIndex());
        assertEquals(c, playlist.getCurrent().get());
    }


    public void testRemoveAtCurrentLastItem()
    {
        fillABC();
        playlist.play(2);
        playlist.removeAt(2);
        assertEquals(1, playlist.getCurrentIndex());
        assertEquals(b, playlist.getCurrent().get());
    }


    public void testRemoveAtAfterCurrent()
    {
        fillABC();
        playlist.play(0);
        playlist.removeAt(2);
        assertEquals(0, playlist.getCurrentIndex());
    }


    public void testRemoveAtOnlyItem()
    {
        playlist.add(a);
        playlist.play(0);
        playlist.removeAt(0);
        assertEquals(-1, playlist.getCurrentIndex());
        assertTrue(playlist.isEmpty());
    }


    public void testRemoveAtInvalidIndex()
    {
        fillABC();
        assertNull(playlist.removeAt(-1));
        assertNull(playlist.removeAt(3));
        assertEquals(3, playlist.size());
        Playlist empty = new Playlist();
        assertNull(empty.removeAt(0));
        assertEquals(0, empty.size());
    }


    public void testRemove()
    {
        playlist.add(a);
        playlist.add(b);
        playlist.add(a);
        assertTrue(playlist.remove(a));
        assertEquals(2, playlist.size());
        assertEquals(b, playlist.getItems()[0]);
        assertEquals(a, playlist.getItems()[1]);
    }


    public void testRemoveNotFound()
    {
        fillABC();
        Song z = new Song("Z", "z", "z", 5);
        assertFalse(playlist.remove(z));
        assertEquals(3, playlist.size());
    }


    public void testRemoveNull()
    {
        fillABC();
        assertFalse(playlist.remove(null));
        assertEquals(3, playlist.size());
    }


    public void testRemoveEmptyPlaylist()
    {
        assertFalse(playlist.remove(a));
    }


    public void testNext()
    {
        fillABC();
        playlist.play(0);
        Optional<Media> result = playlist.next();
        assertTrue(result.isPresent());
        assertEquals(b, result.get());
        assertEquals(1, playlist.getCurrentIndex());
    }


    public void testNextNothingSelected()
    {
        fillABC();
        Optional<Media> result = playlist.next();
        assertEquals(a, result.get());
        assertEquals(0, playlist.getCurrentIndex());
    }


    public void testNextEmptyPlaylist()
    {
        assertFalse(playlist.next().isPresent());
    }


    public void testNextEndOfPlaylistLoopOff()
    {
        fillABC();
        playlist.play(2);
        assertFalse(playlist.next().isPresent());
        assertEquals(2, playlist.getCurrentIndex());
    }


    public void testNextEndOfPlaylistLoopOn()
    {
        fillABC();
        playlist.setLoopMode(true);
        playlist.play(2);
        assertEquals(a, playlist.next().get());
        assertEquals(0, playlist.getCurrentIndex());
    }


    public void testPrevious()
    {
        fillABC();
        playlist.play(2);
        assertEquals(b, playlist.previous().get());
        assertEquals(1, playlist.getCurrentIndex());
    }


    public void testPreviousEmptyPlaylist()
    {
        assertFalse(playlist.previous().isPresent());
    }


    public void testPreviousNothingSelected()
    {
        fillABC();
        assertFalse(playlist.previous().isPresent());
        assertEquals(-1, playlist.getCurrentIndex());
    }


    public void testPreviousStartOfPlaylistLoopOff()
    {
        fillABC();
        playlist.play(0);
        assertFalse(playlist.previous().isPresent());
        assertEquals(0, playlist.getCurrentIndex());
    }


    public void testPreviousStartOfPlaylistLoopOn()
    {
        fillABC();
        playlist.setLoopMode(true);
        playlist.play(0);
        assertEquals(c, playlist.previous().get());
        assertEquals(2, playlist.getCurrentIndex());
    }


    public void testGetCurrent()
    {
        fillABC();
        playlist.play(1);
        assertEquals(b, playlist.getCurrent().get());
    }


    public void testGetCurrentNothingSelected()
    {
        fillABC();
        assertFalse(playlist.getCurrent().isPresent());
    }


    public void testGetCurrentEmptyPlaylist()
    {
        assertFalse(playlist.getCurrent().isPresent());
    }


    public void testPlay()
    {
        fillABC();
        assertEquals(c, playlist.play(2).get());
        assertEquals(2, playlist.getCurrentIndex());
    }


    public void testPlayInvalidIndex()
    {
        fillABC();
        playlist.play(1);
        assertFalse(playlist.play(-1).isPresent());
        assertFalse(playlist.play(3).isPresent());
        assertEquals(1, playlist.getCurrentIndex());
        Playlist empty = new Playlist();
        assertFalse(empty.play(0).isPresent());
        assertEquals(-1, empty.getCurrentIndex());
    }


    public void testSkipTo()
    {
        Song yellow = new Song("Yellow", "Coldplay", "Parachutes", 266);
        playlist.add(a);
        playlist.add(yellow);
        Optional<Media> result = playlist.skipTo("yellow");
        assertEquals(yellow, result.get());
        assertEquals(1, playlist.getCurrentIndex());
    }


    public void testSkipToNoMatch()
    {
        fillABC();
        playlist.play(1);
        assertFalse(playlist.skipTo("nothing").isPresent());
        assertEquals(1, playlist.getCurrentIndex());
    }


    public void testSkipToNull()
    {
        fillABC();
        playlist.play(1);
        assertFalse(playlist.skipTo(null).isPresent());
        assertEquals(1, playlist.getCurrentIndex());
    }


    public void testSkipToEmptyPlaylist()
    {
        assertFalse(playlist.skipTo("A").isPresent());
    }


    public void testSetLoopMode()
    {
        fillABC();
        playlist.setLoopMode(true);
        playlist.play(2);
        assertEquals(a, playlist.next().get());
    }


    public void testSetLoopModeOff()
    {
        fillABC();
        playlist.setLoopMode(true);
        playlist.setLoopMode(false);
        playlist.play(2);
        assertFalse(playlist.next().isPresent());
    }


    public void testIsLoopMode()
    {
        playlist.setLoopMode(true);
        assertTrue(playlist.isLoopMode());
    }


    public void testIsLoopModeDefault()
    {
        assertFalse(new Playlist().isLoopMode());
    }


    public void testShuffle()
    {
        Playlist p = new Playlist(new Random(42));
        Song s1 = new Song("S1", "x", "y", 10);
        Podcast pod = new Podcast("Pod", "Host", 1, 600);
        Song s2 = new Song("S2", "x", "y", 20);
        Song s3 = new Song("S3", "x", "y", 30);
        p.add(s1);
        p.add(pod);
        p.add(s2);
        p.add(s3);
        p.play(2);

        p.shuffle();

        Media[] items = p.getItems();
        assertEquals(4, items.length);
        assertEquals(pod, items[1]);
        assertEquals(-1, p.getCurrentIndex());
        // same songs still present at positions 0, 2, 3
        Media[] songs = { items[0], items[2], items[3] };
        for (Song s : new Song[] { s1, s2, s3 })
        {
            boolean found = false;
            for (Media m : songs)
            {
                if (m.equals(s))
                {
                    found = true;
                }
            }
            assertTrue("missing " + s.getTitle(), found);
        }
    }


    public void testShuffleNextPlaysFirst()
    {
        fillABC();
        playlist.play(2);
        playlist.shuffle();
        assertEquals(-1, playlist.getCurrentIndex());
        assertEquals(playlist.getItems()[0], playlist.next().get());
    }


    public void testShuffleEmptyPlaylist()
    {
        playlist.shuffle();
        assertEquals(0, playlist.size());
        assertEquals(-1, playlist.getCurrentIndex());
    }


    public void testShuffleOneItem()
    {
        playlist.add(a);
        playlist.play(0);
        playlist.shuffle();
        assertEquals(1, playlist.size());
        assertEquals(a, playlist.getItems()[0]);
        assertEquals(0, playlist.getCurrentIndex());
    }


    public void testGetItems()
    {
        fillABC();
        Media[] items = playlist.getItems();
        assertEquals(3, items.length);
        assertEquals(a, items[0]);
        assertEquals(b, items[1]);
        assertEquals(c, items[2]);
    }


    public void testGetItemsEmptyPlaylist()
    {
        assertEquals(0, playlist.getItems().length);
    }


    public void testGetCurrentIndex()
    {
        fillABC();
        playlist.play(2);
        assertEquals(2, playlist.getCurrentIndex());
    }


    public void testGetCurrentIndexDefault()
    {
        assertEquals(-1, playlist.getCurrentIndex());
    }


    public void testSize()
    {
        fillABC();
        assertEquals(3, playlist.size());
    }


    public void testSizeEmptyPlaylist()
    {
        assertEquals(0, playlist.size());
    }


    public void testIsEmpty()
    {
        assertTrue(playlist.isEmpty());
    }


    public void testIsEmptyNonEmptyPlaylist()
    {
        playlist.add(a);
        assertFalse(playlist.isEmpty());
    }
}
