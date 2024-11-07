import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;

/**
 * A library containing useful utility functions
 * @author Aleksandr Stinchcomb
 * @version 1.0
 */
abstract class Utility {
    // -- Attributes -- \\
    private static final String NUMERIC_VALUE = "123456789";

    private static final BigDecimal TO_RADIANS = new BigDecimal("3.141592653589793").divide(new BigDecimal("180"), MathContext.DECIMAL128);
    private static final BigDecimal TO_DEGREES = new BigDecimal("180").divide(new BigDecimal("3.141592653589793"), MathContext.DECIMAL128);

    // -- Methods -- \\
    /**
     * Appends trailing {@code 0}'s to the end of {@code str} so it matches the given {@code precision} length
     * @param number the {@code String} to lengthen to {@code precision}
     * @param precision the precision of {@code str} in length
     * @return a {@code String} with the given {@code precision} 
     */
    private static String pad(String number, byte precision) {
        return (number.length() < precision) ? number+("0").repeat(Math.abs(number.length()-precision)) : number.substring(0, precision);
    }

    /**
     * Returns {@code notation} adjusted for {@code precision}
     * @param notation the {@code String} to correct for {@code precision}
     * @param precision the precision of {@code notation} in length
     * @return a {@code String} with the given {@code precision} with the given notation style
     */
    private static String preciseNotation(String notation, byte precision) {
        int point = notation.indexOf('.');
        int e = notation.indexOf('e');
        
        if (precision > -1) {
            if (point > -1) {
                String decimal = (e-point == 1) ? (precision > 0) ? "."+("0").repeat(precision) : "" : "."+pad(notation.substring(point+1, e), precision);
                return notation.substring(0, point) + decimal + notation.substring(e);
            } else {
                String decimal = (precision > 0) ? "."+("0").repeat(precision) : "";
                return notation.substring(0, e) + decimal + notation.substring(e);
            }
        }
        return notation;
    }

    /**
     * Takes a number {@code value} and limits it to range of {@code min} to {@code max}
     * @return an int at or in-between the values of {@code min} and {@code max}
     */
    public static int clamp(int value, int min, int max) {
        return Math.min(Math.max(value, min), max);
    }
    /**
     * Takes a number {@code value} and limits it to range of {@code min} to {@code max}
     * @return a long at or in-between the values of {@code min} and {@code max}
     */
    public static long clamp(long value, long min, long max) {
        return Math.min(Math.max(value, min), max);
    }
    /**
     * Takes a number {@code value} and limits it to range of {@code min} to {@code max}
     * @return a float at or in-between the values of {@code min} and {@code max}
     */
    public static float clamp(float value, float min, float max) {
        return Math.min(Math.max(value, min), max);
    }
    /**
     * Takes a number {@code value} and limits it to range of {@code min} to {@code max}
     * @return a double at or in-between the values of {@code min} and {@code max}
     */
    public static double clamp(double value, double min, double max) {
        return Math.min(Math.max(value, min), max);
    }

    /**
     * Returns a boolean indicating if {@code num} is even
     * @param num the number to check
     * @return {@code true} if {@code num} is even and {@code false} if it is odd
     */
    public static boolean isEven(int num) {
        return (num % 2 == 0);
    }
    /**
     * Returns a boolean indicating if {@code num} is even
     * @param num the number to check
     * @return {@code true} if {@code num} is even and {@code false} if it is odd
     */
    public static boolean isEven(long num) {
        return (num % 2 == 0);
    }
    /**
     * Returns a boolean indicating if {@code num} is even
     * @param num the number to check
     * @return {@code true} if {@code num} is even and {@code false} if it is odd
     */
    public static boolean isEven(float num) {
        return (num % 2 == 0);
    }
    /**
     * Returns a boolean indicating if {@code num} is even
     * @param num the number to check
     * @return {@code true} if {@code num} is even and {@code false} if it is odd
     */
    public static boolean isEven(double num) {
        return (num % 2 == 0);
    }

