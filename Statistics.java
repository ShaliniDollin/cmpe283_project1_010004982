package cmpe283_project1_010004982;

import java.net.URL;

import com.vmware.vim25.VirtualMachineQuickStats;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.VirtualMachine;

public class Statistics implements Runnable{
	
	URL vmURL;
	String user, pass;
	ServiceInstance si;
	
	public Statistics(URL url, String username, String password)
	{
		vmURL = url;
		user = username;
		pass = password;
	}
	
	@Override
	public void run()
	{
		try {
			si = VMFunctions.CreateInstance(vmURL, user, pass);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ManagedEntity[] vms = VMFunctions.getAllVM(si);
		VirtualMachine vm = null;
		VirtualMachineQuickStats stats;
		
		System.out.println("--------------------- Stats ---------------------");
		System.out.println(" VM Name"+"\t\t Guest OS"+"\t\t CPU Usage"+"\t\t "+"\t\t Guest Memory Usage"+"\t\t Host Memory Usage"+"\t\t IP Addresses"+"\tState");
		for (int i = 0; i < vms.length; i++) {
		
			vm = (VirtualMachine) vms[i];
			
			stats = vm.getSummary().getQuickStats();
			
			System.out.println(vms[i].getName()+"\t\t"+vm.getSummary().getConfig().guestFullName+"\t"
			+stats.overallCpuUsage+"\t\t\t"+stats.getGuestMemoryUsage()
			+stats.getHostMemoryUsage() + "\t\t\t"
			+vm.getSummary().getGuest().getIpAddress()
			+"\t \t "+vm.getGuest().guestState+"\n"); 
		}
			
	}

}
