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

import java.util.concurrent.Callable;
import java.util.function.Consumer;

import net.minecraftforge.eventbus.api.BusBuilder;
import net.minecraftforge.eventbus.api.IEventBus;

public class BenchmarkArmsLength implements Callable<Void>
{
    private static IEventBus staticSubscriberBus;
    private static IEventBus dynamicSubscriberBus;
    private static IEventBus lambdaSubscriberBus;
    private static IEventBus combinedSubscriberBus;

    @Override
    public Void call()
    {
        if (!new CancelableEvent().isCancelable())
            throw new RuntimeException("Transformer did not apply!");

        staticSubscriberBus = BusBuilder.builder().build();
        dynamicSubscriberBus = BusBuilder.builder().build();
        lambdaSubscriberBus = BusBuilder.builder().build();
        combinedSubscriberBus = BusBuilder.builder().build();

        staticSubscriberBus.register(SubscriberStatic.class);
        combinedSubscriberBus.register(SubscriberStatic.class);
        dynamicSubscriberBus.register(new SubscriberDynamic());
        combinedSubscriberBus.register(new SubscriberDynamic());
        SubscriberLambda.register(lambdaSubscriberBus);
        SubscriberLambda.register(combinedSubscriberBus);
        return null;
    }

    @SuppressWarnings("unused") public static final Consumer<Void> postStatic = BenchmarkArmsLength::postStatic;
    @SuppressWarnings("unused") public static final Consumer<Void> postDynamic = BenchmarkArmsLength::postDynamic;
    @SuppressWarnings("unused") public static final Consumer<Void> postLambda = BenchmarkArmsLength::postLambda;
    @SuppressWarnings("unused") public static final Consumer<Void> postCombined = BenchmarkArmsLength::postCombined;

    public static void postStatic(Void nothing)
    {
        postAll(staticSubscriberBus);
    }

    public static void postDynamic(Void nothing)
    {
        postAll(dynamicSubscriberBus);
    }

    public static void postLambda(Void nothing)
    {
        postAll(lambdaSubscriberBus);
    }

    public static void postCombined(Void nothing)
    {
        postAll(combinedSubscriberBus);
    }

    private static void postAll(IEventBus bus)
    {
        bus.post(new CancelableEvent());
        bus.post(new ResultEvent());
        bus.post(new EventWithData("Foo", 5, true)); //Some example data
    }
}
