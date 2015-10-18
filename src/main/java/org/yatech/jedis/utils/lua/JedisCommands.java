package org.yatech.jedis.utils.lua;

import java.util.Map;

/**
 * Created by Yinon Avraham on 01/09/2015.
 */
public interface JedisCommands<VoidReturnType> {

    // +----+
    // | DB |
    // +----+

    // SELECT

    /**
     * Select the DB with having the specified zero-based numeric index.
     * @param index
     * @return
     */
    VoidReturnType select(int index);
    /**
     * Select the DB with having the specified zero-based numeric index.
     * @param index
     * @return
     */
    VoidReturnType select(LuaValue<Integer> index);

    // +------+
    // | KEYS |
    // +------+

    // DEL

    /**
     * Removes the specified key. A key is ignored if it does not exist.
     * @param key
     * @return
     */
    VoidReturnType del(String key);
    /**
     * Removes the specified key. A key is ignored if it does not exist.
     * @param key
     * @return
     */
    VoidReturnType del(LuaValue<String> key);

    // TODO [YA] EXISTS

    // EXPIRE

    /**
     * Set a key's time to live in seconds
     * @param key
     * @param seconds
     * @return
     */
    VoidReturnType expire(String key, int seconds);
    /**
     * Set a key's time to live in seconds
     * @param key
     * @param seconds
     * @return
     */
    VoidReturnType expire(String key, LuaValue<Integer> seconds);
    /**
     * Set a key's time to live in seconds
     * @param key
     * @param seconds
     * @return
     */
    VoidReturnType expire(LuaValue<String> key, int seconds);
    /**
     * Set a key's time to live in seconds
     * @param key
     * @param seconds
     * @return
     */
    VoidReturnType expire(LuaValue<String> key, LuaValue<Integer> seconds);

    // EXPIREAT

    /**
     * Set the expiration for a key as a UNIX timestamp
     * @param key
     * @param timestamp
     * @return
     */
    VoidReturnType expireAt(String key, long timestamp);
    /**
     * Set the expiration for a key as a UNIX timestamp
     * @param key
     * @param timestamp
     * @return
     */
    VoidReturnType expireAt(String key, LuaValue<Long> timestamp);
    /**
     * Set the expiration for a key as a UNIX timestamp
     * @param key
     * @param timestamp
     * @return
     */
    VoidReturnType expireAt(LuaValue<String> key, long timestamp);
    /**
     * Set the expiration for a key as a UNIX timestamp
     * @param key
     * @param timestamp
     * @return
     */
    VoidReturnType expireAt(LuaValue<String> key, LuaValue<Long> timestamp);

    // KEYS

    /**
     * Find all keys matching the pattern
     * @param pattern
     * @return
     */
    LuaLocalArray keys(String pattern);
    /**
     * Find all keys matching the pattern
     * @param pattern
     * @return
     */
    LuaLocalArray keys(LuaValue<String> pattern);

    // MOVE

    /**
     * Move a key to another database
     * @param key
     * @param db
     * @return
     */
    VoidReturnType move(String key, int db);
    /**
     * Move a key to another database
     * @param key
     * @param db
     * @return
     */
    VoidReturnType move(String key, LuaValue<Integer> db);
    /**
     * Move a key to another database
     * @param key
     * @param db
     * @return
     */
    VoidReturnType move(LuaValue<String> key, int db);
    /**
     * Move a key to another database
     * @param key
     * @param db
     * @return
     */
    VoidReturnType move(LuaValue<String> key, LuaValue<Integer> db);

    // DEL

    /**
     * Remove the expiration from a key
     * @param key
     * @return
     */
    VoidReturnType persist(String key);
    /**
     * Remove the expiration from a key
     * @param key
     * @return
     */
    VoidReturnType persist(LuaValue<String> key);

    // PEXPIRE

