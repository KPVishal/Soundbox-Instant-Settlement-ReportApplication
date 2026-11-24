package com.bijlipay.Service;

import com.bijlipay.Config.Constant;
import com.bijlipay.Model.SettlementReport;
import com.bijlipay.Utill.DateUtil; // [SQR_DQR_FLAG] CHANGE - used for DQR TXN_DATE format
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class VpaService {

    @Autowired
    private SoundBoxService soundBoxService;


    public List<String> extractVpasFromExcel(MultipartFile multipartFile) throws IOException {
        List<String> vpaList = new ArrayList<>();

        log.info("The file extract method calling ------->");
        try (InputStream is = multipartFile.getInputStream();
             Workbook workbook = WorkbookFactory.create(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();

            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue; // skip header row
                }
                Cell cell = row.getCell(0); //  VPA is in column A
                if (cell == null) {
                    continue;
                }
                String vpa = formatter.formatCellValue(cell).trim();
                if (!vpa.isEmpty()) {
                    vpaList.add(vpa);
                }
            }
        } catch (InvalidFormatException e) {
            throw new RuntimeException(e);
        }
        return vpaList;
    }


    //To generate For Vpa
    public void generateReportVpa(String fileName, List<SettlementReport> settlementReports, HttpServletResponse response) throws IOException {
        log.info("Settlement VPA ---->{}", settlementReports.size());
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFFont font = workbook.createFont();
        font.setBold(true);

        XSSFCellStyle style = workbook.createCellStyle();
        style.setFont(font);

        XSSFSheet spreadsheet = workbook.createSheet("Settlement_Vpa");
        XSSFRow row = spreadsheet.createRow(0);
        XSSFCell cell;
        log.info("Excel write For Vpa");

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
            log.info("rrn--{}",settlelist.getRrn()==null?"NA":settlelist.getRrn());
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
                row.createCell(11).setCellValue("SUCCESS");                                                         // RESPONSE
                row.createCell(12).setCellValue(DateUtil.toTxnDateNoMillis(settlelist.getGeneratedDate()));         // TXN_DATE yyyy-MM-dd HH:mm:ss
                row.createCell(21).setCellValue(settlelist.getBatchNo() == null ? "NA" : settlelist.getBatchNo());  // BATCH_NO
                row.createCell(23).setCellValue(settlelist.getInitMode() == null ? "NA" : settlelist.getInitMode()); // INIT_MODE
                log.info("[SQR_DQR_FLAG] DQR row | rrn={} | TXNID={} | ORDERID={} | RESPONSE=SUCCESS | TXN_DATE={} | BATCH_NO={} | INIT_MODE={}",
                        settlelist.getRrn(), settlelist.getTxnId(), settlelist.getOrderId(),
                        DateUtil.toTxnDateNoMillis(settlelist.getGeneratedDate()), settlelist.getBatchNo(), settlelist.getInitMode());
            }
            i++;
        }

        soundBoxService.autoSizeColumns(spreadsheet);

        // Persist a copy on disk
        File dir = new File(Constant.FILE_PATH_VPA);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String filePath = Constant.FILE_PATH_VPA + fileName;
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            workbook.write(fos);
        }
        log.info("Report saved to {}", filePath);

        // Stream the same workbook to the HTTP response
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        workbook.write(response.getOutputStream());
        workbook.close();
        System.out.println(fileName + " created successfully");
    }

}
