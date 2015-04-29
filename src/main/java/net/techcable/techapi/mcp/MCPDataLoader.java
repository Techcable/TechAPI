/**
 * The MIT License
 * Copyright (c) 2015 Techcable
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.techcable.techapi.mcp;

import au.com.bytecode.opencsv.CSVReader;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import lombok.SneakyThrows;
import org.apache.commons.lang3.tuple.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MCPDataLoader {
    private final URL fields = makeUrl("http://export.mcpbot.bspk.rs/fields.csv");
    private final URL methods = makeUrl("http://export.mcpbot.bspk.rs/methods.csv");
    private final URL joined = makeUrl("https://raw.githubusercontent.com/MinecraftForge/FML/master/conf/joined.srg");

    @SneakyThrows
    private static URL makeUrl(String raw) {
        return new URL(raw);
    }

    public void reload() {
        BufferedReader reader = download(joined);
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(" ");
                switch (data[0]) {
                    case "MD:" :
                        String srg = data[3].substring(data[3].lastIndexOf('/') + 1);
                        if (srgMethodMap.containsValue(srg)) continue;
                        srgMethodMap.put(data[1], srg);
                        signatureMap.put(srg, data[4]);
                        break;
                    case "FD:" :
                        srg = data[2].substring(data[2].lastIndexOf('/') + 1);
                        if (srgFieldMap.containsValue(srg)) continue; //Meh
                        srgFieldMap.put(data[1], srg);
                        break;
                }
            }
            CSVReader methodReader = new CSVReader(download(methods));
            for (String[] data : methodReader.readAll()) {
                if (data[0].equals("serage")) continue;
                methodCache.put(data[0], new MethodData(srgMethodMap.inverse().get(data[0]), data[0], data[1]));
            }
            CSVReader fieldReader = new CSVReader(download(fields));
            for (String[] data : fieldReader.readAll()) {
                if (data[0].equals("serage")) continue;
                fieldCache.put(data[0], new FieldData(srgFieldMap.inverse().get(data[0]), data[0], data[1]));
            }
        } catch (IOException e) {
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private final BiMap<String, String> srgMethodMap = HashBiMap.create();
    private final Map<String, String> signatureMap = new HashMap<>();
    private final BiMap<String, String> srgFieldMap = HashBiMap.create();
    private final Map<String, MethodData> methodCache = new HashMap<>();
    private final Map<String, FieldData> fieldCache = new HashMap<>();

    @SneakyThrows
    public BufferedReader download(URL url) {
        return new BufferedReader(new InputStreamReader(url.openStream()));
    }

    public FieldData getField(String srg) {
        return fieldCache.get(srg);
    }

    public MethodData getMethod(String srg) {
        return methodCache.get(srg);
    }

    public String getSignature(MethodData data) {
        return signatureMap.get(data.getSrg());
    }
}
