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

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Test;
import net.minecraftforge.eventbus.ListenerList;
import net.minecraftforge.eventbus.api.BusBuilder;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventListenerHelper;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AbstractEventListenerTest {
    @Test
    void eventHandlersCanSubscribeToAbstractEvents() {
        IEventBus bus = BusBuilder.builder().build();
        AtomicBoolean abstractSuperEventHandled = new AtomicBoolean(false);
        AtomicBoolean concreteSuperEventHandled = new AtomicBoolean(false);
        AtomicBoolean abstractSubEventHandled = new AtomicBoolean(false);
        AtomicBoolean concreteSubEventHandled = new AtomicBoolean(false);
        bus.addListener(EventPriority.NORMAL, false, AbstractSuperEvent.class, (event) -> abstractSuperEventHandled.set(true));
        bus.addListener(EventPriority.NORMAL, false, ConcreteSuperEvent.class, (event) -> concreteSuperEventHandled.set(true));
        bus.addListener(EventPriority.NORMAL, false, AbstractSubEvent.class, (event) -> abstractSubEventHandled.set(true));
        bus.addListener(EventPriority.NORMAL, false, ConcreteSubEvent.class, (event) -> concreteSubEventHandled.set(true));

        bus.post(new ConcreteSubEvent());

        assertTrue(abstractSuperEventHandled.get(), "handled abstract super event");
        assertTrue(concreteSuperEventHandled.get(), "handled concrete super event");
        assertTrue(abstractSubEventHandled.get(), "handled abstract sub event");
        assertTrue(concreteSubEventHandled.get(), "handled concrete sub event");
        assertEquals(100, ConcreteSubEvent.MERGED_STATIC_INIT, "static init merge failed");
    }

    // Below, we simulate the things that are added by EventSubclassTransformer
    // to show that it will work alongside the static listener map.

    public static abstract class AbstractSuperEvent extends Event {

    }

    public static class ConcreteSuperEvent extends AbstractSuperEvent {

        private static final ListenerList LISTENER_LIST = new ListenerList(EventListenerHelper.getListenerList(ConcreteSuperEvent.class.getSuperclass()));
        public ConcreteSuperEvent() {}

        @Override
        public ListenerList getListenerList()
        {
            return LISTENER_LIST;
        }
    }

    public static class AbstractSubEvent extends ConcreteSuperEvent {

    }

    public static class ConcreteSubEvent extends AbstractSubEvent {
        protected static int MERGED_STATIC_INIT = 100;
        private static final ListenerList LISTENER_LIST = new ListenerList(EventListenerHelper.getListenerList(ConcreteSubEvent.class.getSuperclass()));
        public ConcreteSubEvent() {}

        @Override
        public ListenerList getListenerList()
        {
            return LISTENER_LIST;
        }
    }

}
