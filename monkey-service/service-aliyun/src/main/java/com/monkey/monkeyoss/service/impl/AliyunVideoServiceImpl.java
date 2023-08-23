package com.monkey.monkeyoss.service.impl;

import com.aliyun.tea.TeaException;
import com.aliyun.vod.upload.impl.UploadVideoImpl;
import com.aliyun.vod.upload.req.UploadStreamRequest;
import com.aliyun.vod.upload.resp.UploadStreamResponse;
import com.aliyun.vod20170321.models.DeleteVideoResponse;
import com.aliyun.vod20170321.models.DeleteVideoResponseBody;
import com.aliyun.vod20170321.models.GetPlayInfoResponse;
import com.monkey.monkeyUtils.exception.MonkeyBlogException;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyoss.service.AliyunVideoService;
import com.monkey.monkeyoss.utils.ConstantPropertiesUtlis;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * @author: wusihao
 * @date: 2023/8/15 17:11
 * @version: 1.0
 * @description:
 */
@Slf4j
@Service
public class AliyunVideoServiceImpl implements AliyunVideoService {
    /**
     * 上传视频到阿里云
     *
     * @param file 文件对象
     * @return {@link null}
     * @author wusihao
     * @date 2023/8/15 17:17
     */
    @Override
    public R uploadAliyunVideo(MultipartFile file) {
        try {

            // 得到文件原有的名字
            String fileName = file.getOriginalFilename();

            // 上传成功之后显示的名称
            String title = fileName.substring(0, fileName.lastIndexOf("."));
            // 得到该文件的输入流
            InputStream inputStream = file.getInputStream();
            UploadStreamRequest request = new UploadStreamRequest(ConstantPropertiesUtlis.KEY_ID,
                    ConstantPropertiesUtlis.KER_SECRET,
                    title,
                    fileName,
                    inputStream);
            /* 是否使用默认水印(可选)，指定模板组ID时，根据模板组配置确定是否使用默认水印*/
            //request.setShowWaterMark(true);
            /* 自定义消息回调设置，参数说明参考文档 https://help.aliyun.com/document_detail/86952.html#UserData */
            //request.setUserData(""{\"Extend\":{\"test\":\"www\",\"localId\":\"xxxx\"},\"MessageCallback\":{\"CallbackURL\":\"http://test.test.com\"}}"");
            /* 视频分类ID(可选) */
            //request.setCateId(0);
            /* 视频标签,多个用逗号分隔(可选) */
            //request.setTags("标签1,标签2");
            /* 视频描述(可选) */
            //request.setDescription("视频描述");
            /* 封面图片(可选) */
            //request.setCoverURL("http://cover.sample.com/sample.jpg");
            /* 模板组ID(可选) */
            //request.setTemplateGroupId("8c4792cbc8694e7084fd5330e56a33d");
            /* 工作流ID(可选) */
            //request.setWorkflowId("d4430d07361f0*be1339577859b0177b");
            /* 存储区域(可选) */
            //request.setStorageLocation("in-201703232118266-5sejdln9o.oss-cn-shanghai.aliyuncs.com");
            /* 开启默认上传进度回调 */
            // request.setPrintProgress(true);
            /* 设置自定义上传进度回调 (必须继承 VoDProgressListener) */
            // request.setProgressListener(new PutObjectProgressListener());
            /* 设置应用ID*/
            //request.setAppId("app-1000000");
            /* 点播服务接入点 */
            //request.setApiRegionId("cn-shanghai");
            /* ECS部署区域*/
            // request.setEcsRegionId("cn-shanghai");
            UploadVideoImpl uploader = new UploadVideoImpl();
            UploadStreamResponse response = uploader.uploadStream(request);
            System.out.print("RequestId=" + response.getRequestId() + "\n");  //请求视频点播服务的请求ID
            String videoId = null;
            if (response.isSuccess()) {
                videoId = response.getVideoId();
                System.out.print("VideoId=" + videoId + "\n");
            } else { //如果设置回调URL无效，不影响视频上传，可以返回VideoId同时会返回错误码。其他情况上传失败时，VideoId为空，此时需要根据返回错误码分析具体错误原因
                videoId = response.getVideoId();
                System.out.print("VideoId=" + videoId + "\n");
                System.out.print("ErrorCode=" + response.getCode() + "\n");
                System.out.print("ErrorMessage=" + response.getMessage() + "\n");
            }
            return R.ok(videoId);
        }catch (Exception e) {
            return null;
        }

    }

