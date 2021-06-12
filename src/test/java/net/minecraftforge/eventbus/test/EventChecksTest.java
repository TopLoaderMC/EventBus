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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import net.minecraftforge.eventbus.api.BusBuilder;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;

public class EventChecksTest {
    public interface MarkerEvent {}
    public static class BaseEvent extends Event implements MarkerEvent {
        
        public BaseEvent() {}
    }
    
    public static class OtherEvent extends Event {
        
        public OtherEvent() {}
    }
    
    private static final String PROP_NAME = "eventbus.checkTypesOnDispatch";
    
    @BeforeAll
    public static void setup() {
        System.setProperty(PROP_NAME, "true");
    }
    
    private static IEventBus bus() {
        return new BusBuilder().markerType(MarkerEvent.class).build();
    }

    @Test
    public void testValidType() {
        IEventBus bus = bus();
        Assertions.assertDoesNotThrow(() -> bus.addListener((BaseEvent e) -> {}));
        Assertions.assertDoesNotThrow(() -> bus.post(new BaseEvent()));
    }
    
    @Test
    public void testInvalidType() {
        IEventBus bus = bus();
        Assertions.assertThrows(IllegalArgumentException.class, () -> bus.addListener((OtherEvent e) -> {}));
        Assertions.assertThrows(IllegalArgumentException.class, () -> bus.post(new OtherEvent()));
    }

    @AfterAll
    public static void teardown() {
        System.clearProperty(PROP_NAME);
    }
}
