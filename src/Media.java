// -------------------------------------------------------------------------
/**
 * Interface for any media that can be played in the playlist (Song and Podcast)
 * Both Song and Podcast implement it so a LinkedChain can hold them both.
 * 
 * @author justisp
 * @version Oct 14, 2026
 */
public interface Media
{
    // ~Public Methods ........................................................

    // ----------------------------------------------------------
    /**
     * Gets the title.
     *
     * @return the title
     */
    String getTitle();


    // ----------------------------------------------------------
    /**
     * Gets the length.
     *
     * @return the length in seconds
     */
    int getLength();


    // ----------------------------------------------------------
    /**
     * Describes this media on one line.
     *
     * @return the display line
     */
    String describe();


    // ----------------------------------------------------------
    /**
     * Tells whether shuffle may move this item. Songs can be moved and podcast
     * episodes keep their positions.
     *
     * @return true if shuffle may move this item, or false if not
     */
    boolean isShuffleable();
}
