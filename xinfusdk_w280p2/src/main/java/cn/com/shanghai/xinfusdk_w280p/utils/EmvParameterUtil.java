package cn.com.shanghai.xinfusdk_w280p.utils;

import android.content.Context;

import com.landicorp.android.finance.transaction.util.TLVDataList;
import com.landicorp.emv.EmvAidParam;
import com.landicorp.emv.EmvCapk;
import com.landicorp.emv.PropConfiguration;
import com.landicorp.util.DataUtil;
import com.landicorp.util.LogS;

public class EmvParameterUtil {

	private static final String TAG = "xj";
	private static EmvParameterUtil instance;
	private static Context ctx;
	private static final int WRITE = 2;
	private static int aidIndex = 0;
	private static int capkIndex = 0;
	private static int aidCounts = 10;
	private static int capkCounts = 20;

	public static int getAidCounts() {
		return aidCounts;
	}

	public static int getCapkCounts() {
		return capkCounts;
	}

	public static EmvParameterUtil getInstance() {
		if (instance == null) {
			instance = new EmvParameterUtil();
		}
		return instance;
	}

	public void initEmvParameter(Context context, String mrchNm, String mrchNo,
			String termId) {
		EmvParameterUtil.ctx = context;
		DataUtil.initSP(context);
		DataUtil.setValue("mrchNm", mrchNm);
		DataUtil.setValue("mrchNo", mrchNo);
		DataUtil.setValue("termId", termId);
	}

	// 初始化AID参数
	public boolean initICParam(String[] aidList) {
		aidCounts = aidList.length;
		for (int i = 0; i < aidList.length; i++) {
			TLVDataList tlvDataList = TLVDataList.fromBinary(aidList[i]
					.replaceAll(" ", ""));
			EmvAidParam emvAidParam = setAidParameter(tlvDataList);
			setAidParamterTofile(ctx, emvAidParam);
		}
		DataUtil.setValue("isSaveICParam", "true");
		return true;
	}

	// 设置AID参数
	private EmvAidParam setAidParameter(TLVDataList tlvDataList) {
		EmvAidParam emvAidParam = new EmvAidParam();
		// AID,9F06 终端支持的借/贷记应用列表，如ISO/IEC 7816-5所述，指明应用
		emvAidParam.setAID(tlvDataList.getTLV("9F06").getValue());
		// 应用选择指示符ASI,DF01
		// 指示应用选择时终端上的AID与卡片中的AID是完全匹配（长度和内容都必须一样），还是部分匹配（卡片AID的前面部分与终端AID相同，长度可以更长）。终端支持的应用列表中的每个AID仅有一个应用选择指示符。
		emvAidParam.setSelFlag(Integer.parseInt(tlvDataList.getTLV("DF01")
				.getValue()));

		// 应用版本号,9F09 支付系统给应用分配的版本号
		// emvAidParam.setVersion(tlvDataList.getTLV("9F09")
		// .getValue());

		// TAC－缺省,DF11 标识如果交易可以联机完成但终端没有联机交易能力时，拒绝交易的收单行条件
		emvAidParam.setTacDefualt(tlvDataList.getTLV("DF11").getValue());

		// TAC－联机,DF12 标识联机交易的收单行条件
		emvAidParam.setTacOnline(tlvDataList.getTLV("DF12").getValue());

		// TAC－拒绝，DF13 标识不作联机尝试即拒绝交易的收单行条件
		emvAidParam.setTacDenial(tlvDataList.getTLV("DF13").getValue());

		// 终端最低限额，9F1B IC卡消费时终端允许的最低脱机限额
		// emvAidParam.setFloorLimit(Long.parseLong(tlvDataList.getTLV("9F1B")
		// .getValue()));

		// 终端最低限额，9F1B IC卡消费时终端允许的最低脱机限额
		// 酷银此处下传的是16进制字符串
		emvAidParam.setFloorLimit(Long.parseLong(tlvDataList.getTLV("9F1B")
				.getValue(), 16));

		// 偏置随机选择的阈值，DF15 在终端风险管理中用于随机交易选择的值
		emvAidParam.setThreshold(Long.parseLong(tlvDataList.getTLV("DF15")
				.getValue()));

		// 偏置随机选择的最大目标百分数，DF16 用于偏置随机选择的最大目标百分数
		emvAidParam.setMaxTargetPer(Integer.parseInt(tlvDataList.getTLV("DF16")
				.getValue()));

		// 随机选择的目标百分数， DF17 用于随机选择的目标百分数
		emvAidParam.setTargetPer(Integer.parseInt(tlvDataList.getTLV("DF17")
				.getValue()));

		// 缺省DDOL，DF14 卡片中无DDOL时用于构造内部认证命令的DDOL
		emvAidParam.setdDOL(tlvDataList.getTLV("DF14").getValue());

		// 终端联机PIN支持能力，DF18 指示终端在每个AID的要求下是否支持联机PIN的输入。
		emvAidParam.setOnlinePin(Integer.parseInt(tlvDataList.getTLV("DF18")
				.getValue(), 16));

		// 终端电子现金交易限额，9F7B
		// 终端使用此数据元（如果存在的话）判断一个交易的处理方式，当授权金额小于该限额时允许电子现金交易，否则设置终端行为代码并根据判断确认交易方式（小额支付参数）。
		emvAidParam.setECTTLFlg(Integer.parseInt(tlvDataList.getTLV("9F7B")
				.getValue(), 16));

		// 非接触读写器脱机最低限额，DF19 在AID联合中，用来指示读写器中非接触交易的最低限额
		emvAidParam.setRdClssFLmt(Long.parseLong(tlvDataList.getTLV("DF19")
				.getValue()));

		// 非接触读写器交易限额，DF20 如果非接触交易的金额大于或等于此数值，则交易终止。允许在其他界面尝试此交易
		emvAidParam.setRdClssTxnLmt(Long.parseLong(tlvDataList.getTLV("DF20")
				.getValue()));

		// 读写器持卡人验证方法（CVM）所需限制，DF21
		// 如果非接触交易超过此值，读写器要求一个持卡人验证方法（CVM）。
		emvAidParam.setRdCVMLmt(Long.parseLong(tlvDataList.getTLV("DF21")
				.getValue()));

		return emvAidParam;
	}

