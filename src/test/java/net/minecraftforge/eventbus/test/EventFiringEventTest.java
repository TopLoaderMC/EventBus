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
import net.minecraftforge.eventbus.api.BusBuilder;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class EventFiringEventTest {

	@Test
	void eventHandlersCanFireEvents() {
		IEventBus bus = BusBuilder.builder().build();
		AtomicBoolean handled1 = new AtomicBoolean(false);
		AtomicBoolean handled2 = new AtomicBoolean(false);
		bus.addListener(EventPriority.NORMAL, false, Event1.class, (event1) -> {
			bus.post(new AbstractEvent.Event2());
			handled1.set(true);
		});
		bus.addListener(EventPriority.NORMAL, false, AbstractEvent.Event2.class, (event2) -> {
			handled2.set(true);
		});

		bus.post(new Event1());

		assertTrue(handled1.get(), "handled Event1");
		assertTrue(handled2.get(), "handled Event2");
	}

	public static class Event1 extends Event {

	}

	public static abstract class AbstractEvent extends Event {
		public static class Event2 extends AbstractEvent {

		}
	}
}
