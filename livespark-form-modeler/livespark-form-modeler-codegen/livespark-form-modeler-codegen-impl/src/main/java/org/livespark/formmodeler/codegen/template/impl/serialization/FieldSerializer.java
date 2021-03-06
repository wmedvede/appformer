/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.livespark.formmodeler.codegen.template.impl.serialization;

import java.lang.reflect.Type;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.livespark.formmodeler.model.FieldDefinition;

import javax.enterprise.context.Dependent;

/**
 * Created by pefernan on 7/9/15.
 */
@Dependent
public class FieldSerializer implements JsonSerializer<FieldDefinition> {

    @Override
    public JsonElement serialize( FieldDefinition field, Type type, JsonSerializationContext context ) {
        JsonArray ja = new JsonArray();
        ja.add(context.serialize(field, field.getClass()));
        return ja;
    }
}
