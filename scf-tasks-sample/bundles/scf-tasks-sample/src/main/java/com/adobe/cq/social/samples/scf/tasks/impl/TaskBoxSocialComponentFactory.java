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

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;

import com.adobe.cq.social.samples.scf.tasks.api.TaskBoxSocialComponent;
import com.adobe.cq.social.scf.ClientUtilities;
import com.adobe.cq.social.scf.QueryRequestInfo;
import com.adobe.cq.social.scf.SocialCollectionComponentFactory;
import com.adobe.cq.social.scf.SocialComponent;
import com.adobe.cq.social.scf.core.AbstractSocialComponentFactory;

@Service
@Component
public class TaskBoxSocialComponentFactory extends AbstractSocialComponentFactory implements SocialCollectionComponentFactory{

    @Override
    public SocialComponent getSocialComponent(Resource resource) {
        return new TaskBoxSocialComponentImpl(resource, getClientUtilities(resource.getResourceResolver()));
    }

    @Override
    public SocialComponent getSocialComponent(Resource resource, SlingHttpServletRequest request) {
        return new TaskBoxSocialComponentImpl(resource, this.getClientUtilities(request), this.getQueryRequestInfo(request));
    }

    @Override
    public String getSupportedResourceType() {
        return TaskBoxSocialComponent.RESOURCE_TYPE;
    }

    @Override
    public SocialComponent getSocialComponent(Resource resource, ClientUtilities clientUtils,
        QueryRequestInfo requestInfo) {
        return new TaskBoxSocialComponentImpl(resource, clientUtils, requestInfo);
    }

}