	// 设置AID参数到文件
	private boolean setAidParamterTofile(Context context, EmvAidParam aidParam) {
		LogS.e(TAG, "setAidParam");

		if (aidParam == null) {
			LogS.e(TAG, "set aidParam fail！");
			return false;
		}
		if (aidIndex >= aidCounts)
			aidIndex = 0;
		PropConfiguration prop = new PropConfiguration(context, "/params/",
				"aid" + aidIndex + ".prop", WRITE);

		if (aidParam.getAID() != null) {
			prop.setValue("aid", aidParam.getAID());
		}

		prop.setValue("SelFlag", aidParam.getSelFlag() + "");

		if (aidParam.getVersion() != null) {
			prop.setValue("Version", aidParam.getVersion());
		}

		if (aidParam.getTacDenial() != null) {
			prop.setValue("TacDenial", aidParam.getTacDenial());
		}
		if (aidParam.getTacDefualt() != null) {
			prop.setValue("TacDefualt", aidParam.getTacDefualt());
		}
		if (aidParam.getTacOnline() != null) {
			prop.setValue("TacOnline", aidParam.getTacOnline());
		}
		prop.setValue("FloorLimit", aidParam.getFloorLimit() + "");
		prop.setValue("Threshold", aidParam.getThreshold() + "");
		prop.setValue("MaxTargetPer", aidParam.getMaxTargetPer() + "");
		prop.setValue("TargetPer", aidParam.getTargetPer() + "");
		if (aidParam.getdDOL() != null) {
			prop.setValue("dDOL", aidParam.getdDOL());
		}
		prop.setValue("OnlinePin", aidParam.getOnlinePin() + "");
		prop.setValue("ECTTLFlg", aidParam.getECTTLFlg() + "");
		prop.setValue("RdClssFLmt", aidParam.getRdClssFLmt() + "");
		prop.setValue("RdClssTxnLmt", aidParam.getRdClssTxnLmt() + "");
		prop.setValue("RdCVMLmt", aidParam.getRdCVMLmt() + "");

		prop.saveFile("saveAidParam");
		aidIndex++;

		return true;
	}

	// 初始化公钥参数
	public boolean initICPublicKey(String[] icPublicKey) {
		capkCounts = icPublicKey.length;
		for (int i = 0; i < icPublicKey.length; i++) {
			TLVDataList tlvDataList = TLVDataList.fromBinary(icPublicKey[i]
					.replaceAll(" ", ""));
			EmvCapk emvCapk = setICPublicKey(tlvDataList);
			setEmvCapkToFile(ctx, emvCapk);
		}
		DataUtil.setValue("isSaveICPublicKey", "true");
		return true;
	}

	// 设置公钥参数到文件
	private boolean setEmvCapkToFile(Context context, EmvCapk emvCapk) {
		if (emvCapk == null) {
			return false;
		}
		if (capkIndex >= capkCounts) {
			capkIndex = 0;
		}
		PropConfiguration prop = new PropConfiguration(context, "/params/",
				"capk" + capkIndex + ".prop", WRITE);

		prop.setValue("rid", emvCapk.getRID());
		prop.setValue("KeyID", emvCapk.getKeyID() + "");
		prop.setValue("Modul", emvCapk.getModul());
		prop.setValue("Exponent", emvCapk.getExponent());
		prop.setValue("CheckSum", emvCapk.getCheckSum());
		prop.setValue("ExpDate", emvCapk.getExpDate());
		prop.setValue("HashInd", emvCapk.getHashInd() + "");
		prop.setValue("ArithInd", emvCapk.getArithInd() + "");
		prop.saveFile("saveemvCapk");
		capkIndex++;
		return true;
	}

	// 设置公钥参数
	private EmvCapk setICPublicKey(TLVDataList tlvDataList) {
		EmvCapk emvCapk = new EmvCapk();
		// 9F06,应用提供商标识
		emvCapk.setRID(tlvDataList.getTLV("9F06").getValue());
		// 9F22，公钥索引
		emvCapk.setKeyID(Integer.parseInt(
				tlvDataList.getTLV("9F22").getValue(), 16));
		// DF05,认证中心公钥有效期
		emvCapk.setExpDate(tlvDataList.getTLV("DF05").getValue());
		// DF06 认证中心公钥哈什算法标识
		emvCapk.setHashInd(Integer.parseInt(tlvDataList.getTLV("DF06")
				.getValue(), 16));
		// DF07,认证中心公钥算法标识
		emvCapk.setArithInd(Integer.parseInt(tlvDataList.getTLV("DF07")
				.getValue(), 16));
		// DF02,认证中心公钥模
		emvCapk.setModul(tlvDataList.getTLV("DF02").getValue());
		// DF04,认证中心公钥指数
		emvCapk.setExponent(tlvDataList.getTLV("DF04").getValue());
		// DF03,认证中心公钥校验值
		emvCapk.setCheckSum(tlvDataList.getTLV("DF03").getValue());

		return emvCapk;
	}

}
