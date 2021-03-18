package net.bflows.pagafatture.dao;

import java.util.Optional;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.bflows.pagafatture.config.Translator;
import net.bflows.pagafatture.entities.MerchantEntity;
import net.bflows.pagafatture.model.MerchantReq;
import net.bflows.pagafatture.model.MerchantReq.MerchantstateEnum;
import net.bflows.pagafatture.repositories.MerchantRepository;
import net.bflows.pagafatture.web.rest.errors.ResourceNotFoundException;
import net.bflows.pagafatture.web.rest.errors.Status;

@Component
public class MerchantDAO {

    @Autowired
    MerchantRepository merchantRepository;

    private static final String MERCHANT_NOT_FOUND="merchant_not_found";
    private <T> void setIfNotNull(final Consumer<T> setter, final T value) {
        if (value != null) {
            setter.accept(value);
        }
    }

	public MerchantReq createMerchant(MerchantReq merchant) {
		MerchantEntity merchantEntity = converMerchantToMerchantEntity(merchant);
		merchantEntity = merchantRepository.save(merchantEntity);

		return convertMerchantEntityToMerchant(merchantEntity);
	}

	private MerchantEntity converMerchantToMerchantEntity(MerchantReq merchant) {
		MerchantEntity merchantEntity = new MerchantEntity();
		merchantEntity.setDirectUrl( merchant.getDirectUrl());
		merchantEntity.setEmail(merchant.getEmail());
		merchantEntity.setName( merchant.getName());
		merchantEntity.setVatNumber( merchant.getVatNumber());
		merchantEntity.setMerchantstate(MerchantstateEnum.ACTIVE.getValue());
		merchantEntity.setAddressCity( merchant.getAddressCity());
		merchantEntity.setAddressProvince( merchant.getAddressProvince());
		merchantEntity.setAddressState( merchant.getAddressState());
		merchantEntity.setAddressCAP( merchant.getAddressCAP());
		merchantEntity.setAddressStreet( merchant.getAddressStreet());
		merchantEntity.setDefaultPaymentDays(
				merchant.getDefaultPaymentDays() == null ? 60 : merchant.getDefaultPaymentDays());
		merchantEntity.setPhone(merchant.getPhone());
		merchantEntity.setBankCompanyName(merchant.getBankCompanyName());
		merchantEntity.setBankName(merchant.getBankName());
		merchantEntity.setSwift(merchant.getSwift());
		merchantEntity.setIban(merchant.getIban());
		merchantEntity.setIntegrations(merchant.getIntegrations());
		merchantEntity.setWebsite(merchant.getWebsite());
		return merchantEntity;
	}

	public MerchantReq updateMarchant(Long id, MerchantReq merchant) {
		Optional<MerchantEntity> dbMerchantEntity = merchantRepository.findById(id);
		if (!dbMerchantEntity.isPresent()) {
			throw new ResourceNotFoundException(Status.NOT_FOUND, Translator.toLocale(""));
		}
		MerchantEntity merchantEntity = dbMerchantEntity.get();
		setIfNotNull(merchantEntity::setDirectUrl, merchant.getDirectUrl());
		setIfNotNull(merchantEntity::setEmail, merchant.getEmail());
		setIfNotNull(merchantEntity::setName, merchant.getName());
		setIfNotNull(merchantEntity::setVatNumber, merchant.getVatNumber());
		setIfNotNull(merchantEntity::setAddressCity, merchant.getAddressCity());
		setIfNotNull(merchantEntity::setAddressCAP, merchant.getAddressCAP());
		setIfNotNull(merchantEntity::setAddressState, merchant.getAddressState());
		setIfNotNull(merchantEntity::setAddressStreet, merchant.getAddressStreet());
		setIfNotNull(merchantEntity::setDefaultPaymentDays, merchant.getDefaultPaymentDays());
		setIfNotNull(merchantEntity::setPhone, merchant.getPhone());
		setIfNotNull(merchantEntity::setBankCompanyName, merchant.getBankCompanyName());
		setIfNotNull(merchantEntity::setBankName, merchant.getBankCompanyName());
		setIfNotNull(merchantEntity::setIban, merchant.getIban());
		setIfNotNull(merchantEntity::setIntegrations,merchant.getIntegrations());
		setIfNotNull(merchantEntity::setSwift, merchant.getSwift());
		setIfNotNull(merchantEntity::setWebsite, merchant.getWebsite());
		
		

		merchantEntity = merchantRepository.save(merchantEntity);
		return convertMerchantEntityToMerchant(merchantEntity);
	}

	public MerchantReq getMerchantById(Long id) {
		Optional<MerchantEntity> merchantEntity = merchantRepository.findById(id);
		if (!merchantEntity.isPresent()) {
			throw new ResourceNotFoundException(Status.NOT_FOUND, Translator.toLocale(MERCHANT_NOT_FOUND));
		}
		return convertMerchantEntityToMerchant(merchantEntity.get());
	}

	public Boolean deleteMerchant(Long id) {
		Optional<MerchantEntity> merchantEntity = merchantRepository.findById(id);
		if (!merchantEntity.isPresent()) {
			throw new ResourceNotFoundException(Status.NOT_FOUND, Translator.toLocale(MERCHANT_NOT_FOUND));
		}
		merchantRepository.delete(merchantEntity.get());
		return true ;
	}

	private MerchantReq convertMerchantEntityToMerchant(MerchantEntity merchantEntity) {
		MerchantReq merchant = new MerchantReq();
		merchant.setId(merchantEntity.getId());
		merchant.setName(merchantEntity.getName());
		merchant.setVatNumber(merchantEntity.getVatNumber());
		merchant.setDirectUrl(merchantEntity.getDirectUrl());
		merchant.setAddressCity(merchantEntity.getAddressCity());
		merchant.setAddressCAP(merchantEntity.getAddressCAP());
		merchant.setAddressProvince(merchantEntity.getAddressProvince());
		merchant.setAddressState(merchantEntity.getAddressState());
		merchant.setAddressStreet(merchantEntity.getAddressStreet());
		merchant.setEmail(merchantEntity.getEmail());
		merchant.setIntegrations(merchantEntity.getIntegrations());
		merchant.setDefaultPaymentDays(merchantEntity.getDefaultPaymentDays());
		merchant.setPhone(merchantEntity.getPhone());
        merchant.setIban(merchantEntity.getIban());
        merchant.setBankCompanyName(merchantEntity.getBankCompanyName());
        merchant.setBankName(merchantEntity.getBankName());
        merchant.setSwift(merchantEntity.getSwift());
        merchant.setWebsite(merchantEntity.getWebsite());
		return merchant;
	}

}
