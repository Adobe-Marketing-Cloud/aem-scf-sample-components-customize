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

import com.adobe.cq.social.scf.SocialCollectionComponent;

/**
 * A TaskBox is a collection of projects. Collections generally extend SocialCollectionComponent because it defines
 * several utility methods that allows SCF to easily iterate and paginate over a collection.
 * SocialCollectionComponents are SocialComponents. SocialComponents are logical representations of resources that
 * represent the business view of a resource. They are also used to convert a resource into JSON.
 */
public interface TaskBoxSocialComponent extends SocialCollectionComponent {
    public static final String RESOURCE_TYPE = "social/samples/components/tasks/taskbox";
}
