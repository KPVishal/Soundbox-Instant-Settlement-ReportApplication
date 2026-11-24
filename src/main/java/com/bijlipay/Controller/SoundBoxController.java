package com.bijlipay.Controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.Schedules;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bijlipay.Config.Constant;
import com.bijlipay.Config.JWEMainClient;
import com.bijlipay.Config.JWEUtilsForPartners;
import com.bijlipay.Dto.DateDto;
import com.bijlipay.Model.SettlementReport;
import com.bijlipay.Repository.AxisFileReportRepository;
import com.bijlipay.Repository.FileEntityDataRepository;
import com.bijlipay.Repository.NodalAccountRepository;
import com.bijlipay.Repository.SecondBackUpReportRepository;
import com.bijlipay.Repository.SettlementReportRepository;
import com.bijlipay.Service.SoundBoxService;
import com.bijlipay.Utill.Aes256CryptUtil;
import com.bijlipay.Utill.ApiResponse;
import com.bijlipay.Utill.AttributeEncryptor;
import com.bijlipay.Utill.CommonUtil;
import com.bijlipay.Utill.DateUtil;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;


@CrossOrigin(origins = "*")
@RequestMapping("/api")
@RestController
public class SoundBoxController {
    // Report Generation Application controller — report endpoints only.
    private final Logger logger = LoggerFactory.getLogger(SoundBoxController.class);
    private final AtomicBoolean callbackRunning = new AtomicBoolean(false);
    private final AtomicBoolean axisTokenRunning = new AtomicBoolean(false);
    @Value ("${DBname}")
    public String dbName;

    @Value("${switch.datasource.url}")
    public String url;

    @Value("${switch.datasource.username}")
    public String userName;
    @Value("${switch.datasource.password}")
    public String password;


    @Value("${web.datasource.url}")
    public String webUrl;

    @Value("${web.datasource.username}")
    public String webUserName;
    @Value("${web.datasource.password}")
    public String webPassword;

//    @Value("${web.db.name}")
//    private String webDb;

    @Value ("${axis.url}")
    public String axisUrl;

    @Value("${axis.publickey}")
    public String axisP12;

    @Value("${axis.certificate.file.name}")
    public String axisCertificate;

    @Value("${axis.certificate.private.key}")
    public String axisPrivateCertificate;

    @Value("${axis.publickey.password}")
    public String axisPassword;

    @Value("${qui.host}")
    private String host;
    @Value("${equ.port}")
    private int port;
    @Value("${equ.password}")
    private String quiPassword;
    @Value("${equ.username}")
    private String quiUsername;
    @Value("${equ.path}")
    private String quiPath;

    @Value("${save.key}")
    private String saveKey;

    @Value("${axis.client.id}")
    public String axisClientId;

    @Value("${axis.client.secret}")
    public String axisClientSecret;

    @Value("${axis.service.version}")
    public String axisServiceVersion;

    @Value("${axis.service.id}")
    public String axisServiceId;

    @Value("${axis.uuid}")
    public String axisUuid;

    @Value("${axis.chennel.id}")
    public String axisChennelId;

    @Value("${axis.account.no}")
    public String axisAccuNo;

    @Value("${axis.crop.code}")
    public String axisCropCode;

    @Value("${axis.user.principle}")
    public String axisUserPrinciple;



    @Autowired
    private SoundBoxService soundBoxService;

    @Autowired
    private SettlementReportRepository settlementReportRepository;

    @Autowired
    private NodalAccountRepository nodalAccountRepository;

    @Autowired
    private FileEntityDataRepository fileRepository;

    @Autowired
    private MailController mailController;


    @Autowired
    private AttributeEncryptor attributeEncryptor;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private Aes256CryptUtil aes256CryptUtil;

    @Autowired
    private SecondBackUpReportRepository secondBackUpReportRepository;

    @Autowired
    private AxisFileReportRepository axisFileReportRepository;

    @Autowired
    private com.bijlipay.Repository.UploadedVpaRepository uploadedVpaRepository;

    @Autowired
    @org.springframework.beans.factory.annotation.Qualifier("switchDataSource")
    @Lazy
    private javax.sql.DataSource switchDataSource;

//    @Value("${transaction.callBack}")
//    private String txnCall;

    private Connection getConnection() throws SQLException {
        return switchDataSource.getConnection();
    }


    private Connection getConnectionWeb() throws SQLException {
        return DriverManager.getConnection(webUrl, webUserName, webPassword);
    }

