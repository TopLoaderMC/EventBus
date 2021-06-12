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

import net.minecraftforge.eventbus.api.Event;

public class EventWithData extends Event
{
    private final String data;
    private final int foo;
    private final boolean bar;

    public EventWithData(String data, int foo, boolean bar)
    {
        this.data = data;
        this.foo = foo;
        this.bar = bar;
    }

    public int getFoo()
    {
        return foo;
    }

    public String getData()
    {
        return data;
    }

    public boolean isBar()
    {
        return bar;
    }
}
