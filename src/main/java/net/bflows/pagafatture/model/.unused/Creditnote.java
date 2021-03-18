/*package net.bflows.pagafatture.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import net.bflows.pagafatture.model.InvoiceReq;
import net.bflows.pagafatture.model.Merchant;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

*//**
 * Creditnote Entity
 *//*
@ApiModel(description = "Creditnote Entity")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-09-04T13:04:38.271714+02:00[Europe/Rome]")

public class Creditnote   {
  @JsonProperty("id")
  private Long id;

  @JsonProperty("credinoteDate")
  private String credinoteDate;

  @JsonProperty("creditnoteNumber")
  private String creditnoteNumber;

  @JsonProperty("supplier")
  private String supplier;

  @JsonProperty("merchant")
  private Merchant merchant;

  @JsonProperty("amount")
  private BigDecimal amount;

  @JsonProperty("invoices")
  @Valid
  private List<InvoiceReq> invoices = null;

  public Creditnote id(Long id) {
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

  public Creditnote credinoteDate(String credinoteDate) {
    this.credinoteDate = credinoteDate;
    return this;
  }

  *//**
   * Get credinoteDate
   * @return credinoteDate
  *//*
  @ApiModelProperty(value = "")


  public String getCredinoteDate() {
    return credinoteDate;
  }

  public void setCredinoteDate(String credinoteDate) {
    this.credinoteDate = credinoteDate;
  }

  public Creditnote creditnoteNumber(String creditnoteNumber) {
    this.creditnoteNumber = creditnoteNumber;
    return this;
  }

  *//**
   * Get creditnoteNumber
   * @return creditnoteNumber
  *//*
  @ApiModelProperty(required = true, value = "")
  @NotNull


  public String getCreditnoteNumber() {
    return creditnoteNumber;
  }

  public void setCreditnoteNumber(String creditnoteNumber) {
    this.creditnoteNumber = creditnoteNumber;
  }

  public Creditnote supplier(String supplier) {
    this.supplier = supplier;
    return this;
  }

  *//**
   * Get supplier
   * @return supplier
  *//*
  @ApiModelProperty(value = "")


  public String getSupplier() {
    return supplier;
  }

  public void setSupplier(String supplier) {
    this.supplier = supplier;
  }

  public Creditnote merchant(Merchant merchant) {
    this.merchant = merchant;
    return this;
  }

  *//**
   * Get merchant
   * @return merchant
  *//*
  @ApiModelProperty(value = "")

  @Valid

  public Merchant getMerchant() {
    return merchant;
  }

  public void setMerchant(Merchant merchant) {
    this.merchant = merchant;
  }

  public Creditnote amount(BigDecimal amount) {
    this.amount = amount;
    return this;
  }

  *//**
   * Get amount
   * @return amount
  *//*
  @ApiModelProperty(required = true, value = "")
  @NotNull

  @Valid

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public Creditnote invoices(List<InvoiceReq> invoices) {
    this.invoices = invoices;
    return this;
  }

  public Creditnote addInvoicesItem(InvoiceReq invoicesItem) {
    if (this.invoices == null) {
      this.invoices = new ArrayList<>();
    }
    this.invoices.add(invoicesItem);
    return this;
  }

  *//**
   * Get invoices
   * @return invoices
  *//*
  @ApiModelProperty(value = "")

  @Valid

  public List<InvoiceReq> getInvoices() {
    return invoices;
  }

  public void setInvoices(List<InvoiceReq> invoices) {
    this.invoices = invoices;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Creditnote creditnote = (Creditnote) o;
    return Objects.equals(this.id, creditnote.id) &&
        Objects.equals(this.credinoteDate, creditnote.credinoteDate) &&
        Objects.equals(this.creditnoteNumber, creditnote.creditnoteNumber) &&
        Objects.equals(this.supplier, creditnote.supplier) &&
        Objects.equals(this.merchant, creditnote.merchant) &&
        Objects.equals(this.amount, creditnote.amount) &&
        Objects.equals(this.invoices, creditnote.invoices);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, credinoteDate, creditnoteNumber, supplier, merchant, amount, invoices);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Creditnote {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    credinoteDate: ").append(toIndentedString(credinoteDate)).append("\n");
    sb.append("    creditnoteNumber: ").append(toIndentedString(creditnoteNumber)).append("\n");
    sb.append("    supplier: ").append(toIndentedString(supplier)).append("\n");
    sb.append("    merchant: ").append(toIndentedString(merchant)).append("\n");
    sb.append("    amount: ").append(toIndentedString(amount)).append("\n");
    sb.append("    invoices: ").append(toIndentedString(invoices)).append("\n");
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