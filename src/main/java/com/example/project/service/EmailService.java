package com.example.project.service;

import com.example.project.model.User;
import com.example.project.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class EmailService {
    private JavaMailSender emailSender;
    private UserRepository userRepository;

    public EmailService(JavaMailSender emailSender, UserRepository userRepository) {
        this.emailSender = emailSender;
        this.userRepository = userRepository;
    }

    public void sendHtmlMessage(String to, String subject, String htmlBody) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);

        emailSender.send(message);
    }

    public String generateHtmlContentCreateUser(User user){
        User user1 = userRepository.findByUsername(user.getUsername());
        return"<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Account Created Successfully</title>\n" +
                "    <style>\n" +
                "        body {\n" +
                "            font-family: Arial, sans-serif;\n" +
                "            background-color: #f4f4f4;\n" +
                "            margin: 0;\n" +
                "            padding: 20px;\n" +
                "        }\n" +
                "        .email-container {\n" +
                "            background-color: #ffffff;\n" +
                "            padding: 20px;\n" +
                "            border-radius: 8px;\n" +
                "            max-width: 600px;\n" +
                "            margin: 0 auto;\n" +
                "            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);\n" +
                "        }\n" +
                "        .email-header {\n" +
                "            background-color: #4CAF50;\n" +
                "            padding: 20px;\n" +
                "            border-radius: 8px 8px 0 0;\n" +
                "            color: #ffffff;\n" +
                "            text-align: center;\n" +
                "        }\n" +
                "        .email-body {\n" +
                "            padding: 20px;\n" +
                "            color: #333333;\n" +
                "        }\n" +
                "        .email-footer {\n" +
                "            margin-top: 20px;\n" +
                "            font-size: 12px;\n" +
                "            color: #777777;\n" +
                "            text-align: center;\n" +
                "        }\n" +
                "        .user-details {\n" +
                "            background-color: #f9f9f9;\n" +
                "            padding: 10px;\n" +
                "            border-radius: 4px;\n" +
                "            margin-top: 10px;\n" +
                "        }\n" +
                "        .user-details p {\n" +
                "            margin: 5px 0;\n" +
                "            font-weight: bold;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"email-container\">\n" +
                "        <div class=\"email-header\">\n" +
                "            <h2>Account Created Successfully</h2>\n" +
                "        </div>\n" +
                "        <div class=\"email-body\">\n" +
                "            <p>Dear " + user1.getUsername() + ",</p>\n" +
                "            <p>We are pleased to inform you that your account has been created successfully. Below are your account details:</p>\n" +
                "\n" +
                "            <div class=\"user-details\">\n" +
                "                <p>User ID: " + user1.getUserID() + "</p>\n" +
                "                <p>Username: " + user1.getUsername() + "</p>\n" +
                "                <p>Role: " + user1.getRole().toString() + "</p>\n" +
                "                <p>Status: " + user1.getUserStatus().toString() + "</p>\n" +
                "            </div>\n" +
                "\n" +
                "            <p>Please keep this information safe and do not share your password with anyone.</p>\n" +
                "            <p>If you have any questions or need assistance, feel free to contact our support team.</p>\n" +
                "            <p>Thank you for joining us!</p>\n" +
                "\n" +
                "            <p>Best regards,<br/>The Team</p>\n" +
                "        </div>\n" +
                "        <div class=\"email-footer\">\n" +
                "            <p>&copy; 2024 Your Company. All rights reserved.</p>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>\n";
    }

    public String generateHtmlContentSuspendUser(User user){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDate = user.getSuspensionEndTime().format(formatter);
        User user1 = userRepository.findByUsername(user.getUsername());
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Account Suspension Notification</title>\n" +
                "    <style>\n" +
                "        body {\n" +
                "            font-family: Arial, sans-serif;\n" +
                "            background-color: #f4f4f4;\n" +
                "            color: #333;\n" +
                "            margin: 0;\n" +
                "            padding: 0;\n" +
                "        }\n" +
                "        .container {\n" +
                "            width: 100%;\n" +
                "            max-width: 600px;\n" +
                "            margin: 0 auto;\n" +
                "            background-color: #ffffff;\n" +
                "            border-radius: 8px;\n" +
                "            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);\n" +
                "            padding: 20px;\n" +
                "        }\n" +
                "        .header {\n" +
                "            background-color: #ff6b6b;\n" +
                "            padding: 10px 20px;\n" +
                "            text-align: center;\n" +
                "            color: #ffffff;\n" +
                "            border-radius: 8px 8px 0 0;\n" +
                "        }\n" +
                "        .content {\n" +
                "            padding: 20px;\n" +
                "        }\n" +
                "        .footer {\n" +
                "            text-align: center;\n" +
                "            margin-top: 20px;\n" +
                "            font-size: 12px;\n" +
                "            color: #777;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"container\">\n" +
                "        <div class=\"header\">\n" +
                "            <h1>Account Suspension Notice</h1>\n" +
                "        </div>\n" +
                "        <div class=\"content\">\n" +
                "            <p>Dear <strong>"+user1.getUsername()+"</strong>,</p>\n" +
                "            <p>We regret to inform you that your account (User ID: <strong>"+user1.getUserID()+"</strong>) has been temporarily suspended due to a violation of our terms of service.</p>\n" +
                "            <p>Here are the details of your account:</p>\n" +
                "            <ul>\n" +
                "                <li><strong>Username:</strong> "+user1.getUsername()+"</li>\n" +
                "                <li><strong>Role:</strong> "+user1.getRole()+"</li>\n" +
                "                <li><strong>Suspension End Time:</strong> "+formattedDate+"</li>\n" +
                "            </ul>\n" +
                "            <p>Your account will be automatically unlocked after the suspension period ends on <strong>"+user.getSuspensionEndTime()+"</strong>.</p>\n" +
                "            <p>If you have any questions or believe this suspension is a mistake, please contact our support team.</p>\n" +
                "            <p>Thank you for your attention to this matter.</p>\n" +
                "            <p>Best regards,</p>\n" +
                "            <p>The Support Team</p>\n" +
                "        </div>\n" +
                "        <div class=\"footer\">\n" +
                "            <p>&copy; 2024 Your Company Name. All rights reserved.</p>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>\n";
    }

    public String generateHtmlContentActivateUser(User user){
        User user1 = userRepository.findByUsername(user.getUsername());
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Account Reactivation Notification</title>\n" +
                "    <style>\n" +
                "        body {\n" +
                "            font-family: Arial, sans-serif;\n" +
                "            background-color: #f4f4f4;\n" +
                "            color: #333;\n" +
                "            margin: 0;\n" +
                "            padding: 0;\n" +
                "        }\n" +
                "        .container {\n" +
                "            width: 100%;\n" +
                "            max-width: 600px;\n" +
                "            margin: 0 auto;\n" +
                "            background-color: #ffffff;\n" +
                "            border-radius: 8px;\n" +
                "            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);\n" +
                "            padding: 20px;\n" +
                "        }\n" +
                "        .header {\n" +
                "            background-color: #4CAF50;\n" +
                "            padding: 10px 20px;\n" +
                "            text-align: center;\n" +
                "            color: #ffffff;\n" +
                "            border-radius: 8px 8px 0 0;\n" +
                "        }\n" +
                "        .content {\n" +
                "            padding: 20px;\n" +
                "        }\n" +
                "        .footer {\n" +
                "            text-align: center;\n" +
                "            margin-top: 20px;\n" +
                "            font-size: 12px;\n" +
                "            color: #777;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"container\">\n" +
                "        <div class=\"header\">\n" +
                "            <h1>Account Reactivation Notice</h1>\n" +
                "        </div>\n" +
                "        <div class=\"content\">\n" +
                "            <p>Dear <strong>"+user1.getUsername()+"</strong>,</p>\n" +
                "            <p>We are pleased to inform you that your account (User ID: <strong>"+user1.getUserID()+"</strong>) has been successfully reactivated.</p>\n" +
                "            <p>Here are the details of your account:</p>\n" +
                "            <ul>\n" +
                "                <li><strong>Username:</strong> "+user1.getUsername()+"</li>\n" +
                "                <li><strong>Role:</strong> "+user1.getRole()+"</li>\n" +
                "                <li><strong>Status:</strong> Active</li>\n" +
                "            </ul>\n" +
                "            <p>Your account is now fully operational, and you can access all the features and services as before.</p>\n" +
                "            <p>If you have any questions or need further assistance, please feel free to reach out to our support team.</p>\n" +
                "            <p>Thank you for your understanding and cooperation.</p>\n" +
                "            <p>Best regards,</p>\n" +
                "            <p>The Support Team</p>\n" +
                "        </div>\n" +
                "        <div class=\"footer\">\n" +
                "            <p>&copy; 2024 Your Company Name. All rights reserved.</p>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>\n";
    }
}
