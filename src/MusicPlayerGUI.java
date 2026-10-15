
import java.awt.*;
import java.util.Optional;
import javax.swing.*;

// -------------------------------------------------------------------------
/**
 * Write a one-sentence summary of your class here. Follow it with additional
 * details about its purpose, what abstraction it represents, and how to use it.
 * 
 * @author lquezadr
 * @version Oct 14, 2026
 */
public class MusicPlayerGUI
    extends JFrame
{
    // ~ Fields ................................................................
    private static final String SONG = "Song";
    private static final String PODCAST = "Podcast";
    private static final String NO_SELECTION = "Select an item first";
    private static final String NOTHING_PLAYING = "Nothing playing";

    private final Playlist playlist;                  // playlist being shown
    private final DefaultListModel<String> listModel; // rows in the list
    private final JList<String> songList;             // visible list
    private final JComboBox<String> typeBox;          // Song or Podcast
    private final JTextField titleField;
    private final JTextField artistOrHostField;
    private final JTextField albumField;              // songs only
    private final JTextField episodeField;            // podcasts only
    private final JTextField lengthField;             // seconds
    private final JButton addButton;
    private final JButton removeButton;
    private final JButton playButton;
    private final JButton nextButton;
    private final JButton previousButton;
    private final JButton shuffleButton;
    private final JToggleButton loopToggle;
    private final JLabel nowPlayingLabel;
    private final JLabel statusLabel;
    // ~ Constructors ..........................................................

    // ~Public Methods ........................................................

    /**
     * Builds the window and wires each control to its handler.
     *
     * @param playlist
     *            the playlist to show and control
     * @throws IllegalArgumentException
     *             if playlist is null
     */
    public MusicPlayerGUI(Playlist playlist)
    {
        super("Music Player");
        if (playlist == null)
        {
            throw new IllegalArgumentException("Playlist must not be null");
        }
        this.playlist = playlist;

        listModel = new DefaultListModel<String>();
        songList = new JList<String>(listModel);
        songList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        typeBox = new JComboBox<String>(new String[] { SONG, PODCAST });
        titleField = new JTextField(12);
        artistOrHostField = new JTextField(12);
        albumField = new JTextField(12);
        episodeField = new JTextField(12);
        lengthField = new JTextField(12);

        addButton = new JButton("Add");
        removeButton = new JButton("Remove");
        playButton = new JButton("Play");
        nextButton = new JButton("Next");
        previousButton = new JButton("Previous");
        shuffleButton = new JButton("Shuffle");
        loopToggle = new JToggleButton("Loop");

        nowPlayingLabel = new JLabel(NOTHING_PLAYING);
        statusLabel = new JLabel(" ");

        buildLayout();
        wireHandlers();
        updateFormForType();
        refreshList();
        updateNowPlaying();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
    }


    /**
     * Lays out the components in the window.
     */
    private void buildLayout()
    {
        setLayout(new BorderLayout(8, 8));

        nowPlayingLabel.setBorder(BorderFactory.createEmptyBorder(8, 8, 0, 8));
        add(nowPlayingLabel, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(songList);
        scroll.setPreferredSize(new java.awt.Dimension(480, 240));
        add(scroll, BorderLayout.CENTER);

        JPanel form = new JPanel(new GridLayout(0, 2, 4, 4));
        form.add(new JLabel("Type:"));
        form.add(typeBox);
        form.add(new JLabel("Title:"));
        form.add(titleField);
        form.add(new JLabel("Artist / Host:"));
        form.add(artistOrHostField);
        form.add(new JLabel("Album (songs):"));
        form.add(albumField);
        form.add(new JLabel("Episode # (podcasts):"));
        form.add(episodeField);
        form.add(new JLabel("Length (seconds):"));
        form.add(lengthField);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.CENTER));
        controls.add(previousButton);
        controls.add(playButton);
        controls.add(nextButton);
        controls.add(shuffleButton);
        controls.add(loopToggle);
        controls.add(addButton);
        controls.add(removeButton);

        JPanel south = new JPanel(new BorderLayout(4, 4));
        south.setBorder(BorderFactory.createEmptyBorder(0, 8, 8, 8));
        south.add(form, BorderLayout.NORTH);
        south.add(controls, BorderLayout.CENTER);
        south.add(statusLabel, BorderLayout.SOUTH);
        add(south, BorderLayout.SOUTH);
    }


    /**
     * Connects each control to its handler.
     */
    private void wireHandlers()
    {
        addButton.addActionListener(e -> handleAdd());
        removeButton.addActionListener(e -> handleRemove());
        playButton.addActionListener(e -> handlePlay());
        nextButton.addActionListener(e -> handleNext());
        previousButton.addActionListener(e -> handlePrevious());
        shuffleButton.addActionListener(e -> handleShuffle());
        loopToggle.addActionListener(e -> handleLoopToggle());
        typeBox.addActionListener(e -> updateFormForType());
    }


    /**
     * Enables only the fields that apply to the chosen type: album for songs,
     * episode number for podcasts.
     */
    private void updateFormForType()
    {
        boolean isSong = SONG.equals(typeBox.getSelectedItem());
        albumField.setEnabled(isSong);
        episodeField.setEnabled(!isSong);
    }


    /**
     * Clears the list and refills it with describe() for each item. Keeps the
     * current item highlighted when there is one.
     */
    void refreshList()
    {
        listModel.clear();
        for (Media item : playlist.getItems())
        {
            listModel.addElement(item.describe());
        }
        int current = playlist.getCurrentIndex();
        if (current >= 0 && current < listModel.size())
        {
            songList.setSelectedIndex(current);
        }
    }


    /**
     * Reads the form, builds a Song or Podcast, adds it, and refreshes. Bad
     * input shows a message in the status label and adds nothing.
     */
    void handleAdd()
    {
        try
        {
            boolean isSong = SONG.equals(typeBox.getSelectedItem());
            int length = parseWholeNumber(
                lengthField.getText(),
                "Length must be a whole number greater than 0");
            Media item;
            if (isSong)
            {
                item = new Song(
                    titleField.getText(),
                    artistOrHostField.getText(),
                    albumField.getText(),
                    length);
            }
            else
            {
                int episode = parseWholeNumber(
                    episodeField.getText(),
                    "Episode number must be a whole number greater than 0");
                item = new Podcast(
                    titleField.getText(),
                    artistOrHostField.getText(),
                    episode,
                    length);
            }
            playlist.add(item);
            refreshList();
            showStatus("Added " + item.getTitle());
        }
        catch (IllegalArgumentException e)
        {
            // Covers constructor rules and NumberFormatException
            // (a subclass of IllegalArgumentException).
            showStatus(e.getMessage());
        }
    }


    /**
     * Parses a whole number from text.
     *
     * @param text
     *            the raw text
     * @param message
     *            the message to use if the text is not a whole number
     * @return the parsed value
     * @throws IllegalArgumentException
     *             with the given message on bad text
     */
    private int parseWholeNumber(String text, String message)
    {
        try
        {
            return Integer.parseInt(text.trim());
        }
        catch (NumberFormatException e)
        {
            throw new IllegalArgumentException(message);
        }
    }


    /**
     * Removes the selected row (that exact copy, even with duplicates).
     */
    void handleRemove()
    {
        int index = songList.getSelectedIndex();
        if (index < 0)
        {
            showStatus(NO_SELECTION);
            return;
        }
        Media removed = playlist.removeAt(index);
        refreshList();
        updateNowPlaying();
        if (removed != null)
        {
            showStatus("Removed " + removed.getTitle());
        }
    }


    /**
     * Plays the selected row and updates the now-playing label.
     */
    void handlePlay()
    {
        int index = songList.getSelectedIndex();
        if (index < 0)
        {
            showStatus(NO_SELECTION);
            return;
        }
        playlist.play(index);
        updateNowPlaying();
        showStatus(" ");
    }


    /**
     * Moves to the next item and updates the label.
     */
    void handleNext()
    {
        Optional<Media> result = playlist.next();
        showMoveResult(result);
    }


    /**
     * Moves to the previous item and updates the label.
     */
    void handlePrevious()
    {
        Optional<Media> result = playlist.previous();
        showMoveResult(result);
    }


    /**
     * Shows the outcome of a next/previous move.
     *
     * @param result
     *            the item moved to, or empty if there was no move
     */
    private void showMoveResult(Optional<Media> result)
    {
        if (result.isPresent())
        {
            refreshList();
            updateNowPlaying();
            showStatus(" ");
        }
        else if (playlist.isEmpty())
        {
            showStatus("The playlist is empty");
        }
        else
        {
            showStatus("End of playlist");
        }
    }


    /**
     * Shuffles the playlist and refreshes the list.
     */
    void handleShuffle()
    {
        playlist.shuffle();
        songList.clearSelection();
        refreshList();
        updateNowPlaying();
    }


    /**
     * Passes the toggle state to the playlist's loop mode.
     */
    void handleLoopToggle()
    {
        playlist.setLoopMode(loopToggle.isSelected());
    }


    /**
     * Sets the now-playing label to the current item, or "Nothing playing".
     */
    void updateNowPlaying()
    {
        Optional<Media> current = playlist.getCurrent();
        if (current.isPresent())
        {
            nowPlayingLabel.setText(current.get().describe());
        }
        else
        {
            nowPlayingLabel.setText(NOTHING_PLAYING);
        }
    }


    /**
     * Sets the status label text. Null shows a blank label.
     *
     * @param message
     *            the text to show
     */
    void showStatus(String message)
    {
        statusLabel
            .setText(message == null || message.isEmpty() ? " " : message);
    }
}
