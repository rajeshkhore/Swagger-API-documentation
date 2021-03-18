package net.bflows.pagafatture.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum DashboardEnums {
	TOTAL_UNPAID("totalUnpaid"), MAIN_DEBTORS("mainDebtors"),AVERAGE_DAYS("averageDays"),AGING_BALANCE("agingBalance") , ACTION_WIDGETS("actionWidgets");
    private String value;

	DashboardEnums(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

}
