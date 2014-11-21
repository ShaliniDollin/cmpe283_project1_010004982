package cmpe283_project1_010004982;

import java.net.URL;
import java.rmi.RemoteException;

import com.vmware.vim25.AlarmState;
import com.vmware.vim25.InvalidProperty;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.VirtualMachinePowerState;
import com.vmware.vim25.VirtualMachineRuntimeInfo;
import com.vmware.vim25.mo.Alarm;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.Task;
import com.vmware.vim25.mo.VirtualMachine;

public class VMFunctions {
	
	public static ManagedEntity[] getAllVM(ServiceInstance si){
		ManagedEntity[] mes = null;
		try {
			System.out.println(si.getRootFolder());
			mes = new InventoryNavigator(si.getRootFolder()).searchManagedEntities("VirtualMachine");
		} catch (Exception e) {
			System.out.println("Error in getAllVM: " + e.getMessage());
		}
		return mes;
	}
	
	public static HostSystem findHost(VirtualMachine vm) throws InvalidProperty, RuntimeFault, RemoteException{
		HostSystem vmHost = null;
			ManagedEntity[] hosts = new InventoryNavigator(MainManager.si.getRootFolder()).searchManagedEntities("HostSystem");
			for(int i=0; i<hosts.length; i++){
				//System.out.println("host["+i+"]=" + hosts[i].getName());
				HostSystem h = (HostSystem) hosts[i];
				VirtualMachine vms[] = h.getVms();
				for (int p = 0; p < vms.length; p++) {
					VirtualMachine v = (VirtualMachine) vms[p];
					if ((v.getName().toLowerCase()).equals(vm.getName().toLowerCase())) {
						vmHost = (HostSystem) hosts[i];
						break;
					}
				}
			}
		return vmHost;
	}
	
	public static boolean pingIP(String ip) throws Exception {
		String cmd = "";
		
		if(ip != null)
		{
		
			if (System.getProperty("os.name").startsWith("Windows")) {
				// For Windows
				cmd = "ping -n 3 " + ip;
			} else {
				// For Linux and OSX
				cmd = "ping -c 3 " + ip;
			}
		
			System.out.println("Ping "+ ip + "......");
			Process process = Runtime.getRuntime().exec(cmd);
			process.waitFor();		
			return process.exitValue() == 0;
		}
		else
		{
			System.out.println("Wating .... ");
			Thread.sleep(100000);
			return false;
		}
	}

	public static void VMRevert(VirtualMachine revertvm) throws Exception
	{
			Task task = revertvm.revertToCurrentSnapshot_Task(null);
			if(task.waitForMe()==Task.SUCCESS)
			{
				System.out.println("Reverted to snapshot:" + revertvm.getName());
			}
			VMFunctions.powerOn(revertvm);
	}
	
	public static void HostRevert(HostSystem revertHost) throws Exception
	{
		//Create admin service instance
		ServiceInstance si = CreateInstance(MainManager.adminurl, MainManager.username, MainManager.password);
		
		//Search failed host name 
		ManagedEntity[] mes = VMFunctions.getAllVM(si);
		for(int i=0; i < mes.length; i++)
		{
			VirtualMachine vm = (VirtualMachine)mes[i];
			if(vm != null)
			{
				if (vm.getName().contains(revertHost.getName()))
				{
					//Revert
					System.out.println("Reverting host:" + vm.getName());
					Task task = vm.revertToCurrentSnapshot_Task(null);
					if(task.waitForTask()==Task.SUCCESS)
					{
						VMFunctions.powerOn(vm);
						System.out.println("Reverted to snapshot:" + vm.getName());
					}
				}
			}
		}
	}

	public static ServiceInstance CreateInstance(URL url, String username, String password) throws Exception
	{
		//System.out.println("I'm here" + url + username + password);
		ServiceInstance si = new ServiceInstance(url, username, password, true);
		return si;
	}
	
	public static boolean checkPowerOffAlarm(Alarm alarm, VirtualMachine vm) {
		AlarmState[] as = vm.getTriggeredAlarmState();
		if (as == null)
			return false;
		for (AlarmState state : as) {
			// if the vm has a poweroff alarm, return true;
			if (alarm.getMOR().getVal().equals(state.getAlarm().getVal()))
				return true;
		}
		return false;
	}
	
	public static void powerOn(VirtualMachine vm) throws Exception
	{
		VirtualMachineRuntimeInfo vmri = (VirtualMachineRuntimeInfo) vm.getRuntime();
		Task task = vm.powerOnVM_Task(null);
		if(vmri.getPowerState() == VirtualMachinePowerState.poweredOff)
		{
			if(task.waitForTask() == Task.SUCCESS)
				System.out.println("vm:" + vm.getName() + " powered on.");
			else
				System.out.println("Can not power on " + vm.getName());
		}
		else
			System.out.println("vm:" + vm.getName() + " is running.");
	}
	
	public static void powerOff(VirtualMachine vm) throws Exception
	{
		VirtualMachineRuntimeInfo vmri = (VirtualMachineRuntimeInfo) vm.getRuntime();
		if(vmri.getPowerState() == VirtualMachinePowerState.poweredOn)
		{
			Task task = vm.powerOffVM_Task();
			if(task.waitForTask() == Task.SUCCESS)
				System.out.println("vm:" + vm.getName() + " powered off.");
			else
				System.out.println("Can not power off " + vm.getName());
		}
	}
}
