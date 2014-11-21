package cmpe283_project1_010004982;

import java.io.FileReader;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.vmware.vim25.mo.Alarm;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ServiceInstance;

public class MainManager {
	
	public static ServiceInstance si, adminsi ;
	public static ManagedEntity[] mes = null;
	static ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(1);
	public static URL adminurl, url;
	public static String URLname, username, password, adminURLname;
	public static Alarm powerOffAlarm, powerOnAlarm;
	public static int repingCounter;
	
	public static void main(String[] args) throws Exception {
		
		VMFunctions vmf = new VMFunctions();
		//si = vmf.createServiceInstance(si); 
		Scanner sc;
		//Read Configurations
			sc = new Scanner(new FileReader("C:\\Users\\user\\Desktop\\Configuration.txt"));
			URLname = sc.nextLine();
			username = sc.nextLine();
			password = sc.nextLine();
			long snapInterval = Long.parseLong(sc.nextLine());
			int pingInterval = Integer.parseInt(sc.nextLine());
			int statInterval = Integer.parseInt(sc.nextLine());
			adminURLname = sc.nextLine();
			System.out.println(URLname + username + password + snapInterval + adminURLname);
			url = new URL(URLname);
			adminurl = new URL(adminURLname);
			si=VMFunctions.CreateInstance(url, username, password);
			//si = new ServiceInstance(adminurl, username, password, true);			
			sc.close();
			
					
		mes = VMFunctions.getAllVM(si);
		if (mes.length > 0  || mes != null)
		{
			//System.out.println(url + username + password + snapInterval + adminurl);
			
			//Create alarm
			powerOffAlarm = AlarmHandler.createPowerAlarm(url, username, password);
			//powerOnAlarm = AlarmHandler.createPowerAlarm(url, username, password, 2);
			
			//Start creating snapshots
			Runnable snapshot = new CreateSnapshot(url, adminurl, username, password);
			executor.scheduleAtFixedRate(snapshot, 0, snapInterval, TimeUnit.MINUTES);
			
			//call to ping all 
			Runnable pingVM = new PingVM(url, username, password, powerOffAlarm, powerOnAlarm, repingCounter);
			//new Thread(new PingVM()).start();//pingVM.start();
			executor.scheduleAtFixedRate(pingVM, 0, pingInterval, TimeUnit.SECONDS);
			
			//Collect stats
			Runnable printStats = new Statistics(url, username, password);
			executor.scheduleAtFixedRate(printStats, 0, statInterval, TimeUnit.MINUTES);
			
		}
	}

}
