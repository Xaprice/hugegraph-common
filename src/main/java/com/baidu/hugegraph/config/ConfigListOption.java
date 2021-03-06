/*
 * Copyright 2017 HugeGraph Authors
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.baidu.hugegraph.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.baidu.hugegraph.util.E;
import com.google.common.base.Predicate;

public class ConfigListOption<T> extends ConfigOption<List<T>> {

    private final Class<T> elemClass;

    @SuppressWarnings("unchecked")
    public ConfigListOption(String name, String desc,
                            Predicate<List<T>> func, T value) {
        this(name, false, desc, func, (Class<T>) value.getClass(), value);
    }

    @SuppressWarnings("unchecked")
    public ConfigListOption(String name, boolean required, String desc,
                            Predicate<List<T>> func, Class<T> clazz,
                            T... values) {
        this(name, required, desc, func, clazz, Arrays.asList(values));
    }

    @SuppressWarnings("unchecked")
    public ConfigListOption(String name, boolean required, String desc,
                            Predicate<List<T>> func, Class<T> clazz,
                            List<T> values) {
        super(name, required, desc, func,
              (Class<List<T>>) values.getClass(), values);
        E.checkArgumentNotNull(clazz, "Element class can't be null");
        this.elemClass = clazz;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<T> convert(Object value) {
        // If target data type is List, parse it as a list
        String str = (String) value;
        if (str.startsWith("[") && str.endsWith("]")) {
            str = str.substring(1, str.length() - 1);
        } else {
            throw new ConfigException(
                      "The list type config option expected " +
                      "to be wrapped in [], actual '%s'", str);
        }

        String[] parts = str.split(",");
        List<T> results = new ArrayList<>(parts.length);
        for (String part : parts) {
            results.add((T) super.convert(part.trim(), this.elemClass));
        }
        return results;
    }
}
