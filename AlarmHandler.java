package cmpe283_project1_010004982;

import java.net.URL;

import com.vmware.vim25.AlarmSetting;
import com.vmware.vim25.AlarmSpec;
import com.vmware.vim25.StateAlarmExpression;
import com.vmware.vim25.StateAlarmOperator;
import com.vmware.vim25.mo.Alarm;
import com.vmware.vim25.mo.AlarmManager;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ServiceInstance;

public class AlarmHandler {
	
	static URL vmURL;
	static String user;
	static String pass;
	ServiceInstance vmsi;
	
	
		public static Alarm createPowerAlarm(URL url, String username, String password) throws Exception{

			ServiceInstance si = VMFunctions.CreateInstance(url, username, password);
			
			ManagedEntity[] dcs = si.getRootFolder().getChildEntity();
			
			AlarmManager alarmMgr = si.getAlarmManager();
			AlarmSpec spec = new AlarmSpec();
			StateAlarmExpression expression = createStateOffAlarmExpression();
		
		    spec.setAction(null);
		    spec.setExpression(expression);
		    spec.setName("VM PowerOff State Alarm");
		    spec.setDescription("Alarm when VM powers off");
		    spec.setEnabled(true);    
		    
		    AlarmSetting as = new AlarmSetting();
		    as.setReportingFrequency(0); 
		    as.setToleranceRange(0);
		    
		    //Remove alrms
		    spec.setSetting(as);
		    Alarm[] alarms = alarmMgr.getAlarm(dcs[0]);
		    for (Alarm alarm : alarms) {
		    	if (alarm.getAlarmInfo().getName().equals(spec.getName()))
		    		alarm.removeAlarm();
		    }
		    System.out.println("Previous alarms removed");
		    
		    Alarm alarm = alarmMgr.createAlarm(dcs[0], spec);
		    System.out.println("PowerOff State Alarm created.");
		    Thread.sleep(2000);
		    return alarm;
		}
		
		private static StateAlarmExpression createStateOffAlarmExpression() {
			StateAlarmExpression expression1 = new StateAlarmExpression();
			expression1.setType("VirtualMachine");
			expression.setStatePath("runtime.powerState");
			expression1.setOperator(StateAlarmOperator.isEqual);
			expression1.setYellow("poweredOff");
			return expression1;
		}
	
}