    @GetMapping("/report")
    public ResponseEntity<?> report() throws IOException {

        String fromDate=DateUtil.currentDate("yyyy-MM-dd")+" "+"00:00:00";
        String toDate=DateUtil.currentDate("yyyy-MM-dd")+" "+"23:59:59";

        List<SettlementReport> settlementReports=settlementReportRepository.fetchNodalReport(fromDate,toDate);
        FileOutputStream outputStream=null;
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        XSSFCellStyle style = workbook.createCellStyle();
        style.setFont(font);
        //creating the worksheet
        XSSFSheet spreadsheet = workbook.createSheet("Settlement");
        XSSFRow row = spreadsheet.createRow(0);
        XSSFCell cell;

        cell = row.createCell(0);
        cell.setCellValue("Transaction ID");
        cell.setCellStyle(style);

        cell = row.createCell(1);
        cell.setCellValue("Merchant Name");
        cell.setCellStyle(style);

        cell = row.createCell(2);
        cell.setCellValue("Amount");
        cell.setCellStyle(style);

        cell = row.createCell(3);
        cell.setCellValue("Nodal Account");
        cell.setCellStyle(style);

        cell = row.createCell(4);
        cell.setCellValue("Axis CallBack");
        cell.setCellStyle(style);

        cell = row.createCell(5);
        cell.setCellValue("Axis File");
        cell.setCellStyle(style);

        cell = row.createCell(6);
        cell.setCellValue("Generate date");
        cell.setCellStyle(style);

        cell = row.createCell(7);
        cell.setCellValue("RRN");
        cell.setCellStyle(style);

        int i = 1;
        for (SettlementReport settlelist:settlementReports) {
            row = spreadsheet.createRow(i);
            cell = row.createCell(0);
            cell.setCellValue(settlelist.getTxnId());

            cell = row.createCell(1);
            cell.setCellValue(settlelist.getMerchantName());

            cell = row.createCell(2);
            cell.setCellValue(settlelist.getAmount());

            cell = row.createCell(3);
            cell.setCellValue(settlelist.getNodelAccount());

            cell = row.createCell(4);
            cell.setCellValue(settlelist.getAxisCallBack());

            cell = row.createCell(5);
            cell.setCellValue(settlelist.getAxisFile());

            cell = row.createCell(6);
            cell.setCellValue(settlelist.getGeneratedDate());

            cell = row.createCell(7);
            cell.setCellValue(settlelist.getRrn());

            i++;
        }

        soundBoxService.autoSizeColumns(spreadsheet);
        outputStream = new FileOutputStream(Constant.FILE_PATH + "SettlementAxis.xlsx");
        System.out.println("SettlementAxis Excel created successfully");
        workbook.write(outputStream);
        outputStream.close();
        workbook.close();
        return null;
    }


