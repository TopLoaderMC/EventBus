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
package net.minecraftforge.eventbus.service;

import java.util.EnumSet;
import java.util.Objects;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;
import net.minecraftforge.eventbus.EventBusEngine;

public class ModLauncherService implements ILaunchPluginService {
    @Override
    public String name() {
        return "eventbus";
    }

    @Override
    public int processClassWithFlags(final Phase phase, final ClassNode classNode, final Type classType, String reason) {
        return Objects.equals(reason, "classloading") ? EventBusEngine.INSTANCE.processClass(classNode, classType) : ComputeFlags.NO_REWRITE;
    }

    private static final EnumSet<Phase> YAY = EnumSet.of(Phase.AFTER);
    private static final EnumSet<Phase> NAY = EnumSet.noneOf(Phase.class);

    @Override
    public EnumSet<Phase> handlesClass(final Type classType, final boolean isEmpty) {
        // we never handle empty classes
        return !isEmpty && EventBusEngine.INSTANCE.handlesClass(classType) ? YAY : NAY;
    }
}
