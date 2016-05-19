package cn.com.shanghai.xinfusdk_w280p.usexor;

/**
 * you can you do.no can no bb.
 * 类说明: 支付通内部逻辑判断类
 * 包名：cn.com.xinfusdk.sdk
 * 作者：赵文贇
 * 创建时间:2016/3/16 17:19
 */
public enum TransType {
    Consume,        //交易类型-消费交易
    Reversal,       //交易类型-冲正
    UnConsume,      //交易类型-撤销交易
    GetBalanc,      //交易类型-查余交易
    UnKnow,

    Consume_Shua,       //刷卡消费
    Consume_Cha,       //插卡消费

    UnConsume_Shua,       //刷卡撤销
    UnConsume_Cha,       //插卡撤销

    GetBalance_Shua,       //刷卡查余
    GetBalance_Cha,      //插卡查余


    ETC_GetCardPsw,//ETC卡片余额获取，上送报文时需要计算mac，用到的分类常量
}
