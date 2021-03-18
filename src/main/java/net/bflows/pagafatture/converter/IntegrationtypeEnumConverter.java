package net.bflows.pagafatture.converter;

import javax.persistence.AttributeConverter;

import net.bflows.pagafatture.enums.IntegrationtypeEnum;

import org.springframework.stereotype.Component;

@Component
public class IntegrationtypeEnumConverter implements AttributeConverter<IntegrationtypeEnum, String>{

	@Override
	public String convertToDatabaseColumn(IntegrationtypeEnum attribute) {
		return attribute.toString();
	}

	@Override
	public IntegrationtypeEnum convertToEntityAttribute(String dbData) {
		return IntegrationtypeEnum.valueOf(dbData);
	}


}
