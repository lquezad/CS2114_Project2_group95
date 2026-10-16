import java.lang.reflect.*;
import javax.swing.*;
import student.TestCase;

/**
 * Tests for MusicPlayerGUI.
 *
 */
public class MusicPlayerGUITest extends TestCase
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


    private void fillSongForm(String title, String artist, String album,
        String length)
    {
        typeBox().setSelectedIndex(0); // assumes index 0 is "Song"
        text("titleField").setText(title);
        text("artistOrHostField").setText(artist);
        text("albumField").setText(album);
        text("lengthField").setText(length);
    }


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


    public void testRefreshListIgnoresIndexBeyondList()
    {
        // A Playlist that reports a current index past the end of the list;
        // refreshList must not try to select a row that does not exist.
        Playlist odd = new Playlist()
        {
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


    public void testRefreshListSelectsCurrentItem()
    {
        fillABC();
        playlist.play(1);
        call("refreshList");
        assertEquals(1, list().getSelectedIndex());
    }


    public void testRefreshList()
    {
        fillABC();
        assertEquals(3, model().size());
        assertEquals(a.describe(), model().get(0));
        assertEquals(b.describe(), model().get(1));
        assertEquals(c.describe(), model().get(2));
    }


    public void testRefreshListEmptyPlaylist()
    {
        call("refreshList");
        assertEquals(0, model().size());
    }


    public void testHandleAdd()
    {
        fillSongForm("Yellow", "Coldplay", "Parachutes", "266");
        call("handleAdd");
        assertEquals(1, playlist.size());
        assertEquals(new Song("Yellow", "Coldplay", "Parachutes", 266),
            playlist.getItems()[0]);
        assertEquals(1, model().size());
    }


    public void testHandleAddAppendsToEnd()
    {
        playlist.add(b);
        call("refreshList");
        fillSongForm("Yellow", "Coldplay", "Parachutes", "266");
        call("handleAdd");
        assertEquals(2, playlist.size());
        assertEquals(b, playlist.getItems()[0]);
        assertEquals(new Song("Yellow", "Coldplay", "Parachutes", 266),
            playlist.getItems()[1]);
    }


    public void testHandleAddShowsStatus()
    {
        fillSongForm("Yellow", "Coldplay", "Parachutes", "266");
        call("handleAdd");
        assertEquals("Added Yellow", status().getText());
    }


    public void testHandleAddBlankArtistAndAlbum()
    {
        fillSongForm("Yellow", "", "   ", "266");
        call("handleAdd");
        assertEquals(1, playlist.size());
        Song added = (Song)playlist.getItems()[0];
        assertEquals("Unknown", added.getArtist());
        assertEquals("Unknown", added.getAlbum());
    }


    public void testHandleAddClearsFormOnSuccess()
    {
        fillSongForm("Yellow", "Coldplay", "Parachutes", "266");
        call("handleAdd");
        assertEquals("", text("titleField").getText());
        assertEquals("", text("artistOrHostField").getText());
        assertEquals("", text("albumField").getText());
        assertEquals("", text("lengthField").getText());
    }


    public void testHandleAddKeepsFormOnError()
    {
        fillSongForm("Yellow", "Coldplay", "Parachutes", "abc");
        call("handleAdd");
        assertEquals("Yellow", text("titleField").getText());
        assertEquals("abc", text("lengthField").getText());
    }


    public void testHandleAddPodcast()
    {
        typeBox().setSelectedIndex(1); // assumes index 1 is "Podcast"
        text("titleField").setText("Hardcore History");
        text("artistOrHostField").setText("Dan");
        text("episodeField").setText("12");
        text("lengthField").setText("3600");
        call("handleAdd");
        assertEquals(1, playlist.size());
        assertEquals(new Podcast("Hardcore History", "Dan", 12, 3600),
            playlist.getItems()[0]);
    }


    public void testHandleAddNonNumericEpisode()
    {
        typeBox().setSelectedIndex(1);
        text("titleField").setText("Hardcore History");
        text("artistOrHostField").setText("Dan");
        text("episodeField").setText("x");
        text("lengthField").setText("3600");
        call("handleAdd");
        assertEquals("Episode number must be a whole number greater than 0",
            status().getText());
        assertEquals(0, playlist.size());
    }


    public void testHandleAddZeroEpisode()
    {
        typeBox().setSelectedIndex(1);
        text("titleField").setText("Hardcore History");
        text("artistOrHostField").setText("Dan");
        text("episodeField").setText("0");
        text("lengthField").setText("3600");
        call("handleAdd");
        assertEquals("Episode number must be greater than 0",
            status().getText());
        assertEquals(0, playlist.size());
    }


    public void testHandleAddBlankTitle()
    {
        fillSongForm("", "Coldplay", "Parachutes", "266");
        call("handleAdd");
        assertEquals("Title must not be blank", status().getText());
        assertEquals(0, playlist.size());
    }


    public void testHandleAddNonNumericLength()
    {
        fillSongForm("Yellow", "Coldplay", "Parachutes", "abc");
        call("handleAdd");
        assertEquals("Length must be a whole number greater than 0",
            status().getText());
        assertEquals(0, playlist.size());
    }


    public void testHandleAddZeroLength()
    {
        fillSongForm("Yellow", "Coldplay", "Parachutes", "0");
        call("handleAdd");
        assertEquals("Length must be greater than 0", status().getText());
        assertEquals(0, playlist.size());
    }


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


    public void testHandleRemoveShowsStatus()
    {
        fillABC();
        list().setSelectedIndex(0);
        call("handleRemove");
        assertEquals("Removed A", status().getText());
    }


    public void testHandleRemoveNoSelection()
    {
        fillABC();
        list().clearSelection();
        call("handleRemove");
        assertEquals("Select an item first", status().getText());
        assertEquals(3, playlist.size());
    }


    public void testHandlePlay()
    {
        fillABC();
        list().setSelectedIndex(2);
        call("handlePlay");
        assertEquals(2, playlist.getCurrentIndex());
        assertEquals(c.describe(), nowPlaying().getText());
    }


    public void testHandlePlayNoSelection()
    {
        fillABC();
        list().clearSelection();
        call("handlePlay");
        assertEquals("Select an item first", status().getText());
        assertEquals(-1, playlist.getCurrentIndex());
    }


    public void testHandleNext()
    {
        fillABC();
        playlist.play(0);
        call("handleNext");
        assertEquals(1, playlist.getCurrentIndex());
        assertEquals(b.describe(), nowPlaying().getText());
    }


    public void testHandleNextEmptyPlaylist()
    {
        call("handleNext");
        assertEquals("The playlist is empty", status().getText());
    }


    public void testHandleNextEndOfPlaylist()
    {
        fillABC();
        playlist.play(2);
        call("handleNext");
        assertEquals("End of playlist", status().getText());
        assertEquals(2, playlist.getCurrentIndex());
    }


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


    public void testHandlePrevious()
    {
        fillABC();
        playlist.play(2);
        call("handlePrevious");
        assertEquals(1, playlist.getCurrentIndex());
        assertEquals(b.describe(), nowPlaying().getText());
    }


    public void testHandlePreviousEmptyPlaylist()
    {
        call("handlePrevious");
        assertEquals("The playlist is empty", status().getText());
    }


    public void testHandlePreviousStartOfPlaylist()
    {
        fillABC();
        playlist.play(0);
        call("handlePrevious");
        assertEquals("End of playlist", status().getText());
        assertEquals(0, playlist.getCurrentIndex());
    }


    public void testHandlePreviousStartOfPlaylistLoopOn()
    {
        fillABC();
        playlist.setLoopMode(true);
        playlist.play(0);
        call("handlePrevious");
        assertEquals(2, playlist.getCurrentIndex());
        assertEquals(c.describe(), nowPlaying().getText());
    }


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


    public void testHandleShuffleStartsAtFirstItem()
    {
        fillABC();
        playlist.play(1);
        call("updateNowPlaying");
        call("handleShuffle");
        assertEquals(0, playlist.getCurrentIndex());
        assertEquals(playlist.getItems()[0].describe(),
            nowPlaying().getText());
        assertEquals(0, list().getSelectedIndex());
    }


    public void testHandleShuffleOneItemPlaysNothing()
    {
        playlist.add(a);
        call("refreshList");
        call("handleShuffle");
        assertEquals(-1, playlist.getCurrentIndex());
        assertEquals("Nothing playing", nowPlaying().getText());
    }


    public void testHandleShuffleEmptyPlaylist()
    {
        call("handleShuffle");
        assertEquals(0, model().size());
        assertEquals(0, playlist.size());
        assertEquals(" ", status().getText());
    }


    public void testHandleLoopToggle()
    {
        JToggleButton toggle = (JToggleButton)field("loopToggle");
        toggle.setSelected(true);
        call("handleLoopToggle");
        assertTrue(playlist.isLoopMode());
    }


    public void testHandleLoopToggleOff()
    {
        JToggleButton toggle = (JToggleButton)field("loopToggle");
        toggle.setSelected(true);
        call("handleLoopToggle");
        toggle.setSelected(false);
        call("handleLoopToggle");
        assertFalse(playlist.isLoopMode());
    }


    public void testUpdateNowPlaying()
    {
        fillABC();
        playlist.play(1);
        call("updateNowPlaying");
        assertEquals(b.describe(), nowPlaying().getText());
    }


    public void testUpdateNowPlayingPodcast()
    {
        playlist.add(new Podcast("Hardcore History", "Dan", 12, 3600));
        call("refreshList");
        playlist.play(0);
        call("updateNowPlaying");
        assertEquals("Hardcore History #12 - Dan 60:00",
            nowPlaying().getText());
    }


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


    public void testShowStatus()
    {
        invokeShowStatus("Added Yellow");
        assertEquals("Added Yellow", status().getText());
    }


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
            Method m = MusicPlayerGUI.class.getDeclaredMethod("showStatus",
                String.class);
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