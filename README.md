aem-scf-sample-components-customize
===================================

Sample package for AEM Social Communities - Social Component Framework (SCF).  Examples of customizing Communities components to achieve a unique user experience. The samples are

scf-tasks-sample
================
This demonstrates building an SCF component from the ground up and implementing a custom search feature. It builds the component on top of SRP directly. While we strongly recommend that people build on top of com.adobe.cq.social.commons.comments.api classes (because of the extensive functionality they provide as a starting point), we recognize that sometimes the model is just too different. This sample requires at least AEM 6.1 with Social Communities 1.8.x (see https://docs.adobe.com/docs/en/aem/6-1/deploy/communities.html).

To use this sample, build and install the bundle and content package. You can then install the content package in the resources directory. This will give you a miniature site at /content/acme/en.html. There is a projects page in that site that demonstrates the sample functionality.

As always, the component is also availble via the REST API. For example, if the projects component node is at /content/acme/en/projects/jcr:content/content/taskbox, the following curl command will create a project that is associated with the taskbox:

curl http://localhost:4503/content/acme/en/projects/jcr:content/content/taskbox -uaparker@geometrixx.info:aparker -v -X POST -H "Accept:application/json" --data ":operation=social:samples:createProject&title=p3&description=p2"

The taskbox can then be queried with
curl http://localhost:4503/content/acme/en/projects/jcr:content/content/taskbox.social.json

This will return a list of projects associated with the taskbox. If, for example, it returns one at /content/usergenerated/asi/jcr/content/acme/en/projects/jcr:content/content/taskbox/pw29sa-p3, this individual item can be fetched with

curl http://localhost:4503/content/usergenerated/asi/jcr/content/acme/en/projects/jcr:content/content/taskbox/pw29sa-p3.social.json

It can be deleted by either a moderator or the original user with
curl http://localhost:4503/content/usergenerated/asi/jcr/content/acme/en/projects/jcr:content/content/taskbox/pw29sa-p3 -v -X POST  --data ":operation=social:samples:deleteProject" -uaparker@geometrixx.info:aparker

This sample is designed to work with whatever SRP is configured in the system. The above examples are using JSRP, but MSRP or ASRP would work identically.

Custom search sample
================

Custom search can be done with 

curl http://localhost:4503/content/acme/en/projects.html?filter=CATEGORY:STRING&date=START, END&sort=CATEGORY:ORDER&path=PATH

CATEGORY is any indexable property of the social component.  In this case, "title" and "description".

STRING is any string.

START/END is a date in yyyy-mm-dd format.

PATH is a path where the social content will be searched.  For example,  /content/usergenerated/asi/jcr/content/acme/en/projects.

Any combination of parameters can be used, and within the parameters, additional constraints can be added by using logical operations.  "." signifies the "and" operation and "|" signifies the "or" operation. 

Here is an example of using the logical operations on text filters

http://localhost:4503/content/acme/en/projects.html?filter=title:MyProject|title:YourProject

http://localhost:4503/content/acme/en/projects.html?filter=title:MyProject.description:InProgress

In the first example, the search will find projects with the title "MyProject" or the title "YourProject".  In the second example, the search will find only projects with both the title "MyProject" and the description "InProgress".

These logical operations can similarly be done with the other parameters "date" and "path".

