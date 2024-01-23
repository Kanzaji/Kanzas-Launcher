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

package com.kanzaji.catdownloaderlegacy.services;

/**
 * IService is an interface describing the default structure of the Service Class.
 * Service should be a Singleton class, running on separate thread when needed. Those requirements aren't forced and multi-instance implementations are possible.
 */
public interface IService {

    /**
     * Method used to launch implementation of the Service.
     * Should not be called more than once, and action should be taken if invoked on already started service.
     * Effects depend heavily on the implementation of this Interface.
     */
    void StartService();

    /**
     * Method used to stop implementation of the Service.
     * Depends on the Implementation and might not always do anything.
     */
    void StopService();

    /**
     * Method used to get Thread created by StartService(), if Service is running on separate thread.;
     * @return Thread this service is running on, or Null if the service doesn't use Threads.
     */
    Thread getThread();
}
