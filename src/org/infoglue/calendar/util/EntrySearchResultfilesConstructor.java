package org.infoglue.calendar.util;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoglue.calendar.actions.CalendarAbstractAction;
import org.infoglue.common.util.JFreeReportHelper;
import org.infoglue.common.util.PropertyHelper;
import org.infoglue.calendar.entities.Entry;

import com.opensymphony.webwork.ServletActionContext;

public class EntrySearchResultfilesConstructor 
{

	private static Log log = LogFactory.getLog(EntrySearchResultfilesConstructor.class);

	private static ResultFilesCleaner cleaner;

	private Map parameters = new HashMap();
	private Set<Entry> entries;
	private Map<String, String> searchResultFiles;
	private Map<String, String> searchResultFilePaths;
	private String tempFilePath;
	private String scheme;
	private String serverName;
	private int port;
	private String fileFolderLocation;
	private List resultValues;
	private CalendarAbstractAction action;
	private String entryTypeId;

	private static synchronized void cleanResults()
	{
		if (cleaner == null)
		{
			cleaner = new ResultFilesCleaner();
			new Thread(cleaner).start();
		}
		else if (!cleaner.isRunning)
		{
			log.warn("Clean result files thread seems to have died. Starting a new one...");
			cleaner = new ResultFilesCleaner();
			new Thread(cleaner).start();
		}
	}

	private static String getTempFilePath()
	{
		String digitalAssetPath = PropertyHelper.getProperty("digitalAssetPath");
		return digitalAssetPath;
	}

	public EntrySearchResultfilesConstructor(Map parameters, Set<Entry> entries, String tempFilePath, String scheme, String serverName, int port, List resultValues, CalendarAbstractAction action, String entryTypeId) 
	{
		this.parameters = parameters;
		this.entries = entries;
		//this.tempFilePath = tempFilePath;
		this.scheme = scheme;
		this.serverName = serverName;
		this.port = port;
		this.resultValues = resultValues;
		this.action = action;
		this.entryTypeId = entryTypeId;
		createResults();
	}

