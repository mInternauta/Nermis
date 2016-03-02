/*
 * Copyright (C) 2015 mInternauta
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * 
 */
package mInternauta.Nermis.Utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import mInternauta.Nermis.Core.nServiceWatcher;
import mInternauta.Nermis.Notifiers.nAbstractNotifier;
import static mInternauta.Nermis.Utils.nApplication.CurrentLogger;

/**
 * Jar File Manager
 */
public class nJarManager {

    private static <T> ArrayList<T> seekJar(String pathToJar, Class baseClass) {
        ArrayList<T> list = new ArrayList<>();

        try {
            File filePath = new File(pathToJar + ".jar");
            JarFile jarFile = new JarFile(filePath.getAbsolutePath());
            Enumeration e = jarFile.entries();

            URL[] urls = {new URL("jar:file:" + filePath.getAbsolutePath() + "!/")};
            URLClassLoader cl = URLClassLoader.newInstance(urls);

            while (e.hasMoreElements()) {
                JarEntry je = (JarEntry) e.nextElement();
                if (je.isDirectory() || !je.getName().endsWith(".class")) {
                    continue;
                }
                // -6 because of .class
                String className = je.getName().substring(0, je.getName().length() - 6);
                className = className.replace('/', '.');
                Class c = cl.loadClass(className);

                Class cSuper = c.getSuperclass();

                if (cSuper.equals(baseClass)) {
                    T obj = (T) c.newInstance();
                    list.add(obj);
                } else {
                    Class[] interfaces = c.getInterfaces();

                    for (Class it : interfaces) {
                        if (it.equals(baseClass)) {
                            T obj = (T) c.newInstance();
                            list.add(obj);
                            break;
                        }
                    }
                }
            }
        } catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            CurrentLogger.log(Level.SEVERE, null, ex);
        }

        return list;
    }

    /**
     * Load all Watchers in the Jar
     *
     * @param pathToJar Path to the Jar File
     * @return A list of watchers in the jar
     */
    public static ArrayList<nServiceWatcher> LoadWatchersFromJar(String pathToJar) {
        ArrayList<nServiceWatcher> Watchers = new ArrayList<>();

        Watchers = seekJar(pathToJar, nServiceWatcher.class);

        return Watchers;
    }

    public static ArrayList<nAbstractNotifier> LoadNotifiersFromJar(String pathToJar) {
        ArrayList<nAbstractNotifier> notifiers = new ArrayList<>();

        notifiers = seekJar(pathToJar, nAbstractNotifier.class);

        return notifiers;
    }
}
