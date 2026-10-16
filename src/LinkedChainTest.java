package project2;

import student.TestCase;

/**
 * Tests for LinkedChain&lt;T&gt;. Chains hold Strings named "A", "B", "C".
 */
public class LinkedChainTest extends TestCase
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


    public void testAdd()
    {
        fill("B", "C");
        assertTrue(chain.add("A"));
        assertEquals("[A, B, C]", chain.toString());
    }


    public void testAddNull()
    {
        fill("A");
        try
        {
            chain.add(null);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals("[A]", chain.toString());
            assertEquals(1, chain.size());
        }
    }


    public void testAddToEnd()
    {
        fill("A", "B");
        chain.addToEnd("C");
        assertEquals("[A, B, C]", chain.toString());
        assertEquals(3, chain.size());
    }


    public void testAddToEndNull()
    {
        fill("A", "B");
        try
        {
            chain.addToEnd(null);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals("[A, B]", chain.toString());
        }
    }


    public void testRemove()
    {
        fill("A", "B", "C");
        assertEquals("A", chain.remove());
        assertEquals("[B, C]", chain.toString());
    }


    public void testRemoveEmptyChain()
    {
        assertNull(chain.remove());
        assertEquals(0, chain.size());
    }


    public void testRemoveEntry()
    {
        fill("A", "B", "A");
        assertTrue(chain.remove("A"));
        assertEquals("[B, A]", chain.toString());
    }


    public void testRemoveEntryNotFound()
    {
        fill("A", "B");
        assertFalse(chain.remove("Z"));
        assertEquals("[A, B]", chain.toString());
    }


    public void testRemoveEntryNull()
    {
        fill("A", "B");
        assertFalse(chain.remove(null));
        assertEquals("[A, B]", chain.toString());
    }


    public void testRemoveEntryEmptyChain()
    {
        assertFalse(chain.remove("A"));
        assertEquals(0, chain.size());
    }


    public void testRemoveEntryMiddle()
    {
        fill("A", "B", "C");
        assertTrue(chain.remove("B"));
        assertEquals("[A, C]", chain.toString());
        assertEquals(2, chain.size());
    }


    public void testRemoveEntryLast()
    {
        fill("A", "B", "C");
        assertTrue(chain.remove("C"));
        assertEquals("[A, B]", chain.toString());
        assertEquals(2, chain.size());
    }


    public void testClear()
    {
        fill("A", "B", "C");
        chain.clear();
        assertEquals(0, chain.size());
        assertTrue(chain.isEmpty());
        assertEquals("[]", chain.toString());
    }


    public void testClearEmptyChain()
    {
        chain.clear();
        assertTrue(chain.isEmpty());
        assertEquals(0, chain.size());
    }


    public void testGet()
    {
        fill("A", "B", "C");
        assertEquals("B", chain.get(1));
        assertEquals("[A, B, C]", chain.toString());
        assertEquals(3, chain.size());
    }


    public void testGetInvalidIndex()
    {
        fill("A", "B", "C");
        int[] bad = { -1, 3 };
        for (int i : bad)
        {
            try
            {
                chain.get(i);
                fail("Expected IndexOutOfBoundsException for index " + i);
            }
            catch (IndexOutOfBoundsException e)
            {
                // expected
            }
        }
        LinkedChain<String> empty = new LinkedChain<String>();
        try
        {
            empty.get(0);
            fail("Expected IndexOutOfBoundsException on empty chain");
        }
        catch (IndexOutOfBoundsException e)
        {
            // expected
        }
    }


    public void testContains()
    {
        fill("A", "B", "C");
        assertTrue(chain.contains("B"));
    }


    public void testContainsNull()
    {
        fill("A", "B", "C");
        assertFalse(chain.contains(null));
    }


    public void testContainsEmptyChain()
    {
        assertFalse(chain.contains("A"));
    }


    public void testCount()
    {
        fill("A", "B", "A");
        assertEquals(2, chain.count("A"));
    }


    public void testCountNull()
    {
        fill("A", "B", "A");
        assertEquals(0, chain.count(null));
    }


    public void testCountEmptyChain()
    {
        assertEquals(0, chain.count("A"));
    }


    public void testSize()
    {
        fill("A", "B", "C");
        assertEquals(3, chain.size());
    }


    public void testSizeEmptyChain()
    {
        assertEquals(0, chain.size());
    }


    public void testIsEmpty()
    {
        assertTrue(chain.isEmpty());
    }


    public void testIsEmptyNonEmptyChain()
    {
        fill("A");
        assertFalse(chain.isEmpty());
    }


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


    public void testToArrayNullArray()
    {
        fill("A", "B", "C");
        try
        {
            chain.toArray(null);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            // expected
        }
    }


    public void testToArrayTooSmallArray()
    {
        fill("A", "B", "C");
        try
        {
            chain.toArray(new String[2]);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            // expected
        }
    }


    public void testToArrayEmptyChain()
    {
        String[] arr = { "x", "y" };
        String[] result = chain.toArray(arr);
        assertEquals("x", result[0]);
        assertEquals("y", result[1]);
    }


    public void testToString()
    {
        fill("A", "B", "C");
        assertEquals("[A, B, C]", chain.toString());
    }


    public void testToStringEmptyChain()
    {
        assertEquals("[]", chain.toString());
    }


    public void testRemoveAt()
    {
        fill("A", "B", "C");
        assertEquals("B", chain.removeAt(1));
        assertEquals("[A, C]", chain.toString());
    }


    public void testRemoveAtFirstIndex()
    {
        fill("A", "B", "C");
        assertEquals("A", chain.removeAt(0));
        assertEquals("[B, C]", chain.toString());
    }


    public void testRemoveAtInvalidIndex()
    {
        fill("A", "B", "C");
        int[] bad = { -1, 3 };
        for (int i : bad)
        {
            try
            {
                chain.removeAt(i);
                fail("Expected IndexOutOfBoundsException for index " + i);
            }
            catch (IndexOutOfBoundsException e)
            {
                assertEquals("[A, B, C]", chain.toString());
            }
        }
        LinkedChain<String> empty = new LinkedChain<String>();
        try
        {
            empty.removeAt(0);
            fail("Expected IndexOutOfBoundsException on empty chain");
        }
        catch (IndexOutOfBoundsException e)
        {
            assertEquals(0, empty.size());
        }
    }


    public void testIndexOf()
    {
        fill("A", "B", "C");
        assertEquals(2, chain.indexOf("C"));
    }


    public void testIndexOfNotFound()
    {
        fill("A", "B", "C");
        assertEquals(-1, chain.indexOf("Z"));
    }


    public void testIndexOfNull()
    {
        fill("A", "B", "C");
        assertEquals(-1, chain.indexOf(null));
    }


    public void testIndexOfEmptyChain()
    {
        assertEquals(-1, chain.indexOf("A"));
    }
}