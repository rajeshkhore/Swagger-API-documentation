package net.bflows.pagafatture.dao;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.bflows.pagafatture.entities.Invoices;
import net.bflows.pagafatture.entities.Invoices.InvoiceStateEnum;
import net.bflows.pagafatture.enums.DashboardEnums;
import net.bflows.pagafatture.model.ActionWidget;
import net.bflows.pagafatture.model.ClientWithNextAction;
import net.bflows.pagafatture.model.widget.InvoicesWithAverageDaysMonthWise;
import net.bflows.pagafatture.model.widget.InvoicesWithAverageDaysWidget;
import net.bflows.pagafatture.model.widget.TopUnpaidInvoiceBean;
import net.bflows.pagafatture.model.widget.TopUnpaidInvoicesWidgets;
import net.bflows.pagafatture.model.widget.TotalDueAndOverDueInvoicesWidget;
import net.bflows.pagafatture.model.widget.TotalDueAndOverDueInvoicesWithRange;
import net.bflows.pagafatture.model.widget.Widget;
import net.bflows.pagafatture.repositories.ClientRepository;
import net.bflows.pagafatture.repositories.InvoicesRepository;
import net.bflows.pagafatture.repositories.MerchantRepository;
import net.bflows.pagafatture.util.DateTimeUtil;
import net.bflows.pagafatture.util.Utils;

@Component
public class DashboardDao {

	@Autowired
	MerchantRepository merchantRepository;

	@Autowired
	InvoicesRepository invoicesRepository;

	@Autowired
	ClientRepository clientRepository;

	@Autowired
	ObjectMapper mapper;
	
	@Autowired
	ActionDao actionDao;

	private static final String CLIENTNAME="clientName";
	private static final String NOVERDUEDAYS="nOverDueDays";
	private static final String ID="id";
	
	public Widget getWidgets(Long id) {
		Widget element = new Widget();
		BigDecimal total = new BigDecimal(0);
		BigDecimal due = invoicesRepository.getInvoiceDueAmountByInvoiceState(id, InvoiceStateEnum.DUE.getValue());
		BigDecimal overDue = invoicesRepository.getInvoiceDueAmountByInvoiceState(id,
				InvoiceStateEnum.OVERDUE.getValue());
		if (due != null) {
			due=Utils.formatDecimalForBigDecimal(due);
			total = total.add(due);

		}
		if (overDue != null) {
			overDue=Utils.formatDecimalForBigDecimal(overDue);
			total = total.add(overDue);
		}
		element.setDue(due);
		element.setOverDue(overDue);
		element.setTotal(Utils.formatDecimalForBigDecimal(total));
		element.setTitle(DashboardEnums.TOTAL_UNPAID.getValue());
		return element;
	}

	public InvoicesWithAverageDaysWidget getInvoicesWithAverageDays(Long merchantId) {
		InvoicesWithAverageDaysWidget response = new InvoicesWithAverageDaysWidget();
		List<InvoicesWithAverageDaysMonthWise> data = new ArrayList<>();
		Double avgDaySum = Double.valueOf(0);
		Double totalAvg=0.0;
		int months = 6;
		for (int i = 6; i >= 1; i--) {
			InvoicesWithAverageDaysMonthWise invoicesWithAverageDaysMonthWise = new InvoicesWithAverageDaysMonthWise();
			LocalDateTime monthDate = DateTimeUtil.getPreviousMonthDate(i);
			Double avgDays = invoicesRepository.getAvgeDurationForInvoices(monthDate.getMonthValue(),
					monthDate.getYear(), merchantId);
			if (avgDays == null) {
				avgDays = 0.0;
			}
			if(avgDays <= 0) {
				months--;
			}
			invoicesWithAverageDaysMonthWise.setMonth(monthDate.getMonthValue());
			invoicesWithAverageDaysMonthWise.setNDays(avgDays.longValue());

			avgDaySum = avgDaySum + avgDays;
			data.add(invoicesWithAverageDaysMonthWise);

		}
        if(months > 0) {
        	totalAvg = avgDaySum / months;
        }
		response.setTitle(DashboardEnums.AVERAGE_DAYS.getValue());
		response.setAverageDays(totalAvg.longValue());
		response.setData(data);
		return response;
	}

