import java.util.Optional;
import java.util.Random;

// -------------------------------------------------------------------------
/**
 * A playlist of songs and podcast episodes that tracks which item is current.
 * 
 * @author justisp
 * @version Oct 14, 2026
 */
public class Playlist
{
    // ~ Fields ................................................................
    private final LinkedChain<Media> chain; // items in play order
    private final Random random;            // used by shuffle
    private int currentIndex;               // position of current item, or -1
    private boolean loopMode;               // true if next/previous wrap around

    // ~ Constructors ..........................................................
    /**
     * Creates an empty playlist with nothing selected and loop mode off.
     * Shuffle uses a new Random.
     */
    public Playlist()
    {
        this(new Random());
    }


    /**
     * Creates an empty playlist with nothing selected and loop mode off.
     *
     * @param random
     *            the Random that shuffle uses
     * @throws IllegalArgumentException
     *             if random is null
     */
    public Playlist(Random random)
    {
        if (random == null)
        {
            throw new IllegalArgumentException("Random must not be null");
        }
        this.chain = new LinkedChain<Media>();
        this.random = random;
        this.currentIndex = -1;
        this.loopMode = false;
    }

    // ~Public Methods ........................................................


    /**
     * Adds an item to the end of the playlist. Duplicates are allowed.
     *
     * @param item
     *            the song or podcast episode to add
     * @throws IllegalArgumentException
     *             if item is null
     */
    public void add(Media item)
    {
        chain.addToEnd(item);
    }


    /**
     * Removes and returns the item at the given position, and fixes the current
     * position. Removing an item before the current one moves the current
     * position down by 1.
     *
     * @param index
     *            the zero-based position to remove
     * @return the removed item, or null if index is out of range
     */
    public Media removeAt(int index)
    {
        if (!isInRange(index))
        {
            return null;
        }
        Media removed = chain.removeAt(index);
        if (chain.isEmpty())
        {
            currentIndex = -1;
        }
        else if (index < currentIndex)
        {
            currentIndex--;
        }
        else if (index == currentIndex && currentIndex >= chain.size())
        {
            currentIndex = chain.size() - 1;
        }
        return removed;
    }


    /**
     * Removes the first item that equals the given item and fixes the current
     * position.
     *
     * @param item
     *            the item to remove
     * @return true if an item was removed or false if item is null, was not
     *             found, or the playlist is empty
     */
    public boolean remove(Media item)
    {
        int index = chain.indexOf(item);
        if (index < 0)
        {
            return false;
        }
        removeAt(index);
        return true;
    }


    /**
     * Moves to the next item and returns it. From nothing selected, it moves to
     * the first item. At the last item, loop mode on wraps to the first item,
     * and loop mode off stays put.
     *
     * @return the new current item or empty if the playlist is empty or the end
     *             was reached with loop mode off
     */
    public Optional<Media> next()
    {
        if (chain.isEmpty())
        {
            return Optional.empty();
        }
        int target = currentIndex + 1;
        if (target >= chain.size())
        {
            if (!loopMode)
            {
                return Optional.empty();
            }
            target = 0;
        }
        currentIndex = target;
        return Optional.of(chain.get(currentIndex));
    }


    /**
     * Moves to the previous item and returns it. At the first item, loop mode
     * on wraps to the last item, and loop mode off stays put.
     *
     * @return the new current item or empty if the playlist is empty, nothing
     *             is selected, or the start was reached with loop mode off
     */
    public Optional<Media> previous()
    {
        if (chain.isEmpty() || currentIndex < 0)
        {
            return Optional.empty();
        }
        int target = currentIndex - 1;
        if (target < 0)
        {
            if (!loopMode)
            {
                return Optional.empty();
            }
            target = chain.size() - 1;
        }
        currentIndex = target;
        return Optional.of(chain.get(currentIndex));
    }


    /**
     * Gets the current item without moving.
     *
     * @return the current item or empty if nothing is selected or the playlist
     *             is empty
     */
    public Optional<Media> getCurrent()
    {
        if (!isInRange(currentIndex))
        {
            return Optional.empty();
        }
        return Optional.of(chain.get(currentIndex));
    }


    /**
     * Makes the item at the given position current.
     *
     * @param index
     *            the zero-based position to play
     * @return the now current item or empty if index is out of range (the
     *             current position is left unchanged)
     */
    public Optional<Media> play(int index)
    {
        if (!isInRange(index))
        {
            return Optional.empty();
        }
        currentIndex = index;
        return Optional.of(chain.get(currentIndex));
    }


    /**
     * Makes the first item whose title matches the given title current. Case is
     * ignored.
     *
     * @param title
     *            the title to look for
     * @return the now current item or empty if title is null, nothing matches,
     *             or the playlist is empty
     */
    public Optional<Media> skipTo(String title)
    {
        if (title == null)
        {
            return Optional.empty();
        }
        Media[] items = getItems();
        for (int i = 0; i < items.length; i++)
        {
            if (items[i].getTitle().equalsIgnoreCase(title))
            {
                currentIndex = i;
                return Optional.of(items[i]);
            }
        }
        return Optional.empty();
    }
}
