/*
 * Minecraft Forge
 * Copyright (c) 2016.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package net.minecraftforge.eventbus.test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.Callable;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.powermock.reflect.internal.WhiteboxImpl;
import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.api.ITransformingClassLoader;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GoodEventDispatcherTest {

    private Object eventBus;
    private boolean gotException;

    @BeforeAll
    public static void setup() {
        Configurator.setRootLevel(Level.DEBUG);
    }

    boolean calledback;
    Class<?> transformedClass;

    @Test
    public void testGoodEvents() throws IOException, URISyntaxException {
        System.setProperty("test.harness", "build/classes/java/testJars,build/classes/java/main");
        System.setProperty("test.harness.callable", "net.minecraftforge.eventbus.test.GoodEventDispatcherTest$TestCallback");
        calledback = false;
        TestCallback.callable = () -> {
            calledback = true;
            final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            ((ITransformingClassLoader)contextClassLoader).
                    addTargetPackageFilter(s->!(
                            s.startsWith("net.minecraftforge.eventbus.") &&
                            !s.startsWith("net.minecraftforge.eventbus.test")));
            final Class<?> aClass = Class.forName("net.minecraftforge.eventbus.api.BusBuilder", true, contextClassLoader);
            Object busBuilder = WhiteboxImpl.invokeMethod(aClass, "builder");
            eventBus = WhiteboxImpl.invokeMethod(busBuilder, "build");
            transformedClass = Class.forName("net.minecraftforge.eventbus.testjar.EventBusTestClass", true, contextClassLoader);
            WhiteboxImpl.invokeMethod(eventBus, "register", transformedClass.newInstance());
            Object evt = Class.forName("net.minecraftforge.eventbus.testjar.DummyEvent$GoodEvent", true, contextClassLoader).newInstance();
            WhiteboxImpl.invokeMethod(eventBus, "post",evt);
            return null;
        };
        Launcher.main("--version", "1.0", "--launchTarget", "testharness");
        assertTrue(calledback, "We got called back");
        assertAll(
                ()-> assertTrue(WhiteboxImpl.getField(transformedClass, "HIT1").getBoolean(null), "HIT1 was hit"),
                ()-> assertTrue(WhiteboxImpl.getField(transformedClass, "HIT2").getBoolean(null), "HIT2 was hit")
        );
    }

    public static class TestCallback {
        private static Callable<Void> callable;
        public static Callable<Void> supplier() {
            return callable;
        }
    }
}
