package com.bijlipay.Utill;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DateUtil {
    public static String yesterday() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        String currentDate = formatter.format(cal.getTime());
        System.out.println("yesterday " + currentDate);
        return currentDate;
    }

    public static String tenDaysAgo() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -10);
        String currentDate = formatter.format(cal.getTime());
        System.out.println("10 days ago " + currentDate);
        return currentDate;
    }

    public static Map<String, String> getAbsoluteDate() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String initiateDate = dateFormatter.format(cal.getTime());
        cal.add(Calendar.HOUR, +1);
        String expiryDate = dateFormatter.format(cal.getTime());
        Map<String, String> dates = new HashMap<>();
        dates.put("initialDate", initiateDate);
        dates.put("expiryDate", expiryDate);
        return dates;
    }


    public static Date longtoDate(long convert) {
        Date date = new Date(convert * 1000);
        SimpleDateFormat dateToString = new SimpleDateFormat("yyyy-MM-dd");
        Date rdate = null;
        try {
            rdate = dateToString.parse(dateToString.format(date));
            System.out.println("rdate " + rdate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return rdate;
    }


    public static String currentDateFormat(String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        Date date = new Date();
        String currentDate = formatter.format(date);
        return currentDate;
    }

    public static Date convertStringToDate(String stringCellValue, String format) {
        if (!(stringCellValue == "")) {
            SimpleDateFormat before = new SimpleDateFormat(format);
            Date date = null;
            try {
                date = before.parse(stringCellValue);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return date;
        } else {
            return null;
        }
    }

    public static Date currentDate() {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
//      System.out.println("currentDate >>>>>>>>>>>>>" + dateFormatter.format(date));
        return date;
    }

    public static String convertToString(Date paymentdate, String format) {
        SimpleDateFormat dateToString = new SimpleDateFormat(format);
        String strDate = dateToString.format(paymentdate);
        return strDate;
    }

    public static String getDate() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormatter.format(cal.getTime());
    }

    public static Date stringToDate(String date, String format) throws ParseException {
        if (date == null)
            return null;
        System.out.println("The input date : ------------------- : " + date + " Format : ---------------- : " + format);
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDateTime d1 = LocalDateTime.parse(date, df);
        System.out.println("The parsed Date :>) ----------- (<: " + d1);
        Date resDate = java.sql.Timestamp.valueOf( d1 );
        System.out.println("THe res Date :> --------------- <: " + resDate);
        return resDate;
    }

    public static String today() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        String currentDate = formatter.format(cal.getTime());
        return currentDate;
    }

    public static Date dateFormat(String date) throws ParseException {

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date dateOne = inputFormat.parse(date);
        String fromDate = sdf.format(dateOne);
        return sdf.parse(fromDate);
    }

    public static String currentDateFromString(String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        Date date = new Date();
        String currentDate = formatter.format(date);
        return currentDate;
    }

    public static String currentDate(String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        Date date = new Date();
        String currentDate = formatter.format(date);
        return currentDate;
    }

    public static String formatDate(String newDate) {
        if (newDate == null || newDate.isEmpty()) {
            return String.valueOf(DateUtil.currentDate());
        }

        DateTimeFormatter outputFormatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        try {
            // ✅ Case 1: ISO format with offset
            OffsetDateTime odt = OffsetDateTime.parse(newDate);
            return odt.toLocalDateTime().format(outputFormatter);

        } catch (DateTimeParseException e1) {
            try {
                // ✅ Case 2: Compact format yyyyMMddHHmmss
                DateTimeFormatter inputFormatter =
                        DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

                LocalDateTime ldt = LocalDateTime.parse(newDate, inputFormatter);
                return ldt.format(outputFormatter);

            } catch (DateTimeParseException e2) {
                System.out.println("Invalid date format: " + newDate);
                return null;
            }
        }
    }

    public static String LocalDateToString(LocalDateTime date){
        DateTimeFormatter dateTimeFormatter=DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formatDate=date.format(dateTimeFormatter);
        return formatDate;
    }



    //To this method date conversion 23/07/2026 14:18:11 into 2026-07-27 10:00:00+05:30
    public static String dataConversion(String inputDate) {
        log.info("Calling Date dataConversion method ---------->");
        DateTimeFormatter INPUT_FORMAT = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        DateTimeFormatter OUTPUT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssXXX");
        ZoneId IST_ZONE = ZoneId.of("Asia/Kolkata");
        if (inputDate == null || inputDate.trim().isEmpty()) {
            log.info("Input date is null or empty, returning null");
            return null;
        }
        try {
            LocalDateTime localDateTime = LocalDateTime.parse(inputDate.trim(), INPUT_FORMAT);
            ZonedDateTime zonedDateTime = localDateTime.atZone(IST_ZONE);
            String result = zonedDateTime.format(OUTPUT_FORMAT);
            log.info(" Date Conversion  successfully --->{}",result!=null?result:"NA");
            return result;
        } catch (DateTimeParseException e) {
            log.info("Failed to parse date: {}", inputDate, e);
            return null; // or return inputDate as fallback, depending on what callers expect
        }
    }
    //2026-07-25 12:17:19.407 into 2026-07-27 10:00:00+05:30
    public static String conversion(String inputDate) {
        log.info("Calling  Conversion method ---------->");
        DateTimeFormatter INPUT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        DateTimeFormatter OUTPUT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssXXX");
        ZoneId IST_ZONE = ZoneId.of("Asia/Kolkata");
        if (inputDate == null || inputDate.trim().isEmpty()) {
            log.info("Input date is null or empty, returning null");
            return null;
        }
        try {
            LocalDateTime localDateTime = LocalDateTime.parse(inputDate.trim(), INPUT_FORMAT);
            ZonedDateTime zonedDateTime = localDateTime.atZone(IST_ZONE);
            String result = zonedDateTime.format(OUTPUT_FORMAT);
            log.info(" Date Conversion  successfully --->{}",result!=null?result:"NA");
            return result;
        } catch (DateTimeParseException e) {
            log.info("Failed to parse date: {}", inputDate, e);
            return null; // or return inputDate as fallback, depending on what callers expect
        }
    }

    //2026-07-28T18:34:09+05:30 into 2026-07-27 10:00:00+05:30
    public static String ConversionData(String inputDate) {
        log.info("Calling ConversionData method ---------->");
        DateTimeFormatter OUTPUT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssXXX");
        DateTimeFormatter COMPACT_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        ZoneId IST_ZONE = ZoneId.of("Asia/Kolkata");
        if (inputDate == null || inputDate.trim().isEmpty()) {
            log.info("Input date is null or empty, returning null");
            return null;
        }
        try {
            ZonedDateTime zonedDateTime;
            // Handle yyyyMMddHHmmss
            if (inputDate.matches("\\d{14}")) {
                LocalDateTime localDateTime = LocalDateTime.parse(inputDate.trim(), COMPACT_FORMAT);
                zonedDateTime = localDateTime.atZone(IST_ZONE);
            } else {
                // Handle 2026-07-28T18:34:09+05:30
                OffsetDateTime odt = OffsetDateTime.parse(inputDate.trim());
                zonedDateTime = odt.atZoneSameInstant(IST_ZONE);
            }
            String result = zonedDateTime.format(OUTPUT_FORMAT);
            log.info("Date Conversion successfully ---> {}", result);
            return result;
        } catch (DateTimeParseException e) {
            log.error("Failed to parse date: {}", inputDate, e);
            return null;
        }
    }

    //To this method date conversion 23/07/2026 14:18:11 into 2026-07-27 10:00:00+05:30
    public static String currentDateConversion() {
        log.info("Calling Date currentDateConversion method ---------->");
        try{
            DateTimeFormatter OUTPUT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssXXX");
            ZoneId IST_ZONE = ZoneId.of("Asia/Kolkata");
            String result = ZonedDateTime.now(IST_ZONE).format(OUTPUT_FORMAT);
            log.info(" Date Conversion  successfully --->{}",result!=null?result:"NA");
            return  result;
        }catch (Exception e){
            log.info("Error ------>{}",e.getMessage());
            e.printStackTrace();
            return  null;
        }
    }
    public static String currentDateNodel() {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        return dateFormatter.format(date);
    }

    public static String Yesterday() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        String currentDate = formatter.format(cal.getTime());
        System.out.println("yesterday " + currentDate);
        return currentDate;
    }

    /**
     * [SQR_DQR_FLAG] NEW METHOD - DQR report TXN_DATE as "yyyy-MM-dd HH:mm:ss" (e.g. 2026-10-27 08:00:45).
     * Drops milliseconds and any "+05:30" offset from generated_date; returns "NA" when empty.
     */
    public static String toTxnDateNoMillis(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "NA";
        }
        String input = value.trim().replace('T', ' ');
        return input.length() >= 19 ? input.substring(0, 19) : input;
    }
}

