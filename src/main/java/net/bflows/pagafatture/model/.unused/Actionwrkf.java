/*package net.bflows.pagafatture.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import net.bflows.pagafatture.model.Actiontemplate;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

*//**
 * Actionwrkf entity. @author the Bflows team
 *//*
@ApiModel(description = "Actionwrkf entity. @author the Bflows team")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-09-04T13:04:38.271714+02:00[Europe/Rome]")

public class Actionwrkf   {
  @JsonProperty("name")
  private String name;

  @JsonProperty("id")
  private Long id;

  @JsonProperty("owner")
  private String owner;

  @JsonProperty("templateAction")
  private Actiontemplate templateAction;

  @JsonProperty("triggerDays")
  private Integer triggerDays;

  *//**
   * Gets or Sets wrkfActionType
   *//*
  public enum WrkfActionTypeEnum {
    MANUAL_EMAIL("MANUAL_EMAIL"),
    
    AUTOMATIC_EMAIL("AUTOMATIC_EMAIL"),
    
    CALL("CALL");

    private String value;

    WrkfActionTypeEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static WrkfActionTypeEnum fromValue(String value) {
      for (WrkfActionTypeEnum b : WrkfActionTypeEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  @JsonProperty("wrkfActionType")
  private WrkfActionTypeEnum wrkfActionType;

  *//**
   * Gets or Sets wrkfTriggerType
   *//*
  public enum WrkfTriggerTypeEnum {
    AFTER_ISSUE_DATE("AFTER_ISSUE_DATE"),
    
    BEFORE_DUE_DATE("BEFORE_DUE_DATE"),
    
    AFTER_DUE_DATE("AFTER_DUE_DATE");

    private String value;

    WrkfTriggerTypeEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static WrkfTriggerTypeEnum fromValue(String value) {
      for (WrkfTriggerTypeEnum b : WrkfTriggerTypeEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  @JsonProperty("wrkfTriggerType")
  private WrkfTriggerTypeEnum wrkfTriggerType;

  public Actionwrkf name(String name) {
    this.name = name;
    return this;
  }

  *//**
   * Get name
   * @return name
  *//*
  @ApiModelProperty(required = true, value = "")
  @NotNull


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Actionwrkf id(Long id) {
    this.id = id;
    return this;
  }

  *//**
   * Get id
   * @return id
  *//*
  @ApiModelProperty(value = "")


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Actionwrkf owner(String owner) {
    this.owner = owner;
    return this;
  }

  *//**
   * Get owner
   * @return owner
  *//*
  @ApiModelProperty(value = "")


  public String getOwner() {
    return owner;
  }

  public void setOwner(String owner) {
    this.owner = owner;
  }

  public Actionwrkf templateAction(Actiontemplate templateAction) {
    this.templateAction = templateAction;
    return this;
  }

  *//**
   * Get templateAction
   * @return templateAction
  *//*
  @ApiModelProperty(value = "")

  @Valid

  public Actiontemplate getTemplateAction() {
    return templateAction;
  }

  public void setTemplateAction(Actiontemplate templateAction) {
    this.templateAction = templateAction;
  }

  public Actionwrkf triggerDays(Integer triggerDays) {
    this.triggerDays = triggerDays;
    return this;
  }

  *//**
   * Get triggerDays
   * minimum: 1
   * @return triggerDays
  *//*
  @ApiModelProperty(required = true, value = "")
  @NotNull

@Min(1)
  public Integer getTriggerDays() {
    return triggerDays;
  }

  public void setTriggerDays(Integer triggerDays) {
    this.triggerDays = triggerDays;
  }

  public Actionwrkf wrkfActionType(WrkfActionTypeEnum wrkfActionType) {
    this.wrkfActionType = wrkfActionType;
    return this;
  }

  *//**
   * Get wrkfActionType
   * @return wrkfActionType
  *//*
  @ApiModelProperty(required = true, value = "")
  @NotNull


  public WrkfActionTypeEnum getWrkfActionType() {
    return wrkfActionType;
  }

  public void setWrkfActionType(WrkfActionTypeEnum wrkfActionType) {
    this.wrkfActionType = wrkfActionType;
  }

  public Actionwrkf wrkfTriggerType(WrkfTriggerTypeEnum wrkfTriggerType) {
    this.wrkfTriggerType = wrkfTriggerType;
    return this;
  }

  *//**
   * Get wrkfTriggerType
   * @return wrkfTriggerType
  *//*
  @ApiModelProperty(required = true, value = "")
  @NotNull


  public WrkfTriggerTypeEnum getWrkfTriggerType() {
    return wrkfTriggerType;
  }

  public void setWrkfTriggerType(WrkfTriggerTypeEnum wrkfTriggerType) {
    this.wrkfTriggerType = wrkfTriggerType;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Actionwrkf actionwrkf = (Actionwrkf) o;
    return Objects.equals(this.name, actionwrkf.name) &&
        Objects.equals(this.id, actionwrkf.id) &&
        Objects.equals(this.owner, actionwrkf.owner) &&
        Objects.equals(this.templateAction, actionwrkf.templateAction) &&
        Objects.equals(this.triggerDays, actionwrkf.triggerDays) &&
        Objects.equals(this.wrkfActionType, actionwrkf.wrkfActionType) &&
        Objects.equals(this.wrkfTriggerType, actionwrkf.wrkfTriggerType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, id, owner, templateAction, triggerDays, wrkfActionType, wrkfTriggerType);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Actionwrkf {\n");
    
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    owner: ").append(toIndentedString(owner)).append("\n");
    sb.append("    templateAction: ").append(toIndentedString(templateAction)).append("\n");
    sb.append("    triggerDays: ").append(toIndentedString(triggerDays)).append("\n");
    sb.append("    wrkfActionType: ").append(toIndentedString(wrkfActionType)).append("\n");
    sb.append("    wrkfTriggerType: ").append(toIndentedString(wrkfTriggerType)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  *//**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   *//*
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

*/