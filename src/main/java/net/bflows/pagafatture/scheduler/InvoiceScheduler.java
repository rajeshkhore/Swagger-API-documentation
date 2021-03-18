package net.bflows.pagafatture.scheduler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import net.bflows.pagafatture.dao.InvoicesDao;
import net.bflows.pagafatture.entities.Invoices;
import net.bflows.pagafatture.entities.Invoices.InvoiceStateEnum;
import net.bflows.pagafatture.entities.TimelineEntity;
import net.bflows.pagafatture.entities.TransactionsEntity;
import net.bflows.pagafatture.repositories.InvoicesRepository;
import net.bflows.pagafatture.repositories.TimelineRepository;
import net.bflows.pagafatture.repositories.TransactionRepository;
import net.bflows.pagafatture.util.DateTimeUtil;

@Component
public class InvoiceScheduler {

    public final Logger log = LoggerFactory.getLogger(InvoiceScheduler.class);

    @Autowired
    InvoicesRepository invoicesRepository;

    @Autowired
    TransactionRepository transactionRepository;
    
    @Autowired 
    TimelineRepository timelineRepository;
    
    @Autowired
    InvoicesDao invoicesDao;

    @Scheduled(cron = "0 0 1 * * ?")
    public void updateInvoiceStateForAllInvoices() {

        try {
            log.info("Started invoice scheduler");

            List<String> invoiceStates = Arrays.asList(InvoiceStateEnum.DUE.getValue(),
                    InvoiceStateEnum.OVERDUE.getValue(), InvoiceStateEnum.SENT.getValue());

            List<Invoices> invoices = invoicesRepository.findByInvoiceStateInAndClientEntityDeleted(invoiceStates,
                    false);

            log.info("Invoices fetched successfully");
            log.info("Invoices going to process count :- {%d}", invoices.size());

            processInvoices(invoices);

            log.info("Invoices processed successfully");
        } catch (Exception exception) {
            log.error("Exception occured while processing invoices :- {0}", exception);
        }

    }

    private void processInvoices(List<Invoices> invoices) {

        List<Invoices> updateInvoicesList = new ArrayList<>();
        List<TimelineEntity> timelineList = new ArrayList<>();
        LocalDateTime currentDate = DateTimeUtil.DateServerToUTC(LocalDateTime.now());
        if (!CollectionUtils.isEmpty(invoices)) {

            for (Invoices invoice : invoices) {
                 if(invoice.getDueDate() != null) {
                	 
                	 if (invoice.getInvoiceState().equals(InvoiceStateEnum.DUE.getValue())
                			 && currentDate.isAfter(invoice.getDueDate())) {
                		 TimelineEntity timelineEntity= invoicesDao.createTimeLineForInvoice(InvoiceStateEnum.OVERDUE.getValue(), invoice);
                		 invoice.setInvoiceState(InvoiceStateEnum.OVERDUE.getValue());
                		 invoice.setUpdatedDate(currentDate);
                		 timelineList.add(timelineEntity);
                		 updateInvoicesList.add(invoice);
                	 } else if (invoice.getInvoiceState().equals(InvoiceStateEnum.OVERDUE.getValue())
                			 && (currentDate.isBefore(invoice.getDueDate()) || currentDate.isEqual(invoice.getDueDate()))) {
                		 TimelineEntity timelineEntity= invoicesDao.createTimeLineForInvoice(InvoiceStateEnum.DUE.getValue(), invoice);
                		 invoice.setInvoiceState(InvoiceStateEnum.DUE.getValue());
                		 invoice.setUpdatedDate(currentDate);
                		 timelineList.add(timelineEntity);
                		 updateInvoicesList.add(invoice);
                	 } else if (invoice.getInvoiceState().equals(InvoiceStateEnum.SENT.getValue())) {
                		 TimelineEntity timelineEntity=null;
                		 if (currentDate.isAfter(invoice.getDueDate())) {
                			 timelineEntity= invoicesDao.createTimeLineForInvoice(InvoiceStateEnum.OVERDUE.getValue(), invoice);
                			 invoice.setInvoiceState(InvoiceStateEnum.OVERDUE.getValue());
                		 } else {
                			 timelineEntity= invoicesDao.createTimeLineForInvoice(InvoiceStateEnum.DUE.getValue(), invoice);
                			 invoice.setInvoiceState(InvoiceStateEnum.DUE.getValue());
                		 }
                		 TransactionsEntity transactionsEntity = transactionRepository.findByInvoicesId(invoice.getId());
                		 if (transactionsEntity != null) {
                			 timelineEntity= invoicesDao.createTimeLineForInvoice(InvoiceStateEnum.PAID.getValue(), invoice);
                			 invoice.setInvoiceState(InvoiceStateEnum.PAID.getValue());
                		 }
                		 invoice.setUpdatedDate(currentDate);
                		 updateInvoicesList.add(invoice);
                		 timelineList.add(timelineEntity);
                	 }
                 }

                if (updateInvoicesList.size() > 100) {
                    invoicesRepository.saveAll(updateInvoicesList);
                    timelineRepository.saveAll(timelineList);
                    timelineList=new ArrayList<>();
                    updateInvoicesList = new ArrayList<>();
                }
            }

            if (!CollectionUtils.isEmpty(updateInvoicesList)) {
                invoicesRepository.saveAll(updateInvoicesList);
                timelineRepository.saveAll(timelineList);
            }
        }

    }
    
    
  
}
