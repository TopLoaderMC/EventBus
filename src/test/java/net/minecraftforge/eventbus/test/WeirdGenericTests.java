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

import java.lang.reflect.Type;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import net.minecraftforge.eventbus.api.BusBuilder;
import net.minecraftforge.eventbus.api.GenericEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class WeirdGenericTests {
	
	boolean genericEventHandled = false;
	
	@Test
	public void testGenericListener() {
		IEventBus bus = BusBuilder.builder().build();
		bus.addGenericListener(List.class, GenericEvent.class, this::handleGenericEvent);
		bus.post(new GenericEvent<List<String>>() {
			@Override public Type getGenericType() {
				return List.class;
			}
		});
		Assertions.assertTrue(genericEventHandled);
	}
	
	@Test
	public void testGenericListenerRegisteredIncorrectly() {
	    IEventBus bus = BusBuilder.builder().build();
	    Assertions.assertThrows(IllegalArgumentException.class, () -> bus.addListener(GenericEvent.class, this::handleGenericEvent));
	}

	private void handleGenericEvent(GenericEvent<List<String>> evt) {
		genericEventHandled = true;
	}

	static boolean hit;
	@Test
	public void testNoFilterRegisterWithWildcard() {
		IEventBus bus = BusBuilder.builder().build();
		bus.register(new GenericHandler());
		hit = false;
		bus.post(new GenericEvent<>());
		Assertions.assertTrue(hit, "Hit the event");
	}

	public static class GenericHandler {
		@SubscribeEvent
		public void handleWildcardGeneric(GenericEvent<?> ge) {
			hit = true;
		}
	}
}
