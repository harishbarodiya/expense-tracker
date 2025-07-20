package com.myproject.expense_tracker.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.textract.TextractClient;
import software.amazon.awssdk.services.textract.model.DetectDocumentTextRequest;
import software.amazon.awssdk.services.textract.model.DetectDocumentTextResponse;
import software.amazon.awssdk.services.textract.model.Document;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReceiptOcrServiceTest {

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private ReceiptOcrService receiptOcrService;

    @Test
    void testParseReceiptAndFetchData_dateAndAmount() throws IOException {
        String mockReceiptText = """
                        Tax Invoice/Bill of Supply/Cash Memo
                        (Original for Recipient)
                        *ASSPL-Amazon Seller Services Pvt. Ltd., ARIPL-Amazon Retail India Pvt. Ltd. (only where Amazon Retail India Pvt. Ltd. fulfillment center is co-located) 
                        Customers desirous of availing input GST credit are requested to create a Business account and purchase on Amazon.in/business from Business eligible offers 
                        Please note that this invoice is not a demand for payment
                        Page 1 of 1
                        For Appario Retail Private Ltd:
                        Authorized Signatory
                        Order Number:406-9555351-3644343 Invoice Number :DEL4-3287980
                        Order Date:10.10.2021 Invoice Details :HR-DEL4-1034-2122
                        Invoice Date :10.10.2021
                        Sl.
                        No Description Unit
                        Price Discount Qty Net
                        Amount
                        Tax
                        Rate
                        Tax
                        Type
                        Tax
                        Amount
                        Total
                        Amount
                        1 Airtel AMF-311WW Data Card (Black), 4g Hotspot
                        Support with 2300 Mah Battery | B08KHM9VBJ (
                        B08KHM9VBJ ) 
                        HSN:85176930
                        ₹1,524.58 ₹0.00 1 ₹1,524.58 18% IGST ₹274.42 ₹1,799.00
                        Shipping Charges ₹33.90 -₹33.90 ₹0.00 18% IGST ₹0.00 ₹0.00
                        TOTAL: ₹274.42 ₹1,799.00
                        Amount in Words:
                        One Thousand Seven Hundred Ninety-nine only
                        Whether tax is payable under reverse charge - No
                        Sold By :
                        Appario Retail Private Ltd 
                        *Kh No 18//21, 19//25, 34//5, 6, 7/1 min, 14/2/2
                        min, 15/1 min, 27, 35//1, 7, 8, 9/1, 9/2, 10/1, 10/2,
                        11 min, 12, 13, 14, Village - Jamalpur 
                        Gurgaon, Haryana, 122503 
                        IN 
                        PAN No:AALCA0171E 
                        GST Registration No:06AALCA0171E1Z3 
                        Dynamic QR Code:
                        Billing Address :
                        Harish barodiya 
                        Kernel house, Behind nehru park 
                        MANAWAR, MADHYA PRADESH, 454446 
                        IN 
                        State/UT Code:23 
                        Shipping Address :
                        Harish barodiya 
                        Harish barodiya 
                        Kernel house, Behind nehru park 
                        MANAWAR, MADHYA PRADESH, 454446 
                        IN 
                        State/UT Code:23 
                        Place of supply:MADHYA PRADESH 
                        Place of delivery:MADHYA PRADESH 
                        """;
        ReceiptOcrService spyService = Mockito.spy(receiptOcrService);
        Mockito.doReturn(mockReceiptText).when(spyService).extractTextFromReceipt(multipartFile);

        Map<String, Object> result = spyService.parseReceiptAndFetchData(multipartFile);

//        then
        assertNotNull(result);
//        assertEquals(1799.00, (Double) result.get("amount"), 0.01);

        LocalDate expectedDate = LocalDate.of(2021, 10, 10);
        assertEquals(expectedDate, result.get("date"));
    }
}