    /**
     * Set a key's time to live in milliseconds
     * @param key
     * @param milliseconds
     * @return
     */
    VoidReturnType pexpire(String key, long milliseconds);
    /**
     * Set a key's time to live in milliseconds
     * @param key
     * @param milliseconds
     * @return
     */
    VoidReturnType pexpire(String key, LuaValue<Long> milliseconds);
    /**
     * Set a key's time to live in milliseconds
     * @param key
     * @param milliseconds
     * @return
     */
    VoidReturnType pexpire(LuaValue<String> key, long milliseconds);
    /**
     * Set a key's time to live in milliseconds
     * @param key
     * @param milliseconds
     * @return
     */
    VoidReturnType pexpire(LuaValue<String> key, LuaValue<Long> milliseconds);

    // PEXPIREAT

    /**
     * Set the expiration for a key as a UNIX timestamp specified in milliseconds
     * @param key
     * @param timestamp
     * @return
     */
    VoidReturnType pexpireAt(String key, long timestamp);
    /**
     * Set the expiration for a key as a UNIX timestamp specified in milliseconds
     * @param key
     * @param timestamp
     * @return
     */
    VoidReturnType pexpireAt(String key, LuaValue<Long> timestamp);
    /**
     * Set the expiration for a key as a UNIX timestamp specified in milliseconds
     * @param key
     * @param timestamp
     * @return
     */
    VoidReturnType pexpireAt(LuaValue<String> key, long timestamp);
    /**
     * Set the expiration for a key as a UNIX timestamp specified in milliseconds
     * @param key
     * @param timestamp
     * @return
     */
    VoidReturnType pexpireAt(LuaValue<String> key, LuaValue<Long> timestamp);

    // PTTL

    /**
     * Get the time to live for a key in milliseconds
     * @param key
     * @return
     */
    LuaLocalValue pttl(String key);
    /**
     * Get the time to live for a key in milliseconds
     * @param key
     * @return
     */
    LuaLocalValue pttl(LuaValue<String> key);

    // RANDOMKEY

    /**
     * Return a random key from the keyspace
     * @return
     */
    LuaLocalValue randomKey();

    // RENAME

    /**
     * Rename a key
     * @param key
     * @param newKey
     * @return
     */
    VoidReturnType rename(String key, String newKey);
    /**
     * Rename a key
     * @param key
     * @param newKey
     * @return
     */
    VoidReturnType rename(String key, LuaValue<String> newKey);
    /**
     * Rename a key
     * @param key
     * @param newKey
     * @return
     */
    VoidReturnType rename(LuaValue<String> key, String newKey);
    /**
     * Rename a key
     * @param key
     * @param newKey
     * @return
     */
    VoidReturnType rename(LuaValue<String> key, LuaValue<String> newKey);

    // RENAMENX

    /**
     * Rename a key, only if the new key does not exist
     * @param key
     * @param newKey
     * @return
     */
    VoidReturnType renamenx(String key, String newKey);
    /**
     * Rename a key, only if the new key does not exist
     * @param key
     * @param newKey
     * @return
     */
    VoidReturnType renamenx(String key, LuaValue<String> newKey);
    /**
     * Rename a key, only if the new key does not exist
     * @param key
     * @param newKey
     * @return
     */
    VoidReturnType renamenx(LuaValue<String> key, String newKey);
    /**
     * Rename a key, only if the new key does not exist
     * @param key
     * @param newKey
     * @return
     */
    VoidReturnType renamenx(LuaValue<String> key, LuaValue<String> newKey);

    // PTTL

    /**
     * Get the time to live for a key
     * @param key
     * @return
     */
    LuaLocalValue ttl(String key);
    /**
     * Get the time to live for a key
     * @param key
     * @return
     */
    LuaLocalValue ttl(LuaValue<String> key);

    // TYPE

    /**
     * Determine the type stored at key
     * @param key
     * @return
     */
    LuaLocalValue type(String key);
    /**
     * Determine the type stored at key
     * @param key
     * @return
     */
    LuaLocalValue type(LuaValue<String> key);

    // +------+
    // | HASH |
    // +------+

    // HDEL

