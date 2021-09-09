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
package net.minecraftforge.eventbus.modlauncher;

import net.minecraftforge.eventbus.LogMarkers;
import net.minecraftforge.eventbus.modlauncher.transformer.EventAccessTransformer;
import net.minecraftforge.eventbus.modlauncher.transformer.EventSubclassTransformer;
import org.apache.logging.log4j.LogManager;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;

public enum EventBusEngine {
    INSTANCE;

    private final EventSubclassTransformer eventTransformer;
    private final EventAccessTransformer accessTransformer;

    EventBusEngine() {
        LogManager.getLogger().debug(LogMarkers.EVENTBUS, "Loading EventBus transformer");
        this.eventTransformer = new EventSubclassTransformer();
        this.accessTransformer = new EventAccessTransformer();
    }

    public int processClass(final ClassNode classNode, final Type classType) {
        final int evtXformFlags = eventTransformer.transform(classNode, classType).isPresent() ? ClassWriter.COMPUTE_FRAMES : 0x0;
        final int axXformFlags = accessTransformer.transform(classNode, classType) ? 0x100 : 0;
        return evtXformFlags | axXformFlags;
    }

    public boolean handlesClass(final Type classType) {
        final String name = classType.getClassName();
        return !(name.equals("net.minecraftforge.eventbus.api.Event") ||
                name.startsWith("net.minecraft.") ||
                name.indexOf('.') == -1);
    }
}
