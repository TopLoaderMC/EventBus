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
		bus.addGenericListener(List.class, this::handleGenericEvent);
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
	    Assertions.assertThrows(IllegalArgumentException.class, () -> bus.addListener(this::handleGenericEvent));
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
