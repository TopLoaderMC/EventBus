package net.minecraftforge.eventbus;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.IEventExceptionHandler;
import net.minecraftforge.eventbus.api.IEventListener;

import static net.minecraftforge.eventbus.LogMarkers.EVENTBUS;

public class LoggingEventExceptionHandler implements IEventExceptionHandler {

    public static final IEventExceptionHandler INSTANCE = new LoggingEventExceptionHandler();

    @Override
    public void handleException(IEventBus bus, Event event, IEventListener[] listeners, int index, Throwable throwable) {
        EventBus.LOGGER.error(EVENTBUS, ()->new EventBusErrorMessage(event, index, listeners, throwable));
    }

}
