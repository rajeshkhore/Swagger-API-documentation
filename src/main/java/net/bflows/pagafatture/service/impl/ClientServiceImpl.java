package net.bflows.pagafatture.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import net.bflows.pagafatture.dao.ClientDao;
import net.bflows.pagafatture.entities.ClientEntity;
import net.bflows.pagafatture.entities.Invoices;
import net.bflows.pagafatture.entities.Invoices.InvoiceStateEnum;
import net.bflows.pagafatture.entities.MerchantEntity;
import net.bflows.pagafatture.model.ClientReq;
import net.bflows.pagafatture.model.ClientResponseBean;
import net.bflows.pagafatture.model.InvoiceReq;
import net.bflows.pagafatture.model.InvoiceSearchReq;
import net.bflows.pagafatture.model.MerchantReq;
import net.bflows.pagafatture.service.ClientService;
import net.bflows.pagafatture.util.Utils;
import net.bflows.pagafatture.util.Validator;
import net.bflows.pagafatture.web.rest.errors.BadRequestAlertException;

@Service
public class ClientServiceImpl implements ClientService {

	@Autowired
	ClientDao clientDao;
	
	@Autowired
	Validator validator;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public ClientReq createClient(HttpServletRequest httpServletRequest, Long id, ClientReq body) {

		Map<String, Object> userInfo = validator.getUserInfoFromToken(httpServletRequest); 
		MerchantEntity merchantEntity=validator.validateUserMerchantId(userInfo, id);
		return clientDao.createClient(merchantEntity, body);
	}

	@Override
	@Transactional(readOnly = true)
	public ClientResponseBean getClientById(HttpServletRequest httpServletRequest, Long merchantId, Long id) {
		
		Map<String, Object> userInfo = validator.getUserInfoFromToken(httpServletRequest); 
		validator.validateUserMerchantId(userInfo, merchantId);
		
		StringBuilder token = new StringBuilder();
		token.append(merchantId).append(".").append(id);
		ClientResponseBean response = clientDao.getClientById(merchantId, id);
		response.getClient().setToken(Utils.encodeBase64String(generateToken(merchantId,id)));
		return response;
	}

