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

package org.livespark.backend.server.service.build;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;

import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationOutputHandler;
import org.apache.maven.shared.invoker.InvocationResult;
import org.guvnor.common.services.project.builder.model.BuildMessage;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.shared.message.Level;
import org.jboss.errai.bus.client.api.base.MessageBuilder;
import org.jboss.errai.bus.server.api.ServerMessageBus;
import org.jboss.errai.common.client.protocols.MessageParts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseBuildCallable implements BuildCallable {

    private static final Logger logger = LoggerFactory.getLogger( BaseBuildCallable.class );
    private static final String LOG_BUILD_OUTPUT_PROPERTY = "livespark.log_build_output";
    private static final boolean logBuildOutput = Boolean.valueOf( System.getProperty( LOG_BUILD_OUTPUT_PROPERTY, "false" ) );

    protected final Project project;
    protected final File pomXml;
    protected final ServletRequest sreq;
    protected final ServerMessageBus bus;
    protected final String queueSessionId;
    protected final OutputHandler outputHandler;


    BaseBuildCallable( Project project,
                       File pomXml,
                       String queueSessionId,
                       ServletRequest sreq,
                       ServerMessageBus bus ) {
        this.project = project;
        this.pomXml = pomXml;
        this.queueSessionId = queueSessionId;
        this.sreq = sreq;
        this.bus = bus;

        OutputHandler outputHandler = new ClientOutputHandler( bus, queueSessionId );
        if ( logBuildOutput ) {
            outputHandler = new LoggingWrapper( outputHandler, logger );
        }

        this.outputHandler = outputHandler;
    }

    protected abstract List<BuildMessage> postBuildTasks( InvocationResult res ) throws Exception;

    @Override
    public List<BuildMessage> call() throws Exception {
        final List<BuildMessage> retVal = new ArrayList<BuildMessage>();

        try {
            cleanClientConsole();
            final InvocationResult res = executeRequest();
            retVal.addAll( postBuildTasks( res ) );
        } catch ( Throwable t ) {
            logBuildException( project, t );
        }

        return retVal;
    }

    private void logBuildException( final Project project,
                                    Throwable t ) {
        // TODO add error messages to build results
        logger.error( "Unable to build LiveSpark project, " + project.getProjectName(),
                      t );
    }

    protected List<BuildMessage> processBuildResults( InvocationResult res ) {
        final List<BuildMessage> messages = new ArrayList<BuildMessage>();

        if ( res.getExitCode() == 0 ) {
            messages.add( createSuccessMessage() );
        } else {
            messages.add( createFailureMessage() );
        }

        return messages;
    }

    private BuildMessage createFailureMessage() {
        final BuildMessage message = new BuildMessage();
        message.setLevel( Level.INFO );
        message.setText( "Build was successful" );

        return message;
    }

    private BuildMessage createSuccessMessage() {
        final BuildMessage message = new BuildMessage();
        message.setLevel( Level.INFO );
        message.setText( "Build successful" );

        return message;
    }

    protected InvocationResult executeRequest() throws Throwable {
        final DefaultInvocationRequest packageRequest = createPackageRequest( pomXml );

        packageRequest.setOutputHandler( new InvocationOutputHandler() {
            @Override
            public void consumeLine( String line ) {
                sendOutputToClient( line );
            }
        } );

        return new DefaultInvoker().execute( packageRequest );
    }

    protected DefaultInvocationRequest createPackageRequest( final File pomXml ) {
        final DefaultInvocationRequest packageRequest = new DefaultInvocationRequest();

        packageRequest.setPomFile( pomXml );
        packageRequest.setGoals( Collections.singletonList( "package" ) );

        return packageRequest;
    }

    private void cleanClientConsole() {
        MessageBuilder.createMessage()
                      .toSubject( "MavenBuilderOutput" )
                      .signalling()
                      .with( MessageParts.SessionID, queueSessionId )
                      .with( "clean", Boolean.TRUE )
                      .noErrorHandling().sendNowWith( bus );
    }

    protected String getWildflyHome() throws MalformedURLException, URISyntaxException {
        String wildflyHome = System.getProperty( "errai.jboss.home" );

        if ( wildflyHome == null ) {
            wildflyHome = findWildflyHome();
        }

        return wildflyHome;
    }

    private String findWildflyHome() throws MalformedURLException, URISyntaxException {
        final ServletContext context = sreq.getServletContext();
        final String webXmlPath = context.getRealPath( "/WEB-INF/web.xml" );
        File cur = new File( webXmlPath );

        do {
            cur = cur.getParentFile();
        } while ( cur != null && !cur.getName().contains( "wildfly" ) );

        if ( cur == null ) {
            throw new RuntimeException( "Could not locate Wildfly/JBoss root directory. Please set the errai.jboss.home system property." );
        }

        return cur.getAbsolutePath();
    }

    protected void sendOutputToClient( String output ) {
        outputHandler.handleOutput( output );
    }
}
