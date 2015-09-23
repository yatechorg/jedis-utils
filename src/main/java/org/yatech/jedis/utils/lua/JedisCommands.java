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

    // +------+
    // | HASH |
    // +------+

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
