
import java.util.Scanner;
import javax.swing.*;

/**
 * The main class. The program is run from here: main creates the Playlist and
 * launches the GUI. The console helpers (readPositiveInt and readNonBlank)
 * re-prompt until the input is valid, so a console front end can use the same
 * rules as the GUI.
 *
 * @author
 * @version October 2026
 */
public class MusicPlayer
{
    private final Playlist playlist; // the main playlist
    private final Scanner scanner;   // reads the user's input

    /**
     * Creates a MusicPlayer around a playlist and an input source.
     *
     * @param playlist
     *            the playlist to manage
     * @param scanner
     *            where console input is read from
     * @throws IllegalArgumentException
     *             if either argument is null
     */
    public MusicPlayer(Playlist playlist, Scanner scanner)
    {
        if (playlist == null || scanner == null)
        {
            throw new IllegalArgumentException(
                "Playlist and scanner must not be null");
        }
        this.playlist = playlist;
        this.scanner = scanner;
    }


    /**
     * Gets the playlist this player manages.
     *
     * @return the playlist
     */
    public Playlist getPlaylist()
    {
        return playlist;
    }


    /**
     * Starts the program: creates an empty Playlist and opens the GUI. Any
     * arguments are ignored.
     *
     * @param args
     *            unused
     */
    public static void main(String[] args)
    {
        Playlist playlist = new Playlist();
        SwingUtilities.invokeLater(() -> {
            MusicPlayerGUI gui = new MusicPlayerGUI(playlist);
            gui.setVisible(true);
        });
    }


    /**
     * Re-prompts until the user types a whole number greater than 0.
     *
     * @param prompt
     *            the text shown before each attempt
     * @return the number typed
     */
    public int readPositiveInt(String prompt)
    {
        while (true)
        {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try
            {
                int value = Integer.parseInt(line);
                if (value > 0)
                {
                    return value;
                }
            }
            catch (NumberFormatException e)
            {
                // fall through to the message below
            }
            System.out.println("Length must be a whole number greater than 0");
        }
    }


    /**
     * Re-prompts until the input is not empty or only spaces.
     *
     * @param prompt
     *            the text shown before each attempt
     * @return the non-blank text typed, trimmed
     */
    public String readNonBlank(String prompt)
    {
        while (true)
        {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            if (!line.isEmpty())
            {
                return line;
            }
            System.out.println("Input must not be blank");
        }
    }
}
