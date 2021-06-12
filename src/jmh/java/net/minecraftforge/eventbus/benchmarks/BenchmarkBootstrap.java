package net.minecraftforge.eventbus.benchmarks;

import java.util.concurrent.Callable;

import org.powermock.reflect.Whitebox;
import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.TransformingClassLoader;

@SuppressWarnings("unused")
public class BenchmarkBootstrap
{
    @SuppressWarnings("unchecked")
    public static Callable<Void> supplier() throws Exception {
        TransformingClassLoader tcl = (TransformingClassLoader) Whitebox.getField(Launcher.class, "classLoader").get(Launcher.INSTANCE);
        tcl.addTargetPackageFilter(s-> !(s.startsWith("net.minecraftforge.eventbus.") && !s.startsWith("net.minecraftforge.eventbus.benchmark")));
        final Class<?> clazz;
        try {
            clazz = Class.forName("net.minecraftforge.eventbus.benchmarks.compiled.BenchmarkArmsLength", true, tcl);
            return (Callable<Void>)clazz.newInstance();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }
}
