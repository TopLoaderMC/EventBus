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

import java.util.function.Function;

import org.junit.jupiter.api.Test;
import net.minecraftforge.eventbus.api.BusBuilder;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class EventLambdaTest {
    boolean hit;
    @Test
    public void eventLambda() {
        final IEventBus iEventBus = BusBuilder.builder().build();
        iEventBus.addListener((Event e)-> hit = true);
        iEventBus.post(new Event());
        assertTrue(hit, "Hit event");
    }

    public void consumeSubEvent(SubEvent e) {
        hit = true;
    }
    @Test
    void eventSubLambda() {
        final IEventBus iEventBus = BusBuilder.builder().build();
        iEventBus.addListener(this::consumeSubEvent);
        iEventBus.post(new SubEvent());
        assertTrue(hit, "Hit subevent");
        hit = false;
        iEventBus.post(new Event());
        assertTrue(!hit, "Didn't hit parent event");
    }

    @Test
    void eventGenericThing() {
        // pathological test because you can't derive the lambda types in all cases...
        IEventBus bus = BusBuilder.builder().build();
        registerSomeGodDamnWrapper(bus, CancellableEvent.class, this::subEventFunction);
        final CancellableEvent event = new CancellableEvent();
        bus.post(event);
        assertTrue(event.isCanceled(), "Event got cancelled");
        final SubEvent subevent = new SubEvent();
        bus.post(subevent);
    }

    private boolean subEventFunction(final CancellableEvent event) {
        return event instanceof CancellableEvent;
    }

    public <T extends Event> void registerSomeGodDamnWrapper(IEventBus bus, Class<T> tClass, Function<T, Boolean> func) {
        bus.addListener(EventPriority.NORMAL, false, tClass, (T event) -> {
            if (func.apply(event)) {
                event.setCanceled(true);
            }
        });
    }

    public static class SubEvent extends Event {

    }

    public static class CancellableEvent extends Event {
        @Override
        public boolean isCancelable() {
            return true;
        }
    }
}
