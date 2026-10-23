import student.TestCase;

/**
 * Tests for LinkedChain.
 */
public class LinkedChainTest
    extends TestCase
{
    private LinkedChain<String> chain;

    public void setUp()
    {
        chain = new LinkedChain<String>();
    }


    /** Builds the chain in the given order using addToEnd. */
    private void fill(String... items)
    {
        for (String s : items)
        {
            chain.addToEnd(s);
        }
    }


    /**
     * Checks that add puts the new entry at the front of the chain.
     */
    public void testAdd()
    {
        fill("B", "C");
        assertTrue(chain.add("A"));
        assertEquals("[A, B, C]", chain.toString());
    }


    /**
     * Checks that add rejects null with an IllegalArgumentException and leaves
     * the chain unchanged.
     */
    public void testAddNull()
    {
        fill("A");
        Exception exception = null;
        try
        {
            chain.add(null);
        }
        catch (IllegalArgumentException e)
        {
            exception = e;
        }
        assertNotNull(exception);
        assertEquals("[A]", chain.toString());
        assertEquals(1, chain.size());
    }


    /**
     * Checks that addToEnd puts the new entry at the end of the chain.
     */
    public void testAddToEnd()
    {
        fill("A", "B");
        chain.addToEnd("C");
        assertEquals("[A, B, C]", chain.toString());
        assertEquals(3, chain.size());
    }


    /**
     * Checks that addToEnd rejects null with an IllegalArgumentException and
     * leaves the chain unchanged.
     */
    public void testAddToEndNull()
    {
        fill("A", "B");
        Exception exception = null;
        try
        {
            chain.addToEnd(null);
        }
        catch (IllegalArgumentException e)
        {
            exception = e;
        }
        assertNotNull(exception);
        assertEquals("[A, B]", chain.toString());
    }


    /**
     * Checks that remove returns and removes the first entry.
     */
    public void testRemove()
    {
        fill("A", "B", "C");
        assertEquals("A", chain.remove());
        assertEquals("[B, C]", chain.toString());
    }


    /**
     * Checks that remove returns null on an empty chain.
     */
    public void testRemoveEmptyChain()
    {
        assertNull(chain.remove());
        assertEquals(0, chain.size());
    }


    /**
     * Checks that remove(entry) takes out only the first matching entry.
     */
    public void testRemoveEntry()
    {
        fill("A", "B", "A");
        assertTrue(chain.remove("A"));
        assertEquals("[B, A]", chain.toString());
    }


    /**
     * Checks that remove(entry) returns false and changes nothing when the
     * entry is missing.
     */
    public void testRemoveEntryNotFound()
    {
        fill("A", "B");
        assertFalse(chain.remove("Z"));
        assertEquals("[A, B]", chain.toString());
    }


    /**
     * Checks that remove(entry) returns false and changes nothing for null.
     */
    public void testRemoveEntryNull()
    {
        fill("A", "B");
        assertFalse(chain.remove(null));
        assertEquals("[A, B]", chain.toString());
    }


    /**
     * Checks that remove(entry) returns false on an empty chain.
     */
    public void testRemoveEntryEmptyChain()
    {
        assertFalse(chain.remove("A"));
        assertEquals(0, chain.size());
    }


    /**
     * Checks that an entry in the middle can be removed and the chain stays
     * linked.
     */
    public void testRemoveEntryMiddle()
    {
        fill("A", "B", "C");
        assertTrue(chain.remove("B"));
        assertEquals("[A, C]", chain.toString());
        assertEquals(2, chain.size());
    }


    /**
     * Checks that the last entry can be removed and the chain ends at the new
     * last entry.
     */
    public void testRemoveEntryLast()
    {
        fill("A", "B", "C");
        assertTrue(chain.remove("C"));
        assertEquals("[A, B]", chain.toString());
        assertEquals(2, chain.size());
    }


    /**
     * Checks that clear removes every entry.
     */
    public void testClear()
    {
        fill("A", "B", "C");
        chain.clear();
        assertEquals(0, chain.size());
        assertTrue(chain.isEmpty());
        assertEquals("[]", chain.toString());
    }


    /**
     * Checks that clear on an empty chain does nothing.
     */
    public void testClearEmptyChain()
    {
        chain.clear();
        assertTrue(chain.isEmpty());
        assertEquals(0, chain.size());
    }


    /**
     * Checks that get returns the entry at the given position without removing
     * it.
     */
    public void testGet()
    {
        fill("A", "B", "C");
        assertEquals("B", chain.get(1));
        assertEquals("[A, B, C]", chain.toString());
        assertEquals(3, chain.size());
    }


    /**
     * Checks that get throws an IndexOutOfBoundsException for a negative index,
     * an index past the end, or an empty chain.
     */
    public void testGetInvalidIndex()
    {
        fill("A", "B", "C");
        int[] bad = { -1, 3 };
        for (int i : bad)
        {
            Exception exception = null;
            try
            {
                chain.get(i);
            }
            catch (IndexOutOfBoundsException e)
            {
                exception = e;
            }
            assertNotNull(exception);
        }
        LinkedChain<String> empty = new LinkedChain<String>();
        Exception exception = null;
        try
        {
            empty.get(0);
        }
        catch (IndexOutOfBoundsException e)
        {
            exception = e;
        }
        assertNotNull(exception);
    }


    /**
     * Checks that contains is true for an entry in the chain.
     */
    public void testContains()
    {
        fill("A", "B", "C");
        assertTrue(chain.contains("B"));
    }


    /**
     * Checks that contains is false for null.
     */
    public void testContainsNull()
    {
        fill("A", "B", "C");
        assertFalse(chain.contains(null));
    }


    /**
     * Checks that contains is false on an empty chain.
     */
    public void testContainsEmptyChain()
    {
        assertFalse(chain.contains("A"));
    }


    /**
     * Checks that count returns how many times an entry appears.
     */
    public void testCount()
    {
        fill("A", "B", "A");
        assertEquals(2, chain.count("A"));
    }


    /**
     * Checks that count returns 0 for null.
     */
    public void testCountNull()
    {
        fill("A", "B", "A");
        assertEquals(0, chain.count(null));
    }


    /**
     * Checks that count returns 0 on an empty chain.
     */
    public void testCountEmptyChain()
    {
        assertEquals(0, chain.count("A"));
    }


    /**
     * Checks that size returns the number of entries.
     */
    public void testSize()
    {
        fill("A", "B", "C");
        assertEquals(3, chain.size());
    }


    /**
     * Checks that size is 0 for an empty chain.
     */
    public void testSizeEmptyChain()
    {
        assertEquals(0, chain.size());
    }


    /**
     * Checks that a new chain is empty.
     */
    public void testIsEmpty()
    {
        assertTrue(chain.isEmpty());
    }


    /**
     * Checks that a chain with an entry is not empty.
     */
    public void testIsEmptyNonEmptyChain()
    {
        fill("A");
        assertFalse(chain.isEmpty());
    }


    /**
     * Checks that toArray fills the array with the entries, first entry first.
     */
    public void testToArray()
    {
        fill("A", "B", "C");
        String[] arr = new String[3];
        String[] result = chain.toArray(arr);
        assertEquals(3, result.length);
        assertEquals("A", result[0]);
        assertEquals("B", result[1]);
        assertEquals("C", result[2]);
    }


    /**
     * Checks that toArray rejects a null array with an
     * IllegalArgumentException.
     */
    public void testToArrayNullArray()
    {
        fill("A", "B", "C");
        Exception exception = null;
        try
        {
            chain.toArray(null);
        }
        catch (IllegalArgumentException e)
        {
            exception = e;
        }
        assertNotNull(exception);
    }


    /**
     * Checks that toArray rejects an array that is too small with an
     * IllegalArgumentException.
     */
    public void testToArrayTooSmallArray()
    {
        fill("A", "B", "C");
        Exception exception = null;
        try
        {
            chain.toArray(new String[2]);
        }
        catch (IllegalArgumentException e)
        {
            exception = e;
        }
        assertNotNull(exception);
    }


    /**
     * Checks that toArray leaves the array unchanged for an empty chain.
     */
    public void testToArrayEmptyChain()
    {
        String[] arr = { "x", "y" };
        String[] result = chain.toArray(arr);
        assertEquals("x", result[0]);
        assertEquals("y", result[1]);
    }


    /**
     * Checks that toString lists the entries in order inside square brackets.
     */
    public void testToString()
    {
        fill("A", "B", "C");
        assertEquals("[A, B, C]", chain.toString());
    }


    /**
     * Checks that toString shows "[]" for an empty chain.
     */
    public void testToStringEmptyChain()
    {
        assertEquals("[]", chain.toString());
    }


    /**
     * Checks that removeAt returns and removes the entry at the given position.
     */
    public void testRemoveAt()
    {
        fill("A", "B", "C");
        assertEquals("B", chain.removeAt(1));
        assertEquals("[A, C]", chain.toString());
    }


    /**
     * Checks that removeAt(0) removes the first entry.
     */
    public void testRemoveAtFirstIndex()
    {
        fill("A", "B", "C");
        assertEquals("A", chain.removeAt(0));
        assertEquals("[B, C]", chain.toString());
    }


    /**
     * Checks that removeAt throws an IndexOutOfBoundsException and changes
     * nothing for a bad index.
     */
    public void testRemoveAtInvalidIndex()
    {
        fill("A", "B", "C");
        int[] bad = { -1, 3 };
        for (int i : bad)
        {
            Exception exception = null;
            try
            {
                chain.removeAt(i);
            }
            catch (IndexOutOfBoundsException e)
            {
                exception = e;
            }
            assertNotNull(exception);
            assertEquals("[A, B, C]", chain.toString());
        }
        LinkedChain<String> empty = new LinkedChain<String>();
        Exception exception = null;
        try
        {
            empty.removeAt(0);
        }
        catch (IndexOutOfBoundsException e)
        {
            exception = e;
        }
        assertNotNull(exception);
        assertEquals(0, empty.size());
    }


    /**
     * Checks that indexOf returns the position of an entry.
     */
    public void testIndexOf()
    {
        fill("A", "B", "C");
        assertEquals(2, chain.indexOf("C"));
    }


    /**
     * Checks that indexOf returns -1 when the entry is missing.
     */
    public void testIndexOfNotFound()
    {
        fill("A", "B", "C");
        assertEquals(-1, chain.indexOf("Z"));
    }


    /**
     * Checks that indexOf returns -1 for null.
     */
    public void testIndexOfNull()
    {
        fill("A", "B", "C");
        assertEquals(-1, chain.indexOf(null));
    }


    /**
     * Checks that indexOf returns -1 on an empty chain.
     */
    public void testIndexOfEmptyChain()
    {
        assertEquals(-1, chain.indexOf("A"));
    }
}
