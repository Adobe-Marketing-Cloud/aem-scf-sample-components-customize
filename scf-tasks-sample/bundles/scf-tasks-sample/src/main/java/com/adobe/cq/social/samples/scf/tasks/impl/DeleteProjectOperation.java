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
import org.apache.sling.servlets.post.PostOperation;

import com.adobe.cq.social.samples.scf.tasks.api.TasksService;
import com.adobe.cq.social.scf.OperationException;
import com.adobe.cq.social.scf.SocialOperationResult;
import com.adobe.cq.social.scf.core.operations.AbstractSocialOperation;

/**
 * A POST endpoint that accepts requests for deleting projects. This class responds to
 * all POST requests with a :operation=social:samples:deleteProject parameter.
 */
@Component(immediate = true)
@Service
@Property(name = PostOperation.PROP_OPERATION_NAME, value = "social:samples:deleteProject")
// All SCF operation endpoints should extend the AbstractSocialOperation to leverage the functionality provided by SCF
public class DeleteProjectOperation extends AbstractSocialOperation {

    // use TaskService to actually delete a project
    @Reference
    private TasksService taskService;

    @Override
    // All Social Operation endpoints return a SocialOperationResult.
    protected SocialOperationResult performOperation(SlingHttpServletRequest request) throws OperationException {
       this.taskService.deleteProject(request);
        return new SocialOperationResult(null, "deleted", HttpServletResponse.SC_NO_CONTENT, request.getResource()
            .getPath());
    }
}
