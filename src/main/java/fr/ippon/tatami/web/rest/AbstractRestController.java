package fr.ippon.tatami.web.rest;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.stereotype.Controller;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import fr.ippon.tatami.exception.FunctionalException;
import fr.ippon.tatami.web.converter.JacksonViewAwareHttpMessageConverter;
import fr.ippon.tatami.web.json.view.JacksonView;

/**
 * @author DuyHai DOAN
 */
@Controller
public abstract class AbstractRestController
{
	private final Logger log = LoggerFactory.getLogger(AbstractRestController.class);

	protected JacksonViewAwareHttpMessageConverter jacksonConverter;

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public String handleValidationException(MethodArgumentNotValidException ex, HttpServletResponse response) throws IOException
	{
		log.error(" Validation exception raised : " + ex.getMessage());
		StringBuilder errorBuffer = new StringBuilder();
		for (FieldError fieldError : ex.getBindingResult().getFieldErrors())
		{
			errorBuffer.append(fieldError.getDefaultMessage()).append("<br/>");
		}

		return errorBuffer.toString();
	}

	@ExceptionHandler(FunctionalException.class)
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public String handleFunctionalException(FunctionalException ex, HttpServletResponse response) throws IOException
	{
		log.error(" Functional exception raised : " + ex.getMessage(), ex);

		return ex.getMessage();
	}

	protected void writeWithView(Object object, HttpServletResponse response, Class<? extends JacksonView> view)
	{
		if (this.jacksonConverter.canWrite(object.getClass(), MediaType.APPLICATION_JSON))
		{
			try
			{
				this.jacksonConverter.writeWithView(object, new ServletServerHttpResponse(response), view);
			}
			catch (IOException ioex)
			{
				log.error(ioex.getMessage(), ioex);
			}
			catch (HttpMessageNotWritableException httpMNWex)
			{
				log.error(httpMNWex.getMessage(), httpMNWex);
			}
		}
	}

	public void setJacksonConverter(JacksonViewAwareHttpMessageConverter jacksonConverter)
	{
		this.jacksonConverter = jacksonConverter;
	}

}
