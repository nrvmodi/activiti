package com.nirav.modi.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nirav.modi.service.MyService;

@RestController
public class ProcessController {

	@Autowired
	private MyService myService;

	@Autowired
	private RuntimeService runTimeService;

	@Autowired
	private HistoryService historyService;

	@Autowired
	private RepositoryService repositoryService;
	
	List<String> highLightedFlows = new ArrayList<>();
	List<String> historicActivityInstanceList = new ArrayList<>();

	@RequestMapping(value = "/process", method = RequestMethod.POST)
	public void startProcessInstance(@RequestBody StartProcessRepresentation startProcessRepresentation) {
		myService.startProcess(startProcessRepresentation.getAssignee());
	}


	@RequestMapping(value = "/diagram/{processInstanceId}", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
	public byte[] displayProcess(@PathVariable String processInstanceId) throws IOException {
		ProcessInstance processInstance = runTimeService.createProcessInstanceQuery()
				.processInstanceId(processInstanceId).singleResult();

		ProcessDefinitionEntity pde = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
				.getDeployedProcessDefinition(processInstance.getProcessDefinitionId());

		List<String> activityIds = runTimeService.getActiveActivityIds(processInstanceId);

		/*
		 * if (pde != null && pde.isGraphicalNotationDefined()) { BpmnModel
		 * bpmnModel = repositoryService.getBpmnModel(pde.getId()); InputStream
		 * resource = new
		 * DefaultProcessDiagramGenerator().generateDiagram(bpmnModel, "png",
		 * activityIds);
		 * 
		 * // ImageInputStream stream = //
		 * ImageIO.createImageInputStream(resource); return
		 * org.apache.commons.io.IOUtils.toByteArray(resource);
		 * 
		 * }
		 */

		List<String> activeAtivityIds = runTimeService.getActiveActivityIds(processInstanceId);
		List<String> activeAndHistorcActivityIds = getHighLightedFlows(pde, processInstanceId);

		List<String> flows = new LinkedList<String>();

		BpmnModel bpmnModel = repositoryService.getBpmnModel(pde.getId());
		/*for (org.activiti.bpmn.model.Process process : bpmnModel.getProcesses()) {
			for (FlowElement flowElement : process.getFlowElements()) {
				if (flowElement instanceof SequenceFlow) {
					SequenceFlow sequenceFlow = (SequenceFlow) flowElement;
					String from = sequenceFlow.getSourceRef();
					String to = sequenceFlow.getTargetRef();
					if (activeAndHistorcActivityIds.contains(from) && activeAndHistorcActivityIds.contains(to)) {
						flows.add(sequenceFlow.getId());
					}
				}
			}
		}*/
		if (pde != null && pde.isGraphicalNotationDefined()) {
			InputStream resource = new DefaultProcessDiagramGenerator().generateDiagram(bpmnModel, "png", activeAtivityIds,
					activeAndHistorcActivityIds);

			// ImageInputStream stream =
			// ImageIO.createImageInputStream(resource);
			return org.apache.commons.io.IOUtils.toByteArray(resource);

		}

		return null;
	}

	private List<String> getHighLightedFlows(ProcessDefinitionEntity processDefinition, String processInstanceId) {

		List<HistoricActivityInstance> historicActivityInstances = historyService.createHistoricActivityInstanceQuery()
				.processInstanceId(processInstanceId).orderByHistoricActivityInstanceStartTime().asc().list();

		for (HistoricActivityInstance hai : historicActivityInstances) {
			historicActivityInstanceList.add(hai.getActivityId());
		}

		// add current activities to list
		List<String> highLightedActivities = runTimeService.getActiveActivityIds(processInstanceId);
		historicActivityInstanceList.addAll(highLightedActivities);

		// activities and their sequence-flows
		getHighLightedFlows(processDefinition.getActivities());

		return highLightedFlows;
	}

	private void getHighLightedFlows(List<ActivityImpl> activityList) {
		for (ActivityImpl activity : activityList) {
			if (activity.getProperty("type").equals("subProcess")) {
				// get flows for the subProcess
				getHighLightedFlows(activity.getActivities());
			}

			if (historicActivityInstanceList.contains(activity.getId())) {
				List<PvmTransition> pvmTransitionList = activity.getOutgoingTransitions();
				for (PvmTransition pvmTransition : pvmTransitionList) {
					String destinationFlowId = pvmTransition.getDestination().getId();
					if (historicActivityInstanceList.contains(destinationFlowId)) {
						highLightedFlows.add(pvmTransition.getId());
					}
				}
			}
		}
	}

	@RequestMapping(value = "/start", method = RequestMethod.GET)
	public void startProcessInstance() {
		myService.startProcess("jbarrez");
		myService.startProcess("trademakers");
		myService.startProcess("nrvmodi");
		myService.startProcess("sonammodi");
	}

	@RequestMapping(value = "/tasks", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<TaskRepresentation> getTasks(@RequestParam String assignee) {
		List<Task> tasks = myService.getTasks(assignee);
		List<TaskRepresentation> dtos = new ArrayList<TaskRepresentation>();
		for (Task task : tasks) {
			dtos.add(new TaskRepresentation(task.getId(), task.getName()));
		}
		return dtos;
	}

	static class TaskRepresentation {

		private String id;
		private String name;

		public TaskRepresentation(String id, String name) {
			this.id = id;
			this.name = name;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

	}

	static class StartProcessRepresentation {

		private String assignee;

		public String getAssignee() {
			return assignee;
		}

		public void setAssignee(String assignee) {
			this.assignee = assignee;
		}
	}

}
