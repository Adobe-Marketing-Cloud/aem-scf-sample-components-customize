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

import com.adobe.cq.social.scf.SocialComponent;
import com.adobe.cq.social.scf.User;

/**
 * A SocialComponent that provides a logical representation of a project. SocialComponents are logical representations
 * of resources that represent the business view of a resource. They are also used to convert a resource into JSON.
 */
public interface ProjectSocialComponent extends SocialComponent {

    public static final String PROJECT_RESOURCE_TYPE = "social/samples/components/tasks/project";

    /**
     * @return the title of the project
     */
    String getTitle();

    /**
     * @return the description of the project
     */
    String getDescription();
    
    /**
     * @return the description of the project
     */
    String getDate();

    /**
     * @return the owner of the project as a {@link User}
     */
    User getOwner();
}
