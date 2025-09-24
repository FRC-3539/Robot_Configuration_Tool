package robot.configuration.sftp;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import com.jcraft.jsch.Channel;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Vector;
import java.io.FilenameFilter;
import robot.configuration.settings.SystemSettings;
import com.jcraft.jsch.JSchException;
import java.net.InetAddress;

public class Sftp {
    public static String getActiveRoboRioIP(SystemSettings settings) {
        for (String ip : settings.getRoboRioIPs()) {
            try {
                InetAddress address = InetAddress.getByName(ip);
                if (address.isReachable(100)) { // timeout in ms
                    return ip;
                }
            } catch (Exception e) {
                // ignore and try next
            }
        }
        return null;
    }

    public static void uploadAllFiles(String host, int port, String username, String password,
            String localFolderPath, String remoteFolderPath) throws Exception {
        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(username, host, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            Channel channel = session.openChannel("sftp");
            channel.connect();
            // System.out.println("Connected to " + host);
            ChannelSftp sftp = (ChannelSftp) channel;

            File localDir = new File(localFolderPath);
            File[] files = localDir.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.endsWith(".ini");
                }
            });
            // if (files != null) {
            // System.out.println("Files to upload:");
            // for (File file : files) {
            // System.out.println(file.getAbsolutePath());
            // }
            // }
            try {
                sftp.cd(remoteFolderPath);
            } catch (com.jcraft.jsch.SftpException e) {
                // System.out.println("Creating remote directory: " + remoteFolderPath);
                sftp.mkdir(remoteFolderPath);
                sftp.cd(remoteFolderPath);
            }
            if (files != null) {
                for (File file : files) {
                    String remoteFile = remoteFolderPath + "/" + file.getName();
                    // System.out.println("Uploading " + file.getAbsolutePath() + " to " +
                    // remoteFile);
                    sftp.put(new FileInputStream(file), remoteFile);
                }
            }

            sftp.exit();
            session.disconnect();

        } catch (JSchException e) {
            throw e;
        } catch (Exception e) {
            throw new JSchException("Upload failed: " + e.getMessage(), e);
        }
        verifyFiles(host, port, username, password, localFolderPath, remoteFolderPath);
    }

    public static void verifyFiles(String host, int port, String username, String password,
            String localFolderPath, String remoteFolderPath) throws Exception {
        File tempDir = new File(SystemSettings.getSettingsFolder(), "robot_config_verify");
        if (!tempDir.exists())
            tempDir.mkdirs();
        robot.configuration.sftp.Sftp.downloadAllFiles(host,
                22,
                username,
                password,
                remoteFolderPath,
                tempDir.getAbsolutePath(), false);
        // Compare each .ini file in project folder and tempDir
        File projectDir = new File(localFolderPath);
        File[] localFiles = projectDir.listFiles((dir, name) -> name.endsWith(".ini"));
        boolean allMatch = true;
        StringBuilder errorFiles = new StringBuilder();
        if (localFiles != null) {
            for (File localFile : localFiles) {
                File downloadedFile = new File(tempDir, localFile.getName());
                if (!downloadedFile.exists() || !filesAreEqual(localFile, downloadedFile)) {
                    allMatch = false;
                    errorFiles.append(localFile.getName()).append("\n");
                }
            }
        }
        // Clean up tempDir
        for (File f : tempDir.listFiles())
            f.delete();
        tempDir.delete();

        if (!allMatch) {
            throw new Exception("Verification failed for files:\n" + errorFiles.toString());
        }
    }

    public static void downloadAllFiles(String host, int port, String username, String password,
            String remoteFolderPath, String localFolderPath, boolean verify) throws Exception {
        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(username, host, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            Channel channel = session.openChannel("sftp");
            channel.connect();
            ChannelSftp sftp = (ChannelSftp) channel;

            Vector<ChannelSftp.LsEntry> files = sftp.ls(remoteFolderPath);
            File localDir = new File(localFolderPath);
            if (!localDir.exists()) {
                localDir.mkdirs();
            }
            for (ChannelSftp.LsEntry entry : files) {
                if (!entry.getAttrs().isDir() && entry.getFilename().endsWith(".ini")) {
                    String remoteFile = remoteFolderPath + "/" + entry.getFilename();
                    String localFile = localFolderPath + File.separator + entry.getFilename();
                    sftp.get(remoteFile, new FileOutputStream(new File(localFile)));
                }
            }

            sftp.exit();
            session.disconnect();
        } catch (JSchException e) {
            throw e;
        } catch (Exception e) {
            throw new JSchException("Download failed: " + e.getMessage(), e);
        }
        if (verify)
            verifyFiles(host, port, username, password, localFolderPath, remoteFolderPath);
    }

    public static void uploadAllFiles(SystemSettings settings) throws Exception {
        String activeIP = getActiveRoboRioIP(settings);
        if (activeIP != null) {
            uploadAllFiles(
                    activeIP,
                    22,
                    settings.getRemoteUsername(),
                    settings.getRemotePassword(),
                    settings.getProjectFolder(),
                    settings.getRemoteFolder());
        } else {
            throw new Exception("No reachable RoboRio found.");
        }
    }

    public static void downloadAllFiles(SystemSettings settings) throws Exception {
        String activeIP = getActiveRoboRioIP(settings);
        if (activeIP != null) {
            downloadAllFiles(activeIP,
                    22,
                    settings.getRemoteUsername(),
                    settings.getRemotePassword(),
                    settings.getRemoteFolder(),
                    settings.getProjectFolder(), true);
        } else {
            throw new Exception("No reachable RoboRio found.");
        }
    }

    // Utility method to compare two files byte-by-byte
    static private boolean filesAreEqual(File f1, File f2) {
        try (java.io.FileInputStream in1 = new java.io.FileInputStream(f1);
                java.io.FileInputStream in2 = new java.io.FileInputStream(f2)) {
            int b1, b2;
            do {
                b1 = in1.read();
                b2 = in2.read();
                if (b1 != b2)
                    return false;
            } while (b1 != -1 && b2 != -1);
            return b1 == b2;
        } catch (Exception e) {
            return false;
        }
    }
}
