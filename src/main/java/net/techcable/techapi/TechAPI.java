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
package net.techcable.techapi;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.io.*;
import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.SettableFuture;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.techcable.techapi.uuid.ProfileUtils;
import net.techcable.techapi.uuid.ProfileUtils.PlayerProfile;

import be.maximvdw.spigotsite.api.SpigotSite;
import be.maximvdw.spigotsite.api.SpigotSiteAPI;

import spark.Request;
import spark.Response;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static spark.Spark.*;

public class TechAPI {

    public static void main(String[] args) throws IOException {
        setPort(12345);
        CharSource in = Files.asCharSource(configFile, Charsets.UTF_8);
        config = new APIConfig((Map<String, String>) yaml.load(in.openBufferedStream()));
        get("/minecraft/uuid/:name", (Request request, Response response) -> {
            response.header(HttpHeaders.CONTENT_TYPE, MediaType.PLAIN_TEXT_UTF_8.toString());
            PlayerProfile profile = ProfileUtils.lookup(request.params(":name"));
            if (profile == null) {
                return "none" + "||" + request.params(":name");
            } else {
                return profile.getId() + "||" + profile.getName();
            }
        });
        get("/minecraft/name/:uuid", (Request request, Response response) -> {
            response.header(HttpHeaders.CONTENT_TYPE, MediaType.PLAIN_TEXT_UTF_8.toString());
            PlayerProfile profile = ProfileUtils.lookup(UUID.fromString(request.params(":name")));
            if (profile == null) {
                return "none" + "||" + request.params(":uuid");
            }
            return profile.getId() + "||" + profile.getName();
        });
        get("/minecraft/rawskin/:uuid", (Request request, Response response) -> {
            response.header(HttpHeaders.CONTENT_TYPE, MediaType.JSON_UTF_8.toString());
            PlayerProfile profile = ProfileUtils.lookupProperties(UUID.fromString(request.params(":uuid")));
            if (profile == null) {
                return "";
            }
            JsonArray properties = profile.getProperties();
            for (JsonElement property : properties) {
                JsonObject object = property.getAsJsonObject();
                if (object.get("name").equals("textures")) {
                    return object.toString();
                }
            }
            return "";
        });
    }
}
