package group.spart.bl.service.remote;

import javax.bluetooth.ServiceRecord;

/** 
 * 
 * @author megre
 * @email renhao.x@seu.edu.cn
 * @version created on: Dec 31, 2020 5:06:00 PM 
 */
public interface ServiceFilter {

	boolean filter(ServiceRecord serviceRecord);
}
