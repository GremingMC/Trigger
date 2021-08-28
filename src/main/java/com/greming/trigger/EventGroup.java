/**
 *
 *    ____                    _
 *   / ___|_ __ ___ _ __ ___ (_)_ __   __ _
 *  | |  _| '__/ _ \ '_ ` _ \| | '_ \ / _` |
 *  | |_| | | |  __/ | | | | | | | | | (_| |
 *   \____|_|  \___|_| |_| |_|_|_| |_|\__, |
 *                                    |___/
 *
 * This file is part of Greming.
 *
 * Greming is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Greming is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Greming. If not, see <https://www.gnu.org/licenses/>.
 *
 * @author Brayan Roman
 *
 */

package com.greming.trigger;

import java.util.HashSet;
import java.util.LinkedHashMap;


public class EventGroup<T>
{

    
    protected final LinkedHashMap<EventPriority, HashSet<EventListener<T>>> listeners = new LinkedHashMap();

    
    public EventGroup() {
        for (EventPriority priority : EventPriority.values()) {
            listeners.put(priority, new HashSet());
        }
    }

    
    public EventGroup<T> on(EventPriority priority, EventListener<T> listener)
    {
        listeners.get(priority).add(listener);
        return this;
    }

    
    public EventGroup<T> off(EventListener<T> listener)
    {
        listeners.forEach((priority, hashset) -> {
            hashset.remove(listener);
        });
        
        return this;
    }

    
    @SuppressWarnings("CallToPrintStackTrace")
    public EventGroup<T> call(T event)
    {
        for (EventPriority priority : EventPriority.values()) {
            for (EventListener<T> listener : listeners.get(priority)) {
                try {
                    listener.call(event);
                } catch (Throwable exception) {
                    exception.printStackTrace();
                }
            }
        }
        
        return this;
    }
    
    
    public EventGroup<T> clear()
    {
        listeners.clear();
        return this;
    }
    
    
}
