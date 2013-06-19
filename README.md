commons-vfs-cmis
================

An Apache Commons VFS provider implementation for CMIS

Currently this implementation is really a prototype to test the idea, but it is already capable of :
- Connecting to the test repository at repo.opencmis.org using the Atom Pub binding
- Accessing the root folder
- Navigating the tree structure (assuming there is one)

There is a lot do to to make this implementation usable :
- Properly implement end point and repository support
- Support more bindings
- Support authentication
- Implement proper reading/writing support

Of course this implementation will never be capable of supporting searching, since Commons VFS doesn't offer an API
for that.

Contributors are more than welcome !

