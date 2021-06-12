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
