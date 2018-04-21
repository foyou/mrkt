package com.mrkt.utils;

import java.io.ByteArrayInputStream;
import java.util.UUID;

import org.springframework.util.Base64Utils;

import com.mrkt.config.CommonConfig;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;

/**
 * @ClassName	UploadUtil
 * @Description 上传工具
 * @author		hdonghong
 * @version 	v1.0
 * @since		2018/04/21 10:57:18
 */
public class UploadUtil {

	public static String uploadImage(String base64Str, CommonConfig commonConfig) throws QiniuException {
		String [] base64Arr = base64Str.split("base64,");
		if (!(base64Arr != null && base64Arr.length == 2)) {
			return null;
		}
//		String dataPrix = base64Arr[0];
		String data = base64Arr[1];
		byte[] b = Base64Utils.decodeFromString(data);
		
		// 构造一个带Zone对象的配置类
		Configuration cfg = new Configuration(Zone.zone2());
		UploadManager uploadManager = new UploadManager(cfg);
		// 上传的凭证参数
		String accessKey = commonConfig.getAccessKey();
		String secretKey = commonConfig.getSecretKey();
		String bucket = commonConfig.getBucket();
		
		// 默认不指定key的情况下，以文件内容的hash值作为文件名
		ByteArrayInputStream inputStream = new ByteArrayInputStream(b);
		Auth auth = Auth.create(accessKey, secretKey);
		String upToken = auth.uploadToken(bucket);
		
		// 设置保存在bucket上的图片文件名
		String imageName = UUID.randomUUID().toString();
		Response response = uploadManager.put(inputStream, imageName, upToken, null, null);
		// 解析上传结果
//		DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
//		System.out.println(putRet.key);
//      System.out.println(putRet.hash);
		
		return commonConfig.getImageUrl() + imageName;
		
	}
}
