package org.zongf.tools.baidu.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zongf.tools.baidu.beans.ocr.AccessTokenResponse;
import org.zongf.tools.baidu.beans.ocr.OcrResponse;
import org.zongf.tools.baidu.exception.OcrException;

import java.net.URLEncoder;
import java.util.Base64;

/**
 * @author: zongf
 * @created: 2019-10-24
 * @since 1.0
 */
public class BaiduOcrUtil {

    private static Logger log = LoggerFactory.getLogger(BaiduOcrUtil.class);


    // 获取token 地址
    private static final String URL_REQUEST_TOKEN = "https://aip.baidubce.com/oauth/2.0/token";

    // 不含位置信息地址
    private static final String URL_BASIC_OCR = "https://aip.baidubce.com/rest/2.0/ocr/v1/general_basic";

    // 含位置信息地址
    private static final String URL_LOCATION_OCR = "https://aip.baidubce.com/rest/2.0/ocr/v1/accurate";



	public BaiduOcrUtil() {
        super();
    }

    public String toString() {
		return getClass().getSimpleName() + "@" + hashCode()  + "}";
	}



    /** 获取请求token
     * @return AccessTokenResponse
     * @since 1.0
     * @author zongf
     * @created 2019-10-25
     */
    public static AccessTokenResponse requestToken(String ak, String sk){
        // 拼接请求参数
        StringBuffer sb = new StringBuffer();
        sb.append("grant_type=client_credentials")
            .append("&client_id=").append(ak)
            .append("&client_secret=").append(sk);

        // 发送请求
        String result = BaiduOcrUtil.httpPost(URL_REQUEST_TOKEN, sb.toString());

        return JSONObject.parseObject(result, AccessTokenResponse.class);
    }

    /** 提取图片中的文字, 不包含文字位置信息
     * @param bytes 图片路径
     * @return OcrRunnable
     * @since 1.0
     * @author zongf
     * @created 2019-10-25
     */
    public static OcrResponse doBasicOcr(String ak, String sk, byte[] bytes){
        return doOcr(ak, sk, bytes, URL_BASIC_OCR);
    }

    /** 提取图片中的文字, 包含文字位置信息
     * @param bytes 图片路径
     * @return OcrRunnable
     * @since 1.0
     * @author zongf
     * @created 2019-10-25
     */
    public static OcrResponse doLocationOcr(String ak, String sk, byte[] bytes) {
        return doOcr(ak, sk, bytes, URL_LOCATION_OCR);
    }


    /** 图片进行ocr 文件转换
     * @param url ocr连接地址, 不同精度的ocr, 链接地址不同
     * @return OcrRunnable
     * @since 1.0
     * @author zongf
     * @created 2019-10-25
     */
    private static OcrResponse doOcr(String ak, String sk, byte[] bytes, String url){

        try{

            // 获取token
            AccessTokenResponse accessTokenResponse = requestToken(ak,sk);

            // 拼接请求地址
            String ocrUrl = url + "?access_token=" + accessTokenResponse.getAccess_token();

            // 获取图片Base64 字节流
            String image = URLEncoder.encode(BaiduOcrUtil.getBase64(bytes), "UTF-8");

            // 拼接参数
            StringBuffer sb = new StringBuffer();
            sb.append("image=" + image);
            sb.append("&language_type=CHN_ENG");

            // 调用请求
            String result = BaiduOcrUtil.httpPost(ocrUrl, sb.toString());

            log.info("图片orc解析完成, 结果:{}", result);

            return JSONObject.parseObject(result, OcrResponse.class);
        } catch (Exception e) {
            throw new OcrException("调用ocr 服务异常", e);
        }
    }


    /** 发送post 请求
     * @param url 请求地址
     * @param params 请求参数
     * @return String
     * @since 1.0
     * @author zongf
     * @created 2019-10-25
     */
    private static String httpPost(String url, String params) {
        try {

            CloseableHttpClient httpClient = HttpClients.createDefault();

            HttpPost httpPost = new HttpPost(url);

            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");

            StringEntity stringEntity = new StringEntity(params);

            httpPost.setEntity(stringEntity);

            CloseableHttpResponse response = httpClient.execute(httpPost);

            return EntityUtils.toString(response.getEntity());
        } catch (Exception ex) {
            throw new OcrException("调用百度服务异常!", ex);
        }
    }

    /** 获取文件字节流, 进行Base64 编码
     * @param bytes 文件字节数组
     * @return String 文件的Base64 编码字符串
     * @since 1.0
     * @author zongf
     * @created 2019-10-25
     */
    private static String getBase64(byte[] bytes) {
        byte[] base64 = Base64.getEncoder().encode(bytes);
        return new String(base64);
    }
}
