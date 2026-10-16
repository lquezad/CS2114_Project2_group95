import java.awt.Frame;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;
import java.util.Scanner;
import javax.swing.SwingUtilities;
import student.TestCase;

/**
 * Tests for MusicPlayer.
 */
public class MusicPlayerTest extends TestCase
{
    private static final String LENGTH_MESSAGE =
        "Length must be a whole number greater than 0";
    private static final String BLANK_MESSAGE = "Input must not be blank";

    private Playlist playlist;


    public void setUp()
    {
        playlist = new Playlist();
    }


    public void tearDown()
    {
        // close any window that main() opened
        for (Frame f : Frame.getFrames())
        {
            if (f instanceof MusicPlayerGUI)
            {
                f.dispose();
            }
        }
    }


    /** Builds a MusicPlayer that reads from the given text. */
    private MusicPlayer playerReading(String input)
    {
        return new MusicPlayer(playlist, new Scanner(input));
    }


    /** Counts how many times sub appears in text. */
    private int count(String text, String sub)
    {
        int total = 0;
        int at = text.indexOf(sub);
        while (at >= 0)
        {
            total++;
            at = text.indexOf(sub, at + sub.length());
        }
        return total;
    }


    /** Waits for main()'s invokeLater work, then finds the window it made. */
    private MusicPlayerGUI findLaunchedGui()
    {
        try
        {
            SwingUtilities.invokeAndWait(new Runnable()
            {
                public void run()
                {
                    // empty: returns once earlier queued work has run
                }
            });
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        for (Frame f : Frame.getFrames())
        {
            // Frame.getFrames() still lists windows from earlier tests that
            // were disposed, so only accept one that is still displayable.
            if (f instanceof MusicPlayerGUI && f.isDisplayable())
            {
                return (MusicPlayerGUI)f;
            }
        }
        return null;
    }


    /** Reads the playlist the window is showing. */
    private Playlist playlistOf(MusicPlayerGUI window)
    {
        try
        {
            Field f = MusicPlayerGUI.class.getDeclaredField("playlist");
            f.setAccessible(true);
            return (Playlist)f.get(window);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Cannot read field playlist", e);
        }
    }


    // ---------------------------------------------------------------
    // MusicPlayer(Playlist, Scanner) and getPlaylist()
    // ---------------------------------------------------------------

    public void testConstructor()
    {
        MusicPlayer player = playerReading("");
        assertSame(playlist, player.getPlaylist());
    }


    public void testConstructorNullPlaylist()
    {
        try
        {
            new MusicPlayer(null, new Scanner(""));
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            // expected
        }
    }


    public void testConstructorNullScanner()
    {
        try
        {
            new MusicPlayer(playlist, null);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            // expected
        }
    }


    // ---------------------------------------------------------------
    // main(String[])
    // ---------------------------------------------------------------

    public void testMain()
    {
        MusicPlayer.main(new String[0]);
        MusicPlayerGUI window = findLaunchedGui();
        assertNotNull(window);
        assertTrue(window.isVisible());
        assertEquals(0, playlistOf(window).size());
        window.dispose();
    }


    public void testMainUnexpectedArguments()
    {
        MusicPlayer.main(new String[] { "foo" });
        MusicPlayerGUI window = findLaunchedGui();
        assertNotNull(window);
        assertTrue(window.isVisible());
        assertEquals(0, playlistOf(window).size());
        window.dispose();
    }


    // ---------------------------------------------------------------
    // readPositiveInt(String)
    // ---------------------------------------------------------------

    public void testReadPositiveInt()
    {
        MusicPlayer player = playerReading("240\n");
        assertEquals(240, player.readPositiveInt("Length: "));
        assertTrue(systemOut().getHistory().contains("Length: "));
        assertFalse(systemOut().getHistory().contains(LENGTH_MESSAGE));
    }


    public void testReadPositiveIntIgnoresSurroundingSpaces()
    {
        MusicPlayer player = playerReading("  15  \n");
        assertEquals(15, player.readPositiveInt("Length: "));
    }


    public void testReadPositiveIntInvalidInput()
    {
        MusicPlayer player = playerReading("abc\n0\n-5\n3.5\n240\n");
        assertEquals(240, player.readPositiveInt("Length: "));
        String out = systemOut().getHistory();
        assertEquals(4, count(out, LENGTH_MESSAGE));
        assertEquals(5, count(out, "Length: "));
    }


    public void testReadPositiveIntEmptyLine()
    {
        MusicPlayer player = playerReading("\n7\n");
        assertEquals(7, player.readPositiveInt("Length: "));
        assertEquals(1, count(systemOut().getHistory(), LENGTH_MESSAGE));
    }


    public void testReadPositiveIntInputRunsOut()
    {
        MusicPlayer player = playerReading("abc\n");
        try
        {
            player.readPositiveInt("Length: ");
            fail("Expected NoSuchElementException");
        }
        catch (NoSuchElementException e)
        {
            // expected: no more input to read
        }
    }


    // ---------------------------------------------------------------
    // readNonBlank(String)
    // ---------------------------------------------------------------

    public void testReadNonBlank()
    {
        MusicPlayer player = playerReading("Yellow\n");
        assertEquals("Yellow", player.readNonBlank("Title: "));
        assertTrue(systemOut().getHistory().contains("Title: "));
        assertFalse(systemOut().getHistory().contains(BLANK_MESSAGE));
    }


    public void testReadNonBlankTrimsSpaces()
    {
        MusicPlayer player = playerReading("  Yellow  \n");
        assertEquals("Yellow", player.readNonBlank("Title: "));
    }


    public void testReadNonBlankBlankInput()
    {
        MusicPlayer player = playerReading("\n   \nYellow\n");
        assertEquals("Yellow", player.readNonBlank("Title: "));
        String out = systemOut().getHistory();
        assertEquals(2, count(out, BLANK_MESSAGE));
        assertEquals(3, count(out, "Title: "));
    }


    public void testReadNonBlankInputRunsOut()
    {
        MusicPlayer player = playerReading("\n");
        try
        {
            player.readNonBlank("Title: ");
            fail("Expected NoSuchElementException");
        }
        catch (NoSuchElementException e)
        {
            // expected: no more input to read
        }
    }
}