package group.spart.bl.service;

/** 
 * 
 * @author megre
 * @email renhao.x@seu.edu.cn
 * @version created on: Dec 26, 2020 10:42:21 PM 
 */
public interface InfoSubject {

	void attachObserver(InfoObserver observer);
	void notifyObservers();
}
