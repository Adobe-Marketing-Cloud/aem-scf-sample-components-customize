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
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import javax.jcr.RepositoryException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import com.adobe.cq.social.samples.scf.tasks.api.ProjectSocialComponent;
import com.adobe.cq.social.samples.scf.tasks.api.TaskBoxSocialComponent;
import com.adobe.cq.social.scf.ClientUtilities;
import com.adobe.cq.social.scf.CollectionPagination;
import com.adobe.cq.social.scf.QueryRequestInfo;
import com.adobe.cq.social.scf.SocialComponentFactory;
import com.adobe.cq.social.scf.core.BaseQueryRequestInfo;
import com.adobe.cq.social.scf.core.BaseSocialComponent;
import com.adobe.cq.social.scf.core.CollectionSortedOrder;
import com.adobe.cq.social.srp.SocialResourceProvider;
import com.adobe.cq.social.ugc.api.ConstraintGroup;
import com.adobe.cq.social.ugc.api.FullTextConstraint;
import com.adobe.cq.social.ugc.api.Operator;
import com.adobe.cq.social.ugc.api.PathConstraint;
import com.adobe.cq.social.ugc.api.PathConstraintType;
import com.adobe.cq.social.ugc.api.RangeConstraint;
import com.adobe.cq.social.ugc.api.SearchResults;
import com.adobe.cq.social.ugc.api.UgcFilter;
import com.adobe.cq.social.ugc.api.UgcSearch;
import com.adobe.cq.social.ugc.api.UgcSort;
import com.adobe.cq.social.ugc.api.ValueConstraint;
import com.adobe.cq.social.ugc.api.UgcSort.Direction;

public class TaskBoxSocialComponentImpl extends BaseSocialComponent implements TaskBoxSocialComponent {

    private int totalSize = 0;
    private List<Object> projects;
    private boolean showOnlyCurrentUsersProjects = false;
    private ResourceResolver resolver;


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
    	final SlingHttpServletRequest request = this.clientUtils.getRequest();
    	BaseQueryRequestInfo queryInfo = new BaseQueryRequestInfo(request);
        SocialResourceProvider provider = this.clientUtils.getSocialUtils().getSocialResourceProvider(resource);
        // check if the request was a query
        if (queryInfo.isQuery()) {     
        	RequestParameter params = request.getRequestParameter("filter");
        	String paramString = params.getString();
        	
            // Step 1: set up the filter
            final UgcFilter filter = getFilter(request);

     
            // Step 2: get a UgcSearch impl.
            resolver = request.getResourceResolver();
            final UgcSearch search = resolver.adaptTo(UgcSearch.class);
 

            // Step 3: search
            // In 6.1, always send null for the first parameter. If possible, send false for the final parameter.
	        int count = 0;
	        projects = new ArrayList<Object>(10);
            try {
				final SearchResults<Resource> results = search.find(null, resolver, filter, 0, 10, false);
				// Do something with the results.
	            for (final Resource resource : results.getResults()) {
	            	Resource project = resource;
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
			} catch (RepositoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        this.totalSize = count;
        }
        
        else {
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
    
    /**
     * Build the filter from the parameter string.
     * @return the filter
     */
    static UgcFilter getFilter(SlingHttpServletRequest request) {

        final UgcFilter filter = new UgcFilter();
        
        // Parse the search text from the parameter string
        if (request.getRequestParameter("filter")!=null){
	        String[] split = new String[10];
	        RequestParameter textSpecParams = request.getRequestParameter("filter");
	        String textSpecs = textSpecParams.getString();
	        int count = 0;
	        for (final String filterString : textSpecs.split(",")){
	        	split[count] = filterString;
	        	count++;
	        }
	  
	        // Add the constraints to a ConstraintGroup
	        ConstraintGroup cg = new ConstraintGroup();
	        count = 0;
	        while (split[count] != null) {
	        	String searchText = split[count].split(":")[1];
	        	String option = "jcr:" + split[count].split(":")[0];
	        	if (searchText != null && option != null){
	        		if (searchText.charAt(0) == '.'){
	        			searchText = searchText.substring(1, searchText.length());
	        			cg.and(new FullTextConstraint(searchText, option));
	        		}
	        		else if (searchText.charAt(0) == '|'){
	        			searchText = searchText.substring(1, searchText.length());
	        			cg.or(new FullTextConstraint(searchText, option));
	        		}
	        	    count++;
	        	}
	        }
	        filter.and(cg);
        }

        // Also restrict the paths we search under.
        final ConstraintGroup pathFilters = new ConstraintGroup(Operator.And);
        pathFilters.addConstraint(new PathConstraint("/content/usergenerated/asi/jcr/content/acme/en/projects",
            PathConstraintType.IsDescendantNode, Operator.Or));
        filter.and(pathFilters);
        // Parse from request
        if(request.getRequestParameter("path")!=null){
	        RequestParameter pathSpec = request.getRequestParameter("path");
	        String pathSpecString = pathSpec.getString();
	        pathFilters.addConstraint(new PathConstraint(pathSpecString,
	                PathConstraintType.IsDescendantNode, Operator.Or));
        	
     
        }

        // Also sort.
        if(request.getRequestParameter("sort")!=null){
	        RequestParameter sortSpecParams = request.getRequestParameter("sort");
        	String[] sortSpecs = new String[10];
        	String sortSpecText = sortSpecParams.getString();
        	int count = 0;
        	for (final String param:sortSpecText.split(",")){
        		sortSpecs[count] = param;
        		count++;
        	}
        	count = 0;
        	while(sortSpecs[count]!=null && count < 10){
        		String propToSort = "jcr:"+sortSpecs[count].split(":")[0];
        		String direction = sortSpecs[count].split(":")[1];
        		if (direction.equals("asc")){
                    filter.addSort(new UgcSort(propToSort, Direction.Asc));
        		}
        		if (direction.equals("desc")){
                    filter.addSort(new UgcSort(propToSort, Direction.Desc));
        		}
        		count++;
        	}
        }
        return filter;
    }

}
