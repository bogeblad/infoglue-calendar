/* ===============================================================================
 *
 * Part of the InfoGlue Content Management Platform (www.infoglue.org)
 *
 * ===============================================================================
 *
 *  Copyright (C)
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License version 2, as published by the
 * Free Software Foundation. See the file LICENSE.html for more information.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY, including the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc. / 59 Temple
 * Place, Suite 330 / Boston, MA 02111-1307 / USA.
 *
 * ===============================================================================
 */

package org.infoglue.calendar.taglib;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;

import org.apache.axis.encoding.Base64;
import org.apache.log4j.Logger;
import org.infoglue.calendar.util.graphics.AdvancedImageRenderer;
import org.infoglue.common.util.PropertyHelper;

/**
 * A Tag used for rendering distorted images with random text.  
 * @author Tommy Berglund <a href="mailto:tommy.berglund@hotmail.com">tommy.berglund@modul1.se</a>
 */

public class GapchaTag extends AbstractTag
{
    private final static Logger logger = Logger.getLogger(GapchaTag.class.getName());

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String allowedCharacters = "abcdefghijklmonpqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	private Map renderAttributes = null;
	private Object result;

	private String textVariableName = "CAPTHCA_TEXT";
	private int numberOfCharacters = 5;
	private static int requestsNO = 0;
	/** If set this value will be interpreted as a variable name to store the Captcha instance ticket.
	 * If this value is set the tag will not store the Captcha ticket in the sessions. That means that
	 * the tag caller has to handle the ticket and pass it with the form submit. */
	private String ticket;

	private static final String DEFAULT_PASSWORD = "TOPSECRETPASSWORDTHATNOONEKNOWS";
	private String password ;
	private static final byte[] SALT = {
		(byte) 0xde, (byte) 0x73, (byte) 0x10, (byte) 0xa2,
		(byte) 0xde, (byte) 0x73, (byte) 0x10, (byte) 0xa2,
	};
	
	public GapchaTag() 
	{
		super();
	}

	public int doEndTag() throws JspException 
	{
		if(requestsNO > 50)
		{
			cleanOldFiles();
			requestsNO = 0;
		}
		
		// create the random string
		char[] randomCharacters = createRandomCharacters();

		if (ticket == null)
		{
			logger.info("Generating Gaptcha with session stored verification");
			String sessionVariableName = textVariableName + "_" + System.currentTimeMillis();
			pageContext.getSession().setAttribute( sessionVariableName, new String(randomCharacters) );
			pageContext.getSession().setAttribute( "useCaptchaForEntry", "true" );
			pageContext.setAttribute(textVariableName, sessionVariableName);
		}
		else
		{
			try
			{
				logger.info("Generating Gaptcha with encoded ticket");
				pageContext.setAttribute(ticket, encodeTicket(new String(randomCharacters)));
			}
			catch (Exception ex)
			{
				logger.error("Error generating encrypted ticket for Gapcha. Message: " + ex.getMessage());
				logger.warn("Error generating encrypted ticket for Gapcha.", ex);
				throw new JspException("Error generating captcha");
			}
		}
		// without spacing it is really hard to read the text
		String randomText = spaceCharacters(randomCharacters);
		try 
		{
			result = this.getRenderedTextUrl( randomText, renderAttributes, true );
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}

		this.produceResult( result );
		requestsNO++;
		
		return EVAL_PAGE;
	}

	private String encodeTicket(String characters) throws GeneralSecurityException, UnsupportedEncodingException
	{
		if (password == null)
		{
			password = DEFAULT_PASSWORD;
		}
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
		SecretKey key = keyFactory.generateSecret(new PBEKeySpec(password.toCharArray()));
		Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
		pbeCipher.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
		return Base64.encode(pbeCipher.doFinal(characters.getBytes("UTF-8")));
	}

	public static String decodeTicket(String ticket, String password) throws GeneralSecurityException, IOException
	{
		if (password == null)
		{
			password = DEFAULT_PASSWORD;
		}
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
		SecretKey key = keyFactory.generateSecret(new PBEKeySpec(password.toCharArray()));
		Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
		pbeCipher.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
		return new String(pbeCipher.doFinal(Base64.decode(ticket)), "UTF-8");
	}

