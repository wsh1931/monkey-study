package com.monkey.monkeyoss.service.impl;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.monkey.monkeyUtils.result.ResultStatus;
import com.monkey.monkeyUtils.result.ResultVO;
import com.monkey.monkeyoss.service.OssService;
import com.monkey.monkeyoss.utils.ConstantPropertiesUtlis;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.print.DocFlavor;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

@Service
public class OssServiceImpl implements OssService {
    // 上传文件到阿里云oss, 并返回阿里云图片存储
    @Override
    public String uploadFile(MultipartFile file, String module) {
        // Endpoint以华东1（杭州）为例，其它Region请按实际情况填写。
        String endpoint = ConstantPropertiesUtlis.END_POINT;
        // 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
        String accessKeyId = ConstantPropertiesUtlis.KEY_ID;
        String accessKeySecret = ConstantPropertiesUtlis.KER_SECRET;
        // 填写Bucket名称，例如examplebucket。
        String bucketName = ConstantPropertiesUtlis.BUCKET_NAME;
        // 填写Object完整路径，完整路径中不能包含Bucket名称，例如exampledir/exampleobject.txt。
        String objectName = "exampledir/exampleobject.txt";
        // 填写本地文件的完整路径，例如D:\\localpath\\examplefile.txt。
        // 如果未指定本地路径，则默认从示例程序所属项目对应本地路径中上传文件流。
        String filePath= "D:\\localpath\\examplefile.txt";

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            // 获取上传文件的输入流
            InputStream inputStream = file.getInputStream();
            // 获取文件名称
            String filename = file.getOriginalFilename();
            String name = file.getName();
            // 保证文件名不重复
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            // 1: 通过 uuid生成随机值
            filename = uuid +  filename;
            // 2: 通过日期生成路径
            // 获取当前日期
            String dataPath = new DateTime().toString("yyyy/MM/dd");
            filename = module + dataPath + "/" + filename;
            // 创建PutObjectRequest对象。
            /*
            * 第一个参数： Bucket名称
            * 第二个参数：上传到OSS文件的路径或文件名称
            * 第三个参数：上传文件的输入流
            * */
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, filename, inputStream);
            // 创建PutObject请求。
            PutObjectResult result = ossClient.putObject(putObjectRequest);

            // 返回上传到阿里云OSS文件的路径
//            https://monkey-blog.oss-cn-beijing.aliyuncs.com/article/relax.jpg
            String fileUrl = " https://" + bucketName + "." + endpoint + "/" + filename;
            return fileUrl;
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }

        return null;
    }

    // 删除阿里云文件
    @Override
    public ResultVO removeFile(String fileUrl) {
        // Endpoint以华东1（杭州）为例，其它Region请按实际情况填写。
        String endpoint = ConstantPropertiesUtlis.END_POINT;
        // 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
        String accessKeyId = ConstantPropertiesUtlis.KEY_ID;
        String accessKeySecret = ConstantPropertiesUtlis.KER_SECRET;
        // 填写Bucket名称，例如examplebucket。
        String bucketName = ConstantPropertiesUtlis.BUCKET_NAME;
        // 填写Object完整路径，例如exampledir/exampleobject.txt。Object完整路径中不能包含Bucket名称。
        String objectName = fileUrl;
        try {
            // 原图片地址https://monkey-blog.oss-cn-beijing.aliyuncs.com/articlePicture/2023/05/29/c873d24883b44e6ab47b22eb92eaef0d04.png
            // 需要图片地址articlePicture/2023/05/29/02bb26a36e004b19b7a57f5a348f312e03.jpg
            objectName = new URL(objectName).getPath().substring(1);
            objectName = objectName.substring(1);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            // 删除文件。
            ossClient.deleteObject(bucketName, objectName);
            return new ResultVO(ResultStatus.OK, null, null);
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
            return new ResultVO(ResultStatus.NO, null, null);
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
            return new ResultVO(ResultStatus.NO, null, null);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
}
