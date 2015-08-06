package edu.sc.seis.launch4j;

/**
 * Use for backwards compatibility instead  of the incubating OperatingSystem class
 */
enum OS {
    MacOsX, Linux, Windows, BSD, Solaris, Other;

    public static final OS CURRENT = getOS();

    private static OS getOS() {
        final String osName = System.getProperty("os.name", "other").toLowerCase(Locale.ENGLISH);
        final OS type;
        if ((osName.contains("mac")) || (osName.contains("darwin"))) {
            type = MacOsX;
        } else if (osName.contains("nux") || osName.contains("nix")) {
            type = Linux;
        } else if (osName.contains("win")) {
            type = Windows;
        } else if (osName.contains("bsd")) {
            type = BSD;
        } else if (osName.contains("solaris") || (osName.contains("sun"))) {
            type = Solaris;
        } else {
            type = Other;
        }
        return type;
    }
    }

