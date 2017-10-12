package tw.com.jarmanager.web;

import java.io.IOException;
import java.util.List;

import javax.jms.JMSException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import tw.com.heartbeat.clinet.vo.HeartBeatClientVO;
import tw.com.jarmanager.api.service.JarManagerAPIService;
import tw.com.jarmanager.api.vo.JarProjectVO;
import tw.com.jarmanager.service.JarManagerService;

@Controller
public class JarManagerController {

	private final Logger logger = LoggerFactory.getLogger(JarManagerController.class);
	private final JarManagerService jarManagerService;

	@Autowired
	public JarManagerController(JarManagerService jarManagerService) {
		this.jarManagerService = jarManagerService;
	}

	@RequestMapping(value = "/Delete/queue", method = RequestMethod.GET)
	public ModelAndView getSoleHeartBeatClientVOList() throws JMSException, IOException {


		List<JarProjectVO> jarProjectVOList = null;

		try {
			jarProjectVOList = jarManagerService.getJarProjectVOStatus();
		} catch (JMSException e) {
			logger.debug("Error: " + e.getMessage());
		}
		
		jarManagerService.clearQueue();
		
		ModelAndView model = new ModelAndView();
		model.setViewName("jarManager");
		model.addObject("jarProjectVOList", jarProjectVOList);

		return model;

	}

	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public ModelAndView jarManagerIndex() {

		logger.debug("jarManager() is executed!");

		List<HeartBeatClientVO> heartBeatClientVOList = null;
		List<JarProjectVO> jarProjectVOList = null;

		try {
			jarProjectVOList = jarManagerService.getJarProjectVOStatus();
		} catch (Exception e) {
			logger.debug("Error: " + e.getMessage());
		}

		ModelAndView model = new ModelAndView();
		model.setViewName("jarManager");
		model.addObject("heartBeatClientVOList", heartBeatClientVOList);
		model.addObject("jarProjectVOList", jarProjectVOList);

		return model;

	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView jarManager() {

		logger.debug("jarManager() is executed!");

		List<HeartBeatClientVO> heartBeatClientVOList = null;
		List<JarProjectVO> jarProjectVOList = null;

		try {
			jarProjectVOList = jarManagerService.getJarProjectVOStatus();
		} catch (Exception e) {
			logger.debug("Error: " + e.getMessage());
		}

		ModelAndView model = new ModelAndView();
		model.setViewName("jarManager");
		model.addObject("heartBeatClientVOList", heartBeatClientVOList);
		model.addObject("jarProjectVOList", jarProjectVOList);

		return model;

	}
}
