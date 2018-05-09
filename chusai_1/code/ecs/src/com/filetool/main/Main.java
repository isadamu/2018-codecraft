package com.filetool.main;

import com.elasticcloudservice.predict.Predict;
import com.filetool.util.FileUtil;
import com.filetool.util.LogUtil;

/**
 * 
 * 工具入口
 * 
 * @version [版本号, 2017-12-8]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class Main {
	public static void main(String[] args) {

		// 纯粹用于测试计数
//		TestFileCount tfc = new TestFileCount();
//		tfc.count("F:\\nut\\code\\hwrj2018\\data\\自己制造\\new\\case5\\test.txt");

		// 本地测试使用 - 笔记本
		for ( int iii = 1; iii <= 8; iii++ ) {
			int casenum = iii;
			System.out.println("case" + casenum);
			String ecsDataPath = "I:\\nut\\code\\hwrj2018\\input\\new\\case" + casenum + "\\train.txt";
			String inputFilePath = "I:\\nut\\code\\hwrj2018\\input\\new\\case" + casenum + "\\input.txt";
			String resultFilePath = "I:\\nut\\code\\hwrj2018\\评分\\res_files\\" + casenum + ".txt";

		// 本地测试使用 - 台式
//		for ( int iii = 1; iii <= 10; iii++ ) {
//			int casenum = iii;
//			System.out.println(iii);
//			String ecsDataPath = "F:\\nut\\code\\hwrj2018\\input\\case" + casenum + "\\train.txt";
//			String inputFilePath = "F:\\nut\\code\\hwrj2018\\input\\case" + casenum + "\\input.txt";
//			String resultFilePath = "F:\\nut\\code\\hwrj2018\\评分\\res_files\\" + casenum + ".txt";

		/* 上传时需要删除或注释上面的代码，并解开这部分代码
		if (args.length != 3) {
			System.err
					.println("please input args: ecsDataPath, inputFilePath, resultFilePath");
			return;
		}

		String ecsDataPath = args[0];
		String inputFilePath = args[1];
		String resultFilePath = args[2];
		*/

			LogUtil.printLog("Begin");

			// 读取输入文件
			String[] ecsContent = FileUtil.read(ecsDataPath, null);
			String[] inputContent = FileUtil.read(inputFilePath, null);

			// 功能实现入口
			String[] resultContents = Predict.predictVm(ecsContent, inputContent);

			// 写入输出文件
			if (hasResults(resultContents)) {
				FileUtil.write(resultFilePath, resultContents, false);
			} else {
				FileUtil.write(resultFilePath, new String[]{"NA"}, false);
			}
			LogUtil.printLog("End");
		}
	}

	private static boolean hasResults(String[] resultContents) {
		if (resultContents == null) {
			return false;
		}
		for (String contents : resultContents) {
			if (contents != null && !contents.trim().isEmpty()) {
				return true;
			}
		}
		return false;
	}

}
