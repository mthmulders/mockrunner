package com.mockrunner.mock.web;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import com.mockrunner.base.NestedApplicationException;
import com.mockrunner.util.common.CaseAwareMap;

/**
 * Mock implementation of <code>HttpServletResponse</code>.
 */
public class MockHttpServletResponse implements HttpServletResponse
{
    private PrintWriter writer;
    private MockServletOutputStream outputStream;
    private Map headers;
    private Locale locale;
    private String characterEncoding;
    private int bufferSize;
    private boolean wasErrorSent;
    private boolean wasRedirectSent;
    private int errorCode;
    private int statusCode;
    private List cookies;

    public MockHttpServletResponse()
    {
        resetAll();
    }

    /**
     * Resets the state of this object to the default values
     */
    public void resetAll()
    {
        headers = new CaseAwareMap();
        characterEncoding = "ISO-8859-1";
        bufferSize = 8192;
        wasErrorSent = false;
        wasRedirectSent = false;
        errorCode = SC_OK;
        statusCode = SC_OK;
        cookies = new ArrayList();
        outputStream = new MockServletOutputStream(characterEncoding);
        try
        {
            writer = new PrintWriter(new OutputStreamWriter(outputStream, characterEncoding), true);
        } 
        catch(UnsupportedEncodingException exc)
        {
            throw new NestedApplicationException(exc);
        }
    }

    public String encodeURL(String url)
    {
        return url;
    }

    public String encodeRedirectUrl(String url)
    {
        return URLEncoder.encode(url);
    }

    public String encodeRedirectURL(String url)
    {
        return URLEncoder.encode(url);
    }

    public String encodeUrl(String url)
    {
        return url;
    }

    public PrintWriter getWriter() throws IOException
    {
        return writer;
    }

    public ServletOutputStream getOutputStream() throws IOException
    {
        return outputStream;
    }

    public String getOutputStreamContent()
    {
        return outputStream.getContent();
    }

    public void addCookie(Cookie cookie)
    {
        cookies.add(cookie);
    }

    public void addDateHeader(String key, long date)
    {
        addHeader(key, getDateString(date));
    }

    public void addHeader(String key, String value)
    {
        List valueList = (List) headers.get(key);
        if (null == valueList)
        {
            valueList = new ArrayList();
            headers.put(key, valueList);
        }
        valueList.add(value);
    }

    public void addIntHeader(String key, int value)
    {
        String stringValue = Integer.toString(value);
        addHeader(key, stringValue);
    }

    public boolean containsHeader(String key)
    {
        return headers.containsKey(key);
    }

    public void sendError(int code, String message) throws IOException
    {
        errorCode = code;
        wasErrorSent = true;
    }

    public void sendError(int code) throws IOException
    {
        errorCode = code;
        wasErrorSent = true;
    }

    public void sendRedirect(String location) throws IOException
    {
        setHeader("Location", location);
        setStatus(SC_FOUND);
        wasRedirectSent = true;
    }

    public void setDateHeader(String key, long date)
    {
        setHeader(key, getDateString(date));
    } 

    public void setHeader(String key, String value)
    {
        List valueList = new ArrayList();
        headers.put(key, valueList);
        valueList.add(value);
    }

    public void setIntHeader(String key, int value)
    {
        String stringValue = Integer.toString(value);
        setHeader(key, stringValue);
    }

    public void setStatus(int code, String message)
    {
        statusCode = code;
    }

    public void setStatus(int code)
    {
        statusCode = code;
    }

    public void flushBuffer() throws IOException
    {
        writer.flush();
        outputStream.flush();
    }

    public int getBufferSize()
    {
        return bufferSize;
    }

    public String getCharacterEncoding()
    {
        return characterEncoding;
    }

    public void setCharacterEncoding(String encoding)
    {
        characterEncoding = encoding;
        outputStream.setEncoding(encoding);
        try
        {
            writer = new PrintWriter(new OutputStreamWriter(outputStream, characterEncoding), true);
        } 
        catch(UnsupportedEncodingException exc)
        {
            throw new NestedApplicationException(exc);
        }
    }

    public Locale getLocale()
    {
        return locale;
    }

    public void setLocale(Locale locale)
    {
        this.locale = locale;
    }

    public boolean isCommitted()
    {
        return false;
    }
    
    public void reset()
    {
        errorCode = SC_OK;
        statusCode = SC_OK;
        clearHeaders();
        resetBuffer();
    }

    public void resetBuffer()
    {
        outputStream.clearContent();
    }
    
    public void clearHeaders()
    {
        headers.clear();
    }

    public void setBufferSize(int size)
    {
        bufferSize = size;
    }

    public void setContentLength(int length)
    {
        setIntHeader("Content-Length", length);
    }
    
    public String getContentType()
    {
        return getHeader("Content-Type");
    }

    public void setContentType(String type)
    {
        setHeader("Content-Type", type);
    }
    
    public Collection getHeaderNames()
    {
        return headers.keySet();
    }
    
    public Collection getHeaders(String name)
    {
        List headerList = (List)headers.get(name);
        if(null == headerList) return null;
        return headerList;
    }
    
    public List getHeaderList(String key)
    {
        return (List)headers.get(key);
    }
    
    public String getHeader(String key)
    {
        List list = getHeaderList(key);
        if(null == list || 0 == list.size()) return null;
        return (String)list.get(0);
    }
    
    public int getStatusCode()
    {
        return statusCode;
    }
    
    public int getStatus()
    {
        return getStatusCode();
    }
    
    public int getErrorCode()
    {
        return errorCode;
    }
    
    public List getCookies()
    {
        return cookies;
    }
    
    public boolean wasErrorSent()
    {
        return wasErrorSent;
    }
    
    public boolean wasRedirectSent()
    {
        return wasRedirectSent;
    }
    
    private String getDateString(long date)
    {
        Date dateValue = new Date(date);
        SimpleDateFormat dateFormat = new SimpleDateFormat(WebConstants.DATE_FORMAT_HEADER, Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(dateValue);
    }
}
