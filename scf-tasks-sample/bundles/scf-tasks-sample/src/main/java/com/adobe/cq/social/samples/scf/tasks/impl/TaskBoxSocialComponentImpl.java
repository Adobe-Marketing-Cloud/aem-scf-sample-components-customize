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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.sling.api.resource.Resource;

import com.adobe.cq.social.samples.scf.tasks.api.ProjectSocialComponent;
import com.adobe.cq.social.samples.scf.tasks.api.TaskBoxSocialComponent;
import com.adobe.cq.social.scf.ClientUtilities;
import com.adobe.cq.social.scf.CollectionPagination;
import com.adobe.cq.social.scf.QueryRequestInfo;
import com.adobe.cq.social.scf.SocialComponentFactory;
import com.adobe.cq.social.scf.core.BaseSocialComponent;
import com.adobe.cq.social.scf.core.CollectionSortedOrder;
import com.adobe.cq.social.srp.SocialResourceProvider;

public class TaskBoxSocialComponentImpl extends BaseSocialComponent implements TaskBoxSocialComponent {

    private int totalSize = 0;
    private List<Object> projects;
    private boolean showOnlyCurrentUsersProjects = false;

    public TaskBoxSocialComponentImpl(Resource resource, ClientUtilities clientUtils) {
        super(resource, clientUtils);
        init();
    }

    public TaskBoxSocialComponentImpl(Resource resource, ClientUtilities clientUtils, QueryRequestInfo requestInfo) {
        super(resource, clientUtils);
        if (requestInfo.isQuery()) {
            final String[] filterBy = requestInfo.getPredicates().get("filter");
            if (filterBy.length > 0 && "onlyMy".equals(filterBy[0])) {
                this.showOnlyCurrentUsersProjects = true;
            }
        }
        init();
    }

    private void init() {
        SocialResourceProvider provider = this.clientUtils.getSocialUtils().getSocialResourceProvider(resource);
        Iterator<Resource> children = provider.listChildren(resource);
        
        int count = 0;
        projects = new ArrayList<Object>(10);
        // this is inefficient and a bad practice if iterating over a large number of resources
        // recommended method for iterating over UGC is using an index or search collection
        // this would suffice for our sample as indexing it out of scope for this sample
        while (children.hasNext()) {
            Resource project = children.next();
            if (!project.isResourceType(ProjectSocialComponent.PROJECT_RESOURCE_TYPE)) {
                continue;
            }
            ProjectSocialComponent projSC = getProjectForResource(project);
            if (!showOnlyCurrentUsersProjects
                    || (this.clientUtils.getAuthorizedUserId().equals(projSC.getOwner().getUserId()))) {
                projects.add(projSC);
                count++;
            }
        }
        this.totalSize = count;
    }

    // get the socialcomponent for a project by using the SocialComponentFactoryManager
    // its important to use the Manager to get a social component instead of initialize the impl as this enables
    // others to customize the social component
    private ProjectSocialComponent getProjectForResource(Resource project) {
        final SocialComponentFactory factory =
            this.clientUtils.getSocialComponentFactoryManager().getSocialComponentFactory(project);
        return (ProjectSocialComponent) factory.getSocialComponent(project, clientUtils, null);
    }

    @Override
    public int getTotalSize() {
        return totalSize;
    }

    @Override
    public void setPagination(CollectionPagination pagination) {
        // not required

    }

    @Override
    public void setSortedOrder(CollectionSortedOrder sortedOrder) {
        // not required

    }

    @Override
    public List<Object> getItems() {
        return projects;
    }

}
