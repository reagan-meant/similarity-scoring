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

import info.debatty.java.stringsimilarity.interfaces.MetricStringDistance;

import net.jcip.annotations.Immutable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.logging.Logger;
import java.util.Arrays;

@Immutable
public class DateDamerau implements MetricStringDistance {

    /**
     * Checks if the day part of date is transposed
     * 
     * @param s1 The first string to compare.
     * @param s2 The second string to compare.
     * @return The computed distance.
     * @throws NullPointerException if s1 or s2 is null.
     */
    public final double distance(String s1, String s2) {
        Logger logger = Logger.getLogger(DateDamerau.class.getName());

        if (s1 == null) {
            throw new NullPointerException("s1 must not be null");
        }

        if (s2 == null) {
            throw new NullPointerException("s2 must not be null");
        }

        if (s1.equals(s2)) {
            return 0;
        }

        String pattern = "\\d{4}-\\d{2}-\\d{2}";

        if (isValidFormat(s1, pattern) && (isValidFormat(s2, pattern))) {

            int lastHyphenIndex1 = s1.lastIndexOf("-");
            int lastHyphenIndex2 = s2.lastIndexOf("-");

            if (lastHyphenIndex1 != -1 && lastHyphenIndex2 != -1) {
                String stringA = s1.substring(0, lastHyphenIndex1);
                String stringB = s1.substring(lastHyphenIndex1 + 1);

                String stringC = s2.substring(0, lastHyphenIndex2);
                String stringD = s2.substring(lastHyphenIndex2 + 1);

                if (stringA.equals(stringC)) {

                    s1 = stringB;
                    s2 = stringD;
                    // Convert strings to char arrays
                    char[] charArray1 = s1.toCharArray();
                    char[] charArray2 = s2.toCharArray();

                    // Sort the char arrays
                    Arrays.sort(charArray1);
                    Arrays.sort(charArray2);
                    if (Arrays.equals(charArray1, charArray2)){
                        return 2;
                    }

                    return 3;
                    }else{
                    return 3;
                }
                

            } else {
                throw new IllegalArgumentException("Invalid format: " + s1 + " " + s2);

            }
        }else {
            throw new IllegalArgumentException("Invalid format: " + s1 + " " + s2);
        }

    }

    private static boolean isValidFormat(String input, String pattern) {
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(input);
        return matcher.matches();
    }

}
