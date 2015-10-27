/*************************************************************************
 * Copyright 2015 Adobe Systems Incorporated
 * All Rights Reserved.
 *
 * NOTICE:  Adobe permits you to use, modify, and distribute this file in accordance with the
 * terms of the Adobe license agreement accompanying it.  If you have received this file from a
 * source other than Adobe, then your use, modification, or distribution of it requires the prior
 * written permission of Adobe.
 **************************************************************************/
package com.adobe.cq.social.samples.scf.tasks.impl;

import javax.servlet.http.HttpServletResponse;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.servlets.post.PostOperation;

import com.adobe.cq.social.samples.scf.tasks.api.TasksService;
import com.adobe.cq.social.scf.OperationException;
import com.adobe.cq.social.scf.SocialComponent;
import com.adobe.cq.social.scf.SocialComponentFactory;
import com.adobe.cq.social.scf.SocialComponentFactoryManager;
import com.adobe.cq.social.scf.SocialOperationResult;
import com.adobe.cq.social.scf.core.operations.AbstractSocialOperation;

/**
 * A POST endpoint that accepts requests for creating projects. This class responds to
 * all POST requests with a :operation=social:samples:createProject parameter. For example,
 * curl http://localhost:4503/content/community-components/en/test/_jcr_content/content/includable/taskbox 
 * -uaparker@geometrixx.info:aparker -v -X POST -H "Accept:application/json" 
 * --data ":operation=social:samples:createProject&title=a&description=b"
 */
@Component(immediate = true)
@Service
@Property(name = PostOperation.PROP_OPERATION_NAME, value = "social:samples:createProject")
// All SCF operation endpoints should extend the AbstractSocialOperation to leverage the functionality provided by SCF
public class CreateProjectOperation extends AbstractSocialOperation {

    // Use TaskService to actually do the work of creating a project
    @Reference
    private TasksService taskService;

    // The SocialComponentFactoryManager lets you access the factories that can give you a SocialComponent for a given
    // resource.
    @Reference
    private SocialComponentFactoryManager scfManager;

    @Override
    // All Social Operation endpoints return a SocialOperationResult.
    protected SocialOperationResult performOperation(SlingHttpServletRequest request) throws OperationException {
        final Resource newProject = this.taskService.createProject(request);
        return new SocialOperationResult(getSocialComponentForProject(newProject, request), "created project",
            HttpServletResponse.SC_OK, request.getResource().getPath());
    }

    private SocialComponent getSocialComponentForProject(Resource newProject, SlingHttpServletRequest request) {
        if (newProject == null) {
            return null;
        }
        final SocialComponentFactory factory = this.scfManager.getSocialComponentFactory(newProject);
        return factory.getSocialComponent(newProject, request);
    }
}
