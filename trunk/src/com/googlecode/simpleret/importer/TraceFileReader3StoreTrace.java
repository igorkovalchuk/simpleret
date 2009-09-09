package com.googlecode.simpleret.importer;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.NonUniqueResultException;
import org.hibernate.Query;
import org.hibernate.classic.Session;

import com.googlecode.simpleret.database.HibernateUtility;
import com.googlecode.simpleret.database.Trace;
import com.googlecode.simpleret.database.Vocabulary;

/**
 * Reading the program trace and store it into the database.
 */
public class TraceFileReader3StoreTrace extends TraceFileReaderAbstract {

	static Logger logger = 
		Logger.getLogger(TraceFileReader3StoreTrace.class);

	Session session = null;
	
	ImportDataHolder dataHolder = null;
	
	Long threadIdentifier = null;
	
	/**
	 * We change identifiers produced by trace recorder from NNNN - NNNN to 1 - MMMM
	 */
	int currentIdentifier = 0;
	
	Map<Integer, Integer> level2id = new HashMap <Integer, Integer>();
	Map<Integer, String> level2call = new HashMap <Integer, String>();
	
	private long traceCounterMemory = 0;
	private long traceCounterMemory1 = 0;
	private long traceCounterMemory2 = 0;
	
	TraceFileReader3StoreTrace(ImportDataHolder dataHolder, Long threadID) {
		super();
		if (threadID == null)
			throw new RuntimeException("Thread identifier.");
		this.threadIdentifier = threadID;
		this.dataHolder = dataHolder;
	}

	@Override
	protected void beforeRead() {
		session = HibernateUtility.getSessionFactory().getCurrentSession();
		session.beginTransaction();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void processString(String[] values) {
		
		long threadID = Long.parseLong(values[2]);
		if (! threadIdentifier.equals(threadID)) {
			// Ignore other threads.
			return;
		}

		currentIdentifier++;
		int id = currentIdentifier;
		
		int level = Integer.parseInt(values[1]);
		
		String call = values[4].trim();

		boolean ret = false;
		if (call.endsWith(".<<return>>()")) {
			ret = true;
		}

		Trace trace = new Trace();
		trace.setId(id);
		trace.setLevel(level);
		trace.setReturn(ret);
		
		// Use the vocabulary to get signature Class.<<return>>() => Class.method()
		if (ret) {
			if (level2call.containsKey(level)) {
				call = level2call.get(level);
			} else {
				logger.error("Can not find appropriate start record: " + call + ", level = " + level);
				return;
				//System.exit(1);
			}
		}
		
		Integer wordID = dataHolder.vocabularyCache.getID(call);
		if (wordID == null) {
			// READ FROM VOCABULARY TABLE TO VOCABULARY CACHE
			String hsql = "from Vocabulary where word=?";
			Query q = session.createQuery(hsql);
			q.setString(0, call);
			Vocabulary vocabularyWord;
			try {
				vocabularyWord = (Vocabulary) q.uniqueResult();
			} catch (NonUniqueResultException e) {
				throw new RuntimeException("Detected non unique result, probably database select is not case sensitive: " + call);
			}
			if (vocabularyWord == null) {
				logger.error("Can not find this word in the vocabulary table: " + call);
				System.exit(1);
			}
			dataHolder.vocabularyCache.setValue(vocabularyWord);
			wordID = vocabularyWord.getId();
		}
		trace.setVocabularyId(wordID);
		
		if (ret) {
			// 'return' - set start id if possible;
			if (level2id.containsKey(level)) {
				Integer startID = level2id.get(level);
				trace.setStartId( startID );
				dataHolder.startID2endID.put(startID, id);
			}
		} else {
			// not 'return', store the id, for each last level; so, later we can find parent id and method signature.
			level2id.put(level, id);
			level2call.put(level, call);
		}
		
		if (! ret) {
			Integer previousLevel = level - 1;
			if (level2id.containsKey(previousLevel)) {
				trace.setParentId( level2id.get(previousLevel) );
			}
		}
		
		session.save(trace);
		traceCounterMemory++;
		traceCounterMemory1++;
		traceCounterMemory2++;
		
		if (traceCounterMemory == TraceImporter.BATCH) {
			session.flush();
			session.clear();
			traceCounterMemory = 0;
		}
		
		if (traceCounterMemory1 == 1000) {
			logger.info(".");
			traceCounterMemory1 = 0;
		}
		
		if (traceCounterMemory2 == 100000) {
			logger.info(" " + currentIdentifier);
			traceCounterMemory2 = 0;
		}
		
	}

	@Override
	protected void afterRead(boolean errors) {
		if (errors) {
			session.getTransaction().rollback();
		} else {
			session.getTransaction().commit();
		}
	}
	
}
