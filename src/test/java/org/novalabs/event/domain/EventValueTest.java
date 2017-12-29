
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
public class EventValueTest {
    public EventValueTest() {
    }

    @Before
    public void setUp() throws Exception {
    }

    @Test
    @Parameters
    public void constructorString(String type) {
        EventValue eventValue = new EventValue(type);
        Assert.assertEquals(type, eventValue.getString());
    }

    private Object[] parametersForConstructorString() {
        List<Object[]> params = new ArrayList();

        for(int i = 1; i <= 255; ++i) {
            params.add(new Object[]{(new Str("a", i)).getString()});
        }

        return params.toArray();
    }

    @Test
    public void constructorChar() {
        EventValue eventValue = new EventValue('a');
        Assert.assertEquals("a", eventValue.getString());
    }

    @Test(
            expected = IllegalArgumentException.class
    )
    @Parameters
    public void constructorIllegalArgument(String type) {
        new EventValue(type);
    }

    private Object[] parametersForConstructorIllegalArgument() {
        List<Object[]> params = new ArrayList();
        params.add(new Object[]{null});
        params.add(new Object[]{""});
        params.add(new Object[]{(new Str("a", 256)).getString()});
        return params.toArray();
    }
}
