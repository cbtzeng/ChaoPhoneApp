package com.example.chaophoneapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

public class Instructions extends Activity {

	private ServerSocket serverSocket;

	Handler updateConversationHandler;

	Thread serverThread = null;

	private TextView textThis;
	private TextView textNext;
	private TextView textTimer;

	public static final int SERVERPORT = 60000;
	
	public InetAddress address;
	
	public static final HashMap<String, String> steps;
	static{
		HashMap<String,String> tMap = new HashMap<String,String>();
		//A1
		tMap.put("A1-1","最大轉速穏定運轉測試時散熱風扇必須有70 %以上時間維持運轉(油門到底)");
		
		//A1 Loader Excavator(Loader)
		tMap.put("A1-loex1-2","最大轉速穏定運轉測試時散熱風扇必須有70 %以上時間維持運轉(油門到底)");
		
		//A2 Excavator
		tMap.put("A2-ex1","最大挖掘半徑75%距地面0.5公尺高處，切刃背面與地面呈60度");
		tMap.put("A2-ex2","高速怠轉");
		tMap.put("A2-ex3","抓斗移動至操作範圍50%，並保持距地面0.5公尺之高度");
		tMap.put("A2-ex4","抓斗最大伸展高度之30%");
		tMap.put("A2-ex5","吊桿向左方向旋轉90度");
		tMap.put("A2-ex6","最大伸展高度之60%停");
		tMap.put("A2-ex7","伸展至75%並使抓斗之切刃呈垂直時傾卸(外推黃錐卸土)");
		tMap.put("A2-ex8","向右迴轉90度");
		tMap.put("A2-ex9","回致原先之位置(下降回紅錐)");
		
		//A2 Loader Excavator(Excavator)
		tMap.put("A2-loex1","最大挖掘半徑75%距地面0.5公尺高處，切刃背面與地面呈60度");
		tMap.put("A2-loex2","高速怠轉");
		tMap.put("A2-loex3","抓斗移動至操作範圍50%，並保持距地面0.5公尺之高度");
		tMap.put("A2-loex4","抓斗最大伸展高度之30%");
		tMap.put("A2-loex5","吊桿向左方向旋轉45度");
		tMap.put("A2-loex6","最大伸展高度之60%停");
		tMap.put("A2-loex7","伸展至75%並使抓斗之切刃呈垂直時傾卸(外推黃錐卸土)");
		tMap.put("A2-loex8","向右迴轉45度");
		tMap.put("A2-loex9","回致原先之位置(下降回紅錐)");
		

		//A2 Loader
		tMap.put("A2-Lo1","高速怠轉(油門到底)");
		tMap.put("A2-Lo2","抓斗上舉制最高舉程之75%處(舉高X米)");
		tMap.put("A2-Lo3","再回復至原始位置(放下)");
		
		//A2 Loader Excavator(Loader)
		tMap.put("A2-loex1-2","高速怠轉(油門到底)");
		tMap.put("A2-loex2-2","抓斗上舉制最高舉程之75%處(舉高X米)");
		tMap.put("A2-loex3-2","再回復至原始位置(放下)");
		
		//A3 Loader
		tMap.put("A3-Lo1","抓斗底距地面0.3公尺±0.05公尺(起點紅錐斗高1尺)");
		tMap.put("A3-Lo2","引擎高速怠轉(高速怠轉)");
		tMap.put("A3-Lo3", "履帶式前進速度儘量接近且不超過4公里/小時，膠輪式前進速度儘量接近且不超過8公里/小時(低檔前進白錐)");
		tMap.put("A3-Lo4", "後退行走與速度無關，可使用適宜之變速檔。(後退回紅錐)");
		
		//A3 Loader Excavator(Loader)
		tMap.put("A3-loex1-2","抓斗底距地面0.3公尺±0.05公尺(起點紅錐斗高1尺)");
		tMap.put("A3-loex2-2","引擎高速怠轉(高速怠轉)");
		tMap.put("A3-loex3-2", "履帶式前進速度儘量接近且不超過4公里/小時，膠輪式前進速度儘量接近且不超過8公里/小時(低檔前進白錐)");
		tMap.put("A3-loex4-2", "後退行走與速度無關，可使用適宜之變速檔。(後退回紅錐)");
		
		//A3 Tractor
		tMap.put("A3-Tr1", "排土板為標準裝置，距地面高0.3公尺±0.05公尺(起點紅錐板高1尺)");
		tMap.put("A3-Tr2", "高速怠轉(油門到底)");
		tMap.put("A3-Tr3", "膠輪式於堅硬反射面行走，前進速度儘量接近且不超過每小時8公里，履帶式及鋼輪式於砂土上行走，前進速度儘量接近且不超過每小時4公里(前進至白錐)(停)");
		tMap.put("A3-Tr4", "後退速度則視情況使用變速檔。(後退回紅錐)");
		//A4
		tMap.put("A4-Asp_Fin","定置高速怠轉狀態。(全速空轉)");
		tMap.put("A4-Aug_Dri_Dri","定置高速怠轉。(全速空轉)");
		tMap.put("A4-Com","原則上定數回轉與定額負載之狀態。(定速定載)");
		tMap.put("A4-Con_Bre","規定之工作壓力為測定作業狀態，鑿桿強烈押在控制板，避免組裝部分影響測值。作業者不可站在噪音測線上");
		tMap.put("A4-Con_Cut","以定速回轉切割混凝土，深度為刀片直徑之1/4");
		tMap.put("A4-Con_Pum", "最大之運轉狀態壓送混凝土，此時吊桿應向水平方向延伸，配管長度約為10公尺(最大之運轉狀態壓送混凝土)");
		tMap.put("A4-Cra", "吊桿之角度為60度，勾子抓斗等以上卷狀態，定置高速怠轉。(全速空轉)");
		tMap.put("A4-Gen", "無負載定速回轉(60Hz)。(無負載定速回轉)");
		tMap.put("A4-Rol","可裝載道碴之機具，以裝載最大量之狀態，定置高速怠轉。(滿載全速空轉)");
		tMap.put("A4-Vib_Ham", "吊在空中之狀態，不超過地面上50公分為原則並使其產生最大振動數");
		
		//Tractor steps
		tMap.put("Tr", "A1-1,A3-Tr1,A3-Tr2,A3-Tr3,A3-Tr4");
		//Loader steps
		tMap.put("Lo","A1-1,A2-Lo1,A2-Lo2,A2-Lo3,A3-Lo1,A3-Lo2,A3-Lo3,A3-Lo4");
		//Excavator
		tMap.put("ex","A1-1,A2-ex1,A2-ex2,A2-ex3,A2-ex4,A2-ex5,A2-ex6,A2-ex7,A2-ex8,A2-ex9");
		//Loader Excavator
		tMap.put("loex", "A1-1,A2-loex1,A2-loex2,A2-loex3,A2-loex4,A2-loex5,A2-loex6,A2-loex7,A2-loex8,A2-loex9,A1-loex1-2,A2-loex1-2,A2-loex2-2,A2-loex3-2,A3-loex1-2,A3-loex2-2,A3-loex3-2,A3-loex4-2");
		
		steps=tMap;
	}

	
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_instructions);

		textThis = (TextView) findViewById(R.id.text1);
		textThis.setTextColor(Color.RED);
		textNext = (TextView) findViewById(R.id.text2);
		textTimer = (TextView) findViewById(R.id.textTimer);
		textTimer.setTextColor(Color.RED);
		
		updateConversationHandler = new Handler();

		this.serverThread = new Thread(new ServerThread());
		this.serverThread.start();

	}

	@Override
	protected void onStop() {
		super.onStop();
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	class ServerThread implements Runnable {

		public void run() {
			Socket socket = null;
			try {
				serverSocket = new ServerSocket(SERVERPORT);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			while (!Thread.currentThread().isInterrupted()) {

				try {

					socket = serverSocket.accept();

					CommunicationThread commThread = new CommunicationThread(socket);
					new Thread(commThread).start();

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	class CommunicationThread implements Runnable {

		private Socket clientSocket;

		private BufferedReader br;
		
		private String[] steps;
		
		private String thisStep;

		private String thisStepLookedup = "";
		private String nextStepLookedup = "";
		
		public CommunicationThread(Socket clientSocket) {

			this.clientSocket = clientSocket;

			try {

				br = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void run() {

			while (!Thread.currentThread().isInterrupted()) {

				try {
					String line = br.readLine();

					
					if (line != null){
						String type = "";
						
						String[] s = line.split(",");
						String tempThisStep = s[0];
						if(tempThisStep.contains("A1")){
							tempThisStep = "A1-1";
						}
						String sec = s[1];
						String changed=s[2];
						if(changed.contains("T") || steps==null){
							if(tempThisStep.contains("loex")){
								type = "loex";
							}else if(tempThisStep.contains("ex")){
								type = "ex";
							}else if(tempThisStep.contains("Lo")){
								type = "Lo";
							}else if(tempThisStep.contains("Tr")){	
								type = "Tr";
							}
							if(!type.equals("")){
								steps = Instructions.steps.get(type).split(",");
							}
						}
						if(!tempThisStep.equals(thisStep)){
							thisStep = tempThisStep;
							nextStepLookedup = "";
							thisStepLookedup = Instructions.steps.get(thisStep);
							if(steps!=null){
								for(int i = 0; i < steps.length -1; i ++){
									if(steps[i].equals(thisStep)){
										if(i < steps.length-1){
											nextStepLookedup= Instructions.steps.get(steps[i+1]);
										}
									}
								}
							}
						}
						updateConversationHandler.post(new updateUIThread(thisStepLookedup,sec,nextStepLookedup));
						
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

	class updateUIThread implements Runnable {
		private String thisStep;
		private String nextStep;
		private String sec;

		public updateUIThread(String thisStep,String sec,String nextStep) {
			this.thisStep = thisStep;
			this.nextStep = nextStep;
			this.sec = sec; 
		}
//adsfalsdfjl;
		@Override
		public void run() {
			textTimer.setText(sec + "s");
			textThis.setText("目前: " + thisStep + " " + System.getProperty("line.separator") );
			textNext.setText("下一步: " + nextStep);
		}
	}
}