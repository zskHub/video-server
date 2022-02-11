package com.winnerdt.common.utils;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;


public class ExcelUtil {
	//xsl格式的excel不能存取太多的数据，不能超过65536
	private static int sheetSize=65000; //单个sheet储存的大小
	@SuppressWarnings({ "resource", "deprecation" })
	public static<T> void listToExcel(List<T> data,OutputStream out,Map<String,String> fields,String excelName) throws Exception {

		//判断传入的数据源是否有数据
		if(data==null||data.size()==0) {
			throw new Exception("传入的数据源中无数据");
		}
		//解决标题乱码
//        for (String key : fields.keySet()) {
//            HttpServletRequest request = null;
//            processFileName(request,key);
//        }


		HSSFWorkbook workBook=new HSSFWorkbook();
		//计算一共有多少个sheet
		int sheetNum=data.size()/sheetSize;
		if(data.size()%sheetSize != 0) {
			sheetNum += 1;
		}
		//字段名（数据库中的字段名）和中文名（自定义的标题名字）分别存在两个数组中
		String [] fieldNames = new String[fields.size()];
		String [] chinaNames=new String[fields.size()];
		int count = 0;
		for(Entry<String, String> entry:fields.entrySet()) {
			String fieldName = entry.getKey();
			String chinaName = entry.getValue();
			fieldNames[count] = fieldName;
			chinaNames[count] = chinaName;
			count++;
		}

		//填充数据
		for(int i=0;i<sheetNum;i++) {
			int rowCount = 0;
			HSSFSheet sheet=workBook.createSheet();
			int startIndex = i*sheetSize;
			int endIndex = (i+1)*sheetSize - 1 > data.size()?data.size():(i+1)*sheetSize - 1;

		  
			HSSFRow row = sheet.createRow(rowCount);
			sheet.setColumnWidth(0, 7000);
			sheet.setColumnWidth(1, 6000);
			sheet.setColumnWidth(2, 6000);
			sheet.setColumnWidth(3, 6000);
			sheet.setColumnWidth(4, 6000);
			sheet.setColumnWidth(5, 6000);
			sheet.setColumnWidth(6, 6000);
			sheet.setColumnWidth(7, 6000);
			sheet.setColumnWidth(8, 6000);
			sheet.setColumnWidth(9, 6000);
			sheet.setColumnWidth(10, 7000);
			sheet.setColumnWidth(11, 7000);
			sheet.setColumnWidth(12, 7000);
			sheet.setColumnWidth(13, 7000);
			sheet.setColumnWidth(14, 7000);
			sheet.setColumnWidth(15, 6000);
			sheet.setColumnWidth(16, 6000);
			sheet.setColumnWidth(17, 7000);
			sheet.setColumnWidth(18, 7000);
			sheet.setColumnWidth(19, 7000);
			HSSFCellStyle cellStyle = workBook.createCellStyle();
			cellStyle.setAlignment(HorizontalAlignment.CENTER); // 居中

			HSSFFont font = workBook.createFont();
			font.setFontHeightInPoints((short) 12);//设置字体大小
			//设置单元格的行高
			row.setHeight((short) 500);

			/*
			 * 设置表格最上面的表头
			 */
//            for(int q = 0; q < 1; q++){
//                //设置表格标题
//                CellRangeAddress cra = new CellRangeAddress(0,0,0,chinaNames.length);
//                sheet.addMergedRegion(cra);
//                HSSFCell cell=row.createCell(0);
//                cell.setCellValue(excelName);//设置单元格内容
//                cell.getCellStyle().setFont(font);
//                cell.getCellStyle().setAlignment(HorizontalAlignment.CENTER);
//            }
//            rowCount++;
			
			//标题行 第一行
			row = sheet.createRow(rowCount);
			row.setHeight((short)500);
			for(int j = 0;j<chinaNames.length;j++) {
				HSSFCell cell=row.createCell(j);
				cell.setCellValue(chinaNames[j]);//设置中文标题
			}
			rowCount++;
			for(int index = startIndex;index<endIndex;index++) {
				T item = data.get(index);//获取数据的实体对象
				row = sheet.createRow(rowCount);

				for(int j = 0;j<chinaNames.length;j++) {
					//判断如果是销售额和吊牌额，设置为数字
					if("salesAmountStr".equals(fieldNames[j]) || "tagsAmountStr".equals(fieldNames[j])){
						//通过map中的字段名（英文名）反射获取该字段的值
						Field field = item.getClass().getDeclaredField(fieldNames[j]);
						field.setAccessible(true);
						Object o = field.get(item);
						String value = o == null?"":o.toString();//通过反射取值
						HSSFCell cell=row.createCell(j);

						HSSFCellStyle cellStyle1 = workBook.createCellStyle();
						cellStyle1.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
						cell.setCellStyle(cellStyle);
						cell.setCellValue(Double.valueOf(value));//设置单元格内容
					}else {
						//通过map中的字段名（英文名）反射获取该字段的值
						Field field = item.getClass().getDeclaredField(fieldNames[j]);
						field.setAccessible(true);
						Object o = field.get(item);
						String value = o == null?"":o.toString();//通过反射取值
						HSSFCell cell=row.createCell(j);
						cell.setCellValue(value);//设置单元格内容
					}

					}
				rowCount++;
				}
			}
		workBook.write(out);
		out.close();
		
	}


