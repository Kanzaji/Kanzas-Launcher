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

package com.kanzaji.catdownloaderlegacy.utils;

import com.kanzaji.catdownloaderlegacy.loggers.LoggerCustom;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.rmi.UnexpectedException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

/**
 * This class holds all utility methods used for Argument and Command interpretation.
 */
public class InterpretationUtilities {
    private static final LoggerCustom logger = new LoggerCustom("Interpretation Utilities");
    private static final String[] modes = {
            "cf-pack",
            "cf-instance",
            "modrinth",
            "automatic"
    };

    /**
     * Returns available app modes.
     * @return Array with app modes
     * @deprecated Modes are deprecated, they are the past of Cat Downloader Legacy, what is not required in the Launcher version.
     */
    public static String[] getAvailableModes() {return modes;}

    /**
     * Used to determine if provided {@link String} is one of the accepted Strings for boolean value.
     * Defaults to {@code true} if incorrect String is passed.
     * @param Value {@link String} with boolean value
     * @return {@link Boolean} with the result of the check.
     */
    public static boolean getBooleanValue(String Value) {
        Value = Value.toLowerCase(Locale.ROOT);
        return !(
                Objects.equals(Value,"false") ||
                        Objects.equals(Value,"disabled") ||
                        Objects.equals(Value,"off") ||
                        Objects.equals(Value,"0")
        );
    }

    /**
     * Used to parse {@link Integer} value from a {@link String}, and validate if it's contained in a specified threshold.
     * @param Value {@link String} with Integer value to parse.
     * @param Argument {@link String} with Name of the argument to provide in Exception.
     * @param MinValue {@link Integer} with Minimal threshold for parsed Integer.
     * @param MaxValue {@link Integer} with Maximal threshold for parsed Integer.
     * @return {@link Integer} parsed from provided String.
     * @throws IllegalArgumentException when parsed Integer is out of the provided threshold.
     * @throws NumberFormatException when String doesn't contain Integer values!
     */
    public static int getIntValue(String Value, String Argument, int MinValue, int MaxValue) throws IllegalArgumentException, NumberFormatException {
        int IntValue;
        try {
            IntValue = Integer.parseInt(Value);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Invalid String (" + Value + ") passed into the argument " + Argument + "!");
        }
        if (IntValue < MinValue || IntValue > MaxValue) {
            throw new IllegalArgumentException(
                    "Value " +
                            ((IntValue < MinValue)? "below minimal": "above maximum") +
                            " threshold passed into the argument \"-" + Argument + "! " +
                            ((IntValue < MinValue)? "Minimal": "Maximal") +
                            " allowed value is " +
                            ((IntValue < MinValue)? MinValue: MaxValue)
            );
        }
        return IntValue;
    }

    /**
     * Used to validate if provided Path exists.
     * @param path {@link String} with Path to validate.
     * @param Argument {@link String} with Name of the argument to provide in Exception.
     * @return {@link String} with provided Path, if it exists.
     * @throws FileNotFoundException when provided Path doesn't exist.
     */
    public static String validatePath(String path, String Argument, boolean override) throws FileNotFoundException, UnexpectedException {
        Path ArgumentPath = Path.of(path);
        if (!Files.exists(ArgumentPath)) {
            if (override) {
                FileUtils.createRequiredPath(ArgumentPath);
                return path;
            }
            logger.error("Specified " + Argument + " does not exists!");
            logger.error(ArgumentPath.toAbsolutePath().toString());
            throw new FileNotFoundException("Specified " + Argument + " does not exists!");
        }
        return path;
    }

    /**
     * Used to validate if provided Path exists.
     * @param path {@link String} with Path to validate.
     * @param Argument {@link String} with Name of the argument to provide in Exception.
     * @return {@link String} with provided Path, if it exists.
     * @throws FileNotFoundException when provided Path doesn't exist.
     */
    public static String validatePath(String path, String Argument) throws FileNotFoundException, UnexpectedException {
        return validatePath(path, Argument, false);
    }

    /**
     * Used to validate selected mode!
     * @param Mode Mode to verify.
     * @return boolean True when mode is available.
     */
    public static boolean validateMode(String Mode) {
        return Arrays.asList(modes).contains(Mode);
    }

}
