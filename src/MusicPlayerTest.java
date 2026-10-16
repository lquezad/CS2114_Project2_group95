package project2;

import java.awt.Frame;
import javax.swing.SwingUtilities;
import student.TestCase;

/**
 * Tests for MusicPlayer: main and the console input helpers.
 *
 * Assumes MusicPlayer has a no-argument constructor that creates its Scanner
 * from System.in, and that readPositiveInt / readNonBlank are instance
 * methods (package-private or public) that print a message and re-prompt on
 * bad input.
 */
public class MusicPlayerTest extends TestCase
{
    private MusicPlayer player;


    /** Creates a MusicPlayer whose Scanner reads the given lines. */
    private MusicPlayer playerWithInput(String... lines)
    {
        setSystemIn(lines);
        return new MusicPlayer();
    }


    public void tearDown()
    {
        // close any window main() opened so test runs don't leave them behind
        for (Frame f : Frame.getFrames())
        {
            f.dispose();
        }
    }


    /** Waits for the Swing thread to finish, then finds the player window. */
    private Frame findPlayerWindow()
    {
        try
        {
            SwingUtilities.invokeAndWait(new Runnable()
            {
                public void run()
                {
                    // empty: just lets queued GUI work finish
                }
            });
        }
        catch (Exception e)
        {
            fail("Swing thread problem: " + e);
        }
        for (Frame f : Frame.getFrames())
        {
            if ("Music Player".equals(f.getTitle()) && f.isVisible())
            {
                return f;
            }
        }
        return null;
    }


    public void testMain()
    {
        MusicPlayer.main(new String[0]);
        assertNotNull("GUI window should open", findPlayerWindow());
    }


    public void testMainUnexpectedArguments()
    {
        MusicPlayer.main(new String[] { "foo" });
        assertNotNull("GUI window should open", findPlayerWindow());
    }


    public void testReadPositiveInt()
    {
        player = playerWithInput("240");
        assertEquals(240, player.readPositiveInt("Length: "));
    }


    public void testReadPositiveIntInvalidInput()
    {
        player = playerWithInput("abc", "0", "-5", "3.5", "42");
        assertEquals(42, player.readPositiveInt("Length: "));
        String out = systemOut().getHistory();
        assertTrue(out.contains("Length must be a whole number greater than 0"));
    }


    public void testReadNonBlank()
    {
        player = playerWithInput("Yellow");
        assertEquals("Yellow", player.readNonBlank("Title: "));
    }


    public void testReadNonBlankBlankInput()
    {
        player = playerWithInput("", "   ", "Yellow");
        assertEquals("Yellow", player.readNonBlank("Title: "));
    }
}
