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
package net.minecraftforge.eventbus.benchmarks;

import java.nio.file.Paths;
import java.util.function.Consumer;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.powermock.reflect.Whitebox;
import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.TransformingClassLoader;

@State(Scope.Benchmark)
public class EventBusBenchmark
{
    private Consumer<Void> postStatic;
    private Consumer<Void> postDynamic;
    private Consumer<Void> postLambda;
    private Consumer<Void> postCombined;

    @SuppressWarnings("unchecked")
    @Setup
    public void setup() throws Exception
    {
        //Forks have an incorrect working dir set, so use the absolute path to correct
        String basePath = Paths.get(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI()).getParent().getParent().toAbsolutePath().toString();
        System.setProperty("test.harness", basePath + "/classes/java/testJars," + basePath + "/classes/java/main");
        System.setProperty("test.harness.callable", "net.minecraftforge.eventbus.benchmarks.BenchmarkBootstrap");
        Launcher.main("--version", "1.0", "--launchTarget", "testharness");

        TransformingClassLoader tcl = (TransformingClassLoader) Whitebox.getField(Launcher.class, "classLoader").get(Launcher.INSTANCE);
        Class<?> cls = Class.forName("net.minecraftforge.eventbus.benchmarks.compiled.BenchmarkArmsLength", false, tcl);
        postStatic = (Consumer<Void>) cls.getDeclaredField("postStatic").get(null);
        postDynamic = (Consumer<Void>) cls.getDeclaredField("postDynamic").get(null);
        postLambda = (Consumer<Void>) cls.getDeclaredField("postLambda").get(null);
        postCombined = (Consumer<Void>) cls.getDeclaredField("postCombined").get(null);
    }

    @Benchmark
    public int testDynamic()
    {
        postDynamic.accept(null);
        return 0;
    }

    @Benchmark
    public int testLambda()
    {
        postLambda.accept(null);
        return 0;
    }

    @Benchmark
    public int testStatic()
    {
        postStatic.accept(null);
        return 0;
    }

}
