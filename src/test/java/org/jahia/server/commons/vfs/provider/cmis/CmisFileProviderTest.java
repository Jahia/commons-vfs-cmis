package org.jahia.server.commons.vfs.provider.cmis;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileType;
import org.apache.commons.vfs.FilesCache;
import org.apache.commons.vfs.cache.SoftRefFilesCache;
import org.apache.commons.vfs.impl.DefaultFileReplicator;
import org.apache.commons.vfs.impl.DefaultFileSystemManager;
import org.apache.commons.vfs.impl.PrivilegedFileReplicator;
import org.apache.commons.vfs.provider.local.DefaultLocalFileProvider;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * A test unit for the CMIS commons VFS provider implementation
 *
 * @todo a lot of code had to be duplicated from the Commons VFS test code because it was never released for version 1.0 :(
 */
public class CmisFileProviderTest {

    private static DefaultFileSystemManager manager;
    private static File tempDir;
    private static FilesCache cache;

    private static Thread[] startThreadSnapshot;
    private static Thread[] endThreadSnapshot;

    @Test
    public void testConnection() throws FileSystemException {
        FileObject rootFile = manager.resolveFile("cmis://repo.opencmis.org/inmemory/atom/");
        Assert.assertNotNull("Root file should not be null", rootFile);
    }

    @Test
    public void testRootChildren() throws FileSystemException {
        FileObject rootFile = manager.resolveFile("cmis://repo.opencmis.org/inmemory/atom/");
        for (FileObject rootChild : rootFile.getChildren()) {
            System.out.println("child=" + rootChild);
        }
    }

    @Test
    public void testRecursiveWalk() throws FileSystemException {
        FileObject rootFile = manager.resolveFile("cmis://repo.opencmis.org/inmemory/atom/");
        int depth = 4;
        walkChildren(rootFile, depth);
    }

    @Test
    public void testBasicProperties() throws FileSystemException {
        FileObject rootFile = manager.resolveFile("cmis://repo.opencmis.org/inmemory/atom/");
        for (FileObject rootChild : rootFile.getChildren()) {
            if (rootChild.getType() == FileType.FILE) {
                System.out.println("file=" + rootChild + " size=" + rootChild.getContent().getSize());
            } else if (rootChild.getType() == FileType.FOLDER) {
                System.out.println("folder=" + rootChild);
            } else {
                System.out.println("child=" + rootChild + " (" + rootChild.getType().toString() + ")");
            }
        }
    }

    @Test
    public void testFolderCreate() throws FileSystemException {
        FileObject rootFile = manager.resolveFile("cmis://repo.opencmis.org/inmemory/atom/");
        FileObject newFolder = manager.resolveFile("cmis://repo.opencmis.org/inmemory/atom/commons-vfs-testfolder1/subfolder1/subfolder2");
        newFolder.createFolder();
        Assert.assertTrue("New folder "+newFolder +" does not exist !", newFolder.exists());
    }


    private void walkChildren(FileObject fileObject, int depth) throws FileSystemException {
        if (depth == 0) {
            return;
        }
        System.out.println(fileObject.getURL().toString() + " (" + fileObject.getType() + ")");
        if (fileObject.getType() == FileType.FOLDER) {
            for (FileObject childFileObject : fileObject.getChildren()) {
                walkChildren(childFileObject, depth - 1);
            }
        }
    }

    @BeforeClass
    public static void setUp() throws Exception {
        startThreadSnapshot = createThreadSnapshot();

        // Locate the temp directory, and clean it up
        tempDir = new File("tempDir");
        tempDir.mkdirs();

        // Create the file system manager
        manager = new DefaultFileSystemManager();
        manager.setFilesCache(getFilesCache());

        final DefaultFileReplicator replicator = new DefaultFileReplicator(tempDir);
        manager.setReplicator(new PrivilegedFileReplicator(replicator));
        manager.setTemporaryFileStore(replicator);

        if (!manager.hasProvider("file")) {
            manager.addProvider("file", new DefaultLocalFileProvider());
        }

        manager.init();
        manager.addProvider("cmis", new CmisFileProvider());

    }

    @AfterClass
    public static void tearDown() throws Exception {
        System.out.println("Shutting down the test environment...");

        // force the SoftRefFilesChache to free all files
        System.out.println(".");
        System.gc();
        Thread.sleep(1000);
        System.out.println(".");
        System.gc();
        Thread.sleep(1000);
        System.out.println(".");
        System.gc();
        Thread.sleep(1000);
        System.out.println(".");
        System.gc();
        Thread.sleep(1000);

        manager.freeUnusedResources();
        endThreadSnapshot = createThreadSnapshot();

        Thread[] diffThreadSnapshot = diffThreadSnapshot(startThreadSnapshot, endThreadSnapshot);
        if (diffThreadSnapshot.length > 0) {
            String message = dumpThreadSnapshot(diffThreadSnapshot);
            /*
            if (providerConfig.checkCleanThreadState())
            {
                // close the manager to do a "not thread safe" release of all resources
                // and allow the vm to shutdown
                manager.close();
                fail(message);
            }
            else
            {
            */
            System.out.println(message);
            // }
        }
        // System.in.read();

        manager.close();

        // Make sure temp directory is empty or gone
        checkTempDir("Temp dir not empty after test");
    }

    /**
     * Asserts that the temp dir is empty or gone.
     */
    private static void checkTempDir(final String assertMsg) {
        if (tempDir.exists()) {
            Assert.assertTrue(assertMsg + " (" + tempDir.getAbsolutePath() + ")", tempDir.isDirectory() && tempDir.list().length == 0);
        }
    }

    private static String dumpThreadSnapshot(Thread[] threadSnapshot) {
        StringBuffer sb = new StringBuffer(256);
        sb.append("created threads still running:\n");

        Field threadTargetField = null;
        try {
            threadTargetField = Thread.class.getDeclaredField("target");
            threadTargetField.setAccessible(true);
        } catch (NoSuchFieldException e) {
        }

        for (int iter = 0; iter < threadSnapshot.length; iter++) {
            Thread thread = threadSnapshot[iter];
            if (thread == null) {
                continue;
            }

            sb.append("#");
            sb.append(iter + 1);
            sb.append(": ");
            sb.append(thread.getThreadGroup().getName());
            sb.append("\t");
            sb.append(thread.getName());
            sb.append("\t");
            if (thread.isDaemon()) {
                sb.append("daemon");
            } else {
                sb.append("not_a_daemon");
            }

            if (threadTargetField != null) {
                sb.append("\t");
                try {
                    Object threadTarget = threadTargetField.get(thread);
                    if (threadTarget != null) {
                        sb.append(threadTarget.getClass());
                    } else {
                        sb.append("null");
                    }
                } catch (IllegalAccessException e) {
                    sb.append("unknown class");
                }
            }

            sb.append("\n");
        }

        return sb.toString();
    }

    private static Thread[] diffThreadSnapshot(Thread[] startThreadSnapshot, Thread[] endThreadSnapshot) {
        List<Thread> diff = new ArrayList<Thread>(10);

        nextEnd:
        for (int iterEnd = 0; iterEnd < endThreadSnapshot.length; iterEnd++) {
            for (int iterStart = 0; iterStart < startThreadSnapshot.length; iterStart++) {
                if (startThreadSnapshot[iterStart] == endThreadSnapshot[iterEnd]) {
                    continue nextEnd;
                }
            }

            diff.add(endThreadSnapshot[iterEnd]);
        }

        Thread ret[] = new Thread[diff.size()];
        diff.toArray(ret);
        return ret;
    }

    private static Thread[] createThreadSnapshot() {
        ThreadGroup tg = Thread.currentThread().getThreadGroup();
        while (tg.getParent() != null) {
            tg = tg.getParent();
        }

        Thread snapshot[] = new Thread[200];
        tg.enumerate(snapshot, true);

        return snapshot;
    }

    public static FilesCache getFilesCache() {
        if (cache == null) {
            // cache = new DefaultFilesCache();
            cache = new SoftRefFilesCache();
        }

        return cache;
    }

}
