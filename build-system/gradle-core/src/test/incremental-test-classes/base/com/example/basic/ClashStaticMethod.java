/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.basic;

/**
 * Check that clashing method names and signatures are dealt with properly.
 *
 * In this case, without deduplication logic:
 * public String build(String string2)
 *         -> public static String build(ClashStaticMethod this, String string2)
 * and public static String build(ClashStaticMethod obj, String string2)
 *         remains the same.
 *
 *
 */
public class ClashStaticMethod {

    private final String string1;

    public ClashStaticMethod(String string1) {
        this.string1 = string1;
    }

    // The two methods that truly crash.
    public String append(String string2) {
        return append(this, string2) + "_instance";
    }

    public static String append(ClashStaticMethod obj, String string2) {
        return obj.string1 + string2;
    }

    // Some other methods which do not
    public String append(int x) {
        return append(this, x);
    }

    public static String append(ClashStaticMethod obj, long x) {
        return append(obj, Long.toString(x));
    }
}
