package com.bijlipay.Controller;

import com.bijlipay.Utill.DateUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;

import static com.bijlipay.Config.Constant.*;
@Controller
public class MailController {

    @Autowired
    private JavaMailSender sender;

    @Value("${SENT.TO.Mail}")
    public String[] setTo;

    public void sendAxisCallback() {
        try {
            MimeMessage message = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(setTo);
            helper.setSubject("Instant Settlement Callback Exception Report");
            helper.setFrom("repo@bijlipay.co.in");
            helper.setText(String.format("Dear All," + "\n\n" + "Greetings from Bijlipay!!" + "\n\n" + "" +
                    " Instant Settlement Callback Exception Report - " + DateUtil.currentDate("yyyy-MM-dd") + "\n\n" + "Regards," + "\n" + "Team " +
                    "Bijlipay\n\n Note :" +
                    "This is an auto generated email. Please do not respond to this email id.\n For any queries You can call us @ 1800 4200 235 or" +
                    " write to us @ service@bijlipay.co.in."));
            String reportName = String.format("Instant_Settlement_Callback_Exception_Report_%s.xlsx", DateUtil.currentDateFromString("yyyy-MM-dd"));
            FileSystemResource report = new FileSystemResource(FILE_PATH+reportName);
            helper.addAttachment(report.getFilename(), report);
            sender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void sendNodalCallback() {
        try {
            MimeMessage message = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(setTo);
            helper.setSubject("Instant Settlement Nodal Exception Report");
            helper.setFrom("repo@bijlipay.co.in");
            helper.setText(String.format("Dear All," + "\n\n" + "Greetings from Bijlipay!!" + "\n\n" + "" +
                    " Instant Settlement Nodal Exception Report - " + DateUtil.currentDate("yyyy-MM-dd") + "\n\n" + "Regards," + "\n" + "Team " +
                    "Bijlipay\n\n Note :" +
                    "This is an auto generated email. Please do not respond to this email id.\n For any queries You can call us @ 1800 4200 235 or" +
                    " write to us @ service@bijlipay.co.in."));
            String reportName = String.format("Instant_Settlement_Nodal_Exception_Report_%s.xlsx", DateUtil.currentDateFromString("yyyy-MM-dd"));
            FileSystemResource report = new FileSystemResource(FILE_PATH+reportName);
            helper.addAttachment(report.getFilename(), report);
            sender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void sendAxisFile() {
        try {
            MimeMessage message = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(setTo);
            helper.setSubject("Instant Settlement Payout Report");
            helper.setFrom("repo@bijlipay.co.in");
            helper.setText(String.format("Dear All," + "\n\n" + "Greetings from Bijlipay!!" + "\n\n" + "" +
                    " Instant Settlement Payout Report - " + DateUtil.currentDate("yyyy-MM-dd") + "\n\n" + "Regards," + "\n" + "Team " +
                    "Bijlipay\n\n Note :" +
                    "This is an auto generated email. Please do not respond to this email id.\n For any queries You can call us @ 1800 4200 235 or" +
                    " write to us @ service@bijlipay.co.in."));
            String reportName = String.format("Instant_Settlement_Payout_Report_%s.xlsx", DateUtil.currentDateFromString("yyyy-MM-dd"));
            FileSystemResource report = new FileSystemResource(FILE_PATH+reportName);
            helper.addAttachment(report.getFilename(), report);
            sender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