    /**
     * Delete one or more hash fields
     * @param key
     * @param field
     * @param moreFields
     * @return
     */
    VoidReturnType hdel(String key, String field, String... moreFields);
    /**
     * Delete one or more hash fields
     * @param key
     * @param field
     * @param moreFields
     * @return
     */
    VoidReturnType hdel(String key, LuaValue<String> field, LuaValue<String>... moreFields);
    /**
     * Delete one or more hash fields
     * @param key
     * @param field
     * @param moreFields
     * @return
     */
    VoidReturnType hdel(LuaValue<String> key, String field, String... moreFields);
    /**
     * Delete one or more hash fields
     * @param key
     * @param field
     * @param moreFields
     * @return
     */
    VoidReturnType hdel(LuaValue<String> key, LuaValue<String> field, LuaValue<String>... moreFields);

    // TODO [YA] HEXISTS

    // HGET

    /**
     * Get the value of a hash field
     * @param key
     * @param field
     * @return
     */
    LuaLocalValue hget(String key, String field);
    /**
     * Get the value of a hash field
     * @param key
     * @param field
     * @return
     */
    LuaLocalValue hget(String key, LuaValue<String> field);
    /**
     * Get the value of a hash field
     * @param key
     * @param field
     * @return
     */
    LuaLocalValue hget(LuaValue<String> key, String field);
    /**
     * Get the value of a hash field
     * @param key
     * @param field
     * @return
     */
    LuaLocalValue hget(LuaValue<String> key, LuaValue<String> field);

    // HGETALL

    /**
     * Returns all fields and values of the hash stored at key.
     * @param key
     * @return
     */
    LuaLocalArray hgetAll(String key);
    /**
     * Returns all fields and values of the hash stored at key.
     * @param key
     * @return
     */
    LuaLocalArray hgetAll(LuaValue<String> key);

    // HMSET

    /**
     * Sets the specified fields to their respective values in the hash stored at key.
     * This command overwrites any existing fields in the hash.
     * If key does not exist, a new key holding a hash is created.
     * @param key
     * @param hash
     * @return
     */
    VoidReturnType hmset(String key, Map<String, String> hash);
    /**
     * Sets the specified fields to their respective values in the hash stored at key.
     * This command overwrites any existing fields in the hash.
     * If key does not exist, a new key holding a hash is created.
     * @param key
     * @param hash
     * @return
     */
    VoidReturnType hmset(LuaValue<String> key, Map<String, String> hash);
    /**
     * Sets the specified fields to their respective values in the hash stored at key.
     * This command overwrites any existing fields in the hash.
     * If key does not exist, a new key holding a hash is created.
     * @param key
     * @param hash
     * @return
     */
    VoidReturnType hmset(String key, LuaLocalArray hash);
    /**
     * Sets the specified fields to their respective values in the hash stored at key.
     * This command overwrites any existing fields in the hash.
     * If key does not exist, a new key holding a hash is created.
     * @param key
     * @param hash
     * @return
     */
    VoidReturnType hmset(LuaValue<String> key, LuaLocalArray hash);

    // +---------+
    // | STRINGS |
    // +---------+

    // SET

    /**
     * Set the string value of a key
     * @param key
     * @param value
     * @return
     */
    VoidReturnType set(String key, String value);
    /**
     * Set the string value of a key
     * @param key
     * @param value
     * @return
     */
    VoidReturnType set(String key, LuaValue<String> value);
    /**
     * Set the string value of a key
     * @param key
     * @param value
     * @return
     */
    VoidReturnType set(LuaValue<String> key, String value);
    /**
     * Set the string value of a key
     * @param key
     * @param value
     * @return
     */
    VoidReturnType set(LuaValue<String> key, LuaValue<String> value);

    // GET

    /**
     * Get the value of a key
     * @param key
     * @return
     */
    LuaLocalValue get(String key);
    /**
     * Get the value of a key
     * @param key
     * @return
     */
    LuaLocalValue get(LuaValue<String> key);

    // +------+
    // | ZSET |
    // +------+

