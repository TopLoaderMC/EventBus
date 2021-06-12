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
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.RepeatedTest;
import net.minecraftforge.eventbus.api.BusBuilder;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

public class ThreadedListenerExceptionTest {

    private static boolean failed;

    private static final IEventBus testEventBus = BusBuilder.builder().setExceptionHandler((bus, event, listeners, index, throwable) -> {
        failed = true;
/*

        throwable.printStackTrace();

        try {
            final ListenerList listenerList = event.getListenerList();

            Method getInstance = listenerList.getClass().getDeclaredMethod("getInstance", int.class);
            getInstance.setAccessible(true);

            Object listenerListInst = getInstance.invoke(listenerList, 0);

            Field prioritiesField = listenerListInst.getClass().getDeclaredField("priorities");
            prioritiesField.setAccessible(true);

            @SuppressWarnings("unchecked")
            ArrayList<ArrayList<IEventListener>> priorities = (ArrayList<ArrayList<IEventListener>>) prioritiesField.get(listenerListInst);

            Arrays.stream(EventPriority.values()).forEach(priority -> {
                LogManager.getLogger().error("priority={}, listeners=[{}]", priority.name(), priorities.get(priority.ordinal()).stream().map(Objects::toString).collect(Collectors.joining(",")));
            });

            final Field listeners1 = listenerListInst.getClass().getDeclaredField("listeners");
            listeners1.setAccessible(true);
            final IEventListener[] cache = (IEventListener[]) listeners1.get(listenerListInst);
            LogManager.getLogger().error("cache={}", Arrays.asList(cache));
        } catch (NoSuchMethodException | NoSuchFieldException | IllegalAccessException | InvocationTargetException ignored) { }
*/
    }).build();
    private static ExecutorService executorService;

//    @Test
//    public void testListenerHammering() {
//        assertTimeoutPreemptively(Duration.ofSeconds(30), this::testListenerListConstruction);
//    }

    @BeforeAll
    static void beforeClass() {
        executorService = Executors.newFixedThreadPool(100);
    }
    @BeforeEach
    public void beforeEach() throws Exception {
        failed = false;
        final List<Callable<Object>> callables = Collections.nCopies(50, Executors.callable(() -> testEventBus.addListener(ThreadedListenerExceptionTest::testEvent1)));
        executorService.invokeAll(callables).stream().forEach(f->{
            try {
                // wait for everybody
                f.get();
            } catch (InterruptedException | ExecutionException e) {
            }
        });
    }

    @Disabled
    @RepeatedTest(100)
    public void testWithTimeout() {
        assertTimeoutPreemptively(Duration.ofMillis(10000), this::testListenerList);
    }

    public void testListenerList() throws Exception {
        final List<Callable<Object>> callables = Collections.nCopies(100, Executors.callable(ThreadedListenerExceptionTest::generateEvents));
        executorService.invokeAll(callables).stream().forEach(f->{
            try {
                // wait for everybody
                f.get();
            } catch (InterruptedException | ExecutionException e) {
            }
        });
        assertFalse(failed);
    }

    private static void generateEvents() {
        for (int i = 0; i < 10; i ++) {
            testEventBus.post(new TestEvent());
        }
    }
    private static void testEvent1(TestEvent evt) {

    }
    private static void testEvent2(TestEvent evt) {

    }
    private static void testEvent3(TestEvent evt) {

    }
    private static void testEvent4(TestEvent evt) {

    }
    private static void testEvent5(TestEvent evt) {

    }

    public static class TestEvent extends Event {

        public TestEvent() { }

        private static class Runner extends Thread {

            @Override
            public void run() {
            }

        }

    }

}
