package com.maurict.networktask;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class NetworkTaskTests {
    @Test
    public void CanCreateWithoutParameters() {
        NetworkTask nt = new NetworkTask("https://randomuser.me/api");
        nt.execute((r) -> {
            assertTrue(r.isSuccess());
            assertNull(r.getError());
            assertTrue(r.toString().length() > 0);
        });
    }

    @Test
    public void CanCreateWithParametersChaining() {
        NetworkTask.fromUrl("https://randomuser.me/api")
                .withParameter("gender", "female")
                .execute((r) -> {
                    assertTrue(r.isSuccess());
                    assertNull(r.getError());
                    assertTrue(r.toString().length() > 0);
                });
    }
}