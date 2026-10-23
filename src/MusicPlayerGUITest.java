import java.lang.reflect.*;
import javax.swing.*;
import student.TestCase;

/**
 * Tests for MusicPlayerGUI.
 */
public class MusicPlayerGUITest
    extends TestCase
{
    private Playlist playlist;
    private MusicPlayerGUI gui;
    private Song a;
    private Song b;
    private Song c;

    public void setUp()
    {
        playlist = new Playlist();
        gui = new MusicPlayerGUI(playlist);
        a = new Song("A", "ArtistA", "AlbumA", 100);
        b = new Song("B", "ArtistB", "AlbumB", 200);
        c = new Song("C", "ArtistC", "AlbumC", 300);
    }


    public void tearDown()
    {
        if (gui != null)
        {
            gui.dispose();
        }
    }


    private Object field(String name)
    {
        return fieldOf(gui, name);
    }


    private Object fieldOf(MusicPlayerGUI target, String name)
    {
        try
        {
            Field f = MusicPlayerGUI.class.getDeclaredField(name);
            f.setAccessible(true);
            return f.get(target);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Cannot read field " + name, e);
        }
    }


    /** Clicks a button the way a user would, so its listener runs. */
    private void click(String buttonName)
    {
        ((AbstractButton)field(buttonName)).doClick(0);
    }


    private void call(String name)
    {
        try
        {
            Method m = MusicPlayerGUI.class.getDeclaredMethod(name);
            m.setAccessible(true);
            m.invoke(gui);
        }
        catch (InvocationTargetException e)
        {
            throw new RuntimeException(e.getCause());
        }
        catch (Exception e)
        {
            throw new RuntimeException("Cannot call " + name, e);
        }
    }


    @SuppressWarnings("unchecked")
    private DefaultListModel<String> model()
    {
        return (DefaultListModel<String>)field("listModel");
    }


    @SuppressWarnings("unchecked")
    private JList<String> list()
    {
        return (JList<String>)field("songList");
    }


    @SuppressWarnings("unchecked")
    private JComboBox<String> typeBox()
    {
        return (JComboBox<String>)field("typeBox");
    }


    private JLabel nowPlaying()
    {
        return (JLabel)field("nowPlayingLabel");
    }


    private JLabel status()
    {
        return (JLabel)field("statusLabel");
    }


    private JTextField text(String name)
    {
        return (JTextField)field(name);
    }


    private void fillABC()
    {
        playlist.add(a);
        playlist.add(b);
        playlist.add(c);
        call("refreshList");
    }


    /** Calls a package-private method that takes only String arguments. */
    private void callWith(String name, String... args)
    {
        try
        {
            Class<?>[] types = new Class<?>[args.length];
            for (int i = 0; i < args.length; i++)
            {
                types[i] = String.class;
            }
            Method m = MusicPlayerGUI.class.getDeclaredMethod(name, types);
            m.setAccessible(true);
            m.invoke(gui, (Object[])args);
        }
        catch (InvocationTargetException e)
        {
            throw new RuntimeException(e.getCause());
        }
        catch (Exception e)
        {
            throw new RuntimeException("Cannot call " + name, e);
        }
    }


    private void addAlbum(String title, String artist, String songs)
    {
        callWith("addAlbum", title, artist, songs);
    }


    private void deleteAlbum(String title)
    {
        callWith("deleteAlbum", title);
    }


    private void addSeries(String title, String host, String episodes)
    {
        callWith("addSeries", title, host, episodes);
    }


    private void deleteSeries(String title)
    {
        callWith("deleteSeries", title);
    }


    /**
     * Puts three episodes of series "Show" (plus two songs) in the playlist.
     */
    private void fillMixedSeries()
    {
        playlist.add(a);
        playlist.add(new Podcast("Show", "Host", 1, 600));
        playlist.add(new Podcast("Show", "Host", 2, 700));
        playlist.add(new Podcast("Show", "Host", 3, 800));
        playlist.add(b);
        call("refreshList");
    }


    /**
     * Puts three songs from album "P" (and one from AlbumA) in the playlist.
     */
    private void fillMixed()
    {
        playlist.add(a);
        playlist.add(new Song("P1", "Art", "P", 10));
        playlist.add(new Song("P2", "Art", "P", 20));
        playlist.add(new Song("P3", "Art", "P", 30));
        playlist.add(b);
        call("refreshList");
    }


    private
        void
        fillSongForm(String title, String artist, String album, String length)
    {
        typeBox().setSelectedIndex(0); // assumes index 0 is "Song"
        text("titleField").setText(title);
        text("artistOrHostField").setText(artist);
        text("albumField").setText(album);
        text("lengthField").setText(length);
    }


    /**
     * Checks that a new window starts with an empty list, nothing playing, and
     * every control created.
     */
    public void testGUIConstructor()
    {
        assertEquals(0, model().size());
        assertEquals("Nothing playing", nowPlaying().getText());
        // every control exists
        assertNotNull(field("addButton"));
        assertNotNull(field("removeButton"));
        assertNotNull(field("playButton"));
        assertNotNull(field("nextButton"));
        assertNotNull(field("previousButton"));
        assertNotNull(field("shuffleButton"));
        assertNotNull(field("loopToggle"));
    }


    /**
     * Checks that the constructor rejects a null playlist with an
     * IllegalArgumentException.
     */
    public void testGUIConstructorNullPlaylist()
    {
        // In the Spec we left this undefined; this test checks the rule
        // IllegalArgumentException.
        try
        {
            new MusicPlayerGUI(null);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            // expected
        }
    }


    /**
     * Clicks each button the way a user would and checks that it changes the
     * playlist.
     */
    public void testButtonsAreWiredToHandlers()
    {
        // Add (twice)
        fillSongForm("Yellow", "Coldplay", "Parachutes", "266");
        click("addButton");
        assertEquals(1, playlist.size());
        fillSongForm("Fix You", "Coldplay", "X&Y", "295");
        click("addButton");
        assertEquals(2, playlist.size());
        // Play
        list().setSelectedIndex(1);
        click("playButton");
        assertEquals(1, playlist.getCurrentIndex());
        // Previous, then Next
        click("previousButton");
        assertEquals(0, playlist.getCurrentIndex());
        click("nextButton");
        assertEquals(1, playlist.getCurrentIndex());
        // Loop toggle
        click("loopToggle");
        assertTrue(playlist.isLoopMode());
        // Shuffle (the GUI then starts at the first item of the new order)
        click("shuffleButton");
        assertEquals(0, playlist.getCurrentIndex());
        assertEquals(2, playlist.size());
        // Remove
        list().setSelectedIndex(0);
        click("removeButton");
        assertEquals(1, playlist.size());
    }


    /**
     * Checks that choosing Song or Podcast enables only the album or episode
     * field that applies.
     */
    public void testTypeBoxEnablesMatchingFields()
    {
        assertTrue(text("albumField").isEnabled());
        assertFalse(text("episodeField").isEnabled());
        typeBox().setSelectedIndex(1);
        assertFalse(text("albumField").isEnabled());
        assertTrue(text("episodeField").isEnabled());
        typeBox().setSelectedIndex(0);
        assertTrue(text("albumField").isEnabled());
        assertFalse(text("episodeField").isEnabled());
    }


    /**
     * Checks that refreshList does not select a row when the playlist reports a
     * current index past the end.
     */
    public void testRefreshListIgnoresIndexBeyondList()
    {
        // A Playlist that reports a current index past the end of the list;
        // refreshList must not try to select a row that does not exist.
        Playlist odd = new Playlist() {
            @Override
            public int getCurrentIndex()
            {
                return 99;
            }
        };
        odd.add(a);
        MusicPlayerGUI oddGui = new MusicPlayerGUI(odd);
        try
        {
            JList<?> oddList = (JList<?>)fieldOf(oddGui, "songList");
            assertEquals(-1, oddList.getSelectedIndex());
        }
        finally
        {
            oddGui.dispose();
        }
    }


    /**
     * Checks that refreshList highlights the row of the current item.
     */
    public void testRefreshListSelectsCurrentItem()
    {
        fillABC();
        playlist.play(1);
        call("refreshList");
        assertEquals(1, list().getSelectedIndex());
    }


    /**
     * Checks that refreshList shows one describe() line for each item in the
     * playlist.
     */
    public void testRefreshList()
    {
        fillABC();
        assertEquals(3, model().size());
        assertEquals(a.describe(), model().get(0));
        assertEquals(b.describe(), model().get(1));
        assertEquals(c.describe(), model().get(2));
    }


    /**
     * Checks that refreshList leaves the list empty for an empty playlist.
     */
    public void testRefreshListEmptyPlaylist()
    {
        call("refreshList");
        assertEquals(0, model().size());
    }


    /**
     * Checks that a valid song form adds the matching song to the playlist and
     * the list.
     */
    public void testHandleAdd()
    {
        fillSongForm("Yellow", "Coldplay", "Parachutes", "266");
        call("handleAdd");
        assertEquals(1, playlist.size());
        assertEquals(
            new Song("Yellow", "Coldplay", "Parachutes", 266),
            playlist.getItems()[0]);
        assertEquals(1, model().size());
    }


    /**
     * Checks that a new song goes after the items already in the playlist.
     */
    public void testHandleAddAppendsToEnd()
    {
        playlist.add(b);
        call("refreshList");
        fillSongForm("Yellow", "Coldplay", "Parachutes", "266");
        call("handleAdd");
        assertEquals(2, playlist.size());
        assertEquals(b, playlist.getItems()[0]);
        assertEquals(
            new Song("Yellow", "Coldplay", "Parachutes", 266),
            playlist.getItems()[1]);
    }


    /**
     * Checks that a successful add shows an "Added" message in the status
     * label.
     */
    public void testHandleAddShowsStatus()
    {
        fillSongForm("Yellow", "Coldplay", "Parachutes", "266");
        call("handleAdd");
        assertEquals("Added Yellow", status().getText());
    }


    /**
     * Checks that a blank artist and album are saved as "Unknown".
     */
    public void testHandleAddBlankArtistAndAlbum()
    {
        fillSongForm("Yellow", "", "   ", "266");
        call("handleAdd");
        assertEquals(1, playlist.size());
        Song added = (Song)playlist.getItems()[0];
        assertEquals("Unknown", added.getArtist());
        assertEquals("Unknown", added.getAlbum());
    }


    /**
     * Checks that the form fields are emptied after a successful add.
     */
    public void testHandleAddClearsFormOnSuccess()
    {
        fillSongForm("Yellow", "Coldplay", "Parachutes", "266");
        call("handleAdd");
        assertEquals("", text("titleField").getText());
        assertEquals("", text("artistOrHostField").getText());
        assertEquals("", text("albumField").getText());
        assertEquals("", text("lengthField").getText());
    }


    /**
     * Checks that the form fields keep their text when the add fails.
     */
    public void testHandleAddKeepsFormOnError()
    {
        fillSongForm("Yellow", "Coldplay", "Parachutes", "abc");
        call("handleAdd");
        assertEquals("Yellow", text("titleField").getText());
        assertEquals("abc", text("lengthField").getText());
    }


    /**
     * Checks that the podcast form adds the matching podcast to the playlist.
     */
    public void testHandleAddPodcast()
    {
        typeBox().setSelectedIndex(1); // assumes index 1 is "Podcast"
        text("titleField").setText("Hardcore History");
        text("artistOrHostField").setText("Dan");
        text("episodeField").setText("12");
        text("lengthField").setText("3600");
        call("handleAdd");
        assertEquals(1, playlist.size());
        assertEquals(
            new Podcast("Hardcore History", "Dan", 12, 3600),
            playlist.getItems()[0]);
    }


    /**
     * Checks that a non-numeric episode number shows an error and adds nothing.
     */
    public void testHandleAddNonNumericEpisode()
    {
        typeBox().setSelectedIndex(1);
        text("titleField").setText("Hardcore History");
        text("artistOrHostField").setText("Dan");
        text("episodeField").setText("x");
        text("lengthField").setText("3600");
        call("handleAdd");
        assertEquals(
            "Episode number must be a whole number greater than 0",
            status().getText());
        assertEquals(0, playlist.size());
    }


    /**
     * Checks that an episode number of 0 shows an error and adds nothing.
     */
    public void testHandleAddZeroEpisode()
    {
        typeBox().setSelectedIndex(1);
        text("titleField").setText("Hardcore History");
        text("artistOrHostField").setText("Dan");
        text("episodeField").setText("0");
        text("lengthField").setText("3600");
        call("handleAdd");
        assertEquals(
            "Episode number must be greater than 0",
            status().getText());
        assertEquals(0, playlist.size());
    }


    /**
     * Checks that a blank title shows an error and adds nothing.
     */
    public void testHandleAddBlankTitle()
    {
        fillSongForm("", "Coldplay", "Parachutes", "266");
        call("handleAdd");
        assertEquals("Title must not be blank", status().getText());
        assertEquals(0, playlist.size());
    }


    /**
     * Checks that a non-numeric length shows an error and adds nothing.
     */
    public void testHandleAddNonNumericLength()
    {
        fillSongForm("Yellow", "Coldplay", "Parachutes", "abc");
        call("handleAdd");
        assertEquals(
            "Length must be a whole number greater than 0",
            status().getText());
        assertEquals(0, playlist.size());
    }


    /**
     * Checks that a length of 0 shows an error and adds nothing.
     */
    public void testHandleAddZeroLength()
    {
        fillSongForm("Yellow", "Coldplay", "Parachutes", "0");
        call("handleAdd");
        assertEquals("Length must be greater than 0", status().getText());
        assertEquals(0, playlist.size());
    }


    /**
     * Checks that removing the selected row removes that exact copy when there
     * are duplicates.
     */
    public void testHandleRemove()
    {
        // duplicate at positions 0 and 1; select row 1 to remove that copy
        playlist.add(a);
        playlist.add(a);
        playlist.add(c);
        call("refreshList");
        list().setSelectedIndex(1);
        call("handleRemove");
        assertEquals(2, playlist.size());
        assertEquals(a, playlist.getItems()[0]);
        assertEquals(c, playlist.getItems()[1]);
        assertEquals(2, model().size());
    }


    /**
     * Checks that removing the last of two equal items leaves the first one in
     * place.
     */
    public void testHandleRemoveLastDuplicate()
    {
        // [A, B, A]: removing row 2 must remove the last A, not the first.
        playlist.add(a);
        playlist.add(b);
        playlist.add(a);
        call("refreshList");
        list().setSelectedIndex(2);
        call("handleRemove");
        assertEquals(2, playlist.size());
        assertEquals(a, playlist.getItems()[0]);
        assertEquals(b, playlist.getItems()[1]);
    }


    /**
     * Checks that removing a row that is already gone refreshes the list and
     * shows no "Removed" message.
     */
    public void testHandleRemoveStaleSelection()
    {
        fillABC();
        list().setSelectedIndex(2);
        playlist.removeAt(2); // playlist changes behind the GUI's back
        call("handleRemove"); // removeAt(2) now returns null
        assertEquals(2, playlist.size());
        assertEquals(2, model().size()); // list refreshed to match
        assertEquals(" ", status().getText()); // no "Removed" message
    }


    /**
     * Checks that a successful remove shows a "Removed" message in the status
     * label.
     */
    public void testHandleRemoveShowsStatus()
    {
        fillABC();
        list().setSelectedIndex(0);
        call("handleRemove");
        assertEquals("Removed A", status().getText());
    }


    /**
     * Checks that removing with no row selected shows a message and removes
     * nothing.
     */
    public void testHandleRemoveNoSelection()
    {
        fillABC();
        list().clearSelection();
        call("handleRemove");
        assertEquals("Select an item first", status().getText());
        assertEquals(3, playlist.size());
    }

    // ---------------------------------------------------------------
    // Albums: buttons, addAlbum, deleteAlbum, handleDeleteAlbum
    // ---------------------------------------------------------------


    /**
     * Checks that the Add album and Delete album buttons were created.
     */
    public void testAlbumButtonsExist()
    {
        assertNotNull(field("addAlbumButton"));
        assertNotNull(field("removeAlbumButton"));
    }


    /**
     * Checks that an album's songs are added in order with the album's artist
     * and title.
     */
    public void testAddAlbum()
    {
        addAlbum(
            "Parachutes",
            "Coldplay",
            "Yellow, 266\nTrouble, 270\nShiver, 299");
        assertEquals(3, playlist.size());
        assertEquals(3, model().size());
        Media[] items = playlist.getItems();
        assertEquals(
            new Song("Yellow", "Coldplay", "Parachutes", 266),
            items[0]);
        assertEquals(
            new Song("Trouble", "Coldplay", "Parachutes", 270),
            items[1]);
        assertEquals(
            new Song("Shiver", "Coldplay", "Parachutes", 299),
            items[2]);
        assertEquals("Added album Parachutes (3 songs)", status().getText());
    }


    /**
     * Checks that a one-song album works and the status says "1 song".
     */
    public void testAddAlbumOneSong()
    {
        addAlbum("Single", "Someone", "Only One, 180");
        assertEquals(1, playlist.size());
        assertEquals("Added album Single (1 song)", status().getText());
    }


    /**
     * Checks that album songs go after the items already in the playlist.
     */
    public void testAddAlbumAppendsToEnd()
    {
        playlist.add(b);
        call("refreshList");
        addAlbum("Parachutes", "Coldplay", "Yellow, 266\nTrouble, 270");
        assertEquals(3, playlist.size());
        assertEquals(b, playlist.getItems()[0]);
        assertEquals("Yellow", playlist.getItems()[1].getTitle());
        assertEquals("Trouble", playlist.getItems()[2].getTitle());
    }


    /**
     * Checks that extra spaces are trimmed and blank lines in the song box are
     * ignored.
     */
    public void testAddAlbumTrimsAndSkipsBlankLines()
    {
        addAlbum(
            "  Parachutes  ",
            " Coldplay ",
            "\n  Yellow ,  266  \n\n   \nTrouble, 270\n\n");
        assertEquals(2, playlist.size());
        assertEquals(
            new Song("Yellow", "Coldplay", "Parachutes", 266),
            playlist.getItems()[0]);
        assertEquals("Added album Parachutes (2 songs)", status().getText());
    }


    /**
     * Checks that songs separated by \r\n line endings are all read.
     */
    public void testAddAlbumWindowsLineEndings()
    {
        addAlbum("Parachutes", "Coldplay", "Yellow, 266\r\nTrouble, 270");
        assertEquals(2, playlist.size());
    }


    /**
     * Checks that only the last comma on a line separates the title from the
     * length.
     */
    public void testAddAlbumCommaInSongTitle()
    {
        addAlbum("Album", "Artist", "So, Long, 10");
        assertEquals(1, playlist.size());
        assertEquals("So, Long", playlist.getItems()[0].getTitle());
        assertEquals(10, playlist.getItems()[0].getLength());
    }


    /**
     * Checks that a blank album artist makes each song's artist "Unknown".
     */
    public void testAddAlbumBlankArtist()
    {
        addAlbum("Parachutes", "  ", "Yellow, 266");
        assertEquals("Unknown", ((Song)playlist.getItems()[0]).getArtist());
        assertEquals("Parachutes", ((Song)playlist.getItems()[0]).getAlbum());
    }


    /**
     * Checks that adding an album updates the visible list.
     */
    public void testAddAlbumRefreshesList()
    {
        addAlbum("Parachutes", "Coldplay", "Yellow, 266");
        assertEquals(1, model().size());
        assertEquals(playlist.getItems()[0].describe(), model().get(0));
    }


    /**
     * Checks that adding an album does not change which item is playing.
     */
    public void testAddAlbumKeepsCurrentItem()
    {
        playlist.add(a);
        call("refreshList");
        playlist.play(0);
        call("updateNowPlaying");
        addAlbum("Parachutes", "Coldplay", "Yellow, 266");
        assertEquals(0, playlist.getCurrentIndex());
        assertEquals(a.describe(), nowPlaying().getText());
    }


    /**
     * Checks that a null, empty, or blank album title shows an error and adds
     * nothing.
     */
    public void testAddAlbumBlankTitle()
    {
        String[] titles = { null, "", "   " };
        for (String t : titles)
        {
            addAlbum(t, "Coldplay", "Yellow, 266");
            assertEquals("Title must not be blank", status().getText());
            assertEquals(0, playlist.size());
        }
    }


    /**
     * Checks that an album with no song lines shows an error and adds nothing.
     */
    public void testAddAlbumNoSongs()
    {
        String[] inputs = { null, "", "  \n \n" };
        for (String songs : inputs)
        {
            addAlbum("Parachutes", "Coldplay", songs);
            assertEquals("Album needs at least one song", status().getText());
            assertEquals(0, playlist.size());
        }
    }


    /**
     * Checks that a song line with no comma shows an error with its line
     * number.
     */
    public void testAddAlbumMissingComma()
    {
        addAlbum("Parachutes", "Coldplay", "Yellow, 266\nTrouble 270");
        assertEquals(
            "Line 2: Write each song as: title, seconds",
            status().getText());
        assertEquals(0, playlist.size());
    }


    /**
     * Checks that a non-numeric song length shows an error with its line number
     * and adds nothing.
     */
    public void testAddAlbumNonNumericLength()
    {
        addAlbum("Parachutes", "Coldplay", "Yellow, 266\nTrouble, abc");
        assertEquals(
            "Line 2: Length must be a whole number greater than 0",
            status().getText());
        assertEquals(0, playlist.size());
    }


    /**
     * Checks that a song length of 0 shows an error with its line number and
     * adds nothing.
     */
    public void testAddAlbumZeroLength()
    {
        addAlbum("Parachutes", "Coldplay", "Yellow, 0");
        assertEquals(
            "Line 1: Length must be greater than 0",
            status().getText());
        assertEquals(0, playlist.size());
    }


    /**
     * Checks that a song line with a blank title shows an error with its line
     * number.
     */
    public void testAddAlbumBlankSongTitle()
    {
        addAlbum("Parachutes", "Coldplay", "Yellow, 266\n , 100");
        assertEquals("Line 2: Title must not be blank", status().getText());
        assertEquals(0, playlist.size());
    }


    /**
     * Checks that the line number in an error counts blank lines too.
     */
    public void testAddAlbumErrorLineNumberCountsBlankLines()
    {
        addAlbum("Parachutes", "Coldplay", "Yellow, 266\n\n\nTrouble, x");
        assertEquals(
            "Line 4: Length must be a whole number greater than 0",
            status().getText());
    }


    /**
     * Checks that one bad line stops the whole album from being added.
     */
    public void testAddAlbumErrorAddsNothingEvenAfterGoodLines()
    {
        playlist.add(a);
        call("refreshList");
        addAlbum("Parachutes", "Coldplay", "Yellow, 266\nTrouble, abc");
        assertEquals(1, playlist.size());
        assertEquals(1, model().size());
    }


    /**
     * Checks that deleting an album removes all of its songs and keeps the
     * others.
     */
    public void testDeleteAlbum()
    {
        fillMixed();
        deleteAlbum("P");
        assertEquals(2, playlist.size());
        assertEquals(a, playlist.getItems()[0]);
        assertEquals(b, playlist.getItems()[1]);
        assertEquals(2, model().size());
        assertEquals("Deleted album P (3 songs)", status().getText());
    }


    /**
     * Checks that deleting a one-song album works and the status says "1 song".
     */
    public void testDeleteAlbumOneSong()
    {
        fillMixed();
        deleteAlbum("AlbumA");
        assertEquals(4, playlist.size());
        assertEquals("Deleted album AlbumA (1 song)", status().getText());
    }


    /**
     * Checks that the album title matches no matter how it is capitalized.
     */
    public void testDeleteAlbumIgnoresCase()
    {
        fillMixed();
        deleteAlbum("p");
        assertEquals(2, playlist.size());
    }


    /**
     * Checks that deleting an album that is not there removes nothing and says
     * so.
     */
    public void testDeleteAlbumNotFound()
    {
        fillMixed();
        deleteAlbum("Nope");
        assertEquals(5, playlist.size());
        assertEquals("No album named Nope", status().getText());
    }


    /**
     * Checks that a null album title removes nothing and does not crash.
     */
    public void testDeleteAlbumNull()
    {
        fillMixed();
        deleteAlbum(null);
        assertEquals(5, playlist.size());
        assertEquals("No album named null", status().getText());
    }


    /**
     * Checks that deleting from an empty playlist says the album was not found.
     */
    public void testDeleteAlbumEmptyPlaylist()
    {
        deleteAlbum("P");
        assertEquals(0, playlist.size());
        assertEquals("No album named P", status().getText());
    }


    /**
     * Checks that every copy of a duplicated album song is removed.
     */
    public void testDeleteAlbumRemovesDuplicates()
    {
        Song p1 = new Song("P1", "Art", "P", 10);
        playlist.add(p1);
        playlist.add(a);
        playlist.add(p1);
        call("refreshList");
        deleteAlbum("P");
        assertEquals(1, playlist.size());
        assertEquals(a, playlist.getItems()[0]);
    }


    /**
     * Checks that a podcast is never removed, even when its title matches the
     * album.
     */
    public void testDeleteAlbumKeepsPodcastWithSameTitle()
    {
        Podcast pod = new Podcast("P", "Host", 1, 600);
        playlist.add(pod);
        playlist.add(new Song("P1", "Art", "P", 10));
        call("refreshList");
        deleteAlbum("P");
        assertEquals(1, playlist.size());
        assertEquals(pod, playlist.getItems()[0]);
    }


    /**
     * Checks that the current item stays the same when it comes after the
     * deleted album.
     */
    public void testDeleteAlbumCurrentItemAfterDeleted()
    {
        fillMixed();
        playlist.play(4); // b, after the album
        call("updateNowPlaying");
        deleteAlbum("P");
        assertEquals(1, playlist.getCurrentIndex());
        assertEquals(b, playlist.getCurrent().get());
        assertEquals(b.describe(), nowPlaying().getText());
    }


    /**
     * Checks that the current item stays the same when it comes before the
     * deleted album.
     */
    public void testDeleteAlbumCurrentItemBeforeDeleted()
    {
        fillMixed();
        playlist.play(0); // a, before the album
        deleteAlbum("P");
        assertEquals(0, playlist.getCurrentIndex());
        assertEquals(a, playlist.getCurrent().get());
    }


    /**
     * Checks that the current position stays valid when the playing song is
     * deleted.
     */
    public void testDeleteAlbumCurrentItemInsideDeleted()
    {
        fillMixed();
        playlist.play(2); // inside the album
        deleteAlbum("P");
        assertEquals(2, playlist.size());
        assertTrue(playlist.getCurrentIndex() >= 0);
        assertTrue(playlist.getCurrentIndex() < playlist.size());
    }


    /**
     * Checks that deleting every item leaves nothing selected and nothing
     * playing.
     */
    public void testDeleteAlbumEmptiesPlaylist()
    {
        playlist.add(new Song("P1", "Art", "P", 10));
        playlist.add(new Song("P2", "Art", "P", 20));
        call("refreshList");
        playlist.play(1);
        call("updateNowPlaying");
        deleteAlbum("P");
        assertTrue(playlist.isEmpty());
        assertEquals(-1, playlist.getCurrentIndex());
        assertEquals("Nothing playing", nowPlaying().getText());
        assertEquals(0, model().size());
    }


    /**
     * Checks that an album can be added and then deleted, leaving the playlist
     * as it was.
     */
    public void testAddThenDeleteAlbum()
    {
        playlist.add(a);
        call("refreshList");
        addAlbum("Parachutes", "Coldplay", "Yellow, 266\nTrouble, 270");
        assertEquals(3, playlist.size());
        deleteAlbum("Parachutes");
        assertEquals(1, playlist.size());
        assertEquals(a, playlist.getItems()[0]);
    }


    /**
     * Checks that songs added through the sidebar form are removed with their
     * album.
     */
    public void testDeleteAlbumAlsoRemovesSongsAddedFromForm()
    {
        fillSongForm("Yellow", "Coldplay", "Parachutes", "266");
        call("handleAdd");
        deleteAlbum("Parachutes");
        assertTrue(playlist.isEmpty());
    }


    /**
     * Checks that deleting with no albums in the playlist shows a message
     * instead of a dialog.
     */
    public void testHandleDeleteAlbumNoAlbums()
    {
        // no dialog opens when there is nothing to choose from
        call("handleDeleteAlbum");
        assertEquals("No albums in the playlist", status().getText());
    }


    /**
     * Checks that songs with an unknown album and podcasts do not count as
     * albums.
     */
    public void testHandleDeleteAlbumOnlyUnknownAlbumsAndPodcasts()
    {
        playlist.add(new Song("U", null, null, 10));
        playlist.add(new Podcast("Pod", "Host", 1, 600));
        call("refreshList");
        call("handleDeleteAlbum");
        assertEquals("No albums in the playlist", status().getText());
        assertEquals(2, playlist.size());
    }

    // ---------------------------------------------------------------
    // Podcast series: buttons, addSeries, deleteSeries, handleDeleteSeries
    // ---------------------------------------------------------------


    /**
     * Checks that the Add series and Delete series buttons were created.
     */
    public void testSeriesButtonsExist()
    {
        assertNotNull(field("addSeriesButton"));
        assertNotNull(field("removeSeriesButton"));
    }


    /**
     * Checks that a series' episodes are added in order with the series' title
     * and host.
     */
    public void testAddSeries()
    {
        addSeries("Hardcore History", "Dan", "1, 3600\n2, 4000\n3, 5000");
        assertEquals(3, playlist.size());
        assertEquals(3, model().size());
        Media[] items = playlist.getItems();
        assertEquals(new Podcast("Hardcore History", "Dan", 1, 3600), items[0]);
        assertEquals(new Podcast("Hardcore History", "Dan", 2, 4000), items[1]);
        assertEquals(new Podcast("Hardcore History", "Dan", 3, 5000), items[2]);
        assertEquals(
            "Added series Hardcore History (3 episodes)",
            status().getText());
    }


    /**
     * Checks that a one-episode series works and the status says "1 episode".
     */
    public void testAddSeriesOneEpisode()
    {
        addSeries("Solo", "Someone", "1, 600");
        assertEquals(1, playlist.size());
        assertEquals("Added series Solo (1 episode)", status().getText());
    }


    /**
     * Checks that series episodes go after the items already in the playlist.
     */
    public void testAddSeriesAppendsToEnd()
    {
        playlist.add(b);
        call("refreshList");
        addSeries("Show", "Host", "1, 60\n2, 70");
        assertEquals(3, playlist.size());
        assertEquals(b, playlist.getItems()[0]);
        assertEquals(1, ((Podcast)playlist.getItems()[1]).getEpisodeNumber());
        assertEquals(2, ((Podcast)playlist.getItems()[2]).getEpisodeNumber());
    }


    /**
     * Checks that extra spaces are trimmed and blank lines in the episode box
     * are ignored.
     */
    public void testAddSeriesTrimsAndSkipsBlankLines()
    {
        addSeries("  Show  ", " Host ", "\n  1 ,  60  \n\n   \n2, 70\n\n");
        assertEquals(2, playlist.size());
        assertEquals(
            new Podcast("Show", "Host", 1, 60),
            playlist.getItems()[0]);
        assertEquals("Added series Show (2 episodes)", status().getText());
    }


    /**
     * Checks that episodes separated by \r\n line endings are all read.
     */
    public void testAddSeriesWindowsLineEndings()
    {
        addSeries("Show", "Host", "1, 60\r\n2, 70");
        assertEquals(2, playlist.size());
    }


    /**
     * Checks that a blank series host makes each episode's host "Unknown".
     */
    public void testAddSeriesBlankHost()
    {
        addSeries("Show", "  ", "1, 60");
        assertEquals("Unknown", ((Podcast)playlist.getItems()[0]).getHost());
        assertEquals("Show", playlist.getItems()[0].getTitle());
    }


    /**
     * Checks that adding a series updates the visible list.
     */
    public void testAddSeriesRefreshesList()
    {
        addSeries("Show", "Host", "1, 60");
        assertEquals(1, model().size());
        assertEquals(playlist.getItems()[0].describe(), model().get(0));
    }


    /**
     * Checks that adding a series does not change which item is playing.
     */
    public void testAddSeriesKeepsCurrentItem()
    {
        playlist.add(a);
        call("refreshList");
        playlist.play(0);
        call("updateNowPlaying");
        addSeries("Show", "Host", "1, 60");
        assertEquals(0, playlist.getCurrentIndex());
        assertEquals(a.describe(), nowPlaying().getText());
    }


    /**
     * Checks that a null, empty, or blank series title shows an error and adds
     * nothing.
     */
    public void testAddSeriesBlankTitle()
    {
        String[] titles = { null, "", "   " };
        for (String t : titles)
        {
            addSeries(t, "Host", "1, 60");
            assertEquals("Title must not be blank", status().getText());
            assertEquals(0, playlist.size());
        }
    }


    /**
     * Checks that a series with no episode lines shows an error and adds
     * nothing.
     */
    public void testAddSeriesNoEpisodes()
    {
        String[] inputs = { null, "", "  \n \n" };
        for (String episodes : inputs)
        {
            addSeries("Show", "Host", episodes);
            assertEquals(
                "Series needs at least one episode",
                status().getText());
            assertEquals(0, playlist.size());
        }
    }


    /**
     * Checks that an episode line with no comma shows an error with its line
     * number.
     */
    public void testAddSeriesMissingComma()
    {
        addSeries("Show", "Host", "1, 60\n2 70");
        assertEquals(
            "Line 2: Write each episode as: number, seconds",
            status().getText());
        assertEquals(0, playlist.size());
    }


    /**
     * Checks that a non-numeric episode number shows an error with its line
     * number and adds nothing.
     */
    public void testAddSeriesNonNumericEpisode()
    {
        addSeries("Show", "Host", "x, 60");
        assertEquals(
            "Line 1: Episode number must be a whole number greater than 0",
            status().getText());
        assertEquals(0, playlist.size());
    }


    /**
     * Checks that an episode number of 0 shows an error with its line number
     * and adds nothing.
     */
    public void testAddSeriesZeroEpisode()
    {
        addSeries("Show", "Host", "0, 60");
        assertEquals(
            "Line 1: Episode number must be greater than 0",
            status().getText());
        assertEquals(0, playlist.size());
    }


    /**
     * Checks that a non-numeric episode length shows an error with its line
     * number and adds nothing.
     */
    public void testAddSeriesNonNumericLength()
    {
        addSeries("Show", "Host", "1, 60\n2, abc");
        assertEquals(
            "Line 2: Length must be a whole number greater than 0",
            status().getText());
        assertEquals(0, playlist.size());
    }


    /**
     * Checks that an episode length of 0 shows an error with its line number
     * and adds nothing.
     */
    public void testAddSeriesZeroLength()
    {
        addSeries("Show", "Host", "1, 0");
        assertEquals(
            "Line 1: Length must be greater than 0",
            status().getText());
        assertEquals(0, playlist.size());
    }


    /**
     * Checks that the line number in an error counts blank lines too.
     */
    public void testAddSeriesErrorLineNumberCountsBlankLines()
    {
        addSeries("Show", "Host", "1, 60\n\n\n2, x");
        assertEquals(
            "Line 4: Length must be a whole number greater than 0",
            status().getText());
    }


    /**
     * Checks that one bad line stops the whole series from being added.
     */
    public void testAddSeriesErrorAddsNothingEvenAfterGoodLines()
    {
        playlist.add(a);
        call("refreshList");
        addSeries("Show", "Host", "1, 60\n2, abc");
        assertEquals(1, playlist.size());
        assertEquals(1, model().size());
    }


    /**
     * Checks that deleting a series removes all of its episodes and keeps the
     * others.
     */
    public void testDeleteSeries()
    {
        fillMixedSeries();
        deleteSeries("Show");
        assertEquals(2, playlist.size());
        assertEquals(a, playlist.getItems()[0]);
        assertEquals(b, playlist.getItems()[1]);
        assertEquals(2, model().size());
        assertEquals("Deleted series Show (3 episodes)", status().getText());
    }


    /**
     * Checks that deleting a one-episode series works and the status says "1
     * episode".
     */
    public void testDeleteSeriesOneEpisode()
    {
        playlist.add(a);
        playlist.add(new Podcast("Solo", "Host", 1, 60));
        call("refreshList");
        deleteSeries("Solo");
        assertEquals(1, playlist.size());
        assertEquals("Deleted series Solo (1 episode)", status().getText());
    }


    /**
     * Checks that the series title matches no matter how it is capitalized.
     */
    public void testDeleteSeriesIgnoresCase()
    {
        fillMixedSeries();
        deleteSeries("sHoW");
        assertEquals(2, playlist.size());
    }


    /**
     * Checks that deleting a series that is not there removes nothing and says
     * so.
     */
    public void testDeleteSeriesNotFound()
    {
        fillMixedSeries();
        deleteSeries("Nope");
        assertEquals(5, playlist.size());
        assertEquals("No series named Nope", status().getText());
    }


    /**
     * Checks that a null series title removes nothing and does not crash.
     */
    public void testDeleteSeriesNull()
    {
        fillMixedSeries();
        deleteSeries(null);
        assertEquals(5, playlist.size());
        assertEquals("No series named null", status().getText());
    }


    /**
     * Checks that deleting from an empty playlist says the series was not
     * found.
     */
    public void testDeleteSeriesEmptyPlaylist()
    {
        deleteSeries("Show");
        assertEquals(0, playlist.size());
        assertEquals("No series named Show", status().getText());
    }


    /**
     * Checks that every copy of a duplicated episode is removed.
     */
    public void testDeleteSeriesRemovesDuplicates()
    {
        Podcast e1 = new Podcast("Show", "Host", 1, 600);
        playlist.add(e1);
        playlist.add(a);
        playlist.add(e1);
        call("refreshList");
        deleteSeries("Show");
        assertEquals(1, playlist.size());
        assertEquals(a, playlist.getItems()[0]);
    }


    /**
     * Checks that a song is never removed, even when its title matches the
     * series.
     */
    public void testDeleteSeriesKeepsSongWithSameTitle()
    {
        Song song = new Song("Show", "Art", "Alb", 10);
        playlist.add(song);
        playlist.add(new Podcast("Show", "Host", 1, 600));
        call("refreshList");
        deleteSeries("Show");
        assertEquals(1, playlist.size());
        assertEquals(song, playlist.getItems()[0]);
    }


    /**
     * Checks that the current item stays the same when it comes after the
     * deleted series.
     */
    public void testDeleteSeriesCurrentItemAfterDeleted()
    {
        fillMixedSeries();
        playlist.play(4); // b, after the series
        call("updateNowPlaying");
        deleteSeries("Show");
        assertEquals(1, playlist.getCurrentIndex());
        assertEquals(b, playlist.getCurrent().get());
        assertEquals(b.describe(), nowPlaying().getText());
    }


    /**
     * Checks that the current item stays the same when it comes before the
     * deleted series.
     */
    public void testDeleteSeriesCurrentItemBeforeDeleted()
    {
        fillMixedSeries();
        playlist.play(0); // a, before the series
        deleteSeries("Show");
        assertEquals(0, playlist.getCurrentIndex());
        assertEquals(a, playlist.getCurrent().get());
    }


    /**
     * Checks that the current position stays valid when the playing episode is
     * deleted.
     */
    public void testDeleteSeriesCurrentItemInsideDeleted()
    {
        fillMixedSeries();
        playlist.play(2); // inside the series
        deleteSeries("Show");
        assertEquals(2, playlist.size());
        assertTrue(playlist.getCurrentIndex() >= 0);
        assertTrue(playlist.getCurrentIndex() < playlist.size());
    }


    /**
     * Checks that deleting every item leaves nothing selected and nothing
     * playing.
     */
    public void testDeleteSeriesEmptiesPlaylist()
    {
        playlist.add(new Podcast("Show", "Host", 1, 600));
        playlist.add(new Podcast("Show", "Host", 2, 700));
        call("refreshList");
        playlist.play(1);
        call("updateNowPlaying");
        deleteSeries("Show");
        assertTrue(playlist.isEmpty());
        assertEquals(-1, playlist.getCurrentIndex());
        assertEquals("Nothing playing", nowPlaying().getText());
        assertEquals(0, model().size());
    }


    /**
     * Checks that a series can be added and then deleted, leaving the playlist
     * as it was.
     */
    public void testAddThenDeleteSeries()
    {
        playlist.add(a);
        call("refreshList");
        addSeries("Show", "Host", "1, 60\n2, 70");
        assertEquals(3, playlist.size());
        deleteSeries("Show");
        assertEquals(1, playlist.size());
        assertEquals(a, playlist.getItems()[0]);
    }


    /**
     * Checks that podcasts added through the sidebar form are removed with
     * their series.
     */
    public void testDeleteSeriesAlsoRemovesEpisodesAddedFromForm()
    {
        typeBox().setSelectedIndex(1); // Podcast
        text("titleField").setText("Show");
        text("artistOrHostField").setText("Host");
        text("episodeField").setText("1");
        text("lengthField").setText("600");
        call("handleAdd");
        assertEquals(1, playlist.size());
        deleteSeries("Show");
        assertTrue(playlist.isEmpty());
    }


    /**
     * Checks that deleting with no podcast series in the playlist shows a
     * message instead of a dialog.
     */
    public void testHandleDeleteSeriesNoSeries()
    {
        // no dialog opens when there is nothing to choose from
        call("handleDeleteSeries");
        assertEquals("No podcast series in the playlist", status().getText());
    }


    /**
     * Checks that a playlist holding only songs has no series to delete.
     */
    public void testHandleDeleteSeriesOnlySongs()
    {
        fillABC();
        call("handleDeleteSeries");
        assertEquals("No podcast series in the playlist", status().getText());
        assertEquals(3, playlist.size());
    }


    /**
     * Checks that playing the selected row makes it current and shows it as now
     * playing.
     */
    public void testHandlePlay()
    {
        fillABC();
        list().setSelectedIndex(2);
        call("handlePlay");
        assertEquals(2, playlist.getCurrentIndex());
        assertEquals(c.describe(), nowPlaying().getText());
    }


    /**
     * Checks that playing with no row selected shows a message and changes
     * nothing.
     */
    public void testHandlePlayNoSelection()
    {
        fillABC();
        list().clearSelection();
        call("handlePlay");
        assertEquals("Select an item first", status().getText());
        assertEquals(-1, playlist.getCurrentIndex());
    }


    /**
     * Checks that next moves to the following item and updates now playing.
     */
    public void testHandleNext()
    {
        fillABC();
        playlist.play(0);
        call("handleNext");
        assertEquals(1, playlist.getCurrentIndex());
        assertEquals(b.describe(), nowPlaying().getText());
    }


    /**
     * Checks that next on an empty playlist shows "The playlist is empty".
     */
    public void testHandleNextEmptyPlaylist()
    {
        call("handleNext");
        assertEquals("The playlist is empty", status().getText());
    }


    /**
     * Checks that next at the last item with loop off shows "End of playlist"
     * and stays put.
     */
    public void testHandleNextEndOfPlaylist()
    {
        fillABC();
        playlist.play(2);
        call("handleNext");
        assertEquals("End of playlist", status().getText());
        assertEquals(2, playlist.getCurrentIndex());
    }


    /**
     * Checks that next at the last item with loop on wraps to the first item.
     */
    public void testHandleNextEndOfPlaylistLoopOn()
    {
        fillABC();
        playlist.setLoopMode(true);
        playlist.play(2);
        call("handleNext");
        assertEquals(0, playlist.getCurrentIndex());
        assertEquals(a.describe(), nowPlaying().getText());
        assertEquals(0, list().getSelectedIndex());
    }


    /**
     * Checks that previous moves to the earlier item and updates now playing.
     */
    public void testHandlePrevious()
    {
        fillABC();
        playlist.play(2);
        call("handlePrevious");
        assertEquals(1, playlist.getCurrentIndex());
        assertEquals(b.describe(), nowPlaying().getText());
    }


    /**
     * Checks that previous on an empty playlist shows "The playlist is empty".
     */
    public void testHandlePreviousEmptyPlaylist()
    {
        call("handlePrevious");
        assertEquals("The playlist is empty", status().getText());
    }


    /**
     * Checks that previous at the first item with loop off shows "End of
     * playlist" and stays put.
     */
    public void testHandlePreviousStartOfPlaylist()
    {
        fillABC();
        playlist.play(0);
        call("handlePrevious");
        assertEquals("End of playlist", status().getText());
        assertEquals(0, playlist.getCurrentIndex());
    }


    /**
     * Checks that previous at the first item with loop on wraps to the last
     * item.
     */
    public void testHandlePreviousStartOfPlaylistLoopOn()
    {
        fillABC();
        playlist.setLoopMode(true);
        playlist.play(0);
        call("handlePrevious");
        assertEquals(2, playlist.getCurrentIndex());
        assertEquals(c.describe(), nowPlaying().getText());
    }


    /**
     * Checks that shuffle keeps the podcast in place and updates the list.
     */
    public void testHandleShuffle()
    {
        Podcast pod = new Podcast("Pod", "Host", 1, 600);
        playlist.add(a);
        playlist.add(pod);
        playlist.add(b);
        playlist.add(c);
        call("refreshList");
        call("handleShuffle");
        assertEquals(4, playlist.size());
        assertEquals(pod, playlist.getItems()[1]);
        assertEquals(4, model().size());
        assertEquals(pod.describe(), model().get(1));
    }


    /**
     * Checks that shuffle neither loses nor adds any items.
     */
    public void testHandleShuffleKeepsAllItems()
    {
        fillABC();
        call("handleShuffle");
        assertEquals(3, playlist.size());
        boolean foundA = false;
        boolean foundB = false;
        boolean foundC = false;
        for (Media m : playlist.getItems())
        {
            foundA = foundA || m.equals(a);
            foundB = foundB || m.equals(b);
            foundC = foundC || m.equals(c);
        }
        assertTrue(foundA);
        assertTrue(foundB);
        assertTrue(foundC);
    }


    /**
     * Checks that after a shuffle the first item of the new order is playing.
     */
    public void testHandleShuffleStartsAtFirstItem()
    {
        fillABC();
        playlist.play(1);
        call("updateNowPlaying");
        call("handleShuffle");
        assertEquals(0, playlist.getCurrentIndex());
        assertEquals(playlist.getItems()[0].describe(), nowPlaying().getText());
        assertEquals(0, list().getSelectedIndex());
    }


    /**
     * Checks that shuffling a one-item playlist leaves nothing playing.
     */
    public void testHandleShuffleOneItemPlaysNothing()
    {
        playlist.add(a);
        call("refreshList");
        call("handleShuffle");
        assertEquals(-1, playlist.getCurrentIndex());
        assertEquals("Nothing playing", nowPlaying().getText());
    }


    /**
     * Checks that shuffling an empty playlist does nothing and shows no
     * message.
     */
    public void testHandleShuffleEmptyPlaylist()
    {
        call("handleShuffle");
        assertEquals(0, model().size());
        assertEquals(0, playlist.size());
        assertEquals(" ", status().getText());
    }


    /**
     * Checks that turning the loop toggle on turns on the playlist's loop mode.
     */
    public void testHandleLoopToggle()
    {
        JToggleButton toggle = (JToggleButton)field("loopToggle");
        toggle.setSelected(true);
        call("handleLoopToggle");
        assertTrue(playlist.isLoopMode());
    }


    /**
     * Checks that turning the loop toggle off turns off the playlist's loop
     * mode.
     */
    public void testHandleLoopToggleOff()
    {
        JToggleButton toggle = (JToggleButton)field("loopToggle");
        toggle.setSelected(true);
        call("handleLoopToggle");
        toggle.setSelected(false);
        call("handleLoopToggle");
        assertFalse(playlist.isLoopMode());
    }


    /**
     * Checks that the now-playing label shows the current item.
     */
    public void testUpdateNowPlaying()
    {
        fillABC();
        playlist.play(1);
        call("updateNowPlaying");
        assertEquals(b.describe(), nowPlaying().getText());
    }


    /**
     * Checks that the now-playing label shows a podcast's description.
     */
    public void testUpdateNowPlayingPodcast()
    {
        playlist.add(new Podcast("Hardcore History", "Dan", 12, 3600));
        call("refreshList");
        playlist.play(0);
        call("updateNowPlaying");
        assertEquals(
            "Hardcore History #12 - Dan 60:00",
            nowPlaying().getText());
    }


    /**
     * Checks that the label reads "Nothing playing" when no item is current.
     */
    public void testUpdateNowPlayingNothingSelected()
    {
        // empty playlist
        call("updateNowPlaying");
        assertEquals("Nothing playing", nowPlaying().getText());

        // items present but none selected
        fillABC();
        call("updateNowPlaying");
        assertEquals("Nothing playing", nowPlaying().getText());
    }


    /**
     * Checks that showStatus puts the message in the status label.
     */
    public void testShowStatus()
    {
        invokeShowStatus("Added Yellow");
        assertEquals("Added Yellow", status().getText());
    }


    /**
     * Checks that a null or empty status message shows a single space.
     */
    public void testShowStatusNull()
    {
        // Rule in MusicPlayerGUI: null or empty shows a single space so the
        // label keeps its height, and nothing crashes.
        invokeShowStatus(null);
        assertEquals(" ", status().getText());
        invokeShowStatus("");
        assertEquals(" ", status().getText());
    }


    private void invokeShowStatus(String s)
    {
        try
        {
            Method m = MusicPlayerGUI.class
                .getDeclaredMethod("showStatus", String.class);
            m.setAccessible(true);
            m.invoke(gui, s);
        }
        catch (InvocationTargetException e)
        {
            throw new RuntimeException(e.getCause());
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
