package il.wapp.app.validation;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import il.wapp.app.exception.*;
import org.apache.commons.lang3.*;
import org.apache.commons.lang3.math.*;

import java.util.*;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.util.CollectionUtils.isEmpty;

public class BaseEntityValidator {

	protected void parseErrors(List<AttrValidationError> errors, ObjectMapper jacksonObjectMapper) {
		String errorString = null;
		try {
			errorString = jacksonObjectMapper.writeValueAsString(errors);
		} catch (JsonProcessingException e) {
			errors.add(new AttrValidationError("parseError", e.getMessage()));
		}
		if (!errors.isEmpty()) {

			throw new AttrValidationErrorException(errorString);
		}
	}

	protected boolean checkMaxLength(String attrValue, String attr, int maxLength, List<AttrValidationError> errors) {
		if (attrValue != null && attrValue.length() > maxLength) {
			errors.add(new AttrValidationError(attr, getMaxLengthErrorText(maxLength)));
			return false;
		}
		return true;
	}

	protected boolean checkRequiredList(List<?> attrValue, String attr, List<AttrValidationError> errors) {
		if (isEmpty(attrValue)) {
			errors.add(new AttrValidationError(attr, getRequiredErrorText()));
			return false;
		}
		return true;
	}

	protected boolean checkRequired(String attrValue, String attr, List<AttrValidationError> errors) {
		if (isBlank(attrValue)) {
			errors.add(new AttrValidationError(attr, getRequiredErrorText()));
			return false;
		}
		return true;
	}

	protected boolean checkRequired(Object attrValue, String attr, List<AttrValidationError> errors) {
		if (attrValue == null) {
			errors.add(new AttrValidationError(attr, getRequiredErrorText()));
			return false;
		}
		return true;
	}

	protected boolean checkIsInteger(String attrValue, String attr, List<AttrValidationError> errors) {
		if (!StringUtils.isNumeric(attrValue)) {
			errors.add(new AttrValidationError(attr, getNonIntegerNumberErrorText()));
			return false;
		}
		return true;
	}

	protected boolean checkIsNumber(String attrValue, String attr, List<AttrValidationError> errors) {
		if (!NumberUtils.isParsable(attrValue)) {
			errors.add(new AttrValidationError(attr, getNonNumberErrorText()));
			return false;
		}
		return true;
	}

	protected boolean checkIsNonNegativeNumber(Double attrValue, String attr, List<AttrValidationError> errors) {
		if (attrValue == null) {
			return false;
		}

		if (attrValue < 0) {
			errors.add(new AttrValidationError(attr, getNegativeNumberErrorText()));
			return false;
		}
		return true;
	}

	protected boolean checkIsNumberBelow(Number attrValue, Number upperLimit, String attr, List<AttrValidationError> errors) {
		if (attrValue == null) {
			return false;
		}

		if (attrValue.doubleValue() > upperLimit.doubleValue()) {
			errors.add(new AttrValidationError(attr, getOverflowNumberErrorText(upperLimit)));
			return false;
		}
		return true;
	}

	protected String getNonIntegerNumberErrorText() {
		return "Значение должно быть целым числом";
	}

	protected String getNonNumberErrorText() {
		return "Значение должно быть числом";
	}

	protected String getNegativeNumberErrorText() {
		return "Значение должно быть положительным числом или нулём";
	}

	protected String getOverflowNumberErrorText(Number upperLimit) {
		return "Максимальное значение (" + upperLimit + ") превышено";
	}

	protected String getRequiredErrorText() {
		return "Обязательно для заполнения";
	}

	protected String getMaxLengthErrorText(int maxLength) {
		return "Длина превышает допустимое значение: " + maxLength;
	}

	protected String getUniqueErrorText() {
		return "Уже существует карточка с таким названием";
	}

	protected String getIpFormatErrorText() {
		return "Неверный формат IP. Требуется XXX.XXX.XXX.XXX. Диапазон значений: 0-255";
	}
}
