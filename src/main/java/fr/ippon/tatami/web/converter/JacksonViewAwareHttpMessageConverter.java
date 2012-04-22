package fr.ippon.tatami.web.converter;

import java.io.IOException;

import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;

import fr.ippon.tatami.web.json.view.JacksonView;

/**
 * @author DuyHai DOAN
 */
public class JacksonViewAwareHttpMessageConverter extends MappingJacksonHttpMessageConverter
{

	private ObjectMapper objectMapper = null;

	private boolean prefixJson = false;

	public JacksonViewAwareHttpMessageConverter() {
		this.objectMapper = super.getObjectMapper();
		this.objectMapper.configure(SerializationConfig.Feature.DEFAULT_VIEW_INCLUSION, false);
	}

	public void writeWithView(Object object, HttpOutputMessage outputMessage, Class<? extends JacksonView> view) throws IOException,
			HttpMessageNotWritableException
	{

		JsonEncoding encoding = getJsonEncoding(outputMessage.getHeaders().getContentType());
		JsonGenerator jsonGenerator = this.objectMapper.getJsonFactory().createJsonGenerator(outputMessage.getBody(), encoding);
		try
		{
			if (this.prefixJson)
			{
				jsonGenerator.writeRaw("{} && ");
			}

			if (view != null)
			{
				this.objectMapper.writerWithView(view).writeValue(jsonGenerator, object);
			}
			else
			{
				this.objectMapper.writeValue(jsonGenerator, object);
			}
		}
		catch (JsonProcessingException ex)
		{
			throw new HttpMessageNotWritableException("Could not write JSON: " + ex.getMessage(), ex);
		}
	}
}
