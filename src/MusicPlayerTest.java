import java.awt.Frame;
import java.util.NoSuchElementException;
import javax.swing.SwingUtilities;
import student.TestCase;

/**
 * Tests for MusicPlayer. The console helpers are tested by giving MusicPlayer
 * input through System.in, and by reading what it printed from systemOut().
 * NOTE: testMain and testMainUnexpectedArguments open the real window, so they
 * need a machine with a display (your own computer, not a headless server). The
 * tests close every window they open.
 */
public class MusicPlayerTest
    extends TestCase
{
    private static final String LENGTH_MESSAGE =
        "Length must be a whole number greater than 0";

    /**
     * Builds a MusicPlayer that reads from the given text.
     */
    private MusicPlayer playerReading(String input)
    {
        System.setIn(
            new java.io.ByteArrayInputStream(
                input.getBytes(java.nio.charset.StandardCharsets.UTF_8)));
        return new MusicPlayer();
    }


    /**
     * Counts how many times sub appears in text.
     */
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


    /**
     * Waits for main()'s invokeLater work, then finds the window it made.
     */
    private MusicPlayerGUI findLaunchedGui()
    {
        try
        {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run()
                {
                    // Empty: returns once earlier queued work has run.
                }
            });
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }

        for (Frame f : Frame.getFrames())
        {
            // Only accept a window that is still displayable.
            if (f instanceof MusicPlayerGUI && f.isDisplayable())
            {
                return (MusicPlayerGUI)f;
            }
        }

        return null;
    }


    public void tearDown()
    {
        // Close any window that main() opened.
        for (Frame f : Frame.getFrames())
        {
            if (f instanceof MusicPlayerGUI)
            {
                f.dispose();
            }
        }
    }

    // ---------------------------------------------------------------
    // MusicPlayer()
    // ---------------------------------------------------------------


    public void testConstructor()
    {
        assertNotNull(new MusicPlayer());
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

        window.dispose();
    }


    public void testMainUnexpectedArguments()
    {
        MusicPlayer.main(new String[] { "foo" });

        MusicPlayerGUI window = findLaunchedGui();

        assertNotNull(window);
        assertTrue(window.isVisible());

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
            // Expected: no more input to read.
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
    }


    public void testReadNonBlankPreservesSurroundingSpaces()
    {
        MusicPlayer player = playerReading("  Yellow  \n");

        assertEquals("  Yellow  ", player.readNonBlank("Title: "));
    }


    public void testReadNonBlankBlankInput()
    {
        MusicPlayer player = playerReading("\n   \nYellow\n");

        assertEquals("Yellow", player.readNonBlank("Title: "));

        String out = systemOut().getHistory();

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
            // Expected: no more input to read.
        }
    }
}
