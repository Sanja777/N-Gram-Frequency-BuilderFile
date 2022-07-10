package ie.atu.sw;

/*
 * ANSI escape sequences are a standard for controlling cursor location, colour, 
 * font styling, and other options on DOS, Mac and Linux terminals. The ANSI escape 
 * codes are formatted as follows:
 * 
 *  	[<PREFIX>];[<COLOR>];[<TEXT DECORATION>]
 *  
 *  See https://en.wikipedia.org/wiki/ANSI_escape_code for a decent description.
 */
public enum ConsoleMenuOption {
    // Reset
    INPUT_DIRECTORY(1),
    N_GRAM_SIZE(2),
    OUTPUT_FILE(3),
    BUILD_N_GRAMS(4),
    QUIT(5),
    INVALID_ENTRY(101);

    private final int value;

    ConsoleMenuOption(final int newValue) {
        value = newValue;
    }

    public int getValue() {
        return value;
    }

    public static ConsoleMenuOption fromInteger(int x) {
        switch (x) {
            case 1: {
                return INPUT_DIRECTORY;
            }
            case 2: {
                return N_GRAM_SIZE;
            }
            case 3: {
                return OUTPUT_FILE;
            }
            case 4: {
                return BUILD_N_GRAMS;
            }
            case 5: {
                return QUIT;
            }
            default: {
                return INVALID_ENTRY;
            }
        }
    }

    public static ConsoleMenuOption fromInteger(String x) {
        try {
            int value = Integer.parseInt(x);
            return fromInteger(value);
        } catch (Exception e) {
            return INVALID_ENTRY;
        }
        
    }
}