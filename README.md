# Media Player (Group 95)

A playlist app with a Swing window. It stores songs and podcast episodes, and lets the user play, skip, loop, and shuffle them. Songs can be added one at a time or as a whole album, and podcast episodes can be added one at a time or as a whole series.

## How to compile and run

### Run the program

1. Open the project in your IDE (for example Eclipse) with all the `.java` files in the default package. Java 8 or newer is needed.
2. Open `MusicPlayer.java` and run it as a Java application (right-click, then Run As, then Java Application).
3. The window opens with an empty playlist. Use the form on the left to add a song or podcast, or the **Add album** and **Add series** buttons to add a group. In the album dialog, write one song per line as `title, seconds`. In the series dialog, write one episode per line as `episode number, seconds`.

### Run the tests with coverage

1. Make sure the CS 2114 `student` library and JUnit are on the project's build path.
2. Select all the test classes (or the folder that holds them) in the project explorer.
3. Right-click and choose Coverage As, then JUnit Test (in Eclipse this uses EclEmma). The coverage view shows how much of each class the tests ran.

Two tests in `MusicPlayerTest` open the real window, so run the tests on a computer with a display, not a headless server.

## Attribution

| File | Who | How |
|---|---|---|
| `Media.java` | Justis | Wrote |
| `Playlist.java` | Justis | Wrote |
| `Song.java` | Leo | Wrote |
| `Album.java` | Leo | Wrote |
| `MusicPlayerGUI.java` | Leo | Assisted with GenAI (including the album and series dialogs and handlers) |
| `Podcast.java` | Krish | Wrote |
| `PodcastSeries.java` | Krish | Wrote |
| `LinkedChain.java` | CS 2114 staff, team | Provided; team added `removeAt` and `indexOf` |
| `MusicPlayer.java` | Team | Wrote and Assisted with GenAI |
| `SongTest.java` | Shea | Wrote and tested |
| `AlbumTest.java` | Leo | Wrote and tested |
| `PodcastTest.java` | Shea | Wrote and tested |
| `PodcastSeriesTest.java` | Krish | Wrote and tested |
| `LinkedChainTest.java` | Shea | Wrote and tested |
| `PlaylistTest.java` | Shea | Wrote and tested |
| `MusicPlayerTest.java` | Shea | Wrote and tested |
| `MusicPlayerGUITest.java` | Shea | Wrote and tested |

**GenAI use:** We used GenAI to research the open questions about loop mode and the Swing GUI, and to help add the album and podcast series buttons, dialogs and handlers to the GUI and the matching GUI tests. Each class owner reviewed and tested the code for their own class, and we checked the Big-O answers, the `currentIndex` logic after a removal, and the test expectations by hand.