    private String getRenderedTextUrl( String text, Map renderAttributes, boolean distort )
    {
        String assetUrl = "";
        if ( text == null || text.length() == 0 )
        {
            logger.warn("Could not render text with a null or 0 lenght value");
            return assetUrl;
        }
        try
        {
            String aText = text.replaceAll( "[^\\w]", "" );
            aText = aText.substring( 0, ( aText.length() < 12 ? aText.length() : 11 ) ).toLowerCase();
            StringBuffer uniqueId = new StringBuffer( aText );
            uniqueId.append( "_" + Math.abs( text.hashCode() ) );
            uniqueId.append( "_" + ( renderAttributes != null ? Math.abs( renderAttributes.hashCode() ) : 4711 ) );

            AdvancedImageRenderer imageRenderer = new AdvancedImageRenderer();
            // render the image
            imageRenderer.renderImage( text, renderAttributes );
            if( distort ) 
            {
            	uniqueId = new StringBuffer( "igcaptcha" );
                uniqueId.append( "_" + Math.abs( text.hashCode() ) );
                uniqueId.append( "_" + ( renderAttributes != null ? Math.abs( renderAttributes.hashCode() ) : 4711 ) );
            	imageRenderer.distortImage();
            }

            String fileName = uniqueId + "." + imageRenderer.getImageFormatName();	// default is png

            // write the result
            assetUrl = writeRenderedImage( imageRenderer, fileName );
        }
        catch ( Exception e )
        {
            logger.error( "An error occurred trying to getRenderedTextUrl(), text = " + text + " :" + e.getMessage(), e );
        }

        return assetUrl;
    }

    private String writeRenderedImage( AdvancedImageRenderer imageRenderer, String fileName ) throws Exception
    {
        // write the result
        int i = 0;
        String filePath = PropertyHelper.getProperty("digitalAssetPath");

	    File imageFile = new File( filePath, fileName );
	    if ( !imageFile.exists() )
	    {
	        imageRenderer.writeImage( imageFile );
	    }

        String urlBase = PropertyHelper.getProperty("urlBase");
		
		String url = urlBase + "digitalAssets/" + fileName;
		
		return url;
    }

    private void setAttribute( String key, Object value )
    {
        if ( renderAttributes == null )
        {
            renderAttributes = new HashMap();
        }
        if ( key != null && value != null )
        {
            renderAttributes.put( key, value.toString() );
        }
    }

	protected void produceResult(Object value) throws JspTagException
	{
	    if(id == null)
	    {
			write((value == null) ? "" : value.toString());
	    }
		else
		{
			setResultAttribute(value);
		}
	}

	protected void write(final String text) throws JspTagException
	{
		try 
		{
			pageContext.getOut().write(text);
		}
		catch(IOException e)
		{
			e.printStackTrace();
			throw new JspTagException("IO error: " + e.getMessage());
		}
	}

	protected void setResultAttribute(Object value)
	{
		if(value == null)
		{
			pageContext.removeAttribute(id);
		}
		else
		{
			pageContext.setAttribute(id, value);
		}
	}

	private static boolean disableCleanOldFiles()
	{
		String setting = PropertyHelper.getProperty("disableGapchaFileRemover");
		logger.info("disableGapchaFileRemover: " + setting);
		if (PropertyHelper.isUndefinedProperty(setting, null))
		{
			return false;
		}
		else
		{
			return "true".equals(setting);
		}
	}

	public static void cleanOldFiles()
	{
		if (disableCleanOldFiles())
		{
			logger.debug("Clean old files is disabled.");
			return;
		}
		int i = 0;
		String filePath = PropertyHelper.getProperty("digitalAssetPath");
		logger.info("Cleaning files...");
		File folder = new File(filePath);
		File[] files = folder.listFiles();
		logger.info("files:" + files.length);
		for(int j=0; j<files.length; j++)
		{
			File file = files[j];
			if(file.getName().startsWith("igcaptcha"))
			{
				logger.info("file.getName():" + file.getName() + " - " + (System.currentTimeMillis() - file.lastModified()));
				if(System.currentTimeMillis() - file.lastModified() > 60000)
				{
					logger.info("Deleting:" + file.getName());
					file.delete();
				}
			}
		}
	}

	/**
	 * Sets the number of characters in the random string. Value must be greater
	 * than 0, otherwise default value (5) is used.
	 * @param numberOfCharacters the number of characters
	 */
	public void setNumberOfCharacters( int numberOfCharacters ) 
	{
		if( numberOfCharacters > 0 ) 
		{
			this.numberOfCharacters = numberOfCharacters;
		}
	}

	/**
	 * Sets the variable name by which to store the captcha text in.
	 * @param numberOfCharacters the number of characters
	 */
	public void setTextVariableName( String textVariableName ) 
	{
		this.textVariableName = textVariableName;
	}

	/**
	 * Creates a char[] of random characters and numbers a-z,A-Z,0-9. 
	 * The  number of characters can be set by an attribute. Default is 5.
	 * @return a char[] of random characters and numbers
	 */
	private char[] createRandomCharacters()
	{
		Random r = new Random();
		StringBuffer sb = new StringBuffer();
		char[] buf = new char[numberOfCharacters];
		for (int i = 0; i < buf.length; i++) 
		{
			buf[i] = allowedCharacters.charAt(r.nextInt(allowedCharacters.length()));
		}
		return buf;
	}

