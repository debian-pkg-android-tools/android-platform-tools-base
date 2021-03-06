/*
 * Copyright (C) 2009 The Android Open Source Project
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
 */
#include <jni.h>
#include <string>
#include "testBuildType.h"
#include "testProductFlavor.h"

/* This is a JNI example where we use a native method to return a new VM
 * string where the string is different depending on the build type and
 * product flavor.
 * The corresponding Java source file located at:
 *   apps/samples/hello-jni/project/src/com/example/hellojni/HelloJni.java
 */

extern "C"
jstring
Java_com_example_simplejni_SimpleJni_buildTypeFromJni(JNIEnv* env, jobject thiz)
{
    return env->NewStringUTF(BUILD_TYPE);
}

extern "C"
jstring
Java_com_example_simplejni_SimpleJni_productFlavorFromJni(JNIEnv* env, jobject thiz)
{
    return env->NewStringUTF(PRODUCT_FLAVOR);
}

extern "C"
jstring
Java_com_example_simplejni_SimpleJni_variantFromJni(JNIEnv* env, jobject thiz)
{
    return env->NewStringUTF(VARIANT);
}