    private void generateReport(String fileName, List<SettlementReport> settlementReports) throws IOException {
        logger.info("settlementReport--{}",settlementReports.size());
//        XSSFWorkbook workbook = new XSSFWorkbook();
//        XSSFFont font = workbook.createFont();
//        font.setBold(true);
//
//        XSSFCellStyle style = workbook.createCellStyle();
//        style.setFont(font);
//
//        XSSFSheet spreadsheet = workbook.createSheet("Settlement");
//        XSSFRow row = spreadsheet.createRow(0);
//        XSSFCell cell;

        SXSSFWorkbook workbook = new SXSSFWorkbook(100);   // was XSSFWorkbook
        Font font = workbook.createFont();
        font.setBold(true);
        CellStyle style = workbook.createCellStyle();
        style.setFont(font);
        Sheet spreadsheet = workbook.createSheet("Settlement");
        spreadsheet.createFreezePane(0, 1);
        Row row = spreadsheet.createRow(0);
        Cell cell;
        logger.info("excel write");

        cell = row.createCell(0); cell.setCellValue("RRN"); cell.setCellStyle(style);
        cell = row.createCell(1); cell.setCellValue("TXNID"); cell.setCellStyle(style);
        cell = row.createCell(2); cell.setCellValue("ORDERID"); cell.setCellStyle(style);
        cell = row.createCell(3); cell.setCellValue("AMOUNT"); cell.setCellStyle(style);
        cell = row.createCell(4); cell.setCellValue("MOBILE NUMBER"); cell.setCellStyle(style);
        cell = row.createCell(5); cell.setCellValue("BANKNAME"); cell.setCellStyle(style);
        cell = row.createCell(6); cell.setCellValue("MASKEDACCOUNTNUMBER"); cell.setCellStyle(style);
        cell = row.createCell(7); cell.setCellValue("IFSC"); cell.setCellStyle(style);
        cell = row.createCell(8); cell.setCellValue("VPA"); cell.setCellStyle(style);
        cell = row.createCell(9); cell.setCellValue("ACCOUNT_CUST_NAME"); cell.setCellStyle(style);
        cell = row.createCell(10); cell.setCellValue("RESPCODE"); cell.setCellStyle(style);
        cell = row.createCell(11); cell.setCellValue("RESPONSE"); cell.setCellStyle(style);
        cell = row.createCell(12); cell.setCellValue("TXN_DATE"); cell.setCellStyle(style);
        cell = row.createCell(13); cell.setCellValue("CREDITVPA"); cell.setCellStyle(style);
        cell = row.createCell(14); cell.setCellValue("REMARKS"); cell.setCellStyle(style);
        cell = row.createCell(15); cell.setCellValue("SURCHARGE"); cell.setCellStyle(style);
        cell = row.createCell(16); cell.setCellValue("TAX"); cell.setCellStyle(style);
        cell = row.createCell(17); cell.setCellValue("DEBIT_AMOUNT"); cell.setCellStyle(style);
        cell = row.createCell(18); cell.setCellValue("MDR_TAX"); cell.setCellStyle(style);
        cell = row.createCell(19); cell.setCellValue("MERCHANT_ID"); cell.setCellStyle(style);
        cell = row.createCell(20); cell.setCellValue("UNQ_CUSTID"); cell.setCellStyle(style);
        cell = row.createCell(21); cell.setCellValue("BATCH_NO"); cell.setCellStyle(style);
        cell = row.createCell(22); cell.setCellValue("SETTLEMENT_STATUS"); cell.setCellStyle(style);
        cell = row.createCell(23); cell.setCellValue("INIT_MODE"); cell.setCellStyle(style);
        cell = row.createCell(24); cell.setCellValue("PURPOSE_CODE"); cell.setCellStyle(style);
        cell = row.createCell(25); cell.setCellValue("PAYER_ACCOUNT_TYPE"); cell.setCellStyle(style);
        cell = row.createCell(26); cell.setCellValue("BIN"); cell.setCellStyle(style);
        cell = row.createCell(27); cell.setCellValue("Nodal Account"); cell.setCellStyle(style);
        cell = row.createCell(28); cell.setCellValue("Axis CallBack"); cell.setCellStyle(style);
        cell = row.createCell(29); cell.setCellValue("Axis File"); cell.setCellStyle(style);



        int i = 1;
        for (SettlementReport settlelist : settlementReports) {
            row = spreadsheet.createRow(i);
            row.createCell(0).setCellValue(settlelist.getRrn() == null ? "NA" : settlelist.getRrn());
            logger.info("rrn--{}",settlelist.getRrn()==null?"NA":settlelist.getRrn());
            row.createCell(1).setCellValue(settlelist.getOrderId() == null ? "NA" : settlelist.getOrderId());
//            row.createCell(2).setCellValue(settlelist.getOrderId() == null ? "NA" : settlelist.getOrderId());//old one
            row.createCell(2).setCellValue("");//new one
            row.createCell(3).setCellValue(settlelist.getAmount() == null ? "NA" : settlelist.getAmount());
            row.createCell(4).setCellValue("");
            row.createCell(5).setCellValue("");
            row.createCell(6).setCellValue("");
            row.createCell(7).setCellValue("");
            row.createCell(8).setCellValue(settlelist.getVpa() == null ? "NA" : settlelist.getVpa());
            row.createCell(9).setCellValue("");
            row.createCell(10).setCellValue(settlelist.getRespCode() == null ? "NA" : settlelist.getRespCode());
            row.createCell(11).setCellValue(settlelist.getResponse()== null ? "NA" : settlelist.getResponse());
            row.createCell(12).setCellValue(settlelist.getGeneratedDate());
            row.createCell(13).setCellValue(settlelist.getCreditVpa() == null ? "NA" : settlelist.getCreditVpa());
            row.createCell(14).setCellValue("");
            row.createCell(15).setCellValue("");
            row.createCell(16).setCellValue("");
            row.createCell(17).setCellValue("");
            row.createCell(18).setCellValue("");
            row.createCell(19).setCellValue(settlelist.getMerchantId() == null ? "NA" : settlelist.getMerchantId());
            row.createCell(20).setCellValue("");
            row.createCell(21).setCellValue("");
            row.createCell(22).setCellValue(settlelist.getSettlementStatus() == null ? "NA" : settlelist.getSettlementStatus());
            row.createCell(23).setCellValue("");
            row.createCell(24).setCellValue("");
            row.createCell(25).setCellValue(settlelist.getPayerAccountType() == null ? "NA" : settlelist.getPayerAccountType());
            row.createCell(26).setCellValue("");
            row.createCell(27).setCellValue(settlelist.getNodelAccount());
            row.createCell(28).setCellValue(settlelist.getAxisCallBack());
            row.createCell(29).setCellValue(settlelist.getAxisFile());
            // [SQR_DQR_FLAG] CHANGE - sqr_dqr_flag = 1 (Dynamic QR) overrides only these 6 columns;
            // sqr_dqr_flag = 0 (Static QR) keeps the report exactly as above.
            if (Integer.valueOf(1).equals(settlelist.getSqrDqrFlag())) {
                row.createCell(1).setCellValue(settlelist.getTxnId() == null ? "NA" : settlelist.getTxnId());       // TXNID
                row.createCell(2).setCellValue(settlelist.getOrderId() == null ? "NA" : settlelist.getOrderId());   // ORDERID
                row.createCell(11).setCellValue(settlelist.getResponse()== null ? "NA" : settlelist.getResponse());                                                         // RESPONSE
                row.createCell(12).setCellValue(DateUtil.toTxnDateNoMillis(settlelist.getGeneratedDate()));         // TXN_DATE yyyy-MM-dd HH:mm:ss
                row.createCell(21).setCellValue(settlelist.getBatchNo() == null ? "NA" : settlelist.getBatchNo());  // BATCH_NO
                row.createCell(23).setCellValue(settlelist.getInitMode() == null ? "NA" : settlelist.getInitMode()); // INIT_MODE
                row.createCell(24).setCellValue(settlelist.getPurposeCode() == null ? "NA" : settlelist.getPurposeCode()); // PURPOSE_CODE
                logger.info("[SQR_DQR_FLAG] DQR row | rrn={} | TXNID={} | ORDERID={} | RESPONSE=SUCCESS | TXN_DATE={} | BATCH_NO={} | INIT_MODE={} | PURPOSE_CODE={}",
                        settlelist.getRrn(), settlelist.getTxnId(), settlelist.getOrderId(),
                        DateUtil.toTxnDateNoMillis(settlelist.getGeneratedDate()), settlelist.getBatchNo(), settlelist.getInitMode(), settlelist.getPurposeCode());
            }
            i++;
        }

//        soundBoxService.autoSizeColumns(spreadsheet);
        for (int c = 0; c < 30; c++) spreadsheet.setColumnWidth(c, 5000);

        // ✅ FIXED: Try-With-Resources to prevent file locking/resource leaks
        try (FileOutputStream outputStream = new FileOutputStream(Constant.FILE_PATH + fileName)) {
            workbook.write(outputStream);
            logger.info("{} created successfully", fileName);
        } catch (IOException e) {
            logger.error("Error writing Excel file {}: {}", fileName, e.getMessage(), e);
            throw e;
        } finally {
            // ✅ FIXED: Free up memory from JVM
            workbook.dispose();
            workbook.close();
        }
    }

