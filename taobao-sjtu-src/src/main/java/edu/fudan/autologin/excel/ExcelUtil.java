package edu.fudan.autologin.excel;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.fudan.autologin.constants.SheetNames;
import edu.fudan.autologin.pojos.BuyerInfo;
import edu.fudan.autologin.pojos.ItemInfo;
import edu.fudan.autologin.pojos.SellerInSearchResult;
import edu.fudan.autologin.pojos.SellerRateInfo;
import edu.fudan.autologin.pojos.TopTenItemInfo;
import edu.fudan.autologin.utils.XmlConfUtil;

import jxl.CellType;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.NumberFormat;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

/**
 * Singleton pattern
 * 
 * @author JustinChen
 * 
 */
public class ExcelUtil {
	private static final Logger log = Logger.getLogger(ExcelUtil.class
			.getName());

	private static WritableWorkbook workbook;
//	private static String path = "D:\\taobao-sjtu.xls";
	private static Map<String, WritableSheet> sheets;

	public static void prepare() {
		createWorkbook();
		createSheets();
		createHeaders();
	}

	public static void createHeaders() {

		List<String> topTenPageHeaders = new ArrayList<String>();
		topTenPageHeaders.add("类别");
		topTenPageHeaders.add("产品名称");
		topTenPageHeaders.add("页面排名");
		topTenPageHeaders.add("周销量");
		topTenPageHeaders.add("周销量卖家");
		topTenPageHeaders.add("链接地址");
		writeHeader(SheetNames.TOP_TEN_SHEET, topTenPageHeaders);

		List<String> searchResultHeaders = new ArrayList<String>();
		searchResultHeaders.add("宝贝编号");
		searchResultHeaders.add("类别");
		searchResultHeaders.add("卖家名称");
		searchResultHeaders.add("全球购标识");
		searchResultHeaders.add("金牌卖家");
		searchResultHeaders.add("卖家报价");
		searchResultHeaders.add("运费");
		searchResultHeaders.add("信用卡支付");
		searchResultHeaders.add("卖家地址");
		searchResultHeaders.add("最近成交笔数");
		searchResultHeaders.add("评价条数");
		searchResultHeaders.add("消费者保障");
		searchResultHeaders.add("假一赔三");
		searchResultHeaders.add("七天退换");
		searchResultHeaders.add("正品保障");
		searchResultHeaders.add("30天维修");
		searchResultHeaders.add("Page");
		searchResultHeaders.add("Rank");
		searchResultHeaders.add("链接地址");
		writeHeader(SheetNames.SEARCH_RESULT_SHEET, searchResultHeaders);

		List<String> buyerInfoHeaders = new ArrayList<String>();
		buyerInfoHeaders.add("宝贝编号");
		buyerInfoHeaders.add("拍下价格");
		buyerInfoHeaders.add("数量");
		buyerInfoHeaders.add("付款时间");
		buyerInfoHeaders.add("款式和型号");
		buyerInfoHeaders.add("买家信用积分");
		buyerInfoHeaders.add("买家地址");
		buyerInfoHeaders.add("买家性别");
		writeHeader(SheetNames.BUYER_INFO_SHEET, buyerInfoHeaders);

		List<String> itemDetailHeaders = new ArrayList<String>();
		itemDetailHeaders.add("宝贝编号");
		itemDetailHeaders.add("价格区间");
		itemDetailHeaders.add("物流运费");
		itemDetailHeaders.add("30天售出");
		itemDetailHeaders.add("评价");
		itemDetailHeaders.add("浏览次数");
		itemDetailHeaders.add("支付");
		itemDetailHeaders.add("服务");
		itemDetailHeaders.add("规格");
		itemDetailHeaders.add("容量");
		itemDetailHeaders.add("第一条评价时间");
		itemDetailHeaders.add("最后一条评价时间");
		itemDetailHeaders.add("页面链接地址");
		itemDetailHeaders.add("卖家信用页面链接地址");
		writeHeader(SheetNames.ITEM_DETAIL_SHEET, itemDetailHeaders);

		List<String> sellerRateHeaders = new ArrayList<String>();
		sellerRateHeaders.add("宝贝编号");
		sellerRateHeaders.add("卖家名称");
		sellerRateHeaders.add("当前主营");
		sellerRateHeaders.add("所在地区");
		sellerRateHeaders.add("创店时间");
		sellerRateHeaders.add("卖家信用");
		sellerRateHeaders.add("买家信用");
		sellerRateHeaders.add("卖家当前保证金金额");
		sellerRateHeaders.add("消费者保障");
		sellerRateHeaders.add("7天退换");
		sellerRateHeaders.add("宝贝与描述相符");
		sellerRateHeaders.add("卖家的服务态度");
		sellerRateHeaders.add("卖家的发货速度");
		sellerRateHeaders.add("平均退款速度");
		sellerRateHeaders.add("近30天退款率");
		sellerRateHeaders.add("近30天投诉率");
		sellerRateHeaders.add("近30天处罚数");

		sellerRateHeaders.add("最近一周-总数-好评");
		sellerRateHeaders.add("最近一周-主营业务-好评");
		sellerRateHeaders.add("最近一周-非主营业务-好评");
		sellerRateHeaders.add("最近一周-总数-中评");
		sellerRateHeaders.add("最近一周-主营业务-中评");
		sellerRateHeaders.add("最近一周-非主营业务-中评");
		sellerRateHeaders.add("最近一周-总数-差评");
		sellerRateHeaders.add("最近一周-主营业务-差评");
		sellerRateHeaders.add("最近一周-非主营业务-差评");

		sellerRateHeaders.add("最近一月-总数-好评");
		sellerRateHeaders.add("最近一月-主营业务-好评");
		sellerRateHeaders.add("最近一月-非主营业务-好评");
		sellerRateHeaders.add("最近一月-总数-中评");
		sellerRateHeaders.add("最近一月-主营业务-中评");
		sellerRateHeaders.add("最近一月-非主营业务-中评");
		sellerRateHeaders.add("最近一月-总数-差评");
		sellerRateHeaders.add("最近一月-主营业务-差评");
		sellerRateHeaders.add("最近一月-非主营业务-差评");

		sellerRateHeaders.add("最近半年-总数-好评");
		sellerRateHeaders.add("最经半年-主营业务-好评");
		sellerRateHeaders.add("最经半年-非主营业务-好评");
		sellerRateHeaders.add("最经半年-总数-中评");
		sellerRateHeaders.add("最经半年-主营业务-中评");
		sellerRateHeaders.add("最经半年-非主营业务-中评");
		sellerRateHeaders.add("最经半年-总数-差评");
		sellerRateHeaders.add("最经半年-主营业务-差评");
		sellerRateHeaders.add("最经半年-非主营业务-差评");

		sellerRateHeaders.add("半年以前-总数-好评");
		sellerRateHeaders.add("半年以前-总数-中评");
		sellerRateHeaders.add("半年以前-总数-差评");

		sellerRateHeaders.add("卖家信用");
		sellerRateHeaders.add("主营行业");
		sellerRateHeaders.add("主营占比");
		
		sellerRateHeaders.add("href");

		writeHeader(SheetNames.USER_RATE_SHEET, sellerRateHeaders);
	}

