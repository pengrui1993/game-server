package test.wx;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.URLUtil;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.notify.WxPayRefundNotifyResult;
import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.github.binarywang.wxpay.bean.request.BaseWxPayRequest;
import com.github.binarywang.wxpay.bean.request.WxPayRefundRequest;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.bean.result.WxPayOrderQueryResult;
import com.github.binarywang.wxpay.bean.result.WxPayRefundResult;
import com.github.binarywang.wxpay.constant.WxPayConstants;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.google.gson.Gson;

import java.math.BigDecimal;
import java.util.*;

public class Pay {
    WxPayService wxPayService;
    @Seq(1)
    void createOrder(String wxOpenId,String money) throws WxPayException {
        Map<String,Object> resp = new HashMap<>();
        String payNo = "system-unique-pay-order-id-001";

        WxPayUnifiedOrderRequest request = new WxPayUnifiedOrderRequest();
        request.setOutTradeNo(payNo);
        request.setVersion("1.0");
        request.setTotalFee(BaseWxPayRequest.yuanToFen(money));
        request.setSpbillCreateIp("127.0.0.1");
        request.setTradeType(WxPayConstants.TradeType.JSAPI);
        request.setTimeExpire(LocalDateTimeUtil.format(LocalDateTimeUtil.now().plusMinutes(5)
                , DatePattern.PURE_DATETIME_PATTERN));
        request.setOpenid(wxOpenId);
        request.setDetail(detail("1",new BigDecimal(money)));
        request.setNotifyUrl(URLUtil.completeUrl("https://store.com", "/pay/callback"));
        Object order = wxPayService.createOrder(request);
        if(order instanceof WxPayMpOrderResult res){
            resp.put("info",res);
            resp.put("payNo",payNo);

        }
        System.out.println(resp);
    }
    @Seq(2)
    void queryOrder(String payNo,String transactionId) throws WxPayException {
        WxPayOrderQueryResult result = wxPayService.queryOrder(transactionId, payNo);
        switch (result.getResultCode()){
            case WxPayConstants.ResultCode.SUCCESS -> {}
            case WxPayConstants.ResultCode.FAIL -> {}
        }
    }
    String transactionId;
    @Seq(2)
    void orderNotify(String xml) throws WxPayException {
        final WxPayOrderNotifyResult result = wxPayService.parseOrderNotifyResult(xml);
        transactionId = result.getTransactionId();
        switch (result.getResultCode()){
            case WxPayConstants.ResultCode.SUCCESS -> {}
            case WxPayConstants.ResultCode.FAIL -> {}
        }
    }
    @Seq(3)
    void refundOrder(String payNo,String machId) throws WxPayException {
        WxPayRefundRequest request = new WxPayRefundRequest();
//        request.setOutTradeNo(payNo);
        request.setTransactionId(transactionId);
        request.setTotalFee(100);
        request.setRefundFee(100);
        request.setOpUserId(machId);
        request.setNotifyUrl(URLUtil.completeUrl("https://store.com", "/pay/refund/callback"));
        WxPayRefundResult result = wxPayService.refund(request);
        switch(result.getResultCode()){
            case WxPayConstants.ResultCode.SUCCESS -> {}
            case WxPayConstants.ResultCode.FAIL -> {}
        }

    }
    @Seq(4)
    void refundNotify(String xml) throws WxPayException {
        WxPayRefundNotifyResult result = wxPayService.parseRefundNotifyResult(xml);
        switch(result.getResultCode()){
            case WxPayConstants.ResultCode.SUCCESS -> {}
            case WxPayConstants.ResultCode.FAIL -> {}
        }
        WxPayRefundNotifyResult.ReqInfo req = result.getReqInfo();
        String tid = req.getTransactionId();
        String outTradeNo = req.getOutTradeNo();
        String outRefundNo = req.getOutRefundNo();
    }

    Gson gson = new Gson();
    private String detail(String goodsId, BigDecimal money) {
        Map<String, Object> detail = new HashMap<>();
        List<Map<String, Object>> goodsDetail = new ArrayList<>();
        detail.put("goods_detail", goodsDetail);
        Map<String, Object> goods = new HashMap<>();
        goods.put("goods_id", goodsId);
        goods.put("quantity", 1);
        goods.put("price", BaseWxPayRequest.yuan2Fen(money));
        goodsDetail.add(goods);
        return gson.toJson(detail);
    }
    private String randomOrder(){
        return UUID.randomUUID().toString();
    }
}