	public TopUnpaidInvoicesWidgets getTopUnpaidInvoices(Long merchantId) {
        TopUnpaidInvoicesWidgets topUnpaidInvoicesWidgets = new TopUnpaidInvoicesWidgets(); 
		Pageable pageable = PageRequest.of(0, 5);
		List<TopUnpaidInvoiceBean> unpaidInvoices = new ArrayList<>();
		List<Map<String, Object>> unpaidInvoicesMap = invoicesRepository.getTopUnpaidinvoiceByClient(merchantId ,pageable);
		if(!CollectionUtils.isEmpty(unpaidInvoicesMap)) {
			for (Map<String, Object> map: unpaidInvoicesMap) {
				TopUnpaidInvoiceBean topUnpaidInvoiceBean = new TopUnpaidInvoiceBean();
					topUnpaidInvoiceBean.setClientName(map.get(CLIENTNAME).toString());
				if(map.get(NOVERDUEDAYS) != null) {
					topUnpaidInvoiceBean.setNOverdueDays(Double.valueOf(map.get(NOVERDUEDAYS).toString()).longValue());
				}
					topUnpaidInvoiceBean.setClientId(Long.valueOf(map.get(ID).toString()));
					Double totalAmount = invoicesRepository.calculateTotalAmountOfInvoicesByClientId(Long.valueOf(map.get(ID).toString()));
						
					topUnpaidInvoiceBean.setAmount(Utils.formatDecimal(totalAmount));
				unpaidInvoices.add(topUnpaidInvoiceBean);
			}
		}
		
		topUnpaidInvoicesWidgets.setTitle(DashboardEnums.MAIN_DEBTORS.getValue());
		topUnpaidInvoicesWidgets.setData(unpaidInvoices);
		return topUnpaidInvoicesWidgets;
	}

