package org.yatech.jedis.utils.lua.ast;

/**
 * Configuration for the lua script. This class is immutable. Use the builder to create instances.
 *
 * @see #newConfig()
 * Created by yinona on 27/09/15.
 */
public final class LuaScriptConfig {

    /**
     * Default lua script configuration:
     * <ul>
     *     <li>Use script caching: <code>true</code></li>
     * </ul>
     */
    public static final LuaScriptConfig DEFAULT = newConfig().build();

    private final boolean useScriptCaching;

    private LuaScriptConfig(boolean useScriptCaching) {
        this.useScriptCaching = useScriptCaching;
    }

    /**
     * Use script caching.
     * If <code>true</code>, the script will be stored in the redis server and will not be sent each time the it is executed.
     * @return
     */
    public boolean isUseScriptCaching() {
        return useScriptCaching;
    }

    // +---------+
    // | Builder |
    // +---------+

    /**
     * Start building a new configuration.
     * @return the configuration builder
     * @see org.yatech.jedis.utils.lua.ast.LuaScriptConfig.LuaScriptConfigBuilder#build()
     */
    public static LuaScriptConfigBuilder newConfig() {
        return new LuaScriptConfigBuilder();
    }

    /**
     * Builder for a lua script configuration
     */
    public static class LuaScriptConfigBuilder {
        private boolean useScriptCaching;

        private LuaScriptConfigBuilder() {
            this.useScriptCaching = true;
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
         * Build the lua script configuration
         * @return the new configuration instance with the settings provided to this builder.
         */
        public LuaScriptConfig build() {
            return new LuaScriptConfig(useScriptCaching);
        }
    }
}