    // ZADD
    /**
     * Add the specified member having the specifeid score to the sorted set stored at key. If member
     * is already a member of the sorted set the score is updated, and the element reinserted in the
     * right position to ensure sorting. If key does not exist a new sorted set with the specified
     * member as sole member is crated. If the key exists but does not hold a sorted set value an
     * error is returned.
     * <p>
     * Time complexity O(log(N)) with N being the number of elements in the sorted set
     * <p>
     * @param key
     * @param score
     * @param member
     * @return
     */
    VoidReturnType zadd(String key, double score, String member);
    /**
     * Add the specified member having the specifeid score to the sorted set stored at key. If member
     * is already a member of the sorted set the score is updated, and the element reinserted in the
     * right position to ensure sorting. If key does not exist a new sorted set with the specified
     * member as sole member is crated. If the key exists but does not hold a sorted set value an
     * error is returned.
     * <p>
     * Time complexity O(log(N)) with N being the number of elements in the sorted set
     * <p>
     * @param key
     * @param score
     * @param member
     * @return
     */
    VoidReturnType zadd(String key, double score, LuaValue<String> member);
    /**
     * Add the specified member having the specifeid score to the sorted set stored at key. If member
     * is already a member of the sorted set the score is updated, and the element reinserted in the
     * right position to ensure sorting. If key does not exist a new sorted set with the specified
     * member as sole member is crated. If the key exists but does not hold a sorted set value an
     * error is returned.
     * <p>
     * Time complexity O(log(N)) with N being the number of elements in the sorted set
     * <p>
     * @param key
     * @param score
     * @param member
     * @return
     */
    VoidReturnType zadd(String key, LuaValue<Double> score, String member);
    /**
     * Add the specified member having the specifeid score to the sorted set stored at key. If member
     * is already a member of the sorted set the score is updated, and the element reinserted in the
     * right position to ensure sorting. If key does not exist a new sorted set with the specified
     * member as sole member is crated. If the key exists but does not hold a sorted set value an
     * error is returned.
     * <p>
     * Time complexity O(log(N)) with N being the number of elements in the sorted set
     * <p>
     * @param key
     * @param score
     * @param member
     * @return
     */
    VoidReturnType zadd(String key, LuaValue<Double> score, LuaValue<String> member);
    /**
     * Add the specified member having the specifeid score to the sorted set stored at key. If member
     * is already a member of the sorted set the score is updated, and the element reinserted in the
     * right position to ensure sorting. If key does not exist a new sorted set with the specified
     * member as sole member is crated. If the key exists but does not hold a sorted set value an
     * error is returned.
     * <p>
     * Time complexity O(log(N)) with N being the number of elements in the sorted set
     * <p>
     * @param key
     * @param score
     * @param member
     * @return
     */
    VoidReturnType zadd(LuaValue<String> key, double score, String member);
    /**
     * Add the specified member having the specifeid score to the sorted set stored at key. If member
     * is already a member of the sorted set the score is updated, and the element reinserted in the
     * right position to ensure sorting. If key does not exist a new sorted set with the specified
     * member as sole member is crated. If the key exists but does not hold a sorted set value an
     * error is returned.
     * <p>
     * Time complexity O(log(N)) with N being the number of elements in the sorted set
     * <p>
     * @param key
     * @param score
     * @param member
     * @return
     */
    VoidReturnType zadd(LuaValue<String> key, double score, LuaValue<String> member);
    /**
     * Add the specified member having the specifeid score to the sorted set stored at key. If member
     * is already a member of the sorted set the score is updated, and the element reinserted in the
     * right position to ensure sorting. If key does not exist a new sorted set with the specified
     * member as sole member is crated. If the key exists but does not hold a sorted set value an
     * error is returned.
     * <p>
     * Time complexity O(log(N)) with N being the number of elements in the sorted set
     * <p>
     * @param key
     * @param score
     * @param member
     * @return
     */
    VoidReturnType zadd(LuaValue<String> key, LuaValue<Double> score, String member);
    /**
     * Add the specified member having the specifeid score to the sorted set stored at key. If member
     * is already a member of the sorted set the score is updated, and the element reinserted in the
     * right position to ensure sorting. If key does not exist a new sorted set with the specified
     * member as sole member is crated. If the key exists but does not hold a sorted set value an
     * error is returned.
     * <p>
     * Time complexity O(log(N)) with N being the number of elements in the sorted set
     * <p>
     * @param key
     * @param score
     * @param member
     * @return
     */
    VoidReturnType zadd(LuaValue<String> key, LuaValue<Double> score, LuaValue<String> member);

