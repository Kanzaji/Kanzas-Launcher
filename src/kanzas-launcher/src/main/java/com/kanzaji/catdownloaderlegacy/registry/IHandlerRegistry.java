/**************************************************************************************
 * MIT License                                                                        *
 *                                                                                    *
 * Copyright (c) 2023. Kanzaji                                                        *
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

import org.jetbrains.annotations.NotNull;
import java.util.HashSet;
import java.util.function.Consumer;

public interface IHandlerRegistry {
    /**
     * Used to decode provided String and execute associated handler from the Registry.
     * @param lineToDecode String to decode.
     * @return Depends on the implementation.
     */
    String decode(@NotNull String lineToDecode);

    /**
     * Used to register new Elements to this registry.
     * @param element String with id of the element. This will be used in decode method to access the handler.
     * @param handler {@code Consumer<String>} handling the call to the registry.
     */
    void register(@NotNull String element, @NotNull Consumer<String> handler);

    /**
     * Used to get {@code HashSet<String>} with currently registered elements.
     * Does *not* return references to the handlers itself.
     * @return new {@code HashSet<String>} with all registered elements of this registry.
     */
    HashSet<String> getRegisteredElements();
}