    //Instant Settlement Callback Exception Report
    @Scheduled(cron = "${file.one}")
    @GetMapping("/report/file-one")
    public ResponseEntity<?> generateAxis() throws Exception {
        final String date = DateUtil.currentDate("yyyy-MM-dd");
        String fromDate=DateUtil.currentDate("yyyy-MM-dd")+" "+"00:00:00";
        String toDate=DateUtil.currentDate("yyyy-MM-dd")+" "+"23:59:59";

        // Fetch VPAs from uploaded file for filtering
        List<String> vpas = uploadedVpaRepository.findAllVpas();
        List<SettlementReport> reports;
        if (vpas != null && !vpas.isEmpty()) {
            reports = settlementReportRepository.fetchAxisCallbackReportByVpa(fromDate, toDate);
        } else {
            reports = settlementReportRepository.fetchAxisCallbackReport(fromDate, toDate);
        }

        generateReport("Instant_Settlement_Callback_Exception_Report_" + date +".xlsx", reports);

        // Flag transactions included in this slot report
        if (!reports.isEmpty()) {
            List<Long> ids = reports.stream().map(SettlementReport::getId).collect(java.util.stream.Collectors.toList());
            settlementReportRepository.updateReportFlag(ids);
        }

        mailController.sendAxisCallback();
        String path = Constant.FILE_PATH+"Instant_Settlement_Callback_Exception_Report_" + date +".xlsx";
        String filename ="Instant_Settlement_Callback_Exception_Report_" + date +".xlsx";

        try {
            File file = new File(path);
            FileInputStream uploadStream = new FileInputStream(file);
            // Upload using SFTP
            commonUtil.uploadToSftp(uploadStream, filename, host,
                    port,
                    quiUsername,
                    quiPassword,
                    quiPath);

        }catch (Exception e){
            logger.info("Exception error--{}",e.getMessage());
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ApiResponse(HttpStatus.OK,"File 1 Reports Generated Successfully"),HttpStatus.OK);
    }

    //Instant Settlement Payout Report
    @Schedules({
            @Scheduled(cron = "${file.second.corn1}"),
            @Scheduled(cron = "${file.second.corn2}")
    })
    @GetMapping("/report/file-second")
    public ResponseEntity<?> generateAxisFile() throws IOException {
        final String date = DateUtil.currentDate("yyyy-MM-dd");

        LocalDate today = LocalDate.now();
        logger.info("Today date --->{}",today);
        LocalDate yesterday = today.minusDays(1);
        String fromDate;
        String toDate;
        LocalTime currentTime = LocalTime.now();
        // 08:30 AM scheduler
        if (currentTime.getHour() == 8) {
            fromDate = yesterday + " 19:30:00.000"; //7.31.00 Pm
            toDate = today + " 07:59:59.999"; //8.0.00 Am
        }
        // 03:30 PM scheduler
        else if (currentTime.getHour() == 15) {
            fromDate = today + " 08:00:00.000";
            toDate = today + " 14:59:59.999"; //3.0.00 Pm
        }
        // 07:30 PM scheduler
        else {
            fromDate = today + " 15:00:00.000";
            toDate = today + " 19:29:59.999";//7.30.59 Pm
        }
        logger.info("From Date : {}", fromDate);
        logger.info("To Date   : {}", toDate);

        // Fetch VPAs from uploaded file for filtering
        List<String> vpas = uploadedVpaRepository.findAllVpas();
        List<SettlementReport> reports;
        if (vpas != null && !vpas.isEmpty()) {
            reports = settlementReportRepository.fetchAxisFileReportByVpa(fromDate, toDate);
        } else {
            reports = settlementReportRepository.fetchAxisFileReport(fromDate, toDate);
        }

        generateReport("Instant_Settlement_Payout_Report_"+date+".xlsx", reports);

        // Flag transactions included in this slot report
        if (!reports.isEmpty()) {
            List<Long> ids = reports.stream().map(SettlementReport::getId).collect(java.util.stream.Collectors.toList());
            settlementReportRepository.updateReportFlag(ids);
        }

        mailController.sendAxisFile();
        String path = Constant.FILE_PATH+"Instant_Settlement_Payout_Report_" + date +".xlsx";
        String filename ="Instant_Settlement_Payout_Report_" + date +".xlsx";
        try {
            File file = new File(path);
            FileInputStream uploadStream = new FileInputStream(file);
            // Upload using SFTP
            commonUtil.uploadToSftp(uploadStream, filename, host,
                    port,
                    quiUsername,
                    quiPassword,
                    quiPath);

        }catch (Exception e){
            logger.info("Exception error--{}",e.getMessage());
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ApiResponse(HttpStatus.OK,"File 2 Reports Generated Successfully"),HttpStatus.OK);
    }

