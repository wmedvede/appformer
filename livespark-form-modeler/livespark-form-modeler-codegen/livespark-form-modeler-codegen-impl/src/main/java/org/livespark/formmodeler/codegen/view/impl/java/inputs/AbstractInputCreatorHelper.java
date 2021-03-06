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

package org.livespark.formmodeler.codegen.view.impl.java.inputs;

import org.livespark.formmodeler.codegen.SourceGenerationContext;
import org.livespark.formmodeler.codegen.view.impl.java.InputCreatorHelper;
import org.livespark.formmodeler.model.FieldDefinition;

/**
 * Created by pefernan on 4/28/15.
 */
public abstract class AbstractInputCreatorHelper<F extends FieldDefinition> implements InputCreatorHelper<F> {

    @Override
    public boolean isInputInjectable() {
        return true;
    }

    @Override
    public String getInputInitLiteral( SourceGenerationContext context, F fieldDefinition ) {
        return null;
    }

    @Override
    public boolean isDisplayInjectable() {
        return false;
    }

    @Override
    public String getDisplayInitLiteral() {
        return "com.google.gwt.user.client.DOM.createTD()";
    }

    @Override
    public String getDisplayWidget( F fieldDefinition ) {
        return "com.google.gwt.user.client.Element";
    }

    @Override
    public String getReadonlyMethod( String fieldName, String readonlyParam ) {
        return fieldName + ".setReadOnly( " + readonlyParam + ");";
    }

    protected String cleanClassName(String className) {
        if (className.indexOf( "." ) == -1) return className;
        return className.substring( className.lastIndexOf( "." ) + 1 );
    }
}
