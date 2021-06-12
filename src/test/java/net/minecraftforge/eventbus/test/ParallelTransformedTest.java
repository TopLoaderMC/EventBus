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

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.RepeatedTest;
import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.TransformingClassLoader;

public class ParallelTransformedTest {
    static final int LISTENER_COUNT = 1000;
    static final int RUN_ITERATIONS = 1000;

    static final AtomicLong COUNTER = new AtomicLong();

    @BeforeEach
    public void setup() {
        COUNTER.set(0);
    }

    @Disabled
    @RepeatedTest(100)
    public void testOneBusParallelTransformed() {
        System.setProperty("test.harness", "out/production/classes,out/test/classes,out/mlservice/classes,out/mlservice/resources,out/testJars/classes,build/classes/java/main,build/classes/java/mlservice,build/classes/java/test,build/classes/java/testJars,build/resources/mlservice");
        System.setProperty("test.harness.callable", "net.minecraftforge.eventbus.test.ParallelTransformedTest$TestCallback");
        Launcher.main("--version", "1.0", "--launchTarget", "testharness");
    }


    public static class TestCallback {
        public static Callable<Void> supplier() {
            final TransformingClassLoader contextClassLoader = (TransformingClassLoader) Thread.currentThread().getContextClassLoader();
            contextClassLoader.addTargetPackageFilter(s->!(
                    s.startsWith("net.minecraftforge.eventbus.") &&
                    !s.startsWith("net.minecraftforge.eventbus.test")));
            final Class<?> clazz;
            try {
                clazz = Class.forName("net.minecraftforge.eventbus.test.ArmsLengthHandler", true, contextClassLoader);
                return (Callable<Void>)clazz.newInstance();
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