    /**
     * 使用AK&SK初始化账号Client
     * @return Client
     * @throws Exception
     */
    public static com.aliyun.vod20170321.Client createClient(){
        try {
            com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                    // 必填，您的 AccessKey ID
                    .setAccessKeyId(ConstantPropertiesUtlis.KEY_ID)
                    // 必填，您的 AccessKey Secret
                    .setAccessKeySecret(ConstantPropertiesUtlis.KER_SECRET);
            // Endpoint 请参考 https://api.aliyun.com/product/vod
            config.endpoint = ConstantPropertiesUtlis.VIDEO_END_POINT;
            return new com.aliyun.vod20170321.Client(config);
        } catch (Exception e) {
            e.printStackTrace();
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }


    }
    /**
     * 通过视频id获取视频基本信息
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/8/16 11:34
     */
    @Override
    public R getVideoPlayUrl(String videoSourceId) {
        // 请确保代码运行环境设置了环境变量 ALIBABA_CLOUD_ACCESS_KEY_ID 和 ALIBABA_CLOUD_ACCESS_KEY_SECRET。
        // 工程代码泄露可能会导致 AccessKey 泄露，并威胁账号下所有资源的安全性。以下代码示例使用环境变量获取 AccessKey 的方式进行调用，仅供参考，建议使用更安全的 STS 方式，更多鉴权访问方式请参见：https://help.aliyun.com/document_detail/378657.html
        com.aliyun.vod20170321.Client client = createClient();
        com.aliyun.vod20170321.models.GetPlayInfoRequest getPlayInfoRequest = new com.aliyun.vod20170321.models.GetPlayInfoRequest()
                .setVideoId(videoSourceId);
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
        try {
            // 复制代码运行请自行打印 API 的返回值
            GetPlayInfoResponse playInfoWithOptions = client.getPlayInfoWithOptions(getPlayInfoRequest, runtime);
            return R.ok(playInfoWithOptions.getBody().getPlayInfoList().getPlayInfo().get(0).getPlayURL());
        } catch (TeaException error) {
            // 如有需要，请打印 error
            com.aliyun.teautil.Common.assertAsString(error.message);
        } catch (Exception _error) {
            TeaException error = new TeaException(_error.getMessage(), _error);
            // 如有需要，请打印 error
            com.aliyun.teautil.Common.assertAsString(error.message);
        }
        return R.error();
    }

    /**
     * 删除视频播放地址
     *
     * @param videoSourceId 视频资源id
     * @return {@link null}
     * @author wusihao
     * @date 2023/8/17 15:22
     */
    @Override
    public R deleteVideoPlayByVideoSourceId(String videoSourceId) {
        // 请确保代码运行环境设置了环境变量 ALIBABA_CLOUD_ACCESS_KEY_ID 和 ALIBABA_CLOUD_ACCESS_KEY_SECRET。
        // 工程代码泄露可能会导致 AccessKey 泄露，并威胁账号下所有资源的安全性。以下代码示例使用环境变量获取 AccessKey 的方式进行调用，仅供参考，建议使用更安全的 STS 方式，更多鉴权访问方式请参见：https://help.aliyun.com/document_detail/378657.html
        com.aliyun.vod20170321.Client client = createClient();
        com.aliyun.vod20170321.models.DeleteVideoRequest deleteVideoRequest = new com.aliyun.vod20170321.models.DeleteVideoRequest()
                .setVideoIds(videoSourceId);
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
        try {
            // 复制代码运行请自行打印 API 的返回值
            DeleteVideoResponse deleteVideoResponse = client.deleteVideoWithOptions(deleteVideoRequest, runtime);
            DeleteVideoResponseBody body = deleteVideoResponse.getBody();
            return R.ok(body);
        } catch (TeaException error) {
            // 如有需要，请打印 error
            com.aliyun.teautil.Common.assertAsString(error.message);
        } catch (Exception _error) {
            TeaException error = new TeaException(_error.getMessage(), _error);
            // 如有需要，请打印 error
            com.aliyun.teautil.Common.assertAsString(error.message);
        }

        return R.error();
    }
}
