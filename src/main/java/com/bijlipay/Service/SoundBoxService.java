package com.bijlipay.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.bijlipay.Config.JWEMainClient;
import com.bijlipay.Config.JWEUtilsForPartners;
import com.bijlipay.Dto.AccountStatementReportRequestBody;
import com.bijlipay.Dto.AccountStatementReportResponseBody;
import com.bijlipay.Dto.AxisTokenDto;
import com.bijlipay.Dto.DataDto;
import com.bijlipay.Dto.TransactionRecordDto;
import com.bijlipay.Model.NodalAccount;
import com.bijlipay.Model.SettlementReport;
import com.bijlipay.Repository.AxisFileReportRepository;
import com.bijlipay.Repository.BackUpSettelmentReportRepository;
import com.bijlipay.Repository.FileEntityDataRepository;
import com.bijlipay.Repository.NodalAccountRepository;
import com.bijlipay.Repository.SettlementReportRepository;
import com.bijlipay.Utill.ApiResponse;
import com.bijlipay.Utill.DateUtil;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.shaded.gson.Gson;


@Service
public class SoundBoxService {
    @Autowired
    private SettlementReportRepository settlementReportRepository;

    @Autowired
    private NodalAccountRepository nodalAccountRepository;

    @Autowired
    private BackUpSettelmentReportRepository backUpSettelmentReportRepository;

    @Autowired
    private FileEntityDataRepository fileRepository;

    @Autowired
    private AxisFileReportRepository axisFileReportRepository;

    @Autowired
    @org.springframework.beans.factory.annotation.Qualifier("switchDataSource")
    @org.springframework.context.annotation.Lazy
    private javax.sql.DataSource switchDataSource;


    @Value("${axis.publickey}")
    public String axisP12;

    @Value("${axis.publickey.password}")
    public String axisPassword;


    private final Logger logger = LoggerFactory.getLogger(SoundBoxService.class);

    @Value("${DBname}")
    public String dbName;

    @Value("${switch.datasource.url}")
    public String url;

    @Value("${switch.datasource.username}")
    public String userName;
    @Value("${switch.datasource.password}")
    public String password;

    @Value("${axis.url}")
    public String axisUrl;

    @Value("${axis.certificate.file.name}")
    public String axisCertificate;

    @Value("${axis.certificate.private.key}")
    public String axisPrivateCertificate;

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



    private Connection getConnection() throws SQLException {
        logger.info("getConnection | Acquiring connection from switchDataSource pool...");
        Connection conn = switchDataSource.getConnection();
        logger.info("getConnection | Connection acquired successfully | URL: {}", conn.getMetaData().getURL());
        return conn;
    }


    @Transactional
    public void truncate(String yesterDayFrom,String yesterDayTo) {
        logger.info("truncate | START | yesterDayFrom: {}, yesterDayTo: {}", yesterDayFrom, yesterDayTo);
        logger.info("truncate | startTimeForDelete: {}", DateUtil.currentDate());
        List<Long> id =settlementReportRepository.dayBoforeYesterDay(yesterDayFrom,yesterDayTo);
        logger.info("truncate | Records found to delete: {}", id.size());
        settlementReportRepository.delete(id);
        logger.info("truncate | END | endTimeForDelete: {}", DateUtil.currentDate());
    }

    public void autoSizeColumns(Sheet sheet) {
        if (sheet.getPhysicalNumberOfRows() > 0) {
            Row row = sheet.getRow(sheet.getFirstRowNum());
            Iterator<Cell> cellIterator = row.cellIterator();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                int columnIndex = cell.getColumnIndex();
                sheet.autoSizeColumn(columnIndex);
                sheet.createFreezePane(0, 1);
            }
        }
    }

    public String extractResultSet(String resultSetValue) {
        return resultSetValue == null ? "NA" : resultSetValue;
    }


    public NodalAccount saveAccount(NodalAccount account) {
        logger.info("saveAccount | Saving nodal account with ReferenceNo: {}", account.getReferenceNo());
        account.setCreatedAt(DateUtil.currentDateNodel());
        NodalAccount saved = nodalAccountRepository.save(account);
        logger.info("saveAccount | Saved successfully with ID: {}", saved.getId());
        return saved;
    }

    public void insertData(String yesterDayFrom,String yesterDayTo) {
        logger.info("insertData | Backing up settlement_report to back_up_settelment_report | from: {}, to: {}", yesterDayFrom, yesterDayTo);
        backUpSettelmentReportRepository.insertReport(yesterDayFrom,yesterDayTo);
        logger.info("insertData | Backup completed successfully");
    }


    private final DataFormatter formatter = new DataFormatter();

    private String getCellValue(Row row, int col) {
        Cell cell = row.getCell(col);
        if (cell == null) return null;

        return formatter.formatCellValue(cell).trim();
    }

    private Double convertToDouble(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            return Double.parseDouble(value);
        } catch (Exception e) {
            return null; // or log
        }
    }


//    public void importExcel(MultipartFile file, String type) throws Exception {
//
//        try{
//            fileRepository.truncate();
//        }catch (Exception e){
//            logger.info("FileEntityException---{}",e.getMessage());
//            e.printStackTrace();
//        }
//
//        Workbook workbook = new XSSFWorkbook(file.getInputStream());
//        Sheet sheet = workbook.getSheetAt(0);
//        List<FileEntityData> fileEntities=new ArrayList<>();
//        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
//            Row row = sheet.getRow(i);
//            if (row == null) continue;
//            String rrn = getCellValue(row, 0);
//            // Update settlement_report using RRN
//            if (rrn != null && !rrn.isEmpty()) {
//                FileEntityData txn = new FileEntityData();
//                txn.setRrn(rrn);
//                fileEntities.add(txn);
//            }
//            workbook.close();
//        }
//        fileRepository.saveAll(fileEntities);
//        axisFileReportRepository.updateData();
//    }