    //Instant Settlement Nodal Exception Report
    @Scheduled(cron = "${file.third}")
    @GetMapping("/report/file-three")
    public ResponseEntity<?> generateAxisThree() throws IOException {
        final String date = DateUtil.currentDate("yyyy-MM-dd");

        String fromDate=DateUtil.yesterday()+" "+"00:00:00";
        String toDate=DateUtil.yesterday()+" "+"23:59:59";

        // Fetch VPAs from uploaded file for filtering
        List<String> vpas = uploadedVpaRepository.findAllVpas();
        List<SettlementReport> reports;
        if (vpas != null && !vpas.isEmpty()) {
            reports = settlementReportRepository.fetchNodalReportByVpa(fromDate, toDate);
        } else {
            reports = settlementReportRepository.fetchNodalReport(fromDate, toDate);
        }

        generateReport("Instant_Settlement_Nodal_Exception_Report_"+date+".xlsx", reports);

        // Flag transactions included in this slot report
        if (!reports.isEmpty()) {
            List<Long> ids = reports.stream().map(SettlementReport::getId).collect(java.util.stream.Collectors.toList());
            settlementReportRepository.updateReportFlag(ids);
        }

        String path = Constant.FILE_PATH+"Instant_Settlement_Nodal_Exception_Report_" + date +".xlsx";
        String filename ="Instant_Settlement_Nodal_Exception_Report_" + date +".xlsx";
        try {
            File file = new File(path);
            FileInputStream uploadStream = new FileInputStream(file);
            // Upload using SFTP
            commonUtil.uploadToSftp(uploadStream, filename, host,
                    port,
                    quiUsername,
                    quiPassword,
                    quiPath);

        }catch (Exception e){
            logger.info("Exception error--{}",e.getMessage());
            e.printStackTrace();
        }
        mailController.sendNodalCallback();

        return new ResponseEntity<>(new ApiResponse(HttpStatus.OK,"File 3 Reports Generated Successfully"),HttpStatus.OK);
    }



