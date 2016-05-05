package org.yatech.jedis.utils.lua;

/**
 * Configuration for the lua script. This class is immutable. Use the builder to create instances.
 * <p>
 * Created on 27/09/15.
 * @see #newConfig()
 * @author Yinon Avraham
 */
public final class LuaScriptConfig {

    /**
     * Default lua script configuration:
     * <ul>
     *     <li>Use script caching: <code>true</code></li>
     *     <li>Use thread safe implementation: <code>false</code></li>
     * </ul>
     */
    public static final LuaScriptConfig DEFAULT = newConfig().build();

    private final boolean useScriptCaching;
    private final boolean threadSafe;

    private LuaScriptConfig(boolean useScriptCaching, boolean threadSafe) {
        this.useScriptCaching = useScriptCaching;
        this.threadSafe = threadSafe;
    }

    /**
     * Use script caching.
     * If <code>true</code>, the script will be stored in the redis server and will not be sent each time the it is executed.
     * @return
     */
    public boolean isUseScriptCaching() {
        return useScriptCaching;
    }

    /**
     * Use thread safe implementation.
     * If <code>true</code>, a thread safe implementation will be created.
     * @return
     */
    public boolean isThreadSafe() {
        return threadSafe;
    }

    // +---------+
    // | Builder |
    // +---------+

    /**
     * Start building a new configuration.
     * @return the configuration builder
     * @see LuaScriptConfig.LuaScriptConfigBuilder#build()
     */
    public static LuaScriptConfigBuilder newConfig() {
        return new LuaScriptConfigBuilder();
    }

    /**
     * Builder for a lua script configuration
     */
    public static class LuaScriptConfigBuilder {
        private boolean useScriptCaching;
        private boolean threadSafe;

        private LuaScriptConfigBuilder() {
            this.useScriptCaching = true;
            this.threadSafe = false;
        }

        /**
         * Use script caching.
         * If <code>true</code>, the script will be stored in the redis server and will not be sent each time the it is executed.
         * By default this setting is <code>true</code>.
         * @return this builder, for chaining builder method calls.
         */
        public LuaScriptConfigBuilder useScriptCaching(boolean use) {
            this.useScriptCaching = use;
            return this;
        }

        /**
         * Use thread safe implementation.
         * If <code>true</code>, a thread safe implementation will be used.
         * By default this setting is <code>false</code>.
         * @return this builder, for chaining builder method calls.
         */
        public LuaScriptConfigBuilder threadSafe(boolean safe) {
            this.threadSafe = safe;
            return this;
        }

        /**
         * Build the lua script configuration
         * @return the new configuration instance with the settings provided to this builder.
         */
        public LuaScriptConfig build() {
            return new LuaScriptConfig(useScriptCaching, threadSafe);
        }
    }
}
