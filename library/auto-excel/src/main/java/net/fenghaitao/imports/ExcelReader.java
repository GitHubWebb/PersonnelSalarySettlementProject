package net.fenghaitao.imports;

import net.fenghaitao.context.ImportContext;
import net.fenghaitao.exception.AutoExcelException;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.util.SAXHelper;
import org.apache.poi.util.XMLHelper;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.File;
import java.io.InputStream;

public class ExcelReader extends DefaultHandler {
    private ImportContext importContext;

    public ExcelReader(ImportContext importContext) {
        this.importContext = importContext;
    }

    /**
     * Traversing all the workbook spreadsheet
     */
    public void process(String filename) {
        try {
            OPCPackage opc = OPCPackage.open(filename);
            XSSFReader xssfReader = new XSSFReader(opc);
            importContext.setStylesTable(xssfReader.getStylesTable());
            importContext.setSharedStringsTable(xssfReader.getSharedStringsTable());
            XSSFReader.SheetIterator sheetIterator = (XSSFReader.SheetIterator) xssfReader.getSheetsData();
            XMLReader sheetParser = fetchSheetParser();
            while (sheetIterator.hasNext()) {
                InputStream sheetInputStream = sheetIterator.next();
                if (importContext.initSheet(sheetIterator)) {
                    InputSource sheetSource = new InputSource(sheetInputStream);
                    // Parse each record of excel. In this process, the three functions startElement(), characters(),
                    // endElement() will be executed in sequence
                    sheetParser.parse(sheetSource);
                }
                sheetInputStream.close();
            }
            opc.close();
        } catch (Exception e) {
            throw new AutoExcelException(e);
        }
    }

    public XMLReader fetchSheetParser() {
        try {
            // XMLReader parser = XMLHelper.newXMLReader();
            XMLReader parser = SAXHelper.newXMLReader();
            parser.setContentHandler(new SheetHandler(importContext));
            return parser;
        } catch (Exception e) {
            throw new AutoExcelException(e);
        }
    }
}