    /**
     * Rounds {@code number} to the given place value
     * @param number the number to be rounded
     * @param placeOffset the offset from the ones place<p>{@code -}: for whole places<p>{@code +}: for decimal places
     * @return a number rounded to the {@code 10^-placeOffset} place
     */
    public static BigDecimal round(BigDecimal number, int placeOffset) {
        String num = number.toPlainString();
        int precision = num.indexOf(".");
        int absPlace = Math.abs(placeOffset);

        BigDecimal transform = new BigDecimal(10).pow(absPlace);
        MathContext context = new MathContext((precision > -1) ? (precision + placeOffset) : num.length() - (num.length() - placeOffset), RoundingMode.HALF_UP);

        return (placeOffset == 0) ? number.round(context) : (placeOffset > 0) ? number.multiply(transform).round(context).divide(transform) : number.divide(transform).round(context).multiply(transform);
    }

    /**
     * Finds the greatest common factor of {@code x} and {@code y}
     * @param x the first number
     * @param y the second number
     * @return the greatest common factor of {@code x} and {@code y}
     */
    public static long gcf(long x, long y) {
        return (y == 0) ? x : gcf(y, x%y);
    }

    /**
     * Finds the least common multiple of {@code x} and {@code y}
     * @param x the first number
     * @param y the second number
     * @return the least common multiple of {@code x} and {@code y}
     */
    public static long lcm(long x, long y) {
        return (x / gcf(x, y)) * y;
    }

    /**
     * Calculates the factorial of a number
     * @param number the number to calculate the factorial of
     * @return the factorial of {@code number}
     */
    public static long fact(long number) {
        for (long n = number-1; n > 1; n--) {
            number *= n;
        }
        return number;
    }

    public static double logGamma(double x) {
        double tmp = (x - 0.5) * Math.log(x + 4.5) - (x + 4.5);
        double ser = 1.0 + 76.18009173    / (x + 0)   - 86.50532033    / (x + 1)
                        + 24.01409822    / (x + 2)   -  1.231739516   / (x + 3)
                        +  0.00120858003 / (x + 4)   -  0.00000536382 / (x + 5);
        return tmp + Math.log(ser * Math.sqrt(2 * Math.PI));
    }
    public static double gamma(double x) { return Math.exp(logGamma(x)); }

    /**
     * Converts {@code degrees} to radians
     * @param degrees the angle measurement in degrees
     * @return the angle measurement in radians
     */
    public static BigDecimal toRadians(BigDecimal degrees) {
        return degrees.multiply(TO_RADIANS, MathContext.DECIMAL128);
    }

    /**
     * Converts {@code radians} to degrees
     * @param radians the angle measurement in radians
     * @return the angle measurement in degrees
     */
    public static BigDecimal toDegrees(BigDecimal radians) {
        return radians.multiply(TO_DEGREES, MathContext.DECIMAL128);
    }

    /**
     * Returns a string representation of {@code number} adjusted for {@code precision}
     * @param number the number to correct for {@code precision}
     * @param precision the precision of {@code number} in length
     * @return a {@code String} with the given {@code precision} with the given notation style
     */
    public static String notation(BigDecimal number, byte precision) {
        number = round(number, (precision > -1) ? precision : 16);

        String numberString = number.toPlainString();
        int decimalIndex = numberString.indexOf(".");

        if (precision == -1 && decimalIndex > -1 && !hasValue(numberString.substring(decimalIndex+1, numberString.length()))) {
            numberString = numberString.substring(0, decimalIndex);
        }
        return numberString;
    }

    /**
     * Converts a number to scientific notation
     * @param number the number to display in scientific notation
     * @param precision the decimal place value to round to
     * @return a {@code String} representing the number in scientific notation
     */
    public static String sciNotation(BigDecimal number, byte precision) {
        DecimalFormat converter = new DecimalFormat("0."+("#").repeat((precision == -1) ? 16 : precision)+"E0");
        String notation = converter.format(round(number, (precision > -1) ? precision+1 : 16)).replace('E', 'e');

        String[] test = notation.replaceAll("e.", "").split("\\.");
        return (test.length == 1 || test[1].length() < precision) ? preciseNotation(notation, precision) : notation.replaceAll("\\.e", "e");
    }

