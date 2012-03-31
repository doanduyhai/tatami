package fr.ippon.tatami.web.rest;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public abstract class AbstractRESTController
{
	private final Logger log = LoggerFactory.getLogger(AbstractRESTController.class);

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseBody
	public String handleFunctionalException(MethodArgumentNotValidException ex, HttpServletRequest request)
	{
		log.error(" Validation exception raised : " + ex.getMessage());
		StringBuilder errorBuffer = new StringBuilder();
		for (FieldError fieldError : ex.getBindingResult().getFieldErrors())
		{
			errorBuffer.append(fieldError.getDefaultMessage()).append("<br/>");
		}
		return errorBuffer.toString();
	}
}
