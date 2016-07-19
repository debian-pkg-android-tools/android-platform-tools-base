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

package com.android.build.gradle.tasks.fd;

import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.ASMifier;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class InjectBootstrapApplicationTaskTest {

    @Test
    public void test() {
        // todo
    }

    private static String disassemble(File expected) throws IOException {
        ClassReader reader = new ClassReader(new FileInputStream(expected));
        StringWriter writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        try {
            reader.accept(new TraceClassVisitor(printWriter), 0);
        } finally {
            printWriter.close();
        }
        return writer.toString();
    }

    private static String asmify(File expected) throws IOException {
        ClassReader reader = new ClassReader(new FileInputStream(expected));
        StringWriter writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        try {
            reader.accept(new TraceClassVisitor(null, new ASMifier(), printWriter), 0);
        } finally {
            printWriter.close();
        }
        printWriter.close();
        return writer.toString();
    }

}
