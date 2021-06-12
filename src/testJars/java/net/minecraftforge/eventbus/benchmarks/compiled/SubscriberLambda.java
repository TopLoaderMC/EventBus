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
package net.minecraftforge.eventbus.benchmarks.compiled;

import net.minecraftforge.eventbus.api.IEventBus;

public class SubscriberLambda
{

    public static void register(IEventBus bus)
    {
        bus.addListener(CancelableEvent.class, SubscriberLambda::onCancelableEvent);
        bus.addListener(ResultEvent.class, SubscriberLambda::onResultEvent);
        bus.addListener(EventWithData.class, SubscriberLambda::onSimpleEvent);
    }

    public static void onCancelableEvent(CancelableEvent event)
    {

    }

    public static void onResultEvent(ResultEvent event)
    {

    }

    public static void onSimpleEvent(EventWithData event)
    {

    }
}
