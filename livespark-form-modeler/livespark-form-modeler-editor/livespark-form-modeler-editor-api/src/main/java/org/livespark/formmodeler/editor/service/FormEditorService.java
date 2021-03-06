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
package org.livespark.formmodeler.editor.service;

import org.guvnor.common.services.shared.file.SupportsUpdate;
import org.jboss.errai.bus.server.annotations.Remote;
import org.livespark.formmodeler.model.FieldDefinition;
import org.livespark.formmodeler.model.FormDefinition;
import org.livespark.formmodeler.editor.model.FormModelerContent;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.service.support.SupportsDelete;
import org.uberfire.ext.editor.commons.service.support.SupportsRename;

import java.util.List;

/**
 * Created by pefernan on 7/7/15.
 */
@Remote
public interface FormEditorService extends SupportsUpdate<FormModelerContent>, SupportsRename, SupportsDelete {
    Path createForm(Path path, String formName);

    public FormModelerContent loadContent( Path path );

    public List<String> getAvailableDataObjects( Path path );

    public List<FieldDefinition> getAvailableFieldsForType( Path path, String holderName, String type );

    FieldDefinition resetField(FormDefinition definition, FieldDefinition field, Path path);
}
