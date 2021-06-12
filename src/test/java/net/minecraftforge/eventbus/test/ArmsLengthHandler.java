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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;

import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.Assertions;
import org.powermock.reflect.Whitebox;
import net.minecraftforge.eventbus.ListenerList;
import net.minecraftforge.eventbus.api.BusBuilder;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.IEventListener;
import net.minecraftforge.eventbus.testjar.DummyEvent;

public class ArmsLengthHandler implements Callable<Void> {
    @Override
    public Void call() throws Exception {
        final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        LogManager.getLogger().info("CCL is {}", contextClassLoader);
        IEventBus bus = BusBuilder.builder().setTrackPhases(false).build();
        LogManager.getLogger().info("Bus is {}", bus.getClass().getClassLoader());
        LogManager.getLogger().info("Event is {}", DummyEvent.GoodEvent.class.getClassLoader());
        Set<Runnable> toAdd = new HashSet<>();

        for (int i = 0; i < ParallelTransformedTest.LISTENER_COUNT; i++) { //prepare parallel listener adding
            toAdd.add(() -> bus.addListener(DummyEvent.GoodEvent.class, (e)-> ParallelTransformedTest.COUNTER.incrementAndGet()));
        }

        Object realListenerList = Whitebox.getField(DummyEvent.GoodEvent.class, "LISTENER_LIST").get(null);
        toAdd.parallelStream().forEach(Runnable::run); //execute parallel listener adding
        final ListenerList listenerList = Whitebox.invokeMethod(new DummyEvent.GoodEvent(), "getListenerList");
        LogManager.getLogger().info("Orig: {}, final {}", realListenerList, listenerList);
        Object inst = ((Object[])Whitebox.getInternalState(listenerList, "lists"))[0];
        final ArrayList<ArrayList<IEventListener>> priorities = Whitebox.getInternalState(inst, "priorities");
        toAdd = new HashSet<>();
        for (int i = 0; i < ParallelTransformedTest.RUN_ITERATIONS; i++) //prepare parallel event posting
            toAdd.add(() -> bus.post(new DummyEvent.GoodEvent()));
        toAdd.parallelStream().forEach(Runnable::run); //post events parallel

        try {
            long expected = ParallelTransformedTest.LISTENER_COUNT * ParallelTransformedTest.RUN_ITERATIONS;
            int busid = Whitebox.getInternalState(bus, "busID");
            Assertions.assertAll(
                    ()->Assertions.assertEquals(expected, ParallelTransformedTest.COUNTER.get()),
                    ()->Assertions.assertEquals(ParallelTransformedTest.LISTENER_COUNT, listenerList.getListeners(busid).length - 1)

            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
