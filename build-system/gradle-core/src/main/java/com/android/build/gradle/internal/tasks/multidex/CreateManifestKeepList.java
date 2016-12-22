/*
 * Copyright (C) 2014 The Android Open Source Project
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

package com.android.build.gradle.internal.tasks.multidex;

import com.android.annotations.NonNull;
import com.android.annotations.Nullable;
import com.android.annotations.VisibleForTesting;
import com.android.build.gradle.internal.scope.ConventionMappingHelper;
import com.android.build.gradle.internal.scope.TaskConfigAction;
import com.android.build.gradle.internal.scope.VariantScope;
import com.android.build.gradle.internal.tasks.DefaultAndroidTask;
import com.android.build.gradle.internal.variant.BaseVariantOutputData;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;

import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.ParallelizableTask;
import org.gradle.api.tasks.TaskAction;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Generate the keep list from the manifest for build tools &lt; 24.
 *
 * <p>For build tools &ge; 24 aapt generates this file (-D flag).
 */
@ParallelizableTask
public class CreateManifestKeepList extends DefaultAndroidTask {

    private File manifest;

    private File outputFile;

    private Filter filter;

    @InputFile
    public File getManifest() {
        return manifest;
    }

    public void setManifest(File manifest) {
        this.manifest = manifest;
    }

    @OutputFile
    public File getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(File outputFile) {
        this.outputFile = outputFile;
    }

    /**
     * Register the filter to remove classes that would otherwise be kept in the main dex by
     * the manifest.
     *
     * @deprecated Will be replaced in a subsequent version
     */
    @Deprecated
    public void setFilter(@NonNull Filter filter) {
        getLogger().warn("setFilter will be replaced in a subsequent version.");
        this.filter = filter;
        // The filter cannot be a task input. Force the task to always run if it is set.
        getOutputs().upToDateWhen(task -> false);
    }

    @TaskAction
    public void generateKeepListFromManifest()
            throws ParserConfigurationException, SAXException, IOException {
        generateKeepListFromManifest(getManifest(), getOutputFile(), filter);
    }

    @VisibleForTesting
    static void generateKeepListFromManifest(
            @NonNull File manifest,
            @NonNull File outputFile,
            @Nullable Filter filter) throws ParserConfigurationException, SAXException,
            IOException {
        SAXParser parser = SAXParserFactory.newInstance().newSAXParser();

        try (Writer out = new BufferedWriter(new FileWriter(outputFile))) {
            parser.parse(manifest, new ManifestHandler(out, filter));
        }
    }

    private static final String DEFAULT_KEEP_SPEC = "{ <init>(); }";

    private static final Map<String, String> KEEP_SPECS = ImmutableMap.<String, String>builder()
            .put("application", "{\n"
                    + "    <init>();\n"
                    + "    void attachBaseContext(android.content.Context);\n"
                    + "}")
            .put("activity", DEFAULT_KEEP_SPEC)
            .put("service", DEFAULT_KEEP_SPEC)
            .put("receiver", DEFAULT_KEEP_SPEC)
            .put("provider", DEFAULT_KEEP_SPEC)
            .put("instrumentation", DEFAULT_KEEP_SPEC).build();

    private static class ManifestHandler extends DefaultHandler {
        private Writer out;
        private final Filter filter;

        ManifestHandler(@NonNull Writer out, @Nullable Filter filter) {
            this.out = out;
            this.filter = filter;
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attr) {
            String keepSpec = CreateManifestKeepList.KEEP_SPECS.get(qName);
            if (!Strings.isNullOrEmpty(keepSpec) &&
                    (filter == null || filter.keep(qName, makeAttrMap(attr)))) {
                keepClass(attr.getValue("android:name"), keepSpec, out);
                // Also keep the original application class when using instant-run.
                keepClass(attr.getValue("name"), keepSpec, out);
            }
        }

        private static ImmutableMap<String, String> makeAttrMap(Attributes attr) {
            ImmutableMap.Builder<String, String> attrMap = ImmutableMap.builder();
            for (int i = 0; i < attr.getLength(); i++) {
                attrMap.put(attr.getQName(i), attr.getValue(i));
            }
            return attrMap.build();
        }
    }

    private static void keepClass(
            @Nullable String className,
            @NonNull String keepSpec,
            @NonNull Writer out) {
        if (className != null) {
            try {
                out.write("-keep class ");
                out.write(className);
                out.write(" ");
                out.write(keepSpec);
                out.write("\n");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class ConfigAction implements TaskConfigAction<CreateManifestKeepList> {

        private final VariantScope scope;

        public ConfigAction(@NonNull VariantScope scope) {
            this.scope = scope;
        }

        @NonNull
        @Override
        public String getName() {
            return scope.getTaskName("collect", "MultiDexComponents");
        }

        @NonNull
        @Override
        public Class<CreateManifestKeepList> getType() {
            return CreateManifestKeepList.class;
        }

        @Override
        public void execute(@NonNull CreateManifestKeepList manifestKeepListTask) {
            manifestKeepListTask.setVariantName(scope.getVariantConfiguration().getFullName());

            // since all the output have the same manifest, besides the versionCode,
            // we can take any of the output and use that.
            final BaseVariantOutputData output = scope.getVariantData().getOutputs().get(0);
            ConventionMappingHelper.map(manifestKeepListTask, "manifest", new Callable<File>() {
                @Override
                public File call() throws Exception {
                    return output.getScope().getManifestOutputFile();
                }
            });

            manifestKeepListTask.outputFile = scope.getManifestKeepListProguardFile();
        }
    }

    /**
     * Callback to allow build authors to selectively remove things that would be generated
     * from the manifest.
     *
     * <p> Registered by calling {@link #setFilter(Filter)}.
     */
    public interface Filter {
        /**
         * Returns whether to keep the referenced code in the main dex.
         *
         * @param name the xml tag name without the android: namespace, e.g. 'activity'
         * @param attributes the xml attributes e.g. ['android:name':'com.example.ActivityClass']
         * @return true if and only if the keep rules for this manifest entry should be generated.
         */
        boolean keep(@NonNull String name, @NonNull Map<String, String> attributes);
    }
}
