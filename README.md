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