    /**
     * Adds all the specified members with the specified scores to the sorted set stored at key.
     * It is possible to specify multiple score / member pairs. If a specified member is already
     * a member of the sorted set, the score is updated and the element reinserted at the right
     * position to ensure the correct ordering.
     * <p>
     * If key does not exist, a new sorted set with the specified members as sole members is created,
     * like if the sorted set was empty. If the key exists but does not hold a sorted set, an error is returned.
     * </p>
     * Time complexity O(log(N)) with N being the number of elements in the sorted set
     * @param key
     * @param scoreMembers
     * @return
     */
    VoidReturnType zadd(String key, Map<String, Double> scoreMembers);
    /**
     * Adds all the specified members with the specified scores to the sorted set stored at key.
     * It is possible to specify multiple score / member pairs. If a specified member is already
     * a member of the sorted set, the score is updated and the element reinserted at the right
     * position to ensure the correct ordering.
     * <p>
     * If key does not exist, a new sorted set with the specified members as sole members is created,
     * like if the sorted set was empty. If the key exists but does not hold a sorted set, an error is returned.
     * </p>
     * Time complexity O(log(N)) with N being the number of elements in the sorted set
     * @param key
     * @param scoreMembers
     * @return
     */
    VoidReturnType zadd(LuaValue<String> key, Map<String, Double> scoreMembers);
    /**
     * Adds all the specified members with the specified scores to the sorted set stored at key.
     * It is possible to specify multiple score / member pairs. If a specified member is already
     * a member of the sorted set, the score is updated and the element reinserted at the right
     * position to ensure the correct ordering.
     * <p>
     * If key does not exist, a new sorted set with the specified members as sole members is created,
     * like if the sorted set was empty. If the key exists but does not hold a sorted set, an error is returned.
     * </p>
     * Time complexity O(log(N)) with N being the number of elements in the sorted set
     * @param key
     * @param scoreMembers
     * @return
     */
    VoidReturnType zadd(String key, LuaLocalArray scoreMembers);
    /**
     * Adds all the specified members with the specified scores to the sorted set stored at key.
     * It is possible to specify multiple score / member pairs. If a specified member is already
     * a member of the sorted set, the score is updated and the element reinserted at the right
     * position to ensure the correct ordering.
     * <p>
     * If key does not exist, a new sorted set with the specified members as sole members is created,
     * like if the sorted set was empty. If the key exists but does not hold a sorted set, an error is returned.
     * </p>
     * Time complexity O(log(N)) with N being the number of elements in the sorted set
     * @param key
     * @param scoreMembers
     * @return
     */
    VoidReturnType zadd(LuaValue<String> key, LuaLocalArray scoreMembers);

    // ZSCORE

    /**
     * Returns the score of member in the sorted set at key.
     * @param key
     * @param member
     * @return A local representing the score value.
     *         If member does not exist in the sorted set, or key does not exist, <code>nil</code> is returned.
     */
    LuaLocalValue zscore(String key, String member);
    /**
     * Returns the score of member in the sorted set at key.
     * @param key
     * @param member
     * @return A local representing the score value.
     *         If member does not exist in the sorted set, or key does not exist, <code>nil</code> is returned.
     */
    LuaLocalValue zscore(LuaValue<String> key, String member);
    /**
     * Returns the score of member in the sorted set at key.
     * @param key
     * @param member
     * @return A local representing the score value.
     *         If member does not exist in the sorted set, or key does not exist, <code>nil</code> is returned.
     */
    LuaLocalValue zscore(String key, LuaValue<String> member);
    /**
     * Returns the score of member in the sorted set at key.
     * @param key
     * @param member
     * @return A local representing the score value.
     *         If member does not exist in the sorted set, or key does not exist, <code>nil</code> is returned.
     */
    LuaLocalValue zscore(LuaValue<String> key, LuaValue<String> member);

}