//    public File generateAxisFileReport() throws Exception {
//
//        String fromDate = DateUtil.Yesterday() + " 00:00:00+05:30";
//        String toDate = DateUtil.Yesterday() + " 23:59:59+05:30";
////        String fromDate=DateUtil.Yesterday()+" "+"00:00:00";
////        String toDate=DateUtil.Yesterday()+" "+"23:59:59";
//
//        logger.info("From date -->{}",fromDate);
//        logger.info("To date -->{}",toDate);
////        List<BackUpSettelmentReport> data = backUpSettelmentReportRepository.findAxisFileComparison(fromDate,toDate);
//        List<AxisFileReport> data = axisFileReportRepository.findAxisFileComparison(fromDate,toDate);
//
////     return generateExcel("Axis_File_Comparison_Report.xlsx", data);      //old file name
//        return generateExcel("Instant_Settlement_Recon_Report.xlsx", data);
//    }


//    public File generateExcel(String fileName, List<AxisFileReport> data) throws Exception {
//
//        logger.info("Instant_Settlement_Recon_Report-->{}",data.size());
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
//        logger.info("excel write");
//
//        cell = row.createCell(0); cell.setCellValue("RRN"); cell.setCellStyle(style);
//        cell = row.createCell(1); cell.setCellValue("TXNID"); cell.setCellStyle(style);
//        cell = row.createCell(2); cell.setCellValue("ORDERID"); cell.setCellStyle(style);
//        cell = row.createCell(3); cell.setCellValue("AMOUNT"); cell.setCellStyle(style);
//        cell = row.createCell(4); cell.setCellValue("MOBILE NUMBER"); cell.setCellStyle(style);
//        cell = row.createCell(5); cell.setCellValue("BANKNAME"); cell.setCellStyle(style);
//        cell = row.createCell(6); cell.setCellValue("MASKEDACCOUNTNUMBER"); cell.setCellStyle(style);
//        cell = row.createCell(7); cell.setCellValue("IFSC"); cell.setCellStyle(style);
//        cell = row.createCell(8); cell.setCellValue("VPA"); cell.setCellStyle(style);
//        cell = row.createCell(9); cell.setCellValue("ACCOUNT_CUST_NAME"); cell.setCellStyle(style);
//        cell = row.createCell(10); cell.setCellValue("RESPCODE"); cell.setCellStyle(style);
//        cell = row.createCell(11); cell.setCellValue("RESPONSE"); cell.setCellStyle(style);
//        cell = row.createCell(12); cell.setCellValue("TXN_DATE"); cell.setCellStyle(style);
//        cell = row.createCell(13); cell.setCellValue("CREDITVPA"); cell.setCellStyle(style);
//        cell = row.createCell(14); cell.setCellValue("REMARKS"); cell.setCellStyle(style);
//        cell = row.createCell(15); cell.setCellValue("SURCHARGE"); cell.setCellStyle(style);
//        cell = row.createCell(16); cell.setCellValue("TAX"); cell.setCellStyle(style);
//        cell = row.createCell(17); cell.setCellValue("DEBIT_AMOUNT"); cell.setCellStyle(style);
//        cell = row.createCell(18); cell.setCellValue("MDR_TAX"); cell.setCellStyle(style);
//        cell = row.createCell(19); cell.setCellValue("MERCHANT_ID"); cell.setCellStyle(style);
//        cell = row.createCell(20); cell.setCellValue("UNQ_CUSTID"); cell.setCellStyle(style);
//        cell = row.createCell(21); cell.setCellValue("BATCH_NO"); cell.setCellStyle(style);
//        cell = row.createCell(22); cell.setCellValue("SETTLEMENT_STATUS"); cell.setCellStyle(style);
//        cell = row.createCell(23); cell.setCellValue("INIT_MODE"); cell.setCellStyle(style);
//        cell = row.createCell(24); cell.setCellValue("PURPOSE_CODE"); cell.setCellStyle(style);
//        cell = row.createCell(25); cell.setCellValue("PAYER_ACCOUNT_TYPE"); cell.setCellStyle(style);
//        cell = row.createCell(26); cell.setCellValue("BIN"); cell.setCellStyle(style);
//        cell = row.createCell(27); cell.setCellValue("Nodal Account"); cell.setCellStyle(style);
//        cell = row.createCell(28); cell.setCellValue("Axis CallBack"); cell.setCellStyle(style);
//        cell = row.createCell(29); cell.setCellValue("Axis File"); cell.setCellStyle(style);
//
//        int i = 1;
//        for (AxisFileReport settlelist : data) {
//            row = spreadsheet.createRow(i);
//            row.createCell(0).setCellValue(settlelist.getRrn() == null ? "NA" : settlelist.getRrn());
//            logger.info("rrn--{}",settlelist.getRrn()==null?"NA":settlelist.getRrn());
//            row.createCell(1).setCellValue(settlelist.getOrderId() == null ? "NA" : settlelist.getOrderId());
////            row.createCell(2).setCellValue(settlelist.getOrderId() == null ? "NA" : settlelist.getOrderId());//old one
//            row.createCell(2).setCellValue("");//new one
//            row.createCell(3).setCellValue(settlelist.getAmount() == null ? "NA" : settlelist.getAmount());
//            row.createCell(4).setCellValue("");
//            row.createCell(5).setCellValue("");
//            row.createCell(6).setCellValue("");
//            row.createCell(7).setCellValue("");
//            row.createCell(8).setCellValue(settlelist.getVpa() == null ? "NA" : settlelist.getVpa());
//            row.createCell(9).setCellValue("");
//            row.createCell(10).setCellValue(settlelist.getRespCode() == null ? "NA" : settlelist.getRespCode());
//            row.createCell(11).setCellValue(settlelist.getResponse()== null ? "NA" : settlelist.getResponse());
//            row.createCell(12).setCellValue(settlelist.getGeneratedDate());
//            row.createCell(13).setCellValue(settlelist.getCreditVpa() == null ? "NA" : settlelist.getCreditVpa());
//            row.createCell(14).setCellValue("");
//            row.createCell(15).setCellValue("");
//            row.createCell(16).setCellValue("");
//            row.createCell(17).setCellValue("");
//            row.createCell(18).setCellValue("");
//            row.createCell(19).setCellValue(settlelist.getMerchantId() == null ? "NA" : settlelist.getMerchantId());
//            row.createCell(20).setCellValue("");
//            row.createCell(21).setCellValue("");
//            row.createCell(22).setCellValue(settlelist.getSettlementStatus() == null ? "NA" : settlelist.getSettlementStatus());
//            row.createCell(23).setCellValue("");
//            row.createCell(24).setCellValue("");
//            row.createCell(25).setCellValue(settlelist.getPayerAccountType() == null ? "NA" : settlelist.getPayerAccountType());
//            row.createCell(26).setCellValue("");
//            row.createCell(27).setCellValue(settlelist.getNodelAccount());
//            row.createCell(28).setCellValue(settlelist.getAxisCallBack());
//            row.createCell(29).setCellValue(settlelist.getAxisFile());
//            i++;
//        }
//
//        excel(spreadsheet);
//
//        // Save file in your configured path
//        File outputFile = new File(Constant.FILE_PATH + fileName);
//        FileOutputStream fos = new FileOutputStream(outputFile);
//        workbook.write(fos);
//        fos.close();
//        workbook.close();
//        System.out.println(fileName + " created successfully");
//        return outputFile;
//    }


    public void excel(Sheet sheet) {
        if (sheet.getPhysicalNumberOfRows() > 0) {
            Row row = sheet.getRow(sheet.getFirstRowNum());
            Iterator<Cell> cellIterator = row.cellIterator();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                int columnIndex = cell.getColumnIndex();
                sheet.autoSizeColumn(columnIndex);
                sheet.createFreezePane(0, 1);
            }
        }
    }



    public ResponseEntity<?> fetchDateRecord(String startDate, String endDate) throws IOException, CertificateException, UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException, ParseException, InvalidKeySpecException, JOSEException {
        logger.info("========== fetchDateRecord START ==========");
        logger.info("fetchDateRecord | startDate: {}, endDate: {}", startDate, endDate);
        //Note Used
//        Connection con = null;
//        Statement stmt = null;
        String txn = "select * from " + dbName + ".static_qr_txn where created_at>= '" + startDate + "' and created_at<= '" + endDate + "' and gateway_response_code='00' and gateway_transaction_id not in ('MBK001');";
        logger.info("txnQuery---{}", txn);
        int totalNewRecords = 0;
        int totalExistingUpdated = 0;
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet txnList = statement.executeQuery(txn)) {
            logger.info("Switch DB connection established successfully");
            logger.info("Fetch Data Record txnlist--->{}", txnList.getFetchSize());
            List<SettlementReport> settlementReports = new ArrayList<>();
            List<SettlementReport> existingToUpdate = new ArrayList<>();
            while (txnList.next()) {
                String currentRrn = txnList.getString("rrn");
                SettlementReport settlementReport1 = settlementReportRepository.findByRrn(currentRrn);
                if (settlementReport1 != null) {
                    logger.debug("RRN {} already exists in settlement_report, updating axisCallBack to YES", currentRrn);
                    settlementReport1.setAxisCallBack("YES");
                    existingToUpdate.add(settlementReport1);
                    totalExistingUpdated++;
                } else {
                    SettlementReport settlementReport = new SettlementReport();
                    settlementReport.setTxnId(txnList.getString("gateway_transaction_id"));
                    settlementReport.setAmount(txnList.getString("transaction_amount"));
                    settlementReport.setCreditVpa(txnList.getString("credit_vpa"));
                    settlementReport.setMerchantName(txnList.getString("merchant_channel_id"));
                    String newDate = txnList.getString("transaction_timestamp");
                    String date;
                    if (newDate != null) {
                        date = DateUtil.formatDate(newDate);
                    } else {
                        date = String.valueOf(DateUtil.currentDate());
                    }
                    logger.debug("New record | RRN: {}, Amount: {}, GeneratedDate: {}", currentRrn, txnList.getString("transaction_amount"), date);
                    settlementReport.setGeneratedDate(date);
                    settlementReport.setAxisCallBack("YES");
                    settlementReport.setRrn(currentRrn);
                    settlementReport.setOrderId(txnList.getString("merchant_transaction_id"));
                    settlementReport.setVpa(txnList.getString("customer_vpa"));
                    settlementReport.setMerchantId(txnList.getString("merchant_id"));
                    settlementReport.setSettlementStatus(txnList.getString("gateway_response_code").equals("00") ? "settled" : "not settled");
                    settlementReport.setResponse(txnList.getString("gateway_response_code").equals("00") ? "TRANSACTION HAS BEEN APPROVED" : "");
                    settlementReport.setInitMode(txnList.getString("init_mode"));
                    settlementReport.setPurposeCode(txnList.getString("purpose_code"));
                    settlementReport.setPayerAccountType(txnList.getString("payeractype"));
                    settlementReport.setBin(txnList.getString("bin"));
                    settlementReport.setRespCode(txnList.getString("gateway_response_code"));
                    settlementReports.add(settlementReport);
                    totalNewRecords++;
                }

                // Flush in batches of 500 to avoid holding a connection too long
                if (existingToUpdate.size() >= 500) {
                    logger.info("Flushing batch | existingToUpdate size: {}", existingToUpdate.size());
                    settlementReportRepository.saveAll(existingToUpdate);
                    existingToUpdate.clear();
                }
                if (settlementReports.size() >= 500) {
                    logger.info("Flushing batch | settlementReports (new) size: {}", settlementReports.size());
                    settlementReportRepository.saveAll(settlementReports);
                    settlementReports.clear();
                }
            }
            // Save remaining
            if (!existingToUpdate.isEmpty()) {
                logger.info("Saving remaining existingToUpdate: {} records", existingToUpdate.size());
                settlementReportRepository.saveAll(existingToUpdate);
            }
            if (!settlementReports.isEmpty()) {
                logger.info("Saving remaining new settlementReports: {} records", settlementReports.size());
                settlementReportRepository.saveAll(settlementReports);
            }
            logger.info("static_qr_txn sync complete | New records inserted: {}, Existing records updated: {}", totalNewRecords, totalExistingUpdated);
        } catch (Exception e) {
            logger.error("StaticSave FAILED | Error: {}", e.getMessage(), e);
        }
            logger.info("---------- NODAL ACCOUNT SYNC START ----------");
            List<NodalAccount> nodalAccounts = nodalAccountRepository.fetchNodalData(startDate, endDate);
            logger.info("Nodal accounts fetched from DB: {} records | startDate: {}, endDate: {}", nodalAccounts.size(), startDate, endDate);
            List<SettlementReport> settlementReportsAll = new ArrayList<>();
            List<SettlementReport> settlementReportsNodal = new ArrayList<>();

            // Batch fetch existing RRNs to avoid N+1 queries
            Set<String> nodalRrns = nodalAccounts.stream()
                    .map(NodalAccount::getReferenceNo)
                    .filter(r -> r != null && !r.isEmpty())
                    .collect(Collectors.toSet());
            logger.info("Unique nodal RRNs to check: {}", nodalRrns.size());
            Map<String, SettlementReport> existingByRrn = new HashMap<>();
            if (!nodalRrns.isEmpty()) {
                existingByRrn = settlementReportRepository.findByRrnIn(nodalRrns).stream()
                        .collect(Collectors.toMap(SettlementReport::getRrn, r -> r, (a, b) -> a));
                logger.info("Existing settlement_report records matched: {}", existingByRrn.size());
            }

            for (NodalAccount nodal : nodalAccounts) {
                String rrnNo = nodal.getReferenceNo();
                SettlementReport settlementReport = existingByRrn.get(rrnNo);
                if (settlementReport == null) {
                    SettlementReport settlementReport1 = new SettlementReport();
                    settlementReport1.setTxnId(nodal.getTransactionId());
                    settlementReport1.setRrn(nodal.getReferenceNo());
                    settlementReport1.setGeneratedDate(String.valueOf(nodal.getCreatedAt()));
                    settlementReport1.setAmount(nodal.getTransactionAmount());
                    settlementReport1.setNodelAccount("YES");
                    settlementReportsNodal.add(settlementReport1);
                    logger.debug("Nodal new record | RRN: {}, Amount: {}, GeneratedDate: {}", rrnNo, nodal.getTransactionAmount(), nodal.getCreatedAt());
                } else {
                    settlementReport.setNodelAccount("YES");
                    settlementReportsAll.add(settlementReport);
                }
            }
            logger.info("Nodal sync | Existing updated: {}, New to insert: {}", settlementReportsAll.size(), settlementReportsNodal.size());
            if (!settlementReportsAll.isEmpty()) {
                settlementReportRepository.saveAll(settlementReportsAll);
                logger.info("Nodal existing records saved successfully: {}", settlementReportsAll.size());
            }
            if (!settlementReportsNodal.isEmpty()) {
                settlementReportRepository.saveAll(settlementReportsNodal);
                logger.info("Nodal new records saved successfully: {}", settlementReportsNodal.size());
            }
            logger.info("---------- NODAL ACCOUNT SYNC END ----------");
