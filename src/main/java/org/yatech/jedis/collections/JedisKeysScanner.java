package org.yatech.jedis.collections;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.yatech.jedis.collections.Utils.*;

/**
 * Scanner to iterate over keys in a given Redis database.
 * <p>
 *     The iterator is based on Redis's <code>SCAN</code> command. <br>
 *     The <code>SCAN</code> command returns a batch of keys and it requires to get the cursor of the batch to read.
 *     Always start with cursor '<code>0</code>'. There is also an option to filter keys by a pattern
 *     (similar to the <code>KEYS</code> command). This can be done using the {@link ScanParams} argument.
 *     The default batch size is 10, but it can also be configured using the {@link ScanParams} argument.
 *     Please note that setting the batch size is more like a hint rather than a strict definition.
 * </p>
 * <p>
 *     The scan result contains two main details:
 *     <ul>
 *         <li>A result list with the keys in the current batch.
 *             The size of the list can be from zero to the defined batch size (more or less, not strict).</li>
 *         <li>The cursor to the next batch to read.
 *             If this cursor is '<code>0</code>' then we have reached the end of the iteration.</li>
 *     </ul>
 *     Some known characteristics of the <code>SCAN</code> command:
 *     <ul>
 *         <li>A scan result can have an empty list, but still have a next cursor (different from '<code>0</code>').</li>
 *         <li>Although keys are unique in Redis, a key can be returned by <code>SCAN</code> more than once.</li>
 *     </ul>
 * </p>
 * <br>
 * @author Yinon Avraham
 * @see <a href="http://redis.io/commands/scan">SCAN command documentation in redis.io</a>
 */
public class JedisKeysScanner implements Iterator<String> {

    private static final String FIRST_CURSOR = "0";
    private static final int DB_NA = -1;

    private final Jedis jedis;
    private final JedisPool jedisPool;
    private final int db;
    private final ScanParams scanParams;
    private ScanResult scanResult;
    private int resultIndex;

    public JedisKeysScanner(Jedis jedis) {
        this(jedis, new ScanParams());
    }

    public JedisKeysScanner(Jedis jedis, ScanParams scanParams) {
        this(checkNotNull(jedis, "jedis"), null, DB_NA, scanParams);
    }

    public JedisKeysScanner(JedisPool jedisPool, int db) {
        this(jedisPool, db, new ScanParams());
    }

    public JedisKeysScanner(JedisPool jedisPool, int db, ScanParams scanParams) {
        this(null, checkNotNull(jedisPool, "jedisPool"), checkNotNegative(db, "db"), scanParams);
    }

    private JedisKeysScanner(Jedis jedis, JedisPool jedisPool, int db, ScanParams scanParams) {
        checkConstructorParams(jedis, jedisPool, db, scanParams);
        this.jedis = jedis;
        this.jedisPool = jedisPool;
        this.db = db;
        this.scanParams = scanParams;
        init();
    }

    private void checkConstructorParams(Jedis jedis, JedisPool jedisPool, int db, ScanParams scanParams) {
        if (jedis == null && jedisPool == null) {
            throw new IllegalArgumentException("One of jedis or jedis pool is required");
        }
        if (jedisPool != null) {
            if (jedis != null) {
                throw new IllegalArgumentException("Only one of jedis or jedis pool can be given");
            }
            checkTrue(db >= 0, "db", "A non-negative DB index is required when using a jedis pool");
        }
        checkNotNull(scanParams, "scanParams");
    }

    private void init() {
        scanResult = scan(FIRST_CURSOR, scanParams);
        resultIndex = 0;
        ensureNext();
    }

    private ScanResult scan(String cursor, ScanParams scanParams) {
        if (jedis != null) {
            return jedis.scan(cursor, scanParams);
        }
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.select(db);
            return jedis.scan(cursor, scanParams);
        }
    }

    @Override
    public boolean hasNext() {
        // There is a next key if there is a cursor to continue with
        // or the index has not yet reached the end of the current result list
        return scanResult != null &&
                (!FIRST_CURSOR.equals(scanResult.getStringCursor()) || resultIndex < scanResult.getResult().size());
    }

    @Override
    public String next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        // Get the key from the scan result according to the current index; increase the index by 1
        String key = (String) scanResult.getResult().get(resultIndex++);
        // Ensure the index points to a valid next key (or end of iteration)
        ensureNext();
        // Return the current key
        return key;
    }

    /**
     * Make sure the result index points to the next available key in the scan result, if exists.
     */
    private void ensureNext() {
        // Check if the current scan result has more keys (i.e. the index did not reach the end of the result list)
        if (resultIndex < scanResult.getResult().size()) {
            return;
        }
        // Since the current scan result was fully iterated,
        // if there is another cursor scan it and ensure next key (recursively)
        if (!FIRST_CURSOR.equals(scanResult.getStringCursor())) {
            scanResult = scan(scanResult.getStringCursor(), scanParams);
            resultIndex = 0;
            ensureNext();
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Remove is not supported");
    }
}
