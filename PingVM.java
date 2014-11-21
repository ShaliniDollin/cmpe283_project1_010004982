package cmpe283_project1_010004982;

import java.net.URL;

import com.vmware.vim25.mo.Alarm;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.VirtualMachine;

public class PingVM implements Runnable {
	
	URL vmurl;
	String user, pass;
	ServiceInstance si;
	Alarm poffAlarm,ponAlarm;
	int repingCounter;
	
	public PingVM(URL url, String username, String password, Alarm powerOffAlarm, Alarm powerOnAlarm, int counter)
	{
		vmurl=url;
		user = username;
		pass = password;
		poffAlarm = powerOffAlarm;
		ponAlarm = powerOnAlarm;
		repingCounter = counter;
	}
	
	@Override
	public void run() 
	{
		//Create VM Service Instance
		try {
			si = VMFunctions.CreateInstance(vmurl, user, pass);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		ManagedEntity[] mes = VMFunctions.getAllVM(si);
		for(int i=0; i < mes.length; i++){
			VirtualMachine vm = (VirtualMachine)mes[i];
			if(vm != null){
					try {
						System.out.println("Pinging:" + vm.getName());
						//if VM not responding 

						if(!VMFunctions.pingIP(vm.getGuest().getIpAddress()))
						{
							//Ping 3 more times to confirm failure
							System.out.println("Vm " + vm.getName() + " may have failed. Let's ping it again to confirm.");
							repingCounter = 0;
							do
							{
								//Ping
								if (!VMFunctions.pingIP(vm.getGuest().getIpAddress()))
								{
									System.out.println("Pinging again : " + (repingCounter + 1));
									repingCounter++;
								}
								else
									break;
							}while (repingCounter < 3);
							
							if(repingCounter == 3)
							{
								repingCounter =0;
								//VM Failure 
								//1. check if power off
								if (VMFunctions.checkPowerOffAlarm(poffAlarm, vm)) {
									System.out.println(vm.getName() + " is powered off.");
								}
								
								else
								{
									System.out.println(vm.getName() + " failed.");
									//2. check host
									//Find host and ping host check host
										System.out.println("Checking Host.");
										new Thread(new PingHost(vm)).start();//pingHost.start();
										Thread.sleep(100000);
								}
							}
							else
								System.out.println("VM: " + vm.getName() + "started responding");
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						System.out.println("PingVM : ");
						e.printStackTrace();
					}
			}
		}
	}

}