    @GetMapping("/report/all")
    public ResponseEntity<?> generateAllReports() throws IOException {
        final String date = DateUtil.currentDate("yyyy-MM-dd");

        String ReportfromDate=DateUtil.currentDate("yyyy-MM-dd")+" "+"00:00:00";
        String ReporttoDate=DateUtil.currentDate("yyyy-MM-dd")+" "+"23:59:59";
        // 1. Nodal Account Report — only unflagged transactions (not already in slot reports)
        generateReport("Instant_Settlement_Nodal_Exception_Report_"+date+".xlsx", settlementReportRepository.fetchNodalReportUnflagged(ReportfromDate,ReporttoDate));
        mailController.sendNodalCallback();
        String path = Constant.FILE_PATH+"Instant_Settlement_Nodal_Exception_Report_" + date +".xlsx";
        String filename ="Instant_Settlement_Nodal_Exception_Report_" + date +".xlsx";
        try {
            File file = new File(path);
            FileInputStream uploadStream = new FileInputStream(file);
            // Upload using SFTP
            commonUtil.uploadToSftp(uploadStream, filename, host,
                    port,
                    quiUsername,
                    quiPassword,
                    quiPath);

        }catch (Exception e){
            logger.info("Exception error--{}",e.getMessage());
            e.printStackTrace();
        }


        String fromDate=DateUtil.currentDate("yyyy-MM-dd")+" "+"00:00:00";
        String toDate=DateUtil.currentDate("yyyy-MM-dd")+" "+"23:59:59";
        // 2. Axis Callback Report — only unflagged transactions
        generateReport("Instant_Settlement_Callback_Exception_Report_"+date+".xlsx",
                settlementReportRepository.fetchAxisCallbackReportUnflagged(fromDate,toDate));
        mailController.sendAxisCallback();
        path = Constant.FILE_PATH+"Instant_Settlement_Callback_Exception_Report_" + date +".xlsx";
        filename ="Instant_Settlement_Callback_Exception_Report_" + date +".xlsx";
        try {
            File file = new File(path);
            FileInputStream uploadStream = new FileInputStream(file);
            // Upload using SFTP
            commonUtil.uploadToSftp(uploadStream, filename, host,
                    port,
                    quiUsername,
                    quiPassword,
                    quiPath);

        }catch (Exception e){
            logger.info("Exception error--{}",e.getMessage());
            e.printStackTrace();
        }

        String fromDateTwo=DateUtil.currentDate("yyyy-MM-dd")+" "+"00:00:00";
        String toDateTwo=DateUtil.currentDate("yyyy-MM-dd")+" "+"23:59:59";
        // 3. Axis File Report — only unflagged transactions
        generateReport("Instant_Settlement_Payout_Report_"+date+".xlsx",
                settlementReportRepository.fetchAxisFileReportUnflagged(fromDateTwo,toDateTwo));
        mailController.sendAxisFile();
        path = Constant.FILE_PATH+"Instant_Settlement_Payout_Report_" + date +".xlsx";
        filename ="Instant_Settlement_Payout_Report_" + date +".xlsx";
        try {
            File file = new File(path);
            FileInputStream uploadStream = new FileInputStream(file);
            // Upload using SFTP
            commonUtil.uploadToSftp(uploadStream, filename, host,
                    port,
                    quiUsername,
                    quiPassword,
                    quiPath);

        }catch (Exception e){
            logger.info("Exception error--{}",e.getMessage());
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ApiResponse(HttpStatus.OK,"All 3 Reports Generated Successfully"),HttpStatus.OK);
    }



//    @PostMapping("/upload-axisfile")
//    public ResponseEntity<?> uploadAxisFileAndDownload(@RequestParam("file") MultipartFile file) {
//
//        logger.info("startTimeForUploadAxisFile--{}",DateUtil.currentDate());
//
//        try {
//            try{
//                axisFileReportRepository.truncate();
//            }catch (Exception e){
//                logger.info("truncate table data--{}",e.getMessage());
//            }
//            try {
//                String yesterDay = DateUtil.yesterday();
//                logger.info("yesterDay--{}", yesterDay);
//                String yesterDayFromDate = yesterDay + " " + "00:00:00";
//                String yesterDayToDate = yesterDay + " " + "23:59:59";
//                logger.info("yesterDayFrom--{},yesterDayTo--{}", yesterDayFromDate, yesterDayToDate);
//                axisFileReportRepository.insertAxisReport(yesterDayFromDate, yesterDayToDate);
//            }catch (Exception e){
//                logger.info("backUpAxisFileReportError--{}",e.getMessage());
//            }
//
////            soundBoxService.importExcel(file, "axisfile");
//
////            File reportFile = soundBoxService.generateAxisFileReport();
//
//            InputStreamResource resource = new InputStreamResource(new FileInputStream(reportFile));
//            logger.info("FileResource--{}",resource);
//
//            logger.info("startendForUploa
//            dAxisFile--{}",DateUtil.currentDate());
//
//            return ResponseEntity.ok()
//                    .header("Content-Disposition", "attachment; filename=" + reportFile.getName())
//                    .contentType(MediaType.parseMediaType(
//                            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
//                    .body(resource);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            logger.info("upload axis file exception--{}",e.getMessage());
//            return new ResponseEntity<>(new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR,"INTERNAL_SERVER_ERROR"),HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

