//package net.bflows.pagafatture.scheduler;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import net.bflows.pagafatture.dao.ActionDao;
//import net.bflows.pagafatture.entities.MerchantEntity;
//import net.bflows.pagafatture.model.ActionReq;
//import net.bflows.pagafatture.model.ClientWithNextAction;
//import net.bflows.pagafatture.model.TimelineReq;
//import net.bflows.pagafatture.repositories.MerchantRepository;
//
//@Component
//public class ActionScheduler {
//
//	public final Logger log = LoggerFactory.getLogger(ActionScheduler.class);
//	@Autowired
//	private ActionDao actionDao;
//
//	@Autowired
//	private MerchantRepository merchantRepository;
//
//	@Scheduled(cron = "0 0 8 * * ?")
//	public void sendAutometicEmail() {
//		try {
//			log.info("Started action scheduler");
//			Map<String, Object> userInfo = new HashMap<>();
//			userInfo.put("merchantId", null); 
//			List<ClientWithNextAction> toDaysActionToPerform = new ArrayList<>();
//			List<MerchantEntity> merchantEntities = merchantRepository.findAll();
//			for (MerchantEntity merchantEntity : merchantEntities) {
//				List<ClientWithNextAction> clientWithNextActions = actionDao
//						.getNextActionsByMerchantId(merchantEntity.getId(), true);
//				for (ClientWithNextAction nextAction : clientWithNextActions) {
//
//					ActionReq actionReq = nextAction.getNextAction();
//					if (actionReq.getActionTypeId().equals(2l)) {
//						toDaysActionToPerform.add(nextAction);
//					}
//				}
//			}
//
//			log.info("actions fetched successfully");
//			log.info("today going to process {0} actions", toDaysActionToPerform.size());
//
//			for (ClientWithNextAction actionToPerform : toDaysActionToPerform) {
//
//				TimelineReq timelineReq = new TimelineReq();
//				timelineReq.setClientId(actionToPerform.getClientId());
//				timelineReq.setInvoiceId(actionToPerform.getInvoiceId());
//				timelineReq.setMessage("action performed by scheduler");
//				actionDao.performAction(actionToPerform.getNextAction().getId(), timelineReq, userInfo);
//			}
//
//			log.info("actions processed successfully");
//		} catch (Exception exception) {
//			log.error("Exception occured while processing actions :- {0}", exception);
//		}
//
//	}
//}
