commons-vfs-cmis
================

An Apache Commons VFS provider implementation for CMIS

Currently this implementation is really a prototype to test the idea, but it is already capable of :
- Connecting to the test repository at repo.opencmis.org using the Atom Pub binding
- Basic URL based authentication
- Accessing the root folder
- Navigating the tree structure (assuming there is one)
- Creating folder and files, and deleting them (no support for renaming, moving, etc..)

There is a lot do to to make this implementation usable :
- Properly implement end point and repository support
- Support more bindings
- Support authentication
- Implement proper reading/writing support for all file operations
- Implement file content attributes mapping to CMIS properties (but can we do this for folders ?)

Of course this implementation will never be capable of supporting searching, since Commons VFS doesn't offer an API
for that.

By default the test execute against the in-memory OpenCMIS Atom repository available at the URL :

cmis://repo.opencmis.org/inmemory/atom/

(please note that these are VFS URLs, not real URLs, that are simply real endpoint URL where the scheme has been
changed from "http" to "cmis".

But other repositories may be used by changing the URI in a Maven command line parameter, such as :

mvn clean install -Dtest.cmis.uri=cmis://admin:admin@cmis.alfresco.com/cmisatom

More Alfresco CMIS end points are available here : http://cmis.alfresco.com

Deploying to an existing application
------------------------------------

If you want to deploy this library to an existing application, you will need to deploy the following dependencies
along with the common-vfs-cmis-VERSION.jar :

activation-1.1.jar
chemistry-opencmis-client-api-0.9.0.jar
chemistry-opencmis-client-bindings-0.9.0.jar
chemistry-opencmis-client-impl-0.9.0.jar
chemistry-opencmis-commons-api-0.9.0.jar
chemistry-opencmis-commons-impl-0.9.0.jar
jaxb-api-2.1.jar
jaxb-impl-2.1.11.jar
jaxws-api-2.1.jar
jaxws-rt-2.1.7.jar
mimepull-1.3.jar
resolver-20050927.jar
saaj-api-1.3.jar
saaj-impl-1.3.3.jar
slf4j-api-1.7.5.jar
stax-api-1.0-2.jar
stax-ex-1.2.jar
stax2-api-3.1.1.jar
streambuffer-0.9.jar
woodstox-core-asl-4.2.0.jar

The easiest way to get all these dependencies is to download the package from the Apache Chemistry project website:

http://www.apache.org/dyn/closer.cgi/chemistry/opencmis/0.9.0/chemistry-opencmis-client-impl-0.9.0-with-dependencies.zip

If your application is already using some of these libraries you will have to handle the conflicts manually. For
example, for deployment in the Jahia CMS, you will only need to copy the following JARs into the WEB-INF/lib directory:

chemistry-opencmis-client-api-0.9.0.jar
chemistry-opencmis-client-bindings-0.9.0.jar
chemistry-opencmis-client-impl-0.9.0.jar
chemistry-opencmis-commons-api-0.9.0.jar
chemistry-opencmis-commons-impl-0.9.0.jar
jaxws-api-2.1.jar
jaxws-rt-2.1.7.jar
mimepull-1.3.jar
resolver-20050927.jar
saaj-api-1.3.jar
saaj-impl-1.3.3.jar
stax-ex-1.2.jar
stax2-api-3.1.1.jar
streambuffer-0.9.jar
woodstox-core-asl-4.2.0.jar

Ideas :
-------
- We might try to implement searching using a query string such as :
  cmis://admin:admin@cmis.alfresco.com/cmisatom?sql=SELECT * FROM cmis:document
  or something like that. Full text queries could use a "q" parameter such as :
  cmis://admin:admin@cmis.alfresco.com/cmisatom?q=john*

Performance:
------------
Walking the tree can be slow. Here is a result using a fresh new local install of Alfresco and a tree walk :
Walked 71 children in 13150ms.

So as much as possible tree walks should be avoided.

Contributors are more than welcome !