////            logger.info("Count List--{}",count);
////            if(count>0) {
////                logger.info("Count List Entry--{}",count);
        String from = DateUtil.today() + " " + "00:00:00";
        String to   = DateUtil.today() + " " + "23:59:59";
            logger.info("---------- AXIS API SYNC START ----------");
            logger.info("Axis API date range | from: {}, to: {}", from, to);
            Gson gson = new Gson();
            AxisTokenDto axisTokenDto1 = new AxisTokenDto();
            AccountStatementReportRequestBody accountStatementReportRequestBodyDto = new AccountStatementReportRequestBody();
            accountStatementReportRequestBodyDto.setAccountNumber(axisAccuNo); //prod
            accountStatementReportRequestBodyDto.setCorpCode(axisCropCode); //prod
            accountStatementReportRequestBodyDto.setUserPrinciple(axisUserPrinciple);
            accountStatementReportRequestBodyDto.setFromDate(DateUtil.dateFormat(startDate));
            accountStatementReportRequestBodyDto.setToDate(DateUtil.dateFormat(endDate));
            logger.info("Axis request | AccountNo: {}, CorpCode: {}, FromDate: {}, ToDate: {}", axisAccuNo, axisCropCode, DateUtil.dateFormat(startDate), DateUtil.dateFormat(endDate));

            DataDto dataDto = new DataDto();
            dataDto.setAccountStatementReportRequestBody(accountStatementReportRequestBodyDto);
            axisTokenDto1.setData(dataDto);

            logger.info("Configuring SSL...");
             this.sslConfig();
            logger.info("SSL configuration completed");

            // =========================================================
            // STEP 2: FETCH ALL RRNs FROM bijlipay_transaction TABLE
            // =========================================================
            String dqrTransaction = "SELECT rrn_number FROM " + dbName
                    + ".bijlipay_transaction where created_at>= '" + from
                    + "' and created_at<= '" + to + "';";
            logger.info("Fetching RRNs from DB: {}", dqrTransaction);

            Set<String> dqrRrnSet  = new HashSet<>();
            Set<String> settleRrn = new HashSet<>();
            try (Connection connection = getConnection();
                 Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(dqrTransaction)) {
                while (resultSet.next()) {
                    String rrnVal = resultSet.getString("rrn_number");
                    if (rrnVal != null && !rrnVal.isEmpty()) {
                        dqrRrnSet.add(rrnVal.trim());
                    }
                }
                logger.info("Total RRNs loaded from bijlipay_transaction: {}", dqrRrnSet.size());
                logger.debug("RRNs in DB: {}", dqrRrnSet);
            } catch (Exception e) {
                logger.error("Error fetching RRNs from DB: {}", e.getMessage(), e);
            }

            // =========================================================
            // STEP 3: SETUP HTTP HEADERS
            // =========================================================
            HttpHeaders headers1 = new HttpHeaders();
            Date date   = DateUtil.currentDate();
            Long millis = date.getTime();
            headers1.set("x-fapi-epoch-millis",   String.valueOf(millis));
            headers1.set("x-fapi-channel-id",     axisChennelId);
            headers1.set("x-fapi-uuid",           axisUuid);
            headers1.set("x-fapi-serviceId",      axisServiceId);
            headers1.set("x-fapi-serviceVersion", axisServiceVersion);
            headers1.set("X-IBM-Client-Id",       axisClientId);
            headers1.set("X-IBM-Client-Secret",   axisClientSecret);
            headers1.setContentType(MediaType.parseMediaType("application/jose"));

            // ✅ FIXED: Use custom RestTemplate that accepts all content types
            RestTemplate  restTemplate1 = buildRestTemplate();
            RSAPublicKey  axisCertPub   = JWEMainClient.getRSAPublicKey(axisCertificate);
            RSAPrivateKey axisPrivate   = JWEMainClient.getRSAPrivateKey(axisPrivateCertificate);

            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            // =========================================================
            // STEP 4: CALL TOKEN API — FIRST TIME
            // =========================================================
            AxisTokenDto tokenDto = fetchAxisAccessToken(
                    axisCertPub, axisPrivate, headers1, axisTokenDto1, mapper, restTemplate1);

            if (tokenDto == null
                    || tokenDto.getData() == null
                    || tokenDto.getData().getAccess_token() == null) {
                logger.error("Access token is null!");
                return new ResponseEntity<>(
                        new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Access token null"),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }

            String accessToken = tokenDto.getData().getAccess_token();
            String tokenType   = tokenDto.getData().getToken_type();
            headers1.set("Authorization", tokenType + " " + accessToken);
            logger.info("Token received. Type: {} value: {}", tokenType, accessToken);

            // =========================================================
            // STEP 5: PREPARE STATEMENT REQUEST
            // =========================================================
            String statementUrl = axisUrl + "/gateway/neobanking/api/v1/account-statement-report";

            AccountStatementReportRequestBody statementRequestBody =
                    axisTokenDto1.getData().getAccountStatementReportRequestBody();

            // =========================================================
            // STEP 6: PAGINATION LOOP — FETCH ALL PAGES FROM AXIS API
            // =========================================================
            String nextCursor          = null;
            int    pageNumber          = 0;
            int    totalRecordsFetched = 0;
            int    rowsCount           = Integer.MAX_VALUE;
            int    maxPages            = 100;
            int    totalSaved          = 0;
            int    consecutiveFailures = 0;
            int    maxConsecutiveFailures = 3;

            do {
                pageNumber++;
                logger.info("=== Fetching Page {} | Cursor: {} ===", pageNumber, nextCursor);

                statementRequestBody.setNext(nextCursor != null ? nextCursor : "");

                AxisTokenDto pageRequest = new AxisTokenDto();
                DataDto      freshData   = new DataDto();
                freshData.setAccountStatementReportRequestBody(statementRequestBody);
                pageRequest.setData(freshData);
                pageRequest.setRisk(axisTokenDto1.getRisk());
                logger.debug("Json Request---{}", gson.toJson(pageRequest));

                String pagePayload   = mapper.writeValueAsString(pageRequest);
                String encryptedPage = JWEUtilsForPartners.jweEncryptAndSign(
                        axisCertPub, axisPrivate, pagePayload);

                try {
                    ResponseEntity<String> pageResponse = null;

                    try {
                        // ✅ First attempt with current token
                        HttpEntity<String> pageEntity = new HttpEntity<>(encryptedPage, headers1);
                        pageResponse = restTemplate1.postForEntity(
                                statementUrl, pageEntity, String.class);

                    } catch (HttpClientErrorException.Unauthorized ex) {
                        // ✅ Token expired (401) — refresh token and retry
                        logger.warn("Page {} | 401 Unauthorized — Token expired. Refreshing token...", pageNumber);

                        AxisTokenDto refreshedTokenDto = fetchAxisAccessToken(
                                axisCertPub, axisPrivate, headers1, axisTokenDto1, mapper, restTemplate1);

                        if (refreshedTokenDto == null
                                || refreshedTokenDto.getData() == null
                                || refreshedTokenDto.getData().getAccess_token() == null) {
                            logger.error("Page {} | Token refresh failed. Stopping.", pageNumber);
                            break;
                        }

                        String newAccessToken = refreshedTokenDto.getData().getAccess_token();
                        String newTokenType   = refreshedTokenDto.getData().getToken_type();
                        headers1.set("Authorization", newTokenType + " " + newAccessToken);
                        logger.info("Page {} | Token refreshed successfully. Retrying page...", pageNumber);

                        // ✅ Retry with refreshed token
                        HttpEntity<String> retryEntity = new HttpEntity<>(encryptedPage, headers1);
                        pageResponse = restTemplate1.postForEntity(
                                statementUrl, retryEntity, String.class);
                    }

                    // ✅ Safety check — if response is still null or body is blank, skip page
                    if (pageResponse == null || pageResponse.getBody() == null
                            || pageResponse.getBody().trim().isEmpty()) {
                        logger.warn("Page {} | Empty response body. Skipping page.", pageNumber);
                        break;
                    }

                    logger.info("Page {} | Status: {}", pageNumber, pageResponse.getStatusCode());

                    if (!pageResponse.getStatusCode().is2xxSuccessful()) {
                        logger.error("Page {} | API failed: {}", pageNumber, pageResponse.getStatusCode());
                        break;
                    }

                    // =========================================================
                    // DECRYPT PAGE RESPONSE
                    // =========================================================
                    String rawBody = pageResponse.getBody().trim();
                    logger.info("Page {} | Raw response body length: {}", pageNumber, rawBody.length());

                    // ✅ Safety check — make sure body looks like a JWE token (not a plain error message)
                    if (!rawBody.contains(".")) {
                        logger.error("Page {} | Response body does not look like JWE. Raw: {}", pageNumber, rawBody);
                        break;
                    }

                    String decryptedPage = JWEUtilsForPartners.jweVerifyAndDecrypt(
                            axisCertPub, axisPrivate, rawBody);
                    logger.debug("Decrypted Pageable Response Data---{}", decryptedPage);

                    AxisTokenDto pageDataDto = mapper.readValue(decryptedPage, AxisTokenDto.class);

                    if (pageDataDto == null
                            || pageDataDto.getData() == null
                            || pageDataDto.getData().getAccountStatementReportResponseBody() == null) {
                        logger.warn("Page {} | Empty response. Stopping.", pageNumber);
                        break;
                    }

                    AccountStatementReportResponseBody responseBody =
                            pageDataDto.getData().getAccountStatementReportResponseBody();

                    if (pageNumber == 1) {
                        rowsCount = responseBody.getRowsCount();
                        logger.info("Page 1 | Total rowsCount from Axis: {}", rowsCount);
                    }

                    List<TransactionRecordDto> records = responseBody.getData();

                    if (records == null || records.isEmpty()) {
                        logger.info("Page {} | No records. Stopping.", pageNumber);
                        break;
                    }

                    totalRecordsFetched += records.size();
                    logger.info("Page {} | Records this page: {} | Total so far: {} / {}",
                            pageNumber, records.size(), totalRecordsFetched, rowsCount);

                    // =========================================================
                    // CORE LOGIC: Compare Axis UTR against dqrRrnSet (BATCH per page)
                    // =========================================================
                    // Step 1: Collect valid UTRs and their parsed data for this page
                    List<String> validUtrs = new ArrayList<>();
                    Map<String, TransactionRecordDto> utrToRecord = new HashMap<>();
                    Map<String, String> utrToFormattedDate = new HashMap<>();

                    for (TransactionRecordDto record : records) {
                        String utr = record.getUtrNumber();

                        if (utr == null || utr.trim().isEmpty()) {
                            continue;
                        }

                        utr = utr.trim();
                        if (!utr.matches("\\d+")) {
                            logger.warn("Page {} | Skipping non-numeric UTR: {}", pageNumber, utr);
                            continue;
                        }

                        if (dqrRrnSet.contains(utr)) {
                            continue;
                        }

                        // Parse date
                        String transactionDate = record.getPstdDate();
                        String formattedDate = null;
                        if (transactionDate != null && !transactionDate.trim().isEmpty()) {
                            try {
                                DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                                LocalDateTime localDateTime = LocalDateTime.parse(transactionDate.trim(), inputFormat);
                                DateTimeFormatter outputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                                formattedDate = localDateTime.format(outputFormat);
                            } catch (DateTimeParseException e) {
                                logger.debug("Failed to parse transaction date: {}", transactionDate);
                            }
                        }

                        validUtrs.add(utr);
                        utrToRecord.put(utr, record);
                        utrToFormattedDate.put(utr, formattedDate);
                    }

                    if (!validUtrs.isEmpty()) {
                        // Step 2: Batch fetch existing records (kills N+1)
                        logger.info("Page {} | Valid UTRs to process: {}", pageNumber, validUtrs.size());
                        Map<String, SettlementReport> existingMap = settlementReportRepository
                                .findByRrnIn(validUtrs).stream()
                                .collect(Collectors.toMap(SettlementReport::getRrn, r -> r, (a, b) -> a));
                        logger.info("Page {} | Already existing in DB: {}, New to insert: {}", pageNumber, existingMap.size(), validUtrs.size() - existingMap.size());

                        List<SettlementReport> toSave = new ArrayList<>();

                        for (String utr : validUtrs) {
                            try {
                                TransactionRecordDto record = utrToRecord.get(utr);
                                BigDecimal amount = record.getAmount();
                                String formattedDate = utrToFormattedDate.get(utr);

                                SettlementReport existing = existingMap.get(utr);
                                if (existing != null) {
                                    existing.setNodelAccount("YES");
                                    toSave.add(existing);
                                } else {
                                    SettlementReport newReport = new SettlementReport();
                                    newReport.setRrn(utr);
                                    newReport.setAmount(amount != null ? amount.toPlainString() : "0");
                                    newReport.setGeneratedDate(formattedDate);
                                    newReport.setNodelAccount("YES");
                                    toSave.add(newReport);
                                    settleRrn.add(utr);
                                }
                                totalSaved++;
                            } catch (Exception saveEx) {
                                logger.error("Page {} | Failed to process UTR {}: {}",
                                        pageNumber, utr, saveEx.getMessage());
                            }
                        }

                        // Step 3: Batch save once per page
                        if (!toSave.isEmpty()) {
                            settlementReportRepository.saveAll(toSave);
                            logger.info("Page {} | Batch saved {} records", pageNumber, toSave.size());
                        }
                    }

                    if (totalRecordsFetched >= rowsCount) {
                        logger.info("Page {} | All {} records fetched. Done.", pageNumber, rowsCount);
                        break;
                    }

                    // ✅ Extract next cursor
                    String newCursor = null;
                    if (responseBody.getPaging() != null
                            && responseBody.getPaging().getCursors() != null) {
                        newCursor = responseBody.getPaging().getCursors().getNext();
                    }

                    logger.info("Page {} | Old cursor: '{}' | New cursor: '{}'",
                            pageNumber, nextCursor, newCursor);

                    // ✅ Stop if Axis echoes same cursor on last page
                    if (newCursor != null && !newCursor.isEmpty()
                            && newCursor.equals(nextCursor)) {
                        logger.warn("Page {} | Cursor unchanged. Stopping.", pageNumber);
                        break;
                    }

                    nextCursor = newCursor;

                } catch (Exception e) {
                    logger.error("Page {} | Exception: {}", pageNumber, e.getMessage(), e);
                    // Continue to the next page instead of breaking the entire loop
                    // The exception on one page should not stop processing of remaining pages
                    consecutiveFailures++;
                    if (consecutiveFailures >= maxConsecutiveFailures) {
                        logger.error("Reached {} consecutive failures. Stopping pagination.", maxConsecutiveFailures);
                        break;
                    }
                    continue;
                }

                // Reset consecutive failures on successful page processing
                consecutiveFailures = 0;

            } while (nextCursor != null && !nextCursor.isEmpty() && pageNumber < maxPages);

            // =========================================================
            // STEP 7: DONE
            // =========================================================
            logger.info("==========================================");
            logger.info("COMPLETED!");
            logger.info("Total Pages Fetched     : {}", pageNumber);
            logger.info("Total Axis Records      : {}", totalRecordsFetched);
            logger.info("Total DB RRNs           : {}", dqrRrnSet.size());
            logger.info("Total Unmatched (Saved) : {}", totalSaved);
            logger.info("==========================================");
//            String placeholders = settleRrn.stream()
//                    .map(s -> "?")
//                    .collect(Collectors.joining(", "));
            try {
                List<String> settlementReport=settlementReportRepository.emptyEntry();
                logger.info("Step 7 | Empty entries (RRN with no creditVpa) found: {}", settlementReport.size());
                if (settlementReport.isEmpty()) {
                    logger.info("Step 7 | No empty entries to process. Skipping static_qr_txn lookup.");
                } else {
                    String placeholders = settlementReport.stream()
                            .map(rrn -> "'" + rrn + "'")
                            .collect(Collectors.joining(", "));
//                String txn1 = "select * from " + dbName + ".static_qr_txn where rrn in ("+placeholders+") and gateway_response_code='00' and gateway_transaction_id not in ('MBK001');"; // For not insert issue
                    String exisitReport = "select * from " + dbName + ".static_qr_txn where rrn in ("+placeholders+") and gateway_response_code='00' and gateway_transaction_id not in ('MBK001');";
                    logger.info("Step 7 | txnQuery---{}", exisitReport);
                    try (Connection connection = getConnection();
                         Statement statement = connection.createStatement();
                         ResultSet txnList1 = statement.executeQuery(exisitReport)) {
                        logger.info("Step 7 | Switch DB connection established. Executing query...");
                        logger.info("Step 7 | Data txnlist---{}", txnList1.getFetchSize());
                        List<SettlementReport> settlementReports = new ArrayList<>();
                        int step7Updated = 0;
                        int step7Skipped = 0;
                        while (txnList1.next()) {
                            SettlementReport settlementReport1 = settlementReportRepository.fetchRrn(txnList1.getString("rrn"));
                            if (settlementReport1 != null) {
                                settlementReport1.setTxnId(txnList1.getString("gateway_transaction_id"));
                                settlementReport1.setAmount(txnList1.getString("transaction_amount"));
                                settlementReport1.setCreditVpa(txnList1.getString("credit_vpa"));
                                settlementReport1.setMerchantName(txnList1.getString("merchant_channel_id"));
                                settlementReport1.setAxisCallBack("YES");
                                settlementReport1.setRrn(txnList1.getString("rrn"));
                                settlementReport1.setOrderId(txnList1.getString("merchant_transaction_id"));
                                settlementReport1.setVpa(txnList1.getString("customer_vpa"));
                                settlementReport1.setMerchantId(txnList1.getString("merchant_id"));
                                settlementReport1.setSettlementStatus(txnList1.getString("gateway_response_code").equals("00") ? "settled" : "not settled");
                                settlementReport1.setResponse(txnList1.getString("gateway_response_code").equals("00") ? "TRANSACTION HAS BEEN APPROVED" : "");
                                settlementReport1.setInitMode(txnList1.getString("init_mode"));
                                settlementReport1.setPurposeCode(txnList1.getString("purpose_code"));
                                settlementReport1.setPayerAccountType(txnList1.getString("payeractype"));
                                settlementReport1.setBin(txnList1.getString("bin"));
                                settlementReport1.setRespCode(txnList1.getString("gateway_response_code"));
                                settlementReports.add(settlementReport1);
                                step7Updated++;
                            } else {
                                logger.warn("Step 7 | RRN {} not found in settlement_report, skipping", txnList1.getString("rrn"));
                                step7Skipped++;
                            }
                        }
                        settlementReportRepository.saveAll(settlementReports);
                        logger.info("Step 7 | Successfully saved {} records, skipped {}", step7Updated, step7Skipped);
                    }
                }
            } catch (Exception e) {
                logger.error("Step 7 | JDBC Exception: {}", e.getMessage(), e);
            }
            logger.info("---------- AXIS API SYNC END ----------");
            logger.info("========== fetchDateRecord END ==========");
            return new ResponseEntity<>(
                    new ApiResponse(HttpStatus.OK, "success"), HttpStatus.OK);
    }


    private void sslConfig() throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException, KeyManagementException {
        try {
            logger.info("sslConfig | Loading PKCS12 keystore from: {}", axisP12);
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(new FileInputStream(axisP12), axisPassword.toCharArray());
            logger.info("sslConfig | Keystore loaded successfully. Initializing KeyManagerFactory...");
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(keyStore, axisPassword.toCharArray());
            KeyManager[] kms = kmf.getKeyManagers();
            final SSLContext sslContext = SSLContext.getInstance("TLS");
            TrustManagerFactory tmf =
                    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init((KeyStore) null);
            sslContext.init(kms, tmf.getTrustManagers(), new SecureRandom());
            sslContext.setDefault(sslContext);
            HostnameVerifier hostnameVerifier = NoopHostnameVerifier.INSTANCE;
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
            logger.info("sslConfig | SSL context initialized successfully");
        }catch (Exception e){
            logger.error("sslConfig | FAILED | Exception: {}", e.getMessage(), e);
        }
    }

    // =========================================================
// ✅ HELPER METHOD 1 — Build RestTemplate that accepts all content types
// =========================================================
    private RestTemplate buildRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        // ✅ Add a StringHttpMessageConverter that accepts ALL media types
        // including text/plain, application/jose, application/json, etc.
        StringHttpMessageConverter converter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
        converter.setSupportedMediaTypes(Arrays.asList(
                MediaType.TEXT_PLAIN,
                MediaType.TEXT_HTML,
                MediaType.APPLICATION_JSON,
                MediaType.parseMediaType("application/jose"),
                MediaType.parseMediaType("application/jwt"),
                MediaType.ALL
        ));

        // ✅ Set this converter as the first one so it takes priority
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(converter);
        converters.addAll(restTemplate.getMessageConverters());
        restTemplate.setMessageConverters(converters);

        return restTemplate;
    }


    // =========================================================
