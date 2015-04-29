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

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public class FieldData {
    private final String obf;
    private final String srg;
    private final String mcp;

    public static Pattern MULTI_PATTERN = Pattern.compile("([^ ]+) => ([^ ]+) \\[ ([^ ]+) \\]");
    public static Pattern SINGLE_PATTERN = Pattern.compile("([^ ]+) => ([^ ]+) => ([^ ]+)");
    public static FieldData parse(String line) {
        if (line.contains("Name")) {
            Matcher m = SINGLE_PATTERN.matcher(line);
            if (!m.find()) return null; //Just in case
            String obf = m.group(1);
            String srg = m.group(2);
            String mcp = m.group(3);
            return new FieldData(obf, srg, mcp);
        } else {
            Matcher m = MULTI_PATTERN.matcher(line);
            if (!m.find()) return null;
            String obf = m.group(1);
            String mcp = m.group(2);
            String srg = m.group(3);
            return new FieldData(obf, srg, mcp);
        }
    }
}