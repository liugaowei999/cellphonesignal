package com.cttic.cell.phone.signal.utils.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.format.UnderlineStyle;
import jxl.write.Boolean;
import jxl.write.DateFormat;
import jxl.write.DateTime;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.NumberFormat;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableImage;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class ExcelOper {

	public static void createExcel(OutputStream os) throws WriteException, IOException {
		//创建工作薄
		WritableWorkbook workbook = Workbook.createWorkbook(os);
		//创建新的一页
		WritableSheet sheet = workbook.createSheet("First Sheet", 0);
		//创建要显示的内容,创建一个单元格，第一个参数为列坐标，第二个参数为行坐标，第三个参数为内容
		Label xuexiao = new Label(0, 0, "学校");
		sheet.addCell(xuexiao);
		Label zhuanye = new Label(1, 0, "专业");
		sheet.addCell(zhuanye);
		Label jingzhengli = new Label(2, 0, "专业竞争力");
		sheet.addCell(jingzhengli);

		Label qinghua = new Label(0, 1, "清华大学");
		sheet.addCell(qinghua);
		Label jisuanji = new Label(1, 1, "计算机专业");
		sheet.addCell(jisuanji);
		Label gao = new Label(2, 1, "高");
		sheet.addCell(gao);

		Label beida = new Label(0, 2, "北京大学");
		sheet.addCell(beida);
		Label falv = new Label(1, 2, "法律专业");
		sheet.addCell(falv);
		Label zhong = new Label(2, 2, "中");
		sheet.addCell(zhong);

		Label ligong = new Label(0, 3, "北京理工大学");
		sheet.addCell(ligong);
		Label hangkong = new Label(1, 3, "航空专业");
		sheet.addCell(hangkong);
		Label di = new Label(2, 3, "低");
		sheet.addCell(di);

		//把创建的内容写入到输出流中，并关闭输出流
		workbook.write();
		workbook.close();
		os.close();
	}

	/***读取Excel*/

	public static void readExcel(String filePath)

	{

		try

		{

			InputStream is = new FileInputStream(filePath);

			Workbook rwb = Workbook.getWorkbook(is);

			//这里有两种方法获取sheet表:名字和下标（从0开始）   

			//Sheet st = rwb.getSheet("original");   

			Sheet st = rwb.getSheet(0);

			/**  
			
			//获得第一行第一列单元的值  
			
			Cell c00 = st.getCell(0,0);  
			
			//通用的获取cell值的方式,返回字符串  
			
			String strc00 = c00.getContents();  
			
			//获得cell具体类型值的方式  
			
			if(c00.getType() == CellType.LABEL)  
			
			{  
			
			    LabelCell labelc00 = (LabelCell)c00;  
			
			    strc00 = labelc00.getString();  
			
			}  
			
			//输出  
			
			System.out.println(strc00);*/

			//Sheet的下标是从0开始   

			//获取第一张Sheet表   

			Sheet rst = rwb.getSheet(0);

			//获取Sheet表中所包含的总列数   

			int rsColumns = rst.getColumns();

			//获取Sheet表中所包含的总行数   

			int rsRows = rst.getRows();

			//获取指定单元格的对象引用   

			for (int i = 0; i < rsRows; i++)

			{

				for (int j = 0; j < rsColumns; j++)

				{

					Cell cell = rst.getCell(j, i);

					System.out.print(cell.getContents() + " ");

				}

				System.out.println();

			}

			//关闭   

			rwb.close();

		}

		catch (Exception e)

		{

			e.printStackTrace();

		}

	}

	/**输出Excel*/

	public static void writeExcel(OutputStream os)

	{

		try

		{

			/** 只能通过API提供的 工厂方法来创建Workbook，而不能使用WritableWorkbook的构造函数，因为类WritableWorkbook的构造函数为 protected类型：方法一：直接从目标文件中读取 WritableWorkbook wwb = Workbook.createWorkbook(new File(targetfile));方法 二：如下实例所示 将WritableWorkbook直接写入到输出流*/

			WritableWorkbook wwb = Workbook.createWorkbook(os);

			//创建Excel工作表 指定名称和位置   

			WritableSheet ws = wwb.createSheet("Test Sheet 1", 0);

			/**************往工作表中添加数据*****************/

			//1.添加Label对象   

			Label label = new Label(0, 0, "测试");

			ws.addCell(label);

			//添加带有字型Formatting对象   

			WritableFont wf = new WritableFont(WritableFont.TIMES, 18, WritableFont.BOLD, true);

			WritableCellFormat wcf = new WritableCellFormat(wf);

			Label labelcf = new Label(1, 0, "this is a label test", wcf);

			ws.addCell(labelcf);

			//添加带有字体颜色的Formatting对象   

			WritableFont wfc = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD, false,

					UnderlineStyle.NO_UNDERLINE, jxl.format.Colour.DARK_YELLOW);

			WritableCellFormat wcfFC = new WritableCellFormat(wfc);

			Label labelCF = new Label(1, 0, "Ok", wcfFC);

			ws.addCell(labelCF);

			//2.添加Number对象   

			jxl.write.Number labelN = new jxl.write.Number(0, 1, 3.1415926);

			ws.addCell(labelN);

			//添加带有formatting的Number对象   

			NumberFormat nf = new NumberFormat("#.##");

			WritableCellFormat wcfN = new WritableCellFormat(nf);

			Number labelNF = new jxl.write.Number(1, 1, 3.1415926, wcfN);

			ws.addCell(labelNF);

			//3.添加Boolean对象   

			Boolean labelB = new jxl.write.Boolean(0, 2, true);

			ws.addCell(labelB);

			Boolean labelB1 = new jxl.write.Boolean(1, 2, false);
			ws.addCell(labelB1);

			//4.添加DateTime对象   

			jxl.write.DateTime labelDT = new jxl.write.DateTime(0, 3, new java.util.Date());

			ws.addCell(labelDT);

			//5.添加带有formatting的DateFormat对象   

			DateFormat df = new DateFormat("dd MM yyyy hh:mm:ss");

			WritableCellFormat wcfDF = new WritableCellFormat(df);

			DateTime labelDTF = new DateTime(1, 3, new java.util.Date(), wcfDF);

			ws.addCell(labelDTF);

			//6.添加图片对象,jxl只支持png格式图片   

			File image = new File("f:\\1.png");

			WritableImage wimage = new WritableImage(0, 4, 6, 17, image);

			ws.addImage(wimage);

			//7.写入工作表   

			wwb.write();

			wwb.close();

		}

		catch (Exception e)

		{

			e.printStackTrace();

		}

	}

	/** 将file1拷贝后,进行修改并创建输出对象file2  
	
	 * 单元格原有的格式化修饰不能去掉，但仍可将新的单元格修饰加上去，  
	
	 * 以使单元格的内容以不同的形式表现  
	
	 */

	public static void modifyExcel(File file1, File file2)

	{

		try

		{

			Workbook rwb = Workbook.getWorkbook(file1);
			Sheet sheet = rwb.getSheet(0);

			WritableWorkbook wwb = Workbook.createWorkbook(file2);
			wwb.importSheet("aa", 1, sheet);
			wwb.write();
			wwb.close();
			rwb.close();

		}

		catch (Exception e)

		{

			e.printStackTrace();

		}

	}

	public static void main(String[] args) throws IOException, WriteException {
		//		System.out.println("人员列表文件：");
		//		InputStream in = System.in;
		//		byte[] employee = new byte[1024];
		//		in.read(employee);
		//		System.out.println(new String(employee));

		//		OutputStream oStream = new FileOutputStream(new File("d://aaa.xls"));
		//		createExcel(oStream);

		File file1 = new File("d://XX项目人员周报_XX（YYYY-MM-DD）.xls");
		File file2 = new File("d://青海交通一卡通项目人员周报_王刚（2016-12-01）.xls");
		modifyExcel(file1, file2);
	}
}
