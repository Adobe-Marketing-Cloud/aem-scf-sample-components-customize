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

import java.util.List;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

import com.adobe.cq.social.samples.scf.tasks.api.ProjectSocialComponent;
import com.adobe.cq.social.scf.ClientUtilities;
import com.adobe.cq.social.scf.User;
import com.adobe.cq.social.scf.core.BaseSocialComponent;

public class ProjectSocialComponentImpl extends BaseSocialComponent implements ProjectSocialComponent {
    
    final ValueMap props;

    public ProjectSocialComponentImpl(Resource resource, ClientUtilities clientUtils) {
        super(resource, clientUtils);
        props = resource.adaptTo(ValueMap.class);
    }

    @Override
    public String getTitle() {
        return props.get("jcr:title","");
    }

    @Override
    public String getDescription() {
        return props.get("jcr:description","");
    }

    @Override
    public User getOwner() {
        final String ownerId = props.get("owner","");
        return this.clientUtils.getUser(ownerId, this.resource.getResourceResolver());
    }

    // override this to remove any properties that you don't want showing up in the properties map by default
    @Override
    protected List<String> getIgnoredProperties() {
        this.ignoredProperties.add("jcr:.*");
        this.ignoredProperties.add("owner");
        return this.ignoredProperties;
    }

}
