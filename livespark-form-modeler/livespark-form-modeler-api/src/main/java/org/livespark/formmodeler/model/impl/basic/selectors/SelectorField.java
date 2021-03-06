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
package org.livespark.formmodeler.model.impl.basic.selectors;

import java.util.ArrayList;
import java.util.List;

import org.livespark.formmodeler.model.FieldDefinition;

/**
 * Created by pefernan on 10/2/15.
 */
public abstract class SelectorField extends FieldDefinition {

    protected List<SelectorOption> options = new ArrayList<SelectorOption>();

    public List<SelectorOption> getOptions() {
        return options;
    }

    public void setOptions(List<SelectorOption> options) {
        this.options = options;
    }

    @Override
    protected void doCopyFrom( FieldDefinition other ) {
        if ( other instanceof  SelectorField ) {
            this.setOptions( ((SelectorField) other).getOptions() );
        }
    }
}