	private String generateToken(Long merchantId, Long id) {
		StringBuilder token = new StringBuilder();
		token.append("merchant-").append(merchantId).append(".client-").append(id);	
		return token.toString();
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public ClientReq updateClient(HttpServletRequest httpServletRequest, Long id, Long clientId, ClientReq body) {
		Map<String, Object> userInfo = validator.getUserInfoFromToken(httpServletRequest); 
		validator.validateUserMerchantId(userInfo, id);
		return clientDao.updateClient(id, clientId, body);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteClient(HttpServletRequest httpServletRequest, Long id,Long clientId) {
		Map<String, Object> userInfo = validator.getUserInfoFromToken(httpServletRequest); 
		validator.validateUserMerchantId(userInfo, id);
		clientDao.deleteClientById(id , clientId);

	}

	@Override
	@Transactional(readOnly = true)
	public List<ClientReq> getClientsByMerchantId(HttpServletRequest httpServletRequest, Long merchantId) {
		Map<String, Object> userInfo = validator.getUserInfoFromToken(httpServletRequest); 
		validator.validateUserMerchantId(userInfo, merchantId);
		List<ClientReq> clientList = clientDao.getClientsByMerchantId(merchantId);
		if(!CollectionUtils.isEmpty(clientList)) {
			for(ClientReq client : clientList) {
					client.setToken(Utils.encodeBase64String(generateToken(merchantId,client.getId())));	
			}
		}
		return clientList;
	}

	@Override
	public ClientResponseBean getInvoicesByMerchantIdAndClientId(HttpServletRequest httpServletRequest, InvoiceSearchReq request) {
		
		if(request == null) {
    		throw new BadRequestAlertException("Request is missing.", null, null);
    	}
		if(StringUtils.isEmpty(request.getToken())) {
    		throw new BadRequestAlertException("token is missing.", null, null);
    	}
		
		String token = Utils.decodeBase64String(request.getToken());
		String[] tokenArr = token.split("\\.");
		
		String merchantIdStr = null;
		String clientIdStr = null;
		if(ArrayUtils.isNotEmpty(tokenArr) && 2 <= tokenArr.length) {
			merchantIdStr = getMerchantId(tokenArr, merchantIdStr);
			clientIdStr = getClientId(tokenArr, clientIdStr);
			
		}else {
			throw new BadRequestAlertException("Please pass valid token.", null, null);	
		}
		
		if(StringUtils.isEmpty(merchantIdStr)) {
    		throw new BadRequestAlertException("Merchant id is missing.", null, null);
    	}
    	if(StringUtils.isEmpty(clientIdStr)) {
    		throw new BadRequestAlertException("Client id is missing.", null, null);
    	}
    	Long merchantId = Long.parseLong(merchantIdStr) ;
    	Long clientId= Long.parseLong(clientIdStr) ;
    	Map<String, Object> userInfo = validator.getUserInfoFromToken(httpServletRequest); 
		validator.validateUserMerchantId(userInfo, merchantId);
    	List<String> invoiceStateList = new ArrayList<>();
    	if(StringUtils.isNotEmpty(request.getInvoiceState())) {
    		String invoiceStateArr[] = request.getInvoiceState().split(",");
    		if(0 < invoiceStateArr.length) {
    			for(String state : invoiceStateArr) {
    				InvoiceStateEnum invoiceStateEnum = InvoiceStateEnum.fromValue(state);
    				if(invoiceStateEnum != null) {
    					invoiceStateList.add(invoiceStateEnum.getValue().toString());
    				}else {
    					throw new BadRequestAlertException("InvoiceStateEnum not valid :-"+state, null, null);
    				}
    			}
    		}
    	}

		List<Invoices> invoicesList = clientDao.getInvoicesByMerchantIdAndClientId(clientId, merchantId,CollectionUtils.isEmpty(invoiceStateList)?null:invoiceStateList);
		ClientResponseBean invoicesView = null;
    	if(!CollectionUtils.isEmpty(invoicesList)) {
    		Invoices invoices = invoicesList.get(0);
    		if(invoices != null) {
    			ClientEntity clientEntity = invoices.getClientEntity();
    			if(clientEntity != null) {
    				MerchantEntity merchantEntity = clientEntity.getMerchantEntity();	
    				if(merchantEntity != null) {
    					invoicesView = new ClientResponseBean();
    					List<InvoiceReq> invoiceList = new ArrayList<InvoiceReq>();
    					invoicesView.setMerchant(mapToModel(new MerchantReq(),merchantEntity));
    					invoicesView.setClient(mapToModel(new ClientReq(),clientEntity));
    					for(Invoices invoicesEnti : invoicesList) {
    						invoiceList.add(mapToModel(new InvoiceReq(),invoicesEnti));	
    					}
    					invoicesView.setInvoicesList(invoiceList);
    				}
    			}
    		}
    	}else {
    		
    		ClientEntity clientEntity = clientDao.getClientByIdAndMerchantId(clientId,merchantId);
			if(clientEntity != null) {
				MerchantEntity merchantEntity = clientEntity.getMerchantEntity();	
				if(merchantEntity != null) {
					invoicesView = new ClientResponseBean();
					invoicesView.setMerchant(mapToModel(new MerchantReq(),merchantEntity));
					invoicesView.setClient(mapToModel(new ClientReq(),clientEntity));
					invoicesView.setInvoicesList(new ArrayList<InvoiceReq>());
				}
			}
    	}
		return invoicesView;
	}

	private String getClientId(String[] tokenArr, String clientIdStr) {
		String client[] = tokenArr[1].split("-");
		if(ArrayUtils.isNotEmpty(client) && 2 <= client.length) {
			clientIdStr = client[1];	
		}
		return clientIdStr;
	}

	private String getMerchantId(String[] tokenArr, String merchantIdStr) {
		String merchant[] = tokenArr[0].split("-");
		if(ArrayUtils.isNotEmpty(merchant) && 2 <= merchant.length) {
			merchantIdStr= merchant[1];	
		}
		return merchantIdStr;
	}

	private InvoiceReq mapToModel(InvoiceReq invoice, Invoices invoices) {
		invoice.setId(invoices.getId());
		invoice.setAmountGross(invoices.getAmountGross());
		invoice.setAmountNet(invoices.getAmountNet());
		invoice.setExternalId(invoices.getExternalId());
		invoice.setExternalRef(invoices.getExternalRef());
		invoice.setLinkDoc(invoices.getLinkDoc());
		invoice.setInvoiceNumber(invoices.getInvoiceNumber());
		invoice.setCurrency(invoices.getCurrency());
		InvoiceStateEnum invoiceState = InvoiceStateEnum.fromValue(invoices.getInvoiceState()) ;
		invoice.setInvoiceState(invoiceState);
		invoice.setExpectedDate(invoices.getExpectedDate());
		invoice.setDueDate(invoices.getDueDate());
		invoice.setCreatedDate(invoices.getCreatedDate());
		return invoice;
	}

	private ClientReq mapToModel(ClientReq client, ClientEntity clientEntity) {
		client.setId(clientEntity.getId());
		client.setName(clientEntity.getName());
		client.setVatNumber(clientEntity.getVatNumber());
		client.setCf(clientEntity.getCf());
		client.setAddress(clientEntity.getAddress());
		client.setZipCode(clientEntity.getZipCode());
		client.setCity(clientEntity.getCity());
		client.setProvince(clientEntity.getProvince());
		client.setAddressExtra(clientEntity.getAddressExtra());
		client.setPec(clientEntity.getPec());
		client.setPaCode(clientEntity.getPaCode());
		client.setEmail(clientEntity.getEmail());
		client.setCountry(clientEntity.getCountry());
		return client;
	}

	private MerchantReq mapToModel(MerchantReq merchant, MerchantEntity merchantEntity) {
		merchant.setId(merchantEntity.getId());
		merchant.setName(merchantEntity.getName());	
		merchant.setVatNumber(merchantEntity.getVatNumber());
		merchant.setDirectUrl(merchantEntity.getDirectUrl());
		merchant.setAddressStreet(merchantEntity.getAddressStreet());
		merchant.setAddressCAP(merchantEntity.getAddressCAP());
		merchant.setAddressCity(merchantEntity.getAddressCity());
		merchant.setAddressProvince(merchantEntity.getAddressProvince());
		merchant.setAddressState(merchantEntity.getAddressState());
		merchant.setEmail(merchantEntity.getEmail());
		merchant.setBankName(merchantEntity.getBankName());
		merchant.setBankCompanyName(merchantEntity.getBankCompanyName());
		merchant.setIban(merchantEntity.getIban());
		merchant.setSwift(merchantEntity.getSwift());
		merchant.setIntegrations(merchantEntity.getIntegrations());
		merchant.setPhone(merchantEntity.getPhone());
		merchant.setWebsite(merchantEntity.getWebsite());
		return merchant;
	}

}
