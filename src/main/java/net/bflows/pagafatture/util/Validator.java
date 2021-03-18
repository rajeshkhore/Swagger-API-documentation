package net.bflows.pagafatture.util;

import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.bflows.pagafatture.config.Translator;
import net.bflows.pagafatture.config.jwt.TokenProvider;
import net.bflows.pagafatture.entities.MerchantEntity;
import net.bflows.pagafatture.repositories.MerchantRepository;
import net.bflows.pagafatture.web.rest.errors.ResourceNotFoundException;
import net.bflows.pagafatture.web.rest.errors.Status;
import net.bflows.pagafatture.web.rest.errors.UnauthorizedAccessException;

@Component
public class Validator {

	public static final String INVALID_MERCHANT = "invalid_merchant";
	public static final String INVALID_USER_MERCHANT = "invalid_user_merchant";

	@Autowired
	private MerchantRepository merchantRepository;

	@Autowired
	private TokenProvider tokenProvider;

	public Map<String, Object> getUserInfoFromToken(HttpServletRequest request) {
		return tokenProvider.getUserInfoFromToken(request);
	}

	private MerchantEntity validateMerchantId(Long merchantId) {
		Optional<MerchantEntity> merchantEntity = merchantRepository.findById(merchantId);

		if (!merchantEntity.isPresent()) {
			throw new ResourceNotFoundException(Status.NOT_FOUND, Translator.toLocale(INVALID_MERCHANT));
		}
		return merchantEntity.get();
	}

	public MerchantEntity validateUserMerchantId(Map<String, Object> userInfo, Long merchantId) {
		MerchantEntity merchantEntity = validateMerchantId(merchantId);
		Long userLoggedInMerchantId = getUserMerchantId(userInfo);
		if (userLoggedInMerchantId != null && !merchantId.equals(userLoggedInMerchantId)) {
			throw new UnauthorizedAccessException(Status.UNAUTHORIZED, Translator.toLocale(INVALID_USER_MERCHANT));
		}

		return merchantEntity;
	}

	public Long getUserMerchantId(Map<String, Object> userInfo) {

		Long userLoggedInMerchantId = null;
		if (userInfo.get("merchantId") != null) {
			userLoggedInMerchantId = Long.valueOf(userInfo.get("merchantId").toString());
		}
		return userLoggedInMerchantId;
	}
}
