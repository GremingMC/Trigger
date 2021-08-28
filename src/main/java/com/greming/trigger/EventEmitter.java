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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;
import java.lang.reflect.Method;
import static java.lang.reflect.Modifier.isAbstract;
import static java.lang.reflect.Modifier.isInterface;
        

public class EventEmitter<EVENT>
{
    
    
    protected final Class<EVENT> eventClass;
    protected final Map<Class<? extends EVENT>, EventGroup<? extends EVENT>> eventGroups;
    
    
    public EventEmitter(Class<EVENT> eventClass) {
        this(eventClass, new ConcurrentHashMap());
    }
    
    
    public EventEmitter(Class<EVENT> eventClass, Map<Class<? extends EVENT>, EventGroup<? extends EVENT>> eventGroups)
    {
        this.eventClass = eventClass;
        this.eventGroups = eventGroups;
    }

    
    public<T extends EVENT> EventGroup<T> registerEvent(Class<T> event)
    {
        if (isAbstract(event.getModifiers()) || isInterface(event.getModifiers())) {
            throw new IllegalArgumentException(event.getName() + " must not be an abstract class or interface!");
        }
        
        if (eventGroups.containsKey(event)) {
            throw new IllegalArgumentException("Event group for " + event.getName() + " already exists!");
        }
        
        EventGroup<T> group = new EventGroup();
        eventGroups.put(event, group);
        
        return group;
    }
    
    
    public<T extends EVENT> EventGroup<T> unregisterEvent(Class<T> event) {
        return (EventGroup<T>) eventGroups.remove(event);
    }
    
    
    public<T extends EVENT> EventListener<T> on(Class<T> event, EventListener<T> listener, EventPriority priority, boolean create)
    {
        EventGroup<T> group = (EventGroup<T>) eventGroups.get(event);
        
        if (group == null) {
            if (create == false) {
                throw new NullPointerException("Not event was found for " + event.getName());
            }
            
            group = registerEvent(event);
        }
        
        group.on(priority, listener);
        return listener;
    }
    
    
    public ArrayList<EventListener<? extends EVENT>> registerListeners(Object target)
    {
        ArrayList<EventListener<? extends EVENT>> listeners = new ArrayList();
        Class<?> clazz = target.getClass();
        
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isBridge() || method.isSynthetic()) {
                continue;
            }
            
            EventTrigger annotation = method.getDeclaredAnnotation(EventTrigger.class);
            
            if (annotation == null) {
                continue;
            }
            
            if (method.getParameterCount() != 1) {
                throw new IllegalArgumentException("Method " + method.getName() + " should contain only 1 parameter.");
            }

            Class<?> param = method.getParameterTypes()[0];
            
            if (eventClass.isAssignableFrom(param) == false) {
                throw new IllegalArgumentException(param.getName() + " is not assignable from " + eventClass.getName());
            }
            
            method.setAccessible(true);
            
            listeners.add(on((Class<? extends EVENT>) param, (event) -> {
                try {
                    method.invoke(target, event);
                } catch (Throwable exception) {
                    throw new RuntimeException(exception);
                }
            }, annotation.priority(), true));
        }
        
        return listeners;
    }
    
    
    public<T extends EVENT> void off(EventListener<T> listener, Class<T> event)
    {
        EventGroup<T> group = (EventGroup<T>) eventGroups.get(event);
        
        if (group != null) {
            group.off(listener);
        }
    }
    
    
    public<T extends EVENT> void emit(T event)
    {
        EventGroup<T> group = (EventGroup<T>) eventGroups.get((Class<? extends EVENT>) event.getClass());
        
        if (group != null) {
            group.call(event);
        }
    }
    
    
    public void clear() {
        eventGroups.clear();
    }
    
    
}