	/**
	 * Spaces characters for easier reading
	 * @param characters the characters to space
	 * @return a <code>String</code> of spaced characters
	 */
	private String spaceCharacters( char[] characters ) 
	{
		StringBuffer sb = new StringBuffer(characters.length*2);
		for( int i = 0; i <characters.length; i++ ) 
		{
			sb.append( characters[i] );
			sb.append( " " );
		}
		return sb.toString();	
	}

	public void setTwirlAngle(String twirlAspect) throws JspException
	{
		this.setAttribute("twirlAspect", ((Float)evaluate("gapcha", "twirlAspect", twirlAspect, Float.class)).floatValue());
	}

	public void setMarbleXScale(String marbleXScale) throws JspException
	{
		this.setAttribute("marbleXScale", ((Float)evaluate("gapcha", "marbleXScale", marbleXScale, Float.class)).floatValue());
	}

	public void setMarbleYScale(String marbleYScale) throws JspException
	{
		this.setAttribute("marbleYScale", ((Float)evaluate("gapcha", "marbleYScale", marbleYScale, Float.class)).floatValue());
	}

	public void setMarbleTurbulence(String marbleTurbulence) throws JspException
	{
		this.setAttribute("marbleTurbulence", ((Float)evaluate("gapcha", "marbleTurbulence", marbleTurbulence, Float.class)).floatValue());
	}

	public void setMarbleAmount(String marbleAmount) throws JspException
	{
		this.setAttribute("marbleAmount", ((Float)evaluate("gapcha", "marbleAmount", marbleAmount, Float.class)).floatValue());
	}

	public void setAllowedCharacters(String allowedCharacters) throws JspException
	{
		this.allowedCharacters = evaluateString("gapcha", "allowedCharacters", allowedCharacters);
	}

	public void setFontName( String fontName ) throws JspException
	{
		this.setAttribute( "fontName", evaluateString( "textRender", "fontName", fontName ) );
	}

	public void setFontSize( String fontSize ) throws JspException
	{
		this.setAttribute( "fontSize", evaluateInteger( "textRender", "fontSize", fontSize ) );
	}

	public void setFontStyle( String fontStyle ) throws JspException
	{
		this.setAttribute( "fontStyle", evaluateInteger( "textRender", "fontStyle", fontStyle ) );
	}

	public void setFgColor( String fgColor ) throws JspException
	{
		this.setAttribute( "fgColor", evaluateString( "textRender", "fgColor", fgColor ) );
	}

	public void setBgColor( String bgColor ) throws JspException
	{
		this.setAttribute( "bgColor", evaluateString( "textRender", "bgColor", bgColor ) );
	}

	public void setImageType( String imageType ) throws JspException
	{
		this.setAttribute( "imageType", evaluateInteger( "textRender", "imageType", imageType ) );
	}

	public void setMaxRows( String maxRows ) throws JspException
	{
		this.setAttribute( "maxRows", evaluateInteger( "textRender", "maxRows", maxRows ) );
	}

	public void setPad( String pad ) throws JspException
	{
		this.setAttribute( "pad", evaluateInteger( "textRender", "pad", pad ) );
	}

	public void setPadBottom( String padBottom ) throws JspException
	{
		this.setAttribute( "padBottom", evaluateInteger( "textRender", "padBottom", padBottom ) );
	}

	public void setPadLeft( String padLeft ) throws JspException
	{
		this.setAttribute( "padLeft", evaluateInteger( "textRender", "padLeft", padLeft ) );
	}

	public void setPadRight( String padRight ) throws JspException
	{
		this.setAttribute( "padRight", evaluateInteger( "textRender", "padRight", padRight ) );
	}

	public void setPadTop( String padTop ) throws JspException
	{
		this.setAttribute( "padTop", evaluateInteger( "textRender", "padTop", padTop ) );
	}

	public void setRenderWidth( String renderWidth ) throws JspException
	{
		this.setAttribute( "renderWidth", evaluateInteger( "textRender", "renderWidth", renderWidth ) );
	}

	public void setTileBackgroundImage( String tileBackgroundImage ) throws JspException
	{
		this.setAttribute( "tileBackgroundImage", evaluateInteger( "textRender", "tileBackgroundImage", tileBackgroundImage ) );
	}

	public void setTrimEdges( String trimEdges ) throws JspException
	{
		this.setAttribute( "trimEdges", evaluateInteger( "textRender", "trimEdges", trimEdges ) );
	}

	public void setTicket(String ticket)
	{
		this.ticket = ticket;
	}

	public void setPassword(String password) throws JspException
	{
		this.password = evaluateString("gapcha", "password", password);
	}
}
