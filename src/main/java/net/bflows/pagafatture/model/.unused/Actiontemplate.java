/*package net.bflows.pagafatture.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

*//**
 * Template Entity 
 *//*
@ApiModel(description = "Template Entity ")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-09-04T13:04:38.271714+02:00[Europe/Rome]")

public class Actiontemplate   {
  @JsonProperty("id")
  private Long id;

  @JsonProperty("name")
  private String name;

  @JsonProperty("mailto")
  private String mailto;

  @JsonProperty("cc")
  private String cc;

  @JsonProperty("bcc")
  private String bcc;

  @JsonProperty("subject")
  private String subject;

  @JsonProperty("message")
  private String message;

  @JsonProperty("sendAuto")
  private Boolean sendAuto = false;

  @JsonProperty("emailsender")
  private String emailsender;

  public Actiontemplate id(Long id) {
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

  public Actiontemplate name(String name) {
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

  public Actiontemplate mailto(String mailto) {
    this.mailto = mailto;
    return this;
  }

  *//**
   * Get mailto
   * @return mailto
  *//*
  @ApiModelProperty(value = "")


  public String getMailto() {
    return mailto;
  }

  public void setMailto(String mailto) {
    this.mailto = mailto;
  }

  public Actiontemplate cc(String cc) {
    this.cc = cc;
    return this;
  }

  *//**
   * Get cc
   * @return cc
  *//*
  @ApiModelProperty(value = "")


  public String getCc() {
    return cc;
  }

  public void setCc(String cc) {
    this.cc = cc;
  }

  public Actiontemplate bcc(String bcc) {
    this.bcc = bcc;
    return this;
  }

  *//**
   * Get bcc
   * @return bcc
  *//*
  @ApiModelProperty(value = "")


  public String getBcc() {
    return bcc;
  }

  public void setBcc(String bcc) {
    this.bcc = bcc;
  }

  public Actiontemplate subject(String subject) {
    this.subject = subject;
    return this;
  }

  *//**
   * Get subject
   * @return subject
  *//*
  @ApiModelProperty(value = "")


  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public Actiontemplate message(String message) {
    this.message = message;
    return this;
  }

  *//**
   * Get message
   * @return message
  *//*
  @ApiModelProperty(value = "")


  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public Actiontemplate sendAuto(Boolean sendAuto) {
    this.sendAuto = sendAuto;
    return this;
  }

  *//**
   * Get sendAuto
   * @return sendAuto
  *//*
  @ApiModelProperty(value = "")


  public Boolean getSendAuto() {
    return sendAuto;
  }

  public void setSendAuto(Boolean sendAuto) {
    this.sendAuto = sendAuto;
  }

  public Actiontemplate emailsender(String emailsender) {
    this.emailsender = emailsender;
    return this;
  }

  *//**
   * Get emailsender
   * @return emailsender
  *//*
  @ApiModelProperty(value = "")


  public String getEmailsender() {
    return emailsender;
  }

  public void setEmailsender(String emailsender) {
    this.emailsender = emailsender;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Actiontemplate actiontemplate = (Actiontemplate) o;
    return Objects.equals(this.id, actiontemplate.id) &&
        Objects.equals(this.name, actiontemplate.name) &&
        Objects.equals(this.mailto, actiontemplate.mailto) &&
        Objects.equals(this.cc, actiontemplate.cc) &&
        Objects.equals(this.bcc, actiontemplate.bcc) &&
        Objects.equals(this.subject, actiontemplate.subject) &&
        Objects.equals(this.message, actiontemplate.message) &&
        Objects.equals(this.sendAuto, actiontemplate.sendAuto) &&
        Objects.equals(this.emailsender, actiontemplate.emailsender);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, mailto, cc, bcc, subject, message, sendAuto, emailsender);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Actiontemplate {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    mailto: ").append(toIndentedString(mailto)).append("\n");
    sb.append("    cc: ").append(toIndentedString(cc)).append("\n");
    sb.append("    bcc: ").append(toIndentedString(bcc)).append("\n");
    sb.append("    subject: ").append(toIndentedString(subject)).append("\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
    sb.append("    sendAuto: ").append(toIndentedString(sendAuto)).append("\n");
    sb.append("    emailsender: ").append(toIndentedString(emailsender)).append("\n");
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