	private void createResults()
	{
		searchResultFiles = new LinkedHashMap<String, String>();
		searchResultFilePaths = new LinkedHashMap<String, String>();
		String exportEntryResultsFolder = PropertyHelper.getProperty("exportEntryResultsFolder");
		fileFolderLocation = getTempFilePath() + File.separator + exportEntryResultsFolder + File.separator;
		String httpFolderLocation = scheme + "://" + serverName + ":" + port + "/infoglueCalendar/digitalAssets/" + exportEntryResultsFolder + "/";
		File f = new File(fileFolderLocation);
		if (!f.exists())
		{
			f.mkdir();
		}

		// start the thread cleaning the directory of old files
		cleanResults();

		String exportEntryResultsTypes = PropertyHelper.getProperty("exportEntryResultsTypes");
		if (exportEntryResultsTypes != null)
		{
			StringTokenizer st = new StringTokenizer(exportEntryResultsTypes, ",", false);
			while (st.hasMoreElements())
			{
				String resultType = st.nextToken().trim();
				if (resultType.equals("TXT"))
				{
					try
					{
						EntrySearchResultfilesConstructor_TXT txtConstructor = new EntrySearchResultfilesConstructor_TXT(entries, fileFolderLocation, httpFolderLocation, resultValues, action);
						if (txtConstructor.createFile())
						{
							searchResultFiles.put("Text", txtConstructor.getFileLocation());
							//searchResultFilePaths.put("Text", txtConstructor.get)
						}
					}
					catch (Exception e) 
					{
						log.error("Error creating TXT-file:" + e.getMessage());
					}
				}
				if (resultType.indexOf("CSV") > -1)
				{
					try
					{
						log.debug("fileFolderLocation:" + fileFolderLocation);
						String fileName = fileFolderLocation + File.separator + "entries_" + System.currentTimeMillis() + ".csv";
						String fileURL = httpFolderLocation + "entries_" + System.currentTimeMillis() + ".csv";
						searchResultFiles.put("CSV", fileURL);
						searchResultFilePaths.put("CSV", fileName);
						
						new JFreeReportHelper().getEntriesReport(parameters, entries, fileName, "csv", entryTypeId);
					}
					catch (Exception e) 
					{
						log.error("Error creating CSV-file:" + e.getMessage());
					}
				}
				if (resultType.indexOf("XLS") > -1)
				{
					try
					{
						log.debug("fileFolderLocation:" + fileFolderLocation);
						String fileName = fileFolderLocation + File.separator + "entries_" + System.currentTimeMillis() + ".xls";
						String fileURL = httpFolderLocation + "entries_" + System.currentTimeMillis() + ".xls";
						searchResultFiles.put("Excel", fileURL);
						searchResultFilePaths.put("Excel", fileName);
	
						new JFreeReportHelper().getEntriesReport(parameters, entries, fileName, "xls", entryTypeId);
					}
					catch (Exception e) 
					{
						log.error("Error creating XLS-file:" + e.getMessage());
					}
				}
				if (resultType.indexOf("PDF") > -1)
				{
					try
					{
						log.debug("fileFolderLocation:" + fileFolderLocation);
						String fileName = fileFolderLocation + File.separator + "entries_" + System.currentTimeMillis() + ".pdf";
						String fileURL = httpFolderLocation + "entries_" + System.currentTimeMillis() + ".pdf";
						searchResultFiles.put("PDF", fileURL);
						searchResultFilePaths.put("PDF", fileName);
	
						new JFreeReportHelper().getEntriesReport(parameters, entries, fileName, "pdf", entryTypeId);
					}
					catch (Exception e) 
					{
						log.error("Error creating PFD-file:" + e.getMessage());
					}
				}
				if (resultType.indexOf("HTML") > -1)
				{ 
					try
					{
						log.debug("fileFolderLocation:" + fileFolderLocation);
						String fileName = fileFolderLocation + File.separator + "entries_" + System.currentTimeMillis() + ".html";
						String fileURL = httpFolderLocation + "entries_" + System.currentTimeMillis() + ".html";
						searchResultFiles.put("HTML", fileURL);
						searchResultFilePaths.put("HTML", fileName);
	
						new JFreeReportHelper().getEntriesReport(parameters, entries, fileName, "html", entryTypeId);
					}
					catch (Exception e) 
					{
						log.error("Error creating HTML-file:" + e.getMessage());
					}
				}
			}
		}
	}

	public Map<String, String> getResults() {
		return searchResultFiles;
	}

	public Map<String, String> getFileResults() {
		return searchResultFilePaths;
	}

	static class ResultFilesCleaner implements Runnable {

		long waitForTurn;
		boolean isRunning = false;
		long maxAge;

		public ResultFilesCleaner() {
			waitForTurn = PropertyHelper.getLongProperty(
					"exportEntryResultsTypesCleanerFrequency", 3600000);
			maxAge = PropertyHelper.getLongProperty(
					"exportEntryResultsTypesFileMaxage", 86400000);

			log.debug( "frequency: " + waitForTurn + ", maxAge: " + maxAge );
		}

		public boolean isRunning()
		{
			return isRunning;
		}

		public void run() {
			try
			{
				isRunning = true;
				while( true )
				{
					try
					{
						log.debug("Running cleanup for old result files.");
						long maxAgeTime = System.currentTimeMillis() - maxAge;
						Date d = new Date( maxAgeTime );
						log.debug( "Delete older than: " + d.toString() );
						File folder = new File(getTempFilePath());
						File[] fileNames = folder.listFiles();
						log.debug( "Found: " + fileNames.length + " files." );
						for (int i = 0; i < fileNames.length; i++) {
							File file = fileNames[i];
							log.debug( "File: " + file.getName() + ": " + file.isFile() + ", " + file.canRead() + ", " + file.canWrite() );
							if( !file.isDirectory() ) {
								log.debug( "LM: " + file.lastModified() + ", maxAge: " + maxAgeTime );
								if (file.lastModified() < maxAgeTime ) {
									log.info("Deleting file: " + file.getName());
									file.delete();
								}
							}
						}
					}
					catch (Exception e)
					{
						log.warn("Failed deleting old result files.", e);
					}
					try
					{
						Thread.sleep(waitForTurn);
					}
					catch (Exception e)
					{
						log.warn("Sleep failed.", e);
					}
				}
			}
			finally
			{
				isRunning = false;
			}
		}
	}

}
