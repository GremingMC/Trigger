
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

import java.util.Scanner;

import com.greming.trigger.EventEmitter;
import com.greming.trigger.EventPriority;
import com.greming.trigger.EventTrigger;


public class EventTest
{

    
    private static boolean running = true;
    
    
    public static void main(String[] args)
    {
        // Create a new emitter
        EventEmitter<String> emitter = new EventEmitter(String.class);
        
        // Add an event listener 
        emitter.on(String.class, (event) -> {
            if (event.equalsIgnoreCase("hello")) {
                System.out.println("Hello to you too!");
            }
        }, EventPriority.Normal, true); // Set to true to automatically register the event class
        
        emitter.on(String.class, (event) -> {
            if (event.equalsIgnoreCase("bye")) {
                running = false;
                System.out.println("Bye bye!");
            }
        }, EventPriority.Normal, true);
        
        // Register all listeners from the given class instance
        emitter.registerListeners(new EventTest());
        
        Scanner scanner = new Scanner(System.in);
        
        while (running) {
            try {
                emitter.emit(scanner.nextLine()); // Emit
            } catch (Throwable exception) {
                exception.printStackTrace();
            }
        }
    }
    
    
    @EventTrigger(priority = EventPriority.Low)
    public void numberOne(String event) {
        System.out.println("Hi i'm in first place!");
    }
    
    
    @EventTrigger(priority = EventPriority.Highest)
    public void numberTwo(String event) {
        System.out.println("Hi i'm in last place!");
    }
    
    
}
