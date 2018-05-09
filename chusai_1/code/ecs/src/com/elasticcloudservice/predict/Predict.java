package com.elasticcloudservice.predict;


import com.algorithm.main.MyMain;

public class Predict {

	public static String[] predictVm(String[] ecsContent, String[] inputContent) {

		/** =========do your work here========== **/

        MyMain mm = new MyMain();

		return mm.run(ecsContent, inputContent);
	}

}
