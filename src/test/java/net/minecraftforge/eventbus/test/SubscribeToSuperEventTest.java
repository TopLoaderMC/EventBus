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

public class SubscribeToSuperEventTest {

	@Test
	void eventHandlersCanSubscribeToSuperEvents() {
		IEventBus bus = BusBuilder.builder().build();
		AtomicBoolean superEventHandled = new AtomicBoolean(false);
		AtomicBoolean subEventHandled = new AtomicBoolean(false);
		bus.addListener(EventPriority.NORMAL, false, SuperEvent.class, (event) -> {
			Class<? extends SuperEvent> eventClass = event.getClass();
			if (eventClass == SuperEvent.class) {
				superEventHandled.set(true);
			} else if (eventClass == SubEvent.class) {
				subEventHandled.set(true);
			}
		});

		bus.post(new SuperEvent());
		bus.post(new SubEvent());

		assertTrue(superEventHandled.get(), "handled super event");
		assertTrue(subEventHandled.get(), "handled sub event");
	}

	public static class SuperEvent extends Event {

	}

	public static class SubEvent extends SuperEvent {

	}
}
