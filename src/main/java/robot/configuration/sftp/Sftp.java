package robot.configuration.sftp;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.Channel;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Vector;
import java.io.FilenameFilter;
import robot.configuration.settings.SystemSettings;
import com.jcraft.jsch.JSchException;

public class Sftp {
    public static void uploadAllFiles(String host, int port, String username, String password,
            String localFolderPath, String remoteFolderPath) throws JSchException {
        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(username, host, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            Channel channel = session.openChannel("sftp");
            channel.connect();
            ChannelSftp sftp = (ChannelSftp) channel;

            File localDir = new File(localFolderPath);
            File[] files = localDir.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.endsWith(".ini");
                }
            });
            if (files != null) {
                for (File file : files) {
                    String remoteFile = remoteFolderPath + "/" + file.getName();
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
    }

    public static void downloadAllFiles(String host, int port, String username, String password,
            String remoteFolderPath, String localFolderPath) throws JSchException {
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
    }

    public static void uploadAllFiles(SystemSettings settings) throws JSchException {
        uploadAllFiles(
                settings.getRemoteUsername(),
                22,
                settings.getRemoteUsername(),
                settings.getRemotePassword(),
                settings.getProjectFolder(),
                settings.getRemoteFolder());
    }

    public static void downloadAllFiles(SystemSettings settings) throws JSchException {
        downloadAllFiles(
                settings.getRemoteUsername(),
                22,
                settings.getRemoteUsername(),
                settings.getRemotePassword(),
                settings.getRemoteFolder(),
                settings.getProjectFolder());
    }
}