	public static<T> void listToExcel1(List<T> data,OutputStream out,Map<String,String> fields,String excelName) throws Exception {

		//判断传入的数据源是否有数据
		if(data==null||data.size()==0) {
			throw new Exception("传入的数据源中无数据");
		}
		//解决标题乱码
//        for (String key : fields.keySet()) {
//            HttpServletRequest request = null;
//            processFileName(request,key);
//        }


		HSSFWorkbook workBook=new HSSFWorkbook();
		//计算一共有多少个sheet
		int sheetNum=data.size()/sheetSize;
		if(data.size()%sheetSize != 0) {
			sheetNum += 1;
		}
		//字段名（数据库中的字段名）和中文名（自定义的标题名字）分别存在两个数组中
		String [] fieldNames = new String[fields.size()];
		String [] chinaNames=new String[fields.size()];
		int count = 0;
		for(Entry<String, String> entry:fields.entrySet()) {
			String fieldName = entry.getKey();
			String chinaName = entry.getValue();
			fieldNames[count] = fieldName;
			chinaNames[count] = chinaName;
			count++;
		}

		//填充数据
		for(int i=0;i<sheetNum;i++) {
			int rowCount = 0;
			HSSFSheet sheet=workBook.createSheet();
			int startIndex = i*sheetSize;
			int endIndex = (i+1)*sheetSize - 1 > data.size()?data.size():(i+1)*sheetSize - 1;


			HSSFRow row = sheet.createRow(rowCount);
			sheet.setColumnWidth(0, 3000);
			sheet.setColumnWidth(1, 3000);
			sheet.setColumnWidth(2, 3000);
			sheet.setColumnWidth(3, 3000);
			sheet.setColumnWidth(4, 3000);
			sheet.setColumnWidth(5, 3000);
			sheet.setColumnWidth(6, 3000);
			sheet.setColumnWidth(7, 3000);
			sheet.setColumnWidth(8, 3000);
			sheet.setColumnWidth(9, 3000);
			sheet.setColumnWidth(10, 3000);
			sheet.setColumnWidth(11, 3000);
			sheet.setColumnWidth(12, 3000);
			sheet.setColumnWidth(13, 3000);
			sheet.setColumnWidth(14, 3000);
			sheet.setColumnWidth(15, 3000);
			sheet.setColumnWidth(16, 3000);
			sheet.setColumnWidth(17, 3000);
			sheet.setColumnWidth(18, 3000);
			sheet.setColumnWidth(19, 3000);
			sheet.setColumnWidth(20, 3000);
			sheet.setColumnWidth(21, 3000);
			sheet.setColumnWidth(22, 3000);
			sheet.setColumnWidth(23, 3000);
			sheet.setColumnWidth(24, 3000);
			HSSFCellStyle cellStyle = workBook.createCellStyle();
			cellStyle.setAlignment(HorizontalAlignment.CENTER); // 居中

			HSSFFont font = workBook.createFont();
			font.setFontHeightInPoints((short) 12);//设置字体大小
			//设置单元格的行高
			row.setHeight((short) 500);

			/*
			 * 设置表格最上面的表头
			 */
//            for(int q = 0; q < 1; q++){
//                //设置表格标题
//                CellRangeAddress cra = new CellRangeAddress(0,0,0,chinaNames.length);
//                sheet.addMergedRegion(cra);
//                HSSFCell cell=row.createCell(0);
//                cell.setCellValue(excelName);//设置单元格内容
//                cell.getCellStyle().setFont(font);
//                cell.getCellStyle().setAlignment(HorizontalAlignment.CENTER);
//            }
//            rowCount++;

			//标题行 第一行
			row = sheet.createRow(rowCount);
			row.setHeight((short)500);
//			for(int j = 0;j<chinaNames.length;j++) {
//				HSSFCell cell=row.createCell(j);
//				cell.setCellValue(chinaNames[j]);//设置中文标题
//			}
			//设置表格标题样式，这里直接写死了样式

			//居中样式
			HSSFCellStyle contextstyle =workBook.createCellStyle();
			contextstyle.setAlignment(HorizontalAlignment.CENTER);

			//设置人群占比相关东西
			HSSFCell cellTitle1 = row.createCell(0);
			cellTitle1.setCellValue("人群占比");
			HSSFCell cellTitle2 = row.createCell(3);
			cellTitle2.setCellValue("男女比例");
			HSSFCell cellTitle3 = row.createCell(13);
			cellTitle3.setCellValue("人群购买力");
			HSSFCell cellTitle4 = row.createCell(22);
			cellTitle4.setCellValue("是否消费");
			//设置单元格合并
			CellRangeAddress region1 = new CellRangeAddress(0, 0, 0, 2);
			CellRangeAddress region2 = new CellRangeAddress(0, 0, 3, 12);
			CellRangeAddress region3 = new CellRangeAddress(0, 0, 13, 21);
			CellRangeAddress region4 = new CellRangeAddress(0, 0, 22, 23);
			sheet.addMergedRegion(region1);
			sheet.addMergedRegion(region2);
			sheet.addMergedRegion(region3);
			sheet.addMergedRegion(region4);

			// 设置单元格内容居中
			cellTitle1.setCellStyle(contextstyle);
			cellTitle2.setCellStyle(contextstyle);
			cellTitle3.setCellStyle(contextstyle);
			cellTitle4.setCellStyle(contextstyle);

			rowCount++;
			row = sheet.createRow(rowCount);

			//处理人群占比二级表头
			List<String> titleTemp1 = new ArrayList<>();
			titleTemp1.add("先锋");
			titleTemp1.add("跟随");
			titleTemp1.add("大众");
			for(int q = 0 ; q < titleTemp1.size() ; q++){
				HSSFCell cellTitle5 = row.createCell(q);
				cellTitle5.setCellValue(titleTemp1.get(q));
				cellTitle5.setCellStyle(contextstyle);
				CellRangeAddress region5 = new CellRangeAddress(1, 2, q, q);
				sheet.addMergedRegion(region5);
			}

			//处理男女比例二级表头
			List<String> titleTemp2 = new ArrayList<>();
			titleTemp2.add("18以下");
			titleTemp2.add("18-23");
			titleTemp2.add("23-30");
			titleTemp2.add("30-45");
			titleTemp2.add("45以上");
			for(int w = titleTemp1.size() ; w < titleTemp2.size()*2 + titleTemp1.size() ; w = w+2){
				HSSFCell cellTitle6 = row.createCell(w);
				cellTitle6.setCellValue(titleTemp2.get((w-3)/2));
				cellTitle6.setCellStyle(contextstyle);
				CellRangeAddress region6 = new CellRangeAddress(1, 1, w, w+1);
				sheet.addMergedRegion(region6);
			}

			//处理人群购买力二级菜单
			List<String> titleTemp3 = new ArrayList<>();
			titleTemp3.add("先锋");
			titleTemp3.add("跟随");
			titleTemp3.add("大众");
			for(int e = titleTemp2.size()*2 + 3 ; e < titleTemp3.size()*3 + titleTemp2.size()*2 + titleTemp1.size() ; e = e+3){
				HSSFCell cellTitle5 = row.createCell(e);
				cellTitle5.setCellValue(titleTemp3.get((e-13)/3));
				cellTitle5.setCellStyle(contextstyle);
				CellRangeAddress region7 = new CellRangeAddress(1, 1, e, e+2);
				sheet.addMergedRegion(region7);
			}

			//处理是否消费二级表头
			List<String> titleTemp4 = new ArrayList<>();
			titleTemp4.add("是");
			titleTemp4.add("否");
			for(int r = titleTemp3.size()*3 + titleTemp2.size()*2 + 3 ; r < titleTemp4.size() + titleTemp3.size()*3 + titleTemp2.size()*2 + titleTemp1.size() ; r++){
				HSSFCell cellTitle6 = row.createCell(r);
				cellTitle6.setCellValue(titleTemp4.get(r-22));
				cellTitle6.setCellStyle(contextstyle);
				CellRangeAddress region8 = new CellRangeAddress(1, 2, r, r);
				sheet.addMergedRegion(region8);
			}

			rowCount++;
			row = sheet.createRow(rowCount);

			//处理男女比例三级表头
			List<String> titleTemp22 = new ArrayList<>();
			titleTemp22.add("女");
			titleTemp22.add("男");
			for(int a = titleTemp1.size() ; a < titleTemp2.size()*2 + titleTemp1.size() ; a++){
				HSSFCell cellTitle7 = row.createCell(a);
				cellTitle7.setCellValue(titleTemp22.get(a%2));
				cellTitle7.setCellStyle(contextstyle);
			}

			//处理人群购买力三级表头
			List<String> titleTemp33 = new ArrayList<>();
			titleTemp33.add("低");
			titleTemp33.add("高");
			titleTemp33.add("中");
			for(int s = titleTemp2.size()*2 + titleTemp1.size() ; s < titleTemp3.size()*3 + titleTemp2.size()*2 + titleTemp1.size() ; s++){
				HSSFCell cellTitle8 = row.createCell(s);
				cellTitle8.setCellValue(titleTemp33.get(s%3));
				cellTitle8.setCellStyle(contextstyle);
			}


			rowCount++;
			for(int index = startIndex;index<endIndex;index++) {
				T item = data.get(index);//获取数据的实体对象
				row = sheet.createRow(rowCount);

				for(int j = 0;j<chinaNames.length;j++) {
					//判断如果是销售额和吊牌额，设置为数字
					if("salesAmountStr".equals(fieldNames[j]) || "tagsAmountStr".equals(fieldNames[j])){
						//通过map中的字段名（英文名）反射获取该字段的值
						Field field = item.getClass().getDeclaredField(fieldNames[j]);
						field.setAccessible(true);
						Object o = field.get(item);
						String value = o == null?"":o.toString();//通过反射取值
						HSSFCell cell=row.createCell(j);

						HSSFCellStyle cellStyle1 = workBook.createCellStyle();
						cellStyle1.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
						cell.setCellStyle(cellStyle);
						cell.setCellValue(Double.valueOf(value));//设置单元格内容
					}else {
						//通过map中的字段名（英文名）反射获取该字段的值
						Field field = item.getClass().getDeclaredField(fieldNames[j]);
						field.setAccessible(true);
						Object o = field.get(item);
						String value = o == null?"":o.toString();//通过反射取值
						HSSFCell cell=row.createCell(j);
						cell.setCellValue(value);//设置单元格内容
						cell.setCellStyle(contextstyle);
					}

				}
				rowCount++;
			}
		}
		workBook.write(out);
		out.close();

	}
	/*
	* 参数说明：
	* file：excel文件
	* isExistTitle:是否存在表头
	* titleZHToUSMap:表头如果是中文，对应的英文名称(按照文件的实际表头顺序）
	*
	*
	* */

