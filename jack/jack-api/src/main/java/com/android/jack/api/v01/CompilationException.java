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

package com.android.jack.api.v01;

import javax.annotation.Nonnull;

/**
 * A fatal problem that caused Jack to abort the compilation. The problem should already have
 * reported, so it is safe to ignore its message.
 */
public class CompilationException extends Exception {
  private static final long serialVersionUID = 1L;

  /**
   * Construct the exception with no details.
   */
  public CompilationException() {
    super();
  }

  /**
   * Construct the exception with a {@link String} message.
   * @param message the message
   */
  public CompilationException(@Nonnull String message) {
    super(message);
  }

  /**
   * Construct the exception with a {@link String} message and a {@link Throwable} cause.
   * @param message the message
   * @param cause the cause
   */
  public CompilationException(@Nonnull String message, @Nonnull Throwable cause) {
    super(message, cause);
  }

  /**
   * Construct the exception with a {@link Throwable} cause.
   * @param cause the cause
   */
  public CompilationException(@Nonnull Throwable cause) {
    super(cause);
  }
}
