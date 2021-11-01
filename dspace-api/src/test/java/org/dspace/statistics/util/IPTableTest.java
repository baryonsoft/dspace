/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.statistics.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.dspace.statistics.util.IPTable.IPFormatException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author mwood
 */
public class IPTableTest {
    private static final String LOCALHOST = "127.0.0.1";

    public IPTableTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of add method, of class IPTable.
     * @throws java.lang.Exception passed through.
     */
    @Test
    public void testAdd() throws Exception {
        IPTable instance = new IPTable();
        // Add IP address
        instance.add(LOCALHOST);
        // Add IP range
        instance.add("192.168.1");

        // Make sure both exist
        Set<String> ipSet = instance.toSet();
        assertEquals(2, ipSet.size());
        assertTrue(ipSet.contains(LOCALHOST));
        assertTrue(ipSet.contains("192.168.1"));
    }

    @Test
    public void testAddSameIPTwice() throws Exception {
        IPTable instance = new IPTable();
        // Add same IP twice
        instance.add(LOCALHOST);
        instance.add(LOCALHOST);
        // Verify it only exists once
        assertEquals(1, instance.toSet().size());

        instance = new IPTable();
        // Add IP range & then add an IP from within that range
        instance.add("192.168.1");
        instance.add("192.168.1.1");
        // Verify only the range exists
        Set<String> ipSet = instance.toSet();
        assertEquals(1, ipSet.size());
        assertTrue(ipSet.contains("192.168.1"));

        instance = new IPTable();
        // Now, switch order. Add IP address, then add a range encompassing that IP
        instance.add("192.168.1.1");
        instance.add("192.168.1");
        // Verify only the range exists
        ipSet = instance.toSet();
        assertEquals(1, ipSet.size());
        assertTrue(ipSet.contains("192.168.1"));
    }

    /**
     * Test of contains method, of class IPTable.
     * @throws java.lang.Exception passed through.
     */
    @Test
    public void testContains()
            throws Exception {
        IPTable instance = new IPTable();
        instance.add(LOCALHOST);
        boolean contains;

        contains = instance.contains(LOCALHOST);
        assertTrue("Address that was add()ed should match", contains);

        contains = instance.contains("192.168.1.1");
        assertFalse("Address that was not add()ed should not match", contains);

        contains = instance.contains("fec0:0:0:1::2");
        assertFalse("IPv6 address should not match anything.", contains);

        // Now test contains() finds an IP within a range of IPs
        instance.add("192.168.1");
        contains = instance.contains("192.168.1.1");
        assertTrue("IP within an add()ed range should match", contains);
    }

    /**
     * Test of isEmpty method, of class IPTable.
     * @throws java.lang.Exception passed through.
     */
    @Test
    public void testisEmpty() throws Exception {
        IPTable instance = new IPTable();
        assertTrue(instance.isEmpty());
        instance.add(LOCALHOST);
        assertFalse(instance.isEmpty());
    }

    /**
     * Test of contains method when presented with an invalid address.
     * @throws Exception passed through.
     */
    @Test(expected = IPFormatException.class)
    public void testContainsBadFormat()
            throws Exception {
        IPTable instance = new IPTable();
        instance.add(LOCALHOST);
        boolean contains;

        // This should throw an IPFormatException.
        contains = instance.contains("axolotl");
        assertFalse("Nonsense string should raise an exception.", contains);
    }

    /**
     * Test of toSet method, of class IPTable.
     */
    @Ignore
    @Test
    public void testToSet() {
    }
}
