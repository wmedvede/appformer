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

package org.livespark.formmodeler.rendering.client.view;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.livespark.formmodeler.rendering.client.shared.FormModel;
import org.livespark.formmodeler.rendering.client.view.validation.FormViewValidator;

/**
 * Created by pefernan on 4/17/15.
 */
public abstract class FormView<M extends FormModel> extends BaseView<M> {

    private boolean newModel = true;

    @Inject
    protected FormViewValidator validator;

    @Override
    public void setModel( M model ) {
        super.setModel( model );
        newModel = false;
        beforeDisplay();
        validator.clearFieldErrors();
    }

    public boolean isNewModel() {
        return newModel;
    }

    @PostConstruct
    private void init() {
        List entites = getEntities();
        if (entites == null || entites.isEmpty() || entites.size() < getEntitiesCount()) {
            initEntities();
        }
        initForm();
    }

    protected abstract void initForm();

    public abstract void setReadOnly( boolean readOnly );

    protected abstract int getEntitiesCount();

    protected abstract List getEntities();

    protected abstract void initEntities();

    public abstract boolean doExtraValidations();

    public boolean validate() {

        boolean isValid = validator.validate( binder.getModel() );

        boolean extraValidations = doExtraValidations();

        return isValid && extraValidations;
    }

    public abstract void beforeDisplay();
}
