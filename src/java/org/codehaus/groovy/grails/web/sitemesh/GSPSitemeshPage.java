package org.codehaus.groovy.grails.web.sitemesh;

import com.opensymphony.module.sitemesh.parser.AbstractHTMLPage;
import com.opensymphony.sitemesh.Content;

import org.codehaus.groovy.grails.web.pages.SitemeshPreprocessor;
import org.codehaus.groovy.grails.web.util.StreamCharBuffer;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * Grails/GSP specific implementation of Sitemesh's AbstractHTMLPage
 * 
 * g:capture* tags in RenderTagLib are used to capture head, meta, title, component & body contents.
 * No html parsing is required for templating since capture tags are added at GSP compilation time.
 * 
 * 
 * @see SitemeshPreprocessor
 * @author Lari Hotari, Sagire Software Oy
 *
 */
public class GSPSitemeshPage extends AbstractHTMLPage implements Content{
	StreamCharBuffer headBuffer;
	StreamCharBuffer bodyBuffer;
	StreamCharBuffer pageBuffer;
	boolean used=false;
	Map<String, StreamCharBuffer> componentBuffers;
	
	public GSPSitemeshPage() {

	}

	@Override
	public void addProperty(String name, String value) {
		super.addProperty(name, value);
		this.used=true;		
	}

	@Override
	public void writeHead(Writer out) throws IOException {
		if(headBuffer != null) {
			headBuffer.writeTo(out);
		}
	}

	@Override
	public void writeBody(Writer out) throws IOException {
		if(bodyBuffer != null) {
			bodyBuffer.writeTo(out);
		} else if (pageBuffer != null) {
			// no body was captured, so write the whole page content
			pageBuffer.writeTo(out);
		}
	}

	@Override
	public void writePage(Writer out) throws IOException {
		if(pageBuffer != null) {
			pageBuffer.writeTo(out);
		}
	}

	public String getHead() {
		if(headBuffer != null) {
			return headBuffer.toString();
		}
		return null;
	}

	@Override
	public String getBody() {
		if(bodyBuffer != null) {
			return bodyBuffer.toString();
		}
		return null;
	}

	@Override
	public String getPage() {
		if(pageBuffer != null) {
			return pageBuffer.toString();
		}
		return null;
	}

	public int originalLength() {
		return pageBuffer.size();
	}

	public void writeOriginal(Writer writer) throws IOException {
		writePage(writer);
	}

	public void setHeadBuffer(StreamCharBuffer headBuffer) {
		this.headBuffer = headBuffer;
		this.used=true;
	}

	public void setBodyBuffer(StreamCharBuffer bodyBuffer) {
		this.bodyBuffer = bodyBuffer;
		this.used=true;
	}

	public void setPageBuffer(StreamCharBuffer pageBuffer) {
		this.pageBuffer = pageBuffer;
	}

	public boolean isUsed() {
		return used;
	}

	public void setUsed(boolean used) {
		this.used = used;
	}

	public void setComponentBuffer(String tagName, StreamCharBuffer buffer) {
		this.used=true;
		if(componentBuffers==null) {
			componentBuffers=new HashMap<String, StreamCharBuffer>();
		}
		String propertyName = "page." + tagName;
		componentBuffers.put(propertyName, buffer);
		// just mark that the property is set
		super.addProperty(propertyName, "");
	}
	
	public Object getComponentBuffer(String name) {
		if(componentBuffers==null) {
			return null;
		}
		return componentBuffers.get(name);
	}
}
