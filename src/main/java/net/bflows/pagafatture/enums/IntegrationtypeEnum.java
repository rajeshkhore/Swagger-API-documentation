package net.bflows.pagafatture.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Define the integration type
 */
public enum IntegrationtypeEnum {
  APIKEY("APIKEY"),
  
  OAUTH2("OAUTH2");

  private String value;

  IntegrationtypeEnum(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }


  @JsonCreator
  public static IntegrationtypeEnum fromValue(String value) {
    for (IntegrationtypeEnum b : IntegrationtypeEnum.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}
