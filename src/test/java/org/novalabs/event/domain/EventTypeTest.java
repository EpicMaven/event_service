
package org.novalabs.event.domain;

import java.util.ArrayList;
import java.util.List;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.incava.ijdk.lang.Str;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JUnitParamsRunner.class)
public class EventTypeTest {
    public EventTypeTest() {
    }

    @Before
    public void setUp() throws Exception {
    }

    @Test
    @Parameters
    public void constructorString(String type) {
        EventType eventType = new EventType(type);
        Assert.assertEquals(type, eventType.getString());
    }

    private Object[] parametersForConstructorString() {
        List<Object[]> params = new ArrayList();

        for(int i = 1; i <= 100; ++i) {
            params.add(new Object[]{(new Str("a", i)).getString()});
        }

        return params.toArray();
    }

    @Test
    public void constructorChar() {
        EventType eventType = new EventType('a');
        Assert.assertEquals("a", eventType.getString());
    }

    @Test(
            expected = IllegalArgumentException.class
    )
    @Parameters
    public void constructorIllegalArgument(String type) {
        new EventType(type);
    }

    private Object[] parametersForConstructorIllegalArgument() {
        List<Object[]> params = new ArrayList();
        params.add(new Object[]{null});
        params.add(new Object[]{""});
        params.add(new Object[]{(new Str("a", 101)).getString()});
        return params.toArray();
    }
}