	public static void writeHeader(String sheetName, List<String> header) {
		writeHeader(sheets.get(sheetName), header);
	}

	public static void writeHeader(WritableSheet sheet, List<String> header) {

		List<Label> labels = new ArrayList<Label>();

		for (int i = 0; i < header.size(); ++i) {
			Label label = new Label(i, 0, header.get(i));
			labels.add(label);
		}

		for (Label label : labels) {
			try {
				sheet.addCell(label);
			} catch (RowsExceededException e) {
				e.printStackTrace();
			} catch (WriteException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 得到数据格式(重载)
	 * 
	 * @return
	 */
	public static WritableCellFormat getDataCellFormat(CellType type,
			Alignment align) {
		WritableCellFormat wcf = null;
		try {
			// 字体样式
			if (type == CellType.NUMBER || type == CellType.NUMBER_FORMULA) {// 数字
				NumberFormat nf = new NumberFormat("#.00"); // 保留小数点后两位
				wcf = new WritableCellFormat(nf);
			} else if (type == CellType.DATE || type == CellType.DATE_FORMULA) {// 日期
				jxl.write.DateFormat df = new jxl.write.DateFormat(
						"yyyy-MM-dd hh:mm:ss"); // 时间显示格式
				wcf = new jxl.write.WritableCellFormat(df);
			} else {
				WritableFont wf = new WritableFont(WritableFont.TIMES, 12,
						WritableFont.NO_BOLD, false);// 字体样式(字体,大小,是否粗体,是否斜体)
				wcf = new WritableCellFormat(wf);
			}
			// 对齐方式
			wcf.setAlignment(align);
			wcf.setVerticalAlignment(VerticalAlignment.CENTRE);

			wcf.setWrap(true);// 自动换行
		} catch (WriteException e) {
			e.printStackTrace();
		}

		return wcf;
	}

	public static void createWorkbook() {
		
		String excelFilePath = XmlConfUtil.getValueByName("excelFilePath");
		log.debug("Excel file path from xml file is: "+excelFilePath);
		String basePath = excelFilePath;
		String fileExtendName = ".xls";
		
		Date date=new Date();

		SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");

		StringBuffer path = new StringBuffer();
		path.append(basePath);
		path.append("taobao-sjtu");
//		path.append(df.format(date).toString());
		path.append(fileExtendName);
		
		log.info("Path is: "+path.toString());
		try {
			
			workbook = Workbook.createWorkbook(new File(XmlConfUtil.getValueByName("excelFilePath")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (workbook == null) {
			log.error("Create workbook error.");
		}
		log.info("Open workbook success.");
	}

	/**
	 * 得到数据表头格式
	 * 
	 * @return
	 */
	public static WritableCellFormat getTitleCellFormat() {
		WritableCellFormat wcf = null;
		try {
			// 字体样式(字体,大小,是否粗体,是否斜体)
			WritableFont wf = new WritableFont(WritableFont.TIMES, 11,
					WritableFont.BOLD, false);
			wcf = new WritableCellFormat(wf);// 实例化文字格式化
			// 对齐方式
			wcf.setAlignment(Alignment.LEFT); // 水平
			wcf.setVerticalAlignment(VerticalAlignment.CENTRE); // 垂直
		} catch (WriteException e) {
			e.printStackTrace();
		}
		return wcf;
	}

	public static void createSheets() {
		int sheetNum = 0;
		sheets = new HashMap<String, WritableSheet>();

		WritableSheet sheet = workbook.createSheet(SheetNames.TOP_TEN_SHEET,
				sheetNum++);
		sheets.put(SheetNames.TOP_TEN_SHEET, sheet);

		WritableSheet sheet1 = workbook.createSheet(
				SheetNames.SEARCH_RESULT_SHEET, sheetNum++);
		sheets.put(SheetNames.SEARCH_RESULT_SHEET, sheet1);

		WritableSheet sheet2 = workbook.createSheet(
				SheetNames.ITEM_DETAIL_SHEET, sheetNum++);
		sheets.put(SheetNames.ITEM_DETAIL_SHEET, sheet2);

		WritableSheet sheet3 = workbook.createSheet(SheetNames.USER_RATE_SHEET,
				sheetNum++);
		sheets.put(SheetNames.USER_RATE_SHEET, sheet3);

		WritableSheet sheet4 = workbook.createSheet(
				SheetNames.BUYER_INFO_SHEET, sheetNum++);
		sheets.put(SheetNames.BUYER_INFO_SHEET, sheet4);
	}

	public static void writeTopTenSheet(List<TopTenItemInfo> topTenItemInfos) {
		WritableSheet sheet = sheets.get(SheetNames.TOP_TEN_SHEET);// 根据名称获取具体的sheet对象
		assert (sheet != null);
		// 将list 中的记录写入sheet中
		for (int i = 0; i < topTenItemInfos.size(); ++i) {
			int j = 0;
			TopTenItemInfo t = topTenItemInfos.get(i);
			Label l0 = new Label(j++, sheet.getRows(), t.getCategoryName());
			Label l1 = new Label(j++, sheet.getRows(), t.getItemName());
			jxl.write.Number l2 = new jxl.write.Number(j++, sheet.getRows(),
					t.getTopRank());
			jxl.write.Number weekSaleNum = new jxl.write.Number(j++, sheet.getRows(),
					t.getWeekSaleNum());
			jxl.write.Number weekSellerNum = new jxl.write.Number(j++, sheet.getRows(),
					t.getWeekSellerNum());
			
			Label l3 = new Label(j++, sheet.getRows(), t.getHref());
			try {

				sheet.addCell(l0);
				sheet.addCell(l1);
				sheet.addCell(l2);
				sheet.addCell(weekSaleNum);
				sheet.addCell(weekSellerNum);
				sheet.addCell(l3);
			} catch (RowsExceededException e) {
				e.printStackTrace();
			} catch (WriteException e) {
				e.printStackTrace();
			}
		}
	}

	public static void writeSearchResultSheet(WritableSheet sheet,
			List<SellerInSearchResult> sellerInSearchResults) {
		for (int i = 0; i < sellerInSearchResults.size(); ++i) {
			SellerInSearchResult s = sellerInSearchResults.get(i);

			Label l1 = new Label(0, sheet.getRows(), s.getSellerId());
			Label l2 = new Label(1, sheet.getRows(), s.getCategoryName());
			Label l3 = new Label(2, sheet.getRows(), s.getSellerName());
			Label l4 = new Label(3, sheet.getRows(), s.isGlobalBuy()?"全球购":"0" + "");
			Label l5 = new Label(4, sheet.getRows(), s.isGoldSeller()?"金牌卖家":"0" + "");
			Label l6 = new Label(5, sheet.getRows(), s.getPrice() + "");
			Label l7 = new Label(6, sheet.getRows(), s.getFreightPrice() + "");
			Label l8 = new Label(7, sheet.getRows(), s.isCreditCardPay()?"信用卡支付":"0" + "");
			Label l9 = new Label(8, sheet.getRows(), s.getSellerAddress());
			Label l10 = new Label(9, sheet.getRows(), s.getSaleNum() + "");
			Label l11 = new Label(10, sheet.getRows(), s.getReviews() + "");
			Label l12 = new Label(11, sheet.getRows(), s.isConsumerPromise()?"消费者保障":"0"
					+ "");
			Label l13 = new Label(12, sheet.getRows(),
					s.isLeaveACompensableThree ? "假一赔三":"0"+ "");
			Label l14 = new Label(13, sheet.getRows(), s.isSevenDayReturn()?"七天退换":"0"
					+ "");
			Label l15 = new Label(14, sheet.getRows(), s.isQualityItem()?"正品保障":"0" + "");
			Label l16 = new Label(15, sheet.getRows(), s.is30DaysMaintain()?"30天维修":"0"
					+ "");
			Label l17 = new Label(16, sheet.getRows(), s.getPage() + "");
			Label l18 = new Label(17, sheet.getRows(), s.getRank() + "");
			
			Label l19 = new Label(18, sheet.getRows(), s.getHref());
			
			try {
				sheet.addCell(l1);
				sheet.addCell(l2);
				sheet.addCell(l3);
				sheet.addCell(l4);
				sheet.addCell(l5);
				sheet.addCell(l6);
				sheet.addCell(l7);
				sheet.addCell(l8);
				sheet.addCell(l9);
				sheet.addCell(l10);
				sheet.addCell(l11);
				sheet.addCell(l12);
				sheet.addCell(l13);
				sheet.addCell(l14);
				sheet.addCell(l15);
				sheet.addCell(l16);
				sheet.addCell(l17);
				sheet.addCell(l18);
				sheet.addCell(l19);

			} catch (RowsExceededException e) {
				e.printStackTrace();
			} catch (WriteException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public static void writeSearchResultSheet(
			List<SellerInSearchResult> sellerInSearchResults) {
		WritableSheet sheet = sheets.get(SheetNames.SEARCH_RESULT_SHEET);// 根据名称获取具体的sheet对象
		assert (sheet != null);

		for (int i = 0; i < sellerInSearchResults.size(); ++i) {
			SellerInSearchResult s = sellerInSearchResults.get(i);

			Label l1 = new Label(0, sheet.getRows(), s.getSellerId());
			Label l2 = new Label(1, sheet.getRows(), s.getCategoryName());
			Label l3 = new Label(2, sheet.getRows(), s.getSellerName());
			Label l4 = new Label(3, sheet.getRows(), s.isGlobalBuy()?"全球购":"0" + "");
			Label l5 = new Label(4, sheet.getRows(), s.isGoldSeller()?"金牌卖家":"0" + "");
			Label l6 = new Label(5, sheet.getRows(), s.getPrice() + "");
			Label l7 = new Label(6, sheet.getRows(), s.getFreightPrice() + "");
			Label l8 = new Label(7, sheet.getRows(), s.isCreditCardPay()?"信用卡支付":"0" + "");
			Label l9 = new Label(8, sheet.getRows(), s.getSellerAddress());
			Label l10 = new Label(9, sheet.getRows(), s.getSaleNum() + "");
			Label l11 = new Label(10, sheet.getRows(), s.getReviews() + "");
			Label l12 = new Label(11, sheet.getRows(), s.isConsumerPromise()?"消费者保障":"0"
					+ "");
			Label l13 = new Label(12, sheet.getRows(),
					s.isLeaveACompensableThree ? "假一赔三":"0"+ "");
			Label l14 = new Label(13, sheet.getRows(), s.isSevenDayReturn()?"七天退换":"0"
					+ "");
			Label l15 = new Label(14, sheet.getRows(), s.isQualityItem()?"正品保障":"0" + "");
			Label l16 = new Label(15, sheet.getRows(), s.is30DaysMaintain()?"30天维修":"0"
					+ "");
			Label l17 = new Label(16, sheet.getRows(), s.getPage() + "");
			Label l18 = new Label(17, sheet.getRows(), s.getRank() + "");
			try {
				sheet.addCell(l1);
				sheet.addCell(l2);
				sheet.addCell(l3);
				sheet.addCell(l4);
				sheet.addCell(l5);
				sheet.addCell(l6);
				sheet.addCell(l7);
				sheet.addCell(l8);
				sheet.addCell(l9);
				sheet.addCell(l10);
				sheet.addCell(l11);
				sheet.addCell(l12);
				sheet.addCell(l13);
				sheet.addCell(l14);
				sheet.addCell(l15);
				sheet.addCell(l16);
				sheet.addCell(l17);
				sheet.addCell(l18);

			} catch (RowsExceededException e) {
				e.printStackTrace();
			} catch (WriteException e) {
				e.printStackTrace();
			}
		}
	}

	public static void writeItemDetailSheet(ItemInfo itemInfo) {
		WritableSheet sheet = sheets.get(SheetNames.ITEM_DETAIL_SHEET);// 根据名称获取具体的sheet对象
		assert (sheet != null);

		Label l0 = new Label(0, sheet.getRows(), itemInfo.getSellerId());
		Label l1 = new Label(1, sheet.getRows(), itemInfo.getPriceRange());
		Label l2 = new Label(2, sheet.getRows(), itemInfo.getFreightPrice());

		jxl.write.Number saleNumIn30Days = new Number(3, sheet.getRows(),
				itemInfo.getSaleNumIn30Days());
		jxl.write.Number reviews = new Number(4, sheet.getRows(),
				itemInfo.getReviews());
		Label l5 = new Label(5, sheet.getRows(), itemInfo.getViewCounter() + "");
		Label l6 = new Label(6, sheet.getRows(), itemInfo.getPayType());
		Label l7 = new Label(7, sheet.getRows(), itemInfo.getServiceType());
		Label l8 = new Label(8, sheet.getRows(), itemInfo.getSpec());
		Label l9 = new Label(9, sheet.getRows(), itemInfo.getCapacity());
		Label l10 = new Label(10, sheet.getRows(),
				itemInfo.getFirstReviewDate());
		Label l11 = new Label(11, sheet.getRows(), itemInfo.getLastReviewDate());
		Label l12 = new Label(12, sheet.getRows(), itemInfo.getItemDetailHref());

		try {

			sheet.addCell(l0);
			sheet.addCell(l1);
			sheet.addCell(l2);
			sheet.addCell(l5);

			sheet.addCell(l6);
			sheet.addCell(l7);
			sheet.addCell(l8);
			sheet.addCell(l9);
			sheet.addCell(l10);
			sheet.addCell(l11);
			sheet.addCell(saleNumIn30Days);
			sheet.addCell(reviews);
			sheet.addCell(l12);

		} catch (RowsExceededException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		}
	}

	public static void writeItemDetailSheet(WritableSheet sheet, ItemInfo itemInfo) {

		Label l0 = new Label(0, sheet.getRows(), itemInfo.getSellerId());
		Label l1 = new Label(1, sheet.getRows(), itemInfo.getPriceRange());
		Label l2 = new Label(2, sheet.getRows(), itemInfo.getFreightPrice());

		jxl.write.Number saleNumIn30Days = new Number(3, sheet.getRows(),
				itemInfo.getSaleNumIn30Days());
		// Label l3 = new Label(3,sheet.getRows(),
		// itemInfo.getSaleNumIn30Days());
		jxl.write.Number reviews = new Number(4, sheet.getRows(),
				itemInfo.getReviews());
		// Label l4 = new Label(4,sheet.getRows(), itemInfo.getReviews());
		Label l5 = new Label(5, sheet.getRows(), itemInfo.getViewCounter()+"");
		Label l6 = new Label(6, sheet.getRows(), itemInfo.getPayType());
		Label l7 = new Label(7, sheet.getRows(), itemInfo.getServiceType());
		Label l8 = new Label(8, sheet.getRows(), itemInfo.getSpec());
		Label l9 = new Label(9, sheet.getRows(), itemInfo.getCapacity());
		Label l10 = new Label(10, sheet.getRows(), itemInfo.getFirstReviewDate());
		Label l11 = new Label(11, sheet.getRows(), itemInfo.getLastReviewDate());
		Label l12 = new Label(12,sheet.getRows(), itemInfo.getItemDetailHref());
		Label l13 = new Label(13, sheet.getRows(), itemInfo.getUserRateHref());

		try {

			sheet.addCell(l0);
			sheet.addCell(l1);
			sheet.addCell(l2);
			sheet.addCell(l5);
			sheet.addCell(l6);
			sheet.addCell(l7);
			sheet.addCell(l8);
			sheet.addCell(l9);
			sheet.addCell(l10);
			sheet.addCell(l11);
			sheet.addCell(saleNumIn30Days);
			sheet.addCell(reviews);
			sheet.addCell(l12);
			sheet.addCell(l13);

		} catch (RowsExceededException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void writeUserRateSheet(SellerRateInfo sellerRateInfo) {
		WritableSheet sheet = sheets.get(SheetNames.USER_RATE_SHEET);// 根据名称获取具体的sheet对象
		assert (sheet != null);

		Label l0 = new Label(0, sheet.getRows(), sellerRateInfo.getSellerId());
		Label l1 = new Label(1, sheet.getRows(), sellerRateInfo.getSellerName());
		Label l2 = new Label(2, sheet.getRows(), sellerRateInfo.getMainSale());
		Label l3 = new Label(3, sheet.getRows(), sellerRateInfo.getLocation());
		Label l4 = new Label(4, sheet.getRows(),
				sellerRateInfo.getCreateShopDate());
		Label l5 = new Label(5, sheet.getRows(), sellerRateInfo.getSellerRate());
		Label l6 = new Label(6, sheet.getRows(), sellerRateInfo.getBuyerRate());
		Label l7 = new Label(7, sheet.getRows(), sellerRateInfo.getChargeNum());

		Label l8 = new Label(8, sheet.getRows(),
				sellerRateInfo.isConsumerPromise() ? "是" : "否");
		Label l9 = new Label(9, sheet.getRows(),
				sellerRateInfo.isSevenDayReturn() ? "是" : "否");
		Label l10 = new Label(10, sheet.getRows(),
				sellerRateInfo.getMatchScore());
		Label l11 = new Label(11, sheet.getRows(),
				sellerRateInfo.getServiceScore());
		Label l12 = new Label(12, sheet.getRows(),
				sellerRateInfo.getConsignmentScore());
		
		Label l90 = new Label(13, sheet.getRows(),sellerRateInfo.getRefundmentScore());
		Label l13 = new Label(13+1, sheet.getRows(),
				sellerRateInfo.getRefundmentRateScore());
		Label l14 = new Label(14+1, sheet.getRows(),
				sellerRateInfo.getComplaintScore());
		Label l15 = new Label(15+1, sheet.getRows(),
				sellerRateInfo.getPunishmentScore());

		Label l16 = new Label(16+1, sheet.getRows(),
				sellerRateInfo.getWeekSumRateOk());
		Label l17 = new Label(17+1, sheet.getRows(),
				sellerRateInfo.getWeekMainRateOk());
		Label l18 = new Label(18+1, sheet.getRows(),
				sellerRateInfo.getWeekNotmainRateOk());
		
		Label l19 = new Label(19+1, sheet.getRows(),
				sellerRateInfo.getWeekSumRateNormal());
		Label l20 = new Label(20+1, sheet.getRows(),
				sellerRateInfo.getWeekMainRateNormal());
		Label l21 = new Label(21+1, sheet.getRows(),
				sellerRateInfo.getWeekNotmainRateNormal());
		
		Label l22 = new Label(22+1, sheet.getRows(),
				sellerRateInfo.getWeekSumRateBad());
		Label l23 = new Label(23+1, sheet.getRows(),
				sellerRateInfo.getWeekMainRateBad());
		Label l24 = new Label(24+1, sheet.getRows(),
				sellerRateInfo.getWeekNotmainRateBad());

		Label l25 = new Label(25+1, sheet.getRows(),
				sellerRateInfo.getMonthSumRateOk());
		Label l26 = new Label(26+1, sheet.getRows(),
				sellerRateInfo.getMonthMainRateOk());
		Label l27 = new Label(27+1, sheet.getRows(),
				sellerRateInfo.getMonthNotmainRateOk());
		Label l28 = new Label(28+1, sheet.getRows(),
				sellerRateInfo.getMonthSumRateNormal());
		Label l29 = new Label(29+1, sheet.getRows(),
				sellerRateInfo.getMonthMainRateNormal());
		Label l30 = new Label(30+1, sheet.getRows(),
				sellerRateInfo.getMonthNotmainRateNormal());
		Label l31 = new Label(31+1, sheet.getRows(),
				sellerRateInfo.getMonthSumRateBad());
		Label l32 = new Label(32+1, sheet.getRows(),
				sellerRateInfo.getMonthMainRateBad());
		Label l33 = new Label(33+1, sheet.getRows(),
				sellerRateInfo.getMonthNotmainRateBad());

		Label l34 = new Label(34+1, sheet.getRows(),
				sellerRateInfo.getHalfYearSumRateOk());
		Label l35 = new Label(35+1, sheet.getRows(),
				sellerRateInfo.getHalfYearMainRateOk());
		Label l36 = new Label(36+1, sheet.getRows(),
				sellerRateInfo.getHalfYearNotmainRateOk());
		Label l37 = new Label(37+1, sheet.getRows(),
				sellerRateInfo.getHalfYearSumRateNormal());
		Label l38 = new Label(38+1, sheet.getRows(),
				sellerRateInfo.getHalfYearMainRateNormal());
		Label l39 = new Label(39+1, sheet.getRows(),
				sellerRateInfo.getHalfYearNotmainRateNormal());
		Label l40 = new Label(40+1, sheet.getRows(),
				sellerRateInfo.getHalfYearSumRateBad());
		Label l41 = new Label(41+1, sheet.getRows(),
				sellerRateInfo.getHalfYearMainRateBad());
		Label l42 = new Label(42+1, sheet.getRows(),
				sellerRateInfo.getHalfYearNotmainRateBad());

		Label l43 = new Label(43+1, sheet.getRows(),
				sellerRateInfo.getBeforeHalfYearSumRateOk());
		Label l44 = new Label(44+1, sheet.getRows(),
				sellerRateInfo.getBeforeHalfYearSumRateNormal());
		Label l45 = new Label(45+1, sheet.getRows(),
				sellerRateInfo.getBeforeHalfYearSumRateBad());

		Label l46 = new Label(46+1, sheet.getRows(),
				sellerRateInfo.getSellerRate());
		Label l47 = new Label(47+1, sheet.getRows(),
				sellerRateInfo.getMainBusiness());
		Label l48 = new Label(48+1, sheet.getRows(),
				sellerRateInfo.getMainBusinessPercentage());
		Label l50 = new Label(50,sheet.getRows(),sellerRateInfo.getSellerRateHref());

		try {
			sheet.addCell(l0);
			sheet.addCell(l1);
			sheet.addCell(l2);
			sheet.addCell(l3);
			sheet.addCell(l4);
			sheet.addCell(l5);
			sheet.addCell(l6);
			sheet.addCell(l7);
			sheet.addCell(l8);
			sheet.addCell(l9);
			sheet.addCell(l10);
			sheet.addCell(l11);
			sheet.addCell(l12);
			sheet.addCell(l13);
			sheet.addCell(l14);
			sheet.addCell(l15);
			sheet.addCell(l16);
			sheet.addCell(l17);
			sheet.addCell(l18);
			sheet.addCell(l19);
			sheet.addCell(l20);
			sheet.addCell(l21);
			sheet.addCell(l22);
			sheet.addCell(l23);
			sheet.addCell(l24);
			sheet.addCell(l25);
			sheet.addCell(l26);
			sheet.addCell(l27);
			sheet.addCell(l28);
			sheet.addCell(l29);
			sheet.addCell(l30);
			sheet.addCell(l31);
			sheet.addCell(l32);
			sheet.addCell(l33);
			sheet.addCell(l34);
			sheet.addCell(l35);
			sheet.addCell(l36);
			sheet.addCell(l37);
			sheet.addCell(l38);
			sheet.addCell(l39);
			sheet.addCell(l40);
			sheet.addCell(l41);
			sheet.addCell(l42);
			sheet.addCell(l43);
			sheet.addCell(l44);
			sheet.addCell(l45);
			sheet.addCell(l46);
			sheet.addCell(l47);
			sheet.addCell(l48);
			sheet.addCell(l50);
			sheet.addCell(l90);
		} catch (RowsExceededException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		}
	}
	
	public static void writeUserRateSheet(WritableSheet sheet, SellerRateInfo sellerRateInfo) {

		Label l0 = new Label(0, sheet.getRows(), sellerRateInfo.getSellerId());
		Label l1 = new Label(1, sheet.getRows(), sellerRateInfo.getSellerName());
		Label l2 = new Label(2, sheet.getRows(), sellerRateInfo.getMainSale());
		Label l3 = new Label(3, sheet.getRows(), sellerRateInfo.getLocation());
		Label l4 = new Label(4, sheet.getRows(),
				sellerRateInfo.getCreateShopDate());
		Label l5 = new Label(5, sheet.getRows(), sellerRateInfo.getSellerRate());
		Label l6 = new Label(6, sheet.getRows(), sellerRateInfo.getBuyerRate());
		Label l7 = new Label(7, sheet.getRows(), sellerRateInfo.getChargeNum());

		Label l8 = new Label(8, sheet.getRows(),
				sellerRateInfo.isConsumerPromise() ? "是" : "否");
		Label l9 = new Label(9, sheet.getRows(),
				sellerRateInfo.isSevenDayReturn() ? "是" : "否");
		Label l10 = new Label(10, sheet.getRows(),
				sellerRateInfo.getMatchScore());
		Label l11 = new Label(11, sheet.getRows(),
				sellerRateInfo.getServiceScore());
		Label l12 = new Label(12, sheet.getRows(),
				sellerRateInfo.getConsignmentScore());
		
		Label l90 = new Label(13, sheet.getRows(),sellerRateInfo.getRefundmentScore());
		Label l13 = new Label(13+1, sheet.getRows(),
				sellerRateInfo.getRefundmentRateScore());
		Label l14 = new Label(14+1, sheet.getRows(),
				sellerRateInfo.getComplaintScore());
		Label l15 = new Label(15+1, sheet.getRows(),
				sellerRateInfo.getPunishmentScore());

		Label l16 = new Label(16+1, sheet.getRows(),
				sellerRateInfo.getWeekSumRateOk());
		Label l17 = new Label(17+1, sheet.getRows(),
				sellerRateInfo.getWeekMainRateOk());
		Label l18 = new Label(18+1, sheet.getRows(),
				sellerRateInfo.getWeekNotmainRateOk());
		
		Label l19 = new Label(19+1, sheet.getRows(),
				sellerRateInfo.getWeekSumRateNormal());
		Label l20 = new Label(20+1, sheet.getRows(),
				sellerRateInfo.getWeekMainRateNormal());
		Label l21 = new Label(21+1, sheet.getRows(),
				sellerRateInfo.getWeekNotmainRateNormal());
		
		Label l22 = new Label(22+1, sheet.getRows(),
				sellerRateInfo.getWeekSumRateBad());
		Label l23 = new Label(23+1, sheet.getRows(),
				sellerRateInfo.getWeekMainRateBad());
		Label l24 = new Label(24+1, sheet.getRows(),
				sellerRateInfo.getWeekNotmainRateBad());

		Label l25 = new Label(25+1, sheet.getRows(),
				sellerRateInfo.getMonthSumRateOk());
		Label l26 = new Label(26+1, sheet.getRows(),
				sellerRateInfo.getMonthMainRateOk());
		Label l27 = new Label(27+1, sheet.getRows(),
				sellerRateInfo.getMonthNotmainRateOk());
		Label l28 = new Label(28+1, sheet.getRows(),
				sellerRateInfo.getMonthSumRateNormal());
		Label l29 = new Label(29+1, sheet.getRows(),
				sellerRateInfo.getMonthMainRateNormal());
		Label l30 = new Label(30+1, sheet.getRows(),
				sellerRateInfo.getMonthNotmainRateNormal());
		Label l31 = new Label(31+1, sheet.getRows(),
				sellerRateInfo.getMonthSumRateBad());
		Label l32 = new Label(32+1, sheet.getRows(),
				sellerRateInfo.getMonthMainRateBad());
		Label l33 = new Label(33+1, sheet.getRows(),
				sellerRateInfo.getMonthNotmainRateBad());

		Label l34 = new Label(34+1, sheet.getRows(),
				sellerRateInfo.getHalfYearSumRateOk());
		Label l35 = new Label(35+1, sheet.getRows(),
				sellerRateInfo.getHalfYearMainRateOk());
		Label l36 = new Label(36+1, sheet.getRows(),
				sellerRateInfo.getHalfYearNotmainRateOk());
		Label l37 = new Label(37+1, sheet.getRows(),
				sellerRateInfo.getHalfYearSumRateNormal());
		Label l38 = new Label(38+1, sheet.getRows(),
				sellerRateInfo.getHalfYearMainRateNormal());
		Label l39 = new Label(39+1, sheet.getRows(),
				sellerRateInfo.getHalfYearNotmainRateNormal());
		Label l40 = new Label(40+1, sheet.getRows(),
				sellerRateInfo.getHalfYearSumRateBad());
		Label l41 = new Label(41+1, sheet.getRows(),
				sellerRateInfo.getHalfYearMainRateBad());
		Label l42 = new Label(42+1, sheet.getRows(),
				sellerRateInfo.getHalfYearNotmainRateBad());

		Label l43 = new Label(43+1, sheet.getRows(),
				sellerRateInfo.getBeforeHalfYearSumRateOk());
		Label l44 = new Label(44+1, sheet.getRows(),
				sellerRateInfo.getBeforeHalfYearSumRateNormal());
		Label l45 = new Label(45+1, sheet.getRows(),
				sellerRateInfo.getBeforeHalfYearSumRateBad());

		Label l46 = new Label(46+1, sheet.getRows(),
				sellerRateInfo.getSellerRate());
		Label l47 = new Label(47+1, sheet.getRows(),
				sellerRateInfo.getMainBusiness());
		Label l48 = new Label(48+1, sheet.getRows(),
				sellerRateInfo.getMainBusinessPercentage());
		Label l50 = new Label(50,sheet.getRows(),sellerRateInfo.getSellerRateHref());

		try {
			sheet.addCell(l0);
			sheet.addCell(l1);
			sheet.addCell(l2);
			sheet.addCell(l3);
			sheet.addCell(l4);
			sheet.addCell(l5);
			sheet.addCell(l6);
			sheet.addCell(l7);
			sheet.addCell(l8);
			sheet.addCell(l9);
			sheet.addCell(l10);
			sheet.addCell(l11);
			sheet.addCell(l12);
			sheet.addCell(l13);
			sheet.addCell(l14);
			sheet.addCell(l15);
			sheet.addCell(l16);
			sheet.addCell(l17);
			sheet.addCell(l18);
			sheet.addCell(l19);
			sheet.addCell(l20);
			sheet.addCell(l21);
			sheet.addCell(l22);
			sheet.addCell(l23);
			sheet.addCell(l24);
			sheet.addCell(l25);
			sheet.addCell(l26);
			sheet.addCell(l27);
			sheet.addCell(l28);
			sheet.addCell(l29);
			sheet.addCell(l30);
			sheet.addCell(l31);
			sheet.addCell(l32);
			sheet.addCell(l33);
			sheet.addCell(l34);
			sheet.addCell(l35);
			sheet.addCell(l36);
			sheet.addCell(l37);
			sheet.addCell(l38);
			sheet.addCell(l39);
			sheet.addCell(l40);
			sheet.addCell(l41);
			sheet.addCell(l42);
			sheet.addCell(l43);
			sheet.addCell(l44);
			sheet.addCell(l45);
			sheet.addCell(l46);
			sheet.addCell(l47);
			sheet.addCell(l48);
			sheet.addCell(l50);
			sheet.addCell(l90);
		} catch (RowsExceededException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 初始化表格属性
	 * 
	 * @param sheet
	 */
	public static void initialSheetSetting(WritableSheet sheet) {
		try {
			sheet.getSettings().setDefaultColumnWidth(30); // 设置列的默认宽度
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void writeItemBuyerSheet(BuyerInfo buyerInfo) {
		WritableSheet sheet = sheets.get(SheetNames.BUYER_INFO_SHEET);// 根据名称获取具体的sheet对象
		assert (sheet != null);
		// 将list 中的记录写入sheet中
//		for (int i = 0; i < buyerInfos.size(); ++i) {
//			BuyerInfo t = buyerInfos.get(i);
			Label l0 = new Label(0, sheet.getRows(), buyerInfo.getSellerId());
			Label l1 = new Label(1, sheet.getRows(), buyerInfo.getPrice() + "");
			Label l2 = new Label(2, sheet.getRows(), buyerInfo.getNum() + "");
			Label l3 = new Label(3, sheet.getRows(), buyerInfo.getPayTime());
			Label l4 = new Label(4, sheet.getRows(), buyerInfo.getSize());
			Label l5 = new Label(5, sheet.getRows(), buyerInfo.getRateScore() + "");
			Label l6 = new Label(6, sheet.getRows(), buyerInfo.getBuyerAddress() + "");
			Label l7 = new Label(7, sheet.getRows(), buyerInfo.getSex() + "");
			try {

				sheet.addCell(l0);
				sheet.addCell(l1);
				sheet.addCell(l2);
				sheet.addCell(l3);
				sheet.addCell(l4);
				sheet.addCell(l5);
				sheet.addCell(l6);
				sheet.addCell(l7);
			} catch (RowsExceededException e) {
				e.printStackTrace();
			} catch (WriteException e) {
				e.printStackTrace();
			}
//		}
	}
	
	public static void writeItemBuyerSheet(WritableSheet sheet,
			BuyerInfo buyerInfo) {
		log.info("Start to write to excel.");
		Label l0 = new Label(0, sheet.getRows(), buyerInfo.getSellerId());
		Label l1 = new Label(1, sheet.getRows(), buyerInfo.getPrice() + "");
		Label l2 = new Label(2, sheet.getRows(), buyerInfo.getNum() + "");
		Label l3 = new Label(3, sheet.getRows(), buyerInfo.getPayTime());
		Label l4 = new Label(4, sheet.getRows(), buyerInfo.getSize());
		Label l5 = new Label(5, sheet.getRows(), buyerInfo.getRateScore() + "");
		Label l6 = new Label(6, sheet.getRows(), buyerInfo.getBuyerAddress()
				+ "");
		Label l7 = new Label(7, sheet.getRows(), buyerInfo.getSex() + "");
		try {

			sheet.addCell(l0);
			sheet.addCell(l1);
			sheet.addCell(l2);
			sheet.addCell(l3);
			sheet.addCell(l4);
			sheet.addCell(l5);
			sheet.addCell(l6);
			sheet.addCell(l7);
		} catch (RowsExceededException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		}

		log.info("Complete to write to excel.");
	}
	
	
	
	public static void writeReviewsSheet(WritableSheet sheet,
			BuyerInfo buyerInfo) {

		int j = 0;
		Label l0 = new Label(j++, sheet.getRows(), buyerInfo.getSellerId());
		Label l1 = new Label(j++, sheet.getRows(), buyerInfo.getFeedDate() + "");
		Label l2 = new Label(j++, sheet.getRows(), buyerInfo.getIndicator()
				+ "");
		Label l5 = new Label(j++, sheet.getRows(), buyerInfo.getRateScore()
				+ "");
		Label l6 = new Label(j++, sheet.getRows(), buyerInfo.getBuyerAddress()
				+ "");
		Label l7 = new Label(j++, sheet.getRows(), buyerInfo.getSex() + "");
		try {

			sheet.addCell(l0);
			sheet.addCell(l1);
			sheet.addCell(l2);
			sheet.addCell(l5);
			sheet.addCell(l6);
			sheet.addCell(l7);
		} catch (RowsExceededException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		}
	}

	public static void writeReviewsSheet(BuyerInfo buyerInfo) {
		WritableSheet sheet = null;
		writeReviewsSheet(sheet, buyerInfo);
	}

	

	// write records into spreadsheet before close workbook
	public static void closeWorkbook() {
		try {
			workbook.write();
		} catch (IOException e) {
			log.error("Write workbook error and error message is:"
					+ e.getMessage());
		}
		try {
			workbook.close();
		} catch (WriteException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
