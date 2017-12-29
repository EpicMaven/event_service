
package org.novalabs.event.domain;

import java.time.Instant;
import java.util.UUID;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JUnitParamsRunner.class)
public class EventTest {
    public EventTest() {
    }

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void defaultConstructor() {
        long now = System.currentTimeMillis();
        Event event = new Event();
        Assert.assertEquals(Event.INVALID_TYPE, event.getType());
        Assert.assertEquals(Event.INVALID_VALUE, event.getValue());
        Assert.assertTrue(event.getEpochMillis() >= now);
    }

    @Test
    @Parameters
    public void constructor(UUID uuid, String type, String value, long epochMillis) {
        EventType eventType = new EventType(type);
        EventValue eventValue = new EventValue(value);
        Event event = new Event(uuid, eventType, eventValue, epochMillis);
        Assert.assertEquals(uuid, event.getUuid());
        Assert.assertEquals(eventType, event.getType());
        Assert.assertEquals(eventValue, event.getValue());
        Assert.assertEquals(epochMillis, event.getEpochMillis());
        Assert.assertEquals(Instant.ofEpochMilli(event.getEpochMillis()), event.getTime());
    }

    private Object[] parametersForConstructor() {
        return new Object[]{new Object[]{UUID.randomUUID(), "foo", "bar", System.currentTimeMillis()}, new Object[]{UUID.randomUUID(), "test type", "test value", 0}, new Object[]{UUID.randomUUID(), "Aa", "bB", 2000}, new Object[]{UUID.randomUUID(), "A-a B!10", "09123*$&#($*& [];}{:", 1}};
    }
}