	public static LinkedList<Map> getExcelData(MultipartFile file, boolean isExistTitle, LinkedHashMap<String,String> titleZHToUSMap) throws Exception {
		checkFile(file);
		//获得Workbook工作薄对象
		Workbook workbook = getWorkBook(file);
		//创建返回对象，把每行中的值作为一个数组，所有行作为一个集合返回
		LinkedList<Map> linkedList = new LinkedList<>();
		if(workbook != null){
			for(int sheetNum = 0;sheetNum < workbook.getNumberOfSheets();sheetNum++){
				//获得当前sheet工作表
				Sheet sheet = workbook.getSheetAt(sheetNum);
				if(sheet == null){
					continue;
				}
				//获得当前sheet的开始行
				int firstRowNum  = sheet.getFirstRowNum();
				//获得当前sheet的结束行
				int lastRowNum = sheet.getLastRowNum();

				LinkedList<String> titleZHList = new LinkedList();
				//判断是否存在表头
				if(isExistTitle){
					firstRowNum = firstRowNum+1;
					for(Entry<String,String> vo:titleZHToUSMap.entrySet()){
						titleZHList.add(vo.getValue());
					}
				}

				//循环读取所有记录

				for(int rowNum = firstRowNum ; rowNum <= lastRowNum ; rowNum++){
					//获得当前行
					Row row = sheet.getRow(rowNum);
					if(row == null){
						continue;
					}
					//获得当前行的开始列
					int firstCellNum = row.getFirstCellNum();
					//获得当前行的列数
					int lastCellNum = row.getLastCellNum();
					//判断标题的数量和每行的列数是否一致
					if(lastCellNum > titleZHList.size()){

					}
					//循环当前行
					Map<String,String> rowMap = new HashMap();
					for(int cellNum = firstCellNum ; cellNum < lastCellNum ; cellNum++){
						Cell cell = row.getCell(cellNum);
						rowMap.put(titleZHList.get(cellNum),getCellValue(cell));
					}
					linkedList.add(rowMap);
				}
			}
		}
		return linkedList;
	}