    /**
     * Converts a number to engineering notation
     * @param number the number to display in engineering notation
     * @param precision the decimal place value to round to
     * @return a {@code String} representing the number in engineering notation
     */
    public static String engNotation(BigDecimal number, byte precision) {
        DecimalFormat converter = new DecimalFormat("##0."+("#").repeat((precision == -1) ? 16 : precision)+"E0");
        String notation = converter.format(round(number, (precision > -1) ? precision+2 : 16)).replace('E', 'e');

        String[] test = notation.replaceAll("e.", "").split("\\.");
        return (test.length == 1 || test[1].length() < precision) ? preciseNotation(notation, precision) : notation.replaceAll("\\.e", "e");
    }

    /**
     * Returns true if numerical string {@code str} has any digits of non-zero value
     * @param str a string containing numerical digits
     * @return boolean indicating if the string has value
     */
    public static boolean hasValue(char ch) {
        return NUMERIC_VALUE.indexOf(ch) > -1;
    }
    /**
     * Returns true if numerical string {@code str} has any digits of non-zero value
     * @param str a string containing numerical digits
     * @return boolean indicating if the string has value
     */
    public static boolean hasValue(String str) {
        for (char ch : str.toCharArray()) {
            if (hasValue(ch)) {return true;}
        }
        return false;
    }

    /**
     * Returns the index of the first digit with value<p>Returns {@code -1} if there is no numeric value
     * @param str a string containing numerical digits
     * @return index of the first digit with value
     */
    public static int valueIndex(String str) {
        for (int index = 0; index < str.length(); index++) {
            if (hasValue(str.charAt(index))) {return index;}
        }
        return -1;
    }

    /**
     * Returns the index of the first digit with value<p>Returns {@code -1} if there is no numeric value
     * @param str a string containing numerical digits
     * @param index the index to start looking for numerical digits with value from
     * @return index of the first digit with value
     */
    public static int valueIndex(String str, int index) {
        for (int i = index; i < str.length(); i++) {
            if (hasValue(str.charAt(i))) {return i;}
        }
        return -1;
    }

    /**
     * Checks if {@code number} has decimal (fractional) value or not
     * @param number the number to check
     * @return a {@code boolean} indicating if {@code number} has decimal value or not
     */
    public static boolean isDecimal(BigDecimal number) {
        String num = number.toPlainString();
        int point = num.indexOf(".");
        
        return (point > -1) ? (valueIndex(num, point+1) > -1) : false;
    }

    /**
     * Strips leading and trailing zeros from a numerical string
     * @param numString the string to strip
     * @return a string with no leading or trailing zeros
     */
    public static String strip(String numString) {
        final int LENGTH = numString.length()-1;
        final String SIGN = (numString.charAt(0) == '-') ? "-" : "";

        int start = -1;
        int end = -1;
        for (int index = 0; index < LENGTH; index++) {
            if (start == -1 && hasValue(numString.charAt(index))) {start = index;}
            if (end == -1 && hasValue(numString.charAt(LENGTH-index))) {end = (LENGTH-index+1);}
            if (start > -1 && end > -1) {break;}
        }
        return (start > -1 && end > -1) ? SIGN+numString.substring(start, end) : "";
    }

    /**
     * Removes trailing zeros from a numerical string
     * @param numString the string to alter
     * @return a string without trailing zeros
     */
    public static String trailing(String numString) {
        final int LENGTH = numString.length()-1;
        final String SIGN = (numString.charAt(0) == '-') ? "-" : "";
        
        int end = -1;
        for (int index = LENGTH; index > -1; index--) {
            if (end == -1 && hasValue(numString.charAt(index))) {end = index+1; break;}
        }
        print(numString);
        return (end > -1) ? SIGN+numString.substring(0, end) : "";
    }

    public static void print(Object... objects) {
        for (Object object : objects) {
            if (object.getClass().isArray()) {
                System.out.println(Arrays.toString((Object[])object));
            } else {
                System.out.print(object + " ");
            }
        }
        System.out.println();
    }
}