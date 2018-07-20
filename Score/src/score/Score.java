package score;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class Score{

	private static final int MAX_RANK = 18;

    private static final int[] CPU = {-1,1,1,1,2,2,2,4,4,4,8,8,8,16,16,16,32,32,32};

    private static final int[] MEM = {-1,1,2,4,2,4,8,4,8,16,8,16,32,16,32,64,32,64,128};

	private static final String res_folder_path = "I:\\nut\\code\\hwrj2018\\评分\\res_files";

	private static final String test_folder_path = "I:\\nut\\code\\hwrj2018\\评分\\test_files";

	private static int N = 0;

	private static String[] serverName;

	private static int[] serverCPU;

	private static int[] serverMEM;

	public static void main(String[] args) throws IOException {
		
		int debug = 1;

		File res_folder	= new File(res_folder_path);
		
		File[] res_files = res_folder.listFiles();
		
		Arrays.sort(res_files);

		System.out.println("");
		
		double[] avg = new double[5];
		for ( int i = 0; i < res_files.length; i++ ) {
			File res = res_files[i];
			
			String caseName = "case" + res.getName().split("\\.")[0];

			File test = new File(test_folder_path + "\\" + caseName + "\\test.txt");
			File input = new File(test_folder_path + "\\" + caseName + "\\input.txt");

			int[] res_vms = parseVms(res);
			int[] test_vms = parseVms(test);
			parseInput(input);
			List<List<int[]>> deploy = parseDeploy(res);

			double[] scores = helpScoring(N, res_vms, test_vms, deploy);
			avg[0] += scores[0];
			avg[1] += scores[1];
			avg[2] += scores[2];
			avg[3] += scores[3];
			avg[4] += scores[1]*scores[2];
			
			if ( debug == 1 ) {
				System.out.println("******************");
				System.out.println("评分文件：" + res.getName() + "\t" + test.getName());
				System.out.printf("预测得分：%.3f\n", scores[0]*100);
				System.out.printf("CPU部署得分：%.3f\n", scores[1]*100);
				System.out.printf("MEM部署得分：%.3f\n", scores[2]*100);
				System.out.printf("总部署得分：%.3f\n", scores[1]*scores[2]*100);
				System.out.printf("最后得分：%.3f\n", scores[3]*100);
			}
		}

		System.out.println("******************");
		System.out.println("测试用例数量：" + res_files.length + "个");
		System.out.printf("平均预测得分：%.3f\n", avg[0] / res_files.length *100);
		System.out.printf("平均CPU部署得分：%.3f\n", avg[1] / res_files.length *100);
		System.out.printf("平均MEM部署得分：%.3f\n", avg[2] / res_files.length *100);
		System.out.printf("平均总部署得分：%.3f\n", avg[4] / res_files.length *100);
		System.out.printf("最后得分：%.3f\n", avg[3] / res_files.length *100);
		System.out.println("");

	}
	
	private static int[] parseVms(File file) throws IOException {
		int[] vms = new int[MAX_RANK];
		N = 0;    // 顺便统计一下需要预测的规格数

		BufferedReader br = new BufferedReader(new FileReader(file));
		
		br.readLine();     // 跳过第一行
		String line = br.readLine();
		while ( line != null && !line.trim().equals("") ) {
			String[] split = line.trim().split(" ");
			int flavor = Integer.parseInt(split[0].substring(6));
			int count = Integer.parseInt(split[1]);
			vms[flavor-1] = count;

			line = br.readLine();
			N++;
		}

		br.close();

		return vms;
	}

	private static List<List<int[]>> parseDeploy(File file) throws IOException {
		List<List<int[]>> deploy = new ArrayList<>();

		BufferedReader br = new BufferedReader(new FileReader(file));
		
		br.readLine();     // 跳过第一行
		String line = br.readLine();
		while ( line != null && !line.trim().equals("") ) { // 跳过前面的VM
			line = br.readLine();
		}
		
		int serverType = 0;
		while ( serverType < serverName.length && line != null ){
			line = br.readLine();
			String[] split = line.trim().split(" ");
			String name = split[0];
			int count = Integer.parseInt(split[1]);
			while ( !serverName[serverType].equals(name) ) {
				deploy.add(new ArrayList<>());
				serverType++;
			}
			List<int[]> servers = new ArrayList<>();
			for ( int i = 0; i < count; i++ ) {
				line = br.readLine();
				split = line.trim().split(" ");
				int[] vms = new int[MAX_RANK];
				for ( int j = 1; j < split.length; j += 2 ) {
					int flavor = Integer.parseInt(split[j].substring(6));
					int num = Integer.parseInt(split[j+1]);
					vms[flavor-1] = num;
				}
				servers.add(vms);
			}
			deploy.add(servers);
			line = br.readLine();
			serverType++;
		}
		
		while ( serverType < serverName.length ) {
			deploy.add(new ArrayList<>());
			serverType++;
		}
		
		br.close();

		return deploy;
	}

	private static void parseInput(File file) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(file));
		
		int typeCount = Integer.parseInt(br.readLine().trim());
		serverName = new String[typeCount];
		serverCPU = new int[typeCount];
		serverMEM = new int[typeCount];

		for ( int i = 0; i < typeCount; i++ ) {
			String[] line = br.readLine().trim().split(" ");
			serverName[i] = line[0];
			serverCPU[i] = Integer.parseInt(line[1]);
			serverMEM[i] = Integer.parseInt(line[2]);
		}

		br.close();
	}



	private static double[] helpScoring(int N, int[] res_vms, int[] test_vms, List<List<int[]>> deploy) {
		
		double sum1 = 0.0, sum2 = 0.0, sum3 = 0.0;
		for ( int i = 0; i < res_vms.length; i++ ) {
			int diff = res_vms[i] - test_vms[i];
			sum1 += diff * diff;
			sum2 += res_vms[i] * res_vms[i];
			sum3 += test_vms[i] * test_vms[i];
		}
		double left = 1 - Math.sqrt(sum1 / N) / (Math.sqrt(sum2 / N) + Math.sqrt(sum3 / N));

		double cpu_server_all = 0.0;
		double mem_server_all = 0.0;
		for ( int serverType = 0; serverType < serverName.length; serverType++ ) {
			cpu_server_all += serverCPU[serverType] * deploy.get(serverType).size();
			mem_server_all += serverMEM[serverType] * deploy.get(serverType).size();
		}

		double cpu_used = 0.0, mem_used = 0.0;
		for ( int flavor = 1; flavor <= MAX_RANK; flavor++ ){
			cpu_used += CPU[flavor] * res_vms[flavor-1];
			mem_used += MEM[flavor] * res_vms[flavor-1];
		}
		double right1 = cpu_used / cpu_server_all;
		double right2 = mem_used / mem_server_all;

		double[] scores = new double[4];
		scores[0] = left;
		scores[1] = right1;
		scores[2] = right2;
		scores[3] = left * (right1 + right2) / 2;

		return scores;
	}

}
	
	
