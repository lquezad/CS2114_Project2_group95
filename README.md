# Music Player (Group 95)

A playlist app with a Swing window. A generic `LinkedChain<Media>` stores songs and podcast episodes. `Playlist` handles next, previous, skip, loop and shuffle. `MusicPlayerGUI` is the window.

## Reflection

### What changed from our specification?

The design held up: `LinkedChain` stores, `Playlist` has the playback logic, the GUI has the window code, and `Song`/`Podcast` guard their own data. Most changes are in the method signatures.

| Area | Spec said | We built | Why |
|---|---|---|---|
| Add | `addSong(Media)` returns `boolean` (true if duplicate) | `add(Media)` returns `void` | Duplicates are allowed, so the GUI never needed the flag. |
| Remove by position | `removeAt` throws `IndexOutOfBoundsException` | Returns `null` on a bad index | Matches our "normal user situation, not a bug" rule. |
| Removing the current item | `currentIndex` becomes -1 | The next item becomes current (or the last one if we removed the end) | Less jarring for the user. Only an emptied playlist resets to -1. |
| Reading the list | `getAll()` returns `List<Media>` | `getItems()` returns `Media[]` | `LinkedChain` already has `toArray`. |
| Current / next / previous | Return `Media` or `null` | Return `Optional<Media>` | No null checks in the GUI. |
| Skip | `skipTo(int)` and `skipToTitle(String)` | `play(int)` and `skipTo(String)` | Renamed for clarity. |
| Shuffle | Returns `boolean`, shuffles everything | Returns `void`; podcasts keep their positions | Episodes are meant to be heard in order. This needed `Media.isShuffleable()`. |
| Shuffle and Random | `Random` created inside | Extra constructor `Playlist(Random)` | Lets tests use a seeded `Random`. |
| Extra `Playlist` methods | Not listed | `remove(Media)`, `getCurrentIndex()` | The GUI and tests needed them. |
| `Media` | `getDisplayString()` | `describe()` plus `isShuffleable()` | Renamed. `isShuffleable()` is new. |
| `Song` / `Podcast` | Constructors and `equals` | Also `hashCode`, and `Song` has `toString`. Blank host becomes "Unknown" | Needed so equal items behave in a chain. |
| `MusicPlayer` | Adds sample data and opens the window | `main` opens the GUI with an empty playlist. Takes a `Playlist` and `Scanner`, with console helpers `readPositiveInt` and `readNonBlank` | The helpers re-prompt until the input is valid. The GUI does not use them yet. |
| GUI | Songs only; handlers named `onAdd`, `onPlay`, etc. | Add form works for **songs and podcasts**. Handlers are `handleAdd`, `handlePlay`, etc., and are package-private | Podcasts made the app more useful. Package-private handlers let tests call them. |
| Where validation lives | Constructors and chain guard their data. GUI catches `NumberFormatException` | Same, but the GUI turns bad numbers into `IllegalArgumentException` with a message and shows it in the status bar | One catch block covers both number errors and constructor errors. |
| Test cases | One normal and one bad case per method | Plus edge cases (duplicates, stale selection, loop on and off, seeded shuffle) | Tests grew as bugs showed up. |

### What changed in the LinkedChain?

- **Added `removeAt(int)` and `indexOf(T)`.** `Playlist` needs them to remove by row and to find an item for `remove(Media)`.
- **Used `addToEnd`** for adding, since the playlist order is the play order.
- **We did not add the `toList()`, `replaceAll()` or `get()` changes from the spec.** The provided `get`, `toArray`, `clear` and `add` already cover them. Shuffle copies to an array with `toArray`, shuffles it, calls `clear()`, then rebuilds the chain.
- Everything else is unchanged. The chain still holds no playback logic.

### What happened to our unknowns?

- **Loop mode: figured out.** We used the flag-based design. The chain stays straight and `Playlist.next()` and `previous()` wrap around when `loopMode` is on. It is simple and safe when removing items.
- **Interactive GUI: mostly figured out.** We used a `JFrame` with a `JList`, a custom row renderer, button handlers and a status label, all started on the Swing thread with `SwingUtilities.invokeLater`.
- **Still open:**
  - Real audio playback. We simulate playback and only show what is current.
  - Whether `next()` and `previous()` should be O(1) instead of O(n) with a cursor in the chain.

### What doesn't work yet?

- **Previous at the start of the playlist (loop off)** shows "End of playlist" instead of "Start of playlist".
- **No duplicate notice** when the same item is added twice. It adds the item, but the GUI says nothing.
- **No "Nothing to shuffle" message.** Shuffling one item or none does nothing and says nothing.
- **Shuffle starts playing the first item.** The spec said "Nothing playing" afterward.
- **No sample data** when the program starts. The playlist is empty.
- **Console helpers are unused.** `readPositiveInt` and `readNonBlank` are only called by their tests.
- **`Podcast` is not fully consistent with `Song`.** It does not trim the title or add a `toString`.
- **Not yet tested:** `Podcast.toString` and a playlist made only of podcasts under shuffle.
- **No tests against a stub chain.** We tested `Playlist` with the real `LinkedChain` only.

## Next Steps

### 1. Finish the MVP (in this order)

1. Fix the "Start of playlist" message.
2. Add the duplicate notice and the "Nothing to shuffle" message.
3. Decide what shuffle shows afterward and make the GUI and the docs agree.
4. Make `Podcast` match `Song` (trim the title, add `toString`).
5. Add sample songs and a podcast when the program starts.
6. Use the console helpers in `MusicPlayer` or remove them.
7. Final pass: Javadoc, style, and a full test run.

### 2. Stretch goals

- **Committed:** the GUI, which is built. Remaining work is polish.
- **Dropped:** multiple playlists, merge, search, recently played, favorites.
- **Optional only if time allows:** an O(1) current-item cursor.

### 3. New tests needed

- Previous at the start with loop off and on, checking the status text.
- Duplicate add shows the notice. Shuffle with 0 or 1 items shows its message.
- A shuffle with only podcasts keeps the order.
- `Podcast` title trimming and `toString`.
- `remove(Media)` when the item is before, at, or after the current item.
- Sample data on startup (`MusicPlayerTest`).

### 4. Who does what

| Item | Owner |
|---|---|
| Playlist and Media | Justis |
| GUI and Song | Leo |
| Podcast | Krish |
| All new tests and the final test run | Shea |

## Attribution

| File | Who | How |
|---|---|---|
| `Media.java` | Justis | Wrote |
| `Playlist.java` | Justis | Wrote |
| `Song.java` | Leo | Wrote |
| `MusicPlayerGUI.java` | Leo | Assisted with GenAI |
| `Podcast.java` | Krish | Wrote |
| `LinkedChain.java` | CS 2114 staff, team | Provided; team added `removeAt` and `indexOf` |
| `MusicPlayer.java` | Team | Wrote and Assisted with GenAI |
| `SongTest.java` | Shea | Wrote and tested |
| `PodcastTest.java` | Shea | Wrote and tested |
| `LinkedChainTest.java` | Shea | Wrote and tested |
| `PlaylistTest.java` | Shea | Wrote and tested |
| `MusicPlayerTest.java` | Shea | Wrote and tested |
| `MusicPlayerGUITest.java` | Shea | Wrote and tested |

**GenAI use:** We used GenAI to research the open questions about loop mode and the Swing GUI. Each class owner reviewed and tested the code for their own class, and we checked the Big-O answers, the `currentIndex` logic after a removal, and the test expectations by hand.
