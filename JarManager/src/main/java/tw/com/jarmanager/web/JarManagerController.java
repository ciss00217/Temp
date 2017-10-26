package tw.com.jarmanager.web;

import java.io.IOException;
import java.util.List;

import javax.jms.JMSException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;

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

		logger.debug("jarManager() is executed!  ");

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

	@RequestMapping(value = "/JarProjectVO", method = RequestMethod.POST)
	public @ResponseBody boolean insertJarPeojectVOs(@RequestBody JarProjectVO JarProjectVO) {
		boolean isSucess = false;
		try {
			isSucess = jarManagerService.addJarProjectVOXml(JarProjectVO);
		} catch (IOException | JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return isSucess;

	}

	@RequestMapping(value = "/JarPeojectVOs", method = RequestMethod.GET)
	public ModelAndView JarPeojectVOs() {
		boolean isRun = true;
		logger.debug("JarPeojectVOs is executed!  ");
		List<JarProjectVO> jarProjectVOList = null;

		try {
			jarProjectVOList = jarManagerService.getJarProjectVOStatus();
			if (jarProjectVOList == null) {
				isRun = false;
				jarProjectVOList = jarManagerService.getXMLJarPeojectVOs();
			}

		} catch (Exception e) {
			logger.debug("Error: " + e.getMessage());
		}

		ModelAndView model = new ModelAndView();
		model.setViewName("jarManagerNew");
		model.addObject("jarProjectVOList", jarProjectVOList);
		model.addObject("isRun", isRun);

		return model;

	}
}
