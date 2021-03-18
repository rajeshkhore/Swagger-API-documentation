package net.bflows.pagafatture.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.Base64;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Utils {
	
	public static final Logger log = LoggerFactory.getLogger(Utils.class);
	
	public static String decodeBase64String(String decodedString) {
        if (StringUtils.isBlank(decodedString)) {
            return decodedString;
        }
        byte[] decode = Base64.getDecoder().decode(decodedString.getBytes(StandardCharsets.UTF_8));
        return new String(decode, StandardCharsets.UTF_8);
    }
	public static String encodeBase64String(String encodedString) {
        if (StringUtils.isBlank(encodedString)) {
            return encodedString;
        }
        byte[] decode = Base64.getEncoder().encode(encodedString.getBytes(StandardCharsets.UTF_8));
        return new String(decode, StandardCharsets.UTF_8);
    }
	
	public static Double formatDecimal(Double number) { 
		if(number == null) {
			return null;
		}
		DecimalFormat df = new DecimalFormat("#.##");
		df.setRoundingMode(RoundingMode.FLOOR);
		return Double.valueOf(df.format(number));
	}
	
	public static BigDecimal formatDecimalForBigDecimal(BigDecimal number) {
		return number.setScale(2, RoundingMode.CEILING);
	}
	
	public static Map<String, Object> convertIntoJson(String jsonString) {
		try {
			ObjectMapper mapper= new ObjectMapper();
			mapper.configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true);
			return mapper.readValue(jsonString, Map.class);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return null;
	}

	public static String convertMapIntoJsonString(Map<String, Object> map) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(map);
		} catch (JsonProcessingException e) {
			log.error(e.getMessage());
		}
		return null;
	}
}
