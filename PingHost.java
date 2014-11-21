package cmpe283_project1_010004982;

import java.rmi.RemoteException;

import com.vmware.vim25.InvalidProperty;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.VirtualMachinePowerState;
import com.vmware.vim25.VirtualMachineRuntimeInfo;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.VirtualMachine;

public class PingHost implements Runnable {
	
	public VirtualMachine vm;
	public HostSystem host;
	public String hostIP;
	
	public PingHost(VirtualMachine vm)
	{
		this.vm = vm;
	}
	
	@Override
	public void run()
	{
		//Find HOst from VMFunctions
		try {
			host = VMFunctions.findHost(vm);
			//System.out.println(host);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		hostIP = host.getName();
		System.out.println("HOST IP: " + hostIP);
		try {
			if (VMFunctions.pingIP(hostIP))
			{
				System.out.println("HOST " + host + "  is RUNNING...." );
				//recoverVM
				VMFunctions.VMRevert(vm);
			}
			else
			{
				System.out.println("HOST" + host + "  is NOT RUNNING...." );
				//recoverHost
				VMFunctions.HostRevert(host);
				//revert VM on host
				VMFunctions.VMRevert(vm);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.out.println("Waiting...");
		}
		
	}
}
