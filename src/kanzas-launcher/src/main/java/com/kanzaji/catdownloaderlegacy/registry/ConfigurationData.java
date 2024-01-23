/**************************************************************************************
 * MIT License                                                                        *
 *                                                                                    *
 * Copyright (c) 2023-2024. Kanzaji                                                   *
 *                                                                                    *
 * Permission is hereby granted, free of charge, to any person obtaining a copy       *
 * of this software and associated documentation files (the "Software"), to deal      *
 * in the Software without restriction, including without limitation the rights       *
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell          *
 * copies of the Software, and to permit persons to whom the Software is              *
 * furnished to do so, subject to the following conditions:                           *
 *                                                                                    *
 * The above copyright notice and this permission notice shall be included in all     *
 * copies or substantial portions of the Software.                                    *
 *                                                                                    *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR         *
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,           *
 * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE       *
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER             *
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,      *
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE      *
 * SOFTWARE.                                                                          *
 **************************************************************************************/

package com.kanzaji.catdownloaderlegacy.registry;

import org.jetbrains.annotations.Contract;

import java.util.HashMap;
import java.util.Map;

public class ConfigurationData {
    /**
     * Holds all boolean entries of the Configuration
     */
    private final Map<String, Boolean> booleanEntries = new HashMap<>();
    /**
     * Holds all String entries of the Configuration
     */
    private final Map<String, String> stringEntries = new HashMap<>();
    /**
     * Holds all Integer entries of the Configuration
     */
    private final Map<String, Integer> intEntries = new HashMap<>();
    /**
     * Holds all non-Boolean/String/Integer entries, should be used as little as possible.
     */
    private final Map<String, ConfigurationDataWrapper<?>> customEntries = new HashMap<>();

    /**
     * This method is used to set Boolean entry in the Configuration Data.
     * @param key String key of the entry
     * @param value Value to store under a specified key.
     */
    protected void setBooleanEntry(String key, Boolean value) {
        booleanEntries.put(key, value);
    }

    /**
     * This method is used to set String entry in the Configuration Data.
     * @param key String key of the entry
     * @param value Value to store under a specified key.
     */
    protected void setStringEntry(String key, String value) {
        stringEntries.put(key, value);
    }

    /**
     * This method is used to set Integer entry in the Configuration Data.
     * @param key String key of the entry
     * @param value Value to store under a specified key.
     */
    protected void setIntEntry(String key, Integer value) {
        intEntries.put(key, value);
    }

    /**
     * This method is used to set Custom entry wrapped in {@link ConfigurationDataWrapper} in the Configuration Data.
     * @param key String key of the entry
     * @param value Value to store under a specified key.
     */
    protected void setCustomEntry(String key, ConfigurationDataWrapper<?> value) {
        customEntries.put(key, value);
    }

    /**
     * This method is used to set Boolean entry from the Configuration Data.
     * @param key String key of the entry
     */
    public boolean getBoolEntry(String key) {
        return booleanEntries.get(key);
    }

    /**
     * This method is used to set String entry from the Configuration Data.
     * @param key String key of the entry
     */
    public String getStringEntry(String key) {
        return stringEntries.get(key);
    }

    /**
     * This method is used to get Integer entry from the Configuration Data.
     * @param key String key of the entry
     */
    public int getIntEntry(String key) {
        return intEntries.get(key);
    }

    /**
     * This method is used to get Custom entry wrapped in {@link ConfigurationDataWrapper} from the Configuration Data.
     * @param key String key of the entry
     */
    public ConfigurationDataWrapper<?> getCustomEntry(String key) {
        return customEntries.get(key);
    }

    /**
     * This class is used to wrap around a single field of class T, to allow for storing different class objects / values in a single map.
     * @param <T> Class of the data.
     */
    public static class ConfigurationDataWrapper<T> {
        private T data;

        /**
         * Constructor of the DataWrapper.
         * @param data Initial value. Null is also valid value.
         */
        @Contract(pure = true)
        private ConfigurationDataWrapper(T data) {
            this.data = data;
        }

        /**
         * Used to get stored data of this object.
         * @return object of class T that is stored in this object.
         */
        @Contract(pure = true)
        private T getData() {return this.data;}

        /**
         * Used to set internal field to specified value.
         * @return Reference to the object this method was called from. (this)
         */
        @Contract(value = "_ -> this", mutates = "this")
        private ConfigurationDataWrapper<T> setData(T data) {
            this.data = data;
            return this;
        }

        /**
         * Uesd to get Class of the stored data.
         * @return Class of the stored data.
         */
        private Class<?> getDataClass() {
            return data.getClass();
        }
    }

    // Wrapped methods for easier access to registered arguments. Not required for registering new Argument!
    public String getMode() {
        return getStringEntry("mode");
    }
}
