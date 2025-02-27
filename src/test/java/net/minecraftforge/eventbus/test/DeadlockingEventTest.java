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

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.RepeatedTest;
import org.powermock.reflect.Whitebox;
import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.TransformingClassLoader;
import net.minecraftforge.eventbus.api.BusBuilder;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventListenerHelper;
import net.minecraftforge.eventbus.api.IEventBus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.fail;

public class DeadlockingEventTest {
    private static final boolean initializeAtClassloading = false;
    private static final long waittimeout = 1; // number of seconds to wait before retrying. Bump this up to debug what's going on.
    public static final int BOUND = 10000;
    public static ThreadPoolExecutor THREAD_POOL;

    @BeforeAll
    static void setup() {
        // force async logging
        System.setProperty("log4j2.contextSelector","org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");
        System.setProperty("test.harness", "out/production/classes,out/test/classes,out/mlservice/classes,out/mlservice/resources,out/testJars/classes,build/classes/java/main,build/classes/java/mlservice,build/classes/java/test,build/classes/java/testJars,build/resources/mlservice");
        System.setProperty("test.harness.callable", "net.minecraftforge.eventbus.test.DeadlockingEventTest$Callback");
   }
    @Disabled
    @RepeatedTest(500)
    void testConstructEventDeadlock() {
        Launcher.main("--version", "1.0", "--launchTarget", "testharness");
    }

    @BeforeEach
    void newThreadPool() {
        THREAD_POOL = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
    }
    @SuppressWarnings("unchecked")
    @AfterEach
    void clearBusStuff() throws Exception {
        Whitebox.invokeMethod(EventListenerHelper.class, "clearAll");
        final HashSet<AbstractQueuedSynchronizer> workers = Whitebox.getInternalState(THREAD_POOL, "workers");
        final List<Thread> threads = workers.stream().map(w -> Whitebox.<Thread>getInternalState(w, "thread")).collect(Collectors.toList());
        threads.stream().map(Thread::getStackTrace).forEach(ts->LogManager.getLogger().info("\n"+stack(ts)));
        THREAD_POOL.shutdown();
    }

    private static String stack(StackTraceElement[] elts) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement elt : elts) {
            sb.append("\tat ").append(elt).append("\n");
        }
        return sb.toString();
    }
    public static class Callback {
        public static Callable<Void> supplier() {
            return () -> {
                final TransformingClassLoader contextClassLoader = (TransformingClassLoader) Thread.currentThread().getContextClassLoader();
                LogManager.getLogger().info("Class Loader {}", contextClassLoader);
                contextClassLoader.addTargetPackageFilter(s -> !(
                        s.startsWith("net.minecraftforge.eventbus.") &&
                                !s.startsWith("net.minecraftforge.eventbus.test")));
                final CountDownLatch cdl = new CountDownLatch(1);
                final IEventBus bus = BusBuilder.builder().build();
                Callable<Void> task2 = () -> {
                    final int nanos = new Random().nextInt(BOUND);
                    LogManager.getLogger().info("Task 2: {}", nanos);
                    long start = System.nanoTime();
                    cdl.await();
                    final Class<? extends Event> clz = (Class<? extends Event>) Class.forName("net.minecraftforge.eventbus.test.DeadlockingEventArmsLength$ChildEvent", initializeAtClassloading, contextClassLoader);
                    Thread.sleep(0, nanos);
                    LogManager.getLogger().info(System.nanoTime() - start);
                    assertEquals(clz.newInstance().getListenerList(), EventListenerHelper.getListenerList(clz));
                    LogManager.getLogger().info("Task 2");
                    return null;
                };
                Callable<Void> task1 = () -> {
                    final int nanos = new Random().nextInt(BOUND);
                    LogManager.getLogger().info("Task 1: {}", nanos);
                    long start = System.nanoTime();
                    cdl.await();
                    final Class<?> clz = Class.forName("net.minecraftforge.eventbus.test.DeadlockingEventArmsLength$Listener1", initializeAtClassloading, contextClassLoader);
                    Thread.sleep(0, nanos);
                    LogManager.getLogger().info(System.nanoTime() - start);
                    bus.register(clz);
                    LogManager.getLogger().info("Task 1");
                    return null;
                };
                final List<Future<Void>> futures = Stream.of(task1, task2).
                        map(THREAD_POOL::submit).collect(Collectors.toList());
                cdl.countDown();
                try {
                    assertTimeoutPreemptively(Duration.ofSeconds(waittimeout), () -> futures.parallelStream().forEach(f -> {
                        try {
                            f.get();
                        } catch (InterruptedException | ExecutionException e) {
                            fail("error", e);
                        }
                    }));
                } finally {
                    futures.forEach(f -> f.cancel(true));
                    futures.forEach(f -> {
                        try {
                            f.get();
                        } catch (CancellationException | InterruptedException | ExecutionException e) {
                            // noop
                        }
                    });
                }
                return null;
            };
        };
    }
}
