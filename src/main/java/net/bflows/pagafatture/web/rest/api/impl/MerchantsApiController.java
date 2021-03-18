package net.bflows.pagafatture.web.rest.api.impl;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;

import net.bflows.pagafatture.config.Translator;
import net.bflows.pagafatture.model.MerchantReq;
import net.bflows.pagafatture.model.util.Response;
import net.bflows.pagafatture.service.MerchantService;
import net.bflows.pagafatture.web.rest.api.MerchantsApi;
import net.bflows.pagafatture.web.rest.errors.ResourceNotFoundException;
import net.bflows.pagafatture.web.rest.errors.Status;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-08-26T12:57:09.877+02:00[Europe/Rome]")


//TODO: add bankName, bankCompanyName, iban and swift for merchant CRUD -> right now it's not checking for those values and not saving to DB 

@RestController
@RequestMapping("${openapi.swaggerBflowsMiddleware.base-path:/v1}")
public class MerchantsApiController implements MerchantsApi {
    
    public final Logger log = LoggerFactory.getLogger(MerchantsApiController.class);
	
	@Autowired
    private MerchantService merchantService;
	
	@Override
	public ResponseEntity<Response<MerchantReq>> createMerchant(MerchantReq merchant) {


				if (merchant.getVatNumber() == null)
					 return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
			                 new Response<MerchantReq>( null, Status.VALIDATION_ERROR, Translator.toLocale("vatNumber_empty")));

				merchant = merchantService.createMerchant(merchant);
			    return ResponseEntity.status(HttpStatus.OK).body(
			            new Response<MerchantReq>( merchant, Status.SUCCESS, Translator.toLocale("create_merchant_se_msg")));
		
	
	}

	@Override
	public ResponseEntity<Response<Void>> deleteMerchant(HttpServletRequest httpServletRequest, Long id) {
		try {
			merchantService.deleteMerchant(httpServletRequest, id);
			
			return ResponseEntity.status(HttpStatus.OK).body(
			        new Response<Void>(null, Status.SUCCESS, Translator.toLocale("delete_merchant_se_msg")));
		} catch (ResourceNotFoundException ex) {
            log.error(ex.getMessage());
            throw ex;
        }
	}



	@Override
	public ResponseEntity<Response<MerchantReq>> getMerchantById(HttpServletRequest httpServletRequest, Long id) {
		try {
			MerchantReq merchant = merchantService.getMerchantById(httpServletRequest, id);

			return ResponseEntity.status(HttpStatus.OK).body(
			         new Response<MerchantReq>( merchant, Status.SUCCESS, Translator.toLocale("get_merchant_se_msg")));
		} catch (ResourceNotFoundException ex) {
            log.error(ex.getMessage());
            throw ex;
        }
	}






	@Override
	public ResponseEntity<Response<MerchantReq>> updateMerchant(HttpServletRequest httpServletRequest, Long id, MerchantReq merchant) {
			try {
				merchant = merchantService.updateMerchant(httpServletRequest, id, merchant);
				
				
				return ResponseEntity.status(HttpStatus.OK).body(
				         new Response<MerchantReq>( merchant, Status.SUCCESS, Translator.toLocale("update_merchant_se_msg")));
			} catch (ResourceNotFoundException ex) {
	            log.error(ex.getMessage());
	            throw ex;
	        }
		
	}



	private final NativeWebRequest request;

	@org.springframework.beans.factory.annotation.Autowired
	public MerchantsApiController(NativeWebRequest request) {
		this.request = request;
	}

	@Override
	public Optional<NativeWebRequest> getRequest() {
		return Optional.ofNullable(request);
	}
	
	
  

}
