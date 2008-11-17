package play.mvc.results;

import java.util.HashMap;
import java.util.Map;

import play.exceptions.UnexpectedException;
import play.mvc.Http.Request;
import play.mvc.Http.Response;
import play.templates.TemplateLoader;

public class Error extends Result {
	
	Throwable throwable;
	
	public Error(String reason) {
	    	super (reason);
	}
	
	public Error (Throwable throwable) {
		this.throwable=throwable;
	}
	
	public void apply(Request request, Response response) {
        response.status = 500;
        response.contentType="text/html";
        Map<String, Object> binding = new HashMap<String, Object>();
        binding.put("exception", new UnexpectedException(throwable == null ? this : throwable));
        binding.put("result",this);
        String errorHtml = TemplateLoader.load("errors/500.html").render(binding);
        try {
			response.out.write(errorHtml.getBytes("utf-8"));
		} catch (Exception e) {
			throw new UnexpectedException (e);
		}
	}

}
