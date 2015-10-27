/*************************************************************************
 * Copyright 2015 Adobe Systems Incorporated
 * All Rights Reserved.
 *
 * NOTICE:  Adobe permits you to use, modify, and distribute this file in accordance with the
 * terms of the Adobe license agreement accompanying it.  If you have received this file from a
 * source other than Adobe, then your use, modification, or distribution of it requires the prior
 * written permission of Adobe.
 **************************************************************************/
package com.adobe.cq.social.samples.scf.tasks.api;

import javax.jcr.Session;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import com.adobe.cq.social.scf.OperationException;


/**
 * This service provides operations that can be used to create and manage projects and tasks.
 */
public interface TasksService {
    public static final String PROJ_DESC_PARAM = "description";
    public static final String PROJ_TITLE_PARAM = "title";

    /**
     * Create a new project from a Sling Servlet Request.
     * @param request the request that was made to the service that contains all the necessary parameters
     * @return the newly created project resource
     */
    Resource createProject(final SlingHttpServletRequest request) throws OperationException;

    /**
     * @param taskBox the taskbox resource in which this project will be created
     * @param owner the user who will be the owner of this project (required)
     * @param title the title of the project (required)
     * @param desc the description of the project (required)
     * @param resolver the resolver to use when creating the project
     * @return the newly created project resource
     */
    Resource createProject(final Resource taskBox, final String owner, final String title, final String desc,
        final ResourceResolver resolver) throws OperationException;
    
    /**
     * Delete a project from a Sling Servlet Request.
     * @param request the request that was made to the service that contains the resource to be deleted
     */
    void deleteProject(final SlingHttpServletRequest request) throws OperationException;

    /**
     * @param project the project path that is to be deleted
     * @param resolver the resolver to use when deleting the project
     */
    void deleteProject(final String project, final ResourceResolver resolver) throws OperationException;
}
