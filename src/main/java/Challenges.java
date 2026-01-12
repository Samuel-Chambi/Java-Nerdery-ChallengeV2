/* (C)2024 */

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/* (C)2024 */
public class Challenges {

    /* *****
    Challenge 1

    "Readable Time"

    The function "readableTime" accepts a positive number as argument,
    you should be able to modify the function to return the time from seconds
    into a human readable format.

    Example:

    Invoking "readableTime(3690)" should return "01:01:30" (HH:MM:SS)
    ***** */

    public String readableTime(Integer seconds) {
        // YOUR CODE HERE...
        // For this, firstly I get the number of hours from the seconds dividing it by 3600
        String hh = (seconds / 3600 > 9) ? String.valueOf(seconds / 3600) : '0' + String.valueOf(seconds / 3600);
        seconds %= 3600;
        // So, the rest after the division must belong to the minutes or seconds section, for get the number of minutes apply the same operation but now dividing by 60
        String mm = (seconds / 60 > 9) ? String.valueOf(seconds / 60) : '0' + String.valueOf(seconds / 60);
        seconds %= 60;
        // At the end, the rest after both operations belongs to the number of seconds.
        String ss = (seconds > 9) ? String.valueOf(seconds) : '0' + String.valueOf(seconds);
        return String.join(":", hh, mm, ss);
    }

    /* *****
    Challenge 2

    "Circular Array"

    Given the following array "COUNTRY_NAMES", modify the function "circularArray"
    to return an array that meets the following criteria:

    - The index number passed to the function should be the first element in the resulting array
    - The resulting array must have the same length as the initial array
    - The value of the argument "index" will always be a positive number

    Example:

    Invoking "circularArray(2)" should return "["Island", "Japan", "Israel", "Germany", "Norway"]"
    ***** */

    public String[] circularArray(int index) {
        String[] COUNTRY_NAMES = {"Germany", "Norway", "Island", "Japan", "Israel"};
        index %= COUNTRY_NAMES.length;
        String[] AUX_LIST = new String[5];
        System.arraycopy(COUNTRY_NAMES, 0, AUX_LIST, 0, 5);
        /*
         * For this challenge, I use the index argument as an offset between two pointers.
         * The first one (in AUX_LIST), will init in the index position while the second (in COUNTRY_NAMES) will init in position 0.
         * For manage the overflow in the first pointer, I use the modular operator to simulate the circular road.
         * */
        for (int i = 0; i < COUNTRY_NAMES.length; i++) COUNTRY_NAMES[i] = AUX_LIST[(i + index) % COUNTRY_NAMES.length];
        return COUNTRY_NAMES;
    }
    /* *****
    Challenge 3

    "Own Powers"

    The function "ownPower" accepts two arguments. "number" and "lastDigits".

    The "number" indicates how long is the series of numbers you are going to work with, your
    job is to multiply each of those numbers by their own powers and after that sum all the results.

    "lastDigits" is the length of the number that your function should return, as a string!.
    See example below.

    Example:

    Invoking "ownPower(10, 3)" should return "317"
    because 1^1 + 2^2 + 3^3 + 4^4 + 5^5 + 6^6 + 7^7 + 8^8 + 9^9 + 10^10 = 10405071317
    The last 3 digits for the sum of powers from 1 to 10 is "317"
    ***** */

    public String ownPower(int number, int lastDigits) {
        /* 
            One 'critical' observation for this problem is that for a number value up to 15 is already a huge value.
            That not fits on Long limits (until around 10^18)
            So for this, I decided to use the BigInteger class instead.
        */
        BigInteger digits = BigInteger.TEN.pow(lastDigits);
        BigInteger accumulator = BigInteger.ZERO;
        for (int i = 1; i <= number; i++) {
            BigInteger adds = BigInteger.valueOf(i).modPow(BigInteger.valueOf(i), digits);
            accumulator = accumulator.add(adds).mod(digits);
        }
        StringBuilder result = new StringBuilder(accumulator.toString());
        while (result.length() < lastDigits) result.insert(0, '0');
        return result.toString();
    }

    /* *****
    Challenge 4

    "Sum of factorial digits"

    A factorial (x!) means x! * (x - 1)... * 3 * 2 * 1.
    For example: 10! = 10 × 9 × ... × 3 × 2 × 1 = 3628800

    Modify the function "digitSum" to return a number that
    equals to the sum of the digits in the result of 10!

    Example:

    Invoking "digitSum(10)" should return "27".
    Since 10! === 3628800 and you sum 3 + 6 + 2 + 8 + 8 + 0 + 0
    ***** */

    public Integer digitSum(int n) {
        // As the previous challenge, here the function will work with big values also.
        BigInteger accumulator = BigInteger.ONE;
        for (int i = 1; i <= n; i++) accumulator = accumulator.multiply(BigInteger.valueOf(i));
        return accumulator.toString()
                .chars()
                .map(Character::getNumericValue)
                .sum();
    }

    /**
     * Decryption.
     * Create a decryption function that takes as parameter an array of ASCII values. The addition between values is the ascii value decrypted.
     * decrypt([ 72, 33, -73, 84, -12, -3, 13, -13, -68 ]) ➞ "Hi there!"
     * H = 72, the sum of H 72 and 33 gives 105 which ascii value is i;
     * The function must return the string encoded using the encryption function below.
     *
     * @param ascivalues hand, player2 hand
     */
    public String decrypt(List<Integer> ascivalues) {
        // As the statement says, each value depends on directly from the previous value, so for manage that, I use an auxiliary variable called 'accumulator'
        StringBuilder result = new StringBuilder();
        int accumulator = 0;
        for (Integer i : ascivalues) {
            accumulator += i;
            result.append((char) accumulator); // Here, 'accumulator' is cast as a char, for its respective ASCII value
        }
        return result.toString();
    }

    /**
     * Encryption Function.
     * Create am encryption function that takes a string and converts into an array of ASCII character values.
     * encrypt("Hello") ➞ [72, 29, 7, 0, 3]
     * // H = 72, the difference between the H and e is 29
     * The function must return an array of integer ascii values.
     *
     * @param text hand, player2 hand
     */
    public List<Integer> encrypt(String text) {
        List<Integer> result = new ArrayList<>();
        // The idea is similar to the last one but for this I do not accumulate the sum, only store the last ASCII value for the next use.
        int prev = 0;
        for (int i = 0; i < text.length(); i++) {
            int val = text.charAt(i); // Declaring 'val' in this scope, automatically cast the ASCII value to int.
            result.add(val - prev);
            prev = val;
        }
        return result;
    }
}