// ✅ HELPER METHOD 2 — Fetch / Refresh Axis Access Token
// =========================================================
    private AxisTokenDto fetchAxisAccessToken(RSAPublicKey axisCertPub,
                                              RSAPrivateKey axisPrivate,
                                              HttpHeaders headers,
                                              AxisTokenDto axisTokenDto1,
                                              ObjectMapper mapper,
                                              RestTemplate restTemplate) {
        try {
            String tokenUrl     = axisUrl + "/gateway/neobanking/api/v1/get-access-token";
            String tokenPayload = mapper.writeValueAsString(axisTokenDto1);
            String encrypted    = JWEUtilsForPartners.jweEncryptAndSign(
                    axisCertPub, axisPrivate, tokenPayload);

            logger.info("Calling Token API: {}", tokenUrl);
            logger.info("Encrypted Token format---{}", encrypted);

            HttpEntity<String> tokenEntity   = new HttpEntity<>(encrypted, headers);
            ResponseEntity<String> tokenResp = restTemplate.postForEntity(
                    tokenUrl, tokenEntity, String.class);

            logger.info("Token API Status: {}", tokenResp.getStatusCode());

            if (!tokenResp.getStatusCode().is2xxSuccessful()) {
                logger.error("Token API returned non-2xx: {}", tokenResp.getStatusCode());
                return null;
            }

            // ✅ Safety check on token response body
            if (tokenResp.getBody() == null || tokenResp.getBody().trim().isEmpty()) {
                logger.error("Token API returned empty body");
                return null;
            }

            String decryptedToken = JWEUtilsForPartners.jweVerifyAndDecrypt(
                    axisCertPub, axisPrivate, tokenResp.getBody().trim());
            logger.info("Token decryption Payload---{}", decryptedToken);

            return mapper.readValue(decryptedToken, AxisTokenDto.class);

        } catch (Exception e) {
            logger.error("fetchAxisAccessToken failed: {}", e.getMessage(), e);
            return null;
        }
    }


}

