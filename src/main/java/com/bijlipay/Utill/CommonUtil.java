package com.bijlipay.Utill;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
@Service
public class CommonUtil {

    private final Logger logger = LoggerFactory.getLogger(CommonUtil.class);

    public void uploadToSftp(InputStream inputStream, String remoteFileName, String sftpHost, int sftpPort, String sftpUser, String sftpPassword, String remoteDir) throws Exception {

        logger.info("connect to  sftp ---------:");
        JSch jsch = new JSch();
        Session session = jsch.getSession(sftpUser, sftpHost, sftpPort);
        session.setPassword(sftpPassword);

        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no"); // Disable host checking for testing
        session.setConfig(config);
        session.connect();

        Channel channel = session.openChannel("sftp");
        channel.connect();

        ChannelSftp sftpChannel = (ChannelSftp) channel;
        sftpChannel.cd(remoteDir);
        sftpChannel.put(inputStream, remoteFileName);

        inputStream.close();
        sftpChannel.exit();
        session.disconnect();
    }
}
