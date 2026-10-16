import java.util.Scanner;
import javax.swing.SwingUtilities;
 
// -------------------------------------------------------------------------
/**
 * The main class of the music player. It starts the program by creating the
 * playlist and opening the Swing window, and it holds the console helpers
 * that keep asking until the user types usable input.
 *
 * @author Team 95
 * @version Oct 15, 2026
 */
public class MusicPlayer
{
    private static final String BAD_NUMBER =
        "Length must be a whole number greater than 0";
 
    private final Playlist playlist; // the main playlist used by the program
    private final Scanner scanner;   // reads the user's console input
 
    /**
     * Creates a music player with an empty playlist that reads console input
     * from System.in.
     */
    public MusicPlayer()
    {
        playlist = new Playlist();
        scanner = new Scanner(System.in);
    }
 
 
    /**
     * Creates the playlist and launches the window. Any arguments are ignored.
     *
     * @param args
     *            unused command-line arguments
     */
    public static void main(String[] args)
    {
        final MusicPlayer player = new MusicPlayer();
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                new MusicPlayerGUI(player.playlist).setVisible(true);
            }
        });
    }
 
 
    /**
     * Shows a prompt and asks again until the user types a whole number
     * greater than 0.
     *
     * @param prompt
     *            the text shown before each attempt
     * @return the number the user typed
     */
    public int readPositiveInt(String prompt)
    {
        while (true)
        {
            System.out.print(prompt);
            String line = scanner.nextLine();
            try
            {
                int value = Integer.parseInt(line.trim());
                if (value > 0)
                {
                    return value;
                }
            }
            catch (NumberFormatException e)
            {
                // not a whole number: fall through to the message below
            }
            System.out.println(BAD_NUMBER);
        }
    }
 
 
    /**
     * Shows a prompt and asks again until the user types something other than
     * an empty line or only spaces.
     *
     * @param prompt
     *            the text shown before each attempt
     * @return the text the user typed
     */
    public String readNonBlank(String prompt)
    {
        while (true)
        {
            System.out.print(prompt);
            String line = scanner.nextLine();
            if (!line.trim().isEmpty())
            {
                return line;
            }
        }
    }
}