	public TotalDueAndOverDueInvoicesWidget getTotalDueAndOverDueInvoices(Long merchantId) {
	
		
		TotalDueAndOverDueInvoicesWidget response = new TotalDueAndOverDueInvoicesWidget();
		List<TotalDueAndOverDueInvoicesWithRange> data = new ArrayList<>();
		Double totalOverDue;
		Double totalDue;

		// for range 0-30
		Double zeroToThirtyOverDue = invoicesRepository
				.getTotalAmountByInvoiceStateAndMerchantId(InvoiceStateEnum.OVERDUE.getValue(), merchantId, 0, 30);
		Double zeroToThirtyDue = invoicesRepository
				.getTotalAmountByInvoiceStateAndMerchantId(InvoiceStateEnum.DUE.getValue(), merchantId, 0, 30);

		if (zeroToThirtyDue == null) {
			zeroToThirtyDue = 0d;
		}
		if (zeroToThirtyOverDue == null) {
			zeroToThirtyOverDue = 0d;
		}
		TotalDueAndOverDueInvoicesWithRange rang1 = new TotalDueAndOverDueInvoicesWithRange();
		rang1.setDueRange("0-30d");
        rang1.setDueAmount(Utils.formatDecimal(zeroToThirtyDue));
        rang1.setOverdueAmount(Utils.formatDecimal(zeroToThirtyOverDue));
		data.add(rang1);

		// for range 31-60
		Double thirtyToSixtyOverDue = invoicesRepository
				.getTotalAmountByInvoiceStateAndMerchantId(InvoiceStateEnum.OVERDUE.getValue(), merchantId, 31, 60);
		Double thirtyToSixtyDue = invoicesRepository
				.getTotalAmountByInvoiceStateAndMerchantId(InvoiceStateEnum.DUE.getValue(), merchantId, 31, 60);

		if (thirtyToSixtyOverDue == null) {
			thirtyToSixtyOverDue = 0d;
		}
		if (thirtyToSixtyDue == null) {
			thirtyToSixtyDue = 0d;
		}
		TotalDueAndOverDueInvoicesWithRange range2 = new TotalDueAndOverDueInvoicesWithRange();
		range2.setDueRange("30-60d");
		range2.setOverdueAmount(Utils.formatDecimal(thirtyToSixtyOverDue));
		range2.setDueAmount(Utils.formatDecimal(thirtyToSixtyDue));
		data.add(range2);

		// for range 61-90
		Double sixtytoNinetyOverDue = invoicesRepository
				.getTotalAmountByInvoiceStateAndMerchantId(InvoiceStateEnum.OVERDUE.getValue(), merchantId, 61, 90);
		Double sixtytoNinetyDue = invoicesRepository
				.getTotalAmountByInvoiceStateAndMerchantId(InvoiceStateEnum.DUE.getValue(), merchantId, 61, 90);

		if (sixtytoNinetyOverDue == null) {
			sixtytoNinetyOverDue = 0d;
		}
		if (sixtytoNinetyDue == null) {
			sixtytoNinetyDue = 0d;
		}
		TotalDueAndOverDueInvoicesWithRange rang3 = new TotalDueAndOverDueInvoicesWithRange();
		rang3.setDueRange("60-90d");
		rang3.setOverdueAmount(Utils.formatDecimal(sixtytoNinetyOverDue));
		rang3.setDueAmount(Utils.formatDecimal(sixtytoNinetyDue));
		data.add(rang3);

		// for range 91 over
		Double overNinetyOverDue = invoicesRepository.getTotalAmountByInvoiceStateAndMerchantIdGreaterThanNinety(
				InvoiceStateEnum.OVERDUE.getValue(), merchantId);
		Double overNinetyDue = invoicesRepository.getTotalAmountByInvoiceStateAndMerchantIdGreaterThanNinety(
				InvoiceStateEnum.DUE.getValue(), merchantId);

		if (overNinetyOverDue == null) {
			overNinetyOverDue = 0d;
		}
		if (overNinetyDue == null) {
			overNinetyDue = 0d;
		}
		TotalDueAndOverDueInvoicesWithRange rang4 = new TotalDueAndOverDueInvoicesWithRange();
		rang4.setDueRange("90d");
		rang4.setOverdueAmount(Utils.formatDecimal(overNinetyOverDue));
		rang4.setDueAmount(Utils.formatDecimal(overNinetyDue));
		data.add(rang4);

		totalDue =  Utils.formatDecimal(zeroToThirtyDue + thirtyToSixtyDue + sixtytoNinetyDue + overNinetyDue);
		totalOverDue = Utils.formatDecimal(zeroToThirtyOverDue + thirtyToSixtyOverDue + sixtytoNinetyOverDue + overNinetyOverDue);
		response.setTitle(DashboardEnums.AGING_BALANCE.getValue());
		response.setDueAmount(totalDue);
		response.setOverdueAmount(totalOverDue);
		response.setData(data);
		return response;
	}
	
	public ActionWidget getTotalActionAndTotalAmountByMerchantId(Long merchantId) {
		ActionWidget actionWidget = new ActionWidget();
		BigDecimal totalAmount = new BigDecimal(0);
		Integer numberOfAction = 0;
		List<ClientWithNextAction> clientWithNextActions = actionDao.getNextActionsByMerchantId(merchantId,false);
		
		for (ClientWithNextAction clientWithNextAction : clientWithNextActions) {
			Optional<Invoices> invoiceOptional = invoicesRepository.findById(clientWithNextAction.getInvoiceId());
		if(invoiceOptional.isPresent()) {
			Invoices invoice = invoiceOptional.get();
			totalAmount = totalAmount.add(invoice.getAmountGross());
			numberOfAction++;	
		}
		}
		actionWidget.setNumberOfAction(numberOfAction);
		actionWidget.setTotalAmount(Utils.formatDecimalForBigDecimal(totalAmount));
		actionWidget.setTitle(DashboardEnums.ACTION_WIDGETS.getValue());
		return actionWidget;
	}
}