    /**
     * Upload Axis file - compare with our DB.
     * If Axis file has 100 txns but our DB has only 50, only those 50 are sent.
     * Txns not in our DB are marked as "No" in the Axis file output.
     */
    @PostMapping("/upload-axisfile")
    public ResponseEntity<?> uploadAxisFileAndDownload(@RequestParam("file") MultipartFile file) {
        logger.info("startTimeForUploadAxisFile--{}", DateUtil.currentDate());
        try {
            // Truncate old uploaded data
            try {
                fileRepository.truncate();
            } catch (Exception e) {
                logger.info("truncate table data--{}", e.getMessage());
            }

            // Read RRNs from uploaded Axis file (Excel)
            org.apache.poi.ss.usermodel.Workbook workbook = new XSSFWorkbook(file.getInputStream());
            org.apache.poi.ss.usermodel.Sheet sheet = workbook.getSheetAt(0);
            org.apache.poi.ss.usermodel.DataFormatter dataFormatter = new org.apache.poi.ss.usermodel.DataFormatter();

            List<String> axisRrns = new ArrayList<>();
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                org.apache.poi.ss.usermodel.Row row = sheet.getRow(i);
                if (row == null) continue;
                org.apache.poi.ss.usermodel.Cell cell = row.getCell(0); // RRN in column A
                if (cell == null) continue;
                String rrn = dataFormatter.formatCellValue(cell).trim();
                if (!rrn.isEmpty()) {
                    axisRrns.add(rrn);
                }
            }
            workbook.close();
            logger.info("Total RRNs from Axis file: {}", axisRrns.size());

            // Check which RRNs exist in our settlement_report DB
            List<SettlementReport> matchedReports = new ArrayList<>();
            List<String> unmatchedRrns = new ArrayList<>();

            if (!axisRrns.isEmpty()) {
                List<SettlementReport> found = settlementReportRepository.findByRrnIn(axisRrns);
                Set<String> foundRrnSet = found.stream()
                        .map(SettlementReport::getRrn)
                        .collect(java.util.stream.Collectors.toSet());
                matchedReports.addAll(found);

                for (String rrn : axisRrns) {
                    if (!foundRrnSet.contains(rrn)) {
                        unmatchedRrns.add(rrn);
                    }
                }
            }

            logger.info("Matched in DB: {} | Not in DB (marked No): {}", matchedReports.size(), unmatchedRrns.size());

            // Save file data with in_db status
            List<com.bijlipay.Model.FileEntityData> fileEntities = new ArrayList<>();
            Set<String> matchedRrnSet = matchedReports.stream()
                    .map(SettlementReport::getRrn)
                    .collect(java.util.stream.Collectors.toSet());
            for (String rrn : axisRrns) {
                com.bijlipay.Model.FileEntityData entity = new com.bijlipay.Model.FileEntityData();
                entity.setRrn(rrn);
                entity.setInDb(matchedRrnSet.contains(rrn) ? "YES" : "NO");
                fileEntities.add(entity);
            }
            fileRepository.saveAll(fileEntities);

            // Generate output Excel with all Axis file RRNs - matched ones get full data, unmatched marked as "No"
            XSSFWorkbook outputWorkbook = new XSSFWorkbook();
            XSSFFont font = outputWorkbook.createFont();
            font.setBold(true);
            XSSFCellStyle style = outputWorkbook.createCellStyle();
            style.setFont(font);

            XSSFSheet outputSheet = outputWorkbook.createSheet("Axis_File_Comparison");
            XSSFRow headerRow = outputSheet.createRow(0);
            XSSFCell headerCell;

            headerCell = headerRow.createCell(0); headerCell.setCellValue("RRN"); headerCell.setCellStyle(style);
            headerCell = headerRow.createCell(1); headerCell.setCellValue("TXNID"); headerCell.setCellStyle(style);
            headerCell = headerRow.createCell(2); headerCell.setCellValue("AMOUNT"); headerCell.setCellStyle(style);
            headerCell = headerRow.createCell(3); headerCell.setCellValue("VPA"); headerCell.setCellStyle(style);
            headerCell = headerRow.createCell(4); headerCell.setCellValue("CREDIT_VPA"); headerCell.setCellStyle(style);
            headerCell = headerRow.createCell(5); headerCell.setCellValue("MERCHANT_NAME"); headerCell.setCellStyle(style);
            headerCell = headerRow.createCell(6); headerCell.setCellValue("TXN_DATE"); headerCell.setCellStyle(style);
            headerCell = headerRow.createCell(7); headerCell.setCellValue("IN_OUR_DB"); headerCell.setCellStyle(style);
            headerCell = headerRow.createCell(8); headerCell.setCellValue("NODAL_ACCOUNT"); headerCell.setCellStyle(style);
            headerCell = headerRow.createCell(9); headerCell.setCellValue("AXIS_CALLBACK"); headerCell.setCellStyle(style);
            headerCell = headerRow.createCell(10); headerCell.setCellValue("AXIS_FILE"); headerCell.setCellStyle(style);

            // Create a map for quick lookup
            Map<String, SettlementReport> matchedMap = matchedReports.stream()
                    .collect(java.util.stream.Collectors.toMap(SettlementReport::getRrn, r -> r, (a, b) -> a));

            int rowIdx = 1;
            for (String rrn : axisRrns) {
                XSSFRow dataRow = outputSheet.createRow(rowIdx);
                SettlementReport report = matchedMap.get(rrn);
                if (report != null) {
                    // Transaction exists in our DB
                    dataRow.createCell(0).setCellValue(rrn);
                    dataRow.createCell(1).setCellValue(report.getTxnId() == null ? "NA" : report.getTxnId());
                    dataRow.createCell(2).setCellValue(report.getAmount() == null ? "NA" : report.getAmount());
                    dataRow.createCell(3).setCellValue(report.getVpa() == null ? "NA" : report.getVpa());
                    dataRow.createCell(4).setCellValue(report.getCreditVpa() == null ? "NA" : report.getCreditVpa());
                    dataRow.createCell(5).setCellValue(report.getMerchantName() == null ? "NA" : report.getMerchantName());
                    dataRow.createCell(6).setCellValue(report.getGeneratedDate() == null ? "NA" : report.getGeneratedDate());
                    dataRow.createCell(7).setCellValue("YES");
                    dataRow.createCell(8).setCellValue(report.getNodelAccount());
                    dataRow.createCell(9).setCellValue(report.getAxisCallBack());
                    dataRow.createCell(10).setCellValue(report.getAxisFile());
                } else {
                    // Transaction NOT in our DB - marked as No
                    dataRow.createCell(0).setCellValue(rrn);
                    dataRow.createCell(1).setCellValue("NA");
                    dataRow.createCell(2).setCellValue("NA");
                    dataRow.createCell(3).setCellValue("NA");
                    dataRow.createCell(4).setCellValue("NA");
                    dataRow.createCell(5).setCellValue("NA");
                    dataRow.createCell(6).setCellValue("NA");
                    dataRow.createCell(7).setCellValue("NO");
                    dataRow.createCell(8).setCellValue("NO");
                    dataRow.createCell(9).setCellValue("NO");
                    dataRow.createCell(10).setCellValue("NO");
                }
                rowIdx++;
            }

            soundBoxService.autoSizeColumns(outputSheet);

            // Save the file
            String outputFileName = "Axis_File_Comparison_Report_" + DateUtil.currentDate("yyyy-MM-dd") + ".xlsx";
            String outputPath = Constant.FILE_PATH + outputFileName;
            FileOutputStream fos = new FileOutputStream(outputPath);
            outputWorkbook.write(fos);
            fos.close();
            outputWorkbook.close();
            logger.info("Axis file comparison report generated: {}", outputPath);

            // Return file as download
            File outputFile = new File(outputPath);
            InputStreamResource resource =
                    new InputStreamResource(new FileInputStream(outputFile));

            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=" + outputFileName)
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(resource);

        } catch (Exception e) {
            e.printStackTrace();
            logger.info("upload axis file exception--{}", e.getMessage());
            return new ResponseEntity<>(new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/decryption")
    public ResponseEntity<?> decryption(@RequestBody ApiResponse apiResponse){
        String decryptedPage="";
        try {
            String decryption = apiResponse.getData().toString();
            logger.info("Decryption response--{}", decryption);
            RSAPublicKey axisCertPub = JWEMainClient.getRSAPublicKey(axisCertificate);
            RSAPrivateKey axisPrivate = JWEMainClient.getRSAPrivateKey(axisPrivateCertificate);
            decryptedPage = JWEUtilsForPartners.jweVerifyAndDecrypt(
                    axisCertPub, axisPrivate, decryption);
            logger.info("Decrypted Pageable Response Data---{}", decryptedPage);
        }catch (Exception e){
            logger.info("internal Decrypted Response---{}",e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(new ApiResponse(HttpStatus.BAD_REQUEST,"Something went wrong"),HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new ApiResponse(HttpStatus.OK,"SUCCESS",decryptedPage),HttpStatus.OK);
    }

    @GetMapping("/encryption")
    public ResponseEntity<?> encrypt(@RequestBody String text) throws Exception {
        String encrypt="";
        try{
            Aes256CryptUtil aes256CryptUtil1=new Aes256CryptUtil("X4bO6rMoXJSgAjhA", "AES");
            encrypt=aes256CryptUtil1.encrypt(text);
            logger.info("Encrypted Data---{}",encrypt);
        }catch (Exception e){
            logger.info("encrypted Exception---{}",e.getMessage());
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ApiResponse(HttpStatus.OK,"success",encrypt),HttpStatus.OK);
    }

    @GetMapping("/date")
    public String date(){
        String newDate="2026-06-11T05:30:31+05:30";
        logger.info("new Date--{}",newDate);
        String date=DateUtil.formatDate(newDate);
        logger.info("date---{}",date);
        return null;
    }


    @GetMapping("/truncate")
    public void truncate() throws Exception {
        settlementReportRepository.truncate();
    }


    //Get Any Date & Time Report

    @GetMapping("/report/anyTime")
    public ResponseEntity<?> generateAxisReportAnyDateAndTime(@RequestBody DateDto dateDto) throws IOException {
        final String date = DateUtil.currentDate("yyyy-MM-dd");
        logger.info("Calling Manual Api For Report...");

        //local variable
        String fromDate=dateDto.getFromDate();
        String toDate=dateDto.getToDate();

        logger.info("From Date ----->{}",fromDate);
        logger.info("To Date ------->{}",toDate);

        generateReport("Instant_Settlement_Payout_Report_"+date+".xlsx",
                settlementReportRepository.fetchAxisFileReport(fromDate,toDate));
        mailController.sendAxisFile();
        String path = Constant.FILE_PATH+"Instant_Settlement_Payout_Report_" + date +".xlsx";
        String filename ="Instant_Settlement_Payout_Report_" + date +".xlsx";
        try {
            File file = new File(path);
            FileInputStream uploadStream = new FileInputStream(file);
            // Upload using SFTP
            commonUtil.uploadToSftp(uploadStream, filename, host,
                    port,
                    quiUsername,
                    quiPassword,
                    quiPath);

        }catch (Exception e){
            logger.info("Exception error--{}",e.getMessage());
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ApiResponse(HttpStatus.OK,"File  Reports Generated Successfully"),HttpStatus.OK);
    }


    //Backup Table settlement into back_up_settelment_report


}
