package net.bflows.pagafatture.web.rest.api.impl;


import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.bflows.pagafatture.config.Translator;
import net.bflows.pagafatture.model.util.Response;
import net.bflows.pagafatture.model.widget.WidgetsResponseBean;
import net.bflows.pagafatture.service.DashboardService;
import net.bflows.pagafatture.web.rest.api.DashboardApi;
import net.bflows.pagafatture.web.rest.errors.ResourceNotFoundException;
import net.bflows.pagafatture.web.rest.errors.Status;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-09-22T12:57:09.877+02:00[Europe/Rome]")
@RestController
@RequestMapping("${openapi.swaggerBflowsMiddleware.base-path:/v1}")
public class DashboardApiController implements DashboardApi {

	public final Logger log = LoggerFactory.getLogger(DashboardApi.class);

	@Autowired
	DashboardService dashboardService;

	@Override
	public ResponseEntity<Response<WidgetsResponseBean>> getWidgetsByMerchantId(HttpServletRequest request, Long merchantId) {

		try {
		    WidgetsResponseBean finalResponse = dashboardService.getWidgets(request, merchantId);
			return ResponseEntity.status(HttpStatus.OK).body(
					new Response<WidgetsResponseBean>(finalResponse, Status.SUCCESS, Translator.toLocale("get_widgets_sc_msg")));

		} catch (ResourceNotFoundException ex) {
			log.error(ex.getMessage());
			throw ex;
		}
	}



}
