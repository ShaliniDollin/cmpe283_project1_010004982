package cmpe283_project1_010004982;

import java.net.URL;

import com.vmware.vim25.VirtualMachineSnapshotInfo;
import com.vmware.vim25.VirtualMachineSnapshotTree;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.Task;
import com.vmware.vim25.mo.VirtualMachine;

public class CreateSnapshot implements Runnable{
	
	URL vmURL, adminURL;
	String user, pass;
	ServiceInstance vmsi, adminsi;
	
	public CreateSnapshot(URL url, URL adminurl, String username, String password)
	{
		vmURL = url;
		adminURL = adminurl;
		user = username;
		pass = password;
	}
	
	@Override
	public void run()
	{
		try {
				//Create VM Service instance
				vmsi = VMFunctions.CreateInstance(vmURL, user, pass);
	
				//Take VM snapshot
				TakeVMSnapshot(vmsi);
				
				//Create Host Service Instance
				adminsi = VMFunctions.CreateInstance(adminURL, user, pass);
				
				//Take Host snapshot
				TakeHostSnapshot(adminsi);
		}
		catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}
	
	public void TakeVMSnapshot(ServiceInstance si)
	{
		ManagedEntity[] mes = VMFunctions.getAllVM(si);
		for(int i=0; i < mes.length; i++)
		{
			VirtualMachine vm = (VirtualMachine)mes[i];
			if(vm != null){
				//Create snap shot for VM
				
				//Remove previous snapshots
				VirtualMachineSnapshotInfo info = vm.getSnapshot();
			    if(info != null){
			    VirtualMachineSnapshotTree[] tree = info.getRootSnapshotList();
				    if(tree.length > 1){
				    	
				    	//delete previous snapshots
				    	
				    	try {
							vm.removeAllSnapshots_Task();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} 
				    }
			    }
			    
			    
			    //take snapshot of VM
			    Task task = null;
				String description = "Snapshot_" + vm.getName() + "_" + System.currentTimeMillis();
				System.out.println("Snapshot of VM: " + vm.getName() + "-" + vm.getGuest().getIpAddress());
				
				try {
					task = vm.createSnapshot_Task(vm.getName(), description, true, true);
					if(task.waitForMe() == Task.SUCCESS){
						System.out.println(vm.getName() + "- Snapshot Created");
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				
			
			}
		}
	}

	public void TakeHostSnapshot(ServiceInstance si)
	{
		ManagedEntity[] mes = VMFunctions.getAllVM(si);
		for(int i=0; i < mes.length; i++)
		{
			VirtualMachine vm = (VirtualMachine)mes[i];
			if(vm != null)
			{
				if (vm.getName().contains("T02") && vm.getName().contains("cum1"))
				{
					//System.out.println(vm.getName());
					//Remove previous snapshots
					VirtualMachineSnapshotInfo info = vm.getSnapshot();
				    if(info != null){
				    VirtualMachineSnapshotTree[] tree = info.getRootSnapshotList();
					    if(tree.length > 1){
					    	
					    	//delete previous snapshots
					    	
					    	try {
								vm.removeAllSnapshots_Task();
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} 
					    }
				    }
				    
				    
				    //take snapshot of host
				    Task task = null;
					String description = "Snapshot_" + vm.getName() + "_" + System.currentTimeMillis();
					System.out.println("Snapshot of VM: " + vm.getName() + "-" + vm.getGuest().getIpAddress());
					
					try {
						task = vm.createSnapshot_Task(vm.getName(), description, true, true);
						if(task.waitForMe() == Task.SUCCESS){
							System.out.println(vm.getName() + "- Snapshot Created");
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
				}
			}
		}
		
	}
}
