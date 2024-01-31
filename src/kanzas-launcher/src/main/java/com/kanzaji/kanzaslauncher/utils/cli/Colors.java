package com.kanzaji.kanzaslauncher.utils.cli;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public enum Colors {
    RED(196),
    GREEN(46),
    BLUE(34),
    WHITE(37),
    DEFAULT(0);

    private final int code;
    Colors(int code) {
        this.code = code;
    }
    @Contract(pure = true)
    public @NotNull String foreground() {
        return "\u001B[38;5;" + code + "m";
    }

    @Contract(pure = true)
    public @NotNull String background() {
        return "\u001B[48;5;" + code + "m";
    }

    public static String reset() {
        return DEFAULT.foreground() + DEFAULT.background();
    }
}
