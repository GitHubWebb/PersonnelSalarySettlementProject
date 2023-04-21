package com.personal.salary.kotlin.util
//
//import com.blankj.utilcode.util.CollectionUtils
//import com.xiaomi.push.it
//import org.apache.poi.hssf.usermodel.HSSFDateUtil
//import org.apache.poi.ss.formula.eval.*
//import org.apache.poi.ss.usermodel.*
//import timber.log.Timber
//import java.io.File
//import java.io.FileInputStream
//import java.io.FileNotFoundException
//import java.io.InputStream
//import java.math.BigDecimal
//import java.text.SimpleDateFormat
//
//
//object ExcelUtil {
//
//    init {
//        /** 对Excel函数DATEDIF支持 */
//        FunctionEval.registerFunction(DATEDIFFunction.FUNCTION_NAME, DATEDIFFunction())
//    }
//
//    /**
//     * 读取Excel文件
//     *
//     * @param file
//     * @throws FileNotFoundException
//     */
//    @Throws(FileNotFoundException::class)
//    fun readExcel(file: File?) {
//        if (file == null) {
//            Timber.d("NullFile 读取Excel出错，文件为空文件")
//            return
//        }
//        val stream: InputStream = FileInputStream(file)
//        try {
//            // Create a workbook object from the Excel file
//            val workbook = WorkbookFactory.create(stream)
//
//            // Get the first sheet in the workbook
//            val sheet = workbook.getSheetAt(0)
//
//            // 获取最大行数(或者sheet.getLastRowNum())
//            val rowsCount = sheet.physicalNumberOfRows;
//            var formulaEvaluator: FormulaEvaluator =
//                workbook.getCreationHelper().createFormulaEvaluator();
//            for (r in 0 until rowsCount) {
//                // 获取第一行(表头)
//                var row: Row = sheet.getRow(r);
//                // 获取最大列数
//                var cellsCount = row.physicalNumberOfCells;
//                //每次读取一行的内容
//                for (c in 0 until cellsCount) {
//                    //将每一格子的内容转换为字符串形式
//                    var value = getCellAsString(row, c, formulaEvaluator);
//                    var cellInfo = "r:$r; c:$c; v:$value";
//                    Timber.d(cellInfo);
//                }
//            }
//
//
//            /*// Iterate over the rows in the sheet
//            for (row in sheet) {
//                // Iterate over the cells in the row
//                for (cell in row) {
//                    // Extract the cell value
//                    val value = cell.toString()//.stringCellValue
//                    // Do something with the value
//                    Timber.d("$row, ${value}")
//                }
//            }*/
//
//            // Close the workbook
//            workbook.close()
//
//        } catch (e: Exception) {
//            /* proper exception handling to be here */
//            Timber.e(e)
//        }
//    }
//
//    /**
//     * 读取excel文件中每一行的内容
//     * @param row
//     * @param c
//     * @param formulaEvaluator
//     * @return
//     */
//    fun getCellAsString(row: Row, c: Int, formulaEvaluator: FormulaEvaluator): String {
//        var value = "";
//        try {
//            var cell = row.getCell(c)
//            var cellValue = formulaEvaluator.evaluate(cell)
//
//            // 当为空时 直接返回空字符串
//            cellValue ?: return value
//
//            when (cellValue.getCellType()) {
//                Cell.CELL_TYPE_BOOLEAN -> value = "" + cellValue.getBooleanValue();
//                Cell.CELL_TYPE_NUMERIC -> {
//                    var numericValue = cellValue.getNumberValue();
//                    if (HSSFDateUtil.isCellDateFormatted(cell)) {
//                        var date = cellValue.getNumberValue();
//                        var formatter = SimpleDateFormat("dd/MM/yy");
//                        value = formatter.format(HSSFDateUtil.getJavaDate(date));
//                    } else {
//                        value = "" + numericValue;
//                    }
//                }
//                Cell.CELL_TYPE_STRING -> {
//                    value = "" + cellValue.getStringValue()
//                }
//            }
//        } catch (e: NullPointerException) {
//            /* proper error handling should be here */
//            Timber.e(e);
//        }
//        return value;
//    }
//
//    /**
//     * 根据类型后缀名简单判断是否Excel文件
//     *
//     * @param file 文件
//     * @return 是否Excel文件
//     */
//    fun checkIfExcelFile(file: File?): Boolean {
//        if (file == null) {
//            return false
//        }
//        val name = file.name
//        //”.“ 需要转义字符
//        val list = name.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
//        //划分后的小于2个元素说明不可获取类型名
//        if (list.size < 2) {
//            return false
//        }
//        val typeName = list[list.size - 1]
//        //满足xls或者xlsx才可以
//        return "xls" == typeName || "xlsx" == typeName
//    }
//}
//
//class DATEDIFFunction : org.apache.poi.ss.formula.functions.Function {
//    companion object {
//        val FUNCTION_NAME = "DATEDIF"
//    }
//
//    override fun evaluate(valueEvals: Array<out ValueEval>?, p1: Int, p2: Int): ValueEval {
//        if (valueEvals?.isEmpty() == true) return ErrorEval.VALUE_INVALID
//
//        valueEvals?.forEach {
//            if (it is AreaEvalBase) {
//                var areaEval = it as AreaEvalBase
//                var firstRow = areaEval.firstRow;
//                var lastRow = areaEval.lastRow;
//                var firstColumn = areaEval.firstColumn;
//                var lastColumn = areaEval.lastColumn;
//
//                var average: ValueEval
//                if (areaEval.isColumn) {
//                    average =
//                        getAvgeagea(getValueEvals(areaEval, firstColumn, firstRow, lastRow, false))
//                } else if (areaEval.isRow()) {
//                    average = getAvgeagea(
//                        getValueEvals(
//                            areaEval, firstRow, firstColumn, lastColumn, true
//                        )
//                    )
//                } else {
//                    return ErrorEval.FUNCTION_NOT_IMPLEMENTED;
//                }
//
//                Timber.e("DATEDIFFunction: ${average}, ${p1}, ${p2}")
//                return average
//            }
//        }
//
//        return ErrorEval.FUNCTION_NOT_IMPLEMENTED;
//
//    }
//
//    fun getValueEvals(
//        areaEval: AreaEvalBase, index: Int, start: Int, end: Int, isRow: Boolean
//    ): List<ValueEval>? {
//        val valueEvals: MutableList<ValueEval> = ArrayList()
//        if (isRow) {
//            for (i in start..end) {
//                valueEvals.add(areaEval.getAbsoluteValue(index, i))
//            }
//        } else {
//            for (i in start..end) {
//                valueEvals.add(areaEval.getAbsoluteValue(i, index))
//            }
//        }
//
//        Timber.d("getValueEvals: $valueEvals")
//        return valueEvals
//    }
//
//    fun getAvgeagea(valueEvals: List<ValueEval>?): NumberEval {
//        if (CollectionUtils.isEmpty(valueEvals)) {
//            return NumberEval.ZERO
//        }
//
//        var size = valueEvals?.size
//        var sum = BigDecimal.ZERO
//
//        for (valueEval in valueEvals!!) {
//            if (valueEval is NumberEval) {
//                var numberEval = valueEval
//                sum = sum.add(BigDecimal.valueOf(numberEval.getNumberValue()));
//            }
//        }
//        return NumberEval(
//            sum.divide(BigDecimal.valueOf(size as Long), 2, BigDecimal.ROUND_HALF_UP) as Double
//        );
//    }
//}
//
