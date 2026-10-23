import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Optional;
import javax.swing.*;
import javax.swing.plaf.basic.*;

/**
 * Swing window for the music player, laid out like a streaming app: a sidebar
 * with the add form on the left, the track list in the middle, and a player bar
 * along the bottom (now playing on the left, transport controls in the center,
 * status messages on the right).
 *
 * @author Leo Quezada-Rocha
 * @version October 2026
 */
public class MusicPlayerGUI
    extends JFrame
{
    // ---- colors and fonts ----
    private static final Color BG = new Color(38, 41, 47);
    private static final Color SIDEBAR = new Color(27, 29, 33);
    private static final Color ELEVATED = new Color(58, 62, 70);
    private static final Color DISABLED_FIELD = new Color(44, 47, 53);
    private static final Color ROW_SELECTED = new Color(62, 80, 112);
    private static final Color TEXT = Color.WHITE;
    private static final Color SUBTEXT = new Color(165, 171, 182);
    private static final Color ACCENT = new Color(74, 144, 255);
    private static final Color DANGER = new Color(235, 100, 100);
    private static final Font BASE = new Font("SansSerif", Font.PLAIN, 13);
    private static final Font BOLD = new Font("SansSerif", Font.BOLD, 13);

    private static final String SONG = "Song";
    private static final String PODCAST = "Podcast";
    private static final String NO_SELECTION = "Select an item first";
    private static final String NOTHING_PLAYING = "Nothing playing";
    private static final String UNKNOWN_ALBUM = "Unknown";

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
    private final JButton addAlbumButton;
    private final JButton removeAlbumButton;
    private final JButton playButton;
    private final JButton nextButton;
    private final JButton previousButton;
    private final JButton shuffleButton;
    private final JToggleButton loopToggle;
    private final JLabel nowPlayingLabel;
    private final JLabel statusLabel;
    private final JLabel countLabel;                  // "3 items" header
    private Media[] shownItems = new Media[0];        // items behind rows

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

        typeBox = new JComboBox<String>(new String[] { SONG, PODCAST });
        titleField = new JTextField();
        artistOrHostField = new JTextField();
        albumField = new JTextField();
        episodeField = new JTextField();
        lengthField = new JTextField();

        addButton =
            new PillButton("Add to playlist", ACCENT, Color.WHITE, null);
        removeButton = new PillButton("Remove selected", null, DANGER, DANGER);
        addAlbumButton = new PillButton("Add album", null, TEXT, SUBTEXT);
        removeAlbumButton =
            new PillButton("Delete album", null, DANGER, DANGER);
        shuffleButton = new PillButton("Shuffle", null, TEXT, SUBTEXT);
        loopToggle = new PillToggle("Loop");
        playButton = new TransportButton(TransportButton.PLAY, 46);
        nextButton = new TransportButton(TransportButton.NEXT1, 34);
        previousButton = new TransportButton(TransportButton.PREVIOUS1, 34);

        nowPlayingLabel = new JLabel(NOTHING_PLAYING);
        statusLabel = new JLabel(" ");
        countLabel = new JLabel("0 items");

        buildLayout();
        wireHandlers();
        updateFormForType();
        refreshList();
        updateNowPlaying();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(860, 520));
        setSize(1000, 640);
        setLocationRelativeTo(null);
    }

    // =====================================================================
    // Layout
    // =====================================================================


    /**
     * Lays out the sidebar, track list, and player bar.
     */
    private void buildLayout()
    {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);
        setContentPane(root);
        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(buildMainPanel(), BorderLayout.CENTER);
        root.add(buildPlayerBar(), BorderLayout.SOUTH);
    }


    /**
     * Builds the left sidebar holding the add form.
     *
     * @return the sidebar panel
     */
    private JPanel buildSidebar()
    {
        JPanel side = new JPanel();
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBackground(SIDEBAR);
        side.setBorder(BorderFactory.createEmptyBorder(24, 20, 20, 20));
        side.setPreferredSize(new Dimension(270, 0));

        JLabel logo = new JLabel("Music Player");
        logo.setFont(new Font("SansSerif", Font.BOLD, 22));
        logo.setForeground(ACCENT);
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);
        side.add(logo);
        side.add(Box.createVerticalStrut(24));

        JLabel section = new JLabel("ADD TO PLAYLIST");
        section.setFont(new Font("SansSerif", Font.BOLD, 11));
        section.setForeground(SUBTEXT);
        section.setAlignmentX(Component.LEFT_ALIGNMENT);
        side.add(section);
        side.add(Box.createVerticalStrut(12));

        styleCombo(typeBox);
        for (JTextField f : new JTextField[] { titleField, artistOrHostField,
            albumField, episodeField, lengthField })
        {
            styleField(f);
        }

        addRow(side, "Type", typeBox);
        addRow(side, "Title", titleField);
        addRow(side, "Artist / Host", artistOrHostField);
        addRow(side, "Album (songs)", albumField);
        addRow(side, "Episode # (podcasts)", episodeField);
        addRow(side, "Length (seconds)", lengthField);

        addButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        addButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        side.add(Box.createVerticalStrut(4));
        side.add(addButton);
        side.add(Box.createVerticalStrut(10));
        removeButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        removeButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        side.add(removeButton);
        side.add(Box.createVerticalGlue());
        return side;
    }


    /**
     * Adds a small caption and a component to a vertical panel.
     *
     * @param panel
     *            the panel to add to
     * @param caption
     *            the text above the component
     * @param comp
     *            the input component
     */
    private void addRow(JPanel panel, String caption, JComponent comp)
    {
        JLabel label = new JLabel(caption);
        label.setFont(new Font("SansSerif", Font.PLAIN, 12));
        label.setForeground(SUBTEXT);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        comp.setAlignmentX(Component.LEFT_ALIGNMENT);
        comp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        comp.setPreferredSize(new Dimension(230, 34));
        panel.add(label);
        panel.add(Box.createVerticalStrut(4));
        panel.add(comp);
        panel.add(Box.createVerticalStrut(12));
    }


    /**
     * Builds the center panel: a header and the track list.
     *
     * @return the main panel
     */
    private JPanel buildMainPanel()
    {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(BG);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG);
        header.setBorder(BorderFactory.createEmptyBorder(28, 28, 12, 28));
        JPanel titleBox = new JPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.setBackground(BG);
        JLabel title = new JLabel("Your Playlist");
        title.setFont(new Font("SansSerif", Font.BOLD, 34));
        title.setForeground(TEXT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        countLabel.setFont(BASE);
        countLabel.setForeground(SUBTEXT);
        countLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleBox.add(title);
        titleBox.add(Box.createVerticalStrut(4));
        titleBox.add(countLabel);
        header.add(titleBox, BorderLayout.CENTER);

        JPanel albumButtons =
            new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        albumButtons.setBackground(BG);
        albumButtons.add(addAlbumButton);
        albumButtons.add(removeAlbumButton);
        JPanel buttonHolder = new JPanel(new BorderLayout());
        buttonHolder.setBackground(BG);
        buttonHolder.add(albumButtons, BorderLayout.SOUTH);
        header.add(buttonHolder, BorderLayout.EAST);
        main.add(header, BorderLayout.NORTH);

        songList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        songList.setBackground(BG);
        songList.setFixedCellHeight(56);
        songList.setCellRenderer(new RowRenderer());
        songList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                if (e.getClickCount() == 2)
                {
                    handlePlay();
                }
            }
        });

        JScrollPane scroll = new JScrollPane(songList);
        scroll.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));
        scroll.getViewport().setBackground(BG);
        scroll.setBackground(BG);
        scroll.getVerticalScrollBar().setUI(new DarkScrollBarUI());
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));
        main.add(scroll, BorderLayout.CENTER);
        return main;
    }


    /**
     * Builds the bottom player bar.
     *
     * @return the player bar panel
     */
    private JPanel buildPlayerBar()
    {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(SIDEBAR);
        bar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        bar.setPreferredSize(new Dimension(0, 86));

        // left: now playing
        JPanel left = new JPanel(new BorderLayout());
        left.setOpaque(false);
        left.setPreferredSize(new Dimension(300, 0));
        JLabel small = new JLabel("NOW PLAYING");
        small.setFont(new Font("SansSerif", Font.BOLD, 10));
        small.setForeground(ACCENT);
        nowPlayingLabel.setFont(BOLD);
        nowPlayingLabel.setForeground(TEXT);
        left.add(small, BorderLayout.NORTH);
        left.add(nowPlayingLabel, BorderLayout.CENTER);

        // center: transport controls
        JPanel center = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 8));
        center.setOpaque(false);
        center.add(shuffleButton);
        center.add(previousButton);
        center.add(playButton);
        center.add(nextButton);
        center.add(loopToggle);

        // right: status messages
        JPanel right = new JPanel(new BorderLayout());
        right.setOpaque(false);
        right.setPreferredSize(new Dimension(300, 0));
        statusLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
        statusLabel.setForeground(SUBTEXT);
        statusLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        right.add(statusLabel, BorderLayout.CENTER);

        bar.add(left, BorderLayout.WEST);
        bar.add(center, BorderLayout.CENTER);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }


    /**
     * Gives a text field a dark, rounded-looking style.
     *
     * @param f
     *            the field to style
     */
    private static void styleField(JTextField f)
    {
        f.setFont(BASE);
        f.setForeground(TEXT);
        f.setCaretColor(TEXT);
        f.setBackground(ELEVATED);
        f.setDisabledTextColor(SUBTEXT);
        f.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ELEVATED),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
    }


    /**
     * Gives the type combo box a dark style.
     *
     * @param box
     *            the combo box to style
     */
    private static void styleCombo(JComboBox<String> box)
    {
        box.setFont(BASE);
        box.setBackground(ELEVATED);
        box.setForeground(TEXT);
        box.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton()
            {
                JButton b = new BasicArrowButton(
                    BasicArrowButton.SOUTH,
                    ELEVATED,
                    ELEVATED,
                    SUBTEXT,
                    ELEVATED);
                b.setBorder(BorderFactory.createEmptyBorder());
                return b;
            }
        });
        box.setBorder(BorderFactory.createLineBorder(ELEVATED));
        box.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                JList<?> list,
                Object value,
                int index,
                boolean selected,
                boolean focus)
            {
                super.getListCellRendererComponent(
                    list,
                    value,
                    index,
                    selected,
                    focus);
                setBackground(selected ? ROW_SELECTED : ELEVATED);
                setForeground(TEXT);
                setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
                return this;
            }
        });
    }

    // =====================================================================
    // Wiring
    // =====================================================================


    /**
     * Connects each control to its handler.
     */
    private void wireHandlers()
    {
        addButton.addActionListener(e -> handleAdd());
        removeButton.addActionListener(e -> handleRemove());
        addAlbumButton.addActionListener(e -> handleAddAlbum());
        removeAlbumButton.addActionListener(e -> handleDeleteAlbum());
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
        albumField.setBackground(isSong ? ELEVATED : DISABLED_FIELD);
        episodeField.setEnabled(!isSong);
        episodeField.setBackground(isSong ? DISABLED_FIELD : ELEVATED);
    }

    // =====================================================================
    // Handlers (package-private so tests can call them)
    // =====================================================================


    /**
     * Clears the list and refills it with describe() for each item. Keeps the
     * current item highlighted when there is one.
     */
    void refreshList()
    {
        listModel.clear();
        shownItems = playlist.getItems();
        for (Media item : shownItems)
        {
            listModel.addElement(item.describe());
        }
        countLabel.setText(
            shownItems.length + (shownItems.length == 1 ? " item" : " items"));
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
            clearForm();
        }
        catch (IllegalArgumentException e)
        {
            // Covers constructor rules and bad numbers.
            showStatus(e.getMessage());
        }
    }


    /**
     * Empties the add form after a successful add.
     */
    private void clearForm()
    {
        titleField.setText("");
        artistOrHostField.setText("");
        albumField.setText("");
        episodeField.setText("");
        lengthField.setText("");
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
     * Asks for an album title, artist, and song list in a dialog, then adds the
     * album. Does nothing if the dialog is cancelled.
     */
    void handleAddAlbum()
    {
        JTextField titleInput = new JTextField(22);
        JTextField artistInput = new JTextField(22);
        JTextArea songsInput = new JTextArea(8, 22);
        JScrollPane songsScroll = new JScrollPane(songsInput);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        Component[] parts = { new JLabel("Album title"), titleInput,
            new JLabel("Artist"), artistInput,
            new JLabel("Songs (one per line: title, length in seconds)"),
            songsScroll };
        for (Component part : parts)
        {
            ((JComponent)part).setAlignmentX(Component.LEFT_ALIGNMENT);
            form.add(part);
            form.add(Box.createVerticalStrut(6));
        }

        int choice = JOptionPane.showConfirmDialog(
            this,
            form,
            "Add album",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE);
        if (choice == JOptionPane.OK_OPTION)
        {
            addAlbum(
                titleInput.getText(),
                artistInput.getText(),
                songsInput.getText());
        }
    }


    /**
     * Builds an album from text and adds all of its songs, in order, to the end
     * of the playlist. If anything is wrong, a message goes to the status label
     * and nothing is added.
     *
     * @param title
     *            the album title
     * @param artist
     *            the artist; blank becomes "Unknown"
     * @param songLines
     *            one song per line, written as "title, seconds"
     */
    void addAlbum(String title, String artist, String songLines)
    {
        try
        {
            Album album = new Album(title, artist);
            String[] lines =
                (songLines == null ? "" : songLines).split("\\r?\\n");
            for (int i = 0; i < lines.length; i++)
            {
                String line = lines[i].trim();
                if (line.isEmpty())
                {
                    continue;
                }
                try
                {
                    album.add(parseAlbumSong(line, album));
                }
                catch (IllegalArgumentException e)
                {
                    throw new IllegalArgumentException(
                        "Line " + (i + 1) + ": " + e.getMessage());
                }
            }
            if (album.isEmpty())
            {
                throw new IllegalArgumentException(
                    "Album needs at least one song");
            }
            int count = album.size();
            album.addToPlaylist(playlist);
            refreshList();
            updateNowPlaying();
            showStatus(
                "Added album " + album.getTitle() + " (" + count
                    + (count == 1 ? " song)" : " songs)"));
        }
        catch (IllegalArgumentException e)
        {
            showStatus(e.getMessage());
        }
    }


    /**
     * Turns one "title, seconds" line into a song on the given album.
     *
     * @param line
     *            the trimmed, non-empty line
     * @param album
     *            the album the song belongs to
     * @return the new song
     * @throws IllegalArgumentException
     *             if the line is not valid
     */
    private Song parseAlbumSong(String line, Album album)
    {
        int comma = line.lastIndexOf(',');
        if (comma < 0)
        {
            throw new IllegalArgumentException(
                "Write each song as: title, seconds");
        }
        int length = parseWholeNumber(
            line.substring(comma + 1),
            "Length must be a whole number greater than 0");
        return new Song(
            line.substring(0, comma),
            album.getArtist(),
            album.getTitle(),
            length);
    }


    /**
     * Asks which album to delete (from the albums in the playlist) and deletes
     * it. Does nothing if the dialog is cancelled.
     */
    void handleDeleteAlbum()
    {
        String[] titles = albumTitlesInPlaylist();
        if (titles.length == 0)
        {
            showStatus("No albums in the playlist");
            return;
        }
        Object choice = JOptionPane.showInputDialog(
            this,
            "Delete which album?",
            "Delete album",
            JOptionPane.PLAIN_MESSAGE,
            null,
            titles,
            titles[0]);
        if (choice != null)
        {
            deleteAlbum((String)choice);
        }
    }


    /**
     * Removes every song from the playlist whose album matches the given title
     * (case is ignored). The current position is kept in step by the playlist.
     *
     * @param title
     *            the album title to delete
     */
    void deleteAlbum(String title)
    {
        int removed = 0;
        Media[] items = playlist.getItems();
        // go backward so earlier positions stay valid after each removal
        for (int i = items.length - 1; i >= 0; i--)
        {
            if (isFromAlbum(items[i], title))
            {
                playlist.removeAt(i);
                removed++;
            }
        }
        refreshList();
        updateNowPlaying();
        if (removed == 0)
        {
            showStatus("No album named " + title);
        }
        else
        {
            showStatus(
                "Deleted album " + title + " (" + removed
                    + (removed == 1 ? " song)" : " songs)"));
        }
    }


    /**
     * Tells whether an item is a song on the named album.
     *
     * @param item
     *            the playlist item
     * @param title
     *            the album title, or null
     * @return true for a song whose album equals title, ignoring case
     */
    private boolean isFromAlbum(Media item, String title)
    {
        return title != null && item instanceof Song
            && ((Song)item).getAlbum().equalsIgnoreCase(title);
    }


    /**
     * Lists each album that has songs in the playlist, once, in order of first
     * appearance. Songs with an unknown album are skipped.
     *
     * @return the album titles
     */
    private String[] albumTitlesInPlaylist()
    {
        ArrayList<String> titles = new ArrayList<String>();
        for (Media item : playlist.getItems())
        {
            if (!(item instanceof Song))
            {
                continue;
            }
            String album = ((Song)item).getAlbum();
            boolean seen = album.equalsIgnoreCase(UNKNOWN_ALBUM);
            for (String t : titles)
            {
                seen = seen || t.equalsIgnoreCase(album);
            }
            if (!seen)
            {
                titles.add(album);
            }
        }
        return titles.toArray(new String[0]);
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
        showMoveResult(playlist.next());
    }


    /**
     * Moves to the previous item and updates the label.
     */
    void handlePrevious()
    {
        showMoveResult(playlist.previous());
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
        if (playlist.size() >= 2)
        {
            // shuffle() resets the position to -1, so next() starts the
            // new order from its first item.
            playlist.next();
        }
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
        songList.repaint();
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

    // =====================================================================
    // Custom look: rows, buttons, scrollbar
    // =====================================================================


    /**
     * Formats seconds as m:ss.
     *
     * @param seconds
     *            the length in seconds
     * @return the text, e.g. "4:26"
     */
    private static String formatLength(int seconds)
    {
        return (seconds / 60) + ":" + String.format("%02d", seconds % 60);
    }

    /**
     * Draws one track row: number, title with subtitle, and length. The row for
     * the current item shows a green marker and title.
     */
    private class RowRenderer
        extends JPanel
        implements ListCellRenderer<String>
    {
        private final JLabel numberLabel =
            new JLabel("", SwingConstants.CENTER);
        private final JLabel titleLabel = new JLabel();
        private final JLabel subLabel = new JLabel();
        private final JLabel lengthLabel = new JLabel();

        /**
         * Builds the reusable row component.
         */
        RowRenderer()
        {
            super(new BorderLayout(14, 0));
            setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 14));
            numberLabel.setPreferredSize(new Dimension(34, 0));
            numberLabel.setFont(BASE);
            titleLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
            subLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
            subLabel.setForeground(SUBTEXT);
            lengthLabel.setFont(BASE);
            lengthLabel.setForeground(SUBTEXT);

            JPanel text = new JPanel();
            text.setOpaque(false);
            text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
            text.add(Box.createVerticalGlue());
            text.add(titleLabel);
            text.add(subLabel);
            text.add(Box.createVerticalGlue());

            add(numberLabel, BorderLayout.WEST);
            add(text, BorderLayout.CENTER);
            add(lengthLabel, BorderLayout.EAST);
        }


        @Override
        public Component getListCellRendererComponent(
            JList<? extends String> list,
            String value,
            int index,
            boolean selected,
            boolean focus)
        {
            Media item = index < shownItems.length ? shownItems[index] : null;
            boolean playing = index == playlist.getCurrentIndex();

            setBackground(selected ? ROW_SELECTED : BG);
            numberLabel.setText(playing ? "\u25B6" : String.valueOf(index + 1));
            numberLabel.setForeground(playing ? ACCENT : SUBTEXT);
            titleLabel.setForeground(playing ? ACCENT : TEXT);

            if (item == null)
            {
                titleLabel.setText(value);
                subLabel.setText(" ");
                lengthLabel.setText("");
            }
            else
            {
                titleLabel.setText(item.getTitle());
                subLabel.setText(subtitle(item));
                lengthLabel.setText(formatLength(item.getLength()));
            }
            return this;
        }


        /**
         * Builds the gray second line for a row.
         *
         * @param item
         *            the item shown
         * @return artist and album for songs, host and episode for podcasts
         */
        private String subtitle(Media item)
        {
            if (item instanceof Song)
            {
                Song s = (Song)item;
                return s.getArtist() + "  \u2022  " + s.getAlbum();
            }
            if (item instanceof Podcast)
            {
                Podcast p = (Podcast)item;
                return p.getHost() + "  \u2022  Episode "
                    + p.getEpisodeNumber();
            }
            return " ";
        }
    }

    /**
     * Paints a rounded pill with centered text.
     *
     * @param g
     *            the graphics context
     * @param b
     *            the button being painted
     * @param fill
     *            fill color, or null for none
     * @param border
     *            outline color, or null for none
     * @param text
     *            text color
     */
    private static void paintPill(
        Graphics g,
        AbstractButton b,
        Color fill,
        Color border,
        Color text)
    {
        Graphics2D g2 = (Graphics2D)g.create();
        g2.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(
            RenderingHints.KEY_TEXT_ANTIALIASING,
            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        int w = b.getWidth();
        int h = b.getHeight();
        if (fill != null)
        {
            g2.setColor(b.getModel().isRollover() ? fill.brighter() : fill);
            g2.fillRoundRect(0, 0, w, h, h, h);
        }
        if (border != null)
        {
            g2.setColor(b.getModel().isRollover() ? TEXT : border);
            g2.drawRoundRect(0, 0, w - 1, h - 1, h, h);
        }
        g2.setColor(text);
        g2.setFont(b.getFont());
        FontMetrics fm = g2.getFontMetrics();
        int x = (w - fm.stringWidth(b.getText())) / 2;
        int y = (h - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(b.getText(), x, y);
        g2.dispose();
    }


    /**
     * Applies the shared flat-button setup.
     *
     * @param b
     *            the button to set up
     */
    private static void flatten(AbstractButton b)
    {
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setOpaque(false);
        b.setRolloverEnabled(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    /**
     * A rounded pill button, filled or outlined.
     */
    private static class PillButton
        extends JButton
    {
        private final Color fill;
        private final Color border;
        private final Color textColor;

        /**
         * Creates a pill button.
         *
         * @param label
         *            the text
         * @param fill
         *            fill color, or null for an outline-only pill
         * @param textColor
         *            the text color
         * @param border
         *            outline color, or null for none
         */
        PillButton(String label, Color fill, Color textColor, Color border)
        {
            super(label);
            this.fill = fill;
            this.textColor = textColor;
            this.border = border;
            flatten(this);
            setFont(BOLD);
            setBorder(BorderFactory.createEmptyBorder(9, 20, 9, 20));
        }


        @Override
        protected void paintComponent(Graphics g)
        {
            paintPill(g, this, fill, border, textColor);
        }
    }


    /**
     * A pill toggle that turns green when selected.
     */
    private static class PillToggle
        extends JToggleButton
    {
        /**
         * Creates the toggle.
         *
         * @param label
         *            the text
         */
        PillToggle(String label)
        {
            super(label);
            flatten(this);
            setFont(BOLD);
            setBorder(BorderFactory.createEmptyBorder(9, 20, 9, 20));
        }


        @Override
        protected void paintComponent(Graphics g)
        {
            if (isSelected())
            {
                paintPill(g, this, ACCENT, null, Color.WHITE);
            }
            else
            {
                paintPill(g, this, null, SUBTEXT, TEXT);
            }
        }
    }


    /**
     * A drawn transport button: play (white circle), next, or previous.
     */
    private static class TransportButton
        extends JButton
    {
        static final int PLAY = 0;
        static final int NEXT1 = 1;
        static final int PREVIOUS1 = 2;

        private final int kind;

        /**
         * Creates a transport button.
         *
         * @param kind
         *            one of PLAY, NEXT, PREVIOUS
         * @param size
         *            the width and height in pixels
         */
        TransportButton(int kind, int size)
        {
            this.kind = kind;
            flatten(this);
            setPreferredSize(new Dimension(size, size));
            setToolTipText(
                kind == PLAY
                    ? "Play selected"
                    : kind == NEXT1 ? "Next" : "Previous");
        }


        @Override
        protected void paintComponent(Graphics g)
        {
            Graphics2D g2 = (Graphics2D)g.create();
            g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            boolean hover = getModel().isRollover();
            int top = h * 30 / 100;
            int bottom = h * 70 / 100;
            int mid = h / 2;

            if (kind == PLAY)
            {
                g2.setColor(hover ? Color.WHITE : new Color(235, 235, 235));
                g2.fillOval(0, 0, w - 1, h - 1);
                g2.setColor(Color.BLACK);
                int x1 = w * 38 / 100;
                int x2 = w * 72 / 100;
                g2.fillPolygon(
                    new Polygon(
                        new int[] { x1, x1, x2 },
                        new int[] { top, bottom, mid },
                        3));
            }
            else
            {
                g2.setColor(hover ? TEXT : SUBTEXT);
                if (kind == NEXT1)
                {
                    int x1 = w * 26 / 100;
                    int x2 = w * 62 / 100;
                    g2.fillPolygon(
                        new Polygon(
                            new int[] { x1, x1, x2 },
                            new int[] { top, bottom, mid },
                            3));
                    g2.fill(
                        new Rectangle(
                            w * 66 / 100,
                            top,
                            Math.max(2, w * 9 / 100),
                            bottom - top));
                }
                else
                {
                    int x1 = w * 74 / 100;
                    int x2 = w * 38 / 100;
                    g2.fillPolygon(
                        new Polygon(
                            new int[] { x1, x1, x2 },
                            new int[] { top, bottom, mid },
                            3));
                    g2.fill(
                        new Rectangle(
                            w * 25 / 100,
                            top,
                            Math.max(2, w * 9 / 100),
                            bottom - top));
                }
            }
            g2.dispose();
        }
    }


    /**
     * A thin dark scrollbar with no arrow buttons.
     */
    private static class DarkScrollBarUI
        extends BasicScrollBarUI
    {
        @Override
        protected void configureScrollBarColors()
        {
            thumbColor = new Color(105, 110, 120);
            trackColor = BG;
        }


        @Override
        protected JButton createDecreaseButton(int orientation)
        {
            return zeroButton();
        }


        @Override
        protected JButton createIncreaseButton(int orientation)
        {
            return zeroButton();
        }


        /**
         * Makes an invisible button, so the arrows take no space.
         *
         * @return a zero-size button
         */
        private JButton zeroButton()
        {
            JButton b = new JButton();
            b.setPreferredSize(new Dimension(0, 0));
            b.setMinimumSize(new Dimension(0, 0));
            b.setMaximumSize(new Dimension(0, 0));
            return b;
        }


        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle r)
        {
            g.setColor(BG);
            g.fillRect(r.x, r.y, r.width, r.height);
        }


        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle r)
        {
            Graphics2D g2 = (Graphics2D)g.create();
            g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(thumbColor);
            g2.fillRoundRect(r.x + 2, r.y, r.width - 4, r.height, 6, 6);
            g2.dispose();
        }
    }
}
