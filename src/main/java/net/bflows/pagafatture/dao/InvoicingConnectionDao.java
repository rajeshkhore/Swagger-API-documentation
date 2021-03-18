package net.bflows.pagafatture.dao;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import net.bflows.pagafatture.config.Translator;
import net.bflows.pagafatture.entities.InvoicingConnectionEntity;
import net.bflows.pagafatture.entities.InvoicingEntity;
import net.bflows.pagafatture.entities.MerchantEntity;
import net.bflows.pagafatture.model.InvoicingConnectionReq;
import net.bflows.pagafatture.repositories.InvoicingConnectionRepository;
import net.bflows.pagafatture.repositories.InvoicingRepository;
import net.bflows.pagafatture.repositories.MerchantRepository;
import net.bflows.pagafatture.repositories.UserRepository;
import net.bflows.pagafatture.util.DateTimeUtil;
import net.bflows.pagafatture.util.Validator;
import net.bflows.pagafatture.web.rest.errors.CustomValidationException;
import net.bflows.pagafatture.web.rest.errors.ResourceNotFoundException;
import net.bflows.pagafatture.web.rest.errors.Status;

@Component
public class InvoicingConnectionDao {

	@Autowired
	MerchantRepository merchantRepository;

	@Autowired
	InvoicingRepository invoicingRepository;

	@Autowired
	InvoicingConnectionRepository invoicingConnectionRepository;
	
	@Autowired
	UserRepository userRepository;
	
	 @Value("${import_scheduler_url}")
	 private String importSchedulerUrl;
	
	@Autowired
	Validator validator;
	
	public final Logger log = LoggerFactory.getLogger(InvoicingConnectionDao.class);

	public InvoicingConnectionEntity createInvoicingConnection(MerchantEntity merchant, InvoicingConnectionReq body) {

		RestTemplate restTemplate = new RestTemplate();

		Map<String, String> request = new HashMap<>();

		String url = "";
		boolean updateMerchant = false;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		if (body.getTypeId() == null) {
			throw new CustomValidationException(Status.VALIDATION_ERROR, "Invalid type_id");
		}
		
		InvoicingConnectionEntity dbInvoicingConnectionEntity = invoicingConnectionRepository.findByMerchantEntityId(merchant.getId());

		if (dbInvoicingConnectionEntity != null) {
			throw new CustomValidationException(Status.VALIDATION_ERROR, Translator.toLocale("connection_already_exists"));
		}
		Optional<InvoicingEntity> invoicingEntity = invoicingRepository.findById(body.getTypeId());

		if (!invoicingEntity.isPresent()) {
			throw new ResourceNotFoundException(Status.NOT_FOUND, "Invalid type_id");
		}
		InvoicingEntity invoicing = invoicingEntity.get();

		if (invoicing.getId().equals(1l)) {
			url = invoicing.getLink();

			request.put("api_uid", body.getApiUid());
			request.put("api_key", body.getApiKey());
		}
		if (invoicing.getId().equals(2l)) {
			url = invoicing.getLink();
			request.put("api_uid", body.getApiUid());
			request.put("api_key", body.getApiKey());
		}

		 ResponseEntity<Map> response;
			try {
				response = restTemplate.postForEntity(url, request, Map.class);
				Map<String, Object> responseBody=null;
				
				if(response != null) {
					responseBody= response.getBody();
				}
				if(responseBody == null) {
					responseBody= new HashMap<>();
				}
				if (response.getStatusCode() == HttpStatus.OK && response.hasBody()){
					if (response.getBody().containsKey("error")) {
						return null;
					}
					if( responseBody.containsKey("success")) {
						updateMerchant= true;
						
					}
					
					

					if( responseBody.containsKey("token")){
						updateMerchant= true;
					}

				}
			} catch (Exception e) {
				log.error(e.getMessage());
				return null;
			}

		if (updateMerchant) {
			triggerImportScheduler(merchant.getId());
			merchant.setIntegrations(true);
			merchantRepository.save(merchant);

		}

		InvoicingConnectionEntity invoicingConnectionEntity = new InvoicingConnectionEntity();
		invoicingConnectionEntity.setApiKey(body.getApiKey());
		invoicingConnectionEntity.setApiUid(body.getApiUid());
		invoicingConnectionEntity.setTypeId(body.getTypeId());
		invoicingConnectionEntity.setMerchantEntity(merchant);
		invoicingConnectionEntity.setCreatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		invoicingConnectionEntity.setUpdatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		invoicingConnectionEntity.setInvoicing(invoicing);
		invoicingConnectionRepository.save(invoicingConnectionEntity);

		return invoicingConnectionEntity;

	}

	private void triggerImportScheduler(Long id) {
		try {
			String url = importSchedulerUrl + id;
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			headers.add("user-agent",
					"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");

			HttpEntity<Map<String, Object>> entity;
			entity = new HttpEntity<>(headers);
			RestTemplate restTemplate = new RestTemplate();
			restTemplate.exchange(url, HttpMethod.POST, entity, new ParameterizedTypeReference<Map<String, Object>>() {
			});
		} catch (RestClientException e) {
			log.error(e.getMessage());
		}

	}

	public InvoicingConnectionEntity getInvoicingConnectionByMerchantId(Long merchantId) {
		InvoicingConnectionEntity dbInvoicingConnectionEntity = invoicingConnectionRepository.findByMerchantEntityId(merchantId);

		if (dbInvoicingConnectionEntity == null) {
			throw new ResourceNotFoundException(Status.NOT_FOUND, Translator.toLocale("connection_not_found"));
		}
        return dbInvoicingConnectionEntity;
	}


	public List<InvoicingConnectionEntity> getInvoicingConnection(Long typeId, Map<String, Object> userInfo) {
		List<InvoicingConnectionEntity> invoicingConnectionEntities = null;
		
		if(typeId != null) {
			invoicingConnectionEntities= invoicingConnectionRepository.findByTypeIdAndMerchantEntityId(typeId,validator.getUserMerchantId(userInfo));
			 return invoicingConnectionEntities;
         }
		
		invoicingConnectionEntities = invoicingConnectionRepository.findAll();

        return invoicingConnectionEntities;
	}
}
