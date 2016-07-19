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
 *
 * THIS FILE WAS GENERATED BY codergen. EDIT WITH CARE.
 */
package com.android.tools.rpclib.any;

import com.android.tools.rpclib.schema.*;
import org.jetbrains.annotations.NotNull;

import com.android.tools.rpclib.binary.BinaryClass;
import com.android.tools.rpclib.binary.BinaryObject;
import com.android.tools.rpclib.binary.Decoder;
import com.android.tools.rpclib.binary.Encoder;
import com.android.tools.rpclib.binary.Namespace;

import java.io.IOException;

final class Int16Slice extends Box implements BinaryObject {
    @Override
    public Object unwrap() {
        return getValue();
    }

    //<<<Start:Java.ClassBody:1>>>
    private short[] mValue;

    // Constructs a default-initialized {@link Int16Slice}.
    public Int16Slice() {}


    public short[] getValue() {
        return mValue;
    }

    public Int16Slice setValue(short[] v) {
        mValue = v;
        return this;
    }

    @Override @NotNull
    public BinaryClass klass() { return Klass.INSTANCE; }


    private static final Entity ENTITY = new Entity("any", "int16Slice", "", "");

    static {
        ENTITY.setFields(new Field[]{
            new Field("Value", new Slice("", new Primitive("int16", Method.Int16))),
        });
        Namespace.register(Klass.INSTANCE);
    }
    public static void register() {}
    //<<<End:Java.ClassBody:1>>>
    public enum Klass implements BinaryClass {
        //<<<Start:Java.KlassBody:2>>>
        INSTANCE;

        @Override @NotNull
        public Entity entity() { return ENTITY; }

        @Override @NotNull
        public BinaryObject create() { return new Int16Slice(); }

        @Override
        public void encode(@NotNull Encoder e, BinaryObject obj) throws IOException {
            Int16Slice o = (Int16Slice)obj;
            e.uint32(o.mValue.length);
            for (int i = 0; i < o.mValue.length; i++) {
                e.int16(o.mValue[i]);
            }
        }

        @Override
        public void decode(@NotNull Decoder d, BinaryObject obj) throws IOException {
            Int16Slice o = (Int16Slice)obj;
            o.mValue = new short[d.uint32()];
            for (int i = 0; i <o.mValue.length; i++) {
                o.mValue[i] = d.int16();
            }
        }
        //<<<End:Java.KlassBody:2>>>
    }
}