	/**
	 * 检查文件
	 * @param file
	 * @throws IOException
	 */
	public static  void checkFile(MultipartFile file) throws Exception {
		//判断文件是否存在
		if(null == file){
			throw new Exception("文件不存在！");
		}
		//获得文件名
		String fileName = file.getOriginalFilename();
		//判断文件是否是excel文件
		if(!fileName.endsWith("xls") && !fileName.endsWith("xlsx")){
			throw new Exception(fileName + "不是excel文件");
		}
	}
	public static  Workbook getWorkBook(MultipartFile file) throws Exception {
		//获得文件名
		String fileName = file.getOriginalFilename();
		//创建Workbook工作薄对象，表示整个excel
		Workbook workbook = null;
		try {
			//获取excel文件的io流
			InputStream is = file.getInputStream();
			//根据文件后缀名不同(xls和xlsx)获得不同的Workbook实现类对象
			if(fileName.endsWith("xls")){
				//2003
				workbook = new HSSFWorkbook(is);
			}else if(fileName.endsWith("xlsx")){
				//2007 及2007以上
				workbook = new XSSFWorkbook(is);
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new Exception("读取excel文件异常");

		}
		return workbook;
	}

	public static String getCellValue(Cell cell){
		String cellValue = "";
		if(cell == null){
			return cellValue;
		}
		//判断数据的类型
		switch (cell.getCellType()){
			//数字
			case NUMERIC:
				cellValue = stringDateProcess(cell);
				break;
			//字符串
			case STRING:
				cellValue = String.valueOf(cell.getStringCellValue());
				break;
			//Boolean
			case BOOLEAN:
				cellValue = String.valueOf(cell.getBooleanCellValue());
				break;
			//公式
			case FORMULA:
				cellValue = String.valueOf(cell.getCellFormula());
				break;
			//空值
			case BLANK:
				cellValue = "";
				break;
			//故障
			case ERROR:
				cellValue = "非法字符";
				break;
			default:
				cellValue = "未知类型";
				break;
		}
		return cellValue;
	}

	/**
	 * 时间格式处理
	 */
	public static String stringDateProcess(Cell cell){
		String result = new String();
		if (HSSFDateUtil.isCellDateFormatted(cell)) {// 处理日期格式、时间格式
			SimpleDateFormat sdf = null;
			if (cell.getCellStyle().getDataFormat() == HSSFDataFormat.getBuiltinFormat("h:mm")) {
				sdf = new SimpleDateFormat("HH:mm");
			} else {// 日期
				sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			}
			Date date = cell.getDateCellValue();
			result = sdf.format(date);
		} else if (cell.getCellStyle().getDataFormat() == 58) {
			// 处理自定义日期格式：m月d日(通过判断单元格的格式id解决，id的值是58)
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			double value = cell.getNumericCellValue();
			Date date = DateUtil
					.getJavaDate(value);
			result = sdf.format(date);
		} else {
			double value = cell.getNumericCellValue();
			CellStyle style = cell.getCellStyle();
			DecimalFormat format = new DecimalFormat();
			String temp = style.getDataFormatString();
			// 单元格设置成常规
			if (temp.equals("General")) {
				format.applyPattern("#");
			}
			result = format.format(value);
		}

		return result;
	}

    //解决设置名称时的乱码
//    public static String processFileName(HttpServletRequest request, String fileNames) {
//        String codedfilename = null;
//        try {
//            String agent = request.getHeader("USER-AGENT");
//            if (null != agent && -1 != agent.indexOf("MSIE") || null != agent
//                    && -1 != agent.indexOf("Trident")) {// ie
//
//                String name = java.net.URLEncoder.encode(fileNames, "UTF8");
//
//                codedfilename = name;
//            } else if (null != agent && -1 != agent.indexOf("Mozilla")) {// 火狐,chrome等
//
//
//                codedfilename = new String(fileNames.getBytes("UTF-8"), "iso-8859-1");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return codedfilename;
//    }



	public static void main(String[] args) {

		try {

//			getExcelData(file);
		}catch (Exception e){
			System.out.printf(e.toString());
		}

	}
}
