/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.intrahealth.elasticsearch.plugin.similarity;

import info.debatty.java.stringsimilarity.interfaces.NormalizedStringSimilarity;
//import info.debatty.java.stringsimilarity.interfaces.NormalizedStringDistance;

import net.jcip.annotations.Immutable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.logging.Logger;

@Immutable
public class DateDamerau implements NormalizedStringSimilarity {

    /**
     * Checks if the day part of date is transposed
     * 
     * @param s1 The first string to compare.
     * @param s2 The second string to compare.
     * @return The computed distance.
     * @throws NullPointerException if s1 or s2 is null.
     */
    @Override
    public final double similarity(String s1, String s2) {

        if (s1 == null) {
            throw new NullPointerException("s1 must not be null");
        }

        if (s2 == null) {
            throw new NullPointerException("s2 must not be null");
        }

        if (s1.equals(s2)) {
            return 1;
        }

        String pattern = "\\d{4}-\\d{2}-\\d{2}";

        if (isValidFormat(s1, pattern) && (isValidFormat(s2, pattern))) {

            int[] array1 = createDateArray(s1);
            int[] array2 = createDateArray(s2);

            return compareArrays(array1, array2);

        } else {
            throw new IllegalArgumentException("Invalid format: " + s1 + " " + s2);
        }

    }

    private static boolean isValidFormat(String input, String pattern) {
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(input);
        return matcher.matches();
    }

    // Function to compare two arrays
    private static double compareArrays(int[] arr1, int[] arr2) {
        double weightYear = 5.0;
        double weightMonth = 4.0;
        double weightDay = 3.0;

        // Step 1: Calculate values for each component
        double yearValue = compareComponents(arr1[0], arr2[0]);
        double monthValue = compareComponents(arr1[1], arr2[1]);
        double dayValue = compareComponents(arr1[2], arr2[2]);

        // Step 2: Calculate main value (value A)
        double mainValue = ((yearValue * weightYear) + (monthValue * weightMonth) + (dayValue * weightDay))
                / (weightYear + weightMonth + weightDay);

        // Step 3: Calculate value B
        double valueB = 0.0;
        if (arr1[0] == arr2[0] && arr1[2] == arr2[1] && arr1[1] == arr2[2]) {
            valueB = 0.9;
        }

        // Step 4: Return the greater of value A and value B
        return Math.max(mainValue, valueB);
    }

    // Function to compare individual components
    private static double compareComponents(int component1, int component2) {
        if (component1 == component2) {
            return 1.0;
        } else if (isTransposition(component1, component2)) {
            return 0.6;
        } else {
            return 0.0;
        }
    }

    // Function to check if two numbers are transpositions of each other
    private static boolean isTransposition(int num1, int num2) {
        // Handle years
        if (num1 > 31 && num2 > 31) {
            String strNum1 = String.valueOf(num1);
            String strNum2 = String.valueOf(num2);

            // Check if the first two digits are the same
            if (strNum1.charAt(0) == strNum2.charAt(0) && strNum1.charAt(1) == strNum2.charAt(1)) {
                // Extract the last two digits from both numbers
                num1 = Integer.parseInt(strNum1.substring(2));
                num2 = Integer.parseInt(strNum2.substring(2));
            } else {
                return false;
            }
        }
        return num1 % 10 == num2 / 10 && num1 / 10 == num2 % 10;
    }

    private static int[] createDateArray(String dateString) {
        String[] components = dateString.split("-");
        int[] dateArray = new int[components.length];
        for (int i = 0; i < components.length; i++) {
            dateArray[i] = Integer.parseInt(components[i]);
        }
        return dateArray;
    }

}
