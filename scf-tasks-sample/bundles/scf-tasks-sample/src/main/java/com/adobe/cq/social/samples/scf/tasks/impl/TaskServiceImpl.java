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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.Session;

import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.NonExistingResource;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.jcr.api.SlingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.social.samples.scf.tasks.api.ProjectSocialComponent;
import com.adobe.cq.social.samples.scf.tasks.api.TaskBoxSocialComponent;
import com.adobe.cq.social.samples.scf.tasks.api.TasksService;
import com.adobe.cq.social.scf.OperationException;
import com.adobe.cq.social.srp.SocialResourceProvider;
import com.adobe.cq.social.ugcbase.CollabUser;
import com.adobe.cq.social.ugcbase.SocialUtils;

@Component
@Service
public class TaskServiceImpl implements TasksService {
    private static final String UGC_WRITER = "ugc-writer";
    
    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY, policy = ReferencePolicy.STATIC)
    private SlingRepository repository;

    @Reference
    private SocialUtils socialUtils;

    /**
     * Resource Resolver used for the request.
     */
    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY, policy = ReferencePolicy.STATIC)
    protected ResourceResolverFactory resourceResolverFactory;

    /** Logger for this class. */
    private static final Logger LOG = LoggerFactory.getLogger(TaskServiceImpl.class);

    @Override
    public Resource createProject(SlingHttpServletRequest request) throws OperationException {
        final Resource taskBox = request.getResource();
        final Session userSession = request.getResourceResolver().adaptTo(Session.class);
        final String owner = userSession.getUserID();
        final String title = request.getParameter(PROJ_TITLE_PARAM);
        final String desc = request.getParameter(PROJ_DESC_PARAM);
        return createProject(taskBox, owner, title, desc, request.getResourceResolver());
    }

    @Override
    public Resource createProject(Resource taskBox, String owner, String title, String desc, ResourceResolver resolver)
        throws OperationException {
        // perform basic validation and thrown an OperationException if request is invalid. Use response code of 400
        // to indicate "bad request"
        if (StringUtils.isEmpty(desc)) {
            throw new OperationException("Description must be present", 400);
        }
        if (StringUtils.isEmpty(title)) {
            throw new OperationException("Title must be present", 400);
        }
        if (!taskBox.isResourceType(TaskBoxSocialComponent.RESOURCE_TYPE)) {
            throw new OperationException("Projects can only be created under a taskbox", 400);
        }
        try {

            // if there is no shadow path already defined for this component, this has the side effect of setting
            // things up so ACLs inherit from what is already there.
            socialUtils.getUGCResource(taskBox);
            SocialResourceProvider srp = socialUtils.getSocialResourceProvider(taskBox);
            srp.setConfig(socialUtils.getDefaultStorageConfig());
            if (!socialUtils.mayPost(resolver, taskBox)) {
                throw new OperationException("Projects can not be created", 400);
            }

            Map<String, Object> props = new HashMap<String, Object>(5);
            props.put("jcr:title", title);
            props.put("jcr:description", desc);
            props.put(CollabUser.PROP_NAME, owner);
            props.put(SocialUtils.PN_PARENTID, taskBox.getPath());
            props.put("sling:resourceType", ProjectSocialComponent.PROJECT_RESOURCE_TYPE);
            props.put("jcr:primaryType", "nt:unstructured");
            props.put("wyatt_extension", "boo");
            // use the Resource API to create resource to make this data store agnostic
            final Resource newProject =
                srp.create(resolver, socialUtils.resourceToUGCStoragePath(taskBox) + "/"
                        + createUniqueNameHint(title), props);
            resolver.commit();
            return newProject;
        } catch (PersistenceException e) {
            LOG.error("Unable to create project", e);
            throw new OperationException("Unable to create project", 500);
        }
    }

    /**
     * @param message string that can be used as a hint when creating a unique name
     * @return a potentially unique string generated by appending a random salt to message
     */
    private String createUniqueNameHint(String message) {
        StringBuilder nodeName;
        nodeName = new StringBuilder(socialUtils.generateRandomString(6)).append("-");
        message = message.replaceAll("\\<.*?>", "");
        message = message.replaceAll("\\&.*?\\;", "");
        if (message.length() > 20) {
            nodeName.append(message.substring(0, 20));
        } else {
            nodeName.append(message);
        }
        return nodeName.toString();
    }

    @Override
    public void deleteProject(SlingHttpServletRequest request) throws OperationException {
        if (request.getResource() instanceof NonExistingResource) {
            throw new OperationException("Nonexisting resource", 400);
        }
        
        if (!mayDelete(request.getResource())) {
            throw new OperationException("Not allowed", 400);
        }

        ResourceResolver serviceUserResolver = null;
        try {
            serviceUserResolver =
                resourceResolverFactory.getServiceResourceResolver(Collections.singletonMap(
                    ResourceResolverFactory.SUBSERVICE, (Object) UGC_WRITER));
            deleteProject(request.getResource().getPath(), serviceUserResolver);
        } catch (LoginException e) {
            throw new OperationException("Not allowed", e, 400);
        } finally {
            if (serviceUserResolver != null && serviceUserResolver.isLive()) {
                serviceUserResolver.close();
            }
        }
    }

    @Override
    public void deleteProject(String project, ResourceResolver resolver) throws OperationException {
        Resource projectResource = resolver.getResource(project);
        if (projectResource == null) {
            return;
        }
        
        try {
            resolver.delete(projectResource);
            resolver.commit();
        } catch (PersistenceException e) {
            throw new OperationException("Unable to delete project " + project, e, 500);
        }
    }

    // only a owner or a moderator can delete a project
    private boolean mayDelete(Resource resource) {
        return (socialUtils.hasModeratePermissions(resource) || socialUtils.isResourceOwner(resource));
    }

}
